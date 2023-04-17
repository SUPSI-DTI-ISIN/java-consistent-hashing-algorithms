package ch.supsi.dti.isin.benchmark.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * Suite to test the {@link ValuePath} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ValuePathTests
{


    private static final Random random = new Random();


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_root_path_should_be_empty()
    {

        final ValuePath root = ValuePath.root();
        assertEquals( "", root.toString() );
        
    }

    @Test
    public void invoking_two_times_the_root_method_should_return_two_different_instances()
    {

        final ValuePath root1 = ValuePath.root();
        final ValuePath root2 = ValuePath.root();

        assertEquals( root1, root2 );
        assertNotSame(root1, root2 );

    }

    @Test
    public void first_level_paths_should_just_contain_the_provided_name()
    {

        final ValuePath path = ValuePath.root().append( "name" );

        assertEquals( "name", path.toString() );

    }

    @Test
    public void first_level_paths_should_just_contain_the_provided_index()
    {

        final int index = random.nextInt( 100 );
        final ValuePath path = ValuePath.root().append( index );

        final String expected = "[" + index + "]";
        assertEquals( expected, path.toString() );

    }
    
    @Test
    public void second_level_name_paths_should_be_separated_by_a_dot()
    {

        final ValuePath path = ValuePath.root()
            .append( "parent" )
            .append( "child" );

        assertEquals( "parent.child", path.toString() );

    }
    
    @Test
    public void second_level_index_paths_should_not_have_dots()
    {

        final int index = random.nextInt( 100 );
        final ValuePath path = ValuePath.root()
            .append( "parent" )
            .append( index );

        final String expected = "parent[" + index + "]";
        assertEquals( expected, path.toString() );

    }
    
    @Test
    public void combining_names_and_indexes_should_result_as_expected()
    {

        final int index = random.nextInt( 100 );
        final ValuePath path = ValuePath.root()
            .append( "parent" )
            .append( index )
            .append( "child" );

        final String expected = "parent[" + index + "].child";
        assertEquals( expected, path.toString() );

    }

}
