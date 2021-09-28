package ch.supsi.dti.isin.consistenthash.ring;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;

import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Implementation of the {@code RingHash} algorithm as described in the related paper:
 * {@code https://www.cs.princeton.edu/courses/archive/fall09/cos518/papers/chash.pdf}
 *
 * <p>
 * <b>IMPORTANT:</b>
 * This class is not performing any consistency check
 * to avoid the performance tests to be falsified.
 *
 * @author Massimo Coluzzi
 */
public class RingEngine
{

    /** Default number of virtual nodes to use for any physical node. */
    private static final int DEFAULT_VIRTUAL_NODE_COUNT = 1000;


    /** Internal representation of the consistent hashing key ring. */
    private final SortedMap<Long, VirtualNode> ring;

    /** The number of virtual node, by default {@link RingHash#DEFAULT_VIRTUAL_NODE_COUNT} */
    private final int vNodeCount;

    /** Hashing function to use. */
    private final HashFunction hashFunction;


    /**
     * Constructor with parameters.
     *
     * @param hashFunction hash Function to hash Node instances
     */
    public RingEngine( HashFunction hashFunction )
    {

        this( DEFAULT_VIRTUAL_NODE_COUNT, hashFunction );

    }


    /**
     * Constructor with parameters.
     *
     * @param vNodeCount   number of virtual nodes for any physical node
     * @param hashFunction hash Function to hash Node instances
     */
    public RingEngine( int vNodeCount, HashFunction hashFunction )
    {

        super();

        this.ring = new TreeMap<>();
        this.vNodeCount = vNodeCount;
        this.hashFunction = hashFunction;
        
    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Returns the physical node associated to the given key.
     * 
     * @param key the key to check
     * @return the related node
     */
    public String getNode( String key )
    {

        final long keyHash = hashFunction.hash( key );
        final SortedMap<Long, VirtualNode> tailMap = ring.tailMap( keyHash );

        final Long nodeHash
            = ! tailMap.isEmpty()
            ? tailMap.firstKey()
            : ring.firstKey();

        return ring.get( nodeHash ).physicalNode;

    }

    /**
     * Adds a new physical node to the cluster and returns the list
     * of virtual nodes associated with the given phisical node.
     * 
     * @param node the physical node to add
     * @return the collection of related virtual nodes
     */
    public Collection<VirtualNode> addNode( String pNode )
    {

        final List<VirtualNode> vNodes = new LinkedList<>();
        for( int i = 0; i < vNodeCount; i++ )
        {

            
            long hash = hashFunction.hash( pNode, i );
            while( ring.containsKey(hash) )
                hash = hashFunction.hash( hash, i );
            
            final VirtualNode vNode = new VirtualNode( pNode, hash );
            ring.put( hash, vNode );
            vNodes.add( vNode );

        }

        return vNodes;

    }

    /**
     * Removes the given list of virtual nodes from the ring.
     * 
     * @param toRemove collection of virtual nodes to remove
     */
    public void removeNodes( Collection<VirtualNode> toRemove )
    {

        for( VirtualNode vNode : toRemove )
            ring.remove( vNode.hash );

    }

    /**
     * Returns the number of virtual nodes.
     * 
     * @return number of virtual nodes.
     */
    public int virtualNodesCount()
    {

        return ring.size();

    }

    /**
     * Applies the given anction for each pair
     * {@code <hash,virtual-node>} in the ring.
     * 
     */
    public void forEach( BiConsumer<Long,VirtualNode> consumer )
    {

        ring.forEach( consumer );

    }

}
