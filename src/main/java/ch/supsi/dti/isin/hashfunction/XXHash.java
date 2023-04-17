package ch.supsi.dti.isin.hashfunction;

import org.apache.commons.codec.digest.XXHash32;
import org.nerd4j.utils.lang.Require;


/**
 * {@code XX32} implementation of the {@link HashFunction} interface.
 * 
 * @author Massimo Coluzzi
 */
public class XXHash implements HashFunction
{

    
    /** Internal implementation of the {@code XX32} algorithm. */
    private XXHash32 xxHash32;


    /**
     * Default constructor.
     * 
     */
    public XXHash()
    {

        super();

        this.xxHash32 = new XXHash32();

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

        return "XX";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public synchronized long hash( byte[] bytes )
    {

        Require.nonEmpty( bytes, "The byte array to hash cannot be null or empty" );
        
        xxHash32.reset();
        xxHash32.update( bytes );

        return xxHash32.getValue();

    }
    
}
