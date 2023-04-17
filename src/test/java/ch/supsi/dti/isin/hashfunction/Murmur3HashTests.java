package ch.supsi.dti.isin.hashfunction;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * Test suite for the class {@link Murmur3Hash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Murmur3HashTests implements HashFunctionContract<Murmur3Hash>
{


    /**
     * {@inheritDoc}
     */
    @Override
    public String expectedName()
    {

        return "MurMur3";

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Murmur3Hash sampleValue()
    {

        return new Murmur3Hash();

    }

}
