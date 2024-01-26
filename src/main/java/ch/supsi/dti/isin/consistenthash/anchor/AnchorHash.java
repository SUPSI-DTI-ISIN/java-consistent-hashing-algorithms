package ch.supsi.dti.isin.consistenthash.anchor;

import java.util.Collection;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Indirection;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link AnchorEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class AnchorHash implements ConsistentHash
{

    /**
     * The {@code AnchorHash} algorithm engine as described in:
     * {@code https://arxiv.org/pdf/1812.09674.pdf}
     */
    private final AnchorEngine engine;

    /** One-to-one mapping between a node and the related bucket. */
    private final Indirection indirection;


    /**
     * Constructor with parameters.
     * 
     * @param initNodes     nodes used to initializa the cluster
     * @param capacity      overall capacity of the cluster (max number of nodes)
     */
    public AnchorHash( Collection<? extends Node> initNodes, int capacity )
    {

        this( initNodes, capacity, DEFAULT_HASH_FUNCTION );

    }
    
    /**
     * Constructor with parameters.
     * 
     * @param initNodes     nodes used to initializa the cluster
     * @param capacity      overall capacity of the cluster (max number of nodes)
     * @param hashFunction  hash function to use 
     */
    public AnchorHash( Collection<? extends Node> initNodes, int capacity, HashFunction hashFunction )
    {

        super();

        final int size = Require.nonEmpty( initNodes, "The cluster must have at least one node" ).size();
        Require.toHold( size <= capacity, "The cluster overall capacity cannot be smaller than the number of working nodes" );
        
        this.engine = new AnchorEngine(
            size, capacity,
            Require.nonNull( hashFunction, "The hash function to use is mandatory" )
        );

        this.indirection = new Indirection( capacity );

        int bucket = 0;
        for( Node node : initNodes )
            indirection.put( node, bucket++ );
        
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
