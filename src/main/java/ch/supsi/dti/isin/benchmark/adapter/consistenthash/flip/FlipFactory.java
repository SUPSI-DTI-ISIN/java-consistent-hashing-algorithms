package ch.supsi.dti.isin.benchmark.adapter.consistenthash.flip;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.flip.FlipEngine;
import ch.supsi.dti.isin.consistenthash.flip.FlipHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Flip} algorithm.
 * 
 * @author Massimo Coluzzi
 */
public class FlipFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public FlipFactory( AlgorithmConfig config )
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
    public FlipHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new FlipHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<FlipEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );


        return () -> new FlipEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public FlipEnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof FlipEngine )
            return new FlipEnginePilot( (FlipEngine) engine );

        throw ResourceLoadingException.incompatibleType( FlipEngine.class, engine.getClass() );

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
