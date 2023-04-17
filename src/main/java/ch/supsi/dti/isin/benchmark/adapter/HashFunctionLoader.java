package ch.supsi.dti.isin.benchmark.adapter;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Singleton class that manages {@link HashFunction} implementations loading.
 *
 * <p>
 * This class searches for all classes implementing the {@link HashFunction} interface
 * and stores them by name.
 * 
 * An instance of every {@link HashFunction} implementation can be created
 * invoking the {@link #load(String, Object...)} method.
 */
public class HashFunctionLoader extends ResourceLoader<HashFunction> 
{


    /** Singleton instance of the current class. */
    private static final HashFunctionLoader INSTANCE = new HashFunctionLoader();


    /**
     * Default constructor
     * 
     */
    private HashFunctionLoader()
    {

        super( "hash function", HashFunction.class, "ch.supsi.dti.isin.hashfunction" );

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the singleton instance of the class.
     *
     * @return the singleton instance of the class
     */
    public static HashFunctionLoader getInstance()
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
        return ConfigUtils.normalize( className ).replace( "hash", "" );
        
    }

}
