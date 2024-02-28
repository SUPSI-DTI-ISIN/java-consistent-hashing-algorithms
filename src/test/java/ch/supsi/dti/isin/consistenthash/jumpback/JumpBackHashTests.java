package ch.supsi.dti.isin.consistenthash.jumpback;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.ConsistentHashContract;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.nerd4j.utils.lang.RequirementFailure;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertThrows;


/**
 * Test suite for the class {@link JumpBackHash}.
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JumpBackHashTests implements ConsistentHashContract<JumpBackHash>
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */

    /**
     * {@inheritDoc}
     */
    @Override
    public JumpBackHash sampleValue( Collection<? extends Node> nodes )
    {

        return new JumpBackHash( nodes );

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
            () -> new JumpBackHash( nodes )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new JumpBackHash( Collections.singletonList(null) )
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
            () -> new JumpBackHash( nodes )
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
