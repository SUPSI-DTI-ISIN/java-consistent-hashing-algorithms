package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


/**
 * Suite to test class {@link BenchmarkConfig}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BenchmarkConfigTests
{


    private static final ValuePath PATH = ValuePath.root().append( "my-benchmark" ).append( 1 );

    private static final CommonConfig COMMON = new CommonConfig();


    /* ******************** */
    /*  INTERFACE METHORDS  */
    /* ******************** */


    /**
     * Returns an instance of the class to test.
     *
     * @return instance of the class to test.
     */
    public BenchmarkConfig sampleValue( Object source )
    {

        return BenchmarkConfig.of( PATH, COMMON, source );

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_not_change_the_defaults( Map<String,Object> source )
    {

        assertThrows( MissingValueException.class,() -> sampleValue(source) );
        
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

        final Map<String,Object> source = Collections.singletonMap( "name", "my-benchmark" );
        final BenchmarkConfig config = sampleValue( source );
        assertEquals( "mybenchmark", config.getName() );
        
        assertNotNull( config.getArgs() );
        assertTrue( config.getArgs().isEmpty() );
        
        assertSame( PATH, config.getPath() );
        assertEquals( COMMON, config.getCommon() );
        assertNotSame( COMMON, config.getCommon() );

    }



    @Test
    public void changing_the_properties_should_apply()
    {

        final String newName = "new-name";
        final String expectedName = "newname";
        final Map<String,Object> newCommon = Collections.singletonMap( "output-folder", "new/output/folder" );
        final Map<String,Object> newArgs   = Collections.singletonMap( "new-arg", true );
        final Map<String,Object> expectedArgs   = Collections.singletonMap( "newarg", true );
        
        final Map<String,Object> source = new HashMap<>();
        source.put( "name", newName );
        source.put( "args", newArgs );
        source.put( "common", newCommon );

        final BenchmarkConfig config = sampleValue( source );
        assertSame( PATH, config.getPath() );
        assertEquals( expectedName, config.getName());
        assertEquals( expectedArgs, config.getArgs() );
        assertEquals( Path.of("new/output/folder").toAbsolutePath(), config.getCommon().getOutputFolder() );
        assertEquals( Path.of("new/output/folder").toAbsolutePath().resolve("results"), config.getCommon().getResultsFolder() );
        
    }

}
