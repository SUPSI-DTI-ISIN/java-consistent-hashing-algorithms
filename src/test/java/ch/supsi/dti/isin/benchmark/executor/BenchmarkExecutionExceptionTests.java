package ch.supsi.dti.isin.benchmark.executor;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * Suite to test class {@link BenchmarkExecutionException}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class BenchmarkExecutionExceptionTests
{


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void factory_method_of_returns_a_proper_cause()
    {

        final Exception cause = new Exception( "test" );
        final BenchmarkExecutionException exception = assertDoesNotThrow(
            () -> BenchmarkExecutionException.of( cause )
        );
        assertSame( cause, exception.getCause() );
        
    }

   
}
