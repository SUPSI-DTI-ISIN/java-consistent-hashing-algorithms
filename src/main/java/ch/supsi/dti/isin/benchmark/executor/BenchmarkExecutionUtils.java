package ch.supsi.dti.isin.benchmark.executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.logging.Logger;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryLoader;
import ch.supsi.dti.isin.benchmark.adapter.HashFunctionLoader;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.Config;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Utility class to perform common benchmark operations.
 * 
 * @author Massimo Coluzzi
 */
public class BenchmarkExecutionUtils
{

    
    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( BenchmarkExecutionUtils.class.getName() );


    /**
     * Returns the path of the file where to store the output of the benchmark.
     * <p>
     * This method ensures that the parent folders exist. Otherwise, it creates them.
     * 
     * @param config the benchmark configuration to use
     * @throws IOException if parent folders cannot be created
     */
    public static Path getOutputFile( BenchmarkConfig config ) throws IOException
    {

        final Path path = config.getCommon().getResultsFolder();
        if( ! Files.exists(path) )
        {
            Files.createDirectories( path );
            logger.info( "Folders for path '" + path + "' have been created." );
        }
        
        return path.resolve( config.getName() + ".csv" );

    }

    /**
     * Returns the list of hash functions defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of hash functions to use
     */
    public static List<HashFunction> getHashFunctions( BenchmarkConfig config )
    {

        return HashFunctionLoader.getInstance().load( config.getCommon().getHashFunctions() );

    }

    /**
     * Returns the list of hash function names defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of hash function names
     */
    public static String[] getHashFunctionNames( BenchmarkConfig config )
    {

        return config.getCommon().getHashFunctions().toArray( String[]::new );

    }

    /**
     * Returns the list of initial cluster nodes defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of initial cluster nodes
     */
    public static String[] getInitNodes( BenchmarkConfig config )
    {

        return config.getCommon().getInitNodes().stream().map( String::valueOf ).toArray( String[]::new );

    }

    /**
     * Returns the list of consistent hashing algorithms defined in the given configuration.
     * 
     * @param factories factories for the arlgorithms to benchmark
     * @return the list of consistent hashing algorithms
     */
    public static String[] getAlgorithms( List<ConsistentHashFactory> factories )
    {

        return factories.stream().map( f -> f.getConfig().getName() ).toArray( String[]::new );

    }

    /**
     * Returns the list of key distributions defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of key distributions
     */
    public static String[] getKeyDistributions( BenchmarkConfig config )
    {

        return config.getCommon().getKeyDistributions().stream().map( String::valueOf ).toArray( String[]::new );

    }

    /**
     * Extracts from the given configuration the algorithm configuration for the given algorithm.
     * 
     * @param config the configuration to parse
     * @param algorithm the algorithm for which to extract the configuration
     * @return the configuration for the given algorithm
     */
    public static AlgorithmConfig getAlgorithmConfig( Config config, String algorithm )
    {

        return config.getAlgorithms().stream().filter( a -> a.getName().equals(algorithm) ).findFirst().get();

    }

    /**
     * Returns the consistent hash factory related to the given algorithm config.
     * 
     * @param config the configuration to parse
     * @return the consistent hash factory
     */
    public static ConsistentHashFactory getFactory( AlgorithmConfig config )
    {

        return ConsistentHashFactoryLoader.getInstance().load( config.getName(), config );

    }

}
