package ch.supsi.dti.isin.benchmark.adapter.consistenthash.maglev;



import java.util.Collection;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.math.PrimeSieve;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevEngine;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Implementation of {@link ConsistentHashFactory} for the {@code Maglev} algorithm.
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class MaglevFactory extends ConsistentHashFactory
{


    /** The default number of permutations to apply if not defined in the configuration. */
    private static final int DEFAULT_PERMUTATIONS = 128;

    /**
     * Constructor with parameters.
     * 
     * @param config the configuration to use
     */
    public MaglevFactory( AlgorithmConfig config )
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
    public MaglevHash createConsistentHash( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int lookupSize = getLookupSize( nodes.size(), config );
        return new MaglevHash( nodes, lookupSize, hash );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Supplier<MaglevEngine> createEngineInitializer( HashFunction hash, Collection<? extends Node> nodes )
    {

        Require.nonNull( hash, "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The initial cluster nodes are mandatory" );

        final int lookupSize = getLookupSize( nodes.size(), config );
        final List<String> backends = nodes.stream().map( String::valueOf ).collect( Collectors.toList() );
        
        return () -> {
            
            final MaglevEngine engine = new MaglevEngine( lookupSize, hash );
            engine.addBackends( backends );

            return engine;

        };

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MaglevEnginePilot createEnginePilot( HashFunction hash, Collection<? extends Node> nodes )
    {

        final MaglevEngine engine = createEngineInitializer( hash, nodes ).get();
        return new MaglevEnginePilot( engine );

    }


        
    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}}
     */
    @Override
    protected AlgorithmConfig validate( AlgorithmConfig config )
    {

        getPermutations( config );
        return config;
        
    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the size of the lookup table used during the initialization of the algorithm.
     *
     * @param initNodes number of initial nodes
     * @param config custom configurations for the algorithm
     * @return lookup table size
     */
    public int getLookupSize( int initNodes, AlgorithmConfig config )
    {

        final int permutations = getPermutations( config );
        final int minThreshold = initNodes * permutations;
        
        return (int) PrimeSieve.get().getSmallestPrimeGreaterEqual( minThreshold );

    }


    /**
     * Returns the number of permutations to use in computing the lookup size.
     * 
     * @param config configuration to parse
     * @return number of permutations
     */
    private int getPermutations( AlgorithmConfig config )
    {

        if( config == null )
            return DEFAULT_PERMUTATIONS;

        final ValuePath path = config.getPath().append( "args" ).append( "permutations" );
        final Object value = config.getArgs().get( "permutations" );
    
        final int permutations = value != null ? ConfigUtils.toInt( path, value ) : DEFAULT_PERMUTATIONS;
        if( permutations < 128 )
            throw InconsistentValueException.lessThan( path, 128, permutations );

        return permutations;

    }

}
