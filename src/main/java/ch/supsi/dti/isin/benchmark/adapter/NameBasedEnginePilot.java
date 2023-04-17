package ch.supsi.dti.isin.benchmark.adapter;

import org.nerd4j.utils.lang.Require;

/**
 * Implementation of {@link ConsistentHashEnginePilot} common to all name-based
 * consistent hashing algorithms.
 *
 * 
 * @author Massimo Coluzzi
 */
public abstract class NameBasedEnginePilot implements ConsistentHashEnginePilot<String>
{

    /** Prefix to add to node names. */
    private final String prefix;

    /** Index to append to node names. */
    private int index;


    /**
     * Constructor with parameters.
     *
     * @param prefix prefix to add to node names
     */
    protected NameBasedEnginePilot( String prefix )
    {

        super();

        this.prefix = checkPrefix( prefix );
        this.index = 0;

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */
    

    /**
     * Returns the name of the resource to handle.
     * 
     * @return name of the resource
     */
    protected String getName()
    {

        return prefix + index++;

    }
    
    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Checks if the given prefix is valid and
     * appends the '_' if not present.
     * 
     * @param prefix the prefix to check
     * @return a valid prefix
     */
    private String checkPrefix( String prefix )
    {

        final String p = Require.nonEmpty( prefix, "The name prefix to use is mandatory" );
        return p.endsWith( "_" ) ? p : p + '_';

    }

}