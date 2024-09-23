package ch.supsi.dti.isin.benchmark.executor;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.openjdk.jol.info.GraphLayout;

import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashFactory;
import ch.supsi.dti.isin.benchmark.config.BenchmarkConfig;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Benchmark tool to measure the memory usage of a consistent hashing algorithm
 * with different HashFunction and nodes number.
 * This version of the memory usage benchmark fixes the size of the cluster
 * and increases the rate of removed nodes.
 * 
 * @author Massimo Coluzzi
 */
public class IncrementalMemoryUsage extends BenchmarkExecutor
{

    /**
     * Constructor with parameters.
     *
     * @param config  configuration to use to setup the current benchmark
     */
    public IncrementalMemoryUsage( BenchmarkConfig config )
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

        runAndWriteMetrics( factories );

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Prints the CSV header.
     *
     * @param writer the writer
     */
    private static void printHeader( BufferedWriter writer ) throws IOException
    {

        writer.write( "HashFunction,Algorithm,Nodes,RemovalRate,Bytes" );
        writer.newLine();

    }

    /**
     * Prints the collected metrics in a CSV format.
     * 
     * @param function     the selected hash function
     * @param factory      the benchmarked algorithm
     * @param nodesCount   number of nodes of the cluster
     * @param removalRate  rate of 
     * @param bytes        the measured amount of memory in bytes
     * @param writer       the writer to use
     */
    public static void printMetrics(
        String function, String algorithm,
        int nodesCount, float removalRate,
        long bytes, BufferedWriter writer
    ) throws IOException
    {

        writer.write( function );
        writer.write( ',' );
        writer.write( algorithm );
        writer.write( ',' );
        writer.write( String.valueOf(nodesCount)) ;
        writer.write( ',');
        writer.write( String.valueOf(removalRate)) ;
        writer.write( ',');
        writer.write( String.valueOf(bytes) );
        writer.newLine();

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

            final List<HashFunction> functions = BenchmarkExecutionUtils.getHashFunctions( config );
            final String[] removalRates = BenchmarkExecutionUtils.getIncrementalRates( config );
            
            printHeader( writer );
            for( HashFunction function : functions )
                for( ConsistentHashFactory factory : factories )
                    for( int nodesCount : config.getCommon().getInitNodes() )
                        for( String rate : removalRates )
                        {

                            final List<Node> nodes = SimpleNode.create(nodesCount);
                            final ConsistentHash consistentHash = factory.createConsistentHash( function, nodes );

                            final float removalRate = Float.parseFloat( rate );
                            BenchmarkExecutionUtils.removeNodes( consistentHash, nodes, removalRate );
                            
                            final long bytes = GraphLayout.parseInstance( consistentHash.engine() ).totalSize();
                            printMetrics( function.name(), factory.getConfig().getName(), nodesCount, removalRate, bytes, writer );

                            writer.flush();
                        }

        }

    }

}
