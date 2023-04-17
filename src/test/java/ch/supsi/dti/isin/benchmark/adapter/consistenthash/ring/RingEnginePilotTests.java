package ch.supsi.dti.isin.benchmark.adapter.consistenthash.ring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.ring.RingEngine;
import ch.supsi.dti.isin.consistenthash.ring.VirtualNode;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link RingEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RingEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new RingEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_generate_the_expected_number_of_virtual_nodes()
    {

        final int virtualNodesCount = new Random().nextInt( 100 ) + 1;
        final RingEngine engine = new RingEngine( virtualNodesCount, HashFunction.create(HashFunction.Algorithm.XX) );
        final RingEnginePilot pilot = new RingEnginePilot( engine );

        final Collection<VirtualNode> virtualNodes = pilot.addNode();
        assertNotNull( virtualNodes );
        assertEquals( virtualNodesCount, virtualNodes.size() );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final RingEngine engine = new RingEngine( HashFunction.create(HashFunction.Algorithm.XX) );
        final RingEnginePilot pilot = new RingEnginePilot( engine );

        pilot.addNode();
        final String retrievedNode = pilot.getNode( "any_string" );

        assertEquals( "node_0", retrievedNode );

    }

}
