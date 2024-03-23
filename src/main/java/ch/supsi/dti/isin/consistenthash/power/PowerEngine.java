package ch.supsi.dti.isin.consistenthash.power;

import java.util.Random;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code PowerHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/2307.12448.pdf}
 * 
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class PowerEngine implements BucketBasedEngine
{


    /** Number of nodes in the cluster. */
    private int size;

    /** Smallest power of 2 greater than or equal to {@code size}. */
    private int m;

    /**
     * The value used by the algorithm are {@code m-1} and {@code m/2-1}.
     * Therefore, we store {@code m1 = m-1} and {@code m2 = m/2-1}.
     */
    private int m1, m2;

    /** Hashing function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     * 
     * @param size         the number of working nodes
     * @param hashFunction the hash function to use
     */
    public PowerEngine( int size, HashFunction hashFunction )
    {

        super();

        this.size = size;
        this.hashFunction = hashFunction;
        
        this.m = Integer.highestOneBit( size );
        if( size > m )
            m = m << 1;

        this.m1 = m-1;
        this.m2 = (m >> 1) - 1;

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

        final long hash = hashFunction.hash( key );
        final Random random = new Random( hash );

        // final int h = (int)(hash ^ (hash >> 1));
        // final int r1 = h & m1;
        // if( r1 < size )
        //     return r1;
        
        // final int hm = m >> 1;
        // final int x = random.nextInt( hm );
        // if( x < size - hm )
        //     return k( hash );

        // // final int r2 = g( hash, random );
        // // if( r2 > this.m2 )
        // //     return r2;

        // return h & m2;

        // final int r1 = f( hash, this.m1, random );
        // if( r1 < size )
        //     return r1;
        
        // final int p = random.nextInt( size );
        // if( p > m2 )
        //     return k( hash );
        // // final int r2 = g( hash, random );
        // // if( r2 > this.m2 )
        // //     return r2;

        // return f( hash, this.m2, random );


        final int r1 = f( hash, this.m1, random );
        if( r1 < size )
            return r1;
        
        final int r2 = g( hash, random );
        if( r2 > this.m2 )
            return r2;

        return f( hash, this.m2, random );

    }

    /**
     * Increases the cluster size by one.
     * 
     */
    public int addBucket()
    {
        
        if( ++size > m )
        {
            this.m2 = this.m - 1;
            this.m  = this.m << 1;
            this.m1 = this.m - 1;

        }

        return size-1;
        
    }

    /**
     * Decreases the cluster size by one.
     * 
     */
    public int removeBucket( int b )
    {
        
        final int m2 = this.m >> 1;
        if( --size <= m2 )
        {
            this.m = m2;
            this.m1 = this.m - 1;
            this.m2 = (this.m >> 1) - 1;
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
     * Returns the value {@code m} as described in the paper.
     * 
     * @return the smallest power of 2 greater than {@code size}.
     */
    public int m()
    {

        return m;

    }

    /**
     * Returns the value {@code m-1}.
     * 
     * @return {@code m-1}.
     */
    public int m1()
    {

        return m1;

    }

    /**
     * Returns the value {@code m/2-1}.
     * 
     * @return {@code m/2-1}.
     */
    public int m2()
    {

        return m2;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    // /**
    //  * The function {@code f} as described in the paper.
    //  * 
    //  * @param hash   the hash of the key.
    //  * @param mx     one of {@link #m1} or {@link #m2}.
    //  * @param random the pseudo-random generator to use.
    //  * @return the intermediate bucket.
    //  */
    private int f( long hash, int mx, Random random )
    {

        final int kBits = (int)(hash & mx);
        if( kBits == 0 )
            return 0;

        final int h = Integer.highestOneBit( kBits );
        // random.setSeed( hash ^ h );
        // random.nextInt();
        
        // return h + random.nextInt( h );

        final long h2 = hashFunction.hash( hash, h );
        final int h3 = (int) h2 & (h -1);

        return h + h3;
        
    }

    /**
     * The function {@code g} as described in the paper.
     * 
     * @param hash   the hash of the key.
     * @param random the pseudo-random generator to use.
     * @return the intermediate bucket.
     */
    private int g( long hash, Random random )
    {

        LinearCongruentialGenerator generator = new LinearCongruentialGenerator( hash );

        int candidate = m2;
        int next;

        // Jump from bucket to bucket until we go out of range
        while( true ){
            next = (int) ((candidate + 1) / generator.nextDouble());
            if (next >= 0 && next < size) {
                candidate = next;
            } else {
                return candidate;
            }
        }

    }

    // /**
    //  * The function {@code g} as described in the paper.
    //  * 
    //  * @param hash   the hash of the key.
    //  * @param random the pseudo-random generator to use.
    //  * @return the intermediate bucket.
    //  */
    // private int k( long hash )
    // {

    //     LinearCongruentialGenerator generator = new LinearCongruentialGenerator( hash );

    //     final int hm = m >> 1;
    //     final int limit = size - hm;
    //     int candidate = 0;
    //     int next;

    //     // Jump from bucket to bucket until we go out of range
    //     while( true ){
    //         next = (int) ((candidate + 1) / generator.nextDouble());
    //         if (next >= 0 && next < limit) {
    //             candidate = next;
    //         } else {
    //             return candidate + hm;
    //         }
    //     }

    // }

    // /**
    //  * The function {@code g} as described in the paper.
    //  * 
    //  * @param hash   the hash of the key.
    //  * @param random the pseudo-random generator to use.
    //  * @return the intermediate bucket.
    //  */
    // private int g( long hash, Random random )
    // {

    //      /* Initially, x is set to the value of m/2-1 */
    //     int x = this.m2;

    //     while( true )
    //     {

    //         /* 1. Generate U. */
    //         final double u = random.nextDouble();

    //         /* 2. Compute r = min{j: U>(x+1)/(j+1) */
    //         final int r = (int) Math.ceil((x + 1) / u) - 1;

    //         /* 3. Set x = r if r < n */
    //         if( r < size )
    //             x = r;
    //         else
    //             /*
    //              * Otherwise, the algorithm returns the current
    //              * value of x as the result.
    //              */
    //             return x;

    //     }

    // }

    private static final class LinearCongruentialGenerator {
        private long state;
    
        public LinearCongruentialGenerator(long seed) {
          this.state = seed;
        }
    
        public double nextDouble() {
          state = 2862933555777941757L * state + 1;
          return ((double) ((int) (state >>> 33) + 1)) / 0x1.0p31;
        }
      }

}
