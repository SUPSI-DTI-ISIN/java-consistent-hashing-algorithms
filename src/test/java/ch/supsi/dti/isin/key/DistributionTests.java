package ch.supsi.dti.isin.key;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

/**
 * Suite to test the {@link Distribution} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class DistributionTests
{

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void providing_a_null_key_to_method_of_shoud_cause_a_NullPointerException()
    {

        assertThrows( NullPointerException.class, () -> Distribution.of(null) );
        
    }

    @ParameterizedTest
    @ValueSource(strings={""," ","\t","\n","my-invalid-key"})
    public void providing_an_invalid_key_to_method_of_shoud_cause_an_IllegalArgumentException( String invalidKey )
    {

        assertThrows( IllegalArgumentException.class, () -> Distribution.of(invalidKey) );

    }

    @ParameterizedTest
    @ValueSource(strings={"CUSTOM","CuStOm","cUsToM","Custom","custom"})
    public void providing_a_valid_key_to_method_of_shoud_return_as_expected( String validKey )
    {

        final Distribution dist = assertDoesNotThrow( () -> Distribution.of(validKey) );
        assertSame( Distribution.CUSTOM, dist );

    }

}
