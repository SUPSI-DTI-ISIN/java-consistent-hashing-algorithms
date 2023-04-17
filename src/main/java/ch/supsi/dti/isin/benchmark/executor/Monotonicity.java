package ch.supsi.dti.isin.benchmark.executor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
 * 
 * Benchmark tool to measure the redistribution of the keys during the
 * resizing of the cluster. Only the keys of the nodes involved in the
 * resizing should change their location. This benchmark measures how
 * many keys comply with such a constraint and how many do not.
 *
 * 
 * @author Massimo Coluzzi
 */
public class Monotonicity extends BenchmarkExecutor
{
    
    /** Default value to use as fractions. */
    public static final List<Double> DEFAULT_FRACTIONS = Arrays.asList( 0.1, 0.3, 0.5 );

    /** Default value to use as key multiplier. */
    public static final int DEFAULT_KEY_MULTIPLIER = 10;


    /** 
     * The number of keys used during the benchmark is the number of nodes multiplied by this value.
     * The default value for this property is {@link #DEFAULT_KEY_MULTIPLIER}.
     */
    private int keyMultiplier;

    /** 
     * The fractions of removed nodes to use for testing monotonicity.
     * The default value for this property is {@link #DEFAULT_FRACTIONS}.
     */
    private List<Double> fractions;


    /**
     * Constructor with parameters.
     *
     * @param config configuration to use to setup the current benchmark
     */
    public Monotonicity( BenchmarkConfig config )
    {

        super( config );

        this.fractions     = getFractions();
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
     * Returns the fractions of nodes to remove.
     * 
     * @return the fractions of nodes to remove
     */
    private List<Double> getFractions()
    {

        final Object argument = config.getArgs().get( "fractions" );
        return argument != null
        ? ConfigUtils.toList( config.getPath(), Double.class, argument )
        : DEFAULT_FRACTIONS;

    }

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
            for( double fraction : fractions )
                for( HashFunction function : functions)
                    for( ConsistentHashFactory factory : factories )
                        for( Distribution distribution : distributions )
                            for( int nodesCount : config.getCommon().getInitNodes() )
                            {

                                if( config.getCommon().isGc() )
                                    System.gc();

                                final int keysCount = nodesCount * keyMultiplier;
                                final Metrics metrics = collectMetrics( function, factory, nodesCount, distribution, keysCount, fraction );

                                printMetrics( metrics, writer );
                                writer.flush();

                            }

        }
        
    }

    /**
     * Returns a list of nodes where the number of elements
     * is given by {@code nodesCount * fraction}.
     * 
     * @param nodesCount total number of nodes
     * @param fraction   the fraction of nodes to create
     * @return a list of nodes
     */
    private List<Node> getNodes( int nodesCount, double fraction )
    {

        final int limit = (int)( fraction * nodesCount );
        return IntStream
            .iterate( nodesCount - 1, i -> i - 1 )
            .limit( limit )
            .mapToObj( SimpleNode::of )
            .collect( Collectors.toList() );

    }

    /**
     * Returns a map relating each node to a unique index.
     * 
     * @param nodes the list of nodes to map
     * @return a map relating each node to a unique index
     */
    private Map<Node,Integer> getNodeMap( List<Node> nodes )
    {

        final Map<Node,Integer> indexMap = new HashMap<>( nodes.size() );
        for( int i = 0; i < nodes.size(); ++i )
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
     * @param fraction     fraction of nodes to add/remove
     */
    private Metrics collectMetrics(
        HashFunction function, ConsistentHashFactory factory,
        int nodesCount, Distribution distribution,
        int keysCount, double fraction
    )
    {

        final KeyGenerator keyGenerator = KeyGenerator.create( distribution );
        final Map<String,Position> keys = keyGenerator.stream()
                .limit( keysCount ).distinct()
                .collect( Collectors.toMap( Function.identity(), s -> new Position() ) );

        final String algorithm = factory.getConfig().getName();
        final List<Node> initNodes = SimpleNode.create( nodesCount );

        final ConsistentHash consistentHash = factory.createConsistentHash( function, initNodes );
        final Metrics metrics = new Metrics( function.name(), algorithm, nodesCount, distribution, keysCount, keys, fraction );

        final List<Node> nodes = getNodes( nodesCount, fraction );
        final Map<Node,Integer> nodeMap = getNodeMap( initNodes );

        collect( Phase.BEFORE_REMOVE, consistentHash, metrics, nodeMap );

        consistentHash.removeNodes( nodes );

        collect( Phase.AFTER_REMOVE, consistentHash, metrics, nodeMap );

        Collections.reverse( nodes );
        consistentHash.addNodes( nodes );

        collect( Phase.AFTER_RESTORE, consistentHash, metrics, nodeMap );

        return metrics;

    }

    /**
     * Performs the data collection for the given phase and metrics.
     *
     * @param phase          the phase for which to collect data
     * @param consistentHash the consistent hashing algorithm
     * @param metrics        the metrics to fill
     * @param nodeMap        a map relating each node to a unique index
     */
    private static void collect( Phase phase, ConsistentHash consistentHash, Metrics metrics, Map<Node,Integer> nodeMap )
    {

        printLog( metrics, phase );

        final long start = System.currentTimeMillis();
        metrics.streamKeys().forEach( key ->
        {

            final Node node = consistentHash.getNode( key );
            metrics.collect( phase, key, nodeMap.get(node) );

        });
        final long end = System.currentTimeMillis();
        final long time = end - start;

        System.out.println( "-> done in " + time + "ms" );

    }

    /**
     * Prints the log of the current operation.
     *
     * @param metrics the metrics to print
     * @param phase the current phase
     */
    private static void printLog( Metrics metrics, Phase phase )
    {

        System.out.print( "Collecting metrics[" );
        System.out.print( phase );
        System.out.print( "] for " );
        System.out.print( metrics.getAlgorithm() );
        System.out.print( "(" );
        System.out.print( metrics.getFunction() );
        System.out.print( ", " );
        System.out.print( metrics.getNodesCount() );
        System.out.print( ", " );
        System.out.print( metrics.getFraction()  );
        System.out.print( "), " );
        System.out.print( metrics.getDistribution() );
        System.out.print( "(" );
        System.out.print( metrics.getKeysCount() );
        System.out.print( ")..." );

    }

    /**
     * Prints the CSV header.
     *
     * @param writer the writer to use
     */
    private static void printHeader( BufferedWriter writer ) throws IOException
    {

        writer.write( "HashFunction,Algorithm,Fraction,Keys,Distribution,Nodes,KeysInRemovedNodes," );
        writer.write( "KeysMovedFromRemovedNodes,KeysMovedFromOtherNodes,NodesLosingKeys,"  );
        writer.write( "KeysMovedToRestoredNodes,KeysMovedToOtherNodes,NodesGainingKeys,"  );
        writer.write( "KeysRelocatedAfterResize,NodesChangedAfterResize,"  );
        writer.write( "KeysMovedFromRemovedNodes%,KeysMovedFromOtherNodes%,NodesLosingKeys%,"  );
        writer.write( "KeysMovedToRestoredNodes%,KeysMovedToOtherNodes%,NodesGainingKeys%,"  );
        writer.write( "KeysRelocatedAfterResize%,NodesChangedAfterResize%" );
        writer.newLine();

    }

    /**
     * Prints the collected metrics in a CSV format.
     *
     * @param metrics metrics to print
     * @param writer  the writer to use
     */
    private static void printMetrics( Metrics metrics, BufferedWriter writer ) throws IOException
    {

            metrics.aggregate();

            final int keys = metrics.getKeysCount();
            final int nodes = metrics.getNodesCount();

            final int keysInRemovedNodes = metrics.keysInRemovedNodes();

            final int keysMovedFromRemovedNodes = metrics.keysMovedFromRemovedNodes();
            final int keysMovedFromOtherNodes = metrics.keysMovedFromOtherNodes();
            final int nodesLosingKeys = metrics.nodesLosingKeys();

            final int keysMovedToRestoredNodes = metrics.keysMovedToRestoredNodes();
            final int keysMovedToOtherNodes = metrics.keysMovedToOtherNodes();
            final int nodesGainingKeys = metrics.nodesGainingKeys();

            final int keysRelocatedAfterResize = metrics.keysRelocatedAfterResize();
            final int nodesChangedAfterResize = metrics.nodesChangedAfterResize();

            final double totalKeysMovedFrom = keysMovedFromRemovedNodes + keysMovedFromOtherNodes;
            final double totalKeysMovedTo   = keysMovedToRestoredNodes + keysMovedToOtherNodes;

            final double keysMovedFromRemovedNodesPerc = keysMovedFromRemovedNodes / totalKeysMovedFrom;
            final double keysMovedFromOtherNodesPerc = keysMovedFromOtherNodes / totalKeysMovedFrom;
            final double nodesLosingKeysPerc = (double) nodesLosingKeys / nodes;

            final double keysMovedToRestoredNodesPerc = keysMovedToRestoredNodes / totalKeysMovedTo;
            final double keysMovedToOtherNodesPerc = keysMovedToOtherNodes / totalKeysMovedTo;
            final double nodesGainingKeysPerc = (double) nodesGainingKeys / nodes;

            final double keysRelocatedAfterResizePerc = (double) keysRelocatedAfterResize / keys;
            final double nodesChangedAfterResizePerc = (double) nodesChangedAfterResize / nodes;

            writer.write( metrics.getFunction() );
            writer.write( ',' );
            writer.write( metrics.getAlgorithm() );
            writer.write( ',' );
            writer.write( String.valueOf(metrics.getFraction()) );
            writer.write( ',' );
            writer.write( String.valueOf(keys) );
            writer.write( ',' );
            writer.write( metrics.getDistribution().name() );
            writer.write( ',' );
            writer.write( String.valueOf(nodes) );
            writer.write( ',' );
            writer.write( String.valueOf(keysInRemovedNodes) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedFromRemovedNodes) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedFromOtherNodes) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesLosingKeys) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedToRestoredNodes) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedToOtherNodes) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesGainingKeys) );
            writer.write( ',' );
            writer.write( String.valueOf(keysRelocatedAfterResize) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesChangedAfterResize) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedFromRemovedNodesPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedFromOtherNodesPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesLosingKeysPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedToRestoredNodesPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(keysMovedToOtherNodesPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesGainingKeysPerc) );
            writer.write( ',' );
            writer.write( String.valueOf(keysRelocatedAfterResizePerc) );
            writer.write( ',' );
            writer.write( String.valueOf(nodesChangedAfterResizePerc) );
            writer.newLine();


    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Enumerates the phases of the data collections.
     *
     * @author Massimo Coluzzi
     */
    private enum Phase
    {

        /**
         * Represents the first phase of the benchmark,
         * when the cluster is stable and all nodes are working.
         */
        BEFORE_REMOVE,

        /**
         * Represents the second phase of the benchmark,
         * when a percentage of the nodes have been removed.
         */
        AFTER_REMOVE,

        /**
         * Represents the final phase of the benchmark,
         * when the removed nodes have been restored.
         */
        AFTER_RESTORE

    }


    /**
     * Class to collect the positions of a key
     * in the different phases.
     * 
     * @author Massimo Coluzzi
     */
    private static class Position
    {

        /** Stores the different positions. */
        private int[] nodeIds;


        /**
         * Default constructor.
         * 
         */
        public Position()
        {

            super();

            this.nodeIds = new int[3];

        }

        /**
         * Stores the position of the key in the given phase.
         * 
         * @param phase  reference phase
         * @param nodeId ID of the node storing the key
         */
        public void set( Phase phase, int nodeId )
        {

            this.nodeIds[phase.ordinal()] = nodeId;
            
        }

        /**
         * Returns the position of the key in the given phase.
         * 
         * @param phase  reference phase
         * @return the ID of the node storing the key
         */
        public int get( Phase phase )
        {

            return this.nodeIds[phase.ordinal()];

        }

    }


    /**
     * Class to collect the monotonicity metrics to test.
     *
     * @author Massimo Coluzzi
     */
    private static class Metrics
    {

        /** Fraction of the nodes to remove and restore. */
        private final double fraction;

        /** The hash function this metrics are related to. */
        private final String function;

        /** The algorithm this metrics are related to.  */
        private final String algorithm;

        /** The keys distribution this metrics are related to.  */
        private final Distribution distribution;

        /** Expected number of collected keys. */
        private final int keysCount;

        /** Stores the position of each key befor and after the resize. */
        private final Map<String,Position> keys;

        /** Number of keys in each node before removal. */
        private final int[] keysPerNode;

        /** Number of keys moved from a node to another after removal. */
        private final int[] movedFrom;

        /** Number of keys moved from a node to another after restoration. */
        private final int[] movedTo;

        /** Number of keys that changed position after removal and restoration. */
        private final int[] changedAfterResize;

        /** Smallest index of the removed nodes. */
        private final int smallestRemovedIndex;


        /**
         * Constructor with parameters.
         *
         * @param fraction fraction of nodes to remove and restore
         * @param function the hash function this metrics refer to
         * @param algorithm the algorithm this metrics refer to
         * @param nodesCount number of nodes
         * @param distribution keys distribution
         * @param keysCount number of values to collect
         * @param keys storage for the keys positions
         */
        public Metrics(
            String function, String algorithm,
            int nodesCount, Distribution distribution,
            int keysCount, Map<String,Position> keys, double fraction
        )
        {

            super();

            this.fraction     = fraction;
            this.function     = function;
            this.algorithm    = algorithm;
            this.keysCount    = keysCount;
            this.distribution = distribution;
            this.keys         = keys;
            this.movedFrom    = new int[nodesCount];
            this.movedTo      = new int[nodesCount];
            this.keysPerNode  = new int[nodesCount];
            this.changedAfterResize = new int[nodesCount];

            final double threshold = 1 - fraction;
            this.smallestRemovedIndex = (int)(threshold * nodesCount);

        }


        /**
         * Collects the position of the given key in the given phase.
         *
         * @param key  key to track
         * @param node position of the key
         */
        public void collect( Phase phase, String key, int index )
        {

            keys.get( key ).set( phase, index );

        }

        /**
         * Aggregates the collected data.
         *
         */
        public void aggregate()
        {

            for( Position pos : keys.values() )
            {

                final int posBeforeRemove = pos.get( Phase.BEFORE_REMOVE );
                final int posAfterRemove  = pos.get( Phase.AFTER_REMOVE  );
                final int posAfterRestore = pos.get( Phase.AFTER_RESTORE );

                ++keysPerNode[posBeforeRemove];

                if( posBeforeRemove != posAfterRemove )
                    ++movedFrom[posBeforeRemove];

                if( posAfterRemove != posAfterRestore )
                    ++movedTo[posAfterRestore];

                if( posBeforeRemove != posAfterRestore )
                    ++changedAfterResize[posBeforeRemove];

            }

        }

        /**
         * Number of keys in the nodes to be removed before the removal.
         *
         * @return number of keys
         */
        public int keysInRemovedNodes()
        {
            return Arrays.stream(keysPerNode, smallestRemovedIndex, keysPerNode.length).sum();
        }

        /**
         * Number of keys moved from a removed node to another
         * after the removal (should always be 1000).
         *
         * @return number of keys
         */
        public int keysMovedFromRemovedNodes()
        {
            return Arrays.stream(movedFrom, smallestRemovedIndex, movedFrom.length).sum();
        }

        /**
         * Number of keys moved from a working node to another
         * after the removal (should always be 0).
         * 
         * @return number of keys
         */
        public int keysMovedFromOtherNodes()
        {
            return Arrays.stream(movedFrom, 0, smallestRemovedIndex).sum();
        }

        /**
         * Number of nodes that losed at least one key after
         * the removal (should always be >= 1).
         * 
         * @return number of nodes
         */
        public int nodesLosingKeys()
        {
            return (int) Arrays.stream( movedFrom )
                .filter( x -> x > 0 )
                .count();
        }

        /**
         * Number of keys moved to other nodes after the removal.
         *
         * @return number of keys
         */
        public int keysMovedToRestoredNodes()
        {
            return Arrays.stream(movedTo, smallestRemovedIndex, movedTo.length).sum();
        }

        /**
         * Number of keys moved to other nodes after the restoration.
         * 
         * @return number of keys
         */
        public int keysMovedToOtherNodes()
        {
            return Arrays.stream(movedTo, 0, smallestRemovedIndex).sum();
        }

        /**
         * Number of nodes that gained at least one key after the restoration.
         * 
         * @return number of nodes
         */
        public int nodesGainingKeys()
        {
            return (int) Arrays.stream( movedTo )
                .filter( x -> x > 0 )
                .count();
        }

        /**
         * Number of keys which position is different after the resize.
         *
         * @return number of keys
         */
        public int keysRelocatedAfterResize()
        {
            return Arrays.stream( changedAfterResize ).sum();
        }

        /**
         * Number of nodes that have different keys after the resize.
         *
         * @return number of nodes
         */
        public int nodesChangedAfterResize()
        {
            return (int) Arrays.stream( changedAfterResize )
                .filter( x -> x > 0 )
                .count();
        }

        /**
         * Returns the fraction of the nodes to remove and restore.
         * 
         * @return the fraction of the nodes to remove and restore
         */
        public double getFraction()
        {
            return fraction;
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
         * Returns the expected number of collected keys.
         * 
         * @return the expected number of collected keys
         */
        public int getKeysCount()
        {
            return keysCount;
        }

        /**
         * Returns the number of nodes of the cluster.
         * 
         * @return the number of nodes of the cluster
         */
        public int getNodesCount()
        {
            return changedAfterResize.length;
        }

        /**
         * Returns a stream of the keys used during the benchmark.
         * 
         * @return a stream of the keys used during the benchmark
         */
        public Stream<String> streamKeys()
        {
            return keys.keySet().stream();
        }

    }

}
