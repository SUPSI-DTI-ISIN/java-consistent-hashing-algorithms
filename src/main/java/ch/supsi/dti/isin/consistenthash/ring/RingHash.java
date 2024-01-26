package ch.supsi.dti.isin.consistenthash.ring;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.tuple.Pair;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link RingEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class RingHash implements ConsistentHash
{
    
    
    /**
     * The {@code RingHash} algorithm engine as described in:
     * {@code https://www.cs.princeton.edu/courses/archive/fall09/cos518/papers/chash.pdf}
     */
    private final RingEngine engine;

    /** The nodes of the cluster. */
    private final Map<String,Pair<Node,Collection<VirtualNode>>> nodeMap;


    /**
     * Constructor with parameters.
     *
     * @param nodes collection of physical nodes
     */
    public RingHash( Collection<? extends Node> nodes )
    {

        this( nodes, DEFAULT_HASH_FUNCTION );

    }


    /**
     * Constructor with parameters.
     *
     * @param nodes        collection of physical nodes
     * @param hashFunction hash Function to hash Node instances
     */
    public RingHash( Collection<? extends Node> nodes, HashFunction hashFunction )
    {

        super();

        this.nodeMap = new HashMap<>();
        this.engine  = new RingEngine( hashFunction );

        this.addNodes( nodes );

    }


    /**
     * Constructor with parameters.
     *
     * @param nodes        collection of physical nodes
     * @param vNodeCount   number of virtual nodes for any physical node
     * @param hashFunction hash Function to hash Node instances
     */
    public RingHash( Collection<? extends Node> nodes, int vNodeCount, HashFunction hashFunction )
    {

        super();

        this.nodeMap = new HashMap<>();
        this.engine  = new RingEngine( vNodeCount, hashFunction );

        this.addNodes( nodes );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode( String key )
    {

        final String node = engine.getNode(
            Require.nonEmpty( key, "The key to evaluate is mandatory" )
        );

        final Pair<Node,?> pair = Require.nonNull(
            nodeMap.get(node),
            () -> "Expected physical node with name " + node + " but it does not exist"
        );

        return pair.getLeft();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNodes( Collection<? extends Node> toAdd )
    {

        Require.nonEmpty( toAdd, "The resources to add are mandatory" );
        for( Node node : toAdd )
        {

            final String pNode = Require.nonNull( node, "The resource to add cannot be null" ).name();
            Require.toHold( ! nodeMap.containsKey(pNode), () -> "Resource '" + node + "' already exists" );

            final Collection<VirtualNode> vNodes = engine.addNode( pNode );
            final Pair<Node,Collection<VirtualNode>> pair = Pair.of( node, vNodes );

            nodeMap.put( pNode, pair );

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNodes( Collection<? extends Node> toRemove )
    {

        Require.nonEmpty( toRemove, "The resources to remove are mandatory" );
        Require.toHold( nodeCount() > toRemove.size(), "Trying to remove more resources than available" );

        for( Node node : toRemove )
        {

            final String pNode = Require.nonNull( node, "The resource to remove is mandatory" ).name();
            final Pair<?,Collection<VirtualNode>> pair = Require.nonNull(
                nodeMap.get( pNode ),
                () -> "Resource '" + node + "' does not exist"
            );
            
            engine.removeNodes( pair.getRight() );
            nodeMap.remove( pNode );

        }

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean supportsOnlyLifoRemovals()
    {

        return false;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeCount()
    {

        return nodeMap.size();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object engine()
    {

        return engine;

    }

}
