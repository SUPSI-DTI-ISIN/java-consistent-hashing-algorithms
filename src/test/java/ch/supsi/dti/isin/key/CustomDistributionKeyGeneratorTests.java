package ch.supsi.dti.isin.key;

import static org.junit.jupiter.api.Assertions.assertThrows;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Iterator;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.nerd4j.utils.lang.RequirementFailure;


/**
 * Test suite for the class {@link CustomDistributionKeyGenerator}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class CustomDistributionKeyGeneratorTests implements KeyGeneratorContract<CustomDistributionKeyGenerator>
{

    /** Random values generator */
    static final Random random = new Random();

    /** Path to use in constructors */
    static final URL url = CustomDistributionKeyGeneratorTests.class.getResource(
        CustomDistributionKeyGenerator.SOURCE_PATH
    );


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    @Override
    public CustomDistributionKeyGenerator sampleValue()
    {

        return new CustomDistributionKeyGenerator( url, KeyGenerator.DEFAULT_SIZE );

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @NullSource
    @ParameterizedTest(name="CustomDistributionKeyGenerator({0},10) -> RequirementFailure")
    public void constructor_needs_a_file_name( URL file )
    {

        assertThrows( RequirementFailure.class, () -> new CustomDistributionKeyGenerator(file, 10) );

    }

    @ValueSource(ints={-10,-1,0})
    @ParameterizedTest(name="CustomDistributionKeyGenerator(file,{0}) -> RequirementFailure")
    public void size_must_be_greater_than_0( int size ) throws MalformedURLException 
    {

        final URL file = Path.of(".").toUri().toURL();
        assertThrows( RequirementFailure.class, () -> new CustomDistributionKeyGenerator(file, size) );

    }

    @Test
    public void an_unexisting_file_should_throw_exception_at_construction_time()
    {

        final URL notExistentFile = CustomDistributionKeyGeneratorTests.class.getResource("/my-unexisting.file");
        assertThrows( RequirementFailure.class, () -> new CustomDistributionKeyGenerator( notExistentFile, 10 ));

    }

    @Test
    public void keys_should_start_with_the_iteration_index()
    {

        final CustomDistributionKeyGenerator generator = sampleValue();
        final Iterator<String> iter = generator.iterator();
        final int iterationSize = generator.size();

        for( int i = 0; i < iterationSize; ++i )
            iter.next().startsWith( "0" );

        for( int i = 0; i < iterationSize; ++i )
            iter.next().startsWith( "1" );
            
    }

}
