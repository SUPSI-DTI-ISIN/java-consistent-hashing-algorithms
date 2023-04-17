package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Path;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;


/**
 * Suite to test class {@link Config}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConfigTests
{

    private static final CommonConfig COMMON = new CommonConfig();


    /* ******************** */
    /*  INTERFACE METHORDS  */
    /* ******************** */


    /**
     * Returns an instance of the class to test.
     *
     * @return instance of the class to test.
     */
    public Config sampleValue( Object source )
    {

        return Config.of( source );

    }


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @ParameterizedTest
    @NullAndEmptySource
    public void merging_an_empty_map_should_not_change_the_defaults( Map<String,Object> source )
    {

        final Config config = sampleValue( source );

        assertEquals( COMMON, config.getCommon() );
        assertNotSame( COMMON, config.getCommon() );
        
        assertNotNull( config.getAlgorithms() );
        assertTrue( config.getAlgorithms().isEmpty() );
        
        assertNotNull( config.getBenchmarks() );
        assertTrue( config.getBenchmarks().isEmpty() );

    }

    @Test
    public void changing_the_properties_should_apply()
    {

        final String outputFolder = "new/output/folder";
        final Map<String,Object> common = Collections.singletonMap( "output-folder", outputFolder );

        final Map<String,Object> algorithm = Collections.singletonMap( "name", "my-algorithm" );
        final List<Map<String,Object>> algorithms = Collections.singletonList( algorithm );
        
        final Map<String,Object> benchmark = Collections.singletonMap( "name", "my-benchmark" );
        final List<Map<String,Object>> benchmarks = Collections.singletonList( benchmark );

        final Map<String,Object> source = new HashMap<>();
        source.put( "common", common );
        source.put( "algorithms", algorithms );
        source.put( "benchmarks", benchmarks );

        final Config config = sampleValue( source );
        assertEquals( Path.of(outputFolder).toAbsolutePath(), config.getCommon().getOutputFolder() );
        assertEquals( Path.of(outputFolder).toAbsolutePath().resolve("results"), config.getCommon().getResultsFolder() );

        assertEquals(1, config.getAlgorithms().size() );
        assertNotNull( config.getAlgorithms().get(0) );
        assertEquals( "myalgorithm", config.getAlgorithms().get(0).getName());

        assertEquals(1, config.getBenchmarks().size() );
        assertNotNull( config.getBenchmarks().get(0) );
        assertEquals("mybenchmark", config.getBenchmarks().get(0).getName() );
        
    }

}
