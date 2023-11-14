package ch.supsi.dti.isin.consistenthash;


import java.util.Collection;

import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.math.PrimeSieve;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.anchor.AnchorHash;
import ch.supsi.dti.isin.consistenthash.dx.DxHash;
import ch.supsi.dti.isin.consistenthash.jump.JumpHash;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevHash;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeHash;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousHash;
import ch.supsi.dti.isin.consistenthash.ring.RingHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Represents a Consistent hashing altoritm.
 * 
 * 
 * @author Massimo Coluzzi
 */
public interface ConsistentHash
{

    /** The {@link HashFunction.Algorithm} to be used by default in consistent hash algorithms. */
    public static final HashFunction.Algorithm DEFAULT_HASH_ALGOTITHM = HashFunction.Algorithm.MURMUR3;

    /** The {@link HashFunction} to be used by default in consistent hash algorithms. */
    public static final HashFunction DEFAULT_HASH_FUNCTION = HashFunction.create( DEFAULT_HASH_ALGOTITHM );


    /**
     * Returns the node associated to the given key.
     * 
     * @param key the key to check
     * @return the related node
     */
    Node getNode( String key );

    /**
     * Makes the algorithm aware of new nodes in the cluster.
     * 
     * @param nodes the nodes to add
     */
    void addNodes( Collection<? extends Node> nodes );

    /**
     * Makes the algorithm aware of the removal of nodes from the cluster.
     * 
     * @param nodes the nodes to remove
     */
    void removeNodes( Collection<? extends Node> nodes );

    /**
     * Tells if the algorithm supports the removal of random nodes.
     * 
     * @return {@code true} if nodes can be removed randomly
     */
    boolean supportsRandomRemovals();

    /**
     * Returns the number of nodes in the cluster.
     * 
     * @return number of nodes in the cluster.
     */
    int nodeCount();


    /**
     * Returns the actual implementation of the algorithm.
     * 
     * @return the actual implementation of the algorithm
     */
    Object engine();

    
    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new consistent hash function that uses the given algorithm.
     * 
     * @param algorithm the algorithm to use
     * @return a new hash function
     */
    public static ConsistentHash create(
        ConsistentHash.Algorithm algorithm,
        HashFunction.Algorithm function,
        Collection<? extends Node> nodes
    )
    {

        Require.nonNull( algorithm, "The algorithm to use is mandatory" );
        Require.nonNull( function,  "The hash function to use is mandatory" );
        Require.nonEmpty( nodes, "The cluster must have at least one node" );
        
        final HashFunction hash = HashFunction.create( function );

        switch( algorithm )
        {

            case ANCHOR_HASH: return new AnchorHash( nodes, nodes.size() << 1, hash );

            case DX_HASH: return new DxHash( nodes, nodes.size() << 1, hash );

            case JUMP_HASH: return new JumpHash( nodes, hash ); 

            case MAGLEV_HASH:
                final int lookupSize = (int) PrimeSieve.get().getSmallestPrimeGreaterEqual( nodes.size() << 7 );
                return new MaglevHash( nodes, lookupSize, hash );

            case MULTIPROBE_HASH: return new MultiProbeHash( nodes, hash );
            
            case RENDEZVOUS_HASH: return new RendezvousHash( nodes, hash );
            
            case RING_HASH: return new RingHash( nodes, hash );

            default:
                throw new IllegalArgumentException( "Unknown algorithm " + algorithm );

        }

    }


    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Enumerates the available implementation algorithms.
     * 
     * @author Massimo Coluzzi
     */
    enum Algorithm
    {

        /** {@code https://scm.ti-edu.ch/attachments/download/2035/AnchorHash.pdf} */
        ANCHOR_HASH,

        /** {@code https://arxiv.org/pdf/2107.07930.pdf} */
        DX_HASH,

        /** {@code https://arxiv.org/pdf/1406.2294.pdf} */
        JUMP_HASH,
        
        /** {@code https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf} */
        MAGLEV_HASH,
        
        /** {@code https://arxiv.org/pdf/1505.00062.pdf} */
        MULTIPROBE_HASH,

        /** {@code https://ieeexplore.ieee.org/abstract/document/663936} */
        RENDEZVOUS_HASH,

        /** {@code https://www.cs.princeton.edu/courses/archive/fall09/cos518/papers/chash.pdf} */
        RING_HASH;

    }

}
