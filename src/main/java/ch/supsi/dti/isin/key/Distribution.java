package ch.supsi.dti.isin.key;


/**
 * Enumerates the distributions to use in the benchmark tool.
 *
 * @author Massimo Coluzzi
 */
public enum Distribution
{


    /** Every value has the same probability to be chosen. */
    UNIFORM,

    /** Probability of values follows a Gaussian curve. */
    NORMAL,

    /** Distribution based on a sample dataset loaded from a file. */
    CUSTOM;


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */

    
    /**
     * Like {@link #valueOf(String)} but works providing 
     * the enum name written with different cases.
     * 
     * @param name name to parse
     * @return the {@link Distribution} with the given name
     * @throws NullPointerException if the given name is null
     * @throws IllegalArgumentException if there are no distributions with the given name
     */
    public static Distribution of( String name )
    {

        if( name == null )
            throw new NullPointerException( "The distribution name to parse cannot be null" );

        return valueOf( name.toUpperCase() );

    }

}
