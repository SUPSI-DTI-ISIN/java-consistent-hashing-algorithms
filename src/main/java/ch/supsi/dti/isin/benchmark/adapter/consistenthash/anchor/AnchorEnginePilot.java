package ch.supsi.dti.isin.benchmark.adapter.consistenthash.anchor;


import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.anchor.AnchorEngine;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Anchor} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class AnchorEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public AnchorEnginePilot( AnchorEngine engine )
    {

        super( engine );

    }

}
