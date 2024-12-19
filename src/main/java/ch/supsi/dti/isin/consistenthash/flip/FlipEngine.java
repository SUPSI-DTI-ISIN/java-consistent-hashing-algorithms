package ch.supsi.dti.isin.consistenthash.flip;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code FlipHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/2402.17549}
 * 
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class FlipEngine implements BucketBasedEngine
{
 
    /** Maximum number of iterations as suggested by the authors. */
    private static final int MAX_NUM_ITERATIONS = 64;


    /** Number of nodes in the cluster. */
    private int size;

    /** Hashing function to use. */
    private final HashFunction hashFunction;

    /** Maximum number of iterations. */
    private final int maxNumIterations;
    

    /**
     * Constructor with parameters.
     * 
     * @param size             the number of working nodes
     * @param hashFunction     the hash function to use
     */
    public FlipEngine( int size, HashFunction hashFunction )
    {

        this( size, MAX_NUM_ITERATIONS, hashFunction );

    }

    /**
     * Constructor with parameters.
     * 
     * @param size             the number of working nodes
     * @param maxNumIterations maximum number of iterations
     * @param hashFunction     the hash function to use
     */
    public FlipEngine( int size, int maxNumIterations, HashFunction hashFunction )
    {

        super();

        this.size = size;
        this.hashFunction = hashFunction;
        this.maxNumIterations = maxNumIterations;

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * {@inheritDoc}
     */
    @Override
    public int getBucket( String key )
    {

        return fliphash( key );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int addBucket()
    {
        
        return size++;
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
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


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * The function to use when {@code 2^(r-1)<n<2^r} as described
     * in the related paper.
     * 
     * @param key the key to hash
     * @return the destination bucket
     */
    private int fliphash( String key )
    {

        if( size == 0 )
            return 0;

        final long pow2Mask = ~0L >>> Long.numberOfLeadingZeros( size );
        final long hash = hashFunction.hash( key );

        final int fliphashPow2 = fliphashPow2( hash, pow2Mask );
        if( fliphashPow2 < size )
            return fliphashPow2;

        else
        {

            final long threshold = pow2Mask >>> 1;
            final int  leadingZeros = Integer.numberOfLeadingZeros( size );
            for( int iterationIndex = 1; iterationIndex <= maxNumIterations; ++iterationIndex )
            {

                final long draw = hashFunction.hash( hash, leadingZeros, iterationIndex) & pow2Mask;
                if( draw <= threshold )
                    break;
                
                else if ( draw < size )
                    return (int) draw;

            }

            return fliphashPow2( hash, threshold );

        }

    }

    /**
     * The function to use when {@code n=2^r} as described in the related paper.
     * 
     * @param hash     the hash value to use
     * @param pow2Mask the power of 2 to use
     * @return the destination bucket
     */
    private int fliphashPow2( long hash, long pow2Mask )
    {
        
        final long maskedHash = hash & pow2Mask;
        if( maskedHash == 0 )
            return 0;

        final int leadingZeros = Long.numberOfLeadingZeros( maskedHash ); 
        final long rehash = hashFunction.hash( hash, leadingZeros );
        final long flipper = rehash & ~0L >>> leadingZeros >>> 1;

        return (int)(maskedHash ^ flipper);

    }

}