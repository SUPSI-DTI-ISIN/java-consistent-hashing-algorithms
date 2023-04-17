package ch.supsi.dti.isin.key;

import org.apache.commons.math3.distribution.RealDistribution;
import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Require;
import org.nerd4j.utils.lang.ToString;

import java.util.Iterator;
import java.util.stream.Stream;


/**
 * Key generator based on a given real-numbers distribution function.
 *
 * @author Massimo Coluzzi
 */
public class RealDistributionKeyGenerator implements KeyGenerator
{

    /** The key generator based on a real-numbers distribution function. */
    private final RealDistribution realDistribution;


    /**
     * Constructor with parameters.
     *
     * @param realDistribution the real-numbers distribution function.
     */
    public RealDistributionKeyGenerator( RealDistribution realDistribution )
    {

        super();

        this.realDistribution = Require.nonNull( realDistribution, "The real-numbers distribution function is mandatory" );

    }


    /* *************** */
    /*  PUBLIC METHOD  */
    /* *************** */


    /**
     * {@inheritDoc}
     */
    public Stream<String> stream()
    {

        return Stream
                .generate( realDistribution::sample )
                .map( String::valueOf );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<String> iterator()
    {

        return stream().iterator();

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
