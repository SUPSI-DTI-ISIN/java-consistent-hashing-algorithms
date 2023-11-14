package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;


/**
 * Suite to test class {@link InconsistentValueException}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class InconsistentValueExceptionTests
{


    private static final Random random = new Random();

    private static final ValuePath PATH = ValuePath.root().append( "my-property" );


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void factory_method_lessThan_should_return_proper_messages()
    {

        final int limit = random.nextInt( 100 );
        final String expectedMessage = "Expected property my-property to be greater or equal to " + limit + " but was -1";
        final InconsistentValueException lessThan = InconsistentValueException.lessThan( PATH, limit, -1 );
        
        assertNotNull( lessThan );
        assertEquals( expectedMessage, lessThan.getMessage() );
        
    }

    @ParameterizedTest
    @ValueSource(ints={0,-1})
    public void factory_method_lessOrEqualToZero_should_return_proper_messages( int value )
    {

        final int limit = random.nextInt( 100 );
        final String expectedMessage = "Expected property my-property to be greater than " + limit + " but was " + value;
        final InconsistentValueException lessThan = InconsistentValueException.lessOrEqual( PATH, limit, value );
        
        assertNotNull( lessThan );
        assertEquals( expectedMessage, lessThan.getMessage() );
        
    }

    @ParameterizedTest
    @ValueSource(floats={-1,100,101})
    public void factory_method_notAPercentage_should_return_proper_messages( float value )
    {

        final String expectedMessage = "Expected property my-property to be in range [0,1) but was " + value;
        final InconsistentValueException lessThanZero = InconsistentValueException.notAPercentage( PATH, value );
        
        assertNotNull( lessThanZero );
        assertEquals( expectedMessage, lessThanZero.getMessage() );
        
    }

    @Test
    public void factory_method_notIn_should_return_proper_messages()
    {

        final List<Object> values = Arrays.asList( "first", "second" );
        final String expectedMessage = "Expected property my-property to be one of [first, second] but was third";
        final InconsistentValueException lessThanZero = InconsistentValueException.notIn( PATH, values, "third" );
        
        assertNotNull( lessThanZero );
        assertEquals( expectedMessage, lessThanZero.getMessage() );
        
    }

}
