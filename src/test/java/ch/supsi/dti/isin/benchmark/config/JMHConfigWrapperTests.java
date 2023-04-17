package ch.supsi.dti.isin.benchmark.config;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Collections;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;


/**
 * Suite to test class {@link JMHConfigWrapper}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class JMHConfigWrapperTests
{


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void an_unloaded_JMHConfigWrapper_should_be_empty()
    {

        final JMHConfigWrapper wrapper = new JMHConfigWrapper();
        assertNull( wrapper.getConfig() );

    }

    @Test
    public void loading_an_empty_file_should_throw_an_Exception() throws IOException
    {

        Files.deleteIfExists( Config.STORE_PATH );
        final JMHConfigWrapper wrapper = new JMHConfigWrapper();

        assertThrows( IOException.class, () -> wrapper.load() );
        
    }

    @Test
    public void loading_an_invalid_file_should_throw_an_Exception() throws IOException
    {

        Files.createDirectories( CommonConfig.DEFAULT_OUTPUT_FOLDER );
        Files.writeString( Config.STORE_PATH, "some:\n\tinvalid:\nyaml" );

        final JMHConfigWrapper wrapper = new JMHConfigWrapper();
        assertThrows( Exception.class, () -> wrapper.load() );
        
    }

    @Test
    public void loading_a_valid_file_should_work_properly() throws IOException
    {

        Files.createDirectories( CommonConfig.DEFAULT_OUTPUT_FOLDER );
        Files.writeString( Config.STORE_PATH, "" );

        final JMHConfigWrapper wrapper = new JMHConfigWrapper();
        assertDoesNotThrow( () -> wrapper.load() );

        final Config expected = Config.of( Collections.emptyMap() );
        assertEquals( expected, wrapper.getConfig() );
        
    }

}
