package ch.supsi.dti.isin.benchmark.adapter.consistenthash.memento;


import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.memento.MementoEngine;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Memento} algorithm.
 *
 * 
 * @author Massimo Coluzzi
 */
public class MementoEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public MementoEnginePilot( MementoEngine engine )
    {

        super( engine );

    }

}
