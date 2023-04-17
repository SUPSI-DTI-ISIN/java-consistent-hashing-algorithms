package ch.supsi.dti.isin.benchmark.adapter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;

/**
 * Suite to test the {@link BucketBasedEnginePilot} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BucketBasedEnginePilotTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_engine_to_test_is_mandatory()
    {

        assertThrows( RequirementFailure.class,  () -> new BucketBasedEnginePilot(null) {} );

    }

    @Test
    public void the_addBucket_method_should_forward_the_call_to_the_engine()
    {

        final MockEngine engine = new MockEngine();
        final BucketBasedEnginePilot pilot = new BucketBasedEnginePilot( engine ) {};
        
        pilot.addNode();
        assertEquals( 1, engine.callsToAddBucket() );
        assertEquals( 0, engine.callsToGetBucket() );
        assertEquals( 0, engine.callsToRemoveBucket() );

    }

    @Test
    public void the_getBucket_method_should_forward_the_call_to_the_engine()
    {

        final MockEngine engine = new MockEngine();
        final BucketBasedEnginePilot pilot = new BucketBasedEnginePilot( engine ) {};
        
        pilot.getNode( "key" );
        assertEquals( 0, engine.callsToAddBucket() );
        assertEquals( 1, engine.callsToGetBucket() );
        assertEquals( 0, engine.callsToRemoveBucket() );

    }

    @Test
    public void the_removeBucket_method_should_forward_the_call_to_the_engine()
    {

        final MockEngine engine = new MockEngine();
        final BucketBasedEnginePilot pilot = new BucketBasedEnginePilot( engine ) {};
        
        pilot.removeNode( 1 );
        assertEquals( 0, engine.callsToAddBucket() );
        assertEquals( 0, engine.callsToGetBucket() );
        assertEquals( 1, engine.callsToRemoveBucket() );

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    private class MockEngine implements BucketBasedEngine
    {

        private final int[] calls = new int[3];

        @Override
        public int addBucket()
        {
            calls[0]++;
            return 0;
        }

        @Override
        public int getBucket( String key )
        {
            calls[1]++;
            return 0;
        }

        @Override
        public int removeBucket( int b )
        {
            calls[2]++;
            return 0;
        }

        public int callsToAddBucket()
        {
            return calls[0];
        }

        public int callsToGetBucket()
        {
            return calls[1];
        }

        public int callsToRemoveBucket()
        {
            return calls[2];
        }

    }

}
