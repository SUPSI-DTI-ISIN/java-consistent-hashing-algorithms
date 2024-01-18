package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.recall.RecallEngine;
import ch.supsi.dti.isin.consistenthash.recall.RecallHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Recall} algorithm.
 * 
 * 
 * @author Massimo Coluzzi
 */
public class RecallFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public RecallFactory( AlgorithmConfig config )
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
    public RecallHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new RecallHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<RecallEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return () -> new RecallEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RecallEnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof RecallEngine )
            return new RecallEnginePilot( (RecallEngine) engine );

        throw ResourceLoadingException.incompatibleType( RecallEngine.class, engine.getClass() );

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
