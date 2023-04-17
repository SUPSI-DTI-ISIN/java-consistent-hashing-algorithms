package ch.supsi.dti.isin.benchmark.config;

/**
 * Exception thrown when the configuration is invalid.
 * 
 * <p>
 * A configuration can be invalid because a mandatory value is missing
 * or a given value is not of the expected type.
 * 
 * @author Massimo Coluzzi
 */
public class InvalidConfigException extends RuntimeException
{
    
    /**
     * Constructor with parameters.
     * 
     * @param message the message of the exception
     */
    public InvalidConfigException( String message )
    {

        super( message );

    }

}
