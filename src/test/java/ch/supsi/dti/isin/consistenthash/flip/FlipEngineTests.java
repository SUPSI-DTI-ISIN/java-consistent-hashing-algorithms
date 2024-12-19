package ch.supsi.dti.isin.consistenthash.flip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.Contract;
import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link FlipEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FlipEngineTests implements Contract<FlipEngine>
{

    public static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Creates a new {@link FlipEngine} with the given size.
     * 
     * @param size size of the cluster
     * @return new instance of {@link FlipEngine}
     */
    public FlipEngine sampleValue( int size )
    {

        return new FlipEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlipEngine sampleValue()
    {

        return sampleValue( 10 );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void a_new_created_engine_should_have_the_expected_size()
    {

        final int size = random.nextInt( 100 ) + 1;
        final FlipEngine engine = sampleValue( size );
        assertEquals(size, engine.size());

    }

    @Test
    public void adding_a_new_bucket_should_increase_the_size()
    {

        int size = random.nextInt( 100 ) + 1;
        final int lowerTreeFilter = Integer.highestOneBit( size ) - 1;
        final int upperTreeFilter = (lowerTreeFilter << 1) | 1;
        if( size == upperTreeFilter )
            size += 1;
        
        final FlipEngine engine = sampleValue( size );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );

    }

    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final FlipEngine engine = sampleValue( size );
        final int bucket = engine.addBucket();

        assertEquals( size, bucket );

    }

    @Test
    public void removing_a_bucket_should_decrease_the_size()
    {

        int size = random.nextInt( 100 ) + 4;
        final int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        if( size == expectedSmallCapacity )
            size += 1;

        final FlipEngine engine = sampleValue( size + 1 );
        assertEquals( size + 1, engine.size() );

        engine.removeBucket( 0 );
        assertEquals( size, engine.size() );

    }

    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final FlipEngine engine = new FlipEngine( 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        for( int i = 0; i < 100; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final int bucket = engine.getBucket( key );
            assertEquals( 0, bucket );

        }
        
    }
    
    @Test
    public void if_the_cluster_has_multiple_nodes_each_node_should_get_some_key()
    {

        final FlipEngine engine = new FlipEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );       
        final Map<Integer,AtomicInteger> map = IntStream
            .range( 0, 10 )
            .boxed()
            .collect(
                Collectors.toMap( Function.identity(), i -> new AtomicInteger() )
            );

        for( int i = 0; i < 1000; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final int bucket = engine.getBucket( key );

            final AtomicInteger count = map.get( bucket );
            assertNotNull( count );

            count.incrementAndGet();

        }

        map.values().stream().forEach( count ->
        {
            assertTrue( count.get() > 0 );
        });
        
    }

    @Test
    public void test_balance()
    {

        final int size = 12;
        final int h = Integer.highestOneBit( size );
        final int m = size > h ? h << 1 : h;
        final List<List<String>> lists = new ArrayList<>( m );
        for( int i = 0; i < m; ++i )
            lists.add( new ArrayList<>() );

        final BucketBasedEngine engine = new FlipEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int keys = size * 1000;
        for( int i = 0; i < keys; ++i )
        {
            final String key = String.valueOf( i );
            final int bucket = engine.getBucket( key );
            lists.get( bucket ).add( key );
        }

        final int limit = 100;
        for( int i = 0; i < size; ++ i )
        {
            final int k = lists.get(i).size();
            final int diff = Math.abs( k - 1000 );
            assertTrue( diff < limit, diff + " >= " + limit );
        }

    }

    @Test
    public void test_monotonicity()
    {

        final int fromSize = random.nextInt(16) + 4;
        final int toSize = fromSize + 1;
        final int keys = 200000;

        final BucketBasedEngine fromEngine = new FlipEngine( fromSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        final Map<Integer,Integer> fromMapping = getKeyMapping( fromEngine, keys );
        
        final BucketBasedEngine toEngine = new FlipEngine( toSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        final Map<Integer,Integer> toMapping = getKeyMapping( toEngine, keys );

        final List<List<Integer>> lists = new ArrayList<>( toSize );
        for( int i = 0; i < toSize; ++i )
            lists.add( new ArrayList<>() );
            
        for( int i = 0; i < keys; ++i )
        {
            final int from = fromMapping.get( i );
            final int to   = toMapping.get( i );

            if( from != to )
                lists.get( to ).add( i );
        }

        int movedWrong = 0;
        for( int i = 0; i < fromSize; ++i )
        {
            int m = lists.get( i ).size();
            movedWrong += m;
        }
        assertEquals( movedWrong, 0 );
        
    }

    public Map<Integer,Integer> getKeyMapping( BucketBasedEngine engine, int keys )
    {

        final Map<Integer,Integer> map = new HashMap<>( keys );
        for( int i = 0; i < keys; ++i )
        {
            final String key = String.valueOf( i );
            final int bucket = engine.getBucket( key );
            map.put( i, bucket );
        }

        return map;

    }


}