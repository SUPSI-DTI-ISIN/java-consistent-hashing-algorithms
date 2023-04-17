package ch.supsi.dti.isin.benchmark.adapter.consistenthash.multiprobe;

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
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeEngine;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeHash;

/**
 * Suite to test the {@link MultiProbeFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MultiProbeFactoryTests implements ConsistentHashFactoryContract<MultiProbeFactory>
{


    private static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProbeFactory sampleValue( AlgorithmConfig config )
    {

        return new MultiProbeFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void if_the_provided_capacity_has_invalid_type_validation_should_fail()
    {

        final String probes = "21";
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "probes", probes )
        );
        
        assertThrows( InvalidTypeException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void if_the_provided_capacity_has_out_of_range_validation_should_fail()
    {

        final int probes = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "probes", probes )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void passing_empty_configuration_should_have_default_lookup_size()
    {

        final MultiProbeFactory factory = sampleValue( CONFIG );
        
        final Supplier<MultiProbeEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final MultiProbeEngine engine = supplier.get();
        assertNotNull( engine );

    }

    @Test
    public void provided_number_of_probes_must_be_at_least_1()
    {

        final int value = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "probes", value )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
                
    }

    @Test
    public void passing_custom_configuration_should_take_custom_lookup_size()
    {

        final int probes = random.nextInt( 100 )+ 1;
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "probes", probes )
        );
            
        final MultiProbeFactory factory = sampleValue( config );
        final MultiProbeHash maglev = factory.createConsistentHash( FUNCTION, NODES );
        assertNotNull( maglev );

        final MultiProbeEngine engine = (MultiProbeEngine) maglev.engine();
        assertEquals( probes, engine.probes() );

    }

}
