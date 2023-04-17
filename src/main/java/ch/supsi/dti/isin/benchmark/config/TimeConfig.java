package ch.supsi.dti.isin.benchmark.config;

import java.util.concurrent.TimeUnit;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.ToString;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.options.TimeValue;

/**
 * Stores the data related to the "common.time" section of the config file.
 * 
 * @author Massimo Coluzzi
 */
public class TimeConfig extends AbstractConfig<TimeConfig>
{

    /** Default value for the "unit" property. */
    public static final TimeUnit DEFAULT_UNIT = TimeUnit.NANOSECONDS;
    
    /** Default value for the "mode" property. */
    public static final Mode DEFAULT_MODE = Mode.AverageTime;
  
    /** Default value for the "warmup" property. */
    public static final TimeValue DEFAULT_WARMUP = TimeValue.seconds( 5 );

    /** Default value for the "execution" property. */
    public static final TimeValue DEFAULT_EXECUTION = TimeValue.seconds( 5 );
    

    /** The time unit the results refer to. */
    private TimeUnit unit;

    /** Defines how the execution time is measured. */
    private Mode mode;

    /** Number of seconds every warmup iteration should last. */
    private TimeValue warmup;

    /** Number of seconds every execution iteration should last. */
    private TimeValue execution;


    /**
     * Default constructor.
     * 
     */
    private TimeConfig()
    {

        super();

        this.unit      = DEFAULT_UNIT;
        this.mode      = DEFAULT_MODE;
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
    public static TimeConfig getDefault()
    {

        return new TimeConfig();

    }

    /**
     * Creates a new configuration from the given source.
     * 
     * @param path   the path of the property
     * @param source the source of the configuration
     * @return a new configuration
     */
    public static TimeConfig of( ValuePath path, Object source )
    {

        final TimeConfig config = new TimeConfig();
        config.merge( path, source );

        return config;

    }


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * Returns the time unit.
     * 
     * @return the time unit.
     */
    public TimeUnit getUnit()
    {
        return unit;
    }

    /**
     * Returns the time acquisition mode.
     * 
     * @return the time actuisition mode
     */
    public Mode getMode()
    {
        return mode;
    }

    /**
     * Returns the warmup iterations.
     * 
     * @return the warmup iterations.
     */
    public TimeValue getWarmup()
    {
        return warmup;
    }

    /**
     * Returns the execution iterations.
     * 
     * @return the execution iterations.
     */
    public TimeValue getExecution()
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

            case "unit":
                this.unit = ConfigUtils.toEnum( path, value, TimeUnit.class, v -> TimeUnit.valueOf(ConfigUtils.normalize(v).toUpperCase()) );
                break;

            case "mode":
                this.mode = ConfigUtils.toEnum( path, value, Mode.class, Mode::valueOf );
                break;

            case "warmup":
                final int warmupSeconds = requireGreaterThanZero( path, ConfigUtils.toInt(path, value) );
                this.warmup = TimeValue.seconds( warmupSeconds );
                break;

            case "execution":
                final int executionSeconds = requireGreaterThanZero( path, ConfigUtils.toInt(path, value) );
                this.execution = TimeValue.seconds( executionSeconds );
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
        
        return Hashcode.of( unit, mode, warmup, execution );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
            this, other,
            o -> o.unit,
            o -> o.mode,
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
            .print( "unit", unit )
            .print( "mode", mode )
            .print( "warmup", warmup )
            .print( "execution", execution )
            .using( "{", ":", ",", "}" );

    }
    
}
