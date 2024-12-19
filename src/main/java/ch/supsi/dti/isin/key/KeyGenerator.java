package ch.supsi.dti.isin.key;

import java.net.URL;
import java.util.Iterator;
import java.util.stream.Stream;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.nerd4j.utils.lang.Require;


/**
 * Tool to generate text keys based on the given distribution.
 *
 * @author Massimo Coluzzi
 */
public interface KeyGenerator
{

    /** The default size of the key base dataset. */
    public static final int DEFAULT_SIZE = 20_000_000;


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */

    
    /**
     * Creates a new key generator with the given distribution.
     *
     * @param distribution the distribution of the values in the dataset
     * @return a new key generator
     */
    static KeyGenerator create( Distribution distribution )
    {

        return create( distribution, DEFAULT_SIZE );
        
    }

    /**
     * Creates a new key generator with the given distribution.
     *
     * @param distribution the distribution of the values in the dataset
     * @param size the size of the base dataset
     * @return a new key generator
     */
    static KeyGenerator create( Distribution distribution, int size )
    {

        switch( Require.nonNull(distribution, "The distribution is mandatory") )
        {

            case NORMAL:
                return new RealDistributionKeyGenerator( new NormalDistribution(), size );

            case UNIFORM:
                return new RealDistributionKeyGenerator(new UniformRealDistribution(), size );

            case CUSTOM:
                final URL source = KeyGenerator.class.getResource( CustomDistributionKeyGenerator.SOURCE_PATH );
                return new CustomDistributionKeyGenerator( source, size );

            default:
                throw new IllegalArgumentException( "Unable to handle distribution of type " + distribution );

        }

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * Returns an infinite stream of keys.
     *
     * @return an infinite stream of keys.
     */
    Stream<String> stream();

    /**
     * Returns an infinite iterator of keys.
     *
     * @return an infinite iterator of keys.
     */
    Iterator<String> iterator();

}
