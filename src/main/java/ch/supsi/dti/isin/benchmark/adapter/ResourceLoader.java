package ch.supsi.dti.isin.benchmark.adapter;

import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import org.nerd4j.utils.lang.Require;
import org.reflections.Reflections;

import ch.supsi.dti.isin.benchmark.config.ConfigUtils;


/**
 * This abstract class is able to scan the classpath to find certain instantiable classes and store them.
 *
 * <p>
 * The implementations of this class would define which are the classes to load.
 * 
 * @param <R> Type or resource to load
 * 
 * @author Samuel De Babo Martins
 * @author Massimo Coluzzi
 */
public abstract class ResourceLoader<R>
{


    /** Java Logging System. */
    private static final Logger logger = Logger.getLogger( ResourceLoader.class.getName() );


    /** Describes the kind of resources handled by this loader. */
    private final String resourceKind;

    /** Collects the available resource types. */
    private final Map<String,Class<? extends R>> resourceTypes;



    /**
     * Constructor with parameters.
     * 
     * @param resourceKind kind of resource handled by this loader
     * @param resourceTyoe class representing the type of reaources to load
     * @param basePackage  the package where to search for resources
     */
    public ResourceLoader( String resourceKind, Class<R> resourceType, String basePackage )
    {

        super();

        this.resourceKind = Require.nonBlank( resourceKind );
        this.resourceTypes = scan( basePackage, resourceType );

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Returns an instance of the resource mapped to the requested key.
     *
     * @param key the key of the resource to load
     * @param args the arguments needed by the resource to be loaded
     * @return an instance of the resource mapped to the requested key
     * @throws ResourceLoadingException if the given key is not mapped to any resource
     */
    public R load( String key, Object... args )
    {

        logger.info( "Loading " + resourceKind + " with key " + key );
        final Class<? extends R> type = resourceTypes.get( ConfigUtils.normalize(key) );
        if( type == null )
            throw ResourceLoadingException.notFound( resourceKind, key, resourceTypes.keySet() );

        return instantiate( type, args );

    }

    /**
     * Returns the list of resources mapped to the requested keys.
     * This method works only for resources that do not need arguments
     * to be loaded.
     *
     * @param keys the keys of the resources to load
     * @return a list of resources mapped to the requested keys
     * @throws ResourceLoadingException if one of the given keys is not mapped to any resource
     */
    public List<R> load( Collection<String> keys )
    {

        return Require.nonNull( keys, "The keys of the resources to load cannot be null" )
                .stream()
                .map( this::load )
                .toList();

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * Returns the key related to the given resource.
     * 
     * @param resource
     * @return
     */
    protected abstract String getKey( Class<?> resource );


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Checks if it is possible to instantiate a class.
     *
     * @param type the class to instantiate
     * @return {@code true} if class is instantiable
     */
    private boolean isInstantiable( Class<?> type )
    {

        final int modifiers = type.getModifiers();
        return Modifier.isPublic( modifiers )
               && ! Modifier.isAbstract( modifiers )
               && ! Modifier.isInterface( modifiers );

    }

    /**
     * Returns the types of the given objects.
     * 
     * @param objects the objects to extract the type from
     * @return the related types
     */
    private Class<?>[] getTypes( Object[] objects )
    {

       return Arrays.stream( objects )
                    .map( Object::getClass )
                    .collect( Collectors.toList() )
                    .toArray( new Class[0] );

    }

    /**
     * Returns an instance of the given type.
     *
     * @param type class to instantiate
     * @param args constructor arguments
     * @return an instance of the required type
     * @throws ResourceLoadingException if an error occurs while instantiating the class
     */
    private R instantiate( Class<? extends R> type, Object... args )
    {

        try{

            final Class<?>[] types = getTypes( args );
            return type.getConstructor( types ).newInstance( args );

        }catch( ReflectiveOperationException ex )
        {
            
            throw ResourceLoadingException.notInstantiable( resourceKind, type );
            
        }

    }


    /**
     * Searches for instantiable classes that are subclass of a defined class in a certain package.
     * And adds these classes to the state of the object.
     *
     * @param basePackage  package where to look for classes
     * @param resourceType class representing the type of resources to load
     * @return the map binding resource keys to the related types
     */
    private Map<String,Class<? extends R>> scan( String basePackage, Class<R> resourceType )
    {

        Require.nonBlank( basePackage, "The base package where to load resources is mandatory" );
        Require.nonNull( resourceType, "The resource type is mandatory" );

        final Reflections reflections = new Reflections( basePackage );
        final Set<Class<? extends R>> subTypes = reflections.getSubTypesOf( resourceType );

        final Map<String,Class<? extends R>> resourceTypes = new HashMap<>();
        for( Class<? extends R> subType : subTypes )
        {
            if( isInstantiable(subType) )
            {

                final String resourceKey = getKey( subType );
                resourceTypes.put( resourceKey, subType );

                logger.info( "Loaded class " + subType.getCanonicalName() + " with key " + resourceKey );

            }
            else
                logger.warning( "Unable to load class " + subType.getCanonicalName() + " it is not an istantiable type" );

        }

        return resourceTypes;

    }

}
