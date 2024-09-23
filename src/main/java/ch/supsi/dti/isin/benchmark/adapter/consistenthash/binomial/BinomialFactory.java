package ch.supsi.dti.isin.benchmark.adapter.consistenthash.binomial;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ResourceLoadingException;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.consistenthash.binomial.BinomialEngine;
import ch.supsi.dti.isin.consistenthash.binomial.BinomialHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Binomial} algorithm.
 * 
 * @author Massimo Coluzzi
 */
public class BinomialFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public BinomialFactory( AlgorithmConfig config )
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
    public BinomialHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        return new BinomialHash( nodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<BinomialEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );


        return () -> new BinomialEngine( nodes.size(), hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BinomialEnginePilot createEnginePilot( ConsistentHash consistentHash )
    {

        final Object engine = Require.nonNull(
            consistentHash, "The consistent hash to pilot is mandatory"
        ).engine();
        
        if( engine instanceof BinomialEngine )
            return new BinomialEnginePilot( (BinomialEngine) engine );

        throw ResourceLoadingException.incompatibleType( BinomialEngine.class, engine.getClass() );

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
