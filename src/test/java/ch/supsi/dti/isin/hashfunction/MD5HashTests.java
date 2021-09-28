package ch.supsi.dti.isin.hashfunction;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * Test suite for the class {@link MD5Hash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class MD5HashTests implements HashFunctionContract<MD5Hash>
{
    

    /**
     * {@inheritDoc}
     */
    @Override
    public MD5Hash sampleValue()
    {

        return new MD5Hash();

    }

}
