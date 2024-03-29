package ch.supsi.dti.isin.consistenthash.memento;

import com.google.common.hash.Hashing;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code MementoHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/2306.09783.pdf}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class MementoEngine implements BucketBasedEngine
{ 

    /** The hash function to use. */
    final HashFunction hashFunction;
    
    /** The memory of the removed nodes, also addressed as replacement set. */
    private final Memento memento;

    /**
     * Size of the related b-array.
     * Inside this range, there can also be nonworking
     * buckets that the algorithm will handle.
    */
    private int bArraySize;

    /** The last removed bucket. */
    private int lastRemoved;


    /**
     * Creates a new MementoHash engine.
     * 
     * @param size          initial number of working buckets (0 < size)
     * @param hashFunction  hash function to use
     */
    public MementoEngine( int size, HashFunction hashFunction )
    {
        
        super();
        
        this.lastRemoved  = size;
        this.bArraySize   = size;

        this.hashFunction = hashFunction;
        this.memento      = new Memento();

    }


    /**
     * Returns the bucket where the given key should be mapped.
     * 
     * @param key the key to map
     * @return the related bucket
     */
    @Override
    public int getBucket( String key )
    {

        /*
         * We invoke JumpHash to get a bucket
         * in the range [0,bArraySize-1].
         */
        int b = Hashing.consistentHash( hashFunction.hash( key ), bArraySize );
        
        /*
         * We check if the bucket was removed, if not we are done.
         * If the bucket was removed the replacing bucket is >= 0,
         * otherwise it is -1.
         */
        int replacer = memento.replacer( b );
        while( replacer >= 0 )
        {

            /*
             * If the bucket was removed, we must re-hash and find
             * a new bucket in the remaining slots. To know the
             * remaining slots, we look at 'replacer' that also
             * represents the size of the working set when the bucket
             * was removed and get a new bucket in [0,replacer-1].
             */
            final long h = Math.abs( hashFunction.hash(key,b) );
            b = (int)( h % replacer );

            /*
             * If we hit a removed bucket we follow the replacements
             * until we get a working bucket or a bucket in the range
             * [0,replacer-1]
             */
            int r = memento.replacer( b );
            while( r >= replacer )
            {
                b = r;
                r = memento.replacer( b );
            }
                
            /* Finally we update the entry of the external loop. */
            replacer = r;
                        
        }

        return b;

    }


    /**
     * Adds a new bucket to the engine.
     * 
     * @return the added bucket
     */
    @Override
    public int addBucket()
    {

        /* The new bucket to add is the last removed one. */
        final int bucket = lastRemoved;

        /**
         * We restore the bucket from the replacement set
         * and update the value of the last removed bucket.
         */
        this.lastRemoved = memento.restore( bucket );

        /**
         * If the restored bucket is 'bArraySize'
         * we must update the actual size by 1.
         */
        this.bArraySize = bArraySize > bucket ? bArraySize : bucket + 1;
        
        return bucket;

    }


    /**
     * Removes the given bucket from the engine.
     * 
     * @param bucket the bucket to remove
     * @return the removed bucket
     */
    @Override
    public int removeBucket( int bucket )
    {
        
        /*
         * If the lookup table is empty and the bucket to remove is the last one,
         * we are in the same use case as JumpHash. In this case we don't need
         * to remember the bucket, we just need to reduce the size of the b-array.
         */
        if( memento.isEmpty() && bucket == bArraySize - 1 )
        {
            lastRemoved = bArraySize = bucket;
            return bucket;
        }
        
        /* Otherwise, we add the entry to the memento table using the removed bucket as the key. */
        this.lastRemoved = memento.remember( bucket, size() - 1, lastRemoved );
        
        return bucket;

    }


    /**
     * Returns the size of the working set.
     * 
     * @return size of the working set.
     */
    public int size()
    {

        return bArraySize - memento.size();

    }


    /**
     * Returns the size of the b-array.
     * 
     * @return the size of the b-array.
     */
    public int bArraySize() 
    {

        return bArraySize;

    }

}