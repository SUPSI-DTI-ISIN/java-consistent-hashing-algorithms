package ch.supsi.dti.isin.benchmark.executor;


import java.nio.file.Path;
import java.util.List;

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
import ch.supsi.dti.isin.benchmark.config.IterationsConfig;
import ch.supsi.dti.isin.benchmark.config.JMHConfigWrapper;
import ch.supsi.dti.isin.benchmark.config.TimeConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Benchmarks the time needed for a consistent hashing algorithm
 * to update its internal data structure after a cluster resize.
 * 
 * @author Massimo Coluzzi
 * @author Samuel De Babo Martins
 */
public class ResizeTime extends BenchmarkExecutor
{


    /**
     * Constructor with parameters.
     * 
     * @param config configuration to use to setup the current benchmark
     */
    public ResizeTime( BenchmarkConfig config )
    {

        super(config);

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}
     */
    @Override
    public void performBenchmak( List<ConsistentHashFactory> factories ) throws Exception
    {

        final Path file = BenchmarkExecutionUtils.getOutputFile( config );
        
        final String[] functions = BenchmarkExecutionUtils.getHashFunctionNames( config );
        final String[] algorithms = BenchmarkExecutionUtils.getAlgorithms( factories );
        final String[] initNodes = BenchmarkExecutionUtils.getInitNodes( config );

        final CommonConfig common = config.getCommon();
        final IterationsConfig iterations = common.getIterations();
        final TimeConfig time = common.getTime();

        final Options opt = new OptionsBuilder()
            .include( ResizeTime.ResizeTimeExecutor.class.getSimpleName() )

            .param( "function", functions )
            .param( "initNodes", initNodes )
            .param( "algorithm", algorithms )

            .resultFormat( ResultFormatType.CSV )
            .result( file.toString() )

            .shouldDoGC( common.isGc() )
            .forks( 1 )

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
     * <p>
     * {@code JMH} benchmarks need an inner class in order to work.
     * The outer class is used to pass the configurations and run the benchmark inside the inner class.
     * 
     * @param <N> the class representing a node in the cluster
     * 
     * @author Samuel De Babo Martins
     * @author Massimo Coluzzi
     */
    @State(Scope.Benchmark)
    public static class ResizeTimeExecutor<N>
    {

        /** Number of nodes used to initialize the cluster. */
        @Param({})
        private int initNodes;

        /** Hash function used inside the algorithm. */
        @Param({})
        private String function;

        /** Name of the algorithm to benchmark. */
        @Param({})
        private String algorithm;

        /** Performs the operations of adding and removing nodes. */
        private ConsistentHashEnginePilot<N> pilot;


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
        @SuppressWarnings("unchecked")
        public void setup( JMHConfigWrapper wrapper )
        {

            final AlgorithmConfig algorithmConfig = BenchmarkExecutionUtils.getAlgorithmConfig( wrapper.getConfig(), algorithm );
            final ConsistentHashFactory factory = BenchmarkExecutionUtils.getFactory( algorithmConfig );

            final HashFunction hashFunction = HashFunctionLoader.getInstance().load( function );
            final List<Node> nodes = SimpleNode.create( initNodes );

            final ConsistentHash consistentHash = factory.createConsistentHash( hashFunction, nodes );
            final ConsistentHashEnginePilot<?> pilot = factory.createEnginePilot( consistentHash );
            this.pilot = (ConsistentHashEnginePilot<N>) pilot;

        }

        /**
         * Adds and immediately removes a node.
         * The resulting metric reports the cost in time of updating
         * the internal data structure of the algorithm when the cluster scales.
         * The metric comprises both cases: when a node is added and when it is removed.
         *
         */
        @Benchmark
        public void resizeNodes()
        {

            final N lastInsertedNode = pilot.addNode();
            pilot.removeNode( lastInsertedNode );

        }

    }

}
