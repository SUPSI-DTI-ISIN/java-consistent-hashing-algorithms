package ch.supsi.dti.isin.cluster;


import org.nerd4j.utils.lang.Comparative;


/**
 * Represents a node in a cluster.
 * 
 * <p>
 * The only required feature for a node is to have
 * a unique name that can be an IP address, a MAC
 * address a UUID, or any similar value.
 * 
 * @author Massimo Coluzzi
 */
public interface Node extends Comparative<Node>
{

    /**
     * Returns the unique name of the node.
     * 
     * @return the unique name of the node
     */
    String name();


    /* ************************* */
    /*  DEFAULT IMPLEMENTATIONS  */
    /* ************************* */

    
    /**
     * {@inheritDoc}
     */
    @Override
    default int compareTo( Node other )
    {

        return this.name().compareTo( other.name() );

    }

}
