package ch.supsi.dti.isin.consistenthash.jumpback;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import com.dynatrace.hash4j.consistent.ConsistentBucketHasher;
import com.dynatrace.hash4j.consistent.ConsistentHashing;
import com.dynatrace.hash4j.random.PseudoRandomGeneratorProvider;


/**
 * Implementation of the {@code JumpBackHash} algorithm as implemented in the hash4j library:
 * {@code https://github.com/dynatrace-oss/hash4j}
 * 
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 */
public class JumpBackEngine implements BucketBasedEngine
{

    /** Number of nodes in the cluster. */
    private int size;

    /** Hashing function to use. */
    private final HashFunction hashFunction;

    private final ConsistentBucketHasher consistentBucketHasher;


    /**
     * Constructor with parameters.
     * 
     * @param size         the number of working nodes
     * @param hashFunction the hash function to use
     */
    public JumpBackEngine( int size, HashFunction hashFunction )
    {

        super();

        this.size = size;
        this.hashFunction = hashFunction;
        this.consistentBucketHasher = ConsistentHashing.jumpBackHash( PseudoRandomGeneratorProvider.splitMix64_V1() );

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

        return consistentBucketHasher.getBucket( hashFunction.hash(key), size );

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
