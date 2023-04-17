package ch.supsi.dti.isin.benchmark.adapter.consistenthash.dx;


import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.consistenthash.dx.DxEngine;

/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Dx} algorithm.
 *
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public class DxEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public DxEnginePilot( DxEngine engine )
    {

        super( engine );

    }

}
