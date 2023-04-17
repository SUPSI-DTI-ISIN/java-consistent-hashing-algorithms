package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.options.TimeValue;


/**
 * Suite to test class {@link ConfigLoader}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConfigLoaderTests
{


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */

    // TODO: add tests for other methods and storage

    @Test
    public void a_complete_and_working_configuration_should_be_loaded_properly()
    {
        // TODO: uncomment and fix
        // final Config config = assertDoesNotThrow( () ->  ConfigLoader.ofFrom("cp:configs/complete.yaml") );
        // assertNotNull( config );

    }


    /* Test common section */

    /* Test common.gc */

    @ParameterizedTest
    @ValueSource(strings={"12","my/folder","[1,2,3]"})
    public void an_invalid_gc_input_should_throw_an_exception( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                gc: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_gc_input_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                gc: false
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertFalse( config.getCommon().isGc() );

    }

    /* Test common.output-folder */

    @ParameterizedTest
    @ValueSource(strings={" ","\t","\n"," \t\n"})
    public void if_the_outputFolder_is_blank_the_default_value_will_be_taken( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                output-folder: "%s"
            """
            , source
        );

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( CommonConfig.DEFAULT_OUTPUT_FOLDER, config.getCommon().getOutputFolder() );

    }

    @ParameterizedTest
    @ValueSource(strings={"/","/tmp","my/folder"})
    public void a_valid_outputFolder_should_replace_the_default( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                output-folder: %s
            """
            , source
        );

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( Path.of(source).toAbsolutePath(), config.getCommon().getOutputFolder() );
        assertEquals( Path.of(source).toAbsolutePath().resolve("results"), config.getCommon().getResultsFolder() );

    }


    /* Test common.init-nodes */


    @ParameterizedTest
    @ValueSource(strings={"text","12","true"})
    public void the_expected_value_for_initNodes_is_a_list_of_integers( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                init-nodes: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @ParameterizedTest
    @ValueSource(strings={"text","1.0","true"})
    public void initNodes_is_expected_to_be_an_array_of_integers( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                init-nodes: [10,%s,100]
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void empty_values_are_not_allowed_in_initNodes()
    {
        
        final String yaml = 
            """
            common:
                init-nodes: [10,null,100]
            """;

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void valid_initNodes_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                init-nodes: [1,2,3]
            """;

        final List<Integer> expected = Arrays.asList( 1, 2, 3 );
        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( expected, config.getCommon().getInitNodes() );

    }


    /* Test common.functions */

    @ParameterizedTest
    @ValueSource(strings={"text","12","true"})
    public void the_expected_value_for_functions_is_a_list_of_strings( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                hash-functions: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @ParameterizedTest
    @ValueSource(strings={"11","1.0","true"})
    public void functions_is_expected_to_be_an_array_of_strings( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                hash-functions: [XX,%s,YY]
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void empty_values_are_not_allowed_in_functions()
    {
        
        final String yaml = 
            """
            common:
                hash-functions: [XX,null,YY]
            """;

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void valid_functions_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                hash-functions: [XX,YY]
            """;

        final List<String> expected = Arrays.asList( "xx", "yy" );
        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( expected, config.getCommon().getHashFunctions() );

    }

    /* Test common.iterations.warmup */

    @ParameterizedTest
    @ValueSource(strings={"text","true"})
    public void iterations_warmup_is_expected_to_be_a_numeric_value( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                iterations:
                    warmup: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_iterations_warmup_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                iterations:
                    warmup: 101
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( 101, config.getCommon().getIterations().getWarmup() );

    }

    /* Test common.iterations.execution */

    @ParameterizedTest
    @ValueSource(strings={"text","true"})
    public void iterations_execution_is_expected_to_be_a_numeric_value( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                iterations:
                    execution: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_iterations_execution_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                iterations:
                    execution: 101
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( 101, config.getCommon().getIterations().getExecution() );

    }

    /* Test common.time.unit */

    @ParameterizedTest
    @ValueSource(strings={"10","true"})
    public void time_unit_is_expected_to_be_a_string( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                time:
                    unit: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_time_unit_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                time:
                    unit: milli-seconds
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        final TimeUnit expected = TimeUnit.MILLISECONDS;

        assertEquals( expected, config.getCommon().getTime().getUnit() );

    }

    /* Test common.time.mode */

    @ParameterizedTest
    @ValueSource(strings={"10","true"})
    public void time_mode_is_expected_to_be_a_string( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                time:
                    mode: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @ParameterizedTest
    @ValueSource(strings={"sample time","sample-time","sample_time","SAMPLE TIME","SAMPLE-TIME","SAMPLE_TIME","Sample Time","Sample-Time","Sample_Time","Sampletime"})
    public void time_mode_is_expected_to_be_written_in_camel_notaion( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                time:
                    mode: %s
            """
            , source
        );

        assertThrows( UnknownConstantException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_time_mode_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                time:
                    mode: SampleTime
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        final Mode expected = Mode.SampleTime;
        
        assertEquals( expected, config.getCommon().getTime().getMode() );

    }

    /* Test common.time.warmup */

    @ParameterizedTest
    @ValueSource(strings={"text","true"})
    public void time_warmup_is_expected_to_be_a_numeric_value( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                time:
                    warmup: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_time_warmup_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                time:
                    warmup: 101
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        final TimeValue expected = new TimeValue( 101, TimeUnit.SECONDS );

        assertEquals( expected, config.getCommon().getTime().getWarmup() );

    }

    /* Test common.time.execution */

    @ParameterizedTest
    @ValueSource(strings={"text","true"})
    public void time_execution_is_expected_to_be_a_numeric_value( String source )
    {
        
        final String yaml = String.format(
            """
            common:
                time:
                    execution: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_time_execution_value_should_replace_the_default()
    {
        
        final String yaml = 
            """
            common:
                time:
                    execution: 101
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        final TimeValue expected = new TimeValue( 101, TimeUnit.SECONDS );

        assertEquals( expected, config.getCommon().getTime().getExecution() );

    }

    /* Test algorithms */

    @ParameterizedTest
    @ValueSource(strings={"text","10","true"})
    public void algorithms_is_expected_to_be_a_list_of_objects( String source )
    {
        
        final String yaml = String.format(
            """
            algorithms: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @ParameterizedTest
    @ValueSource(strings={" ","\n"})
    public void the_algorithm_name_cannot_be_blank( String source )
    {
        
        final String yaml = String.format(
            """
            algorithms:
            - name: %s
            """
            , source
        );

        assertThrows( MissingValueException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_algorithm_name_should_replace_the_default()
    {
        
        final String yaml =
            """
            algorithms:
            - name: my-name
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( "myname", config.getAlgorithms().get(0).getName() );

    }

    @Test
    public void a_valid_algorithm_args_should_replace_the_default()
    {
        
        final String yaml =
            """
            algorithms:
            - name: my-name
              args:
                my: arg
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        final Map<String,Object> args = config.getAlgorithms().get(0).getArgs();
        assertEquals( 1, args.size() );
        assertEquals( "arg", args.get("my") );

    }

    /* Test benchmarks */

    @ParameterizedTest
    @ValueSource(strings={"text","10","true"})
    public void benchmarks_is_expected_to_be_a_list_of_objects( String source )
    {
        
        final String yaml = String.format(
            """
            benchmarks: %s
            """
            , source
        );

        assertThrows( InvalidTypeException.class, () -> ConfigLoader.of(yaml) );

    }

    @ParameterizedTest
    @ValueSource(strings={" ","\n"})
    public void the_benchmark_name_cannot_be_blank( String source )
    {
        
        final String yaml = String.format(
            """
            benchmarks:
            - name: %s
            """
            , source
        );

        assertThrows( MissingValueException.class, () -> ConfigLoader.of(yaml) );

    }

    @Test
    public void a_valid_benchmark_name_should_replace_the_default()
    {
        
        final String yaml =
            """
            benchmarks:
            - name: my-name
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( 1, config.getBenchmarks().size() );
    
        final BenchmarkConfig benchmark = config.getBenchmarks().get( 0 );
        assertEquals( "myname", benchmark.getName() );

    }

    @Test
    public void a_valid_benchmark_properties_should_replace_the_default()
    {
        
        final String yaml =
            """
            benchmarks:
            - name: my-name
              common:
                gc: false
              args:
                my: arg
            """;

        final Config config = assertDoesNotThrow( () -> ConfigLoader.of(yaml) );
        assertEquals( 1, config.getBenchmarks().size() );

        final BenchmarkConfig benchmark = config.getBenchmarks().get( 0 );
        assertEquals( "myname", benchmark.getName() );
        assertFalse( benchmark.getCommon().isGc() );

        final Map<String,Object> args = benchmark.getArgs();
        assertEquals( 1, args.size() );
        assertEquals( "arg", args.get("my") );
        
    }


}
