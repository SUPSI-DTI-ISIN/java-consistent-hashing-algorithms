package ch.supsi.dti.isin.consistenthash.multiprobe;


/**
 * Represents a point inside the consistent hash ring.
 * It maps the resource to a hash code representing
 * the resource positino in the ring.
 * 
 * @author Massimo Coluzzi
 */
class Point implements Comparable<Point>
{

    /** The resource to store. */
    final String resource;

    /** The position in the consistent hash ring. */
    final long hash;


    /**
     * Constructor with parameters.
     * 
     * @param resource the resource to store.
     * @param hash     the position in the consistent hash ring
     */
    Point( String resource, long hash )
    {

        super();

        this.resource = resource;
        this.hash = hash;

    }

    /**
     * Returns the distance between the given hash
     * and the hash of the current bucket.
     * 
     * @param hash the hash to test
     * @return the related distance
     */
    long distance( long hash )
    {

        return Math.abs( this.hash - hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compareTo( Point other )
    {

        return Long.compare( this.hash, other.hash );

    }

}
