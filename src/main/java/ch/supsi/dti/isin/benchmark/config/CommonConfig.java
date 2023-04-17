package ch.supsi.dti.isin.benchmark.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Is;
import org.nerd4j.utils.lang.IsNot;
import org.nerd4j.utils.lang.ToString;

import ch.supsi.dti.isin.key.Distribution;

/**
 * Stores the data related to the "common" section in the configuration file.
 * 
 * @author Massimo Coluzzi
 */
public class CommonConfig extends AbstractConfig<CommonConfig> implements Cloneable
{

    /** Default value for the {@link #gc} property. */
    public static final boolean DEFAULT_GC = true;

    /** Default value for the {@link #outputFolder} property. */
    public static final Path DEFAULT_OUTPUT_FOLDER = Path
        .of( System.getProperty("java.io.tmpdir") )
        .toAbsolutePath()
        .resolve( "ch-bench" );

    /** Default value for the {@link #initNodes} property. */
    public static final List<Integer> DEFAULT_INIT_NODES
    = Collections.unmodifiableList( Arrays.asList(10,100,1000,10_000) );

    /** Default value for the {@link #keyDistributions} property. */
    public static final List<Distribution> DEFAULT_DISTRIBUTIONS
    = Arrays.stream( Distribution.values() ).toList();

    /** Default value for the {@link #hashFunctions} property. */
    public static final List<String> DEFAULT_FUNCTIONS
    = Collections.unmodifiableList( Arrays.asList("Murmur3","XX","MD5","CRC32") );


    /** Defines if the garbage collector should be called before every benchmark. */
    private boolean gc;

    /** Folder containing the benchmark output. */
    private Path outputFolder;

    /** Folder containing the benchmark results. */
    private Path resultsFolder;

    /** Number of initial active nodes. */
    private List<Integer> initNodes;

    /** Statistical distributions of the keys to use during key-related benchmarks. */
    private List<Distribution> keyDistributions;

    /** Hash functions used by the consistent hashing algorithms. */
    private List<String> hashFunctions;

    /** Configuration block describing the number of benchmark iterations. */
    private IterationsConfig iterations;

    /** Configuration block describing properties of time-based benchmarks. */
    private TimeConfig time;

    /**
     * Default constructor.
     * 
     */
    public CommonConfig()
    {

        super();

        this.gc               = DEFAULT_GC;
        this.initNodes        = DEFAULT_INIT_NODES;
        this.hashFunctions    = DEFAULT_FUNCTIONS;
        this.outputFolder     = DEFAULT_OUTPUT_FOLDER;
        this.keyDistributions = DEFAULT_DISTRIBUTIONS;
        this.resultsFolder    = DEFAULT_OUTPUT_FOLDER.resolve( "results" );

        this.iterations       = IterationsConfig.getDefault();
        this.time             = TimeConfig.getDefault();

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
    public static CommonConfig of( ValuePath path, Object source )
    {

        final CommonConfig config = new CommonConfig();
        config.merge( path, source );

        return config;

    }


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * Returns if the GC should be called before every benchmark.
     * 
     * @return {@code true} if the GC should be called before every benchmark
     */
    public boolean isGc()
    {

        return gc;

    }

    /**
     * Returns the path of the folder where to put the benchmarks' output.
     * 
     * @return the path of the folder where to put the benchmarks' output
     */
    public Path getOutputFolder()
    {

        return outputFolder;

    }

    /**
     * Returns the path of the folder where to put the benchmarks' results.
     * 
     * @return the path of the folder where to put the benchmarks' results
     */
    public Path getResultsFolder()
    {

        return resultsFolder;

    }

    /**
     * Returns the list of initial active nodes.
     * 
     * @return the list of initial active nodes
     */
    public List<Integer> getInitNodes()
    {

        return initNodes;

    }

    /**
     * Returns the list of hash functions to use.
     * 
     * @return the list of hash functions to use
     */
    public List<String> getHashFunctions()
    {

        return hashFunctions;

    }

    /**
     * Returns the list of key distributions to use.
     * 
     * @return the list of key distributions to use
     */
    public List<Distribution> getKeyDistributions()
    {

        return keyDistributions;

    }

    /**
     * Returns the iterations configuration block.
     * 
     * @return the iterations configuration block
     */
    public IterationsConfig getIterations()
    {

        return this.iterations;

    }

    /**
     * Returns the time configuration block.
     * 
     * @return the time configuration block
     */
    public TimeConfig getTime()
    {

        return this.time;

    }
    

    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     *  {@inheritDoc}}
     */
    @Override
    void merge( ValuePath path, String key, Object value )
    {

        switch( key )
        {

            case "gc":
                this.gc = ConfigUtils.toBoolean( path, value );
                break;

            case "outputfolder":
                final String outputFolder = ConfigUtils.toString( path, value );
                if( IsNot.blank(outputFolder) )
                {
                    this.outputFolder = Path.of( outputFolder ).toAbsolutePath();
                    this.resultsFolder = this.outputFolder.resolve( "results" );
                }
                break;

            case "initnodes":
                this.initNodes = mergeInitNodes( path, value );
                break;

            case "hashfunctions":
                this.hashFunctions = mergeFunctions( path, value );
                break;

            case "keydistributions":
                this.keyDistributions = mergeDistributions( path, value );
                break;

            case "iterations":
                this.iterations.merge( path, value );
                break;

            case "time":
                this.time.merge( path, value );
                break;

        }

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Creates a list of initial nodes.
     * 
     * @param path   path of the property
     * @param source the configuration source
     * @return list of initial nodes
     */
    private List<Integer> mergeInitNodes( ValuePath path, Object source )
    {

        final List<Integer> initNodes = ConfigUtils.toList( path, Integer.class, source );
        for( int i = 0; i < initNodes.size(); ++i )
            requireGreaterThanZero( path.append(i), initNodes.get(i) );

        return Collections.unmodifiableList( initNodes );

    }

    /**
     * Creates a list of function names.
     * 
     * @param path   path of the property
     * @param source the configuration source
     * @return list of function names
     */
    private List<String> mergeFunctions( ValuePath path, Object source )
    {

        final List<String> sourceFunctions = ConfigUtils.toList( path, String.class, source );
        final List<String> hashFunctions = new ArrayList<>( sourceFunctions.size() );
        for( int i = 0; i < sourceFunctions.size(); ++i )
        {
            final String functionName = sourceFunctions.get( i );
            if( Is.blank(functionName) )
                throw MissingValueException.of( path.append(i) );

            final String functionKey = ConfigUtils.normalize( functionName ).replace( "hash", "" );
            hashFunctions.add( functionKey );

        }

        return Collections.unmodifiableList( hashFunctions );

    }

    /**
     * Creates a list of key distributions.
     * 
     * @param path   path of the property
     * @param source the configuration source
     * @return list of key distributions
     */
    private List<Distribution> mergeDistributions( ValuePath path, Object source )
    {

        final List<Distribution> sourceDistributions = ConfigUtils.toEnumList( path, Distribution.class, source, Distribution::of );
        return Collections.unmodifiableList( sourceDistributions );

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

        return Hashcode.of( gc, hashFunctions, initNodes, outputFolder );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {
        
        return Equals.ifSameClass(
            this, other,
            o -> o.gc,
            o -> o.hashFunctions,
            o -> o.initNodes,
            o -> o.outputFolder
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
            .print( "gc", gc )
            .print( "init-nodes", initNodes )
            .print( "hash-functions", hashFunctions )
            .print( "output-folder", outputFolder )
            .using( "{", ":", ",", "}" );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public CommonConfig clone()
    {

        try{

            final CommonConfig config = (CommonConfig) super.clone();
            return config;

        }catch( CloneNotSupportedException ex )
        {

            return null;

        }

    }

}
