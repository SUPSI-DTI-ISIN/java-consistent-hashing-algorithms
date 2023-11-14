package ch.supsi.dti.isin.benchmark.executor;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.nerd4j.utils.lang.IsNot;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.results.format.ResultFormatType;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.adapter.HashFunctionLoader;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.CommonConfig;
import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.benchmark.config.InvalidTypeException;
import ch.supsi.dti.isin.benchmark.config.IterationsConfig;
import ch.supsi.dti.isin.benchmark.config.JMHConfigWrapper;
import ch.supsi.dti.isin.benchmark.config.TimeConfig;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import ch.supsi.dti.isin.key.Distribution;
import ch.supsi.dti.isin.key.KeyGenerator;


/**
 * Benchmarks the time needed for a consistent hashing algorithm to lookup a key.
 * 
 * @author Massimo Coluzzi
 * @author Samuel De Babo Martins
 */
public class LookupTime extends BenchmarkExecutor
{

    
    /**
     * Constructor with parameters.
     *
     * @param config  configuration to use to setup the current benchmark
     */
    public LookupTime( BenchmarkConfig config )
    {

        super( config );

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}
     */
    @Override
    protected void performBenchmak( List<ConsistentHashFactory> factories ) throws Exception
    {
    
        final Path file = BenchmarkExecutionUtils.getOutputFile( config );
        
        final String[] benchmarks    = { config.getName() };
        final String[] distributions = BenchmarkExecutionUtils.getKeyDistributions( config );
        final String[] functions     = BenchmarkExecutionUtils.getHashFunctionNames( config );
        final String[] initNodes     = BenchmarkExecutionUtils.getInitNodes( config );
        final String[] algorithms    = BenchmarkExecutionUtils.getAlgorithms( factories );

        final CommonConfig common = config.getCommon();
        final TimeConfig time = common.getTime();
        final IterationsConfig iterations = common.getIterations();
                
        final Options opt = new OptionsBuilder()
            .include( LookupTime.LookupTimeExecutor.class.getSimpleName() )

            .param( "benchmark", benchmarks )
            .param( "function", functions )
            .param( "initNodes", initNodes )
            .param( "algorithm", algorithms )
            .param( "distribution", distributions )

            .resultFormat( ResultFormatType.CSV )
            .result( file.toString() )

            .shouldDoGC( common.isGc() )
            .forks( 0 )

            .mode( Mode.AverageTime )
            .timeUnit( time.getUnit() )
            .warmupTime( time.getWarmup() )
            .measurementTime( time.getExecution() )
            .warmupIterations( iterations.getWarmup() )
            .measurementIterations( iterations.getExecution() )

            .build();

        try{

            new Runner( opt ).run();

        }catch( RunnerException ex )
        {

            throw BenchmarkExecutionException.of( ex );

        }
    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Inner class that executes the benchmark.
     *
     * <p>{@code JMH} benchmarks need an inner class in order to work.
     * The outer class is used to pass the configurations and run the benchmark inside the inner class.
     * 
     * @author Samuel De Babo Martins
     * @author Massimo Coluzzi
     */
    @State(Scope.Benchmark)
    public static class LookupTimeExecutor
    {

        /** Name of the current benchmark. */
        @Param({})
        private String benchmark;

        /** Number of nodes used to initialize the cluster. */
        @Param({})
        private int initNodes;

        /** Hash function used to initialize the cluster. */
        @Param({})
        private String function;

        /** Name of the algorithm to benchmark. */
        @Param({})
        private String algorithm;

        /** Statistical key distribution. */
        @Param({})
        private Distribution distribution;

        /** The keys to use during the benchmark. */
        private Iterator<String> keys;

        /** The pilot where to invoke the lookup function on. */
        private ConsistentHashEnginePilot<?> pilot;

                
        /* **************** */
        /*  PUBLIC METHODS  */
        /* **************** */


        /**
         * Setups config values before running the benchmark. This method is automatically run by {@code JMH} before the benchmark.
         *
         * <p>
         * Since {@code JMH} benchmarks run in another process, previously created object are not accessible from the other process.
         * Therefore, before every {@code JMH} benchmark, the config objects, needed for that specific benchmark, are recreated.
         *
         * @param wrapper a wrapper object, automatically created and populated by {@code JMH},
         *                containing all the configurations needed by the benchmark.
         */
        @Setup
        public void setup( JMHConfigWrapper wrapper )
        {
            
            final BenchmarkConfig benchmarkConfig = BenchmarkExecutionUtils.getBenchmarkConfig( wrapper.getConfig(), benchmark );
            final AlgorithmConfig algorithmConfig = BenchmarkExecutionUtils.getAlgorithmConfig( wrapper.getConfig(), algorithm );
            final ConsistentHashFactory factory = BenchmarkExecutionUtils.getFactory( algorithmConfig );

            final HashFunction hashFunction = HashFunctionLoader.getInstance().load( function );
            final List<Node> nodes = SimpleNode.create( initNodes );

            final ConsistentHash consistentHash = factory.createConsistentHash( hashFunction, nodes );
            removeNodesIfNeeded( benchmarkConfig, consistentHash, nodes );

            this.keys = KeyGenerator.create(distribution).iterator();
            this.pilot = factory.createEnginePilot( consistentHash );

            
        }


        /**
         * Does a lookup using a key from the keys iterator.
         * This operation will be benchmarked by {@code JMH}.
         *
         * <p>
         * The looked up node is returned to prevent the {@code JIT}
         * compiler from optimizing the method.
         * 
         * @return the looked up node
         */
        @Benchmark
        public Object getNode()
        {

            return pilot.getNode( keys.next() );

        }


        /* ***************** */
        /*  PRIVATE METHODS  */
        /* ***************** */


        /**
         * Removes the configured percentage of nodes from the consistent hash engine.
         * If the removal rate is set to 0, nothing will be removed.
         * 
         * @param benchmarkConfig the configuration to parse
         * @param consistentHash  the algorithm to update
         * @param nodes           all the available nodes
         */
        private void removeNodesIfNeeded(
            BenchmarkConfig benchmarkConfig, ConsistentHash consistentHash, List<Node> nodes
        )
        {

            final List<Node> toRemove = getNodesToRemove( benchmarkConfig, nodes );
            if( IsNot.empty(toRemove) )
                consistentHash.removeNodes( toRemove );

        }

        /**
         * Returns the list of nodes to remove if any.
         * Otherwise, returns {@code null}.
         * 
         * @param benchmarkConfig configuration to parse
         * @param nodes list of nodes
         * @return nodes to remove or {@code null}
         */
        private List<Node> getNodesToRemove( BenchmarkConfig benchmarkConfig, List<Node> nodes )
        {

            final float removalRate = getRemovalRate( benchmarkConfig );
            if( removalRate <= 0 )
                return null;

            final List<Node> toRemove = new ArrayList<>( nodes );
            final int nodesToRemove = (int)(initNodes * removalRate);
            final RemovalOrder removalOrder = getRemovalOrder( benchmarkConfig );

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
        private float getRemovalRate( BenchmarkConfig benchmarkConfig )
        {

            final String property = "removalrate";
            final Object value = benchmarkConfig.getArgs().get( property );
            if( value == null )
                return 0;

            if( ! (value instanceof Number) )
                InvalidTypeException.of( valuePathOf(property), value, Float.class );

            final float removalRate = ((Number) value).floatValue();
            if( removalRate < 0 && removalRate >= 1 )
                InconsistentValueException.notAPercentage( valuePathOf(property), removalRate );

            return removalRate;

        }

        /**
         * Returns the order to apply to the nodes removal.
         * 
         * @param benchmarkConfig the configuration to parse
         * @return the order of node removal
         */
        private RemovalOrder getRemovalOrder( BenchmarkConfig benchmarkConfig )
        {

            final String property = "removalorder";
            final Object value = benchmarkConfig.getArgs().get( property );
            if( value == null )
                return RemovalOrder.LIFO;

            if( ! (value instanceof String) )
                InvalidTypeException.of( valuePathOf(property), value, String.class );

            try{

                return RemovalOrder.valueOf( value.toString().toUpperCase() );

            }catch( Exception ex )
            {

                final List<Object> possibleValues = Arrays.asList( (Object[]) RemovalOrder.values() );
                throw InconsistentValueException.notIn( valuePathOf(property), possibleValues, value );

            }

        }


        /**
         * Returns the path of the given property in the context of this benchmark.
         * 
         * @param property the property to point
         * @return the path to the property in the configuration file
         */
        private ValuePath valuePathOf( String property )
        {

             return ValuePath.root()
                .append( "benchmarks" )
                .append( "lookup-time" )
                .append( "args" )
                .append( property );

        }


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

}
