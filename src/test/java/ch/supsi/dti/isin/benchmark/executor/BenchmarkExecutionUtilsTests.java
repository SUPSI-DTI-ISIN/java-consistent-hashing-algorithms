package ch.supsi.dti.isin.benchmark.executor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryLoader;
import ch.supsi.dti.isin.benchmark.adapter.HashFunctionLoader;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.jump.JumpFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.CommonConfig;
import ch.supsi.dti.isin.benchmark.config.Config;
import ch.supsi.dti.isin.benchmark.config.ConfigLoader;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.benchmark.config.InvalidConfigException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Suite to test class {@link BenchmarkExecutionUtils}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BenchmarkExecutionUtilsTests
{

    /** Path of the temp folder.  */
    private static final Path TMP = Path.of( System.getProperty("java.io.tmpdir") ).toAbsolutePath();


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */

    
    @Test
    public void getOutputFile_should_create_the_parent_folders()
    {
        
        final String folder = UUID.randomUUID().toString();
        final String file   = ConfigUtils.normalize( UUID.randomUUID().toString() );

        final Path folderPath = TMP.resolve( folder );
        final Path filePath   = folderPath.resolve( "results" ).resolve( file + ".csv" );

        assertFalse( Files.exists(folderPath) );
        assertFalse( Files.exists(filePath) );

        final CommonConfig common = CommonConfig.of( ValuePath.root(), Map.of("output-folder",folderPath.toString()) );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root().append(file), common, Map.of("name", file) );
        
        final Path path = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getOutputFile( benchmark )
        );

        assertNotNull( path );
        assertEquals( filePath, path );

        assertTrue( Files.exists(folderPath) );
        assertFalse( Files.exists(filePath) );

    }

    @Test
    public void getHashFunctions_should_return_the_expected_functions()
    {
        
        final List<HashFunction> expected = HashFunctionLoader.getInstance().load( CommonConfig.DEFAULT_FUNCTIONS );

        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of(
            ValuePath.root().append("test"), common, Map.of("name","test")
        );
        
        final List<HashFunction> functions = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getHashFunctions( benchmark )
        );

        assertNotNull( functions );
        assertEquals( expected.size(), functions.size() );
        for( int i=0; i<expected.size(); ++i )
            assertEquals( expected.get(i).getClass(), functions.get(i).getClass() );

    }

    @Test
    public void getHashFunctionNames_should_return_the_expected_names()
    {
        
        final String[] expected = CommonConfig.DEFAULT_FUNCTIONS.toArray( String[]::new );
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of(
            ValuePath.root().append("test"), common, Map.of("name","test")
        );
        
        final String[] functions = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getHashFunctionNames( benchmark )
        );

        assertArrayEquals( expected, functions );

    }

    @Test
    public void getInitNodes_should_return_the_expected_names()
    {
        
        final String[] expected = CommonConfig.DEFAULT_INIT_NODES
            .stream().map( String::valueOf ).toArray( String[]::new );

        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root().append("test"), common, Map.of("name","test") );
        
        final String[] initNodes = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getInitNodes( benchmark )
        );

        assertArrayEquals( expected, initNodes );

    }

    @Test
    public void getAlgorithms_should_return_the_expected_names()
    {
        
        final String[] expected = { "jump", "maglev", "multiprobe", "rendezvous", "ring" };
        final List<AlgorithmConfig> configs = Arrays.stream(expected)
            .map( name -> AlgorithmConfig.of(ValuePath.root(), Map.of("name",name)) )
            .toList();
        final List<ConsistentHashFactory> factories = configs.stream()
            .map( config -> ConsistentHashFactoryLoader.getInstance().load(config.getName(),config) )
            .toList();
        final String[] algorithms = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getAlgorithms( factories )
        );

        assertArrayEquals( expected, algorithms );

    }

    @Test
    public void getKeyDistributions_should_return_the_expected_names()
    {
        
        final String[] expected = { "UNIFORM","NORMAL","CUSTOM" };
        final CommonConfig common = CommonConfig.of( ValuePath.root(), Map.of("key-distributions",Arrays.asList(expected)) );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, Map.of("name","test") );
        final String[] distributions = BenchmarkExecutionUtils.getKeyDistributions( benchmark );

        assertArrayEquals( expected, distributions );

    }

    @Test
    public void getAlgorithmConfig_should_return_the_expected_config() throws IOException
    {
     
        final Config config = ConfigLoader.loadFromFile( "cp:/configs/complete.yaml" );
        final AlgorithmConfig expected = AlgorithmConfig.of( ValuePath.root(), Map.of("name","jump") );
        
        final AlgorithmConfig algorithmConfig = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getAlgorithmConfig( config, "jump" )
        );

        assertEquals( expected, algorithmConfig );

    }

    @Test
    public void getFactory_should_return_the_expected_ConsistentHashFactory() throws IOException
    {
     
        final AlgorithmConfig config = AlgorithmConfig.of( ValuePath.root(), Map.of("name","jump") );
        
        final ConsistentHashFactory factory = assertDoesNotThrow(
            () -> BenchmarkExecutionUtils.getFactory( config )
        );

        assertNotNull( factory );
        assertEquals( JumpFactory.class, factory.getClass() );

    }


    @Test
    public void if_not_configured_getIncrementalRates_should_return_the_default_values()
    {
        
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, Map.of("name","test") );

        final String[] expected = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9" };
        final String[] rates = BenchmarkExecutionUtils.getIncrementalRates( benchmark );

        assertArrayEquals( expected, rates );

    }

    @Test
    public void if_the_provided_conf_is_empty_getIncrementalRates_should_return_the_default_values()
    {
        
        final Map<String,Object> conf = Map.of(
            "name", "test",
            "args", Map.of( "removal-rates", List.of() )
        );
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, conf );

        final String[] expected = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9" };
        final String[] rates = BenchmarkExecutionUtils.getIncrementalRates( benchmark );

        assertArrayEquals( expected, rates );

    }

    @ParameterizedTest
    @NullAndEmptySource
    @ValueSource(strings="0.9")
    public void getIncrementalRates_should_fail_if_values_are_not_numbers( String badValue )
    {

        final Map<String,Object> conf = Map.of(
            "name", "test",
            "args", Map.of( "removal-rates", Stream.of(0,badValue,0.9).collect(Collectors.toList()) )
        );
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, conf );

        assertThrows(
            InvalidConfigException.class,
            () -> BenchmarkExecutionUtils.getIncrementalRates( benchmark )
        );

    }

    @ParameterizedTest
    @ValueSource(floats={-0.5f,1.0f,1.2f})
    public void getIncrementalRates_should_fail_if_values_are_out_of_range( float badValue )
    {

        final Map<String,Object> conf = Map.of(
            "name", "test",
            "args", Map.of( "removal-rates", List.of(0,badValue,0.9) )
        );
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, conf );

        assertThrows(
            InvalidConfigException.class,
            () -> BenchmarkExecutionUtils.getIncrementalRates( benchmark )
        );

    }

    @Test
    public void getIncrementalRates_should_return_as_expected_if_properly_configured()
    {

        final Random random = new Random();

        final float v1 = random.nextFloat();
        final float v2 = random.nextFloat();
        final float v3 = random.nextFloat();

        final List<Float> rates = List.of( v1, v2, v3 );
        final String[] expected = { String.valueOf(v1), String.valueOf(v2), String.valueOf(v3) };

        final Map<String,Object> conf = Map.of(
            "name", "test",
            "args", Map.of( "removal-rates", rates )
        );
        final CommonConfig common = CommonConfig.of( ValuePath.root(), null );
        final BenchmarkConfig benchmark = BenchmarkConfig.of( ValuePath.root(), common, conf );

        assertArrayEquals( expected, BenchmarkExecutionUtils.getIncrementalRates(benchmark) );

    }
   
}
