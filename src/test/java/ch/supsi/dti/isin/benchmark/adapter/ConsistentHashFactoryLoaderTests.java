package ch.supsi.dti.isin.benchmark.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.benchmark.adapter.consistenthash.anchor.AnchorFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.dx.DxFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.jump.JumpFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.jumpback.JumpBackFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.maglev.MaglevFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.multiprobe.MultiProbeFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.rendezvous.RendezvousFactory;
import ch.supsi.dti.isin.benchmark.adapter.consistenthash.ring.RingFactory;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ValuePath;

/**
 * Suite to test the {@link ConsistentHashFactoryLoader} class.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class ConsistentHashFactoryLoaderTests
{


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void all_consistent_hash_factory_implementations_should_be_loaded()
    {

        final Map<String,Class<? extends ConsistentHashFactory>> expected = new HashMap<>();
        expected.put( "anchor", AnchorFactory.class );
        expected.put( "dx", DxFactory.class );
        expected.put( "jump", JumpFactory.class );
        expected.put( "maglev", MaglevFactory.class );
        expected.put( "multiprobe", MultiProbeFactory.class );
        expected.put( "rendezvous", RendezvousFactory.class );
        expected.put( "ring", RingFactory.class );
        expected.put( "jumpback", JumpBackFactory.class );
        
        final ConsistentHashFactoryLoader loader = ConsistentHashFactoryLoader.getInstance();
        for( Map.Entry<String,Class<? extends ConsistentHashFactory>> entry : expected.entrySet() )
        {

            final AlgorithmConfig config = AlgorithmConfig.of(
                ValuePath.root(),
                Collections.singletonMap( "name", entry.getKey() )
            );

            final ConsistentHashFactory factory = assertDoesNotThrow( () -> loader.load(entry.getKey(), config) );
            assertNotNull( factory );
            assertEquals( factory.getClass(), entry.getValue() );

        }

    }

}
