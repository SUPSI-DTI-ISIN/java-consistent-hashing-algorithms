package ch.supsi.dti.isin.benchmark.adapter.consistenthash.flip;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.flip.FlipEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test the {@link FlipEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FlipEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new FlipEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final int nodes = new Random().nextInt( 100 ) + 1;
        final FlipEngine engine = new FlipEngine( nodes, HashFunction.create(HashFunction.Algorithm.XX) );
        final FlipEnginePilot pilot = new FlipEnginePilot( engine );

        final int bucket = pilot.addNode();
        assertEquals( nodes, bucket );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final FlipEngine engine = new FlipEngine( 1, HashFunction.create(HashFunction.Algorithm.XX) );
        final FlipEnginePilot pilot = new FlipEnginePilot( engine );

        final int bucket = pilot.getNode( "any_string" );

        assertEquals( 0, bucket );

    }

}
