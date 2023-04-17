package ch.supsi.dti.isin.benchmark.adapter.consistenthash.ring;



import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ring.RingEngine;
import ch.supsi.dti.isin.consistenthash.ring.RingHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Ring} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class RingFactory extends ConsistentHashFactory
{


    /** The default number of virtual nodes to apply if not defined in the configuration. */
    private static final int DEFAULT_VIRTUAL_NODES = 1000;


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public RingFactory( AlgorithmConfig config )
    {

        super( config );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /*
     *
     * {@inheritDoc}
     */
    @Override
    public RingHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int virtualNodes = getVirtualNodes( config );
        return new RingHash( nodes, virtualNodes, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<RingEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int virtualNodes = getVirtualNodes( config );
        return () -> {
            
            final RingEngine engine = new RingEngine( virtualNodes, hash );
            nodes.forEach( node -> engine.addNode(node.name()) );

            return engine;
            
        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public RingEnginePilot createEnginePilot( HashFunction hash, Collection<? extends Node> nodes )
    {

        final RingEngine engine = createEngineInitializer( hash, nodes ).get();
        return new RingEnginePilot( engine );

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    public AlgorithmConfig validate( AlgorithmConfig config )
    {

        getVirtualNodes( config );
        return config;
        
    }

    
    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the number of virtual nodes used during the initialization of the {@code Ring} algorithm.
     *
     * @param config custom configurations for the {@code Ring} algorithm
     * @return number of virtual nodes for each physical node
     */
    public int getVirtualNodes( AlgorithmConfig config )
    {

        if( config == null )
            return DEFAULT_VIRTUAL_NODES;

        final ValuePath path = config.getPath().append( "args" ).append( "virtual-nodes" );
        final Object value = config.getArgs().get( "virtualnodes" );

        final int virtualNodes = value != null ? ConfigUtils.toInt( path, value ) : DEFAULT_VIRTUAL_NODES;
        if( virtualNodes < 1 )
            throw InconsistentValueException.lessThan( path, 1, virtualNodes );


        return virtualNodes;

    }

}
