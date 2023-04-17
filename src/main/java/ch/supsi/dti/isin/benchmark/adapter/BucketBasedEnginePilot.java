package ch.supsi.dti.isin.benchmark.adapter;

import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.consistenthash.BucketBasedEngine;

/**
 * Implementation of {@link ConsistentHashEnginePilot} common to all bucket-based
 * consistent hashing algorithms.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public abstract class BucketBasedEnginePilot implements ConsistentHashEnginePilot<Integer>
{


    /** The bucket-based engine to pilot. */
    private final BucketBasedEngine engine;


    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    protected BucketBasedEnginePilot( BucketBasedEngine engine )
    {

        super();

        this.engine = Require.nonNull( engine, "The bucket-based engine to pilot is mandatory" );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getNode( String key )
    {

        return engine.getBucket(key);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer addNode()
    {

        return engine.addBucket();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeNode( Integer node )
    {

        engine.removeBucket( node );

    }

}