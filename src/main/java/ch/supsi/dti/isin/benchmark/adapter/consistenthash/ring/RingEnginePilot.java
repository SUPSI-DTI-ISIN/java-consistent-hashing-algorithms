package ch.supsi.dti.isin.benchmark.adapter.consistenthash.ring;

import java.util.Collection;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.ring.RingEngine;
import ch.supsi.dti.isin.consistenthash.ring.VirtualNode;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Ring} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class RingEnginePilot implements ConsistentHashEnginePilot<Collection<VirtualNode>>
{
    

    /** Prefix to add to node names. */
    private static final String PREFIX = "node_";


    /** The engine to pilot. */
    private final RingEngine engine;
    
    /** Index to append to node names. */
    private long id = 0;


    /**
     * Constructor with parameters.
     *
     * @param engine the consistent hash engine to pilot
     */
    public RingEnginePilot( RingEngine engine )
    {

        super();

        this.engine = Require.nonNull( engine, "The engine to pilot is mandatory" );
        this.id = 0;

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public String getNode( String key )
    {

        return engine.getNode( key );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Collection<VirtualNode> addNode()
    {

        final String node = PREFIX + id++;
        return engine.addNode( node );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode( Collection<VirtualNode> virtualNodes )
    {

        engine.removeNodes( virtualNodes );

    }

}
