package ch.supsi.dti.isin.cluster;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.nerd4j.utils.lang.IsNot;

import ch.supsi.dti.isin.Contract;


/**
 * Suite to test the contract imposed by the {@link Node} interface.
 *
 * @param <N> implementation of node to test
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public interface NodeContract<N extends Node> extends Contract<N>
{


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */
    

    /**
     * Returns a {@link Node} with the given name.
     * 
     * @param name the name of the node to create
     * @return sample {@link Node} instance
     */
    N sampleValue( String name );


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    default void the_node_name_should_not_be_blank()
    {

        final N sample = sampleValue();
        assertNotNull( sample );
        assertTrue( IsNot.blank(sample.name()) );

    }

    
    @CsvSource({
        "node_A,node_C,<",
        "node_B,node_B,=",
        "node_C,node_A,>"
    })
    @ParameterizedTest(name="compareTo({0},{1}) {2} 0")
    default void the_method_compareTo_should_work_as_expected(
        String name1, String name2, char expected
    )
    {

        final N node1 = sampleValue( name1 );
        final N node2 = sampleValue( name2 );

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

}
