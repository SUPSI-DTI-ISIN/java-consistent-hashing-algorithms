package ch.supsi.dti.isin.key;

import java.util.Iterator;
import java.util.stream.Stream;

import org.nerd4j.utils.lang.Require;


/**
 * Abstract class common to all implementations of {@link KeyGenerator}.
 * 
 * @author Massimo Coluzzi
 */
public abstract class AbstractKeyGenerator implements KeyGenerator
{

    /** Keys that will be returned by the generator. */
    private final String[] data;


    /**
     * Constructor with parameters.
     *
     * @param data the base set of key to use.
     */
    protected AbstractKeyGenerator( String[] data )
    {

        super();

        this.data = Require.nonNull( data, "The base set to keys to use is mandatory" );
        
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

        
        return new KeyIterator();

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Represents a never-ending iterator over the provided keys.
     * 
     * <p>
     * The iterator will prepend an incremental value to the key.
     * Every time all the keys have been returned, the iterator
     * will increment the prefix in oder to generate always new keys.
     * 
     * @author Massimo Coluzzi
     */
    private class KeyIterator implements Iterator<String>
    {

        /** The index of the current key. */
        private int i = -1;

        /** Number of times the data array was read. */
        private int iteration = 0;

        /** The prefix to add to each key. */
        private String prefix = "0";

        
        /**
         * {@inheritDoc}
         */
        @Override
        public boolean hasNext()
        {
            return true;
        }

        /**
         * {@inheritDoc}
         */
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

    }

}
