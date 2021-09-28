package ch.supsi.dti.isin.cluster;


import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.nerd4j.utils.lang.RequirementFailure;


/**
 * Suite to test consistency in class {@link Indirection}
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class IndirectionTests
{

    /** Random values generator */
    private static final Random random = new Random();


    /* *************** */
    /*  TEST METHORDS  */
    /* *************** */


    @Test
    public void the_initial_size_cannot_be_negativa()
    {

        assertThrows( RequirementFailure.class, () -> new Indirection(-1) );

    }

    @ValueSource(ints={0,1,10,100})
    @ParameterizedTest(name="new Indirection({0}) -> OK")
    public void if_the_size_is_not_negative_the_Indirection_should_be_created( int size )
    {

        assertDoesNotThrow( () -> new Indirection(size) );

    }

    @Test
    public void the_initial_size_and_capacity_should_be_zero()
    {

        final int size = random.nextInt( 100 );
        final Indirection indirection = new Indirection( size );

        assertEquals( 0, indirection.size() );
        assertEquals( 0, indirection.capacity() );

    }

    @Test
    public void cannot_map_a_null_node()
    {

        final Indirection indirection = new Indirection( 10 );
        assertThrows( RequirementFailure.class, () -> indirection.put(null,0) );

    }

    @Test
    public void when_a_mapping_is_created_the_size_should_increase()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        
        assertEquals( 0, indirection.size() );
        indirection.put( node, 0 );
        assertEquals( 1,  indirection.size() );

    }

    @Test
    public void when_the_bucket_equals_the_capacity_the_capacity_should_increase()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        
        assertEquals( 0, indirection.capacity() );
        indirection.put( node, 0 );
        assertEquals( 1,  indirection.capacity() );

    }

    @Test
    public void if_a_mapping_exists_should_be_found_by_node()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );

        indirection.put( node, 0 );
        assertEquals( 0,  indirection.get(node) );

    }

    @Test
    public void if_a_mapping_exists_should_be_found_by_bucket()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );

        indirection.put( node, 0 );
        assertEquals( node,  indirection.get(0) );

    }

    @Test
    public void a_node_cannot_be_mapped_twice()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );

        indirection.put( node, 0 );
        assertThrows( RequirementFailure.class, () -> indirection.put(node,1) );

    }

    @Test
    public void a_bucket_cannot_be_mapped_twice()
    {

        final Node node0 = SimpleNode.of( "node" );
        final Node node1 = SimpleNode.of( 1 );
        final Indirection indirection = new Indirection( 10 );

        indirection.put( node0, 0 );
        assertThrows( RequirementFailure.class, () -> indirection.put(node1,0) );

    }

    @Test
    public void if_a_mapping_exists_can_be_removed_by_node()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( 1,  indirection.size() );
        indirection.remove( node );
        assertEquals( 0, indirection.size() );

    }

    @Test
    public void when_a_node_is_removed_the_related_bucket_is_returned()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( 0, indirection.remove(node) );
        

    }

    @Test
    public void if_a_mapping_exists_can_be_removed_by_bucket()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( 1,  indirection.size() );
        indirection.remove( 0 );
        assertEquals( 0, indirection.size() );

    }


    @Test
    public void when_a_bucket_is_removed_the_related_node_is_returned()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( node, indirection.remove(0) );
        

    }

    @Test
    public void if_a_mapping_does_not_exist_cannot_be_removed_by_node()
    {

        final Node node0 = SimpleNode.of( "node" );
        final Node node1 = SimpleNode.of( 1 );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node0, 0 );

        assertEquals( 1,  indirection.size() );
        assertThrows( RequirementFailure.class, () -> indirection.remove(node1) );

    }

    @Test
    public void if_a_mapping_does_not_exist_cannot_be_removed_by_bucket()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( 1,  indirection.size() );
        assertThrows( RequirementFailure.class, () -> indirection.remove(1) );

    }

    @Test
    public void the_bucket_must_be_between_zero_and_capacity()
    {

        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        assertThrows( RequirementFailure.class, () -> indirection.put(node, 1) );

    }

    @Test
    public void if_the_removed_bucket_is_the_greatest_one_capacity_should_decrease()
    {


        final Node node = SimpleNode.of( "node" );
        final Indirection indirection = new Indirection( 10 );
        indirection.put( node, 0 );

        assertEquals( 1,  indirection.capacity() );
        indirection.remove( 0 );
        assertEquals( 0, indirection.capacity() );

    }

    @Test
    public void if_there_were_more_removals_capacity_should_decrease_accordingly()
    {


        final int size = 10;
        final Indirection indirection = new Indirection( 10 );

        final Node[] nodes = new Node[size];
        for( int i = 0; i < size; ++ i )
        {
            nodes[i] = SimpleNode.of( i );
            indirection.put( nodes[i], i );
        }

        for( int i = size - 2; i >= size >> 1; --i )
            indirection.remove( i );
        
        assertEquals( (size >> 1) + 1,  indirection.size() );
        assertEquals( size,  indirection.capacity() );

        indirection.remove( size - 1 );

        assertEquals( size >> 1, indirection.size() );
        assertEquals( size >> 1, indirection.capacity() );

    }

}
