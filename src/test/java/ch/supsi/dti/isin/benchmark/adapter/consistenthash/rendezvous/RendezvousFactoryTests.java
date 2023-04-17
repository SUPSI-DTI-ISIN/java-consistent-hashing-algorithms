package ch.supsi.dti.isin.benchmark.adapter.consistenthash.rendezvous;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousEngine;


/**
 * Suite to test the {@link RendezvousFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RendezvousFactoryTests implements ConsistentHashFactoryContract<RendezvousFactory>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public RendezvousFactory sampleValue( AlgorithmConfig config )
    {

        return new RendezvousFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void passing_empty_configuration_should_have_default_lookup_size()
    {

        final RendezvousFactory factory = sampleValue( CONFIG );
        
        final Supplier<RendezvousEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final RendezvousEngine engine = supplier.get();
        assertNotNull( engine );

    }

}
