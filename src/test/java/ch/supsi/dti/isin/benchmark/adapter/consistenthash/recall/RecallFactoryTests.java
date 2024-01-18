package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall;


import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.recall.RecallEngine;

/**
 * Suite to test the {@link RecallFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RecallFactoryTests implements ConsistentHashFactoryContract<RecallFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public RecallFactory sampleValue( AlgorithmConfig config )
    {

        return new RecallFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void default_configuration_should_return_default_engine()
    {

        final RecallFactory factory = sampleValue( CONFIG );
        
        final Supplier<RecallEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final RecallEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
