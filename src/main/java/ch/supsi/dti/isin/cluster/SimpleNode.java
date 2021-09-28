package ch.supsi.dti.isin.cluster;


import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;
import org.nerd4j.utils.lang.Require;


/**
 * Simple implementation of the {@link Node} interface.
 * 
 * @author Massimo Coluzzi
 */
public class SimpleNode implements Node
{

    /** Unique name of the node. */
    private final String name;


    /**
     * Constructor with parameters.
     * 
     * @param name unique name of the node
     */
    private SimpleNode( String name )
    {

        super();

        this.name = Require.nonBlank( name, "The name cannot be empty  or blank" );

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Creates a new {@link SimpleNode} with the given name.
     * 
     * @param name the name of the node (cannot be empty or blank).
     * @return a new {@link SimpleNode}
     */
    public static SimpleNode of( String name )
    {

        return new SimpleNode( name );

    }

    /**
     * Creates a new {@link SimpleNode} for the given index.
     * <p>
     * The name of the node will be in the form {@code "node_<index>"}
     * 
     * @param index the index of the node to create.
     * @return a new {@link SimpleNode}
     */
    public static SimpleNode of( int index )
    {

        final String name = "node_" + Require.trueFor( index, index >= 0, "The node index canno be negative" );
        return new SimpleNode( name );

    }

    /**
     * Creates the given number of nodes.
     * <p>
     * The name of each node will be in the form {@code "node_<index>"}
     * with the value of {@code index} in range {@code [0, size-1]}.
     * 
     * @param size the number of nodes to create
     * @return a list of nodes
     */
    public static List<SimpleNode> create( int size )
    {

        Require.toHold( size > 0, "The requested number of nodes must be strict positive" );
        return IntStream
            .range( 0, size )
            .mapToObj( SimpleNode::of )
            .collect( Collectors.toList() );

    }


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * {@inheritDoc}
     */
    public String name()
    {

        return name;

    }


    /* ****************** */
    /*  OBJECT OVERRIDES  */
    /* ****************** */


    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals( Object other )
    {

        return Equals.ifSameClass(
            this, other,
            o -> o.name
        );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode()
    {

        return Hashcode.of( name );

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString()
    {

        return name;

    }

}
