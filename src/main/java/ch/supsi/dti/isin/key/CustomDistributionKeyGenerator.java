package ch.supsi.dti.isin.key;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.ZipInputStream;


/**
 * Generates the set of keys based on the values of a given text file, one key per line.
 *
 * <p>The class implements a circular array so that its able to provide an unbounded amount of keys.
 *
 * @author Massimo Coluzzi
 */
public class CustomDistributionKeyGenerator implements KeyGenerator
{

    /** Path to the source file used to load the keys. */
    public static final String SOURCE_PATH = "/clustered-distribution.zip";


    /** Source of the key distribution. */
    private URL source;

    /** Keys that will be returned by the generator. */
    private final String[] data;


    /**
     * Constructor with parameters.
     *
     * @param url the path of the file to load.
     */
    public CustomDistributionKeyGenerator( URL url )
    {

        super();

        this.source = Require.nonNull( url, "The dataset file name is mandatory" );
        this.data = loadData();
        
    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the number of keys loaded from file.
     * 
     * @return the original number of keys.
     */
    public int size()
    {

        return data.length;
        
    }

    /**
     * {@inheritDoc}
     */
    public Stream<String> stream()
    {

        final Iterator<String> iter = iterator();
        return Stream.generate( iter::next );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> iterator()
    {

        return new Iterator<>()
        {

            /** The index of the current key. */
            private int i = -1;

            /** Number of times the data array was read. */
            private int iteration = 0;

            /** The prefix to add to each key. */
            private String prefix = "0";

            
            @Override
            public boolean hasNext()
            {
                return true;
            }

            @Override
            public String next()
            {

                if( ++i >= data.length )
                {
                    i = 0;
                    iteration += 1;
                    prefix = String.valueOf( iteration );
                }

                return prefix + data[i];

            }

        };

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Loads the data from the given source into an array of keys.
     * 
     * @return array of keys to use in the generator
     */
    private String[] loadData()
    {

        try(
            final ZipInputStream zis = new ZipInputStream( source.openStream() );
            final BufferedReader reader = new BufferedReader( new InputStreamReader(zis) );
        )
        {

            /* We expect the zip file to have only one entry. */
            zis.getNextEntry();
            return reader.lines()
                    .limit( Integer.MAX_VALUE )
                    .collect( Collectors.toList() )
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
