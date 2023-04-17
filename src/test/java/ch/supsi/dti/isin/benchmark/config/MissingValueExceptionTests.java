package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


/**
 * Suite to test class {@link MissingValueException}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MissingValueExceptionTests
{


    private static final ValuePath PATH = ValuePath.root().append( "my-property" );


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void factory_method_of_should_return_proper_messages()
    {

        final String expectedMessage = "The mandatory property my-property is missing";
        final MissingValueException ex = MissingValueException.of( PATH );
        
        assertNotNull( ex );
        assertEquals( expectedMessage, ex.getMessage() );
        
    }

}
