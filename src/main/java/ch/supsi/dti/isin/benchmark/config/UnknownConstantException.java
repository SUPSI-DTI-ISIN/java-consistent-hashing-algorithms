package ch.supsi.dti.isin.benchmark.config;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Exception thrown when the value of a property of type enum
 * does not match any existing constant.
 * 
 * @author Massimo Coluzzi
 */
public class UnknownConstantException extends InvalidConfigException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the exception message
     */
    private UnknownConstantException( String message )
    {

        super( message );
        
    }


    /**
     * Creates a new {@link UnknownConstantException}.
     * 
     * @param path the name of the missing property
     * @param value the provided value
     * @param type  the enum type where to search for matching values
     * @return the new exception
     */
    public static UnknownConstantException of( ValuePath path, String value, Class<? extends Enum<?>> type )
    {

        final List<?> constants = Arrays.stream( type.getEnumConstants() ).toList();
        return of( path, value, constants );

    }

    /**
     * Creates a new {@link UnknownConstantException}.
     * 
     * @param path the name of the missing property
     * @param value the provided value
     * @param allowedValues collection of allowed values
     * @return the new exception
     */
    public static UnknownConstantException of( ValuePath path, String value, Collection<?> allowedValues)
    {

        return new UnknownConstantException(
            new StringBuilder()
                .append( "Unknown value " )
                .append( value )
                .append( " for property ")
                .append( path )
                .append( " allowed values are: " )
                .append( allowedValues )
                .toString()
        );

    }

}
