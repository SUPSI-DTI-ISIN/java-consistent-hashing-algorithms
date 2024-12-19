package ch.supsi.dti.isin.benchmark.adapter.consistenthash.flip;

import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.flip.FlipEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Flip} algorithm.
 *
 * @author Massimo Coluzzi
 */
public class FlipEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public FlipEnginePilot( FlipEngine engine )
    {

        super( engine );

    }

}
