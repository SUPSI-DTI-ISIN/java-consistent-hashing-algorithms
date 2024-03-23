package ch.supsi.dti.isin.consistenthash.binomial1;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code BinomialHash} algorithm as described in the related paper:
 * {@code N/A}
 * 
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class Binomial1Engine implements BucketBasedEngine
{


    /** Number of nodes in the cluster. */
    private int size;

    /**
     * This value is used to filter values in the range {@code [0,upperTreeCapacity-1]}
     * where the term {@code upperTreeCapacity} identifies the capacity of the smallest
     * binary tree capable of containing the cluster size.
     */
    private int upperTreeFilter;

    /**
     * This value is used to filter values in the range {@code [0,lowerTreeCapacity-1]}
     * where the term {@code lowerTreeCapacity} identifies the capacity of the biggest
     * binary tree incapable of containing the cluster size.
     */    
    private int lowerTreeFilter;

    /** Hashing function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     * 
     * @param size         the number of working nodes
     * @param hashFunction the hash function to use
     */
    public Binomial1Engine( int size, HashFunction hashFunction )
    {

        super();

        this.size = size;
        this.hashFunction = hashFunction;
        
        int highestOneBit = Integer.highestOneBit( size );
        if( size > highestOneBit )
            highestOneBit = highestOneBit << 1;

        this.upperTreeFilter = highestOneBit - 1;
        this.lowerTreeFilter = this.upperTreeFilter >> 1;
        
    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns a random position iside the same tree level of the provided bucket.
     * 
     * @param hash
     * @param bucket the bucket to relocate
     * @return a random position inside the same tree level
     */
    public int relocateInsideLevel( int bucket, long hash )
    {

        /*
         * If the bucket is {@code 0} or {@code 1}, we are in the root
         * of the tree. Therefore, no relocation is needed.
         */
        if( bucket < 2 )
            return bucket;

        final int levelBaseIndex = Integer.highestOneBit( bucket );
        final long newHash = hashFunction.hash( hash, levelBaseIndex );
        final int h = (int) newHash & (levelBaseIndex - 1);

        final int r = levelBaseIndex + h;
        return r;

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

        if( size <= 1 )
            return 0;

        final long hash = hashFunction.hash( key );
        
        /* We get a random position within the upper tree. */
        int bucket = (int) hash & upperTreeFilter;

        /* We relocate the bucket randomly inside the same tree level. */
        bucket = relocateInsideLevel( bucket, hash );

        /* If the final position is valid, we return it. */
        if( bucket < size )
            return bucket;

        /*
         * Otherwise, we get a new random position in the upper tree
         * and return it if in the range {@code [lowerTreeFilter+1,size-1]}.
         * We repeat the operation twice if needed to get a better balance.
         */
        long rehash = hash;
        for( int i = 0; i < 2; ++i )
        {

            rehash = hashFunction.hash( rehash, upperTreeFilter );
            bucket = (int) rehash & upperTreeFilter;
            if( bucket > lowerTreeFilter && bucket < size )
                return bucket;

        }

        /*
         * Finally, if none of the previous operations succeed,
         * we remap the key in the range covered by the lower tree,
         * which is guaranteed valid.
         */
        bucket = (int) hash & lowerTreeFilter;
        return relocateInsideLevel( bucket, hash );

    }



    /**
     * Increases the cluster size by one.
     * 
     */
    public int addBucket()
    {
        
        final int newBucket = size;
        if( ++size > upperTreeFilter )
        {
            this.upperTreeFilter = (this.upperTreeFilter << 1) | 1;
            this.lowerTreeFilter = (this.lowerTreeFilter << 1) | 1;
        }

        return newBucket;
        
    }

    /**
     * Decreases the cluster size by one.
     * 
     */
    public int removeBucket( int b )
    {
        
        if( --size <= lowerTreeFilter )
        {
            this.lowerTreeFilter = this.lowerTreeFilter >> 1;
            this.upperTreeFilter = this.upperTreeFilter >> 1;
        }

        return size;
        
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

    /**
     * Returns the {@code upperTreeFilter} as described in the paper.
     * 
     * @return the {@code upperTreeFilter} as described in the paper.
     */
    public long upperTreeFilter()
    {

        return upperTreeFilter;

    }

    /**
     * Returns the {@code lowerTreeFilter} as described in the paper.
     * 
     * @return the {@code lowerTreeFilter} as described in the paper.
     */
    public long lowerTreeFilter()
    {

        return lowerTreeFilter;

    }

}
