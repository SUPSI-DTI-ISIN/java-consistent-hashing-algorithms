package ch.supsi.dti.isin.benchmark.config;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.ToString;

/**
 * Stores the data related to the "common.iterations" section of the config file.
 * 
 * @author Massimo Coluzzi
 */
public class IterationsConfig extends AbstractConfig<IterationsConfig>
{

    /** Default value for the "warmup" property. */
    public static final int DEFAULT_WARMUP = 5;
    
    /** Default value for the "execution" property. */
    public static final int DEFAULT_EXECUTION = 5;
    

    /**
     * Number of non-recorded iterations of a benchmark. Used to warm up the JVM.
     * The warm-up iterations will not be run for benchmarks that doesn't benefit from it.
     * Default value is 5.
     */
    private int warmup;

    /**
     * How many times a single benchmark will be run.
     * Default value is 5.
     */
    private int execution;


    /**
     * Default constructor.
     * 
     */
    private IterationsConfig()
    {

        super();

        this.warmup    = DEFAULT_WARMUP;
        this.execution = DEFAULT_EXECUTION;

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new configuration with default values.
     * 
     * @return a new configuration with default values
     */
    public static IterationsConfig getDefault()
    {

        return new IterationsConfig();
        
    }

    /**
     * Creates a new configuration from the given source.
     * 
     * @param path   the path of the property
     * @param source the source of the configuration
     * @return a new configuration
     */
    public static IterationsConfig of( ValuePath path, Object source )
    {

        final IterationsConfig config = new IterationsConfig();
        config.merge( path, source );

        return config;

    }


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * Returns the warmup iterations.
     * 
     * @return the warmup iterations.
     */
    public int getWarmup()
    {
        return warmup;
    }

    /**
     * Returns the execution iterations.
     * 
     * @return the execution iterations.
     */
    public int getExecution()
    {
        return execution;
    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    void merge( ValuePath path, String key, Object value )
    {
        
        switch( key )
        {

            case "warmup":
                this.warmup = requireGreaterOrEqualToZero( path, ConfigUtils.toInt(path, value) );
                break;

            case "execution":
                this.execution = requireGreaterThanZero( path, ConfigUtils.toInt(path, value) );
                break;

        }

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
        
        return Hashcode.of( warmup, execution );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
            this, other,
            o -> o.warmup,
            o -> o.execution
        );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public String toString()
    {

        return ToString.of( this )
            .withNoClassName()
            .print( "warmup", warmup )
            .print( "execution", execution )
            .using( "{", ":", ",", "}" );

    }
    
}
