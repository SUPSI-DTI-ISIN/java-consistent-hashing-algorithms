package ch.supsi.dti.isin.consistenthash.maglev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.math.PrimeSieve;

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link MaglevEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MaglevEngineTests
{


    /** Random values generator */
    private static final Random random = new Random();

    /** Prime numbers generator */
    private static final PrimeSieve primes = PrimeSieve.get();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void adding_backends_shoud_change_the_size_accordingly()
    {

        final int size = random.nextInt( 100 ) + 5;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final List<String> backends = IntStream.range( 0, size ).mapToObj( i -> "node_" + i ).collect( Collectors.toList() );

        assertEquals( 0, engine.size() );
        assertEquals( lookupSize, engine.lookupSize() );

        engine.addBackends( backends );

        assertEquals( backends.size(), engine.size() );
        assertTrue( engine.lookupSize() >= backends.size() << 7 );

    }

    @Test
    public void the_lookup_table_should_be_complete()
    {

        final int size = random.nextInt( 100 ) + 5;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final Set<String> backends = IntStream.range( 0, size ).mapToObj( i -> "node_" + i ).collect( Collectors.toSet() );
        engine.addBackends( backends );

        final Map<String,List<String>> entries = engine.streamLookupEntries()
            .peek( Assertions::assertNotNull )
            .collect( Collectors.groupingBy(Function.identity()) );

        entries.forEach( (backend, list) ->
        {

            assertTrue( backends.contains(backend) );
            assertNotNull( list );
            assertFalse( list.isEmpty() );

        });

        final double avg = entries.values().stream().mapToInt( List::size ).average().getAsDouble();
        assert( avg >= 128 );

    }


    @Test
    public void removing_backends_shoud_change_the_size_accordingly()
    {

        final int size = random.nextInt( 100 ) + 5;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final List<String> backends = IntStream.range( 0, size ).mapToObj( i -> "node_" + i ).collect( Collectors.toList() );

        assertEquals( 0, engine.size() );
        assertEquals( lookupSize, engine.lookupSize() );

        engine.addBackends( backends );
        assertEquals( backends.size(), engine.size() );
        assertTrue( engine.lookupSize() >= backends.size() << 7 );

        final Set<String> toRemove = IntStream.range( 0, size >> 1 ).mapToObj( i -> "node_" + i ).collect( Collectors.toSet() );
        engine.removeBackends( toRemove );

        final int newSize = backends.size() - toRemove.size();
        assertEquals( newSize, engine.size() );
        assertEquals( lookupSize, engine.lookupSize() );

        final Map<String,List<String>> entries = engine.streamLookupEntries()
            .peek( Assertions::assertNotNull )
            .collect( Collectors.groupingBy(Function.identity()) );

        entries.forEach( (backend, list) ->
        {

            assertTrue( ! toRemove.contains(backend) );
            assertNotNull( list );
            assertFalse( list.isEmpty() );

        });

        final double avg = entries.values().stream().mapToInt( List::size ).average().getAsDouble();
        assert( avg >= 128 );

    }


    @Test
    public void adding_backends_should_not_change_the_previous_positions_too_much()
    {

        final int size = random.nextInt( 100 ) + 5;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final Set<String> backends = IntStream.range( 0, size ).mapToObj( i -> "node_" + i ).collect( Collectors.toSet() );
        engine.addBackends( backends );

        final List<String> prev = engine.streamLookupEntries().collect( Collectors.toList() );
        final List<String> added = IntStream.range( size, size << 1 ).mapToObj( i -> "node_" + i ).collect( Collectors.toList() );
        engine.addBackends( added );
        
        final List<String> post = engine.streamLookupEntries().collect( Collectors.toList() );
        
        double distruption = 0;
        for( int i = 0; i < lookupSize; ++ i )
        {
            if( backends.contains(post.get(i)) && ! prev.get(i).equals(post.get(i)))
                ++distruption;
        }

        assertTrue( distruption / post.size() < 0.001 );

    }


    @Test
    public void removing_backends_should_not_change_the_previous_positions_too_much()
    {

        final int size = random.nextInt( 100 ) + 5;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final Set<String> backends = IntStream.range( 0, size << 1 ).mapToObj( i -> "node_" + i ).collect( Collectors.toSet() );
        engine.addBackends( backends );

        final List<String> prev = engine.streamLookupEntries().collect( Collectors.toList() );
        final List<String> removed = IntStream.range( 0, size ).mapToObj( i -> "node_" + i ).collect( Collectors.toList() );
        engine.removeBackends( removed );
        
        final List<String> post = engine.streamLookupEntries().collect( Collectors.toList() );
        
        double distruption = 0;
        for( int i = 0; i < lookupSize; ++ i )
        {
            if( ! removed.contains(prev.get(i)) && ! prev.get(i).equals(post.get(i)))
                ++distruption;
        }

        assertTrue( distruption / post.size() < 0.001 );

    }


    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final int size = 1;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.addBackends( Collections.singleton("node") );

        for( int i = 0; i < 100; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String backend = engine.getBackend( key );
            assertEquals( "node", backend );

        }
        
    }
    

    @Test
    public void if_the_cluster_has_multiple_nodes_each_node_should_get_some_key()
    {

        final int size = 10;
        final int lookupSize = (int) primes.getSmallestPrimeGreaterEqual( size << 7 );
        final MaglevEngine engine = new MaglevEngine( lookupSize, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final Map<String,AtomicInteger> backends = IntStream.range( 0, size )
            .mapToObj( i -> "node_" + i )
            .collect( Collectors.toMap(Function.identity(), s -> new AtomicInteger()) );

        engine.addBackends( backends.keySet() );
        for( int i = 0; i < 1000; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String backend = engine.getBackend( key );

            final AtomicInteger count = backends.get( backend );
            assertNotNull( count );

            count.incrementAndGet();

        }

        backends.values().stream().forEach( count ->
        {
            assertTrue( count.get() > 0 );
        });
        
    }

}
