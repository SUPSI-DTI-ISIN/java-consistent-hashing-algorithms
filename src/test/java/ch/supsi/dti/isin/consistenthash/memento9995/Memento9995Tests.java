package ch.supsi.dti.isin.consistenthash.memento9995;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

/**
 * Test suite for the class {@link Memento9995}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Memento9995Tests
{

    private static final Random random = new Random();

    @Test
    public void when_a_memento_is_created_size_and_capacity_should_be_as_expected()
    {

        final Memento9995 memento = new Memento9995();

        assertEquals( 0, memento.size() );
        assertEquals( 12, memento.capacity() );
        
    }


    @Test
    public void when_a_memento_is_created_should_not_contain_any_value()
    {

        final Memento9995 memento = new Memento9995();
        final int size = random.nextInt( 50 ) + 50;

        for( int i = 0; i < size; ++i )
            assertNull( memento.get(i) );

    }

    @Test
    public void a_memento_entry_must_contain_the_expected_values()
    {

        final Memento9995 memento = new Memento9995();
        final int bucket = random.nextInt( 50 ) + 50;
        final int working = random.nextInt( 50 ) + 50;
        final int replacer = random.nextInt( 50 ) + 50; 
        final int lastRemoved = random.nextInt( 50 ) + 50;

        assertNull( memento.get(bucket) );
        memento.remember( bucket, working, replacer, lastRemoved );
        
        final Memento9995.Entry entry = memento.get( bucket );
        assertNotNull( entry );

        assertEquals( bucket, entry.bucket );
        assertEquals( working, entry.working );
        assertEquals( replacer, entry.replacer );

    }


    @Test
    public void when_mappings_are_added_or_removed_the_size_should_change_accordingly()
    {

        final Memento9995 memento = new Memento9995();
        final int size = random.nextInt( 5 ) + 5;

        int lastRemoved = size;
        final int toRemove = size >>> 1;
        for( int i = 1; i <= toRemove; ++i )
        {
            lastRemoved = memento.remember( i, size - i, size - i, lastRemoved );
            assertEquals( i, memento.size() );
        }

        for( int i = 1; i <= toRemove; ++i )
        {
            lastRemoved = memento.restore( lastRemoved );
            assertEquals( toRemove - i, memento.size() );
        }

        for( int i = 1; i <= toRemove; ++i )
        {
            lastRemoved = memento.restore( lastRemoved );
            assertEquals( size + i, lastRemoved );
            assertEquals( 0, memento.size() );
        }

    }


    @Test
    public void when_capacity_is_reached_should_increase()
    {

        final int size = 24;
        final Memento9995 memento = new Memento9995();

        assertEquals( 12, memento.capacity() );

        int lastRemoved = size;
        for( int i = 0; i < 12; ++i )
            lastRemoved = memento.remember( i, size - i, size - i, lastRemoved );

        assertEquals( 12, memento.capacity() );

        memento.remember( 13, 12, 12, lastRemoved );

        assertEquals( 24, memento.capacity() );

    }


    @Test
    public void when_size_reduces_capacity_should_decrease_accordingly()
    {

        final int size = 24;
        final Memento9995 memento = new Memento9995();

        int lastRemoved = size;
        for( int i = 0; i <= 12; ++i )
            lastRemoved = memento.remember( i, size - i, size - i, lastRemoved );

        assertEquals( 24, memento.capacity() );
        
        for( int i = 0; i < 7; ++ i )
            lastRemoved = memento.restore( lastRemoved );

        assertEquals( 12, memento.capacity() );

    }


    @Test
    public void if_not_needed_capacity_should_not_reduce()
    {

        final int size = 24;
        final Memento9995 memento = new Memento9995();

        int lastRemoved = size;
        for( int i = 0; i <= 12; ++i )
            lastRemoved = memento.remember( i, size - i, size - i, lastRemoved );

        assertEquals( 24, memento.capacity() );
        
        for( int i = 0; i < 6; ++ i )
        {
            lastRemoved = memento.restore( lastRemoved );
            assertEquals( 24, memento.capacity() );
        }

        lastRemoved = memento.restore( lastRemoved );
        assertEquals( 12, memento.capacity() );

    }


    @Test
    public void capacity_should_not_reduce_under_12()
    {

        final int size = 24;
        final Memento9995 memento = new Memento9995();

        int lastRemoved = size;
        for( int i = 0; i <= 12; ++i )
            lastRemoved = memento.remember( i, size - i, size - i, lastRemoved );

        assertEquals( 24, memento.capacity() );
        
        for( int i = 0; i < 7; -- i )
            lastRemoved = memento.restore( lastRemoved );

        assertEquals( 0, memento.size() );
        assertEquals( 12, memento.capacity() );

    }

}
