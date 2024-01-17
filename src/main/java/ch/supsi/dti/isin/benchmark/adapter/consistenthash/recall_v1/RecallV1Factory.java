package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall_v1;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.recall_v1.RecallV1Engine;
import ch.supsi.dti.isin.consistenthash.recall_v1.RecallV1Hash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Recall} algorithm.
 * 
 * 
 * @author Massimo Coluzzi
 */
public class RecallV1Factory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public RecallV1Factory( AlgorithmConfig config )
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
    public RecallV1Hash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new RecallV1Hash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<RecallV1Engine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return () -> new RecallV1Engine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecallV1EnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof RecallV1Engine )
            return new RecallV1EnginePilot( (RecallV1Engine) engine );

        throw ResourceLoadingException.incompatibleType( RecallV1Engine.class, engine.getClass() );

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
