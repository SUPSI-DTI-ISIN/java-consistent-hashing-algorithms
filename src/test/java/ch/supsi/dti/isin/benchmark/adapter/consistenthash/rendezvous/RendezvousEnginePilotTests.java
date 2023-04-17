package ch.supsi.dti.isin.benchmark.adapter.consistenthash.rendezvous;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link RendezvousEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RendezvousEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new RendezvousEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final RendezvousEngine engine = new RendezvousEngine( HashFunction.create(HashFunction.Algorithm.XX) );
        final RendezvousEnginePilot pilot = new RendezvousEnginePilot( engine );

        final String resource = pilot.addNode();
        assertEquals( "resource_0", resource );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final RendezvousEngine engine = new RendezvousEngine( HashFunction.create(HashFunction.Algorithm.XX) );
        final RendezvousEnginePilot pilot = new RendezvousEnginePilot( engine );

        final String addedNode = pilot.addNode();
        final String retrievedNode = pilot.getNode( "any_string" );

        assertEquals( addedNode, retrievedNode );

    }

}
