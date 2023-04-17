package ch.supsi.dti.isin.benchmark.adapter.consistenthash.ring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.InvalidTypeException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.consistenthash.ring.RingEngine;
import ch.supsi.dti.isin.consistenthash.ring.RingHash;


/**
 * Suite to test the {@link RingFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RingFactoryTests implements ConsistentHashFactoryContract<RingFactory>
{


    private static final Random random = new Random();
    

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public RingFactory sampleValue( AlgorithmConfig config )
    {

        return new RingFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void if_the_provided_capacity_has_invalid_type_validation_should_fail()
    {

        final String virtualNodes = "1000";
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "virtual-nodes", virtualNodes )
        );
        
        assertThrows( InvalidTypeException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void if_the_provided_capacity_has_out_of_range_validation_should_fail()
    {

        final int virtualNodes = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "virtual-nodes", virtualNodes )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void passing_empty_configuration_should_have_default_lookup_size()
    {

        final RingFactory factory = sampleValue( CONFIG );
        
        final Supplier<RingEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final RingEngine engine = supplier.get();
        assertNotNull( engine );

    }

    @Test
    public void provided_number_of_probes_must_be_at_least_1()
    {

        final int value = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "virtual-nodes", value )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
                
    }

    @Test
    public void passing_custom_configuration_should_take_custom_lookup_size()
    {

        final int virtualNodes = random.nextInt( 100 )+ 1;
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "virtual-nodes", virtualNodes )
        );
            
        final RingFactory factory = sampleValue( config );
        final RingHash ring = factory.createConsistentHash( FUNCTION, NODES );
        assertNotNull( ring );

        final RingEngine engine = (RingEngine) ring.engine();
        assertEquals( virtualNodes, engine.virtualNodesCount() );

    }

}
