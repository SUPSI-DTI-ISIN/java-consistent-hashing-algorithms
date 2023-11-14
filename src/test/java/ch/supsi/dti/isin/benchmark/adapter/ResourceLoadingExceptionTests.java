package ch.supsi.dti.isin.benchmark.adapter;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.config.InconsistentValueException;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Suite to test class {@link InconsistentValueException}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ResourceLoadingExceptionTests
{


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void factory_method_notFound_should_return_proper_messages()
    {

        final List<String> availableKeys = Arrays.asList( "yy", "zz" );
        final String expectedMessage = "Unable to load hash function with key xx. Available keys are: [yy, zz]";
        final ResourceLoadingException notFound = ResourceLoadingException.notFound( "hash function", "xx", availableKeys );
        
        assertNotNull( notFound );
        assertEquals( expectedMessage, notFound.getMessage() );
        
    }

    @Test
    public void factory_method_notInstantiable_should_return_proper_messages()
    {

        final Class<?> type = HashFunction.class;
        final String expectedMessage = "Unable to instantiate hash function of type " + type;
        final ResourceLoadingException notInstantiable = ResourceLoadingException.notInstantiable( "hash function", type );
        
        assertNotNull( notInstantiable );
        assertEquals( expectedMessage, notInstantiable.getMessage() );
        
    }

    @Test
    public void factory_method_incompatibleType_should_return_proper_messages()
    {

        final Class<?> expected = HashFunction.class;
        final Class<?> actual   = ConsistentHash.class;

        final String expectedMessage = "Incompatible type. Expected " + expected + ", but " + actual + " has been provided";
        final ResourceLoadingException notInstantiable = ResourceLoadingException.incompatibleType( expected, actual );
        System.out.println( expectedMessage );
        assertNotNull( notInstantiable );
        assertEquals( expectedMessage, notInstantiable.getMessage() );
        
    }

}
