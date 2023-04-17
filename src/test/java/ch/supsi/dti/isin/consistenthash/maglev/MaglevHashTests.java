package ch.supsi.dti.isin.consistenthash.maglev;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
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
import org.nerd4j.utils.math.PrimeSieve;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHashContract;

/**
 * Test suite for the class {@link MaglevHash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MaglevHashTests implements ConsistentHashContract<MaglevHash>
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */

    /**
     * {@inheritDoc}
     */
    @Override
    public MaglevHash sampleValue( Collection<? extends Node> nodes )
    {

        final int lookupSize = (int) PrimeSieve.get().getSmallestPrimeGreaterEqual( nodes.size() << 7 );
        return new MaglevHash( nodes, lookupSize );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */

    
    @ParameterizedTest
    @NullAndEmptySource
    public void the_cluster_must_have_at_least_one_node( List<? extends Node> nodes )
    {

        assertThrows(
            RequirementFailure.class,
            () -> new MaglevHash( nodes, 131 )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> new MaglevHash( Collections.singletonList(null), 131 )
        );
        
    }

    @Test
    public void initial_nodes_cannot_be_duplicated()
    {

        final List<Node> nodes = IntStream.of( 1, 1 )
            .mapToObj( SimpleNode::of )
            .collect( toList() );

        assertThrows(
            RequirementFailure.class,
            () -> new MaglevHash( nodes, 131 )
        );
        
    }

    @Test
    public void the_lookup_table_size_must_be_prime()
    {

        final List<Node> nodes = IntStream.of( 1, 2 )
            .mapToObj( SimpleNode::of )
            .collect( toList() );

        assertThrows(
            RequirementFailure.class,
            () -> new MaglevHash( nodes, 100 )
        );
        
    }

    @Test
    public void the_lookup_table_must_be_at_least_128_times_bigger_than_the_cluster()
    {

        final List<Node> nodes = IntStream.of( 1, 2 )
            .mapToObj( SimpleNode::of )
            .collect( toList() );

        assertThrows(
            RequirementFailure.class,
            () -> new MaglevHash( nodes, 11 )
        );
        
    }

    @Test
    public void adding_nodes_cannot_exceed_lookup_table_rate()
    {

        final MaglevHash maglevHash = sampleValue( 1 );
                
        assertDoesNotThrow(
            () -> maglevHash.addNodes( Collections.singleton(SimpleNode.of(1)) )
        );
        assertThrows(
            RequirementFailure.class,
            () -> maglevHash.addNodes( Collections.singleton(SimpleNode.of(2)) )
        );

    }

}
