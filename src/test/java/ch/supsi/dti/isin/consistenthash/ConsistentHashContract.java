package ch.supsi.dti.isin.consistenthash;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.Contract;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;

/**
 * Suite to test the contract imposed by the {@link ConsistentHash} interface.
 *
 * @param <CH> implementation of the hash function to test
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public interface ConsistentHashContract<CH extends ConsistentHash> extends Contract<CH>
{
    
    /** Random values generator */
    static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */
    

    /**
     * Returns a sample {@link ConsistentHash} instance with
     * the given initial nodes.
     * 
     * @param nodes initial nodes to set
     * @return sample {@link ConsistentHash} instance
     */
    CH sampleValue( Collection<? extends Node> nodes );

    /**
     * Returns a sample {@link ConsistentHash} instance with
     * the given number of nodes.
     * 
     * @param size number of nodes
     * @return sample {@link ConsistentHash} instance
     */
    default CH sampleValue( int size )
    {

        return sampleValue( SimpleNode.create(size) );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    default CH sampleValue()
    {

        return sampleValue( 1 );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    default void initial_nodes_should_not_contain_duplicated_nodes()
    {

        final List<Node> nodes = IntStream
            .generate( () -> random.nextInt(10) )
            .limit( 20 )
            .mapToObj( SimpleNode::of )
            .collect( Collectors.toList() );

        assertThrows( RequirementFailure.class, () -> sampleValue(nodes) );
        
    }

    @ParameterizedTest
    @NullAndEmptySource
    default void the_key_to_evaluate_cannot_be_null_or_empty( String key )
    {

        assertThrows(
            RequirementFailure.class,
            () -> sampleValue().getNode( key )
        );
        
    }

    @Test
    default void every_key_should_be_associated_to_a_node()
    {

        final String key = "key_" + Math.abs( random.nextInt() );
        final Node node = sampleValue( 10 ).getNode( key );

        assertNotNull( node );

    }

    @Test
    default void keys_with_the_same_value_should_be_sent_to_the_same_node()
    {

        final String key1 = "key_" + Math.abs( random.nextInt() );
        final String key2 = key1.substring( 0 );
        final ConsistentHash ch = sampleValue( 10 );

        assertEquals( ch.getNode(key1), ch.getNode(key2) );

    }

    @Test
    default void all_the_keys_should_not_be_sent_to_the_same_node()
    {

        
        final ConsistentHash ch = sampleValue( 10 );
        final Node node = ch.getNode( "key_" + Math.abs(random.nextInt()) );
        for( int i = 0; i < 100; ++i )
        {

            final String key = "key_" + Math.abs( random.nextInt() );
            if( ! node.equals( ch.getNode(key)) )
                return;

        }

        fail( "Every key lands to the same node out of 10" );

    }

    @ParameterizedTest
    @NullAndEmptySource
    default void method_addNodes_should_not_accept_null_or_empty( List<Node> nodes )
    {

        final ConsistentHash ch = sampleValue( 5 );
        assertThrows( RequirementFailure.class, () -> ch.addNodes(nodes) );

    }

    @Test
    default void nodes_to_add_cannot_be_null()
    {

        final ConsistentHash ch = sampleValue( 5 );
        assertThrows( RequirementFailure.class, () -> ch.addNodes(Collections.singleton(null)) );

    }

    @Test
    default void adding_a_duplicated_node_should_throw_an_exception()
    {

        final ConsistentHash ch = sampleValue( 5 );
        final List<Node> nodes = Collections.singletonList( ch.getNode("key") );
        assertThrows(
            RequirementFailure.class,
            () -> ch.addNodes( nodes )
        );

    }

    @Test
    default void when_a_node_is_added_the_node_count_should_increase()
    {

        
        final ConsistentHash ch = sampleValue( 10 );
        final int expected = ch.nodeCount() + 1;

        ch.addNodes( Collections.singleton(SimpleNode.of(10)) );
        assertEquals( expected, ch.nodeCount() );

    }

    @ParameterizedTest
    @NullAndEmptySource
    default void method_removeNode_should_not_accept_null_or_empty( List<Node> nodes )
    {

        final ConsistentHash ch = sampleValue( 5 );
        assertThrows( RequirementFailure.class, () -> ch.removeNodes(nodes) );
        
    }

    @Test
    default void nodes_to_remove_cannot_be_null()
    {

        final ConsistentHash ch = sampleValue( 5 );
        assertThrows( RequirementFailure.class, () -> ch.removeNodes(Collections.singleton(null)) );
        
    }

    @Test
    default void removing_an_unexisting_node_should_throw_an_exception()
    {

        final ConsistentHash ch = sampleValue( 5 );
        final List<Node> nodes = Collections.singletonList( SimpleNode.of(100) );
        assertThrows(
            RequirementFailure.class,
            () -> ch.removeNodes( nodes )
        );

    }

    @Test
    default void when_a_node_is_removed_the_node_count_should_decrease()
    {

        
        final ConsistentHash ch = sampleValue( 10 );
        final int expected = ch.nodeCount() - 1;

        ch.removeNodes( Collections.singleton(SimpleNode.of(expected)) );
        assertEquals( expected, ch.nodeCount() );

    }
    
    @Test
    default void at_least_one_node_should_remain_in_the_cluster()
    {
        
        final ConsistentHash ch = sampleValue( 2 );

        assertDoesNotThrow( () -> ch.removeNodes(Collections.singleton(SimpleNode.of(1))) );
        assertThrows(
            RequirementFailure.class,
            () -> ch.removeNodes( Collections.singleton(SimpleNode.of(0)) )
        );

    }


}
