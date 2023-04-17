package ch.supsi.dti.isin.consistenthash.anchor;

import java.util.Deque;
import java.util.LinkedList;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code AnchorHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/1812.09674.pdf}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 * @author Davide Bertacco
 */
public class AnchorEngine implements BucketBasedEngine
{

    /** Common default seed to use during hashing of the nodes. */
    private static final int  SEED = 0xDEADBEEF;


    /** The anchor: the total set of nodes. */
    private final int[] A;

    /** The working set of nodes. */
    private final int[] W;

    /** Keeps track of the last position of each node. */
    private final int[] L;

    /** Keeps track of the replacement of each node. */
    private final int[] K;

    /** Maximum number of nodes in the cluster (the size of the anchor). */
    private int capacity;

    /** Size of the working set of nodes. */
    private int size;

    /** Keeps track of the removed nodes in reverse order. */
    private final Deque<Integer> removed;

    /** The hash function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     * 
     * @param size          initial number of working buckets (0 < size <= capacity)
     * @param capacity      overall number of available buckets
     * @param hashFunction  hash function to use
     */
    public AnchorEngine( int size, int capacity, HashFunction hashFunction )
    {
        
        super();
        
        this.hashFunction = hashFunction;
                
        this.A = new int[capacity];
        this.W = new int[capacity];
        this.L = new int[capacity];
        this.K = new int[capacity];

        /* Initialize "swap" arrays */
        for( int i = 0; i < capacity; ++i )
        {

            L[i] = i;
            W[i] = i;
            K[i] = i;

        }

        /* We treat initial removals as ordered removals. */
        for( int i = capacity - 1; i >= size; --i )
            A[i] = i;

        this.capacity = capacity;
        this.size = size;

        this.removed = new LinkedList<>();

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the bucket where the given key should be mapped.
     * 
     * @param key the key to map
     * @return the related bucket
     */
    public int getBucket( String key )
    {

        long k = Math.abs( hashFunction.hash(key,SEED) );
        int b = (int) (k % capacity);

        /* Loop until hitting a working bucket. */
        while( A[b] > 0 )
        {
            /* We rehash the key using the bucket as seed */
            k = Math.abs( hashFunction.hash(k,b,SEED) );
            
            int h = (int) (k % A[b]);
            while (A[h] >= A[b])
                h = K[h];
            
            b = h;

        }

        return b;

    }

    /**
     * Adds a new bucket to the engine.
     * 
     * @return the added bucket
     */
    public int addBucket()
    {

        /*
         * If the stack is not empty takes the last removed bucket.
         * Otherwise, uses the next available bucket (with index 'size').
         */
        final int b = removed.isEmpty() ? size : removed.pop();

        /* Restores the removed bucket. */
        A[b] = 0;

        /* Restores the last position of the bucket. */
        L[W[size]] = size;

        /* Adds the bucket to the working set. */
        W[L[b]] = b;

        /* Restores the replacement. */
        K[b] = b;

        /* Updates the size of the working set. */
        ++size;

        return b;

    }

    /**
     * Removes the given bucket from the engine.
     * 
     * @param b the bucket to remove
     * @return the removed bucket
     */
    public int removeBucket( int b )
    {
        
        /* Updates the size of the working set. */
        --size;

        /*
         * If the stack of the removed buckets is empty
         * and the bucket to remove is the one with the 
         * highest index we do not need to put it into
         * the stack.
         */
        if( b < size || ! removed.isEmpty() )
            removed.push( b );

        /* Marks the bucket as removed. */
        A[b] = size;

        /* Saves the last position of the bucket. */
        W[L[b]] = W[size];

        /* Removes the bucket from the working set. */
        L[W[size]] = L[b];

        /* Stores the replacement for the bucket. */
        K[b] = W[size];

        return b;

    }

    /**
     * Returns the size of the working set.
     * 
     * @return size of the working set.
     */
    public int size()
    {

        return size;

    }

    /**
     * Returns the overall capacity of the cluster.
     * 
     * @return overall capacity of the cluster.
     */
    public int capacity()
    {

        return capacity;

    }

}
