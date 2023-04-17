package ch.supsi.dti.isin.consistenthash.multiprobe;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link MultiProbeEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class MultiProbeHash implements ConsistentHash
{

    /**
     * As stated in; {@code https://arxiv.org/pdf/1505.00062.pdf}
     * the number of probes to get the best balance between
     * performance and peak-to-average load ratio is 21.
     */
    public static final int DEFAULT_PROBES = 21;

    /**
     * The {@code MaglevHash} algorithm engine as described in:
     * {@code https://arxiv.org/pdf/1505.00062.pdf}
     */
    private final MultiProbeEngine engine;

    /** The nodes of the cluster. */
    private final Map<String,Node> nodeMap;


    /**
     * Constructor with parameters.
     * 
     * @param nodes list of initial nodes
     */
    public MultiProbeHash( Collection<? extends Node> nodes )
    {

        this( nodes, DEFAULT_PROBES, DEFAULT_HASH_FUNCTION );

    }

    /**
     * Constructor with parameters.
     * 
     * @param nodes list of initial nodes
     * @param hashFunction the hash function to use
     */
    public MultiProbeHash( Collection<? extends Node> nodes, HashFunction hashFunction )
    {

        this( nodes, DEFAULT_PROBES, hashFunction );

    }

    /**
     * Creates a multi-probe consistent hash ring with given points map and number of probes.
     *
     * @param nodes        list of initial nodes
     * @param probes       number of probes
     * @param hashFunction the hash function to use.
     */
    public MultiProbeHash( Collection<? extends Node> nodes, int probes, HashFunction hashFunction )
    {

        super();
        
        
        Require.nonEmpty( nodes, "The cluster must have at least one node" );
        
        this.engine = new MultiProbeEngine(
            Require.trueFor( probes, probes > 0, "The number of probes must be strictly positive" ),
            Require.nonNull( hashFunction, "The hash function to use is mandatory" )
        );

        this.nodeMap = new HashMap<>( nodes.size() );
        for( Node node : nodes )
        {

            Require.nonNull( node, "The resource to add cannot be null" );
            Require.toHold( nodeMap.put(node.name(),node) == null, "Duplicated resource " + node );
            
            this.engine.addResource( node.name() );

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

        final String resource = engine.getResource(
            Require.nonEmpty( key, "The key to evaluate is mandatory" )
        );

        return Require.nonNull(
            nodeMap.get( resource ),
            () -> "Expected node with name " + resource + " but it does not exist"
        );

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
            Require.toHold( nodeMap.putIfAbsent(resource,node) == null, () -> "Resource '" + node + "' already exists" );

            engine.addResource( resource );

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

            final String resource = Require.nonNull( node, "The resource to remove cannot be null" ).name();
            Require.toHold( nodeMap.remove(resource) != null, () -> "Resource '" + node + "' does not exist" );

            engine.removeResource( resource );

            
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
