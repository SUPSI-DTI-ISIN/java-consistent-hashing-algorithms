package ch.supsi.dti.isin.consistenthash.power;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link PowerEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PowerEngineTests implements Contract<PowerEngine>
{

    public static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Creates a new {@link PowerEngine} with the given size.
     * 
     * @param size size of the cluster
     * @return new instance of {@link PowerEngine}
     */
    public PowerEngine sampleValue( int size )
    {

        return new PowerEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PowerEngine sampleValue()
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
        final PowerEngine engine = sampleValue( size );
        assertEquals(size, engine.size());

        int m = Integer.highestOneBit( size );
        if( size > m )
            m = m << 1;

        assertEquals( m, engine.m() );

    }

    @Test
    public void adding_a_new_bucket_when_size_lt_m_should_increase_the_size_but_not_m()
    {

        int size = random.nextInt( 100 ) + 1;
        final int m = Integer.highestOneBit( size ) << 1;
        if( size == m )
            size += 1;
        
        final PowerEngine engine = sampleValue( size );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( m, engine.m() );

    }

    @Test
    public void adding_a_new_bucket_when_size_eq_m_should_increase_both_the_size_and_m()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size );
        
        final PowerEngine engine = sampleValue( size );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( size << 1, engine.m() );

    }

    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final PowerEngine engine = sampleValue( size );
        final int bucket = engine.addBucket();

        assertEquals( size, bucket );

    }

    @Test
    public void removing_a_bucket_when_size_gt_m2_plus_1_should_decrease_the_size_but_not_m()
    {

        // int size = random.nextInt( 100 ) + 1;
        int size = 64;
        int m = Integer.highestOneBit( size );
        if( size == m )
            size += 1;
        
        m = m << 1;

        final PowerEngine engine = sampleValue( size + 1 );
        engine.removeBucket( 0 );

        assertEquals( size, engine.size() );
        assertEquals( m, engine.m() );
        assertEquals( m-1, engine.m1() );
        assertEquals( (m>>1)-1, engine.m2() );

    }

    @Test
    public void removing_a_bucket_when_size_eq_m2_plus_1_should_decrease_both_the_size_and_m()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size );
        
        final PowerEngine engine = sampleValue( size + 1 );

        engine.removeBucket( 0 );
        assertEquals( size, engine.size() );
        assertEquals( size, engine.m() );
        assertEquals( size-1, engine.m1() );
        assertEquals( (size>>1)-1, engine.m2() );

    }

    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final PowerEngine engine = new PowerEngine( 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final PowerEngine engine = new PowerEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );       
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

}