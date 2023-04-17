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

        switch( Require.nonNull(distribution, "The distribution is mandatory") )
        {

            case NORMAL:
                return new RealDistributionKeyGenerator( new NormalDistribution() );

            case UNIFORM:
                return new RealDistributionKeyGenerator(new UniformRealDistribution());

            case CUSTOM:
                final URL source = KeyGenerator.class.getResource( CustomDistributionKeyGenerator.SOURCE_PATH );
                return new CustomDistributionKeyGenerator( source );

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
