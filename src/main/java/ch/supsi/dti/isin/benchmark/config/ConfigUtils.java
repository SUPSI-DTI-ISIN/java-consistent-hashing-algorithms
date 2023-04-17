package ch.supsi.dti.isin.benchmark.config;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.nerd4j.utils.lang.Is;

/**
 * Utility class to normalize configuration values.
 * 
 * @author Massimo Coluzzi
 */
public class ConfigUtils
{

 
    /**
     * Returns a normalized version of the given string where:
     * - Every character other than letters and numbers is removed
     * - All letters are converted into lowercase.
     * 
     * @param source string to normalize
     * @return normalized text
     */
    public static String normalize( String source )
    {
        
        if( Is.empty(source) )
            return source;

        return source.toLowerCase().replaceAll("[^a-z0-9]", "" );

    }

    /**
     * Converts the given object into a value of type {@link String}
     * if the object is not {@code null} and has a compatible type.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static String toString( ValuePath path, Object source )
    {

        return cast( path, source, String.class );

    }

    /**
     * Converts the given object into a value of type {@link String}
     * if the object is not {@code null} and has a compatible type.
     * It applies the {@link #normalize(String)} method to get the
     * normalized version of the string.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the normalized version of the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static String toNormalizedString( ValuePath path, Object source )
    {

        return normalize(
            cast( path, source, String.class )
        );

    }

    /**
     * Converts the given object into a value of type {@code int}
     * if the object is not {@code null} and has a compatible type.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static int toInt( ValuePath path, Object source )
    {

        return cast( path, source, Number.class ).intValue();

    }

    /**
     * Converts the given object into a value of type {@code float}
     * if the object is not {@code null} and has a compatible type.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static float toFloat( ValuePath path, Object source )
    {

        return cast( path, source, Number.class ).floatValue();

    }

    /**
     * Converts the given object into a value of type {@code boolean}
     * if the object is not {@code null} and has a compatible type.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static boolean toBoolean( ValuePath path, Object source )
    {

        return cast( path, source, Boolean.class ).booleanValue();

    }

    /**
     * Converts the given object into an {@code enum} constant
     * if the object is not {@code null} and has a compatible type.
     * 
     * @param <E>        the enum type to get
     * @param path       the path of the related property
     * @param source     the value to convert
     * @param type       class representing the enum type
     * @param conversion the conversion operation to apply
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static <E extends Enum<E>> E toEnum( ValuePath path, Object source, Class<E> type, Function<String,E> conversion )
    {

        final String name = toString( path, source );
        try{

            return conversion.apply( name );

        }catch( Exception ex )
        {

            throw UnknownConstantException.of( path, name, type );

        }

    }

    /**
     * Converts the given object into a {@link List} of elements
     * of the given type. If the object is {@code null} or is not
     * of a compatible type, an exception is thrown.
     * 
     * @param path  the path of the related property
     * @param type  the type of the elements in the list
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static <T> List<T> toList( ValuePath path, Class<T> type, Object source )
    {

        final List<?> list = cast( path, source, List.class );
        for( int i = 0; i < list.size(); ++i )
            validateType( path.append(i), list.get(i), type, false );

        @SuppressWarnings("unchecked")
        final List<T> result = (List<T>) list;

        return result;

    }

    /**
     * Converts the given object into a {@link List} of enums
     * of the given type. If the object is {@code null} or is not
     * of a compatible type, an exception is thrown.
     * 
     *  @param <E>       the enum type to get
     * @param path       the path of the related property
     * @param source     the value to convert
     * @param type       class representing the enum type
     * @param conversion the conversion operation to apply
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static <E extends Enum<E>> List<E> toEnumList( ValuePath path, Class<E> type, Object source, Function<String,E> conversion )
    {

        final List<?> list = cast( path, source, List.class );
        final List<E> values = new ArrayList<>( list.size() );
        for( int i = 0; i < list.size(); ++i )
            values.add( toEnum(path.append(i),list.get(i),type,conversion) );

        return values;

    }

    /**
     * Converts the given object into a {@link Map} of entries
     * of the given type. If the object is {@code null} or is not
     * of a compatible type, an exception is thrown.
     * 
     * @param path      the path of the related property
     * @param keyType   the type of the keys
     * @param valueType the type of the values
     * @param source    the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static <K,V> Map<K,V> toMap( ValuePath path, Class<K> keyType, Class<V> valueType, Object source )
    {

        final Map<?,?> map = cast( path, source, Map.class );
        for( Map.Entry<?,?> entry : map.entrySet() )
        {
            validateType( path, entry.getKey(),   keyType,   false );
            validateType( path, entry.getValue(), valueType, true  );
        }

        @SuppressWarnings("unchecked")
        final Map<K,V> result = (Map<K,V>) cast( path, source, Map.class );
        return result;

    }

    /**
     * Converts the given object into a source {@link Map}
     * where keys are expected to be of type {@link String}
     * and values can be any type. If the object is {@code null}
     * or is not of a compatible type, an exception is thrown.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static Map<String,Object> toSourceMap( ValuePath path, Object source )
    {

        return toMap( path, String.class, Object.class, source );

    }

    /**
     * Converts the given object into a custom arguments {@link Map}
     * where keys are expected to be of type {@code String}
     * and values can be any type. The keys of an argument map
     * get normalized. If the object is {@code null}
     * or is not of a compatible type, an exception is thrown.
     * 
     * @param path   the path of the related property
     * @param source the value to convert
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    public static Map<String,Object> toArgs( ValuePath path, Object source )
    {

        final Map<String,Object> sourceMap = toMap( path, String.class, Object.class, source );
        final Map<String,Object> targetMap = new HashMap<>( sourceMap.size() );

        sourceMap.forEach( (k, v) -> targetMap.put( normalize(k), v ) );
        return targetMap;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Checks if the given value is not {@code null} and has a compatible type.
     * It it is the case, performs a cast into the required type.
     * Otherwise, it throws a {@link InvalidTypeException}.
     * 
     * @param path  the path of the related property
     * @param value the value to cast
     * @return the converted value
     * @throws InvalidTypeException if the value is of the wrong type
     */
    private static <T> T cast( ValuePath path, Object value, Class<T> type )
    {

        validateType( path, value, type, false );
        return type.cast( value );

    }

    /**
     * Checks if the given value belongs to the given type.
     * <p>
     * If {@code allowNull} is {@code false} and the given value
     * is {@code null}, the method will throw an exception.
     * 
     * @param path      the path of the related property
     * @param value     value to check
     * @param type      type to match
     * @param allowNull tells if {@code null} values are allowed
     * @throws InvalidTypeException if the value is of the wrong type
     */
    private static void validateType( ValuePath path, Object value, Class<?> type, boolean allowNull )
    {

        if( allowNull && value == null )
            return;

        if( ! type.isInstance(value) )
            throw InvalidTypeException.of( path, value, type );

    }

}
