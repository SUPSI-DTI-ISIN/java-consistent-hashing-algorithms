package ch.supsi.dti.isin.benchmark.adapter.consistenthash.dx;

import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.dx.DxEngine;
import ch.supsi.dti.isin.consistenthash.dx.DxHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Dx} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class DxFactory extends ConsistentHashFactory
{


    /** The default capacity multiplier to apply if not defined in the configuration. */
    private static final int DEFAULT_CAPACITY = 10;


    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public DxFactory( AlgorithmConfig config )
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
    public DxHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int capacity = getCapacity( nodes.size(), config );
        return new DxHash( nodes, capacity, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<DxEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );


        final int capacity = getCapacity( nodes.size(), config );
        return () -> new DxEngine( nodes.size(), capacity, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DxEnginePilot createEnginePilot( HashFunction hash, Collection<? extends Node> nodes )
    {

        final DxEngine engine = createEngineInitializer( hash, nodes ).get();
        return new DxEnginePilot( engine );

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

        getCapacity( 10, config );
        return config;
        
    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the capacity of the cluster used during the initialization of the {@code Dx} algorithm.
     *
     * @param nodes  number of initial nodes
     * @param config custom configurations for the {@code Dx} algorithm
     * @return capacity of the cluster
     */
    public int getCapacity( int nodes, AlgorithmConfig config )
    {

        if( config == null )
            return nodes * DEFAULT_CAPACITY;

        final ValuePath path = config.getPath().append( "args" ).append( "capacity" );
        final Object value = config.getArgs().get( "capacity" );

        final int modifier = value != null ? ConfigUtils.toInt( path, value ) : DEFAULT_CAPACITY;
        if( modifier < 1 )
            throw InconsistentValueException.lessThan( path, 1, modifier );


        return nodes * modifier;

    }

}
