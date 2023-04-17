package ch.supsi.dti.isin.benchmark.executor;


/**
 * Exception thrown when a benchmark execution fails.
 * 
 * @author Massimo Coluzzi
 */
public class BenchmarkExecutionException extends RuntimeException
{
    

    /**
     * Constructor with parameters.
     * 
     * @param message the cause of failure
     */
    private BenchmarkExecutionException( Exception cause )
    {

        super( cause );
        
    }


    /**
     * Creates a new {@link BenchmarkExecutionException} with the given cause.
     * 
     * @param cause the cause of failure
     * @return a new exception
     */
    public static BenchmarkExecutionException of( Exception cause )
    {

        return new BenchmarkExecutionException( cause );

    }

}
