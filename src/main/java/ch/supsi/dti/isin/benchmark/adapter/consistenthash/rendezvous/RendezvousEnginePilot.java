package ch.supsi.dti.isin.benchmark.adapter.consistenthash.rendezvous;


import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.NameBasedEnginePilot;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Rendezvous} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class RendezvousEnginePilot extends NameBasedEnginePilot
{

    /** The engine to pilot. */
    private final RendezvousEngine engine;


    /**
     * Constructor with parameters.
     *
     * @param engine the consistent hash engine to pilot
     */
    public RendezvousEnginePilot( RendezvousEngine engine )
    {
        
        super( "resource" );
        
        this.engine = Require.nonNull( engine, "The engine to pilot is mandatory" );

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

        return engine.getResource( key );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addNode()
    {

        final String resource = getName();
        engine.addResource( resource );
        
        return resource;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode( String resource )
    {

        engine.removeResource( resource );

    }

}
