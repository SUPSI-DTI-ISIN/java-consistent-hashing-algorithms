package ch.supsi.dti.isin.benchmark.adapter;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.config.ConfigUtils;

/**
 * Singleton class that manages {@link ConsistentHashFactory} implementations loading.
 *
 * <p>
 * This class searches for all classes implementing the {@link ConsistentHashFactory}
 * abstract class and stores them by name.
 * 
 * An instance of every {@link ConsistentHashFactory} implementation can be created
 * invoking the {@link #load(String, Object...)} method.
 */
public class ConsistentHashFactoryLoader extends ResourceLoader<ConsistentHashFactory>
{


    /** Singleton instance of the current class. */
    private static final ConsistentHashFactoryLoader INSTANCE = new ConsistentHashFactoryLoader();
    

    /**
     * Default constructor
     * 
     */
    private ConsistentHashFactoryLoader()
    {

        super( 
            "consistent hash engine pilot",
            ConsistentHashFactory.class,
            "ch.supsi.dti.isin.benchmark.adapter.consistenthash"
        );
        
    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Returns the instance of the singleton class.
     *
     * @return the instance of the singleton class
     */
    public static ConsistentHashFactoryLoader getInstance()
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
            .replace( "consistent", "" )
            .replace( "factory", "" )
            .replace( "hash", "" );
            
    }

}
