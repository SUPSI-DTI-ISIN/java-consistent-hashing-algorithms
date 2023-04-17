package ch.supsi.dti.isin.benchmark.executor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.benchmark.config.ConfigUtils;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import ch.supsi.dti.isin.key.Distribution;
import ch.supsi.dti.isin.key.KeyGenerator;


/**
 * Benchmark tool to measure the distribution of keys among nodes in a consistent hashing algorithm.
 *
 * @author Massimo Coluzzi
 */
public class Balance extends BenchmarkExecutor
{


    /** Default value to use as key multiplier. */
    public static final int DEFAULT_KEY_MULTIPLIER = 10;


    /** 
     * The number of keys used for this benchmark will be the number of nodes multiplied by this value.
     * The default value for this property is {@link #DEFAULT_KEY_MULTIPLIER}.
     */
    private final int keyMultiplier;


    /**
     * Constructor with parameters.
     *
     * @param config  configuration to use to setup the current benchmark
     */
    public Balance( BenchmarkConfig config )
    {

        super( config );

        this.keyMultiplier = getKeyMultiplier();
        
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

        runAndWriteMetrics( factories );
        
    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Returns the multiplier to apply to get the number of keys to use.
     * 
     * @return the multiplier to apply to get the number of keys to use
     */
    private int getKeyMultiplier()
    {

        final Object argument = config.getArgs().get( "keymultiplier" );
        return argument != null
        ? ConfigUtils.toInt( config.getPath().append("key-multiplier"), argument )
        : DEFAULT_KEY_MULTIPLIER;

    }

    /**
     * Runs the benchmark and writes the results.
     *
     * @param factories the algorithms to benchmark
     * @throws IOException if an error occurred while writing results on file.
     */
    private void runAndWriteMetrics( List<ConsistentHashFactory> factories ) throws IOException
    {
        
        final Path file = BenchmarkExecutionUtils.getOutputFile( config );
        try( final BufferedWriter writer = Files.newBufferedWriter(file) )
        {
        
            final List<Distribution> distributions = config.getCommon().getKeyDistributions();
            final List<HashFunction> functions = BenchmarkExecutionUtils.getHashFunctions( config );
            
            printHeader( writer );
            for( HashFunction function :  functions )
                for( ConsistentHashFactory factory : factories )
                    for( Distribution distribution : distributions )
                        for( int nodesCount : config.getCommon().getInitNodes() )
                        {

                            if( config.getCommon().isGc() )
                                System.gc();

                            final int keysCount = nodesCount * keyMultiplier;
                            final Metrics metrics = collectMetrics(
                                function, factory, nodesCount, distribution, keysCount,
                                config.getCommon().getIterations().getExecution()
                            );

                            printMetrics( metrics, writer );
                            writer.flush();

                        }

        }

    }

    /**
     * Prints the CSV header.
     *
     * @param writer the writer
     */
    private void printHeader( BufferedWriter writer ) throws IOException
    {

        writer.write( "HashFunction,Algorithm,Keys,Distribution,Nodes,Iterations,Min,Max,Expected,Min%,Max%" );
        writer.newLine();

    }

    /**
     * Prints the collected metrics in a CSV format.
     *
     * @param metrics metrics to print.
     */
    private void printMetrics( Metrics metrics, BufferedWriter writer ) throws IOException
    {

        final int keys = metrics.getKeysCount();
        final int nodes = metrics.getNodesCount();

        final double min = metrics.getMinCount();
        final double max = metrics.getMaxCount();

        writer.write(metrics.getFunction());
        writer.write(',');
        writer.write(metrics.getAlgorithm());
        writer.write(',');
        writer.write(String.valueOf(keys));
        writer.write(',');
        writer.write(metrics.getDistribution().name());
        writer.write(',');
        writer.write(String.valueOf(nodes));
        writer.write(',');
        writer.write(String.valueOf(metrics.getIterations()));
        writer.write(',');
        writer.write(String.valueOf(min));
        writer.write(',');
        writer.write(String.valueOf(max));
        writer.write(',');
        writer.write(String.valueOf(keys / nodes));
        writer.write(',');
        writer.write(String.valueOf(min * nodes / keys));
        writer.write(',');
        writer.write(String.valueOf(max * nodes / keys));
        writer.newLine();

    }

    /**
     * Returns a map relating each node to an unique index.
     * 
     * @param nodesCount number of nodes to create
     * @return a map relating each node to an unique index
     */
    private Map<Node,Integer> getNodes( int nodesCount )
    {

        final List<Node> nodes = SimpleNode.create( nodesCount );
        final Map<Node,Integer> indexMap = new HashMap<>( nodesCount );
        for( int i = 0; i < nodesCount; ++i )
            indexMap.put( nodes.get(i), i );

        return indexMap;

    }

    /**
     * Collects metrics about the distribution of keys among the nodes.
     *
     * @param function     the hash function to use
     * @param factory      the algorithm to collect metrics from
     * @param nodesCount   number of nodes of the cluster
     * @param distribution keys distribution
     * @param keysCount    number of keys
     * @param iterations   number of times the test will be repeated
     * @return metrics
     */
    private Metrics collectMetrics(
            HashFunction function, ConsistentHashFactory factory,
            int nodesCount, Distribution distribution,
            int keysCount, int iterations
    )
    {

        final String algorithm = factory.getConfig().getName();
        final Metrics metrics = new Metrics( function.name(), algorithm, nodesCount, distribution, keysCount, iterations );

        final Map<Node,Integer> nodes = getNodes( nodesCount );
        final ConsistentHash consistentHash = factory.createConsistentHash( function, nodes.keySet() );

        for( int i = 0; i < iterations; ++i )
        {

            final int iteration = i;

            System.out.print("ITERATION ");
            System.out.print(iteration + 1);
            System.out.print(": Collecting metrics for ");
            System.out.print(algorithm);
            System.out.print("(" + function.name() + ", ");
            System.out.print(nodesCount + "), ");
            System.out.print(distribution);
            System.out.print("(" + keysCount + ")...");

            final KeyGenerator keyGenerator = KeyGenerator.create( distribution );

            final long start = System.currentTimeMillis();
            keyGenerator.stream().limit( keysCount ).forEach( key ->
            {
                final Node node = consistentHash.getNode(key);
                metrics.collect( iteration, nodes.get(node) );
            });

            final long end = System.currentTimeMillis();
            final long time = end - start;

            System.out.println("-> [" + metrics.getMinCount(i + 1) + ","
                    + metrics.getMaxCount(i + 1) + "] in " + time + "ms");

        }

        return metrics;

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Class to collect the balance metrics to test.
     *
     * @author Massimo Coluzzi
     */
    private static class Metrics
    {

        /** The hash function this metrics are related to. */
        private final String function;

        /** The algorithm this metrics are related to. */
        private final String algorithm;

        /** The keys' distribution this metrics are related to. */
        private final Distribution distribution;

        /** Expected number of collected keys. */
        private final int keysCount;

        /** Keeps the count of keys for each node. */
        private final int[][] counts;


        /**
         * Constructor with parameters.
         *
         * @param function     the hash function this metrics refer to
         * @param algorithm    the algorithm this metrics refer to
         * @param nodesCount   number of nodes
         * @param distribution keys distribution
         * @param keysCount    number of values to collect
         * @param iterations   number of times the benchmark is repeated
         */
        public Metrics(
                String function, String algorithm,
                int nodesCount, Distribution distribution,
                int keysCount, int iterations
        )
        {

            super();

            this.function = function;
            this.algorithm = algorithm;
            this.keysCount = keysCount;
            this.distribution = distribution;
            this.counts = new int[iterations][nodesCount];

        }

        /**
         * Collects a hit for the given node.
         *
         * @param nodeIndex index of the node to collect
         */
        public void collect( int iteration, int nodeIndex )
        {

            counts[iteration][nodeIndex]++;

        }

        /**
         * Returns the average of min counts across all the iterations.
         *
         * @return average min count
         */
        public double getMinCount()
        {

            return getMinCount( counts.length );

        }

        /**
         * Returns the average of min counts for the given number
         * of iterations.
         *
         * @param iterations number of iterations to evaluate
         * @return average min count
         */
        public double getMinCount( int iterations )
        {

            return Arrays.stream( counts )
                    .limit( iterations )
                    .mapToInt( iter ->
                        Arrays.stream( iter )
                            .min()
                            .orElseThrow()
                    )
                    .average()
                    .orElseThrow();

        }

        /**
         * Returns the average of max counts across all the iterations.
         *
         * @return average max count
         */
        public double getMaxCount()
        {

            return getMaxCount( counts.length );

        }

        /**
         * Returns the average of max counts for the given number
         * of iterations.
         *
         * @param iterations number of iterations to evaluate
         * @return average max count
         */
        public double getMaxCount( int iterations )
        {

            return Arrays.stream( counts )
                    .limit( iterations )
                    .mapToInt( iter ->
                        Arrays.stream( iter )
                            .max()
                            .orElseThrow()
                    )
                    .average()
                    .orElseThrow();

        }

        /**
         * Returns the hash function this metrics refer to.
         * 
         * @return the hash function this metrics refer to
         */
        public String getFunction()
        {
            return function;
        }

        /**
         * Returns the consistent hashing algorithm this metrics refer to.
         * 
         * @return the consistent hashing algorithm this metrics refer to
         */
        public String getAlgorithm()
        {
            return algorithm;
        }

        /**
         * Returns the key distribution this metrics refer to.
         * 
         * @return the key distribution this metrics refer to
         */
        public Distribution getDistribution()
        {
            return distribution;
        }

        /**
         * Returns the number of iterations of the benchmark.
         * 
         * @return the number of iterations of the benchmark
         */
        public int getIterations()
        {
            return counts.length;
        }

        /**
         * Returns the number of keys used during the benchmark.
         * 
         * @return the number of keys used during the benchmark
         */
        public int getKeysCount()
        {
            return keysCount;
        }

        /**
         * Returns the cluster's size this metrics refer to.
         * 
         * @return the cluster's size this metrics refer to
         */
        public int getNodesCount()
        {
            return counts[0].length;
        }

    }

}
