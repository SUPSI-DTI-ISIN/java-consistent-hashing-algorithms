package ch.supsi.dti.isin.benchmark.adapter;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.nerd4j.utils.lang.RequirementFailure;

import ch.supsi.dti.isin.Contract;
import ch.supsi.dti.isin.benchmark.config.AlgorithmConfig;
import ch.supsi.dti.isin.benchmark.config.ValuePath;
import ch.supsi.dti.isin.cluster.Node;
import ch.supsi.dti.isin.cluster.SimpleNode;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;
import ch.supsi.dti.isin.hashfunction.XXHash;

/**
 * Suite to test the contract imposed by the {@link ConsistentHashFactory} interface.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public interface ConsistentHashFactoryContract<F extends ConsistentHashFactory> extends Contract<F>
{

    public static final Map<String,Object> DEFAULT_ALGORITHM_CONFIG = Collections.singletonMap( "name", "my-algorithm" );

    public static final HashFunction FUNCTION = new XXHash();
    public static final Collection<? extends Node> NODES = Collections.singleton( SimpleNode.of(1) );
    public static final AlgorithmConfig CONFIG = AlgorithmConfig.of( ValuePath.root(), DEFAULT_ALGORITHM_CONFIG );


    /* ******************* */
    /*  INTERFACE METHODS  */
    /* ******************* */


    /**
     * An instance of the class that should respect the contract.
     *
     * @return instance of the class to test.
     */
    F sampleValue( AlgorithmConfig config );


    /**
     * {@inheritDoc}}
     */
    @Override
    default F sampleValue()
    {

        return sampleValue( AlgorithmConfig.of( ValuePath.root(), CONFIG ));

    }


    /* ************** */
    /*  TEST METHODS  */
    /* ************** */



    @Test
    default void the_algorithm_config_should_never_be_null()
    {

        assertThrows(
            RequirementFailure.class,
            () -> sampleValue( null )
        );
        
    }

    @Test
    default void method_createConsistentHash_should_not_allow_null_values()
    {

        final F factory = sampleValue( CONFIG );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createConsistentHash( null, NODES )
        );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createConsistentHash( FUNCTION, null )
        );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createConsistentHash( FUNCTION, Collections.emptyList() )
        );

    }

    @Test
    default void method_createConsistentHash_should_create_an_algorithm_with_the_given_properties()
    {

        final F factory = sampleValue( CONFIG );
        final ConsistentHash ch = factory.createConsistentHash( FUNCTION, NODES );

        assertNotNull( ch );
        assertEquals( 1, ch.nodeCount() );

    }

    @Test
    default void method_createEngineInitializer_should_not_allow_null_values()
    {

        final F factory = sampleValue( CONFIG );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createEngineInitializer( null, NODES )
        );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createEngineInitializer( FUNCTION, null )
        );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createEngineInitializer( FUNCTION, Collections.emptyList() )
        );

    }

    @Test
    default void method_createEngineInitializer_should_create_a_valid_supplier()
    {

        final F factory = sampleValue( CONFIG );
        
        final Supplier<?> supplier = factory.createEngineInitializer( FUNCTION, NODES );
        assertNotNull( supplier );

        final Object engine = assertDoesNotThrow( () -> supplier.get() );
        assertNotNull( engine );

    }

    @Test
    default void method_createEnginePilot_should_not_allow_null_values()
    {

        final F factory = sampleValue( CONFIG );
        assertThrows(
            RequirementFailure.class,
            () -> factory.createEnginePilot( null )
        );

    }

    @Test
    default void method_createEnginePilot_should_create_a_valid_pilot()
    {

        final F factory = sampleValue( CONFIG );
        
        final ConsistentHash consistentHash = factory.createConsistentHash( FUNCTION, NODES );
        final ConsistentHashEnginePilot<?> pilot = factory.createEnginePilot( consistentHash );
        assertNotNull( pilot );

    }

    @Test
    default void method_createEnginePilot_must_verify_the_engine_to_be_of_the_expected_type()
    {

        final F factory = sampleValue( CONFIG );
        
        final ConsistentHash consistentHash = new FakeConsistentHash();
        assertThrows( ResourceLoadingException.class, () -> factory.createEnginePilot(consistentHash) );
        
    }


    /* **************** */
    /*  HELPER METHODS  */
    /* **************** */


    default Map<String,Object> algorithmConfig( String key, Object value )
    {

        final Map<String,Object> map = new HashMap<>();
        map.put( "name", "my-algorithm" );
        map.put( "args", Collections.singletonMap( key, value) );

        return map;

    }


    public static class FakeConsistentHash implements ConsistentHash
    {

        @Override
        public Node getNode( String key )
        {
            throw new UnsupportedOperationException("Unimplemented method 'getNode'");
        }

        @Override
        public void addNodes( Collection<? extends Node> nodes )
        {
            throw new UnsupportedOperationException("Unimplemented method 'addNodes'");
        }

        @Override
        public void removeNodes( Collection<? extends Node> nodes )
        {
            throw new UnsupportedOperationException("Unimplemented method 'removeNodes'");
        }

        @Override
        public boolean supportsRandomRemovals()
        {
            throw new UnsupportedOperationException("Unimplemented method 'supportsRandomRemovals'");
        }

        @Override
        public int nodeCount()
        {
            throw new UnsupportedOperationException("Unimplemented method 'nodeCount'");
        }

        @Override
        public Object engine()
        {
            return new Object();
        }}
    

  }
