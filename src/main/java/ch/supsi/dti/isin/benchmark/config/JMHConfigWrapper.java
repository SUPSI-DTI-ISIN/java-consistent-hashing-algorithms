package ch.supsi.dti.isin.benchmark.config;

import java.io.IOException;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;


/**
 * Configuration wrapper designed to be used as a {@code JMH} state object.
 * 
 * <p>
 * The {@code JMH} benchmark framework manages and injects state object automatically.
 * Only objects annotated with {@link State} are managed by the {@code JMH} framework.
 * 
 * @author Massimo Coluzzi
 */
@State(Scope.Benchmark)
public class JMHConfigWrapper
{
    

    /** The source configuration to wrap. */
    private Config config;


    /**
     * Default constructor.
     * 
     */
    public JMHConfigWrapper()
    {

        super();

        this.config = null;

    }


    /**
     * Since {@code JMH} benchmarks run in another process, previously created objects are not accessible from the other process.
     * Therefore, before every {@code JMH} benchmark, the config objects needed for that specific benchmark are recreated.
     * It is the task of {@code JMH} to run this method see {@link Setup}. 
     *
     * @throws IOException if the {@code YAML} file cannot be opened
     * @throws InvalidConfigException if some elements in the {@code YAML} are not as expected
     */
    @Setup
    public void load() throws IOException, InvalidConfigException
    {
        
        this.config = ConfigLoader.restore();

    }
    

    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */

    
    /**
     * Returns the wrapped configuration.
     * 
     * @return the wrapped configuration
     */
    public Config getConfig()
    {

        return config;

    }

    
    /* ****************** */
    /*  OBJECT OVERRIDES  */
    /* ****************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    public int hashCode()
    {

        return Hashcode.of( config );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {
        
        return Equals.ifSameClass(
            this, other,
            o -> o.config
        );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public String toString()
    {
       
        return String.valueOf( config );

    }

}
