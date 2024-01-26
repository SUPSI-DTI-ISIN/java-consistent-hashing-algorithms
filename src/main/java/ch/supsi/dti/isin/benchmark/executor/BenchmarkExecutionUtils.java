package ch.supsi.dti.isin.benchmark.executor;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.nerd4j.utils.lang.IsNot;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactoryLoader;
import ch.supsi.dti.isin.benchmark.adapter.HashFunctionLoader;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.Config;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.InvalidTypeException;
import ch.supsi.dti.isin.benchmark.config.MissingValueException;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Utility class to perform common benchmark operations.
 * 
 * @author Massimo Coluzzi
 */
public class BenchmarkExecutionUtils
{

    
    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( BenchmarkExecutionUtils.class.getName() );


    /**
     * Returns the path of the file where to store the output of the benchmark.
     * <p>
     * This method ensures that the parent folders exist. Otherwise, it creates them.
     * 
     * @param config the benchmark configuration to use
     * @throws IOException if parent folders cannot be created
     */
    public static Path getOutputFile( BenchmarkConfig config ) throws IOException
    {

        final Path path = config.getCommon().getResultsFolder();
        if( ! Files.exists(path) )
        {
            Files.createDirectories( path );
            logger.info( "Folders for path '" + path + "' have been created." );
        }
        
        return path.resolve( config.getName() + ".csv" );

    }

    /**
     * Returns the list of hash functions defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of hash functions to use
     */
    public static List<HashFunction> getHashFunctions( BenchmarkConfig config )
    {

        return HashFunctionLoader.getInstance().load( config.getCommon().getHashFunctions() );

    }

    /**
     * Returns the list of hash function names defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of hash function names
     */
    public static String[] getHashFunctionNames( BenchmarkConfig config )
    {

        return config.getCommon().getHashFunctions().toArray( String[]::new );

    }

    /**
     * Returns the list of initial cluster nodes defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of initial cluster nodes
     */
    public static String[] getInitNodes( BenchmarkConfig config )
    {

        return config.getCommon().getInitNodes().stream().map( String::valueOf ).toArray( String[]::new );

    }

    /**
     * Returns the list of consistent hashing algorithms defined in the given configuration.
     * 
     * @param factories factories for the arlgorithms to benchmark
     * @return the list of consistent hashing algorithms
     */
    public static String[] getAlgorithms( List<ConsistentHashFactory> factories )
    {

        return factories.stream().map( f -> f.getConfig().getName() ).toArray( String[]::new );

    }

    /**
     * Returns the list of key distributions defined in the given configuration.
     * 
     * @param config the benchmark configuration to use
     * @return the list of key distributions
     */
    public static String[] getKeyDistributions( BenchmarkConfig config )
    {

        return config.getCommon().getKeyDistributions().stream().map( String::valueOf ).toArray( String[]::new );

    }

    /**
     * Returns the list of removal rates defined in the given benchmark configuration.
     * 
     * @param config the benchmark configuration to use
     * @return a list of removal rates
     */
    public static String[] getIncrementalRates( BenchmarkConfig config )
    {

        final String[] defaultRates = { "0.0", "0.1", "0.2", "0.3", "0.4", "0.5", "0.6", "0.7", "0.8", "0.9" };

        final Object removalRates = config.getArgs().get( "removalrates" );
        if( removalRates == null )
            return defaultRates;

        final ValuePath valuePath = config.getPath().append("args").append("removal-rates");
        if( ! (removalRates instanceof List) )
            throw InvalidTypeException.of( valuePath, removalRates, List.class );

        @SuppressWarnings("unchecked")
        final List<Object> rateList = (List<Object>) removalRates;
        if( rateList.isEmpty() )
            return defaultRates;

        final String[] rates = new String[rateList.size()];
        for( int i = 0; i < rates.length; ++i )
        {
            final Object value = rateList.get( i );
            if( value == null )
                throw MissingValueException.of( valuePath.append(i) );

            if( ! (value instanceof Number) )
                throw InvalidTypeException.of( valuePath.append(i), value, Number.class );

            final float rate = ((Number) value).floatValue();
            if( rate < 0 || rate >= 1 )
                throw InconsistentValueException.notAPercentage( valuePath.append(i), rate );

            rates[i] = String.valueOf( rate );

        }

        return rates;
        
    }

    /**
     * Extracts from the given configuration the benchmark configuration for the given algorithm.
     * 
     * @param config the configuration to parse
     * @param benchmark the benchmark for which to extract the configuration
     * @return the configuration for the given benchmark
     */
    public static BenchmarkConfig getBenchmarkConfig( Config config, String benchmark )
    {

        return config.getBenchmarks().stream().filter( b -> b.getName().equals(benchmark) ).findFirst().get();

    }

    /**
     * Extracts from the given configuration the algorithm configuration for the given algorithm.
     * 
     * @param config the configuration to parse
     * @param algorithm the algorithm for which to extract the configuration
     * @return the configuration for the given algorithm
     */
    public static AlgorithmConfig getAlgorithmConfig( Config config, String algorithm )
    {

        return config.getAlgorithms().stream().filter( a -> a.getName().equals(algorithm) ).findFirst().get();

    }

    /**
     * Returns the consistent hash factory related to the given algorithm config.
     * 
     * @param config the configuration to parse
     * @return the consistent hash factory
     */
    public static ConsistentHashFactory getFactory( AlgorithmConfig config )
    {

        return ConsistentHashFactoryLoader.getInstance().load( config.getName(), config );

    }

    /**
     * Removes a percentage of the nodes.
     * 
     * @param consistentHash the consistent hash algorithm to update
     * @param nodes          the original list of nodes
     * @param removalRate    the rate of nodes to remove
     */
    public static void removeNodes( ConsistentHash consistentHash, List<Node> nodes, float removalRate )
    {

        final int removeCount = (int)(nodes.size() * removalRate);
        if( removeCount <= 0 )
            return;
            
        if( consistentHash.supportsOnlyLifoRemovals() )
            Collections.reverse( nodes );
        else
            Collections.shuffle( nodes );

        final List<Node> toRemove = nodes.subList( 0, removeCount );
        consistentHash.removeNodes( toRemove );

    }

    /**
     * Removes the configured percentage of nodes from the consistent hash engine.
     * If the removal rate is set to 0, nothing will be removed.
     * 
     * @param benchmarkConfig the configuration to parse
     * @param consistentHash  the algorithm to update
     * @param nodes           all the available nodes
     * @return the list of removed nodes
     */
    public static List<Node> removeNodesIfNeeded(
        BenchmarkConfig benchmarkConfig, ConsistentHash consistentHash, Collection<Node> nodes
    )
    {

        final List<Node> toRemove = getNodesToRemove( benchmarkConfig, consistentHash, nodes );
        if( IsNot.empty(toRemove) )
            consistentHash.removeNodes( toRemove );

        return toRemove;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the list of nodes to remove if any.
     * Otherwise, returns {@code null}.
     * 
     * @param benchmarkConfig configuration to parse
     * @param nodes list of nodes
     * @return nodes to remove or {@code null}
     */
    private static List<Node> getNodesToRemove(
        BenchmarkConfig benchmarkConfig, ConsistentHash consistentHash, Collection<Node> nodes
    )
    {

        final float removalRate = getRemovalRate( benchmarkConfig );
        if( removalRate <= 0 )
            return Collections.emptyList();

        final List<Node> toRemove = new ArrayList<>( nodes );
        final int nodesToRemove = (int)(nodes.size() * removalRate);

        RemovalOrder removalOrder = getRemovalOrder( benchmarkConfig );
        if( consistentHash.supportsOnlyLifoRemovals() )
            removalOrder = RemovalOrder.LIFO;

        switch( removalOrder )
        {

            case FIFO:
                break;
            
            case LIFO:
                Collections.reverse( toRemove );
                break;

            case RANDOM:
                Collections.shuffle( toRemove );
                break;

        }

        return toRemove.subList( 0, nodesToRemove );

    }

    /**
     * Returns the rate of nodes to remove. 
     * 
     * @param benchmarkConfig the configuration to parse
     * @return the rate of nodes to remove
     */
    private static float getRemovalRate( BenchmarkConfig benchmarkConfig )
    {

        final String property = "removalrate";
        final Object value = benchmarkConfig.getArgs().get( property );
        if( value == null )
            return 0;

        if( ! (value instanceof Number) )
            throw InvalidTypeException.of( benchmarkConfig.getPath().append(property), value, Float.class );

        final float removalRate = ((Number) value).floatValue();
        if( removalRate < 0 || removalRate >= 1 )
            throw InconsistentValueException.notAPercentage( benchmarkConfig.getPath().append(property), removalRate );

        return removalRate;

    }

    /**
     * Returns the order to apply to the nodes removal.
     * 
     * @param benchmarkConfig the configuration to parse
     * @return the order of node removal
     */
    private static RemovalOrder getRemovalOrder( BenchmarkConfig benchmarkConfig )
    {

        final String property = "removalorder";
        final Object value = benchmarkConfig.getArgs().get( property );
        if( value == null )
            return RemovalOrder.LIFO;

        if( ! (value instanceof String) )
            throw InvalidTypeException.of( benchmarkConfig.getPath().append(property), value, String.class );

        try{

            return RemovalOrder.valueOf( value.toString().toUpperCase() );

        }catch( Exception ex )
        {

            final List<Object> possibleValues = Arrays.asList( (Object[]) RemovalOrder.values() );
            throw InconsistentValueException.notIn( benchmarkConfig.getPath().append(property), possibleValues, value );

        }

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Enumerates the possible removal orders.
     * 
     * @author Massimo Coluzzi
     */
    private enum RemovalOrder
    {

        /** Nodes are removed in First-In-First-Out order. */
        FIFO,
        
        /** Nodes are removed in Last-In-First-Out order. */
        LIFO,
        
        /** Nodes are removed in random order. */
        RANDOM

    }

}
