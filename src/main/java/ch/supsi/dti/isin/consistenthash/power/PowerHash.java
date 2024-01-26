package ch.supsi.dti.isin.consistenthash.power;

import java.util.Collection;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Indirection;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link PowerEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class PowerHash implements ConsistentHash
{


    /**
     * The {@code PowerHash} algorithm engine as described in:
     * {@code https://arxiv.org/pdf/2307.12448.pdf}
     */
    private final PowerEngine engine;

    /** One-to-one mapping between a node and the related bucket. */
    private final Indirection indirection;


    /**
     * Constructor with parameters.
     * 
     * @param nodes initial cluster nodes
     */
    public PowerHash( Collection<? extends Node> nodes )
    {

        this( nodes, DEFAULT_HASH_FUNCTION );

    }

    /**
     * Constructor with parameters.
     * 
     * @param initNodes    initial cluster nodes
     * @param hashFunction the hash function to use
     */
    public PowerHash( Collection<? extends Node> initNodes, HashFunction hashFunction )
    {

        super();

        final int size = Require.nonEmpty( initNodes, "The cluster must have at least one node" ).size();
        
        this.indirection = new Indirection( size );

        for( Node node : initNodes )
        {
            
            Require.nonNull( node, "The resource to add cannot be null" );

            final int bucket = indirection.size();
            indirection.put( node, bucket );

        }

        this.engine = new PowerEngine(
            size,
            Require.nonNull( hashFunction, "The hash function to use is mandatory" )
        );

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

        final int bucket = engine.getBucket(
            Require.nonEmpty( key, "The key to evaluate is mandatory" )
        );

        return indirection.get( bucket );
        
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

            final int bucket = engine.size();
            this.indirection.put( node, bucket );
            engine.addBucket();

        }
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNodes( Collection<? extends Node> toRemove )
    {

        Require.nonEmpty( toRemove, "The resources to remove are mandatory" );
        Require.toHold( engine.size() > toRemove.size(), "Trying to remove more resources than available" );

        for( Node node : toRemove )
        {

            final int bucket = indirection.remove( node );
            Require.toHold( bucket == engine.size() - 1, "Only the last inserted node can be removed" );
            engine.removeBucket( bucket );
            
        }

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean supportsOnlyLifoRemovals()
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
