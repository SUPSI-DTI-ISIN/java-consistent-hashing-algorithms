package ch.supsi.dti.isin.benchmark.config;


/**
 * Exception thrown when the value is not of the expected type.
 * 
 * @author Massimo Coluzzi
 */
public class InvalidTypeException extends InvalidConfigException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the exception message
     */
    private InvalidTypeException( String message )
    {

        super( message );
        
    }


    /**
     * Creates a new {@link InvalidTypeException}.
     * 
     * @param path  the path of the property
     * @param value the expected type
     * @param type  the actual value
     * @return a new exception
     */
    public static InvalidTypeException of( ValuePath path, Object value, Class<?> type )
    {

        return new InvalidTypeException(
            new StringBuilder()
                .append( "Expected value of type " )
                .append( type.getSimpleName() )
                .append( " for property " )
                .append( path )
                .append( " but was " )
                .append( value )
                .toString()
        );

    }

}
