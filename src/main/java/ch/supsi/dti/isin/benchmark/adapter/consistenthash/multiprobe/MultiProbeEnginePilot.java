package ch.supsi.dti.isin.benchmark.adapter.consistenthash.multiprobe;


import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeEngine;
import ch.supsi.dti.isin.benchmark.adapter.NameBasedEnginePilot;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Multi-probe} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class MultiProbeEnginePilot extends NameBasedEnginePilot
{


    /** The engine to pilot. */
    private final MultiProbeEngine engine;

    
    /**
     * Constructor with parameters.
     *
     * @param engine the consistent hash engine to pilot
     */
    public MultiProbeEnginePilot( MultiProbeEngine engine )
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
