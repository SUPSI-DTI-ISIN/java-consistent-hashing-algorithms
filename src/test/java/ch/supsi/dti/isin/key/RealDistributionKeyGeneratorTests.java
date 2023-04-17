package ch.supsi.dti.isin.key;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

/**
 * Test suite for the class {@link RealDistributionKeyGenerator}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class RealDistributionKeyGeneratorTests implements KeyGeneratorContract<RealDistributionKeyGenerator>
{

    /** Random values generator */
    static final Random random = new Random();

    /** Distribution to use in constructors */
    static final RealDistribution distribution = new NormalDistribution();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public RealDistributionKeyGenerator sampleValue( )
    {

        return new RealDistributionKeyGenerator( distribution );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void constructor_needs_a_distribution()
    {

        assertThrows( RequirementFailure.class, () -> new RealDistributionKeyGenerator(null) );

    }

}
