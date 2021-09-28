package ch.supsi.dti.isin.consistenthash.maglev;

import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Represents the permutation table of a node.
 * 
 * @author Massimo Coluzzi
 */
class Permutation
{

    /** Seed used to compute the state offset. */
    private static final int  OFFSET_SEED = 0xDEADBABE;

    /** Seed used to compute the state skip. */
    private static final int  SKIP_SEED = 0xDEADDEAD;
    

    /** The backend associated to the permutation. */
    private final String backend;

    /** The size of the lookup table. */
    private final int size;

    /** Position where to start. */
    private final int offset;

    /** Positions to skip. */
    private final int skip;


    /** The current value of the permutation. */
    private int current;


    /**
     * Constructor with parameters.
     * 
     * @param backend       the backend to wrap
     * @param hashFunction  the hash function to use
     * @param size          size of the lookup table
     */
    Permutation( String backend, HashFunction hashFunction, int size )
    {

        super();

        this.size    = size;
        this.backend = backend;

        this.offset  = (int)(hashFunction.hash(backend,OFFSET_SEED) % size);
        this.skip    = (int)(hashFunction.hash(backend, SKIP_SEED) % (size-1) + 1 );

        this.current = offset;
       
    }

    /**
     * Returns the backend related to the current permutation.
     * 
     * @return the backend related to the current permutation
     */
    String backend()
    {

        return backend;

    }

    /**
     * Returns the next value in the permutation.
     * 
     * @return the next value
     */
    int next()
    {

        final int current = this.current;
        this.current = (current + skip) % size;

        return current;

    }

    /**
     * Resets the permutation for the new lookup size.
     * 
     */
    void reset()
    {

        this.current = offset;
        
    }

}