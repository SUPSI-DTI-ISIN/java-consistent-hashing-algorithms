package ch.supsi.dti.isin.benchmark.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.benchmark.adapter.resource.FakeResource;
import ch.supsi.dti.isin.benchmark.adapter.resource.good.GoodResource;

/**
 * Suite to test the contract imposed by the {@link ResourceLoader} abstract class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ResourceLoaderTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void constructor_params_are_mandatory()
    {

        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>(null,Object.class,"ch.supsi.dti.isin.benchmark.adapter") );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>("",Object.class,"ch.supsi.dti.isin.benchmark.adapter") );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>(" \t\n",Object.class,"ch.supsi.dti.isin.benchmark.adapter") );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>("resource",null,"ch.supsi.dti.isin.benchmark.adapter") );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>("resource",Object.class,null) );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>("resource",Object.class,"") );
        assertThrows( RequirementFailure.class, () -> new MockResourceLoader<>("resource",Object.class," \t\n") );

    }

    @Test
    public void if_a_class_is_loaded_should_be_accessible()
    {

        final ResourceLoader<FakeResource> resourceLoader = new MockResourceLoader<>( "fake resource", FakeResource.class, "ch.supsi.dti.isin.benchmark.adapter.resource.good" );
        final FakeResource resource = resourceLoader.load( "goodresource" );
        assertNotNull( resource );
        assertEquals( GoodResource.class, resource.getClass() );

    }

    @Test
    public void only_instantiable_classes_should_be_returned()
    {

        final ResourceLoader<FakeResource> resourceLoader = new MockResourceLoader<>( "fake resource", FakeResource.class, "ch.supsi.dti.isin.benchmark.adapter.resource.good" );
        assertThrows( ResourceLoadingException.class, () -> resourceLoader.load("abstractresource") );
        assertThrows( ResourceLoadingException.class, () -> resourceLoader.load("privateresource") );
        assertThrows( ResourceLoadingException.class, () -> resourceLoader.load("badresource") );
        assertDoesNotThrow( () -> resourceLoader.load("goodresource") );

    }

    
    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    private static class MockResourceLoader<R> extends ResourceLoader<R>
    {

        public MockResourceLoader(String resourceKind, Class<R> resourceType, String basePackage )
        {

            super( resourceKind, resourceType, basePackage );

        }

        @Override
        protected String getKey( Class<?> resource )
        {

            return resource.getSimpleName().toLowerCase();

        }

    }

}
