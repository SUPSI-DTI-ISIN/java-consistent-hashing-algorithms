package ch.supsi.dti.isin.consistenthash.ring;


/**
 * Represents a virtual node used by the Ring altorithm
 * to balance the distribution of nodes.
 *
 * @author Massimo Coluzzi
 */
public class VirtualNode
{

    
    /** Physical node whom this virtual node is a replica. */
    final String physicalNode;

    /** The hash associated with the current virtual node. */
    final long hash;


    /**
     * Constructor with parameters.
     *
     * @param physicalNode physical node whom this virtual node is a replica.
     * @param hash         the hash associated with the virtual node.
     */
    public VirtualNode( String physicalNode, long hash )
    {

        super();
        
        this.hash = hash;
        this.physicalNode = physicalNode;

    }

}
