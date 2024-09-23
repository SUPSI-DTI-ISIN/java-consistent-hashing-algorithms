package ch.supsi.dti.isin.benchmark.adapter.consistenthash.binomial;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.binomial.BinomialEngine;

/**
 * Suite to test the {@link BinomialFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BinomialFactoryTests implements ConsistentHashFactoryContract<BinomialFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public BinomialFactory sampleValue( AlgorithmConfig config )
    {

        return new BinomialFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void default_configuration_should_return_default_engine()
    {

        final BinomialFactory factory = sampleValue( CONFIG );
        
        final Supplier<BinomialEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final BinomialEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
