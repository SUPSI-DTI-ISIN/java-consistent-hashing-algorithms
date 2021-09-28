package ch.supsi.dti.isin.hashfunction;

import org.apache.commons.codec.digest.DigestUtils;
import org.nerd4j.utils.lang.Require;

/**
 * {@code MD5} implementation of the {@link HashFunction} interface.
 * 
 * @author Massimo Coluzzi
 */
public class MD5Hash implements HashFunction
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long hash( byte[] bytes )
    {

        Require.nonEmpty( bytes, "The byte array to hash cannot be null or empty" );

        final byte[] digest = DigestUtils.md5( bytes );

        long hash = 0;
        for( int i = 0; i < 4; i++ )
        {
            hash <<= 8;
            hash |= ((int) digest[i]) & 0xFF;
        }

        return hash;

    }

}