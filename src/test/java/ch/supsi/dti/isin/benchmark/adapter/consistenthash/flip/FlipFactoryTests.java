package ch.supsi.dti.isin.benchmark.adapter.consistenthash.flip;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.flip.FlipEngine;

/**
 * Suite to test the {@link FlipFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class FlipFactoryTests implements ConsistentHashFactoryContract<FlipFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public FlipFactory sampleValue( AlgorithmConfig config )
    {

        return new FlipFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void default_configuration_should_return_default_engine()
    {

        final FlipFactory factory = sampleValue( CONFIG );
        
        final Supplier<FlipEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final FlipEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
