package ch.supsi.dti.isin.benchmark.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Suite to test the contract imposed by the {@link AbstractConfig} abstract class.
 *
 * @param <C> implementation of the configuration to test
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AbstractConfigTests
{

    private static final ValuePath PATH = ValuePath.root();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    /* Test merge */

    @Test
    public void merge_method_should_invoke_the_extension_hook_for_every_property()
    {

        final Random random = new Random();
        final int size = random.nextInt( 9 ) + 1;

        final Map<String,Object> source = new HashMap<>();
        for( int i = 0; i < size; ++i )
        {

            final String key = "K3Y_" + random.nextInt( 100 );
            final String value = "V4LU3_" + random.nextInt( 100 );
            
            source.put( key, value );

        }

        new MockConfig( source ).merge( PATH, source );

    }

    /* Test required */

    @Test
    public void method_required_should_throw_an_exception_if_the_value_is_null()
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertThrows( MissingValueException.class, () -> config.required( PATH,null) );

    }

    /* Test requireGreaterOrEqualToZero */

    @ParameterizedTest
    @ValueSource(ints={0,1})
    public void method_requireGreaterOrEqualToZero_should_return_the_given_value_if_greater_or_equal_to_zero( int value )
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertEquals( value, config.requireGreaterOrEqualToZero(PATH,value) );

    }

    @Test
    public void method_requireGreaterOrEqualToZero_should_throw_an_exception_if_the_value_is_negative()
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertThrows( InconsistentValueException.class, () -> config.requireGreaterOrEqualToZero( PATH,-1) );

    }

    /* Test requireGreaterThanZero(int) */

    @Test
    public void int_method_requireGreaterThanZero_should_return_the_given_value_if_greater_than_zero()
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertEquals( 1, config.requireGreaterThanZero(PATH,1) );

    }

    @ParameterizedTest
    @ValueSource(ints={0,-1})
    public void int_method_requireGreaterThanZero_should_throw_an_exception_if_the_value_is_less_or_equal_to_zero( int value )
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertThrows( InconsistentValueException.class, () -> config.requireGreaterThanZero( PATH,value) );

    }

    /* Test requireGreaterThanZero(float) */

    @Test
    public void float_method_requireGreaterThanZero_should_return_the_given_value_if_greater_than_zero()
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertEquals( 1, config.requireGreaterThanZero(PATH,1.0f) );

    }

    @ParameterizedTest
    @ValueSource(floats={0,-1})
    public void float_method_requireGreaterThanZero_should_throw_an_exception_if_the_value_is_less_or_equal_to_zero( float value )
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertThrows( InconsistentValueException.class, () -> config.requireGreaterThanZero( PATH,value) );

    }

    /* Test requirePercentage */

    @Test
    public void method_requirePercentage_should_return_the_given_value_if_between_0_and_100()
    {

        final int value = new Random().nextInt(100);
        final AbstractConfig<?> config = new EmptyConfig();
        assertEquals( value, config.requirePercentage(PATH,value) );

    }

    @ParameterizedTest
    @ValueSource(floats={-1,100,101})
    public void method_requirePercentage_should_throw_an_exception_if_the_value_is_not_a_percentage( float value )
    {

        final AbstractConfig<?> config = new EmptyConfig();
        assertThrows( InconsistentValueException.class, () -> config.requirePercentage( PATH,value) );

    }

    
    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    private static class EmptyConfig extends AbstractConfig<EmptyConfig>
    {

        @Override
        void merge( ValuePath path, String key, Object value ) {}

    }

    private static class MockConfig extends AbstractConfig<MockConfig>
    {

        private Iterator<Map.Entry<String,Object>> expected;

        public MockConfig( Map<String,Object> source )
        {

            super();

            this.expected = source.entrySet().iterator();

        }

        @Override
        void merge( ValuePath path, String key, Object value )
        {

            final Map.Entry<String,Object> entry = this.expected.next();
            assertEquals( ConfigUtils.normalize(entry.getKey()), key );
            assertEquals( entry.getValue(), value );

        }

    }

}
