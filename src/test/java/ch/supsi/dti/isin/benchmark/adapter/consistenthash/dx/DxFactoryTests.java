package ch.supsi.dti.isin.benchmark.adapter.consistenthash.dx;

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
import ch.supsi.dti.isin.consistenthash.dx.DxEngine;

/**
 * Suite to test the {@link DxFactory} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DxFactoryTests implements ConsistentHashFactoryContract<DxFactory>
{


    private static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public DxFactory sampleValue( AlgorithmConfig config )
    {

        return new DxFactory( config );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void if_the_provided_capacity_has_invalid_type_validation_should_fail()
    {

        final String capacity = "100";
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "capacity", capacity )
            );
            
        assertThrows( InvalidTypeException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void if_the_provided_capacity_has_out_of_range_validation_should_fail()
    {

        final int capacity = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "capacity", capacity )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void passing_empty_configuration_should_take_default_capacity()
    {

        final DxFactory factory = sampleValue( CONFIG );
        
        final Supplier<DxEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final DxEngine engine = supplier.get();
        assertNotNull( engine );
        assertEquals( 10, engine.capacity() );

    }

    @Test
    public void provided_capacity_must_be_at_least_1()
    {

        final int capacity = -random.nextInt( 100 );
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "capacity", capacity )
        );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(config) );
        
    }

    @Test
    public void passing_custom_configuration_should_take_custom_capacity()
    {

        final int capacity = random.nextInt( 100 ) + 1;
        final AlgorithmConfig config = AlgorithmConfig.of(
            ValuePath.root(), 
            algorithmConfig( "capacity", capacity )
        );
            
        final DxFactory factory = sampleValue( config );
        final Supplier<DxEngine> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final DxEngine engine = supplier.get();
        assertNotNull( engine );
        assertEquals( capacity, engine.capacity() );

    }

}
