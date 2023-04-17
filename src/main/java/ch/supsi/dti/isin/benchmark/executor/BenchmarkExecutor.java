package ch.supsi.dti.isin.benchmark.executor;

import java.util.List;
import java.util.logging.Logger;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;


/**
 * Abstract class common to all the benchmark runners.
 * 
 * @author Massimo Coluzzi
 * @author Samuel De Babo Martins
 */
public abstract class BenchmarkExecutor
{


    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( BenchmarkExecutor.class.getName() );
    

    /** The configuration to use to setup the current benchmark. */
    protected final BenchmarkConfig config;
    

    /**
     * Constructor with parameters.
     *
     * @param config  configuration to use to setup the current benchmark
     */
    public BenchmarkExecutor( BenchmarkConfig config )
    {

        super();

        this.config = Require.nonNull( config, "The benchmark's configuration is mandatory" );
        
    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Executes the current benchmark on the given algorithms.
     * 
     * @param factories factories for the arlgorithms to benchmark
     */
    public void execute( List<ConsistentHashFactory> factories )
    {

        try{

            final String benchmark = getBenchmark().getName();
            logger.info( "Starting " + benchmark + " benchmarks" );

            performBenchmak( factories );
            logger.info( benchmark + " benchmarks completed" );

        }catch( BenchmarkExecutionException ex )
        {

            throw ex;

        }catch( Exception ex )
        {

            throw BenchmarkExecutionException.of( ex );

        }

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * Performs the current benchmark on the given algorithms.
     * 
     * @param factories factories for the arlgorithms to benchmark
     * @throws Exception if an error occurs
     */
    protected abstract void performBenchmak( List<ConsistentHashFactory> factories ) throws Exception;


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * The current benchmark configuration.
     * 
     * @return the current benchmark configuration
     */
    public BenchmarkConfig getBenchmark()
    {

        return config;
        
    }

}