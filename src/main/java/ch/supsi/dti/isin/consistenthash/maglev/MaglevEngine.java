package ch.supsi.dti.isin.consistenthash.maglev;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code MaglevHash} algorithm as described in the related paper:
 * {@code https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 * @author Davide Bertacco
 */
public class MaglevEngine
{

    /** Seed used to compute the lookup index. */
    private static final int  INDEX_SEED = 0xDEADBEEF;

    
    /**
     * As described in the related paper the lookup table size
     * should be a prime number and it should be much bigger
     * than the number of nodes (lookupSize >> maxNodes ).
     */
    private final int lookupSize;

    /** The hash function to use. */
    private final HashFunction hashFunction;

    /**
     * The lookup table as described in:
     * {@code https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf}
     */
    private String[] lookup;

    /** Maps each backend to the related permutation. */
    private Map<String,Permutation> permutations;


    /**
     * Constructor with parameters.
     * 
     * @param lookupSize size of the lookup table
     * @param hashFunction the hash function to use
     */
    public MaglevEngine( int lookupSize, HashFunction hashFunction )
    {

        super();

        this.lookupSize   = lookupSize;
        this.hashFunction = hashFunction;

        this.lookup       = new String[0];
        this.permutations = new HashMap<>();
        
    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the backend where the given key should be mapped.
     * 
     * @param key the key to map
     * @return the related backend
     */
    public String getBackend( String key )
    {

        final int index = (int)( hashFunction.hash(key, INDEX_SEED) % lookup.length );
        return lookup[index];

    }

    /**
     * Adds the given collection of backends to the lookup table.
     * 
     * @param toAdd backends to add
     */
    public void addBackends( Collection<String> toAdd )
    {
        
        permutations.values().forEach( Permutation::reset );
        for( String backend : toAdd )
            permutations.put( backend, newPermutation(backend) );

        this.lookup = newLookup();
            
    }

    /**
     * Removes the given collection of backends from the lookup table.
     * 
     * @param toRemove backends to remove
     */
    public void removeBackends( Collection<String> toRemove )
    {

        toRemove.forEach( permutations::remove );
        permutations.values().forEach( Permutation::reset );

        this.lookup = newLookup();

    }
        
    /**
     * Returns the number of backends.
     * 
     * @return the number of backends
     */
    public int size()
    {

        return permutations.size();

    }

    /**
     * Returns the size of the lookup table.
     * 
     * @return the size of the lookup table
     */
    public int lookupSize()
    {

        return lookupSize;

    }


    /* ***************** */
    /*  DEFAULT METHODS  */
    /* ***************** */


    /**
     * Returns a stream with the content of the lookup table.
     * 
     * @return stream of the lookup table entries
     */
    Stream<String> streamLookupEntries()
    {

        return Arrays.stream( lookup );
        
    }

    
    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Creates a new permutation for the given backend.
     * 
     * @param backend the source of the permutation
     * @return a new permutation
     */
    private Permutation newPermutation( String backend )
    {

        return new Permutation( backend, hashFunction, lookupSize );

    }

    /**
     * Creates a new lookup table.
     * 
     * @return the new lookup table
     */
    private String[] newLookup()
    {

        final String[] lookup = new String[lookupSize];
        final AtomicInteger filled = new AtomicInteger();

        do {

            permutations.values().forEach( permutation ->
            {

                final int pos = permutation.next();

                if( lookup[pos] == null )
                { //found

                    lookup[pos] = permutation.backend();
                    if( filled.incrementAndGet() >= lookupSize )
                        return;
                }

            });

        }while( filled.get() < lookupSize );

        return lookup;

    }

}
