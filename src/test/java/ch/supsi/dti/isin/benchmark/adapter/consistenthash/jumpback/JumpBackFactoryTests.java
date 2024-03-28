package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jumpback;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.jumpback.JumpBackEngine;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Suite to test the {@link JumpBackFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JumpBackFactoryTests implements ConsistentHashFactoryContract<JumpBackFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public JumpBackFactory sampleValue( AlgorithmConfig config )
    {

        return new JumpBackFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void default_configuration_should_return_default_engine()
    {

        final JumpBackFactory factory = sampleValue( CONFIG );
        
        final Supplier<JumpBackEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final JumpBackEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
