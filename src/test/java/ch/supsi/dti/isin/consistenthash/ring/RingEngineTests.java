package ch.supsi.dti.isin.consistenthash.ring;

import static java.util.stream.Collectors.toSet;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link RingEngine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RingEngineTests
{

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @ParameterizedTest
    @ValueSource(ints={1,10,100,1000})
    public void adding_a_physical_node_should_return_a_list_of_virtual_nodes( int vNodesCount )
    {

        final RingEngine engine = new RingEngine( vNodesCount, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final String pNode = "physical-node";

        final Collection<VirtualNode> vNodes = engine.addNode( pNode );
        assertNotNull( vNodes );
        assertEquals( vNodesCount, vNodes.size() );
        assertEquals( vNodesCount, engine.virtualNodesCount() );

        for( VirtualNode vNode : vNodes )
        {
            assertNotNull( vNode );
            assertSame( pNode, vNode.physicalNode );
        }

    }


    @ParameterizedTest
    @ValueSource(ints={1,10,100,1000})
    public void the_virtual_nodes_returned_after_adding_a_physical_node_should_be_stored_in_the_ring( int vNodesCount )
    {

        final RingEngine engine = new RingEngine( vNodesCount, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final String pNode = "physical-node";

        final Collection<VirtualNode> vNodes = engine.addNode( pNode );
        assertNotNull( vNodes );
        assertEquals( vNodesCount, vNodes.size() );
        assertEquals( vNodesCount, engine.virtualNodesCount() );

        final Set<VirtualNode> set = vNodes.stream().collect( toSet() );
        assertEquals( vNodesCount, set.size() );

        engine.forEach( (hash, vNode) -> 
        {

            assertNotNull( vNode );
            assertTrue( set.contains(vNode) );
            assertEquals( vNode.hash, hash );
            assertEquals( vNode.physicalNode, pNode );

        });

    }


    @ParameterizedTest
    @ValueSource(ints={1,10,100,1000})
    public void adding_more_physical_nodes_should_work_as_expected( int vNodesCount )
    {

        final RingEngine engine = new RingEngine( vNodesCount, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final String pNode1 = "physical-node-1";
        final String pNode2 = "physical-node-2";

        final Set<String> set = Stream.of( pNode1, pNode2 ).collect( toSet() );
        final Collection<VirtualNode> vNodes = engine.addNode( pNode1 );
        
        assertNotNull( vNodes );
        vNodes.addAll( engine.addNode(pNode2) );

        final int expectedCount = vNodesCount << 1;
        assertEquals( expectedCount , vNodes.size() );
        assertEquals( expectedCount, engine.virtualNodesCount() );

        engine.forEach( (hash, vNode) -> 
        {

            assertNotNull( vNode );
            assertTrue( set.contains(vNode.physicalNode) );

        });

    }


    @Test
    public void removing_virtual_nodes_should_reduce_the_size_of_the_ring_accordingly()
    {
        
        final RingEngine engine = new RingEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
        final String pNode = "physical-node";

        final Set<VirtualNode> vNodes = engine.addNode( pNode ).stream().collect( toSet() );
        assertEquals( vNodes.size(), engine.virtualNodesCount() );
        engine.forEach( (hash,vNode) -> assertTrue(vNodes.contains(vNode)) );
        
        engine.addNode( "physical-node-2" );
        assertEquals( vNodes.size() << 1,  engine.virtualNodesCount() );
        
        engine.removeNodes( vNodes );
        assertEquals( vNodes.size(),  engine.virtualNodesCount() );

        engine.forEach( (hash,vNode) -> assertFalse(vNodes.contains(vNode)) );


    }


    @Test
    public void if_the_cluster_has_one_node_all_the_keys_should_land_to_such_a_node()
    {

        final String pNode = "physical-node";
        final RingEngine engine = new RingEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );
        engine.addNode( pNode );

        final Random random = new Random();
        for( int i = 0; i < 100; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String node = engine.getNode( key );

            assertNotNull( node );
            assertEquals( pNode, node );

        }
        
    }


    @Test
    public void if_the_cluster_has_multiple_nodes_each_node_should_get_some_key()
    {

        final RingEngine engine = new RingEngine( 10, ConsistentHash.DEFAULT_HASH_FUNCTION );

        final Map<String,AtomicInteger> pNodes = new HashMap<>();
        for( int i = 0; i < 10; ++i )
        {
            final String pNode = "physical-node-" + i;
            pNodes.put( pNode, new AtomicInteger() );
            engine.addNode( pNode );
        }
        

        final Random random = new Random();
        for( int i = 0; i < 1000; ++i )
        {
            
            final String key = String.valueOf( random.nextInt() );
            final String node = engine.getNode( key );
            assertNotNull( node );

            final AtomicInteger count = pNodes.get( node );
            assertNotNull( count );

            count.incrementAndGet();

        }

        pNodes.values().stream().forEach( count ->
        {
            assertTrue( count.get() > 0 );
        });
        
    }

}
