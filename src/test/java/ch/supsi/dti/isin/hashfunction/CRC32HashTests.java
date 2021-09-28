package ch.supsi.dti.isin.hashfunction;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;

/**
 * Test suite for the class {@link CRC32Hash}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CRC32HashTests implements HashFunctionContract<CRC32Hash>
{
    

    /**
     * {@inheritDoc}
     */
    @Override
    public CRC32Hash sampleValue()
    {

        return new CRC32Hash();

    }

}
