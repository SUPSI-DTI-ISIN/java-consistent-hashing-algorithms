package ch.supsi.dti.isin.benchmark.adapter.resource.bad;

import ch.supsi.dti.isin.benchmark.adapter.resource.FakeResource;

/**
 * Represents a resource that cannot be instantiated because it does not provide a valid constructor.
 * 
 */
public class BadResource implements FakeResource
{

    public BadResource( String name ) {}

}
