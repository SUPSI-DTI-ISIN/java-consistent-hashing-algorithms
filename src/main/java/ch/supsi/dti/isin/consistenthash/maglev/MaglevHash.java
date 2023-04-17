package ch.supsi.dti.isin.consistenthash.maglev;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Wrapper to adapt the {@link MaglevEngine} to the {@link ConsistentHash} interface.
 * 
 * This wrapper performs all the consistency checks.
 *
 *
 * @author Massimo Coluzzi
 */
public class MaglevHash implements ConsistentHash
{

    /**
     * As described in the related paper the lookup table size
     * should be a prime number and it should be much bigger
     * than the number of nodes (lookupSize >> maxNodes ).
     * In the paper is suggested to set the lookup size 100
     * times bigger than the cluster size.
     * We start expecting the lookup size to be 128 times the cluster
     * size (size << 7 == size * 128) and allow to add
     * nodes until the lookup size is at least 64 times the cluster
     * size (size << 6 == size * 64).
     */

    /** Initial rate */
    private static final int INIT_RATE = 7;

    /** Minimum allowed rate */
    private static final int MIN_RATE = 6;
    

    /**
     * The {@code MaglevHash} algorithm engine as described in:
     * {@code https://static.googleusercontent.com/media/research.google.com/en//pubs/archive/44824.pdf}
     */
    private final MaglevEngine engine;

    /** The nodes of the cluster. */
    private final Map<String,Node> nodeMap;


    /**
     * Constructor with parameters.
     * 
     * @param initNodes    nodes used to initializa the cluster
     * @param lookupSize   the lookup table size
     */
    public MaglevHash( Collection<? extends Node> initNodes, int lookupSize )
    {

        this( initNodes, lookupSize, DEFAULT_HASH_FUNCTION );
        
    }


    /**
     * Constructor with parameters.
     * 
     * @param initNodes    nodes used to initializa the cluster
     * @param lookupSize   the lookup table size
     * @param hashFunction the hash function to use
     */
    public MaglevHash( Collection<? extends Node> initNodes, int lookupSize, HashFunction hashFunction )
    {

        Require.nonEmpty( initNodes, "The cluster must have at least one node" );
        Require.toHold(
            lookupSize >= initNodes.size() << INIT_RATE,
            "The size of the lookup table must be much bigger than the number of nodes"
        );
        
        Require.toHold( 
            isPrime( lookupSize ),
            () -> "Expected the lookup table size to be a prime number but was " + lookupSize 
        );
        
        this.engine = new MaglevEngine( lookupSize, Require.nonNull( hashFunction,"The hash function to use is mandatory") );
        this.nodeMap = new HashMap<>( initNodes.size() );

        this.addNodes( initNodes );
                
    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public Node getNode( String key )
    {

        final String backend = Require.nonNull(
            engine.getBackend( key ),
            () -> "Unable to find a backend for the key " + key
        );

        final Node node = Require.nonNull(
            nodeMap.get( backend ),
            () -> "Expected node with name " + backend + " but it does not exist"
        );

        return node;

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public void addNodes( Collection<? extends Node> toAdd )
    {

        Require.nonEmpty( toAdd, "The resources to add are mandatory" );
        Require.toHold(
            engine.lookupSize() >= (engine.size()+toAdd.size()) << MIN_RATE,
            "No room for more resources"
        );

        toAdd.forEach( node ->
        {

            Require.nonNull( node, "The resource to add cannot be null" );
            Require.toHold( nodeMap.putIfAbsent(node.name(),node) == null, () -> "Resource '" + node + "' already exists" );

        });

        final List<String> backends = toAdd.stream()
            .map( node -> node.name() )
            .collect( Collectors.toList() );

        engine.addBackends( backends );
            
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNodes( Collection<? extends Node> toRemove )
    {

        Require.nonEmpty( toRemove, "The resources to remove are mandatory" );
        Require.toHold( engine.size() > toRemove.size(), "Trying to remove more resources than available" );

        toRemove.forEach( node ->
        {

            Require.nonNull( node, "The resource to remove cannot be null" );
            Require.toHold( nodeMap.containsKey(node.name()), () -> "Resource '" + node + "' does not exists" );

        });

        final List<String> backends = toRemove.stream()
            .map( node -> node.name() )
            .peek( nodeMap::remove )
            .collect( Collectors.toList() );

        engine.removeBackends( backends );

    }
    
    /**
     * {@inheritDoc}}
     */
    public boolean supportsRandomRemovals()
    {

        return true;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int nodeCount()
    {

        return engine.size();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object engine()
    {

        return engine;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Chechs if the given value is prime.
     * 
     * @param value the value to check
     * @return {@code true} ir prime, {@code false} otherwise.
     */
    private boolean isPrime( int value )
    {

        /* Value cannot be < 2 by contract. */
        if( value < 4 )
            return true;

        final int sqrt = ((int) Math.sqrt( value )) + 1;
        for( int i = 2; i <= sqrt; ++i )
            if( value % i == 0 )
                return false;

        return true;

    }

}
