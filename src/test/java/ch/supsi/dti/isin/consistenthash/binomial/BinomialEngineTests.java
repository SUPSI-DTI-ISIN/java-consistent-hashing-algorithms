package ch.supsi.dti.isin.consistenthash.binomial;

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

        int m = Integer.highestOneBit( size );
        if( size == m )
            m = m >> 1;
            
        int expectedMinorTreeFilter = m - 1;
        assertEquals( expectedMinorTreeFilter, engine.minorTreeFilter() );
        assertEquals( (expectedMinorTreeFilter << 1) | 1, engine.enclosingTreeFilter() );

    }

    @Test
    public void adding_a_new_bucket_when_size_lt_enclosingTreeFilter_should_increase_the_size_but_not_enclosingTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        final int minorTreeFilter = Integer.highestOneBit( size ) - 1;
        final int enclosingTreeFilter = (minorTreeFilter << 1) | 1;
        if( size == enclosingTreeFilter )
            size += 1;
        
        final BinomialEngine engine = sampleValue( size );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );

        final int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        assertEquals( expectedSmallCapacity, engine.minorTreeFilter() );

        final int expectedTreeCapacity  = (expectedSmallCapacity << 1) | 1;
        assertEquals( expectedTreeCapacity, engine.enclosingTreeFilter() );

    }

    @Test
    public void adding_a_new_bucket_when_size_eq_enclosingTreeFilter_should_increase_both_the_size_and_enclosingTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size ) - 1;
        
        final BinomialEngine engine = sampleValue( size );
        assertEquals( size, engine.size() );
        assertEquals( size, engine.enclosingTreeFilter() );

        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( size, engine.minorTreeFilter() );

        final int expectedTreeCapacity = (size << 1) | 1;
        assertEquals( expectedTreeCapacity, engine.enclosingTreeFilter() );

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
    public void removing_a_bucket_when_size_gt_minorTreeFilter_plus_1_should_decrease_the_size_but_not_the_capacity()
    {

        int size = random.nextInt( 100 ) + 4;
        final int expectedSmallCapacity = Integer.highestOneBit( size ) - 1;
        final int expectedTreeCapacity = (expectedSmallCapacity << 1) | 1;
        if( size == expectedSmallCapacity )
            size += 1;

        final BinomialEngine engine = sampleValue( size + 1 );
        assertEquals( size + 1, engine.size() );
        assertEquals( expectedTreeCapacity, engine.enclosingTreeFilter() );
        assertEquals( expectedSmallCapacity, engine.minorTreeFilter() );

        engine.removeBucket( 0 );

        assertEquals( size, engine.size() );
        assertEquals( expectedTreeCapacity, engine.enclosingTreeFilter() );
        assertEquals( expectedSmallCapacity, engine.minorTreeFilter() );

    }

    @Test
    public void removing_a_bucket_when_size_eq_enclosingTreeFilter_plus_1_should_decrease_both_size_and_enclosingTreeFilter()
    {

        int size = random.nextInt( 100 ) + 1;
        size = Integer.highestOneBit( size );
        
        final BinomialEngine engine = sampleValue( size );

        engine.removeBucket( 0 );
        assertEquals( size-1, engine.size() );
        assertEquals( size-1, engine.enclosingTreeFilter() );
        assertEquals( (size-1)>>1, engine.minorTreeFilter() );

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


    @Test
    public void test_balance()
    {

        final int size = 12;
        final int h = Integer.highestOneBit( size );
        final int m = size > h ? h << 1 : h;
        final List<List<String>> lists = new ArrayList<>( m );
        for( int i = 0; i < m; ++i )
            lists.add( new ArrayList<>() );

        final BucketBasedEngine engine = new BinomialEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final BucketBasedEngine fromEngine = new BinomialEngine( fromSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        final Map<Integer,Integer> fromMapping = getKeyMapping( fromEngine, keys );
        
        final BucketBasedEngine toEngine = new BinomialEngine( toSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        final Map<Integer,Integer> toMapping = getKeyMapping( toEngine, keys );

        final List<List<Integer>> lists = new ArrayList<>( toSize );
        for( int i = 0; i < toSize; ++i )
            lists.add( new ArrayList<>() );
            
        for( int i = 0; i < keys; ++i )
        {
            final int from = fromMapping.get( i );
            final int to   = toMapping.get( i );

            if( from != to )
            {
                lists.get( to ).add( i );
                if( to < fromSize )
                    System.out.println( fromSize + "-" + toSize + "-> " + i );
            }
        }

        int movedWrong = 0;
        for( int i = 0; i < fromSize; ++i )
        {
            int m = lists.get( i ).size();
            movedWrong += m;
        }
        assertEquals( 0, movedWrong );
        
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

    @Test
    public void debug_test()
    {

        final int fromSize = 4;
        final int toSize = fromSize + 1;
        
        final BucketBasedEngine fromEngine = new BinomialEngine( fromSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        final BucketBasedEngine toEngine = new BinomialEngine( toSize, ConsistentHash.DEFAULT_HASH_FUNCTION ); 
        
        final String key = "195289";

        final int from = fromEngine.getBucket( key );
        final int to   = toEngine.getBucket( key );

        assertEquals( from, to );

    }

}