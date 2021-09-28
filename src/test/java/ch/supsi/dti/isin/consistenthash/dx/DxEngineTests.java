package ch.supsi.dti.isin.consistenthash.dx;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
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

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link DxEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DxEngineTests
{

    private static final Random random = new Random();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void a_new_created_engine_should_have_the_expected_size_and_capacity()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size << 1;
        final DxEngine engine = new DxEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );

        assertEquals( size, engine.size() );
        assertEquals( capacity, engine.capacity() );

    }


    @Test
    public void adding_a_new_bucket_should_change_the_size_but_not_the_capacity()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size << 1;
        final DxEngine engine = new DxEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );

        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( capacity, engine.capacity() );

    }


    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;

        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int bucket = engine.addBucket();

        assertEquals( size, bucket );

    }

    @Test
    public void adding_a_removed_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int removed = engine.removeBucket( toRemove );
        final int bucket = engine.addBucket();

        assertEquals( removed, bucket );

    }


    @Test
    public void removing_an_existing_bucket_should_change_the_size_of_the_working_set()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.removeBucket( toRemove );

        assertEquals( size - 1, engine.size() );

    }


    @Test
    public void removing_an_existing_bucket_should_not_reduce_the_capacity()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size << 1;
        final int toRemove = random.nextInt( size );

        final DxEngine engine = new DxEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.removeBucket( toRemove );

        assertEquals( capacity, engine.capacity() );

    }


    @Test
    public void removing_an_existing_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int bucket = engine.removeBucket( toRemove );
        assertEquals( toRemove, bucket );

    }


    @Test
    public void removing_more_buckets_than_available_will_not_throw_any_error()
    {

        final DxEngine engine = new DxEngine( 0, 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertDoesNotThrow( () -> engine.removeBucket(0) );

    }
    

    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final DxEngine engine = new DxEngine( 1, 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final DxEngine engine = new DxEngine( 10, 20, ConsistentHash.DEFAULT_HASH_FUNCTION );       
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
    public void the_property_of_minimal_distruption_should_hold()
    {

        final int size = 100;
        final int keyCount = 1000;
        
        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
                        
        final Map<String,int[]> map = new HashMap<>();
        final List<String> keys = IntStream.generate( random::nextInt ).limit( keyCount ).mapToObj( String::valueOf ).collect( toList() );
                
        recordOriginalKeyPositions( keys, 1, map, engine );

        final int removed = random.nextInt( size );
        engine.removeBucket( removed );
        for( String key : keys )
        {

            final int previous = map.get( key )[0];
            final int current = engine.getBucket( key );

            if( previous == removed )
                assertNotEquals( previous, current );
            else
                assertEquals( previous, current );

        }

        recordKeyPositions( keys, 0, map, engine );
        engine.addBucket();
        for( String key : keys )
        {

            final int previous = map.get( key )[1];
            final int current = engine.getBucket( key );

            if( current == removed )
                assertNotEquals( previous, current );
            else
                assertEquals( previous, current );

        }
        
    }


    @Test
    public void when_nodes_are_restored_keys_shoud_return_to_the_previous_bucket()
    {

        final int size = 100;
        final int removed = 50;
        final int keyCount = 1000;
        final DxEngine engine = new DxEngine( size, size << 1, ConsistentHash.DEFAULT_HASH_FUNCTION );       
        
        final Map<String,int[]> map = new HashMap<>();
        final List<String> keys = IntStream.generate( random::nextInt ).limit( keyCount ).mapToObj( String::valueOf ).collect( toList() );
        final List<Integer> nodes = getNodesToRemove( size, removed );

        recordOriginalKeyPositions( keys, removed, map, engine);

        for( int i = 0; i < removed; ++i )
        {
            engine.removeBucket( nodes.get(i) );
            recordKeyPositions( keys, i, map, engine );
        }

        for( int i = removed - 1; i >= 0; --i )
        {

            final int restored = engine.addBucket();
            assertEquals( nodes.get(i), restored );

            for( String key : keys )
            {

                final int previous = map.get( key )[i];
                final int current = engine.getBucket( key );

                assertEquals( previous, current );

            }

        }
        
    }

    /* **************** */
    /*  HELPER METHODS  */
    /* **************** */


    private List<Integer> getNodesToRemove( int totalSize, int removeSize )
    {

        final List<Integer> values = IntStream.range( 0, totalSize ).boxed().collect( toList() );
        Collections.shuffle( values );
        
        return values.subList( 0, removeSize );

    }


    private void recordOriginalKeyPositions( List<String> keys, int iterations, Map<String,int[]> map, DxEngine engine )
    {

        for( String key : keys )
        {
            
            final int bucket = engine.getBucket( key );

            final int[] buckets = new int[iterations + 1];
            buckets[0] = bucket;

            map.put( key, buckets );

        }

    }

    private void recordKeyPositions( List<String> keys, int iteration, Map<String,int[]> map, DxEngine engine )
    {

        for( String key : keys )
        {
            
            final int bucket = engine.getBucket( key );
            map.get( key )[iteration+1] = bucket;

        }

    }

}
