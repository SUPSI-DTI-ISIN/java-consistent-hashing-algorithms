package ch.supsi.dti.isin.benchmark.executor;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

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
   
}
