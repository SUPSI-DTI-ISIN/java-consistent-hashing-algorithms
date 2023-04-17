package ch.supsi.dti.isin.benchmark.config;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Is;
import org.nerd4j.utils.lang.IsNot;
import org.nerd4j.utils.lang.ToString;


/**
 * Stores the data related to the overall configuration file.
 * 
 * @author Massimo Coluzzi
 */
public class Config extends AbstractConfig<Config>
{

    /**
     * The default location where to store the loaded config {@code YAML}
     * for other processes to access.
     */
    public static final Path STORE_PATH
    = CommonConfig.DEFAULT_OUTPUT_FOLDER
        .resolve( "config.yaml" );


    /** Configurations common to all benchmarks. */
    private CommonConfig common;

    /** Algorithms related configurations. */
    private List<AlgorithmConfig> algorithms;

    /** Benchmarks specific configurations. */
    private List<BenchmarkConfig> benchmarks;


    /**
     * Default constructor.
     * 
     */
    private Config()
    {

        super();

        this.common = new CommonConfig();
        this.algorithms = Collections.emptyList();
        this.benchmarks = Collections.emptyList();

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new configuration from the given source.
     * 
     * @param source the source of the configuration
     * @return a new configuration
     */
    public static Config of( Object source )
    {

        final Config config = new Config();
        config.merge( ValuePath.root(), source );

        return config;

    }


    /* ***************** */
    /*  EXTENSION HOOKS  */
    /* ***************** */


    /**
     * {@inheritDoc}}
     */
    @Override
    void merge( ValuePath path, Object source )
    {

        if( source == null )
            return;

        final Map<String,Object> sourceMap = ConfigUtils.toSourceMap( path, source );
        if( Is.empty(sourceMap) )
            return;
        
        final String commonKey = "common";
        final Object common = sourceMap.get( commonKey );
        if( common != null )
            merge( path.append(commonKey), commonKey, common );
        
        final String algorithmsKey = "algorithms";
        final Object algorithms = sourceMap.get( algorithmsKey );
        if( algorithms != null )
            merge( path.append(algorithmsKey) ,algorithmsKey, algorithms );
        
        final String benchmarksKey = "benchmarks";
        final Object benchmarks = sourceMap.get( benchmarksKey );
        if( benchmarks != null )
            merge( path.append(benchmarksKey), benchmarksKey, benchmarks );

    }


    /* ******************* */
    /*  GETTERS & SETTERS  */
    /* ******************* */


    /**
     * Returns configurations common to all benchmarks.
     * 
     * @return configurations common to all benchmarks
     */
    public CommonConfig getCommon()
    {

        return common;

    }

    /**
     * Returns algorithms related configurations.
     * 
     * @return algorithms related configurations
     */
    public List<AlgorithmConfig> getAlgorithms()
    {

        return Collections.unmodifiableList( algorithms );

    }

    /**
     * Returns benchmarks specific configurations.
     * 
     * @return benchmarks specific configurations
     */
    public List<BenchmarkConfig> getBenchmarks()
    {

        return Collections.unmodifiableList( benchmarks );

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

            case "common":
                this.common.merge( path, ConfigUtils.toSourceMap(path,value) );
                break;

            case "algorithms":
                this.algorithms = mergeAlgorithms( path, ConfigUtils.toList(path,Object.class, value) );
                break;

            case "benchmarks":
                this.benchmarks = mergeBenchmarks( path, ConfigUtils.toList(path,Object.class,value) );
                break;

        }

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Creates a list of {@link AlgorithmConfig} from the given list of configurations sources.
     * 
     * @param path    path of the property
     * @param sources the configuration sources
     * @return list of algorithm configurations
     */
    private List<AlgorithmConfig> mergeAlgorithms( ValuePath path, List<Object> sources )
    {

        final List<AlgorithmConfig> algorithms = new ArrayList<>();
        for( int i = 0; i < sources.size(); ++i )
        {

            final AlgorithmConfig algorithm = AlgorithmConfig.of( path.append(i), sources.get(i) );
            algorithms.add( algorithm );

        }

        return algorithms;

    }

    /**
     * Creates a list of {@link BenchmarkConfig} from the given list of configurations sources.
     * 
     * @param path    path of the property
     * @param sources the configuration sources
     * @return list of benchmark configurations
     */
    private List<BenchmarkConfig> mergeBenchmarks( ValuePath path, List<Object> sources )
    {

        final List<BenchmarkConfig> benchmarks = new ArrayList<>();
        for( int i = 0; i < sources.size(); ++i )
        {

            final ValuePath benchmarkPath = path.append( i );
            final Map<String,Object> map = ConfigUtils.toSourceMap( benchmarkPath, sources.get(i) );
            if( IsNot.empty(map) )
            {

                final BenchmarkConfig algorithm = BenchmarkConfig.of( benchmarkPath, common, map );
                benchmarks.add( algorithm );

            }

        }

        return benchmarks;

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

        return Hashcode.of( common, algorithms, benchmarks );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {
        
        return Equals.ifSameClass(
            this, other,
            o -> o.common,
            o -> o.algorithms,
            o -> o.benchmarks
        );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public String toString()
    {
       
        return ToString.of( this )
            .print( "common", common )
            .print( "algorithms", algorithms )
            .print( "benchmarks", benchmarks )
            .using( "{", ":", ",", "}" );

    }

}
