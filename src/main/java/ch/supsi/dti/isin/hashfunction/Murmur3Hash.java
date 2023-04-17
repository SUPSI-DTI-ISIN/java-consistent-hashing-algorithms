package ch.supsi.dti.isin.hashfunction;

import org.apache.commons.codec.digest.MurmurHash3;
import org.nerd4j.utils.lang.Require;


/**
 * {@code Murmur3} implementation of the {@link HashFunction} interface.
 * 
 * @author Massimo Coluzzi
 */
public class Murmur3Hash implements HashFunction
{


    /**
     * Default constructor.
     * 
     */
    public Murmur3Hash()
    {

        super();

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}}
     */
    @Override
    public String name()
    {

        return "MurMur3";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public long hash( byte[] bytes )
    {

        Require.nonEmpty( bytes, "The byte array to hash cannot be null or empty" );
        return Math.abs( MurmurHash3.hash32x86(bytes) );

    }

}
