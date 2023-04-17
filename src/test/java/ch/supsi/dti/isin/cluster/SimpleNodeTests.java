package ch.supsi.dti.isin.cluster;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.List;
import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nerd4j.utils.lang.RequirementFailure;


/**
 * Suite to test consistency in class {@link SimpleNode}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class SimpleNodeTests implements NodeContract<SimpleNode>
{

    /** Random values generator */
    private static final Random random = new Random();


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */
    

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleNode sampleValue( String name )
    {

        return SimpleNode.of( name );
        
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public SimpleNode sampleValue()
    {

        return sampleValue( "node" );

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void the_name_factory_method_always_creates_e_new_node()
    {
        
        final int index = random.nextInt( 100 );
        final String name = String.valueOf( index );

        final SimpleNode node1 = SimpleNode.of( name );
        final SimpleNode node2 = SimpleNode.of( name );

        assertEquals( node1, node2 );
        assertNotSame( node1, node2 );
        
    }

    @Test
    public void the_node_name_should_be_as_expected()
    {

        final int index = random.nextInt( 100 );
        final String expected = String.valueOf( index );

        final SimpleNode node = SimpleNode.of( expected );

        assertEquals( expected, node.name() );

    }

    
    @Test
    public void the_index_factory_method_always_creates_e_new_node()
    {

        final int index = random.nextInt( 100 );
        final SimpleNode node1 = SimpleNode.of( index );
        final SimpleNode node2 = SimpleNode.of( index );

        assertEquals( node1, node2 );
        assertNotSame( node1, node2 );
        
    }

    @Test
    public void the_name_of_the_node_created_by_the_index_factory_method_should_be_as_expected()
    {

        final int index = random.nextInt( 100 );
        final String expected = "node_" + index;

        final SimpleNode node = SimpleNode.of( index );

        assertEquals( expected, node.name() );
        
    }

    @Test
    public void the_node_index_cannot_be_negative()
    {

        assertThrows( RequirementFailure.class, () -> SimpleNode.of(-1) );
        
    }

    @CsvSource({
        "10,20,<",
        "15,15,=",
        "20,10,>"
    })
    @ParameterizedTest(name="compareTo({0},{1}) {2} 0")
    public void the_method_compareTo_should_work_as_expected(
        int index1, int index2, char expected
    )
    {

        final SimpleNode node1 = SimpleNode.of( index1 );
        final SimpleNode node2 = SimpleNode.of( index2 );

        final int value = node1.compareTo( node2 );

        switch( expected )
        {
            case '>': assertTrue( value > 0 );
                      break;

            case '<': assertTrue( value < 0 );
                      break;

            case '=': assertEquals( 0, value );

        }
        
    }

    @CsvSource({
        "node_A,node_C,false",
        "node_B,node_B,true",
        "node_C,node_A,false"
    })
    @ParameterizedTest(name="equals({0},{1}) = {2}")
    public void two_nodes_are_equal_iif_they_have_the_same_name(
        String name1, String name2, boolean expected
    )
    {

        final SimpleNode node1 = SimpleNode.of( name1 );
        final SimpleNode node2 = SimpleNode.of( name2 );

        assertEquals( expected, node1.equals(node2) );
        
    }

    @CsvSource({
        "10,20,false",
        "15,15,true",
        "20,10,false"
    })
    @ParameterizedTest(name="equals({0},{1}) = {2}")
    public void two_nodes_are_equal_iif_they_have_the_same_index(
        int index1, int index2, boolean expected
    )
    {

        final SimpleNode node1 = SimpleNode.of( index1 );
        final SimpleNode node2 = SimpleNode.of( index2 );

        assertEquals( expected, node1.equals(node2) );
        
    }

    @Test
    public void method_create_shoud_throw_a_RequirementFailure_if_the_requested_size_is_not_positive()
    {

        assertThrows( RequirementFailure.class, () -> SimpleNode.create(-1) );
        assertThrows( RequirementFailure.class, () -> SimpleNode.create( 0) );

    }

    @Test
    public void method_create_shoud_behave_as_expected()
    {

        final int size = random.nextInt( 100 ) + 1;
        final List<Node> nodes = SimpleNode.create( size );

        assertNotNull( nodes );
        assertFalse( nodes.isEmpty() );

        for( int i = 0; i < size; ++i )
        {
            final Node node = nodes.get( i );

            assertNotNull( node );
            assertEquals( "node_" + i, node.name() );
            
        }

    }

}
