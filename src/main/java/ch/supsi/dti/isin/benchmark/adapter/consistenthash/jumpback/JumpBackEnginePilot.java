package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jumpback;

import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.jumpback.JumpBackEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code JumpBackHash} algorithm.
 */
public class JumpBackEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public JumpBackEnginePilot( JumpBackEngine engine )
    {

        super( engine );

    }

}
