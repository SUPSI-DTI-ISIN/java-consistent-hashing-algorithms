package ch.supsi.dti.isin.consistenthash.binomial;

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
 * Test suite for the class {@link BinomialEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BinomialEngineTests implements Contract<BinomialEngine>
{

    public static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Creates a new {@link BinomialEngine} with the given size.
     * 
     * @param size size of the cluster
     * @return new instance of {@link BinomialEngine}
     */
    public BinomialEngine sampleValue( int size )
    {

        return new BinomialEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinomialEngine sampleValue()
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
        final BinomialEngine engine = sampleValue( size );
        assertEquals(size, engine.size());

        int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        assertEquals( expectedSmallCapacity, engine.lowerTreeFilter() );
        assertEquals( (expectedSmallCapacity << 1) | 1, engine.upperTreeFilter() );

    }

    @Test
    public void adding_a_new_bucket_when_size_lt_upperTreeFilter_should_increase_the_size_but_not_upperTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        final int lowerTreeFilter = Integer.highestOneBit( size ) - 1;
        final int upperTreeFilter = (lowerTreeFilter << 1) | 1;
        if( size == upperTreeFilter )
            size += 1;
        
        final BinomialEngine engine = sampleValue( size );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );

        final int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        assertEquals( expectedSmallCapacity, engine.lowerTreeFilter() );

        final int expectedTreeCapacity  = (expectedSmallCapacity << 1) | 1;
        assertEquals( expectedTreeCapacity, engine.upperTreeFilter() );

    }

    @Test
    public void adding_a_new_bucket_when_size_eq_upperTreeFilter_should_increase_both_the_size_and_upperTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size ) - 1;
        
        final BinomialEngine engine = sampleValue( size );
        assertEquals( size, engine.size() );
        assertEquals( size, engine.upperTreeFilter() );

        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( size, engine.lowerTreeFilter() );

        final int expectedTreeCapacity = (size << 1) | 1;
        assertEquals( expectedTreeCapacity, engine.upperTreeFilter() );

    }

    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final BinomialEngine engine = sampleValue( size );
        final int bucket = engine.addBucket();

        assertEquals( size, bucket );

    }

    @Test
    public void removing_a_bucket_when_size_gt_lowerTreeFilter_plus_1_should_decrease_the_size_but_not_the_capacity()
    {

        int size = random.nextInt( 100 ) + 4;
        final int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        final int expectedTreeCapacity = (expectedSmallCapacity << 1) | 1;
        if( size == expectedSmallCapacity )
            size += 1;

        final BinomialEngine engine = sampleValue( size + 1 );
        assertEquals( size + 1, engine.size() );
        assertEquals( expectedTreeCapacity, engine.upperTreeFilter() );
        assertEquals( expectedSmallCapacity, engine.lowerTreeFilter() );

        engine.removeBucket( 0 );

        assertEquals( size, engine.size() );
        assertEquals( expectedTreeCapacity, engine.upperTreeFilter() );
        assertEquals( expectedSmallCapacity, engine.lowerTreeFilter() );

    }

    @Test
    public void removing_a_bucket_when_size_eq_upperTreeFilter_plus_1_should_decrease_both_size_and_upperTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size );
        
        final BinomialEngine engine = sampleValue( size );

        engine.removeBucket( 0 );
        assertEquals( size-1, engine.size() );
        assertEquals( size-1, engine.upperTreeFilter() );
        assertEquals( (size-1)>>1, engine.lowerTreeFilter() );

    }

    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final BinomialEngine engine = new BinomialEngine( 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final BinomialEngine engine = new BinomialEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );       
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