package ch.supsi.dti.isin.consistenthash.binomial;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;

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

    /** Smallest multiple of 2 greater than or equal to {@code size}. */
    private int m;

    /**
     * The value used by the algorithm are {@code m} and {@code m/2}.
     * Therefore, we store {@code m1 = m-1} and {@code m2 = m/2-1}.
     */
    private int m1, m2, hm;

    private double l;

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
        
        this.m = Integer.highestOneBit( size );
        if( size > m )
            m = m << 1;

        this.hm = m >> 1;
        this.m1 = m-1;
        this.m2 = hm - 1;

        this.l = ((double) this.m / size) - 1;

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

        /* 2X USING REHASH */

        final long hash1 = hashFunction.hash( key );
        final int h1 = (int)(hash1 ^ (hash1 >> 32));

        // final int r1 = h1 & m1;
        final int r1 = f( hash1, m1 );
        if( r1 < size )
            return r1;

        long hash2 = hash1;
        for( int i = 0; i < 2; ++i )
        {

            hash2 = hashFunction.hash( hash2, m1 );
            final int h2 = (int)(hash2 ^ (hash2 >> 32));
            final int r2 = h2 & m1;
            if( r2 > m2 & r2 < size )
                return r2;
        }

        return f( hash1, m2 );

    }



    // /**
    //  * Returns the index of the bucket where the given key should be mapped.
    //  * 
    //  * @param key the key to map
    //  * @return the related bucket
    //  */
    // public int getBucket( String key )
    // {

    //     /* PERCENTAGE MOVE */

    //     final long hash = hashFunction.hash( key );
    //     final Random random = new Random( hash );
    //     final int h1 = random.nextInt();
        
    //     final int r1 = h1 & m1;
    //     if( r1 < size )
    //         return r1;

    //     final int hm = m >> 1;
    //     final int x = random.nextInt( hm );
    //     if( x < size - hm )
    //         return x + hm;
        
    //     return h1 & m2;

    // }

    // /**
    //  * Returns the index of the bucket where the given key should be mapped.
    //  * 
    //  * @param key the key to map
    //  * @return the related bucket
    //  */
    // public int getBucket( String key )
    // {

    //     /* BALANCED PERCENTAGE MOVE */

    //     final long hash = hashFunction.hash( key );
    //     final int r1 =f( hash, m1 );
    //     if( size == m )
    //         return f( hash, m1 );
            
    //     // CASE 1: r1 < m
    //     if( r1 < hm )
    //         return r1;
        
    //     // Key related probability to shift backwards
    //     final int h1 = (int)(hash ^ (hash>>32));
    //     final double p = (double) h1 / Integer.MAX_VALUE;
    //     final double a = Math.abs( p );
    //     if( a < l )
    //         return r1 - hm;
            
    //     // CASE 2: m <= r1 < size
    //     if( r1 < size )
    //         return r1;
            
    //     // CASE 3: size <= r1 < 2m    
    //     final int x = size - hm;
        
    //     // final long hash2 = hashFunction.hash( hash, m );
    //     // final int h2 = (int)(hash2 ^ (hash2>>32));
    //     // final double p2 = (double) h2 / Integer.MAX_VALUE;
    //     // final double a2 = Math.abs( p2 );
    //     // final double v = a2 * x;
    //     // final int r2 = (int) v;

    //     // return r2 + hm;
    //     return k( hash, x ) + hm;

    // }

    // /**
    //  * Returns the index of the bucket where the given key should be mapped.
    //  * 
    //  * @param key the key to map
    //  * @return the related bucket
    //  */
    // public int getBucket( String key )
    // {

    //     /* MONOTONE PERCENTAGE MOVE */

    //     final long hash = hashFunction.hash( key );
    //     final Random random = new Random( hash );
    //     final int h1 = random.nextInt();
        
    //     final int r1 = h1 & m1;
    //     // if( size == m )
    //     //     return r1;
            
    //     // CASE 1: r1 < size
    //     final int hm = m >> 1;
    //     if( r1 < size )
    //         return r1;
        

    //     // CASE 2: size <= r1 < 2m
    //     final int s = size - hm;
    //     final double l = (double)s / hm;
    //     final double p = random.nextDouble();

    //     if( p > l )
    //         return r1 - hm;
            
    //     return hm + k( hash, s );
    //     // return hm + random.nextInt(s);

    // }

        /**
     * The function {@code g} as described in the paper.
     * 
     * @param hash   the hash of the key.
     * @param random the pseudo-random generator to use.
     * @return the intermediate bucket.
     */
    private int k( long hash, int limit )
    {

        LinearCongruentialGenerator generator = new LinearCongruentialGenerator( hash );

        int candidate = 0;
        int next;

        // Jump from bucket to bucket until we go out of range
        while( true ){
            next = (int) ((candidate + 1) / generator.nextDouble());
            if (next >= 0 && next < limit) {
                candidate = next;
            } else {
                return candidate;
            }
        }

    }

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

    /**
     * The function {@code f} as described in the paper.
     * 
     * @param hash   the hash of the key.
     * @param mx     one of {@link #m1} or {@link #m2}.
     * @param random the pseudo-random generator to use.
     * @return the intermediate bucket.
     */
    private int f( long hash, int mx )
    {

        final int kBits = (int)(hash & mx);
        if( kBits == 0 )
            return 0;

        final int h = Integer.highestOneBit( kBits );
        final long h2 = hashFunction.hash( hash, h );
        final int h3 = (int) h2 & (h -1);

        return h + h3;
        
    }

    // /**
    //  * Returns the index of the bucket where the given key should be mapped.
    //  * 
    //  * @param key the key to map
    //  * @return the related bucket
    //  */
    // public int getBucket( String key )
    // {

    //     /* 4M DIFFERENT HASHINGS */

    //     final long hash1 = hashFunction.hash( key );
    //     final int h1 = (int)(hash1 ^ (hash1 >> 32));

    //     final int r1 = h1 & m1;
    //     if( r1 < size )
    //         return r1;

    //     /* size <= r1 <= m1 */
    //     final int k = 3;
    //     final int mask = (1 << k) -1;
    //     final int seed = h1 & mask;
    //     final long hash2 = hashFunction.hash( hash1, seed );
    //     final int h2 = (int)(hash2 ^ (hash2 >> 32));
        
    //     final int m4 = (m2 << k) | mask;
    //     final int r2 = h2 & m4;
    //     if( r2 > m2 & r2 < size )
    //         return r2;

    //     return h1 & m2;

    // }


    // /**
    //  * Returns the index of the bucket where the given key should be mapped.
    //  * 
    //  * @param key the key to map
    //  * @return the related bucket
    //  */
    // public int getBucket( String key )
    // {

    //     /* KM DIFFERENT HASHINGS */

    //     final long hash1 = hashFunction.hash( key );
    //     final int h1 = (int)(hash1 ^ (hash1 >> 32));

    //     final int r1 = h1 & m1;
    //     if( r1 < size )
    //         return r1;
        
    //     /* size <= r4 <= m8 */
    //     final int m8 = (m<<10)-1;
    //     final int seed = r1 & 1023;

    //     final long hash2 = hashFunction.hash( hash1, seed );
    //     final int h2 = (int)(hash2 ^ (hash2 >> 32));

    //     final int r2 = h2 & m8;
    //     if( r2 > m2 & r2 < size )
    //         return r2;

    //     return h1 & m2;

    // }


    /**
     * Increases the cluster size by one.
     * 
     */
    public int addBucket()
    {
        
        final int newBucket = size;
        if( ++size > m )
        {
            this.m  = this.m << 1;
            this.hm = this.hm << 1;

            this.m1 = (this.m1 << 1) | 1;
            this.m2 = (this.m2 << 1) | 1;

            this.l = ((double) this.m / size) - 1;
        }

        return newBucket;
        
    }

    /**
     * Decreases the cluster size by one.
     * 
     */
    public int removeBucket( int b )
    {
        
        if( --size <= hm )
        {
            this.m = hm;
            this.hm = this.hm >> 1;
            this.m1 = this.m1 >> 1;
            this.m2 = this.m2 >> 1;

            this.l = ((double) this.m / size) - 1;
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
     * @return the smallest binomial of 2 greater than {@code size}.
     */
    public long m()
    {

        return m;

    }

    /**
     * Returns the value {@code m-1}.
     * 
     * @return {@code m-1}.
     */
    public long m1()
    {

        return m1;

    }

    /**
     * Returns the value {@code m/2-1}.
     * 
     * @return {@code m/2-1}.
     */
    public long m2()
    {

        return m2;

    }

}
