package ch.supsi.dti.isin.consistenthash;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash.Algorithm;
import ch.supsi.dti.isin.consistenthash.anchor.AnchorHash;
import ch.supsi.dti.isin.consistenthash.dx.DxHash;
import ch.supsi.dti.isin.consistenthash.jump.JumpHash;
import ch.supsi.dti.isin.consistenthash.maglev.MaglevHash;
import ch.supsi.dti.isin.consistenthash.multiprobe.MultiProbeHash;
import ch.supsi.dti.isin.consistenthash.rendezvous.RendezvousHash;
import ch.supsi.dti.isin.consistenthash.ring.RingHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;


/**
 * Test suite for the factory method {@link ConsistentHash#create(Algorithm)}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConsistentHashTests
{

    
    @Test
    public void if_arguments_are_null_or_empty_a_RequirementFailure_should_be_thrown()
    {

        final ConsistentHash.Algorithm algorithm = ConsistentHash.Algorithm.ANCHOR_HASH;
        final HashFunction.Algorithm function = HashFunction.Algorithm.XX;
        final Collection<? extends Node> nodes = SimpleNode.create( 10 );

        assertThrows( RequirementFailure.class, () -> ConsistentHash.create( null, function, nodes ) );
        assertThrows( RequirementFailure.class, () -> ConsistentHash.create( algorithm, null, nodes ) );
        assertThrows( RequirementFailure.class, () -> ConsistentHash.create( algorithm, function, null ) );
        assertThrows( RequirementFailure.class, () -> ConsistentHash.create( algorithm, function, Collections.emptyList() ) );

    }


    @Test
    public void for_each_algorithm_the_right_implementation_should_be_returned()
    {

        final List<SimpleNode> nodes = SimpleNode.create( 10 );
        final Map<Algorithm,Class<?>> map = new HashMap<>();
        map.put( Algorithm.ANCHOR_HASH, AnchorHash.class );
        map.put( Algorithm.DX_HASH, DxHash.class );
        map.put( Algorithm.JUMP_HASH, JumpHash.class );
        map.put( Algorithm.MAGLEV_HASH, MaglevHash.class );
        map.put( Algorithm.MULTIPROBE_HASH, MultiProbeHash.class );
        map.put( Algorithm.RENDEZVOUS_HASH, RendezvousHash.class );
        map.put( Algorithm.RING_HASH, RingHash.class );

        map.forEach( (algorithm, expected) ->
        {

            final ConsistentHash function = ConsistentHash.create( algorithm, ConsistentHash.DEFAULT_HASH_ALGOTITHM, nodes );
            assertNotNull( function );
            assertEquals( expected, function.getClass() );

        });
    }

}
