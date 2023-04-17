package ch.supsi.dti.isin.benchmark.config;

import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Is;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;


/**
 * Stores the data related to the "benchmarks" section of the config file.
 * 
 * @author Massimo Coluzzi
 */
public class BenchmarkConfig extends AbstractConfig<BenchmarkConfig>
{
    

    /** Path of the property containing the benchmark configurations. */
    private final ValuePath path;
    
    /** Name of the benchmark. */
    private String name;

    /** Overwrites of the common configuration. */
    private CommonConfig common;

    /** Arguments used to initialize the benchmark. */
    private Map<String,Object> args;


    /**
     * Constructor with parameters.
     * 
     * @param common the common configuration to overwrite
     * @param path   the property containing the benchmark configurations
     */
    private BenchmarkConfig( CommonConfig common, ValuePath path )
    {

        super();
        
        this.common = Require.nonNull(
            common, "The common configuration is mandatory"
        ).clone();
        this.path = Require.nonNull(
            path,
            "The path to the benchmark property in the configuration file is mandatory"
        );

        this.name = null;
        this.args = new HashMap<>();

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new configuration from the given source.
     * 
     * @param path   the path of the property
     * @param common the common configuration to overwrite
     * @param source the source of the configuration
     * @return a new configuration
     */
    public static BenchmarkConfig of( ValuePath path, CommonConfig common, Object source )
    {

        final BenchmarkConfig config = new BenchmarkConfig( common, path );
        config.merge( path, source );
        config.validate( path );

        return config;

    }


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * Returns the algorithm's name.
     * 
     * @return the algorithm's name
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the path pf the related property.
     * 
     * @return the path of the related property
     */
    public ValuePath getPath()
    {
        return path;
    }

    /**
     * Returns the common configuration overwrites.
     * 
     * @return the common configuration overwrites
     */
    public CommonConfig getCommon()
    {
        return common;
    }

    /**
     * Returns the algorithm's init arguments.
     * 
     * @return the algorithm's init arguments
     */
    public Map<String,Object> getArgs()
    {
        return args;
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

            case "name":
                this.name = ConfigUtils.toNormalizedString( path, required(path,value) );
                break;

            case "args":
                this.args = ConfigUtils.toArgs( path, value );
                break;

            case "common":
                this.common.merge( path, value );
                break;

        }

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Checks for all the mandatory fields to be defined.
     * 
     * @param path base path of the configuration
     */
    private void validate( ValuePath path )
    {

        if( Is.blank(this.name) )
            throw MissingValueException.of( path.append("name") );

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
        
        return Hashcode.of( name, common, args );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
            this, other,
            o -> o.name,
            o -> o.common,
            o -> o.args
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
            .print( "name", name )
            .print( "common", common )
            .print( "args", args )
            .using( "{", ":", ",", "}" );

    }

}
