package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jumpback;


import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.jumpback.JumpBackEngine;
import ch.supsi.dti.isin.consistenthash.jumpback.JumpBackHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import org.nerd4j.utils.lang.Require;

import java.util.Collection;
import java.util.function.Supplier;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code JumpBackHash} algorithm.
 */
public class JumpBackFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public JumpBackFactory(AlgorithmConfig config )
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
    public JumpBackHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new JumpBackHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<JumpBackEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );


        return () -> new JumpBackEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public JumpBackEnginePilot createEnginePilot(ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof JumpBackEngine )
            return new JumpBackEnginePilot( (JumpBackEngine) engine );

        throw ResourceLoadingException.incompatibleType( JumpBackEngine.class, engine.getClass() );

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
