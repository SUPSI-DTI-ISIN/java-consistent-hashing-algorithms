package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jump;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.jump.JumpEngine;
import ch.supsi.dti.isin.consistenthash.jump.JumpHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Jump} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class JumpFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public JumpFactory( AlgorithmConfig config )
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
    public JumpHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new JumpHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<JumpEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );


        return () -> new JumpEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JumpEnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof JumpEngine )
            return new JumpEnginePilot( (JumpEngine) engine );

        throw ResourceLoadingException.incompatibleType( JumpEngine.class, engine.getClass() );

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
