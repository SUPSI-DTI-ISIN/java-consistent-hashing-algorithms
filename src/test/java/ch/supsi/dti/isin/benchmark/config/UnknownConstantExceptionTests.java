package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.key.Distribution;


/**
 * Suite to test class {@link UnknownConstantException}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class UnknownConstantExceptionTests
{


    private static final ValuePath PATH = ValuePath.root().append( "my-property" );


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void factory_method_of_should_return_proper_message_for_enum()
    {

        final String constants = Arrays.toString( Distribution.values() );
        final String expectedMessage = "Unknown value unknown for property my-property allowed values are: " + constants;
        final UnknownConstantException ex = UnknownConstantException.of( PATH, "unknown", Distribution.class );
        
        assertNotNull( ex );
        assertEquals( expectedMessage, ex.getMessage() );
        
    }

    @Test
    public void factory_method_of_should_return_proper_message_for_list()
    {

        final List<?> values = Stream.of( 1, '1', "1" ).toList();
        final String expectedMessage = "Unknown value unknown for property my-property allowed values are: " + values;
        final UnknownConstantException ex = UnknownConstantException.of( PATH, "unknown", values );
        
        assertNotNull( ex );
        assertEquals( expectedMessage, ex.getMessage() );
        
    }

}
