package ch.supsi.dti.isin.consistenthash.rendezvous;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link RendezvousEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class RendezvousHash implements ConsistentHash
{

    /**
     * The {@code RendezvousHash} algorithm engine as described in:
     * {@code https://ieeexplore.ieee.org/abstract/document/663936}
     */
    private final RendezvousEngine engine;

    /** The nodes of the cluster. */
    private final Map<String,Node> nodeMap;



    /**
     * Constructor with parameters.
     * 
     * @param init collection of initial nodes
     */
    public RendezvousHash( Collection<? extends Node> init )
    {

        this( init, DEFAULT_HASH_FUNCTION );

    }

    /**
     * Constructor with parameters.
     * 
     * @param initNodes    collection of initial nodes
     * @param hashFunction the hashing function to use
     */
    public RendezvousHash( Collection<? extends Node> initNodes, HashFunction hashFunction )
    {

        super();
        
        this.nodeMap = new HashMap<>(
            Require.nonEmpty( initNodes, "The cluster must have at least one node" ).size()
        );

        this.engine = new RendezvousEngine(
            Require.nonNull( hashFunction, "The hash function to use cannot be null" )
        );

        this.addNodes( initNodes );

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

        final String resource = engine.getResource(
            Require.nonEmpty( key, "The key to evaluate is mandatory" )
        );

        final Node node = nodeMap.get(
          Require.nonNull( resource, "The given key was mapped to an unexisting resource" )  
        );
        
        return Require.nonNull( node, "The given key was mapped to an unexisting node" );

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

            final String resource = Require.nonNull( node, "The resource to add cannot be null" ).name();
            Require.toHold( engine.addResource(resource), () -> "Resource '" + node + "' already exists" );

            nodeMap.put( resource, node );

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNodes( Collection<? extends Node> toRemove )
    {

        Require.nonEmpty( toRemove, "The resources to remove are mandatory" );
        Require.toHold( nodeMap.size() > toRemove.size(), "Trying to remove more resources than available" );

        for( Node node : toRemove )
        {

            final String resource = Require.nonNull( node, "The resource to remove cannot be null" ).name();
            Require.toHold( engine.removeResource(resource), () -> "Resource '" + node + "' does not exist" );

            nodeMap.remove( resource );
                
        }

    }

    /**
     * {@inheritDoc}}
     */
    public boolean supportsRandomRemovals()
    {

        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeCount()
    {

        return engine.size();

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
