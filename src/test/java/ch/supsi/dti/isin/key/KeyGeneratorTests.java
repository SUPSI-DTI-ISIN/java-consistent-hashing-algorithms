package ch.supsi.dti.isin.key;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

/**
 * Test suite for the class {@link KeyGenerator}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class KeyGeneratorTests
{

    @Test
    public void if_the_given_distribution_is_null_a_RequirementFailure_should_be_thrown()
    {

        assertThrows( RequirementFailure.class, () -> KeyGenerator.create( null) );

    }

    @Test
    public void if_the_given_distribution_is_valid_the_proper_generator_should_be_returned()
    {

        for( Distribution dist : Distribution.values() )
        {
            
            final KeyGenerator generator = assertDoesNotThrow( () -> KeyGenerator.create(dist) );
            assertNotNull( generator );

        }

    }

}
