package ch.supsi.dti.isin.benchmark.config;

import org.nerd4j.utils.lang.Equals;
import org.nerd4j.utils.lang.Hashcode;


/**
 * Represents the path of a value in the configuration.
 * 
 * <p>
 * Paths are in the form {@code prop.sub-prop.sub-sub-prop}
 * 
 * @author Massimo Coluzzi
 */
public class ValuePath
{


    /** The property name. */
    private final String name;

    /** The parent property. */
    private final ValuePath parent;


    /**
     * Constructor with parameters.
     * 
     * @param name   the property name
     * @param parent the parent property
     */
    private ValuePath( String name, ValuePath parent )
    {

        super();

        this.name = name;
        this.parent = parent;

    }


    /* ***************** */
    /*  FACTORY METHODS  */
    /* ***************** */


    /**
     * Returns the root of the path.
     * 
     * @return the root of the path
     */
    public static ValuePath root()
    {

        return new ValuePath( "", null );

    }


    /* **************** */
    /*  PUBLIC METHODS  */
    /* **************** */


    /**
     * Creates a new path by appending the given property
     * to the current path.
     * 
     * @param name name of the property to append
     * @return a new path
     */
    public ValuePath append( String name )
    {
        
        return this.parent == null
        ? new ValuePath( name, this )
        : new ValuePath( '.' + name, this );

    }

    /**
     * Creates a new path by appending the given index
     * to the current path.
     * <p>
     * This method is intended to be used when the property
     * is part of a list.
     * 
     * @param index index of the property to append
     * @return a new path
     */
    public ValuePath append( int index )
    {

        final String name = "[" + index + "]";
        return new ValuePath( name, this );

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Fills the given {@link StringBuilder} with
     * the content of the parent path.
     * 
     * @param sb the {@link StringBuilder} to fill
     * @return the same {@link StringBuilder} instance
     */
    private StringBuilder fill( StringBuilder sb )
    {

        return parent != null
        ? parent.fill( sb ).append( name )
        : sb.append( name );

    }

    /* ******************* */
    /*  OBJECT OVERWRITES  */
    /* ******************* */


    /**
     * {@inheritDoc}}
     */
    @Override
    public int hashCode()
    {

        return Hashcode.of( name, parent );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public boolean equals( Object other )
    {
        
        return Equals.ifSameClass(
            this, other,
            o -> o.name,
            o -> o.parent
        );

    }

    /**
     * {@inheritDoc}}
     */
    @Override
    public String toString()
    {

        return fill( new StringBuilder() ).toString();

    }

}
