package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;


/**
 * Suite to test class {@link AlgorithmConfig}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class AlgorithmConfigTests
{


    private static final ValuePath PATH = ValuePath.root().append( "my-algorithm" ).append( 1 );


    /* ******************** */
    /*  INTERFACE METHORDS  */
    /* ******************** */


    /**
     * Returns an instance of the class to test.
     *
     * @return instance of the class to test.
     */
    public AlgorithmConfig sampleValue( Object source )
    {

        return AlgorithmConfig.of( PATH, source );

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_cause_an_exception_to_be_thrown( Map<String,Object> source )
    {

        assertThrows( MissingValueException.class, () -> sampleValue(source) );
        
    }

    @ParameterizedTest
    @NullAndEmptySource
    public void missing_the_name_should_cause_an_exception_to_be_thrown( String name )
    {
       
        final Map<String,Object> source = new HashMap<>();
        source.put( "name", name );
        
        assertThrows( MissingValueException.class, () -> sampleValue(source) );
        
    }

    @Test
    public void check_default_values()
    {

        final Map<String,Object> source = Collections.singletonMap( "name", "my-algorithm" );
        final AlgorithmConfig config = sampleValue( source );
        assertEquals( "myalgorithm", config.getName());
        
        assertNotNull( config.getArgs() );
        assertTrue( config.getArgs().isEmpty() );

        assertSame( PATH, config.getPath() );

    }

    @Test
    public void changing_the_properties_should_apply()
    {

        final String newName = "new-name";
        final String expectedName = "newname";
        final Map<String,Object> newArgs = Collections.singletonMap( "newarg", true );
        
        final Map<String,Object> source = new HashMap<>();
        source.put( "name", newName );
        source.put( "args", newArgs );

        final AlgorithmConfig config = sampleValue( source );
        assertSame( PATH, config.getPath() );
        assertEquals( expectedName, config.getName());
        assertEquals( newArgs, config.getArgs() );
        
    }

    @ParameterizedTest
    @ValueSource(strings={"my-arg","my arg","MY_ARG","MyArg"})
    public void argument_keys_should_be_normalized( String argKey )
    {

        final String newName = "new-name";
        final String expectedName = "newname";
        final Map<String,Object> newArgs = Collections.singletonMap( argKey, true );
        final Map<String,Object> expectedArgs = Collections.singletonMap( ConfigUtils.normalize(argKey), true );
        
        final Map<String,Object> source = new HashMap<>();
        source.put( "name", newName );
        source.put( "args", newArgs );

        final AlgorithmConfig config = sampleValue( source );
        assertSame( PATH, config.getPath() );
        assertEquals( expectedName, config.getName());
        assertEquals( expectedArgs, config.getArgs() );
        
    }

}
