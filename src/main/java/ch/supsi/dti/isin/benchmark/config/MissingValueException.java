package ch.supsi.dti.isin.benchmark.config;


/**
 * Exception thrown when a mandatory value is missing.
 * 
 * @author Massimo Coluzzi
 */
public class MissingValueException extends InvalidConfigException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the exception message
     */
    private MissingValueException( String message )
    {

        super( message );
        
    }


    /**
     * Creates a new {@link MissingValueException}.
     * 
     * @param path the name of the missing property
     * @return a new exception
     */
    public static MissingValueException of( ValuePath path )
    {

        return new MissingValueException(
            new StringBuilder()
                .append( "The mandatory property " )
                .append( path )
                .append( " is missing" )
                .toString()
        );

    }

}
