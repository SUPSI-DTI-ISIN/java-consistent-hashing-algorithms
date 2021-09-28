package ch.supsi.dti.isin.hashfunction;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.Contract;


/**
 * Suite to test the contract imposed by the {@link HashFunction} interface.
 *
 * @param <F> implementation of the hash function to test
 * 
 * @author Massimo Coluzzi
 * @since 2.0.0
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public interface HashFunctionContract<F extends HashFunction>extends Contract<F>
{

    /** Random values generator */
    static final Random random = new Random();


    /* ******************************************* */
    /*  TESTS FOR METHOD HashFunction.hash(byte[]) */
    /* ******************************************* */


    @Test
    default void the_same_bytes_should_allways_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );
        final byte[] bytes = key.getBytes( StandardCharsets.UTF_8 );

        assertEquals( function.hash( bytes ), function.hash( bytes ) );

    }

    @Test
    default void two_byte_arrays_with_the_same_value_should_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );        
        final byte[] bytes1 = key.getBytes( StandardCharsets.UTF_8 );
        final byte[] bytes2 = Arrays.copyOf( bytes1, bytes1.length );

        assertEquals( function.hash( bytes1 ), function.hash( bytes2 ) );

    }

    @ParameterizedTest
    @NullAndEmptySource
    default void if_the_given_byte_array_is_null_or_empty_a_RequirementFailure_should_be_thronw( byte[] bytes )
    {

        assertThrows( RequirementFailure.class, () -> sampleValue().hash(bytes) );

    }


    /* ******************************************* */
    /*  TESTS FOR METHOD HashFunction.hash(String) */
    /* ******************************************* */


    @Test
    default void the_same_key_should_allways_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );

        assertEquals( function.hash( key ), function.hash( key ) );

    }

    @Test
    default void two_keys_with_the_same_value_should_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final String key1 = "key_" + Math.abs( random.nextInt() );
        final String key2 = key1.substring( 0 );

        assertEquals( function.hash( key1 ), function.hash( key2 ) );

    }

    @ParameterizedTest
    @NullAndEmptySource
    default void if_the_given_key_is_null_or_empty_a_RequirementFailure_should_be_thronw( String key )
    {

        assertThrows( RequirementFailure.class, () -> sampleValue().hash( key ) );

    }

    @Test
    default void bytes_and_string_should_produce_consistent_hash_values()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );
        final byte[] bytes = key.getBytes( StandardCharsets.UTF_8 );

        assertEquals( function.hash(key), function.hash(bytes) );

    }


    /* *********************************************** */
    /*  TESTS FOR METHOD HashFunction.hash(String,int) */
    /* *********************************************** */


    @Test
    default void the_same_key_and_seed_should_allways_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );
        final int seed = Math.abs( random.nextInt() );

        assertEquals( function.hash( key, seed ), function.hash( key, seed ) );

    }

    @Test
    default void different_seeds_should_produce_different_hashes()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );
        final int seed1 = Math.abs( random.nextInt() );
        final int seed2 = Math.abs( random.nextInt() );

        assertNotEquals( seed1, seed2 );
        assertNotEquals( function.hash( key, seed1 ), function.hash( key, seed2 ) );

    }

    @Test
    default void two_keys_with_the_same_value_and_seed_should_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final int seed = Math.abs( random.nextInt() );
        final String key1 = "key_" + Math.abs( random.nextInt() );
        final String key2 = key1.substring( 0 );

        assertEquals( function.hash( key1, seed ), function.hash( key2, seed ) );

    }

    @Test
    default void two_keys_with_the_same_value_but_different_seed_should_produce_different_hashes()
    {

        final HashFunction function = sampleValue();
        final String key1 = "key_" + Math.abs( random.nextInt() );
        final String key2 = key1.substring( 0 );
        final int seed1 = Math.abs( random.nextInt() );
        final int seed2 = Math.abs( random.nextInt() );

        assertNotEquals( seed1, seed2 );
        assertNotEquals( function.hash( key1, seed1 ), function.hash( key2, seed2 ) );

    }

    @ParameterizedTest
    @NullAndEmptySource
    default void if_the_given_key_is_null_or_empty_a_RequirementFailure_should_be_thronw_despite_from_seed( String key )
    {

        final int seed = Math.abs( random.nextInt() );
        assertThrows( RequirementFailure.class, () -> sampleValue().hash( key, seed ) );

    }

    @Test
    default void simple_hashing_shoud_be_the_same_as_hashing_with_seed_0()
    {

        final HashFunction function = sampleValue();
        final String key = "key_" + Math.abs( random.nextInt() );

        assertEquals( function.hash(key), function.hash(key,0) );

    }


    /* ********************************************* */
    /*  TESTS FOR METHOD HashFunction.hash(long,int) */
    /* ********************************************* */


    @Test
    default void the_same_long_key_and_seed_should_allways_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final long key = Math.abs( random.nextLong() );
        final int seed = Math.abs( random.nextInt() );

        assertEquals( function.hash( key, seed ), function.hash( key, seed ) );

    }

    @Test
    default void same_long_key_but_different_seeds_should_produce_different_hashes()
    {

        final HashFunction function = sampleValue();
        final long key = Math.abs( random.nextLong() );
        final int seed1 = Math.abs( random.nextInt() );
        final int seed2 = Math.abs( random.nextInt() );

        assertNotEquals( seed1, seed2 );
        assertNotEquals( function.hash( key, seed1 ), function.hash( key, seed2 ) );

    }

    @Test
    default void two_long_keys_with_the_same_value_and_seed_should_produce_the_same_hash()
    {

        final HashFunction function = sampleValue();
        final int seed = Math.abs( random.nextInt() );
        final long key1 = Math.abs( random.nextLong() );
        final long key2 = key1;

        assertEquals( function.hash( key1, seed ), function.hash( key2, seed ) );

    }

    @Test
    default void two_long_keys_with_the_same_value_but_different_seed_should_produce_different_hashes()
    {

        final HashFunction function = sampleValue();
        final long key1 = Math.abs( random.nextLong() );
        final long key2 = key1;
        final int seed1 = Math.abs( random.nextInt() );
        final int seed2 = Math.abs( random.nextInt() );

        assertNotEquals( seed1, seed2 );
        assertNotEquals( function.hash( key1, seed1 ), function.hash( key2, seed2 ) );

    }

    @Test
    default void hashing_long_key_should_be_the_same_as_hashing_the_related_byte_array()
    {

        final HashFunction function = sampleValue();
        final long key = Math.abs( random.nextLong() );
        final int seed = Math.abs( random.nextInt() );

        final byte[] bytes = ByteBuffer
            .allocate( 12 )
            .putLong( key )
            .putInt( seed )
            .array();

        assertEquals( function.hash(key, seed), function.hash( bytes ) );

    }


    /* ************************************************* */
    /*  TESTS FOR METHOD HashFunction.hash(long,int,int) */
    /* ************************************************* */


    @Test
    default void hashing_long_key_and_index_should_be_the_same_as_hashing_the_related_byte_array()
    {

        final HashFunction function = sampleValue();
        final long key = Math.abs( random.nextLong() );
        final int index = Math.abs( random.nextInt() );
        final int seed = Math.abs( random.nextInt() );

        final byte[] bytes = ByteBuffer
            .allocate( 16 )
            .putLong( key )
            .putInt( index )
            .putInt( seed )
            .array();

        assertEquals( function.hash(key,index,seed), function.hash( bytes ) );

    }

}
