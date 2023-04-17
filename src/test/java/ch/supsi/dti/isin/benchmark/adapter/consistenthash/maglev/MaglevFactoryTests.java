package ch.supsi.dti.isin.benchmark.adapter.consistenthash.maglev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.math.PrimeSieve;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryContract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.InvalidTypeException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevEngine;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevHash;

/**
 * Suite to test the {@link MaglevFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MaglevFactoryTests implements ConsistentHashFactoryContract<MaglevFactory>
{


    private static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public MaglevFactory sampleValue( AlgorithmConfig config )
    {

        return new MaglevFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void if_the_provided_capacity_has_invalid_type_validation_should_fail()
    {

        final String permutations = "100";
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "permutations", permutations )
        );
        
        assertThrows( InvalidTypeException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void if_the_provided_capacity_has_out_of_range_validation_should_fail()
    {

        final int permutations = random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "permutations", permutations )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void passing_empty_configuration_should_have_default_lookup_size()
    {

        final MaglevFactory factory = sampleValue( CONFIG );
        
        final Supplier<MaglevEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final MaglevEngine engine = supplier.get();
        assertNotNull( engine );
        assertEquals( 131, engine.lookupSize() );

    }

    @Test
    public void provided_lookup_size_must_be_at_least_128_times_the_number_of_nodes()
    {

        final int value = random.nextInt( 100 );
        final int permutations = (int) PrimeSieve.get().getSmallestPrimeGreaterEqual( value );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "permutations", permutations )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
                
    }

    @Test
    public void passing_custom_configuration_should_take_custom_lookup_size()
    {

        final int permutations = (NODES.size() << 7) + random.nextInt( 100 );
        final int lookupSize = (int) PrimeSieve.get().getSmallestPrimeGreaterEqual( permutations );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "permutations", permutations )
        );
        
        final MaglevFactory factory = sampleValue( config );
        final MaglevHash maglev = factory.createConsistentHash( FUNCTION, NODES );
        assertNotNull( maglev );

        final MaglevEngine engine = (MaglevEngine) maglev.engine();
        assertEquals( lookupSize, engine.lookupSize() );

    }

}
