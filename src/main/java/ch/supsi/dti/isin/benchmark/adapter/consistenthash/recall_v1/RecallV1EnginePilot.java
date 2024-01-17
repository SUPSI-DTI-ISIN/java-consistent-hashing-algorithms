package ch.supsi.dti.isin.benchmark.adapter.consistenthash.recall_v1;


import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.recall_v1.RecallV1Engine;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code RecallV1} algorithm.
 *
 * 
 * @author Massimo Coluzzi
 */
public class RecallV1EnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public RecallV1EnginePilot( RecallV1Engine engine )
    {

        super( engine );

    }

}
