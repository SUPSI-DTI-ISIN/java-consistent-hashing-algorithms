package ch.supsi.dti.isin.benchmark.adapter.consistenthash.binomial;

import ch.supsi.dti.isin.benchmark.adapter.BucketBasedEnginePilot;
import ch.supsi.dti.isin.benchmark.adapter.ConsistentHashEnginePilot;
import ch.supsi.dti.isin.consistenthash.binomial.BinomialEngine;


/**
 * Implementation of the {@link ConsistentHashEnginePilot} interface for the {@code Binomial} algorithm.
 *
 * @author Massimo Coluzzi
 */
public class BinomialEnginePilot extends BucketBasedEnginePilot
{

    /**
     * Constructor with parameters.
     *
     * @param engine the bucket-based engine to pilot
     */
    public BinomialEnginePilot( BinomialEngine engine )
    {

        super( engine );

    }

}
