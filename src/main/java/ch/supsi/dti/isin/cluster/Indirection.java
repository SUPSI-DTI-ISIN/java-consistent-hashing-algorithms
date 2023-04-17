package ch.supsi.dti.isin.cluster;

import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.RequirementFailure;


/**
 * Represents a one-to-one mapping between a {@code Node}
 * and a bucket. This class ensures that there is only
 * one node per bucket and vice versa.
 * 
 * <p>
 * This class also performs all the consistency checks:
 * <ul>
 *  <li>The node to map cannot be {@code null}.</li>
 *  <li>The bucket must be in the interval {@code [0,N]}.</li>
 *  <li>Two nodes cannot be mapped to the same bucket.</li>
 *  <li>Two buckets cannot be mapped to the same node.</li>
 * </ul>
 * 
 * @author Massimo Coluzzi
 */
public class Indirection
{

    /** Maps each node to the related bucket. */
    private Map<Node,Integer> nodeToBucket;

    /** Maps each bucket to the related node. */
    private Map<Integer,Node> bucketToNode;

    /** The upper bound of the interval of allowed buckets. */
    private int capacity;


    /**
     * Constructor with parameters.
     * 
     * @param size the initial size of the indirection
     */
    public Indirection( int size )
    {

        super();

        Require.toHold( size >= 0, "The initial size cannot be negative" );
        this.nodeToBucket = new HashMap<>( size );
        this.bucketToNode = new HashMap<>( size );
        this.capacity     = 0;

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Adds the new mapping between the given node and bucket.
     * 
     * @param node   the node to map
     * @param bucket the bucket to map
     * @throws RequirementFailure if consistency checks fail
     */
    public void put( Node node, int bucket )
    {

        Require.nonNull( node, "The node to add cannot be null" );
        Require.toHold(
            bucket >= 0 && bucket <= capacity,
            () -> "The bucket must be in the interval [0," + capacity + "] but was " + bucket
        );

        Require.toHold( ! nodeToBucket.containsKey(node), () -> "Duplicated node " + node );
        Require.toHold( ! bucketToNode.containsKey(bucket), () -> "Duplicated bucket " + bucket );

        nodeToBucket.put( node, bucket );
        bucketToNode.put( bucket, node );

        if( bucket == capacity )
            ++capacity;

    }

    /**
     * Returns the bucket mapped to the given node, if any.
     * If the given node is {@code null} or the mapping does
     * not exist a {@linkplain RequirementFailure} will be
     * thrown.
     * 
     * @param node the node to search for
     * @return the bucket related to the given node
     * @throws RequirementFailure if the bucket cannot be found
     */
    public int get( Node node )
    {

        final Integer bucket = nodeToBucket.get(
            Require.nonNull( node, "The node to search for cannot be null" )
        );

        return Require.nonNull( bucket, () -> "Node " + node + " is not mapped to any bucket" ).intValue();
        
    }

    /**
     * Returns the node mapped to the given bucket, if any.
     * If the given bucket is not in range or the mapping does
     * not exist a {@linkplain RequirementFailure} will be
     * thrown.
     * 
     * @param bucket the bucket to search for
     * @return the node related to the given bucket
     * @throws RequirementFailure if the node cannot be found
     */
    public Node get( int bucket )
    {

        Require.toHold(
            bucket >= 0 && bucket < capacity,
            () -> "The bucket must be in the interval [0," + capacity + ") but was " + bucket
        );
        
        final Node node = bucketToNode.get( bucket );
        return Require.nonNull( node, () -> "Bucket " + bucket + " is not mapped to any node" );

    }

    /**
     * Removes the mapping related to the given node.
     * 
     * @param node the node to remove
     * @return the removed bucket
     * @throws RequirementFailure if the mapping cannot be found
     */
    public int remove( Node node )
    {

        final int bucket = get( node );

        nodeToBucket.remove( node );
        bucketToNode.remove( bucket );
        
        if( bucket == capacity - 1 )
            resizeCapacity();

        return bucket;

    }

    /**
     * Removes the mapping related to the given bucket.
     * 
     * @param bucket the bucket to remove
     * @return the removed node
     * @throws RequirementFailure if the mapping cannot be found
     */
    public Node remove( int bucket )
    {

        final Node node = get( bucket );

        nodeToBucket.remove( node );
        bucketToNode.remove( bucket );

        if( bucket == capacity - 1 )
            resizeCapacity();

        return node;
        
    }

    /**
     * Returns the number of mappings in the indirection.
     * 
     * @return number of mappings
     */
    public int size()
    {

        return nodeToBucket.size();

    }

    /**
     * Returns the capacity of the indirection.
     * 
     * @return capacoty
     */
    public int capacity()
    {

        return capacity;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Reduces the value of the filed {@link #capacity}
     * to fit the actual capacity.
     * 
     */
    private void resizeCapacity()
    {

        do{

            --capacity;

        }while( capacity > 0 && ! bucketToNode.containsKey(capacity - 1) );

    }

}
