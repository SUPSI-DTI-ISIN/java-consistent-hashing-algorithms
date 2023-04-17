package ch.supsi.dti.isin.consistenthash.jump;

import com.google.common.hash.Hashing;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code JumpHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/1406.2294.pdf}
 * 
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 * @author Davide Bertacco
 */
public class JumpEngine implements BucketBasedEngine
{

    /** Number of nodes in the cluster. */
    private int size;

    /** Hashing function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     * 
     * @param size         the number of working nodes
     * @param hashFunction the hash function to use
     */
    public JumpEngine( int size, HashFunction hashFunction )
    {

        super();

        this.size = size;
        this.hashFunction = hashFunction;

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the index of the bucket where the given key should be mapped.
     * 
     * @param key the key to map
     * @return the related bucket
     */
    public int getBucket( String key )
    {

        return Hashing.consistentHash( hashFunction.hash(key), size );

    }

    /**
     * Increases the cluster size by one.
     * 
     */
    public int addBucket()
    {
        
        return size++;
        
    }

    /**
     * Decreases the cluster size by one.
     * 
     */
    public int removeBucket( int b )
    {
        
        return --size;
        
    }
   
    /**
     * Returns the size of the cluster.
     * 
     * @return the size of the cluster.
     */
    public int size()
    {

        return size;

    }

}
