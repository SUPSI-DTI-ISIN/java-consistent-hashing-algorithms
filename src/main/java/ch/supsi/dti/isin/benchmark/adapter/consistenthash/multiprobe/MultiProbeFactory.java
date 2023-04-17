package ch.supsi.dti.isin.benchmark.adapter.consistenthash.multiprobe;


import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeEngine;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Multi-probe} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class MultiProbeFactory extends ConsistentHashFactory
{


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public MultiProbeFactory( AlgorithmConfig config )
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
    public MultiProbeHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int probes = getProbes( config );
        return new MultiProbeHash( nodes, probes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<MultiProbeEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int probes = getProbes( config );
        return () -> {
            
            final MultiProbeEngine engine = new MultiProbeEngine( probes, hash );
            nodes.forEach( node -> engine.addResource(node.name()) );

            return engine;
            
        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MultiProbeEnginePilot createEnginePilot( HashFunction hash, Collection<? extends Node> nodes )
    {

        final MultiProbeEngine engine = createEngineInitializer( hash, nodes ).get();
        return new MultiProbeEnginePilot( engine );

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

        getProbes( config );
        return config;
        
    }

    
    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the number of probes used during the initialization of the algorithm.
     *
     * @param config custom configurations for the algorithm
     * @return number of probes
     */
    public int getProbes( AlgorithmConfig config )
    {

        if( config == null )
            return MultiProbeHash.DEFAULT_PROBES;

        final ValuePath path = config.getPath().append( "args" ).append( "probes" );
        final Object value = config.getArgs().get( "probes" );

        final int probes = value != null
        ? ConfigUtils.toInt( path, value )
        : MultiProbeHash.DEFAULT_PROBES;

        if( probes < 1 )
            throw InconsistentValueException.lessThan( path, 1, probes );

        return probes;
        
    }

}
