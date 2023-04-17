package ch.supsi.dti.isin.consistenthash.dx;

import java.util.Collection;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Indirection;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link DxEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class DxHash implements ConsistentHash
{

    /**
     * The {@code DxHash} algorithm engine as described in:
     * {@code https://arxiv.org/pdf/2107.07930.pdf}
     */
    private final DxEngine engine;

    /** One-to-one mapping between a node and the related bucket. */
    private final Indirection indirection;


    /**
     * Constructor with parameters.
     *
     * @param nodes    initial cluster nodes
     * @param capacity overall capacity of the cluster (max number of nodes)
     */
    public DxHash( Collection<? extends Node> nodes, int capacity )
    {

        this( nodes, capacity, DEFAULT_HASH_FUNCTION );

    }

    /**
     * Constructor with parameters.
     *
     * @param nodes        initial cluster nodes
     * @param capacity     overall capacity of the cluster (max number of nodes)
     * @param hashFunction the hash function to use
     */
    public DxHash( Collection<? extends Node> nodes, int capacity, HashFunction hashFunction )
    {

        super();

        Require.nonEmpty( nodes, "The cluster must have at least one node" );
        Require.toHold( nodes.size() <= capacity, "The cluster overall capacity cannot be smaller than the number of working nodes" );
        
        this.engine = new DxEngine(
            0, capacity, Require.nonNull( hashFunction, "The hash function to use cannot be null" )
        );

        this.indirection = new Indirection( nodes.size() );
        
        for ( Node node : nodes )
        {
            
            final int bucket = engine.addBucket();
            indirection.put( node, bucket );

        }

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

        Require.nonEmpty( key, "The key to evaluate is mandatory" );

        final int bucket = engine.getBucket( key );
        return indirection.get( bucket );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addNodes( Collection<? extends Node> toAdd )
    {

        Require.nonEmpty( toAdd, "The resources to add are mandatory" );
        Require.toHold( engine.size() + toAdd.size() <= engine.capacity(), "No room for more resources" );
        for( Node node : toAdd )
        {

            final int bucket = engine.addBucket();

            try{

                indirection.put( node, bucket );

            }catch( RuntimeException ex )
            {

                engine.removeBucket( bucket );
                throw ex;
                
            }
            
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
            engine.removeBucket( bucket );

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
    public int nodeCount() {

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
