package ch.supsi.dti.isin.benchmark.executor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.nerd4j.utils.lang.Require;

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
 * Benchmark tool to measure the distribution of keys among nodes
 * in a consistent hashing algorithm after the cluster has beed resized.
 *
 * @author Massimo Coluzzi
 */
public class ResizeBalance extends BenchmarkExecutor
{

    /** Random values generator. */
    private static final Random random = new Random();

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
     * @param config configuration to use to setup the current benchmark
     */
    public ResizeBalance( BenchmarkConfig config )
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
        ? ConfigUtils.toInt(config.getPath(),argument)
        : DEFAULT_KEY_MULTIPLIER;

    }

    /**
     * Prints the CSV header.
     *
     * @param writer the writer to use
     * @throws IOException if the writer fails
     */
    public static void printHeader( BufferedWriter writer ) throws IOException
    {

        writer.write( "HashFunction,Algorithm,Keys,Distribution,Nodes,Iterations,Min,Max,Expected,Min%,Max%,Var" );
        writer.newLine();

    }

    /**
     * Prints the collected metrics in a CSV format.
     *
     * @param metrics the metrics to print
     * @param writer  the writer to use
     * @throws IOException if the writer fails
     */
    public static void printMetrics( Metrics metrics, BufferedWriter writer ) throws IOException
    {

        final int keys = metrics.getKeysCount();
        final int nodes = metrics.getNodesCount();

        final double min = metrics.getMinCount();
        final double max = metrics.getMaxCount();

        writer.write( metrics.getFunction() );
        writer.write( ',' );
        writer.write( metrics.getAlgorithm() );
        writer.write( ',' );
        writer.write( String.valueOf(keys) );
        writer.write( ',' );
        writer.write( metrics.getDistribution().name() );
        writer.write( ',' );
        writer.write( String.valueOf(nodes) );
        writer.write( ',' );
        writer.write( String.valueOf(metrics.getIterations()) );
        writer.write( ',' );
        writer.write( String.valueOf(min) );
        writer.write( ',' );
        writer.write( String.valueOf(max) );
        writer.write( ',' );
        writer.write( String.valueOf(keys / nodes) );
        writer.write( ',' );
        writer.write( String.valueOf(min * nodes / keys) );
        writer.write( ',' );
        writer.write( String.valueOf(max * nodes / keys) );
        writer.write(',');
        writer.write(String.valueOf(metrics.getVariance()));
        writer.newLine();

    }


    /**
     * Runs the benchmark and writes the results.
     *
     * @param factories the algorithms to benchmark
     * @throws IOException if an error occurred while writing results on file
     */
    private void runAndWriteMetrics( List<ConsistentHashFactory> factories ) throws IOException
    {

        final Path file = BenchmarkExecutionUtils.getOutputFile( config );
        try( final BufferedWriter writer = Files.newBufferedWriter(file) )
        {
            
            final List<Distribution> distributions = config.getCommon().getKeyDistributions();
            final List<HashFunction> functions = BenchmarkExecutionUtils.getHashFunctions( config );

            printHeader( writer );
            for( HashFunction function : functions )
                for( ConsistentHashFactory factory : factories )
                    for( Distribution distribution : distributions )
                        for( int nodesCount : config.getCommon().getInitNodes() )
                        {

                            if( config.getCommon().isGc() )
                                System.gc();

                            final int keysCount = nodesCount * keyMultiplier;
                            final Metrics metrics = collectMetrics(
                                function, factory, nodesCount, distribution, keysCount,
                                config.getCommon().getIterations().getExecution() );

                            printMetrics( metrics, writer );
                            writer.flush();

                        }

        }

    }
    
    /**
     * Collects metrics about the distribution of keys among the nodes.
     *
     * @param function     the hash function to use
     * @param factory      the algorithm to collect metrics from
     * @param nodesCount   number of nodes of the cluster
     * @param distribution keys distribution
     * @param keysCount    number of keys
     * @param iterations   munber of times the test will be repeated
     */
    public Metrics collectMetrics(
        HashFunction function, ConsistentHashFactory factory,
        int nodesCount, Distribution distribution,
        int keysCount, int iterations
    )
    {

        final String algorithm = factory.getConfig().getName();
        final List<Node> initNodes = SimpleNode.create( nodesCount );
        final ConsistentHash consistentHash = factory.createConsistentHash( function, initNodes );

        /* Perform the resize of the cluster several times */
        final Map<Node,Integer> nodes = randomlyResize( consistentHash );
        final Metrics metrics = new Metrics( function.name(), algorithm, nodes.size(), distribution, keysCount, iterations );

        for( int i = 0; i < iterations; ++i )
        {

            final int iteration = i;

            System.out.print( "ITERATION " );
            System.out.print( iteration + 1 );
            System.out.print( ": Collecting metrics for " );
            System.out.print( algorithm );
            System.out.print( "(" + function.name() + ", " );
            System.out.print( nodes.size() + "), " );
            System.out.print( distribution );
            System.out.print( "(" + keysCount + ")..." );

            final KeyGenerator keyGenerator = KeyGenerator.create( distribution );

            final long start = System.currentTimeMillis();
            keyGenerator.stream().limit( keysCount ).forEach( key ->
            {

                final Node node = consistentHash.getNode( key );
                metrics.collect( iteration, nodes.get(node) );

            });

            final long end = System.currentTimeMillis();
            final long time = end - start;

            System.out.println( "-> [" + metrics.getMinCount(i+1) + ","
                + metrics.getMaxCount(i+1) + "] in " + time + "ms" );

        }

        return metrics;

    }

    /**
     * Randomly adds and removes nodes several times.
     * The nodes in the cluster are always in the interval
     * {@code [size/2,size*1.5]}
     *
     * @param consistentHash the cluster to resize
     */
    private static Map<Node,Integer> randomlyResize( ConsistentHash consistentHash )
    {

        final int size = consistentHash.nodeCount();
        final List<Node> active = SimpleNode.create( size );
        final List<Node> removed = SimpleNode.create( size << 1 ).subList( size, size << 1 );

        for( int i = 0; i < 4; ++i )
        {

            addNodes( consistentHash, size, active, removed );
            Require.toHold( consistentHash.nodeCount() == active.size() );

            removeNodes( consistentHash, size, active, removed );
            Require.toHold( consistentHash.nodeCount() == active.size() );

        }

        final Map<Node,Integer> nodes = new HashMap<>();
        for( int i = 0; i < active.size(); ++i )
            nodes.put( active.get(i), i );

        return nodes;

    }

    /**
     * Adds a random number of nodes to the given cluster.
     *
     * @param consistentHash cluster to resize
     * @param initSize       initial size of the cluster
     * @param active         active nodes in the cluster
     * @param removed        nodes removed from the cluster
     */
    private static void addNodes( ConsistentHash consistentHash, int initSize, List<Node> active, List<Node> removed )
    {

        final int addableCount = (initSize * 3 / 2) - consistentHash.nodeCount();
        final int number = random.nextInt( addableCount ) + 1;
        final List<Node> toAdd = new LinkedList<>();
        if( consistentHash.supportsOnlyLifoRemovals() )
        {

            for( int i = 0; i < number; ++ i )
                toAdd.add( removed.get(i) );

            removed.removeAll( toAdd );

        }
        else
        {

            for( int i = 0; i < number; ++ i )
            {

                final int index = random.nextInt( removed.size() );
                final Node node = removed.get( index );
                removed.remove( node );
                toAdd.add( node );

            }

        }
        

                
        active.addAll( toAdd );
        consistentHash.addNodes( toAdd );

    }

    /**
     * Removes a random number of nodes from the given cluster.
     *
     * @param consistentHash cluster to resize
     * @param initSize       initial size of the cluster
     * @param active         active nodes in the cluster
     * @param removed        nodes removed from the cluster
     */
    private static void removeNodes( ConsistentHash consistentHash, int initSize, List<Node> active, List<Node> removed )
    {

        final int removableCount = consistentHash.nodeCount() - (initSize /2);
        final int number = random.nextInt( removableCount ) + 1;
        final List<Node> toRemove = new LinkedList<>();
        if( consistentHash.supportsOnlyLifoRemovals() )
        {

            for( int i = 0; i < number; ++ i )
            {
                final int index = active.size() - (i + 1);
                toRemove.add( active.get(index) );
            }

            removed.addAll( toRemove );
            active.removeAll( toRemove );
            consistentHash.removeNodes( toRemove );

            Collections.sort( removed );

        }
        else
        {

            for( int i = 0; i < number; ++ i )
            {
                final int index = random.nextInt( active.size() );
                final Node node = active.get( index );

                active.remove( node );
                removed.add( node );
                toRemove.add( node );

            }

            consistentHash.removeNodes( toRemove );

        }
        
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

        /** The algorithm this metrics are related to.  */
        private final String algorithm;

        /** The keys distribution this metrics are related to.  */
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
            int nodesCount,  Distribution distribution,
            int keysCount,   int iterations
        )
        {

            super();

            this.function     = function;
            this.algorithm    = algorithm;
            this.keysCount    = keysCount;
            this.distribution = distribution;
            this.counts = new int[iterations][nodesCount];

        }

        /**
         * Collects a hit for the given node.
         *
         * @param iteration the iteration to collect
         * @param index     the index of the node to collect
         */
        public void collect( int iteration, int index )
        {

            counts[iteration][index]++;

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
                {
                    return Arrays.stream( iter )
                        .min()
                        .getAsInt();
                })
                .average()
                .getAsDouble();

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
                {
                    return Arrays.stream( iter )
                        .max()
                        .getAsInt();
                })
                .average()
                .getAsDouble();

        }
        
                /**
         * Returns the variance across all the iterations.
         *
         * @return variance
         */
        public double getVariance() {
            return getVariance(counts.length);
        }

        /**
         * Returns the variance for the given number of iterations
         *
         * @param iterations number of iterations to evaluate
         * @return variance
         */
        public double getVariance(int iterations) {
            int mean = keysCount / getNodesCount();
            return Arrays.stream(counts)
                    .limit(iterations)
                    .mapToDouble(iter
                            -> Arrays.stream(iter)
                            .map(n -> n - mean)
                            .map(n -> n * n)
                            .mapToDouble(n -> n).average().getAsDouble()
                    )
                    .average()
                    .orElseThrow();

        }

        /**
         * Returns the hash function this metrics are related to.
         * 
         * @return the hash function this metrics are related to
         */
        public String getFunction()
        {
            return function;
        }

        /**
         * Returns the algorithm this metrics are related to.
         * 
         * @return the algorithm this metrics are related to
         */
        public String getAlgorithm()
        {
            return algorithm;
        }

        /**
         * Returns the keys distribution this metrics are related to.
         * 
         * @return the keys distribution this metrics are related to
         */
        public Distribution getDistribution()
        {
            return distribution;
        }

        /**
         * Returns the number of times the benchmark is repeated.
         * 
         * @return the number of times the benchmark is repeated
         */
        public int getIterations()
        {
            return counts.length;
        }

        /**
         * Returns the number of keys.
         * 
         * @return the number of keys
         */
        public int getKeysCount()
        {
            return keysCount;
        }

        /**
         * Returns the numbero of nodes.
         * 
         * @return the number of nodes
         */
        public int getNodesCount()
        {
            return counts[0].length;
        }

    }

}
