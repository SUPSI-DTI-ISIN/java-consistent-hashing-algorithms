package ch.supsi.dti.isin.consistenthash.rendezvous;

import java.util.HashSet;
import java.util.Set;

import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code RendezvousHash} algorithm as described in the related paper:
 * {@code https://ieeexplore.ieee.org/abstract/document/663936}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 * 
 * 
 * @author Massimo Coluzzi
 */
public class RendezvousEngine
{

    /** The hashing function to use. */
    private final HashFunction hashFunction;

    /** Resources of the cluster. */    
    private final Set<String> resources;


    /**
     * Constructor with parameters.
     * 
     * @param hashFunction the hashing function to use
     */
    public RendezvousEngine( HashFunction hashFunction )
    {

        super();

        this.hashFunction = hashFunction;
        this.resources    = new HashSet<>();

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Returns the resource related to the given key.
     * 
     * @param key the key to map
     * @return the related resource
     */
    public String getResource( String key )
    {

        String selected = null;
        long maxHash = Long.MIN_VALUE;

        for( String resource : resources )
        {

            final long hash = hashFunction.hash( key + resource );
            if( hash > maxHash )
            {
                selected = resource;
                maxHash = hash;
            }

        }

        return selected;

    }

    /**
     * Adds the given resource to the ring.
     * 
     * @param resource the resource to add
     * @return {@code true} if the resource was added
     */
    public boolean addResource( String resource )
    {

        return resources.add( resource );

    }

    /**
     * Removes the given resource from the ring.
     * 
     * @param resource the resource to remove
     * @return {@code true} if the resource was removed
     */
    public boolean removeResource( String resource )
    {

        return resources.remove( resource );

    }

    /**
     * Returns the number of resources.
     * 
     * @return the number of resources
     */
    public int size()
    {

        return resources.size();
        
    }
    
}
