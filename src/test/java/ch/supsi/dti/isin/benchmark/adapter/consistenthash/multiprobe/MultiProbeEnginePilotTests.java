package ch.supsi.dti.isin.benchmark.adapter.consistenthash.multiprobe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeEngine;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link MultiProbeEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MultiProbeEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new MultiProbeEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( MultiProbeHash.DEFAULT_PROBES, HashFunction.create(HashFunction.Algorithm.XX) );
        final MultiProbeEnginePilot pilot = new MultiProbeEnginePilot( engine );

        final String resource = pilot.addNode();
        assertEquals( "resource_0", resource );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final MultiProbeEngine engine = new MultiProbeEngine( MultiProbeHash.DEFAULT_PROBES, HashFunction.create(HashFunction.Algorithm.XX) );
        final MultiProbeEnginePilot pilot = new MultiProbeEnginePilot( engine );

        final String addedNode = pilot.addNode();
        final String retrievedNode = pilot.getNode( "any_string" );

        assertEquals( addedNode, retrievedNode );

    }

}
