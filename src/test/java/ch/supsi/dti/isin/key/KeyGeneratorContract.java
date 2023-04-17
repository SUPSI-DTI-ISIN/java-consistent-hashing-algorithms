package ch.supsi.dti.isin.key;

import java.util.Random;

import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.Require;

import ch.supsi.dti.isin.Contract;

/**
 * Suite to test the contract imposed by the {@link KeyGenerator} interface.
 *
 * @param <K> implementation of the key generator to test
 * 
 * @author Massimo Coluzzi
 */
public interface KeyGeneratorContract<K extends KeyGenerator> extends Contract<K>
{
    
     /** Random values generator */
     static final Random random = new Random();


     /* ************** */
     /*  TEST METHODS  */
     /* ************** */


     @Test
     default void generated_keys_cannot_be_null_or_empty()
     {

          final int size = random.nextInt( 100 ) + 1;
          final KeyGenerator keyGenerator = sampleValue();
          
          keyGenerator.stream().limit(size).forEach( Require::nonEmpty );

     }

}
