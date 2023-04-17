package ch.supsi.dti.isin.hashfunction;

import java.util.zip.CRC32;

import org.nerd4j.utils.lang.Require;


/**
 * {@code CRC32} implementation of the {@link HashFunction} interface.
 * 
 * @author Massimo Coluzzi
 */
public class CRC32Hash implements HashFunction
{

    /** Internal implementation of the {@code CRC32} algorithm. */
    private final CRC32 crc32;


    /**
     * Default constructor.
     * 
     */
    public CRC32Hash()
    {

        super();

        this.crc32 = new CRC32();

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

        return "CRC32";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long hash( byte[] bytes )
    {

        Require.nonEmpty( bytes, "The byte array to hash cannot be null or empty" );

        crc32.reset();
        crc32.update( bytes );

        return crc32.getValue();

    }

}
