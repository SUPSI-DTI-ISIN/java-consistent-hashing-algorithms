package ch.supsi.dti.isin.consistenthash.binomial;

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
public class BinomialEngine implements BucketBasedEngine
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
    public BinomialEngine( int size, HashFunction hashFunction )
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
     * Linear congruential generator to create uniformly
     * distributed values.
     * 
     * @param value the value to rehash
     * @param seed the seed to use
     * @return the rehashed value
     */
    public long rehash( long value, int seed )
    {
        
        final long hash = 2862933555777941757L * value + 1;
        return (hash * hash * seed) >>> 32;

    }

    /**
     * Returns a random position iside the same tree level of the provided bucket.
     * 
     * @param bucket the bucket to relocate
     * @param hash   the hash of the key mapped to the bucket
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
        final int levelFilter = levelBaseIndex - 1;

        final long levelHash = rehash( hash, levelFilter );
        final int levelIndex = (int) levelHash & levelFilter;

        return levelBaseIndex + levelIndex;

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

        /* If the cluster counts only one node we return such a node. */
        if( size < 2 )
            return 0;

        /* We get the hash of the provided key. */
        final long hash = hashFunction.hash( key );
        
        /* We get a position within the upper tree based on the value of the key hash. */
        int bucket = (int) hash & upperTreeFilter;

        /* We relocate the bucket randomly inside the same tree level. */
        bucket = relocateInsideLevel( bucket, hash );

        /* If the final position is valid, we return it. */
        if( bucket < size )
            return bucket;

        /*
         * Otherwise, we get a new random position in the upper tree
         * and return it if in the range [lowerTreeFilter+1,size-1].
         * We repeat the operation twice (if needed) to get a better balance.
         */
        long h = hash;
        for( int i = 0; i < 4; ++i )
        {

            h = rehash( h, upperTreeFilter );
            bucket = (int) h & upperTreeFilter;
            
            if( bucket <= lowerTreeFilter )
                break;

            if( bucket < size )
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
