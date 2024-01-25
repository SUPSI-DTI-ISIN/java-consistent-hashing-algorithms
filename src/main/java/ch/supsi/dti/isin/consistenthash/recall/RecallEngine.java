package ch.supsi.dti.isin.consistenthash.recall;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;
import ch.supsi.dti.isin.consistenthash.power.PowerEngine;
import ch.supsi.dti.isin.consistenthash.recall.HashTable.Pointer;
import ch.supsi.dti.isin.consistenthash.recall.HashTable.Replacement;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code RecallHash} algorithm as described in the related paper:
 * {@code TBD}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class RecallEngine implements BucketBasedEngine
{ 

    /** The hash function to use. */
    final HashFunction hashFunction;

    /** The underlying consistent hashing engine to use. */
    final PowerEngine engine;
    
    /** The hash table collecting the replacements for the removed nodes, also addressed as replacement set. */
    private final HashTable<Replacement> R;

    /** The hash table storing the replacement chains in reverse order. */
    private final HashTable<Pointer> I;

    /**
     * Size of the related b-array.
     * Inside this range, there can also be non-working
     * buckets that the algorithm will handle.
    */
    private int n;

    /** The last removed bucket. */
    private int l;


    /**
     * Creates a new RecallHash engine.
     * 
     * @param size          initial number of working buckets (must be > 0)
     * @param hashFunction  hash function to use
     */
    public RecallEngine( int size, HashFunction hashFunction )
    {
        
        super();
        
        this.l = this.n = Require.trueFor( size, size > 0, "The size of the cluster must be greater than 0");

        this.R = new HashTable<>();
        this.I = new HashTable<>();

        this.hashFunction = hashFunction;
        this.engine       = new PowerEngine( size, hashFunction );

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
         * We invoke the underlying CH algorithm
         * to get a bucket in the range [0,bArraySize-1].
         */
        int b = engine.getBucket( key );
        
        /*
         * If b is working we are done. Otherwise,
         * we need to iterate on a smaller range.
         */
        Replacement b_rep = R.get( b );
        while( b_rep != null )
        {

            /*
             * If b_rep != null then b has beed removed.
             * - b_rep.w is the size of working buckets after
             *   removing b.
             * - b_rep.r is the replacing bucket of b.
             *   We expect b_rep.r to always be a working bucket.
             */
            final int w_b = b_rep.w;

            /*
             * If the bucket b has been removed, we must re-hash and find
             * a new bucket in the rage [0,w_b-1], where w_b is the
             * number of working buckets after the removal of b.
             */
            final long h = Math.abs( hashFunction.hash(key,b) );
            b = (int)( h % w_b );

            /* We check if the new bucket has been removed as well. */
            b_rep = R.get( b );

            if( b_rep == null )
                return b;

            if( b_rep.w >= w_b )
            {
                b = b_rep.r;
                b_rep = R.get( b );
            }
            

            /*
             * If the new bucket b is working, we are done (the next check will exit the loop).
             * If it was removed after the original b, we would perform another iteration.
             * Otherwise, it was removed before the original b.
             * In this case, we use the replacing bucket (which is supposed to be working).
             */
            if( b_rep != null && b_rep.w >= w_b )
            {
                /*
                 * If the new b was removed before the original one,
                 * we take its replacement as the destination bucket.
                 */
                b = b_rep.r;
                b_rep = R.get( b );
            }

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
        final int b = l;

        /*
         * If there are no removed buckets we enlarge the size
         * of the b-array.
         */
        if( R.isEmpty() )
        {

            /* We must update the underlying engine accordingly. */
            engine.addBucket();

            l = n = n + 1;
            return b;
            
        }

        /* Otherwise, we must restore the last removed bucket. */
        
        /* We remove the replacement entry for b from the replacement set. */
        final Replacement b_rep = R.rem( b );

        /* The last removed bucket is now the last bucket removed before b. */
        l = b_rep.p;

        /* The number of working buckets after removing b. */
        final int w_b = b_rep.w;

        /*
         * The next bucket in the replacement chain.
         * By construction, n_b and w_b have always the same value.
         */
        final int n_b = w_b;

        /* We remove b from the replacement chain. */
        I.rem( n_b );

        /*
         * In some edge cases, b can be replaced by itself.
         * If this is the case, nothing needs to be done.
         * Otherwise, we check if b replaces some other
         * bucket removed previously.
         */
        if( b != b_rep.r )
        {

            /*
             * For every failed bucket x previous than b
             * in the replacement chain, we replace r_x
             * with b.
             */
            Pointer p = I.get( b );
            while( p != null )
            {
                final int x = p.value;
                R.get( x ).r = b;
                p = I.get( x );
            }

        }

        return b;

    }


    /**
     * Removes the given bucket from the engine.
     * 
     * @param b the bucket to remove
     * @return the removed bucket
     */
    @Override
    public int removeBucket( int b )
    {
        
        /*
         * If the lookup table is empty and the bucket to remove is the last one,
         * we just need to reduce the size of the b-array.
         */
        if( b == n - 1 && R.isEmpty() )
        {

            /* We must update the underlying engine accordingly. */
            engine.removeBucket( b );

            l = n = b;
            return b;

        }
        
        /* Otherwise, we need to remember the removed bucket. */

        /* The number of working buckets before removing b. */
        final int w = n - R.size();

        /* The number of working buckets after removing b. */
        final int w_b = w - 1;

        /*
         * The next bucket in the replacement chain.
         * By construction, n_b and w_b have always the same value.
         */
        final int n_b = w_b;

        /* 
         * We check if the next bucket in the replacement chain
         * has been removed in a previous iteration.
         */
        final Replacement n_b_rep = R.get( n_b );

        /*
         * If n_b is working, it becomes the replacement of b.
         * Otherwise, we replace b with the replacement of n_b
         * (which is assumed to be working).
         */
        final int r_b = n_b_rep != null ? n_b_rep.r : n_b;
        
        /* We create the replacement for b and add it to R. */
        R.add( new Replacement(b, r_b, w_b, l) );

        /* We remember that n_b is next to b in the replacement chain. */
        I.add( new Pointer( n_b, b ) );

        /*
         * In some edge cases, b can be replaced by itself.
         * If this is the case, nothing needs to be done.
         * Otherwise, we check if b replaces some other
         * bucket removed previously.
         */
        if( b != r_b )
        {

            /*
             * For every failed bucket x previous than b
             * in the replacement chain, we replace r_x
             * (that has value b) with r_b.
             */
            Pointer p = I.get( b );
            while( p != null )
            {
                final int x = p.value;
                R.get( x ).r = r_b;
                p = I.get( x );
            }

        }

        /* The new last removed bucket is b. */
        l = b;

        return b;

    }


    /**
     * Returns the size of the working set.
     * 
     * @return size of the working set.
     */
    public int size()
    {

        return n - R.size();

    }


    /**
     * Returns the size of the b-array.
     * 
     * @return the size of the b-array.
     */
    public int bArraySize() 
    {

        return n;

    }

}