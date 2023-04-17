package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


/**
 * Suite to test class {@link IterationsConfig}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IterationsConfigTests
{


    private static final ValuePath PATH = ValuePath.root();


    /* ******************** */
    /*  INTERFACE METHORDS  */
    /* ******************** */


    /**
     * Returns an instance of the class to test.
     *
     * @return instance of the class to test.
     */
    public IterationsConfig sampleValue( Object source )
    {

        return IterationsConfig.of( PATH, source );

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void the_default_configuration_should_have_default_values()
    {

        final IterationsConfig config = IterationsConfig.getDefault();
        assertEquals( IterationsConfig.DEFAULT_WARMUP, config.getWarmup() );
        assertEquals( IterationsConfig.DEFAULT_EXECUTION, config.getExecution() );

    }

    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_not_change_the_defaults( Map<String,Object> source )
    {

        final IterationsConfig config = sampleValue( source );
        assertEquals( IterationsConfig.DEFAULT_WARMUP, config.getWarmup() );
        assertEquals( IterationsConfig.DEFAULT_EXECUTION, config.getExecution() );

    }

    @Test
    public void changing_the_properties_should_apply()
    {

        final int newWarmup = 10;
        final int newExecution = 10;

        final Map<String,Object> source = new HashMap<>();
        source.put( "warm-up", newWarmup );
        source.put( "execution", newExecution );

        final IterationsConfig config = sampleValue( source );
        assertEquals( newWarmup, config.getWarmup() );
        assertEquals( newExecution, config.getExecution() );

    }

    @ParameterizedTest
    @ValueSource(ints={-1})
    public void the_warmup_value_should_be_greater_than_zero( int warmup )
    {

        final Map<String,Object> source = Collections.singletonMap( "warm-up", warmup );
        assertThrows( InconsistentValueException.class, () -> sampleValue(source) );
        
    }

    @ParameterizedTest
    @ValueSource(ints={-1,0})
    public void the_execution_value_should_be_greater_than_zero( int execution )
    {

        final Map<String,Object> source = Collections.singletonMap( "execution", execution );
        assertThrows( InconsistentValueException.class, () -> sampleValue(source) );

    }

}
