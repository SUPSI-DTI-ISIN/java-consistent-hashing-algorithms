package ch.supsi.dti.isin.benchmark;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.nerd4j.utils.lang.Is;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryLoader;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.Config;
import ch.supsi.dti.isin.benchmark.config.ConfigLoader;
import ch.supsi.dti.isin.benchmark.executor.BenchmarkExecutionException;
import ch.supsi.dti.isin.benchmark.executor.BenchmarkExecutor;
import ch.supsi.dti.isin.benchmark.executor.BenchmarkExecutorLoader;


/**
 * Main class of the consistent hashing benchmarking tool.
 * 
 * @author Massimo Coluzzi
 * @author Samuel De Babo Martins
 */
public class ConsistentHashBenchmark
{


    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( ConsistentHashBenchmark.class.getName() );


    /* ************* */
    /*  ENTRY POINT  */
    /* ************* */


    /**
     * Entry point to the benchmarks execution.
     * <p>
     * It expects up to 1 argument to be provided.
     * If one argument is provided it should be the
     * path to the configuration file.
     * 
     * @param args command line arguments.
     */
    public static void main( String[] args )
    {
        
        final Config config = loadConfig( args );

        final List<BenchmarkExecutor> executors = loadBenchmarkExecutors( config );
        final List<ConsistentHashFactory> factories = loadConsistentHashFactories( config );

        logger.info( "Starting benchmark execution" );
        for( BenchmarkExecutor executor : executors )
        {

            final String benchmark = executor.getBenchmark().getName();
            try{

                logger.info( "Starting benchmark " + benchmark );
                executor.execute( factories );

            }catch( BenchmarkExecutionException ex )
            {
                logger.severe( "Error while running benchmark " + benchmark );
                logger.severe( ex.getMessage() );
                logger.severe( "Execution of '" + benchmark + "' will be interrupted" );
            }

            logger.info( "Stopping benchmark " + benchmark );

        }

    }


    /**
     * Loads the configuration from the given path if provided.
     * Otherwise, loads the default configuration.
     * 
     * @param args command line arguments to parse
     * @return a new configuration
     */
    private static Config loadConfig( String[] args )
    {

        try{

            if( Is.empty(args) )
            {
                logger.info( "Loading the default configuration file." );
                return ConfigLoader.loadDefault();
            }
            else
            {
                final String path = args[0];
                logger.info( "Loading configuration file from path " + path );
                return ConfigLoader.loadFromFile( path );
            }

        }catch( FileNotFoundException ex )
        {

            logger.log( Level.SEVERE, "Unable to load configuration file", ex );
            System.exit( 1 );

        }catch( Exception ex )
        {

            logger.log( Level.SEVERE, "Error in configuration file", ex );
            System.exit( 2 );

        }

        /* Unreachable code. */
        return null;

    }

    /**
     * Loads the consistent hash factories for every consistent hash algorithm defined in the configuration.
     * 
     * @param config the configuration to parse
     * @return list of consistent hash factories
     */
    private static List<ConsistentHashFactory> loadConsistentHashFactories( Config config )
    {

        logger.info( "Loading consistent hash algorithms..." );
        final List<AlgorithmConfig> algorithms = config.getAlgorithms();
        if( Is.empty(algorithms) )
        {
            logger.info( "No algorithms to be benchmarked found in configuration." );
            logger.info( "Nothing will be done." );
            System.exit( 0 );
        }

        final List<ConsistentHashFactory> factories = new ArrayList<>( algorithms.size() );
        final ConsistentHashFactoryLoader loader = ConsistentHashFactoryLoader.getInstance();
        for( AlgorithmConfig algorithm : algorithms )
            try{

                final ConsistentHashFactory factory = loader.load( algorithm.getName(), algorithm );
                factories.add( factory );

            }catch( Exception ex )
            {

                logger.log( Level.SEVERE, "Failure loading ConsistentHashFactory for algorithm " + algorithm.getName(), ex );
                System.exit( 3 );

            }

        return factories;

    }


    /**
     * Loads the benchmarks to run defined in the configuration.
     * 
     * @param config the configuration to parse
     * @return list of benchmark executors
     */
    private static List<BenchmarkExecutor> loadBenchmarkExecutors( Config config )
    {

        final List<BenchmarkConfig> benchmarks = config.getBenchmarks();
        if( Is.empty(benchmarks) )
        {
            logger.info( "No benchmarks to execute found in configuration." );
            logger.info( "Nothing will be done." );
            System.exit( 0 );
        }

        final BenchmarkExecutorLoader loader = BenchmarkExecutorLoader.getInstance();
        final List<BenchmarkExecutor> executors = new ArrayList<>( benchmarks.size() );
        for( BenchmarkConfig benchmark : benchmarks )
        try{

            final BenchmarkExecutor executor = loader.load( benchmark.getName(), benchmark );
            executors.add( executor );

        }catch( Exception ex )
        {

            logger.log( Level.SEVERE, "Failure loading BenchmarkExecutor with name " + benchmark.getName(), ex );
            System.exit( 4 );

        }

        return executors;

    }


}
