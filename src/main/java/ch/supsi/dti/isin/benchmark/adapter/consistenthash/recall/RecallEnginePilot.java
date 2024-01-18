package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall;


import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.recall.RecallEngine;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Recall} algorithm.
 *
 * 
 * @author Massimo Coluzzi
 */
public class RecallEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public RecallEnginePilot( RecallEngine engine )
    {

        super( engine );

    }

}
