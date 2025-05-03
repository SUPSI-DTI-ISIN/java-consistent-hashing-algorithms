package ch.supsi.dti.isin.consistenthash.memento9995;

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
import ch.supsi.dti.isin.consistenthash.ConsistentHashContract;

/**
 * Test suite for the class {@link Memento9995Hash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Memento9995HashTests implements ConsistentHashContract<Memento9995Hash>
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */

    /**
     * {@inheritDoc}
     */
    @Override
    public Memento9995Hash sampleValue( Collection<? extends Node> nodes )
    {

        return new Memento9995Hash( nodes );

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
            () -> new Memento9995Hash( nodes )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new Memento9995Hash( Collections.singletonList(null) )
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
            () -> new Memento9995Hash( nodes )
        );
        
    }

}
