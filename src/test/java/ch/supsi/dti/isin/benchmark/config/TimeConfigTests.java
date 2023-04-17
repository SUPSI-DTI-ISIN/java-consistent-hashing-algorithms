package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.options.TimeValue;


/**
 * Suite to test class {@link TimeConfig}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class TimeConfigTests
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
    public TimeConfig sampleValue( Object source )
    {

        return TimeConfig.of( PATH, source );

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_not_change_the_defaults( Map<String,Object> source )
    {

        final TimeConfig config = sampleValue( source );
        assertEquals( TimeConfig.DEFAULT_UNIT, config.getUnit() );
        assertEquals( TimeConfig.DEFAULT_MODE, config.getMode() );
        assertEquals( TimeConfig.DEFAULT_WARMUP, config.getWarmup() );
        assertEquals( TimeConfig.DEFAULT_EXECUTION, config.getExecution() );

    }

    @Test
    public void changing_the_properties_should_apply()
    {

        final TimeUnit newUnit = TimeUnit.NANOSECONDS;
        final Mode newMode = Mode.SampleTime;
        final TimeValue newWarmup = TimeValue.seconds( 10 );
        final TimeValue newExecution = TimeValue.seconds( 10 );

        final Map<String,Object> source = new HashMap<>();
        source.put( "unit", newUnit.name() );
        source.put( "mode", newMode.name() );
        source.put( "warm-up", newWarmup.getTime() );
        source.put( "execution", newExecution.getTime() );

        final TimeConfig config = sampleValue( source );
        assertEquals( newUnit, config.getUnit() );
        assertEquals( newMode, config.getMode() );
        assertEquals( newWarmup, config.getWarmup() );
        assertEquals( newExecution, config.getExecution() );

    }

    @ParameterizedTest
    @ValueSource(ints={-1,0})
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
