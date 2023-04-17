package ch.supsi.dti.isin.benchmark.adapter.consistenthash.maglev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.maglev.MaglevEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link MaglevEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MaglevEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new MaglevEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final MaglevEngine engine = new MaglevEngine( 131, HashFunction.create(HashFunction.Algorithm.XX) );
        final MaglevEnginePilot pilot = new MaglevEnginePilot( engine );

        final String backend = pilot.addNode();
        assertEquals( "backend_0", backend );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final MaglevEngine engine = new MaglevEngine( 131, HashFunction.create(HashFunction.Algorithm.XX) );
        final MaglevEnginePilot pilot = new MaglevEnginePilot( engine );

        final String addedNode = pilot.addNode();
        final String retrievedNode = pilot.getNode( "any_string" );

        assertEquals( addedNode, retrievedNode );

    }

}
