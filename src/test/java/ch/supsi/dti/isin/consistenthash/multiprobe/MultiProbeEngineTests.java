package ch.supsi.dti.isin.consistenthash.multiprobe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link MultiProbeEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MultiProbeEngineTests
{

    /** Random values generator */
    private static final Random random = new Random();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void adding_a_resource_should_change_the_size_of_the_ring()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( 21, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertEquals( 0, engine.size() );

        final int resources = random.nextInt( 90 ) + 10;
        for( int i = 0; i < resources; ++ i )
        {

            engine.addResource( "resource_" + i );
            assertEquals( i + 1, engine.size() );

        }

    }

    @Test
    public void the_resources_in_the_ring_should_be_sorted()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( 21, ConsistentHash.DEFAULT_HASH_FUNCTION );
        assertEquals( 0, engine.size() );

        final int resources = random.nextInt( 90 ) + 10;
        for( int i = 0; i < resources; ++ i )
            engine.addResource( "resource_" + i );
            
        final Iterator<Point> iter = engine.streamRing().iterator();
        Point prev, current = iter.next();
        while( iter.hasNext() )
        {

            prev = current;
            current = iter.next();

            assertTrue( prev.hash <= current.hash );
            assertTrue( prev.compareTo(current) < 1 );

        }

    }


    @Test
    public void removing_a_resource_should_change_the_size_of_the_ring()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( 21, ConsistentHash.DEFAULT_HASH_FUNCTION );
        for( int i = 0; i < 100; ++i )
            engine.addResource( "resource_" + i );

        assertEquals( 100, engine.size() );

        final int resources = random.nextInt( 90 ) + 10;
        for( int i = 0; i < resources; ++ i )
        {

            engine.removeResource( "resource_" + i );
            assertEquals( 99 - i, engine.size() );

        }

    }

    @Test
    public void after_removing_the_resources_in_the_ring_should_be_sorted()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( 21, ConsistentHash.DEFAULT_HASH_FUNCTION );
        for( int i = 0; i < 100; ++i )
            engine.addResource( "resource_" + i );

        assertEquals( 100, engine.size() );

        final int resources = random.nextInt( 90 ) + 10;
        for( int i = 0; i < resources; ++ i )
            engine.removeResource( "resource_" + i );
            
        final Iterator<Point> iter = engine.streamRing().iterator();
        Point prev, current = iter.next();
        while( iter.hasNext() )
        {

            prev = current;
            current = iter.next();

            assertTrue( prev.hash <= current.hash );
            assertTrue( prev.compareTo(current) < 1 );

        }

    }


    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final String expected = "resource";
        final MultiProbeEngine engine = new MultiProbeEngine( 21, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.addResource( expected);

        final Random random = new Random();
        for( int i = 0; i < 100; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String resource = engine.getResource( key );

            assertNotNull( resource );
            assertEquals( expected, resource );

        }
        
    }


    @Test
    public void if_the_cluster_has_multiple_nodes_each_node_should_get_some_key()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final Map<String,AtomicInteger> resources = new HashMap<>();
        for( int i = 0; i < 10; ++i )
        {
            final String resource = "resource-" + i;
            resources.put( resource, new AtomicInteger() );
            engine.addResource( resource );
        }
        
        for( int i = 0; i < 1000; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String resource = engine.getResource( key );
            assertNotNull( resource );

            final AtomicInteger count = resources.get( resource );
            assertNotNull( count );

            count.incrementAndGet();

        }

        resources.values().stream().forEach( count ->
        {
            assertTrue( count.get() > 0 );
        });
        
    }

}
