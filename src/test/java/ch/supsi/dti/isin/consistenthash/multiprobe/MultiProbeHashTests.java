package ch.supsi.dti.isin.consistenthash.multiprobe;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

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
 * Test suite for the class {@link MultiProbeHash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MultiProbeHashTests implements ConsistentHashContract<MultiProbeHash>
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProbeHash sampleValue( Collection<? extends Node> nodes )
    {

        return new MultiProbeHash( nodes );

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
            () -> new MultiProbeHash( nodes )
        );

    }

    @Test
    public void initial_nodes_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new MultiProbeHash( Collections.singletonList(null) )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_duplicated()
    {

        final List<Node> nodes = Stream.of( 1, 1 )
            .map( SimpleNode::of )
            .collect( toList() );

        assertThrows(
            RequirementFailure.class,
            () -> new MultiProbeHash( nodes )
        );
        
    }

    @Test
    public void the_hash_function_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new MultiProbeHash( Collections.singletonList(SimpleNode.of(0)), null )
        );

    }

    @Test
    public void the_number_of_probes_must_be_strictly_positive()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new MultiProbeHash( Collections.singletonList(SimpleNode.of(0)), 0, ConsistentHash.DEFAULT_HASH_FUNCTION )
        );

    }

}


