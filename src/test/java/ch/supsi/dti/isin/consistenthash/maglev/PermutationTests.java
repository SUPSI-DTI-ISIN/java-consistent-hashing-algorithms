package ch.supsi.dti.isin.consistenthash.maglev;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.consistenthash.ConsistentHash;

/**
 * Test suite for the class {@link Permutation}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PermutationTests
{

    @Test
    public void when_a_permutation_is_created_the_related_backend_should_be_stored()
    {

        final String backend = "node_ " + new Random().nextInt( 100 );
        final Permutation permutation = new Permutation( backend, ConsistentHash.DEFAULT_HASH_FUNCTION, 10 );

        assertSame( backend, permutation.backend() );
    }

    @Test
    public void a_permutation_should_visit_all_the_values()
    {

        final int primeSize = 11;
        final Permutation permutation = new Permutation( "node", ConsistentHash.DEFAULT_HASH_FUNCTION, primeSize );
        final Set<Integer> remaining = IntStream.range( 0, primeSize ).boxed().collect( Collectors.toSet() );

        for( int i = 0; i < 11; ++ i )
        {

            final int value = permutation.next();
            assertTrue( remaining.remove(value) );
            
        }

        assertTrue( remaining.isEmpty() );

    }


    @Test
    public void after_a_reset_the_sequence_should_start_again()
    {

        final Permutation permutation = new Permutation( "node", ConsistentHash.DEFAULT_HASH_FUNCTION, 10 );
        final List<Integer> sequence = IntStream
            .generate( permutation::next )
            .limit( 5 ).boxed()
            .collect( Collectors.toList() );

        permutation.reset();
        for( int i = 0; i < 5; ++ i )
        {

            assertEquals( sequence.get(i), permutation.next() );
            
        }

    }

}
