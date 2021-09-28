package ch.supsi.dti.isin.hashfunction;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.hashfunction.HashFunction.Algorithm;

/**
 * Test suite for the factory method {@link HashFunction#create(Algorithm)}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HashFunctionTests
{

    
    @Test
    public void if_the_algorithm_is_null_a_RequirementFailure_should_be_thrown()
    {

        assertThrows( RequirementFailure.class, () -> HashFunction.create( null ) );

    }


    @Test
    public void for_each_algorithm_the_right_implementation_should_be_returned()
    {

        final Map<Algorithm,Class<?>> map = new HashMap<>();
        map.put( Algorithm.MURMUR3, Murmur3Hash.class );
        map.put( Algorithm.CRC32, CRC32Hash.class );
        map.put( Algorithm.MD5, MD5Hash.class );
        map.put( Algorithm.XX, XXHash.class );

        map.forEach( (algorithm, expected) ->
        {

            final HashFunction function = HashFunction.create( algorithm );
            assertNotNull( function );
            assertEquals( expected, function.getClass() );

        });
    }

}
