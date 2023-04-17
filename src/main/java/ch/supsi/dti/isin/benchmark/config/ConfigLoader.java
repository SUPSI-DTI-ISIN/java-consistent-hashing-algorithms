package ch.supsi.dti.isin.benchmark.config;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.logging.Logger;

import org.nerd4j.utils.lang.Require;
import org.yaml.snakeyaml.Yaml;

import ch.supsi.dti.isin.benchmark.ConsistentHashBenchmark;


/**
 * Loads a configuration file in {@code YAML} format
 * and creates a {@Link Config} object from it.
 * 
 * @author Massimo Coluzzi
 * @author Samuel De Babo Martins
 */
public class ConfigLoader
{


    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( ConsistentHashBenchmark.class.getName() );
    
    /** The path to the default configuration file. */
    private static final String DEFAULT_CONFIG_FILE = "cp:/configs/default.yaml";


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */
    

    /**
     * Loads the configuration from the default file.
     * 
     * @return the default configuration
     * @throws IOException if reading the source fails
     */
    public static Config loadDefault() throws IOException
    {

        logger.info( "Loading config from default location " + DEFAULT_CONFIG_FILE );
        return loadFromFile( DEFAULT_CONFIG_FILE );

    }

    /**
     * Loads the configuration from the given file path.
     *
     * <p>If the path is preceded by "cp:" or "classpath:" the file will be loaded from the classpath.
     * Otherwise, it will be loaded from the local filesystem.
     *
     * @param path the path to the config file
     * @return the configuration stored in the given file
     * @throws IOException if reading the given path fails
     */
    public static Config loadFromFile( String path ) throws IOException
    {
        
        logger.info( "Loading config from file " + path );
        try( final Reader reader = getFileReader( path ) )
        {
            
            return loadFromSource( reader );
                        
        }
        
    }

    /**
     * Loads the configuration from the given {@link Reader}.
     * <p>
     * The {@link Reader} can read from any source.
     * But, it is expected to provide a valid {@code YAML} text.
     * 
     * @param reader the {@link Reader} to read from
     * @return the configuration provided by the given {@link Reader}
     * @throws IOException if reading the source fails
     */
    public static Config loadFromSource( Reader reader ) throws IOException
    {

        logger.info( "Loading config from source" );
        try( final Writer writer = new StringWriter();
             final BufferedReader br = new BufferedReader(reader); )
        {
            
            reader.transferTo( writer );
            writer.flush();
            
            final String yaml = writer.toString();
            return of( yaml );
                        
        }

    }

    /**
     * Loads the configuration from the provided {@code YAML} text.
     * <p>
     * The provided string must represent a valid {code YAML} text.
     * 
     * @param yaml some {@code YAML} text
     * @return a new configuration
     * @throws IOException if writing to default location fails
     */
    public static Config of( String yaml ) throws IOException
    {

        logger.finest( "Creating configuration from yaml:" );
        logger.finest( yaml );

        final Map<String,Object> source = new Yaml()
        .load( 
            Require.nonNull( yaml, "The YAML reader cannot be null" )
        );

        /*
         * Benchmarks are executed in different processes.
         * We save the configuration to a default location
         * to allow other processes to retrieve it if needed.
         */
        writeToDefaultLocation( yaml );
        
        return Config.of( source );

    }


    /**
     * Restores the last {@link Config} by reading it
     * from the default location where it was stored
     * during loading.
     * 
     * @return the last {@link Config}
     * @throws IOException if reading the source fails
     */
    public static Config restore() throws IOException
    {

        final Path file = CommonConfig.DEFAULT_OUTPUT_FOLDER.resolve( "config.yaml" );
        logger.fine( "Restore config from default location: " + file );

        final String yaml = Files.readString( file );
        return of( yaml );

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Loads a config file as a {@link Reader}.
     *
     * <p>If the path is preceded by "cp:" or "classpath:" the file will be loaded from the classpath.
     * Otherwise, it will be loaded from the local filesystem.
     *
     * @param path the path to the config file
     * @return a {@link Reader} of the config file
     * @throws FileNotFoundException if the file is not available at the given path
     */
    private static Reader getFileReader( String path ) throws FileNotFoundException
    {
        
        boolean loadFromClasspath = false;
        if( path.startsWith("cp:") )
        {
            path = path.substring( 3 );
            loadFromClasspath = true;
        }

        if( path.startsWith("classpath:") )
        {
            path = path.substring( 10 );
            loadFromClasspath = true;
        }
        
        if( ! loadFromClasspath )
            return new FileReader( path );

        /* Path should start with "/" if it is in the classpath. */
        if( ! path.startsWith("/") )
            path = "/" + path;

        final InputStream is = ConfigLoader.class.getResourceAsStream( path );

        if (is == null)
            throw new FileNotFoundException( "Resource: '" + path + "' was not found in the classpath" );

        return new InputStreamReader( is );
        
    }

    /**
     * Writes the given {@code YAML} text to a file called {@code config.yaml}
     * in the default location defined in {@link CommonConfig#DEFAULT_OUTPUT_FOLDER}
     * 
     * @param yaml the {@code YAML} text to write
     * @throws IOException if writing to default location fails
     */
    private static void writeToDefaultLocation( String yaml ) throws IOException
    {

        final Path directory = CommonConfig.DEFAULT_OUTPUT_FOLDER;
        if( ! Files.exists(directory) )
        {
            
            Files.createDirectories( directory );
            logger.fine( "Folders for path '" + directory + "' have been created." );
            
        }
        
        final Path file = Config.STORE_PATH;
        logger.fine( "Storing config to default location: " + file );

        Files.writeString( file, yaml );

    }

}
