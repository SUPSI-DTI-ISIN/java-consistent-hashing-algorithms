package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
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
 * Suite to test class {@link CommonConfig}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CommonConfigTests
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
    public CommonConfig sampleValue( Map<String,Object> source )
    {

        final CommonConfig config = new CommonConfig();
        config.merge( PATH, source );

        return config;

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_not_change_the_defaults( Map<String,Object> source )
    {

        final CommonConfig config = sampleValue( source );
        
        assertEquals( CommonConfig.DEFAULT_GC, config.isGc());
        assertEquals( CommonConfig.DEFAULT_OUTPUT_FOLDER, config.getOutputFolder() );
        assertEquals( CommonConfig.DEFAULT_INIT_NODES, config.getInitNodes() );
        assertEquals( CommonConfig.DEFAULT_FUNCTIONS, config.getHashFunctions() );

        assertEquals( IterationsConfig.DEFAULT_WARMUP, config.getIterations().getWarmup() );
        assertEquals( IterationsConfig.DEFAULT_EXECUTION, config.getIterations().getExecution() );

        assertEquals( TimeConfig.DEFAULT_UNIT, config.getTime().getUnit() );
        assertEquals( TimeConfig.DEFAULT_MODE, config.getTime().getMode() );
        assertEquals( TimeConfig.DEFAULT_WARMUP, config.getTime().getWarmup() );
        assertEquals( TimeConfig.DEFAULT_EXECUTION, config.getTime().getExecution() );

    }

    @Test
    public void changing_the_properties_should_apply()
    {

        final boolean newGc = false;
        final String newOutputFolder = "/new/output/folder";
        final List<String> newFunctions = Collections.singletonList( "XX" );
        final List<Integer> newInitNodes = Collections.singletonList( 10 );

        final Map<String,Object> source = new HashMap<>();
        source.put( "gc", newGc );
        source.put( "init-nodes", newInitNodes );
        source.put( "output-folder", newOutputFolder );
        source.put( "hash-functions", newFunctions );
        
        final CommonConfig config = sampleValue( source );
        assertEquals( newGc, config.isGc() );
        assertEquals( newInitNodes, config.getInitNodes() );
        assertEquals( Path.of(newOutputFolder), config.getOutputFolder() );
        assertEquals( Path.of(newOutputFolder).resolve("results"), config.getResultsFolder() );

        assertNotNull( config.getHashFunctions() );
        assertTrue( config.getHashFunctions().size() == 1 );
        assertEquals( "xx", config.getHashFunctions().get(0) );

    }

    @Test
    public void changing_the_iterations_properties_should_apply()
    {

        final int newWarmup = 20;
        final int newExecution = 30;
        
        final Map<String,Object> iterations = new HashMap<>();
        iterations.put( "warmup", newWarmup );
        iterations.put( "execution", newExecution );

        final Map<String,Object> source = Collections.singletonMap( "iterations", iterations );
        
        final CommonConfig config = sampleValue( source );
        assertEquals( newWarmup, config.getIterations().getWarmup() );
        assertEquals( newExecution, config.getIterations().getExecution() );

    }

    @Test
    public void changing_the_time_properties_should_apply()
    {

        final Mode newMode = Mode.SampleTime;
        final TimeUnit newUnit = TimeUnit.HOURS;
        final TimeValue newWarmup = new TimeValue( 20, TimeUnit.SECONDS );
        final TimeValue newExecution = new TimeValue( 30, TimeUnit.SECONDS );
        
        final Map<String,Object> iterations = new HashMap<>();
        iterations.put( "unit", newUnit.toString() );
        iterations.put( "mode", newMode.toString() );
        iterations.put( "warmup", newWarmup.getTime() );
        iterations.put( "execution", newExecution.getTime() );

        final Map<String,Object> source = Collections.singletonMap( "time", iterations );
        
        final CommonConfig config = sampleValue( source );
        assertEquals( newUnit, config.getTime().getUnit() );
        assertEquals( newMode, config.getTime().getMode() );
        assertEquals( newWarmup, config.getTime().getWarmup() );
        assertEquals( newExecution, config.getTime().getExecution() );

    }

    @ParameterizedTest
    @ValueSource(strings={" ","\t","\n","\r"," \t\n\r"})
    public void if_outputFolder_is_blank_should_not_overwrite_the_default_value( String outputFolder )
    {

        final Map<String,Object> source = Collections.singletonMap( "output-folder", outputFolder );
        final CommonConfig config = sampleValue( source );
        assertEquals( CommonConfig.DEFAULT_OUTPUT_FOLDER, config.getOutputFolder() );

    }

    @Test
    public void initNodes_should_be_positive_otherwise_an_exception_is_thrown()
    {

        final List<Integer> initNodes = Arrays.asList( 10, -10, 100 );
        final Map<String,Object> source = Collections.singletonMap( "init-nodes", initNodes );
        
        assertThrows( InconsistentValueException.class, () -> sampleValue(source) );

    }

    @ParameterizedTest
    @ValueSource(strings={" ","\t","\n","\r"," \t\n\r"})
    public void functions_should_not_be_blank_otherwise_an_exception_is_thrown( String function )
    {

        final List<String> functions = Arrays.asList( "first-function", function, "last-function" );
        final Map<String,Object> source = Collections.singletonMap( "hash-functions", functions );
        
        assertThrows( MissingValueException.class, () -> sampleValue(source) );

    }

    @ParameterizedTest
    @ValueSource(strings={"MURMUR3","MurMur3","murmur3-hash","hash-murmur3"," Murmur3Hash", "HashMurMur3"})
    public void function_names_should_be_normalized( String function )
    {

        final List<String> sourceFunctions = Collections.singletonList( function );
        final Map<String,Object> source = Collections.singletonMap( "hash-functions", sourceFunctions );
        
        final CommonConfig config = assertDoesNotThrow( () -> sampleValue(source) );
        final List<String> functions = config.getHashFunctions();

        assertNotNull( functions );
        assertTrue( functions.size() == 1 );
        assertEquals( "murmur3", functions.get(0) );

    }

    @ParameterizedTest
    @ValueSource(strings={""," ","\t\n","bad-distro"})
    public void proper_distribution_names_must_be_listed_otherwise_an_exception_is_thrown( String distribution )
    {

        final List<String> distributions = Arrays.asList( "uniform", distribution, "custom" );
        final Map<String,Object> source = Collections.singletonMap( "key-distributions", distributions );
        
        assertThrows( InvalidConfigException.class, () -> sampleValue(source) );

    }

}
