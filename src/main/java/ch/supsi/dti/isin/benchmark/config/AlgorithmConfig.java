package ch.supsi.dti.isin.benchmark.config;

import java.util.HashMap;
import java.util.Map;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Is;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;


/**
 * Stores the data related to the "algorithms" section of the config file.
 * 
 * @author Massimo Coluzzi
 */
public class AlgorithmConfig extends AbstractConfig<AlgorithmConfig>
{

    
    /** Path of the property containing the algorithm configurations. */
    private final ValuePath path;
    
    /** Name of the algorithm. */
    private String name;

    /** Arguments used to initialize the algorithm. */
    private Map<String,Object> args;


    /**
     * Constructor with parameters.
     * 
     * @param path Path of the property containing the algorithm configurations
     */
    private AlgorithmConfig( ValuePath path )
    {

        super();

        this.path = Require.nonNull( path, "The path to the algorithm property in the configuration file is mandatory" );
        
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
     * @param source the source of the configuration
     * @return a new configuration
     */
    public static AlgorithmConfig of( ValuePath path, Object source )
    {

        final AlgorithmConfig config = new AlgorithmConfig( path );
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
     * @return the algorithm's name.
     */
    public String getName()
    {
        return name;
    }

    /**
     * Returns the path of the related property.
     * 
     * @return the path of the related property
     */
    public ValuePath getPath()
    {
        return path;
    }

    /**
     * Returns the algorithm's init arguments.
     * 
     * @return the algorithm's init arguments.
     */
    public Map<String,Object> getArgs()
    {
        return args;
    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}
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
        
        return Hashcode.of( name, args );

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
            .print( "args", args )
            .using( "{", ":", ",", "}" );

    }

}
