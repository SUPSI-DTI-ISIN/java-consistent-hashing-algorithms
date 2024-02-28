package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jumpback;

import ch.supsi.dti.isin.benchmark.adapter.consistenthash.jump.JumpEnginePilot;
import ch.supsi.dti.isin.consistenthash.jumpback.JumpBackEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Suite to test the {@link JumpEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JumpBackEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new JumpBackEnginePilot(null) );

    }

    @Test
    public void the_added_node_should_have_the_expected_name()
    {

        final int nodes = new Random().nextInt( 100 ) + 1;
        final JumpBackEngine engine = new JumpBackEngine( nodes, HashFunction.create(HashFunction.Algorithm.XX) );
        final JumpBackEnginePilot pilot = new JumpBackEnginePilot( engine );

        final int bucket = pilot.addNode();
        assertEquals( nodes, bucket );

    }

    @Test
    public void the_added_node_should_be_retrieved_using_get()
    {

        final JumpBackEngine engine = new JumpBackEngine( 1, HashFunction.create(HashFunction.Algorithm.XX) );
        final JumpBackEnginePilot pilot = new JumpBackEnginePilot( engine );

        final int bucket = pilot.getNode( "any_string" );

        assertEquals( 0, bucket );

    }

}
