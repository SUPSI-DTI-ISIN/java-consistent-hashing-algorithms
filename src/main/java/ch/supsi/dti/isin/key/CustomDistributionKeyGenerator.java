package ch.supsi.dti.isin.key;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.zip.ZipInputStream;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;


/**
 * Generates the set of keys based on the values of a given text file, one key per line.
 *
 * <p>The class implements a circular array so that its able to provide an unbounded amount of keys.
 *
 * @author Massimo Coluzzi
 */
public class CustomDistributionKeyGenerator extends AbstractKeyGenerator
{

    /** Path to the source file used to load the keys. */
    public static final String SOURCE_PATH = "/clustered-distribution.zip";


    /** Source of the key distribution. */
    private URL source;


    /**
     * Constructor with parameters.
     *
     * @param url the path of the file to load
     * @param size the size of the base dataset to create
     */
    public CustomDistributionKeyGenerator( URL url, int size  )
    {

        super( loadData(url, size) );

        this.source = url;
        
    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Loads the data from the given source into an array of keys.
     * 
     * @param source the path of the file to load
     * @param size the size of the base dataset to create
     * @return array of keys to use in the generator
     */
    private static String[] loadData( URL source, int size )
    {

        Require.nonNull( source, "The source file to load is mandatory" );
        Require.toHold( size > 0, "The size of the base dataset must be strictly positive" );

        try(
            final ZipInputStream zis = new ZipInputStream( source.openStream() );
            final BufferedReader reader = new BufferedReader( new InputStreamReader(zis) );
        )
        {

            /* We expect the zip file to have only one entry. */
            zis.getNextEntry();
            return reader.lines()
                    .limit( size )
                    .toArray( String[]::new );

        }catch( IOException ex )
        {

            throw new RuntimeException( ex );

        }

    }


    /* ******************* */
    /*  OBJECT OVERWRITES  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
                this, other,
                o -> o.source
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {

        return Hashcode.of(source);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {

        return ToString.of( this )
                .withCustomClassName( KeyGenerator.class.getSimpleName() )
                .print( source )
                .likeEclipse();

    }

}
