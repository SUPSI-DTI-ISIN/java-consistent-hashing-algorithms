package ch.supsi.dti.isin.benchmark.adapter.consistenthash.power;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.power.PowerEngine;

/**
 * Suite to test the {@link PowerFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PowerFactoryTests implements ConsistentHashFactoryContract<PowerFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public PowerFactory sampleValue( AlgorithmConfig config )
    {

        return new PowerFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void default_configuration_should_return_default_engine()
    {

        final PowerFactory factory = sampleValue( CONFIG );
        
        final Supplier<PowerEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final PowerEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
