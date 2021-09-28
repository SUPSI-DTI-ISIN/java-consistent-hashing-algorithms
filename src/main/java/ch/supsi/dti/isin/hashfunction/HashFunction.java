package ch.supsi.dti.isin.hashfunction;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.nerd4j.utils.lang.Require;

/**
 * Represents an hashing function to be used inside
 * a consistent hashing altorithm.
 * 
 * @author Massimo Coluzzi
 */
public interface HashFunction
{

    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Performs the hashing of the given byte array.
     * 
     * @param bytes the byte array to hash
     * @return the related hash value
     */
    long hash( byte[] bytes );

    /**
     * Performs the hashing of the given string.
     * 
     * @param key the key to hash
     * @return the related hash value
     */
    default long hash( String key )
    {
        
        return hash(
            Require
                .nonEmpty( key, "The key to hash cannot be null or empty" )
                .getBytes( StandardCharsets.UTF_8 )
        );

    }

    /**
     * Performs the hashing of the given string
     * using the diven seed.
     * 
     * @param key  the key to hash
     * @param seed the seed to use
     * @return the related hash value
     */
    default long hash( String key, int seed )
    {
        
        /*
         * If the seed is 0 we don't take it into consideration
         * therefore we return the hash without seed.
         */
        if( seed == 0 )
            return hash( key );

        final byte[] bytes = Require
            .nonEmpty( key, "The key to hash cannot be null or empty" )
            .getBytes( StandardCharsets.UTF_8 );

        return hash(
            ByteBuffer
                .allocate( bytes.length + 4 )
                .put( bytes )
                .putInt( seed )
                .array()
        );

    }

    /**
     * Performs the hashing of the given long
     * using the diven seed.
     * 
     * @param key  the key to hash
     * @param seed the seed to use
     * @return the related hash value
     */
    default long hash( long key, int seed )
    {

        return hash(
            ByteBuffer
                .allocate( 12 )
                .putLong( key )
                .putInt( seed )
                .array()
        );

    }

    /**
     * Performs the hashing of the given long
     * using the diven seed.
     * 
     * @param key   the key to hash
     * @param index the index to hash with the key
     * @param seed  the seed to use
     * @return the related hash value
     */
    default long hash( long key, int index, int seed )
    {

        return hash(
            ByteBuffer
                .allocate( 16 )
                .putLong( key )
                .putInt( index )
                .putInt( seed )
                .array()
        );

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new hash function that uses the given algorithm.
     * 
     * @param algorithm the algorithm to use
     * @return a new hash function
     */
    public static HashFunction create( HashFunction.Algorithm algorithm )
    {

        Require.nonNull( algorithm, "The algorithm to use is mandatory" );

        switch( algorithm )
        {

            case CRC32: return new CRC32Hash();

            case MD5: return new MD5Hash();
            
            case XX: return new XXHash();

            case MURMUR3: return new Murmur3Hash();

            default:
                throw new IllegalArgumentException( "Unknown algorithm " + algorithm );

        }

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Enumerates the available implementation algorithms.
     * 
     * @author Massimo Coluzzi
     */
    enum Algorithm
    {

        /** {@code https://en.wikipedia.org/wiki/MurmurHash} */
        MURMUR3,

        /** {@code https://en.wikipedia.org/wiki/Cyclic_redundancy_check} */
        CRC32,

        /** {@code https://en.wikipedia.org/wiki/MD5} */
        MD5,
        
        /** {@code http://cyan4973.github.io/xxHash/} */
        XX;        

    }
    
}
