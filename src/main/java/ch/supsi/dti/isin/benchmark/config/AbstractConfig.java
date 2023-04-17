package ch.supsi.dti.isin.benchmark.config;

import java.util.Map;

import org.nerd4j.utils.lang.Is;
import org.nerd4j.utils.lang.Require;


/**
 * Abstract implementation common to all the configuration objects.
 * 
 * @param <C> the actual implementation of the {@code Config} abstract class.
 * 
 * @author Massimo Coluzzi
 */
public abstract class AbstractConfig<C extends AbstractConfig<C>>
{


    /**
     * Default constructor.
     * 
     */
    protected AbstractConfig()
    {

        super();

    }


    /* **************** */
    /*  COMMON METHODS  */
    /* **************** */
    

    /**
     * Merges the given source into the current configuration.
     * If a key matches an existing property and the value is
     * not {@code null} or empty, the related property will be
     * replaced.
     * 
     * @param path   the path of the related property
     * @param source source values to merge
     * @throws RequirementFailure if mandatory values are missing
     * @throws InvalidConfigException if the given value is invalid
     */
    void merge( ValuePath path, Object source )
    {

        if( source == null )
            return;

        final Map<String,Object> sourceMap = ConfigUtils.toSourceMap( path, source );
        if( Is.empty(sourceMap) )
            return;

        Require.nonNull( path, "The property path is mandatory" );
        for( Map.Entry<String,Object> entry : sourceMap.entrySet() )
        {

            final Object value = entry.getValue();
            if( value == null )
                continue;

            final String key = ConfigUtils.normalize( entry.getKey() );
            merge( path.append(key), key, value );

        }

    }
    
    /**
     * Returns the given object if it is not {@code null}.
     * Otherwise, it throws an exception.
     * 
     * @param path  path of the property to check
     * @param value value of the property
     * @return the given value if not {@code null}
     * @throws MissingValueException if the value is {@code null}
     */
    Object required( ValuePath path, Object value )
    {

        if( value == null )
            throw MissingValueException.of( path );

        return value;

    }

    /**
     * Returns the given value if it is greater or equal to zero.
     * Otherwise, it throws an exception.
     * 
     * @param path  path of the property to check
     * @param value value of the property
     * @return the given value if greater or equal to zero
     * @throws InconsistentValueException if the value is inconsistent
     */
    int requireGreaterOrEqualToZero( ValuePath path, int value )
    {

        if( value < 0 )
            throw InconsistentValueException.lessThan( path, 0, value );

        return value;

    }

    /**
     * Returns the given value if it is greater than zero.
     * Otherwise, it throws an exception.
     * 
     * @param path  path of the property to check
     * @param value value of the property
     * @return the given value if greater than zero
     * @throws InconsistentValueException if the value is inconsistent
     */
    int requireGreaterThanZero( ValuePath path, int value )
    {

        if( value <= 0 )
            throw InconsistentValueException.lessOrEqual( path, 0, value );

        return value;

    }

    /**
     * Returns the given value if it is greater than zero.
     * Otherwise, it throws an exception.
     * 
     * @param path  path of the property to check
     * @param value value of the property
     * @return the given value if greater than zero
     * @throws InconsistentValueException if the value is inconsistent
     */
    float requireGreaterThanZero( ValuePath path, float value )
    {

        if( value <= 0 )
            throw InconsistentValueException.lessOrEqual( path, 0, value );

        return value;

    }

    /**
     * Returns the given value if it is a percentage less than {@code 100%}.
     * Otherwise, it throws an exception.
     * 
     * @param path  path of the property to check
     * @param value value of the property
     * @return the given value if it is a percentage less than {@code 100%}.
     * @throws InconsistentValueException if the value is inconsistent
     */
    float requirePercentage( ValuePath path, float value )
    {

        if( value < 0 || value >= 100 )
            throw InconsistentValueException.notAPercentage( path, value );

        return value;

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */

    
    /**
     * Performs the merge of the given configuration property.
     * <p>
     * If the given key matches any key in the configuration
     * and the value is not {@code null} or empty, the related
     * property will be replaced. 
     * 
     * @param path  the path of the related property
     * @param key   the key to match
     * @param value the value to replace
     * @throws InvalidTypeException if the value is of the wrong type
     * @throws MissingValueException if a mandatory value is missing
     */
    abstract void merge( ValuePath path, String key, Object value );

}
