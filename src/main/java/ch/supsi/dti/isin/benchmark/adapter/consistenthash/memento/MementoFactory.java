package ch.supsi.dti.isin.benchmark.adapter.consistenthash.memento;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.memento.MementoEngine;
import ch.supsi.dti.isin.consistenthash.memento.MementoHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Memento} algorithm.
 * 
 * 
 * @author Massimo Coluzzi
 */
public class MementoFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public MementoFactory( AlgorithmConfig config )
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
    public MementoHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new MementoHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<MementoEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return () -> new MementoEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MementoEnginePilot createEnginePilot( HashFunction hash, Collection<? extends Node> nodes )
    {

        final MementoEngine engine = createEngineInitializer( hash, nodes ).get();
        return new MementoEnginePilot( engine );

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
