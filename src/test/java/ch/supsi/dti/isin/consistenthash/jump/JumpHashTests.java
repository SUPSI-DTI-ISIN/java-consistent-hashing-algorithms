package ch.supsi.dti.isin.consistenthash.jump;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.ConsistentHashContract;


/**
 * Test suite for the class {@link JumpHash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JumpHashTests implements ConsistentHashContract<JumpHash>
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */

    /**
     * {@inheritDoc}
     */
    @Override
    public JumpHash sampleValue( Collection<? extends Node> nodes )
    {

        return new JumpHash( nodes );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void the_cluster_must_have_at_least_one_node( List<Node> nodes )
    {

        assertThrows(
            RequirementFailure.class,
            () -> new JumpHash( nodes )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new JumpHash( Collections.singletonList(null) )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_duplicated()
    {

        final List<Node> nodes = IntStream.of( 1, 1 )
            .mapToObj( SimpleNode::of)
            .collect( toList() );

        assertThrows(
            RequirementFailure.class,
            () -> new JumpHash( nodes )
        );
        
    }
    
    @Test
    public void only_the_last_node_can_be_removed()
    {

        final ConsistentHash ch = sampleValue( 10 );
        assertThrows(
            RequirementFailure.class,
            () -> ch.removeNodes( Collections.singleton(SimpleNode.of(0)) )
        );
        
    }

}
