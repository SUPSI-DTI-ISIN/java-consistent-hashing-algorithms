package ch.supsi.dti.isin.benchmark.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import ch.supsi.dti.isin.key.Distribution;

/**
 * Suite to test the {@link ConfigUtils} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConfigUtilsTests
{


    private static final ValuePath PATH = ValuePath.root();

    private static final Function<String,Distribution> TO_DISTRIBUTION = Distribution::of;


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    /* Test normalize */

    @ParameterizedTest
    @ValueSource(strings={"k3y-_+=.,;:n4m3","k3y!@#$%^&*n4m3","{[(k3y-n4m3)]}","K3y N4m3","K3Y\t\nN4M3"})
    public void normalize_should_work_as_expected( String source )
    {

        assertEquals( "k3yn4m3", ConfigUtils.normalize(source) );

    }

        
    
    /* Test toString */

    @Test
    public void toString_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toString(PATH,value) );
        
    }

    @Test
    public void toString_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toString(PATH,value) );
        
    }

    @Test
    public void toString_a_string_value_should_work_as_expected()
    {

        assertEquals( "", ConfigUtils.toString(PATH,"") );
        assertEquals( " \t\n", ConfigUtils.toString(PATH," \t\n") );
        assertEquals( "My-TeXt", ConfigUtils.toString(PATH,"My-TeXt") );
        
    }


    /* Test toNormalizedString */

    @Test
    public void toNormalizedString_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toNormalizedString(PATH,value) );
        
    }

    @Test
    public void toNormalizedString_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toNormalizedString(PATH,value) );
        
    }

    @Test
    public void toNormalizedString_a_string_value_should_work_as_expected()
    {

        assertEquals( "", ConfigUtils.toNormalizedString(PATH,"") );
        assertEquals( "", ConfigUtils.toNormalizedString(PATH," \t\n") );
        assertEquals( "mytext", ConfigUtils.toNormalizedString(PATH,"My-TeXt") );
        
    }

    /* Test toInt */

    @Test
    public void toInt_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toInt(PATH,value) );
        
    }

    @Test
    public void toInt_a_non_numeric_value_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toInt(PATH,value) );
        
    }

    @Test
    public void toInt_a_numeric_value_should_work_as_expected()
    {

        assertEquals( 10, ConfigUtils.toInt(PATH,(byte) 10) );
        assertEquals( 10, ConfigUtils.toInt(PATH,(short) 10) );
        assertEquals( 10, ConfigUtils.toInt(PATH,10) );
        assertEquals( 10, ConfigUtils.toInt(PATH,10L) );
        assertEquals( 10, ConfigUtils.toInt(PATH,10f));
        assertEquals( 10, ConfigUtils.toInt(PATH,10d));
        
    }


    /* Test toFloat */

    @Test
    public void toFloat_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toFloat(PATH,value) );
        
    }

    @Test
    public void toFloat_a_non_numeric_value_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toFloat(PATH,value) );
        
    }

    @Test
    public void toFloat_a_numeric_value_should_work_as_expected()
    {

        assertEquals( 10, ConfigUtils.toFloat(PATH,(byte) 10) );
        assertEquals( 10, ConfigUtils.toFloat(PATH,(short) 10) );
        assertEquals( 10, ConfigUtils.toFloat(PATH,10) );
        assertEquals( 10, ConfigUtils.toFloat(PATH,10L) );
        assertEquals( 10, ConfigUtils.toFloat(PATH,10f));
        assertEquals( 10, ConfigUtils.toFloat(PATH,10d));
        
    }


    /* Test toBoolean */

    @Test
    public void toBoolean_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toBoolean(PATH,value) );
        
    }

    @Test
    public void toBoolean_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toBoolean(PATH,value) );
        
    }

    @Test
    public void toBoolean_a_boolean_value_should_work_as_expected()
    {

        assertEquals( true,  ConfigUtils.toBoolean(PATH,Boolean.TRUE) );
        assertEquals( false, ConfigUtils.toBoolean(PATH,Boolean.FALSE) );
        
    }


    /* Test toList */

    @Test
    public void toList_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toList(PATH,Object.class,value) );
        
    }

    @Test
    public void toList_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toList(PATH,Object.class,value) );
        
    }

    @Test
    public void toList_a_list_with_elements_of_different_type_shoud_throw_an_exception()
    {

        final List<String> value = Collections.singletonList( "elem" );
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toList(PATH,Byte.class,value) );
        
    }

    @Test
    public void toList_a_valid_list_should_work_as_expected()
    {

        final List<String> value = Collections.singletonList( "elem" );

        assertEquals( value, ConfigUtils.toList(PATH,String.class,value) );
        
    }
    
    
    /* Test toEnumList */

    @Test
    public void toEnumList_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toEnumList(PATH,Distribution.class,value,TO_DISTRIBUTION) );
        
    }

    @Test
    public void toEnumList_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toEnumList(PATH,Distribution.class,value,TO_DISTRIBUTION) );
        
    }

    @Test
    public void toEnumList_a_list_with_elements_of_different_type_shoud_throw_an_exception()
    {

        final List<Integer> value = Collections.singletonList( 10 );
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toEnumList(PATH,Distribution.class,value,TO_DISTRIBUTION) );
        
    }

    @Test
    public void toEnumList_invalid_constant_names_shoud_throw_an_exception()
    {

        final List<String> value = Arrays.asList( "normal", "unknown", "uniform" );
        assertThrows( UnknownConstantException.class, () -> ConfigUtils.toEnumList(PATH,Distribution.class,value,TO_DISTRIBUTION) );
        
    }

    @Test
    public void toEnumList_a_valid_list_should_work_as_expected()
    {

        final List<String> value = Arrays.asList( "uniform", "normal", "custom" );
        final List<Distribution> expected = Arrays.asList( Distribution.UNIFORM, Distribution.NORMAL, Distribution.CUSTOM );
        
        assertEquals( expected, ConfigUtils.toEnumList(PATH,Distribution.class,value,TO_DISTRIBUTION) );
        
    }


    /* Test toMap */

    @Test
    public void toMap_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows(
            InvalidTypeException.class,
            () -> ConfigUtils.toMap( PATH, Byte.class, Byte.class, value )
        );
        
    }

    @Test
    public void toMap_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows(
            InvalidTypeException.class,
            () -> ConfigUtils.toMap( PATH, Byte.class, Byte.class, value )
        );
        
    }

    @Test
    public void toMap_a_map_with_elements_of_different_type_shoud_throw_an_exception()
    {

        final Map<String,String> value = Collections.singletonMap( "key", "value" );
        assertThrows(
            InvalidTypeException.class,
            () -> ConfigUtils.toMap( PATH, Byte.class, Byte.class, value )
        );
        
    }

    @Test
    public void toMap_a_valid_map_should_work_as_expected()
    {

        final Map<String,String> value = Collections.singletonMap( "key", "value" );
        assertEquals( value, ConfigUtils.toMap(PATH,String.class,String.class,value) );
        
    }


    /* Test toSourceMap */

    @Test
    public void toSourceMap_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toSourceMap_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toSourceMap_a_map_with_elements_of_different_type_shoud_throw_an_exception()
    {

        final Map<Integer,Integer> value = Collections.singletonMap( 10, 10 );
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toSourceMap_a_valid_map_should_work_as_expected()
    {

        final Map<String,Object> value = Collections.singletonMap( "key", new Object() );
        assertEquals( value, ConfigUtils.toSourceMap(PATH,value) );
        
    }


    /* Test toArgs */

    @Test
    public void toArgs_a_null_value_shoud_throw_an_exception()
    {

        final Object value = null;
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toArgs_a_different_object_shoud_throw_an_exception()
    {

        final Object value = new Object();
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toArgs_a_map_with_elements_of_different_type_shoud_throw_an_exception()
    {

        final Map<Integer,Integer> value = Collections.singletonMap( 10, 10 );
        assertThrows( InvalidTypeException.class, () -> ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @Test
    public void toArgs_a_valid_map_should_work_as_expected()
    {

        final Map<String,Object> value = Collections.singletonMap( "key", new Object() );
        assertEquals( value, ConfigUtils.toSourceMap(PATH,value) );
        
    }

    @ParameterizedTest
    @ValueSource(strings={"my-arg","my arg","MY_ARG","MyArg"})
    public void after_applying_toArgs_keys_should_be_normalized( String key )
    {

        final Object value = new Object();
        final String normalizedKey = ConfigUtils.normalize( key );
        final Map<String,Object> source = Collections.singletonMap( key, value );
        
        final Map<String,Object> target = ConfigUtils.toArgs( PATH, source );
        assertNotNull( target );
        assertEquals( 1, target.size() );
        assertFalse( target.containsKey(key) );
        assertTrue( target.containsKey(normalizedKey) );
        assertEquals( value, target.get(normalizedKey) );
        
    }

}
