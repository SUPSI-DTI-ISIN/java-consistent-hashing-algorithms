package ch.supsi.dti.isin.benchmark.adapter.resource.good;

import ch.supsi.dti.isin.benchmark.adapter.resource.FakeResource;

public abstract class AbstractResource implements FakeResource
{

    private static class PrivateResource implements FakeResource {}

    public static void print()
    {

        System.out.println( new PrivateResource() );
        
    }

}

