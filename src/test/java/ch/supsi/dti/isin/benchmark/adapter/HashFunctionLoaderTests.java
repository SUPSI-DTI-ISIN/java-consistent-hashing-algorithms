package ch.supsi.dti.isin.benchmark.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.hashfunction.CRC32Hash;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import ch.supsi.dti.isin.hashfunction.MD5Hash;
import ch.supsi.dti.isin.hashfunction.Murmur3Hash;
import ch.supsi.dti.isin.hashfunction.XXHash;

/**
 * Suite to test the {@link HashFunctionLoader} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HashFunctionLoaderTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void all_hash_function_implementations_should_be_loaded()
    {


        final Map<String,Class<? extends HashFunction>> expected = new HashMap<>();
        expected.put( "murmur3", Murmur3Hash.class );
        expected.put( "crc32", CRC32Hash.class );
        expected.put( "md5", MD5Hash.class );
        expected.put( "xx", XXHash.class );
        
        final HashFunctionLoader loader = HashFunctionLoader.getInstance();
        for( Map.Entry<String,Class<? extends HashFunction>> entry : expected.entrySet() )
        {

            final HashFunction function = assertDoesNotThrow( () -> loader.load(entry.getKey()) );
            assertNotNull( function );
            assertEquals( function.getClass(), entry.getValue() );

        }

    }

}
