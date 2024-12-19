package ch.supsi.dti.isin.key;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.apache.commons.math3.distribution.NormalDistribution;
import org.apache.commons.math3.distribution.RealDistribution;
import org.apache.commons.math3.distribution.UniformRealDistribution;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
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

        return new RealDistributionKeyGenerator( distribution, KeyGenerator.DEFAULT_SIZE );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void constructor_needs_a_distribution()
    {

        assertThrows( RequirementFailure.class, () -> new RealDistributionKeyGenerator(null, 10) );

    }

    @ValueSource(ints={-10,-1,0})
    @ParameterizedTest(name="RealDistributionKeyGenerator(distribution,{0}) -> RequirementFailure")
    public void size_must_be_greater_than_0( int size )
    {

        final RealDistribution distribution = new UniformRealDistribution();
        assertThrows( RequirementFailure.class, () -> new RealDistributionKeyGenerator(distribution, size) );

    }

}
