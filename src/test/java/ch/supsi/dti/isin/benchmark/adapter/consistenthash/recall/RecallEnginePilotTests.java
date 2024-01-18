package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.recall.RecallEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link RecallEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RecallEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new RecallEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final int nodes = new Random().nextInt( 100 ) + 1;
        final RecallEngine engine = new RecallEngine( nodes, HashFunction.create(HashFunction.Algorithm.XX) );
        final RecallEnginePilot pilot = new RecallEnginePilot( engine );

        final int bucket = pilot.addNode();
        assertEquals( nodes, bucket );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final RecallEngine engine = new RecallEngine( 1, HashFunction.create(HashFunction.Algorithm.XX) );
        final RecallEnginePilot pilot = new RecallEnginePilot( engine );

        final int bucket = pilot.getNode( "any_string" );

        assertEquals( 0, bucket );

    }

}
