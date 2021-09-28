package ch.supsi.dti.isin.consistenthash.multiprobe;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of the {@code MultiProbeHash} algorithm as described in the related paper:
 * {@code https://arxiv.org/pdf/1505.00062.pdf}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 * 
 * @author Massimo Coluzzi
 * @author Davide Bertacco
 */
public class MultiProbeEngine
{

    /** Common default seed to use during hashing of the nodes. */
    private static final int  SEED = 0xDEADBEEF;

    /** Internal representation of the consistent hashing key ring. */
    private final List<Point> ring;

    /** The number of probes, by default {@link MultiProbeHash#DEFAULT_NUM_PROBES} */
    private final int numProbes;

    /** The hashing function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     *
     * @param numProbes Number of probes need to perform. The higher the number is, the more balanced
     *                  the hash ring is.
     * @param hashFunction the hash function to use.
     */
    public MultiProbeEngine( int numProbes, HashFunction hashFunction )
    {

        super();

        this.numProbes =numProbes;
        this.hashFunction = hashFunction;

        this.ring = new ArrayList<>();

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the resource related to the given key.
     * 
     * @param key the key to map
     * @return the related resource
     */
    public String getResource( String key )
    {

        final int index = getIndex( key );
        return ring.get( index ).resource;

    }

    /**
     * Adds the given resource to the ring.
     * 
     * @param resource the resource to add
     */
    public void addResource( String resource )
    {

        final Point bucket = wrap( resource );
        final int pos = Collections.binarySearch( ring, bucket );
            
        final int index = -(pos + 1);
        ring.add( index, bucket );

    }

    /**
     * Removes the given resource from the ring.
     * 
     * @param resource the resource to remove
     */
    public void removeResource( String resource )
    {

        final Point bucket = wrap( resource );
        final int pos = Collections.binarySearch( ring, bucket );
            
        ring.remove( pos );
        
    }

    /**
     * Returns the size of the ring.
     * 
     * @return the size of the ring
     */
    public int size()
    {

        return ring.size();
        
    }


    /* ***************** */
    /*  DEFAULT METHODS  */
    /* ***************** */


    /**
     * Streams the points in the ring.
     * 
     * @return stream of the poins in the ring
     */
    Stream<Point> streamRing()
    {

        return ring.stream();

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Wraps the given resource into a point in the ring.
     * 
     * @param node the resource to wrap
     * @return the related point in the ring
     */
    private Point wrap( String resource )
    {

        final long hash = hashFunction.hash( resource, SEED );
        return new Point( resource, hash );

    }

    /**
     * Computes the index of the point related to the given key.
     * 
     * @param key key to search
     * @return index of the related point
     */
    private int getIndex( String key )
    {

        int index = 0;
        long minDistance = Long.MAX_VALUE;
        for( int i = 0; i < numProbes; i++ )
        {

            final long hash = hashFunction.hash( key, i );

            int low = 0;
            int high = ring.size();
            while( low < high )
            {

                final int mid = (low + high) >>> 1;
                if( ring.get(mid).hash > hash )
                    high = mid;
                else
                    low = mid + 1;

            }

            /*
             * This check implements the concept of ring.
             * If we exceed the last we start over.
             */
            if (low >= ring.size())
                low = 0;

            final long distance = ring.get( low ).distance( hash );
            if( distance < minDistance )
            {
                minDistance = distance;
                index = low;
            }

        }

        return index;
        
    }

}
