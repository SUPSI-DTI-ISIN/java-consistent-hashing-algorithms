package ch.supsi.dti.isin.consistenthash.multiprobe;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

/**
 * Test suite for the class {@link Point}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class PointTests
{

    private static final Random random = new Random();

    @Test
    public void when_a_point_is_created_the_related_values_should_be_stored()
    {

        final long hash = random.nextLong();
        final String resource = "resource_" + random.nextInt( 100 );
        
        final Point point = new Point( resource, hash );
        assertEquals( hash, point.hash );
        assertEquals( resource, point.resource );
        
    }

    @CsvSource({
        "100,1000,-1",
        "1000,100,1",
        "100,100,0"
    })
    @ParameterizedTest(name="{0}.compareTo({1} = {2}")
    public void comparison_should_work_as_expected( long hash1, long hash2, int expected )
    {

        final Point point1 = new Point( "resource_" + hash1, hash1 );
        final Point point2 = new Point( "resource_" + hash2, hash2 );

        assertEquals( expected, point1.compareTo(point2) );

    }

    @CsvSource({
        "100,1000,900",
        "1000,100,900",
        "100,100,0"
    })
    @ParameterizedTest(name="{0}.compareTo({1} = {2}")
    public void distance_should_work_as_expected( long hash1, long hash2, long expected )
    {

        final Point point1 = new Point( "resource_" + hash1, hash1 );

        assertEquals( expected, point1.distance(hash2) );

    }


}
