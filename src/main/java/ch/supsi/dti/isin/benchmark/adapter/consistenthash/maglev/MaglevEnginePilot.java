package ch.supsi.dti.isin.benchmark.adapter.consistenthash.maglev;

import java.util.Collections;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.NameBasedEnginePilot;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Maglev} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class MaglevEnginePilot extends NameBasedEnginePilot
{

    
    /** The engine to pilot. */
    private final MaglevEngine engine;


    /**
     * Constructor with parameters.
     *
     * @param engine the consistent hash engine to pilot
     */
    public MaglevEnginePilot( MaglevEngine engine )
    {

        super( "backend" );

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

        return engine.getBackend(key);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String addNode()
    {

        final String backend = getName();
        engine.addBackends( Collections.singleton(backend) );

        return backend;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode( String node )
    {

        engine.removeBackends(Collections.singleton(node));

    }

}
