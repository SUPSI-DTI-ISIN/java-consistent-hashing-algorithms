package ch.supsi.dti.isin.benchmark.adapter.consistenthash.rendezvous;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousEngine;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Rendezvous} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class RendezvousFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public RendezvousFactory( AlgorithmConfig config )
    {

        super( config );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public RendezvousHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new RendezvousHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<RendezvousEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return () -> {
            
            final RendezvousEngine engine = new RendezvousEngine( hash );
            nodes.forEach( node -> engine.addResource(node.name()) );

            return engine;
            
        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RendezvousEnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof RendezvousEngine )
            return new RendezvousEnginePilot( (RendezvousEngine) engine );

        throw ResourceLoadingException.incompatibleType( RendezvousEngine.class, engine.getClass() );

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    protected AlgorithmConfig validate( AlgorithmConfig config )
    {

        return config;
        
    }

}
