package ch.supsi.dti.isin.benchmark.adapter;

import java.util.Collection;

/**
 * This exception is thrown if something get wrong during the loading
 * of some runtime resource.
 * 
 * @author Massimo Coluzzi
 */
public class ResourceLoadingException extends RuntimeException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the exception message
     */
    private ResourceLoadingException( String message )
    {

        super( message );

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Returns a new {@link ResourceLoadingException} in the case of an unavailable key.
     * 
     * @param resourceKind the kind of resource to load
     * @param resourceKey the key to search for
     * @param availableResources the list of available resource keys
     * @return a new {@link ResourceLoadingException}
     */
    public static ResourceLoadingException notFound( String resourceKind, String resourceKey, Collection<String> availableResources )
    {

        final String message = new StringBuilder()
            .append( "Unable to load " )
            .append( resourceKind )
            .append( " with key " )
            .append( resourceKey )
            .append( ". Available keys are: " )
            .append( availableResources )
            .toString();

        return new ResourceLoadingException( message );

    }

    /**
     * Returns a new {@link ResourceLoadingException} in the case of a not instantiable class.
     * 
     * @param resourceKind the kind of resource to load
     * @param resourceType the list of available resource keys
     * @return a new {@link ResourceLoadingException}
     */
    public static ResourceLoadingException notInstantiable( String resourceKind, Class<?> resourceType )
    {

        final String message = new StringBuilder()
            .append( "Unable to instantiate " )
            .append( resourceKind )
            .append( " of type " )
            .append( resourceType )
            .toString();

        return new ResourceLoadingException( message );

    }

}
