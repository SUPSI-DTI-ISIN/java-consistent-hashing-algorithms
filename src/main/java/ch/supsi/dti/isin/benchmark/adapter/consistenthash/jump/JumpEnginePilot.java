package ch.supsi.dti.isin.benchmark.adapter.consistenthash.jump;

import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.jump.JumpEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Jump} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class JumpEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public JumpEnginePilot( JumpEngine engine )
    {

        super( engine );

    }

}
