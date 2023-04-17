package ch.supsi.dti.isin.benchmark.adapter;

import java.util.Collection;
import java.util.function.Supplier;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.InvalidConfigException;
import ch.supsi.dti.isin.benchmark.executor.InitTime;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * The current factory aims to uniform the way consistent hashing algorithms are created.
 * Authors who want to test a new consistent hashing algorithm must implement the current
 * interface according to their algorithm and related configurations.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public abstract class ConsistentHashFactory
{


    /** The configuration to use to create consistent hash algorithms. */
    protected final AlgorithmConfig config;

    
    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    protected ConsistentHashFactory( AlgorithmConfig config )
    {

        super();

        this.config = validate(
            Require.nonNull( config, "The algorithm configuration is mandatory" )
        );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Creates a new instance of the consistent hashing algorithm.
     *
     * @param hash   hash function to use inside the consistent hashing algorithm
     * @param nodes  list of initial nodes
     * @param config additional configurations for the algorithm
     * @return a new object implementing the {@link ConsistentHash} interface
     */
    public abstract ConsistentHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes );

    /**
     * Returns a {@link Supplier} creating a new instance of the consistent hashing engine.
     * <p>
     * The {@link Supplier} returned by this method is intended to be used in the {@link InitTime}
     * benchmark to test the algorithm's initialization time.
     *
     * @param hash   hash function to use inside the consistent hashing algorithm
     * @param nodes  list of initial nodes
     * @param config additional configurations for the algorithm
     * @return a {@link Supplier} used to test initialization time
     */
    public abstract Supplier<?> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes );

    /**
     * Creates an instance of {@link ConsistentHashEnginePilot}.
     * <p>
     * The engine pilot is used to uniform operations on engines without losing performance.
     *
     * @param hash   hash function to use inside the consistent hashing algorithm
     * @param nodes  list of initial nodes
     * @param config additional configurations for the algorithm
     * @return a new object implementing the {@link ConsistentHashEnginePilot} interface
     */
     public abstract ConsistentHashEnginePilot<?> createEnginePilot( HashFunction hash, Collection<? extends Node> nodes );


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns the {@link AlgorithmConfig} related to the current factory.
     * 
     * @return the consistent hash algorithm configuration
     */
    public AlgorithmConfig getConfig()
    {

        return config;

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * Checks if the provided configuration is valid
     * for the related consistent hash algorithm.
     * 
     * @param config the configuration to validate
     * @throws InvalidConfigException if the validation fails
     */
    protected abstract AlgorithmConfig validate( AlgorithmConfig config );

}
