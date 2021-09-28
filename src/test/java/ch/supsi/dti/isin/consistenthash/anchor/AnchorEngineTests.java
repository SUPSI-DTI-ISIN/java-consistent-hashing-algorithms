package ch.supsi.dti.isin.consistenthash.anchor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link AnchorEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AnchorEngineTests
{


    /** Random values generator */
    private static final Random random = new Random();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void a_new_created_engine_should_have_the_expected_size_and_capacity()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 );

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertEquals( capacity, engine.capacity() );
        assertEquals( size, engine.size() );

    }


    @Test
    public void adding_a_new_bucket_should_change_the_size_of_the_working_set()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 ) + 1;

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.addBucket();

        assertEquals( size + 1, engine.size() );
        assertEquals( capacity, engine.capacity() );

    }


    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 ) + 1;

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int bucket = engine.addBucket();

        assertEquals( size, bucket );

    }


    @Test
    public void adding_a_removed_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int removed = engine.removeBucket( toRemove );
        final int bucket = engine.addBucket();

        assertEquals( removed, bucket );

    }


    @Test
    public void adding_buckets_beyond_the_capacity_should_cause_an_error()
    {

        final AnchorEngine engine = new AnchorEngine( 10, 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertThrows( IndexOutOfBoundsException.class, () -> engine.addBucket() );

    }


    @Test
    public void removing_an_existing_bucket_should_change_the_size_of_the_working_set()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.removeBucket( toRemove );

        assertEquals( size - 1, engine.size() );
        assertEquals( capacity, engine.capacity() );

    }


    @Test
    public void removing_an_existing_bucket_should_return_the_bucket_id_accordingly()
    {

        final int size = random.nextInt( 100 ) + 1;
        final int capacity = size + random.nextInt( 100 ) + 1;
        final int toRemove = random.nextInt( size );

        final AnchorEngine engine = new AnchorEngine( size, capacity, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final int bucket = engine.removeBucket( toRemove );
        assertEquals( toRemove, bucket );

    }


    @Test
    public void removing_more_buckets_than_available_should_cause_an_error()
    {

        final AnchorEngine engine = new AnchorEngine( 0, 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertThrows( IndexOutOfBoundsException.class, () -> engine.removeBucket(0) );

    }
    

    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final AnchorEngine engine = new AnchorEngine( 1, 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final AnchorEngine engine = new AnchorEngine( 10, 20, ConsistentHash.DEFAULT_HASH_FUNCTION );       

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
