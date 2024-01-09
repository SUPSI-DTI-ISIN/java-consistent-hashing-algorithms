package ch.supsi.dti.isin.benchmark.adapter.consistenthash.power;

import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.power.PowerEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Power} algorithm.
 *
 * @author Massimo Coluzzi
 */
public class PowerEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public PowerEnginePilot( PowerEngine engine )
    {

        super( engine );

    }

}
