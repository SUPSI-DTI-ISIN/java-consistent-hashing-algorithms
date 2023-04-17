package ch.supsi.dti.isin.benchmark.executor;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ResourceLoader;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;

/**
 * Singleton class that manages the loading of {@link BenchmarkExecutor} implementations.
 *
 * <p>
 * This class searches for all classes implementing the {@link BenchmarkExecutor}
 * abstract class and stores them by name.
 * 
 * An instance of every {@link BenchmarkExecutor} implementation can be created
 * invoking the {@link #load(String,Object...)} method.
 */
public class BenchmarkExecutorLoader extends ResourceLoader<BenchmarkExecutor>
{


    /** Singleton instance of the current class. */
    private static final BenchmarkExecutorLoader INSTANCE = new BenchmarkExecutorLoader();
    

    /**
     * Default constructor
     * 
     */
    private BenchmarkExecutorLoader()
    {

        super( 
            "benchmark executor",
            BenchmarkExecutor.class,
            "ch.supsi.dti.isin.benchmark.executor"
        );
        
    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Returns the instance of the singleton class.
     *
     * @return the instance of the singleton class.
     */
    public static BenchmarkExecutorLoader getInstance()
    {
        
        return INSTANCE;

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    protected String getKey( Class<?> resourceType )
    {

        final String className = Require.nonNull( resourceType ).getSimpleName();
        return ConfigUtils.normalize( className )
            .replace( "benchmarker", "" )
            .replace( "benchmark", "" );
            
    }

}
