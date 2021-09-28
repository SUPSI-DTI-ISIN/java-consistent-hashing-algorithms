package ch.supsi.dti.isin.consistenthash.jump;

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
 * Test suite for the class {@link JumpEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JumpEngineTests
{


    /** Random values generator */
    private static final Random random = new Random();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void a_new_created_engine_should_have_the_expected_size()
    {
        
        final int size = random.nextInt( 100 ) + 1;
        final JumpEngine engine = new JumpEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );

        assertEquals( size, engine.size() );

    }


    @Test
    public void adding_a_new_bucket_should_change_the_size_of_the_working_set()
    {

        
        final int size = random.nextInt( 100 ) + 1;
        final JumpEngine engine = new JumpEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.increase();

        assertEquals( size + 1, engine.size() );

    }

    @Test
    public void removing_an_existing_bucket_should_change_the_size_of_the_working_set()
    {

        
        final int size = random.nextInt( 100 ) + 1;
        final JumpEngine engine = new JumpEngine( size, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.decrease();

        assertEquals( size - 1, engine.size() );

    }


    @Test
    public void if_the_cluster_is_empty_method_getBucket_should_throw_exception()
    {

        final JumpEngine engine = new JumpEngine( 0, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertThrows( IllegalArgumentException.class, () -> engine.getBucket("key") );

    }


    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final JumpEngine engine = new JumpEngine( 1, ConsistentHash.DEFAULT_HASH_FUNCTION );
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

        final JumpEngine engine = new JumpEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );       
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
