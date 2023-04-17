package ch.supsi.dti.isin.benchmark.adapter;


import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * All consistent hashing algorithms developed in this project come with two classes.
 * One class called engine implements the algorithm as described in the related paper.
 * The other class adapts the algorithm to the common {@link ConsistentHash} interface
 * and performs all the data consistency checks.
 * 
 * <p>
 * To be able to benchmark the algorithms, all of them must share a common interface.
 * But, using the classes implementing {@link ConsistentHash} would create overhead
 * and falsify the results.
 * 
 * <p>
 * The current interface and its implementations aim to uniform the execution
 * of the benchmarked operations without adding any overhead.
 * 
 *
 * @param <N> the type used to represent a node in the CH algorithm.
 */
public interface ConsistentHashEnginePilot<N>
{

    /**
     * Performs a lookup and returns the node associated with the specified key.
     *
     * <p>Since every CH algorithm could return a different type of Node the method will return Object.
     * The returned value is not used but can avoid dead code elimination from the JVM.
     *
     * @param key key of the node that will be returned
     * @return a Node associated with the specified key
     */
    Object getNode( String key );

    /**
     * Adds a random node to the CH algorithm and returns its identification.
     *
     * <p>Depending on the implementation the "random" node could be incremental to the last created.
     *
     * @return identifier of the created node
     */
    N addNode();

    /**
     * Removes a node from the CH algorithm.
     *
     * <p>The parameter can be taken from the method addNode().
     *
     * @param node identifier of the node to remove
     */
    void removeNode( N node );

}
