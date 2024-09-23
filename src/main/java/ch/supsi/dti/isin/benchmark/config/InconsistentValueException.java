package ch.supsi.dti.isin.benchmark.config;

import java.util.List;

/**
 * Exception thrown when the given value is not in the expected range.
 * 
 * @author Massimo Coluzzi
 */
public class InconsistentValueException extends InvalidConfigException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the exception message
     */
    private InconsistentValueException( String message )
    {

        super( message );
        
    }


    /**
     * Creates a new {@link InconsistentValueException} for the case
     * when the value is expected to be greater or equal to a given number.
     * 
     * @param path  the path of the property
     * @param limit the value to overcome
     * @param value the inconsistent value
     * @return a new exception
     */
    public static InconsistentValueException lessThan( ValuePath path, int limit, int value )
    {

        return new InconsistentValueException(
            new StringBuilder()
                .append( "Expected property " )
                .append( path )
                .append( " to be greater or equal to " )
                .append( limit )
                .append( " but was " )
                .append( value )
                .toString()
        );

    }

    /**
     * Creates a new {@link InconsistentValueException} for the case
     * when the value is expected to be greater than a given number.
     * 
     * @param path  the path of the property
     * @param limit the value to overcome
     * @param value the inconsistent value
     * @return a new exception
     */
    public static InconsistentValueException lessOrEqual( ValuePath path, Number limit, Number value )
    {

        return new InconsistentValueException(
            new StringBuilder()
                .append( "Expected property " )
                .append( path )
                .append( " to be greater than " )
                .append( limit )
                .append( " but was " )
                .append( value )
                .toString()
        );

    }

    /**
     * Creates a new {@link InconsistentValueException} for the case
     * when the value is expected to be a percentage less than {@code 100%}.
     * 
     * @param path  the path of the property
     * @param value the inconsistent value
     * @return a new exception
     */
    public static InconsistentValueException notAPercentage( ValuePath path, float value )
    {

        return new InconsistentValueException(
            new StringBuilder()
                .append( "Expected property " )
                .append( path )
                .append( " to be in range [0,1) but was " )
                .append( value )
                .toString()
        );

    }

    /**
     * Creates a new {@link InconsistentValueException} for the case
     * when the value is expected to be within a list of constants.
     * 
     * @param path   the path of the property
     * @param values the possible values
     * @param value  the inconsistent value
     * @return a new exception
     */
    public static InconsistentValueException notIn( ValuePath path, List<Object> values, Object value )
    {

        return new InconsistentValueException(
            new StringBuilder()
                .append( "Expected property " )
                .append( path )
                .append( " to be one of " )
                .append( values )
                .append( " but was " )
                .append( value )
                .toString()
        );

    }

}
