package ch.supsi.dti.isin.key;

import java.util.stream.Stream;

import org.apache.commons.math3.distribution.RealDistribution;
import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;


/**
 * Key generator based on a given real-numbers distribution function.
 *
 * @author Massimo Coluzzi
 */
public class RealDistributionKeyGenerator extends AbstractKeyGenerator
{

    /** The key generator based on a real-numbers distribution function. */
    private final RealDistribution realDistribution;


    /**
     * Constructor with parameters.
     *
     * @param realDistribution the real-numbers distribution function
     * @param size the size of the base dataset to create
     */
    public RealDistributionKeyGenerator( RealDistribution realDistribution, int size )
    {

        super( loadData(realDistribution, size) );

        this.realDistribution = realDistribution;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Loads the data from the given source into an array of keys.
     * 
     * @param realDistribution the real-numbers distribution function
     * @param size the size of the base dataset to create
     * @return array of keys to use in the generator
     */
    private static String[] loadData( RealDistribution realDistribution, int size )
    {

        Require.nonNull( realDistribution, "The real-numbers distribution function is mandatory" );
        Require.toHold( size > 0, "The size of the base dataset must be strictly positive" );

        return Stream
                .generate( realDistribution::sample )
                .map( String::valueOf )
                .limit( size )
                .toArray( String[]::new );

    }


    /* ******************* */
    /*  OBJECT OVERWRITES  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
                this, other,
                o -> o.realDistribution
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {

        return Hashcode.of( realDistribution );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {

        return ToString.of( this )
                .withCustomClassName( KeyGenerator.class.getSimpleName() )
                .print( realDistribution.getClass().getSimpleName() )
                .likeEclipse();

    }

}
