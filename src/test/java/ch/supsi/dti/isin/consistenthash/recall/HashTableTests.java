package ch.supsi.dti.isin.consistenthash.recall;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Random;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.consistenthash.recall.HashTable.Pointer;
import ch.supsi.dti.isin.consistenthash.recall.HashTable.Replacement;

/**
 * Test suite for the class {@link HashTable}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class HashTableTests
{

    private static final Random random = new Random();

    @Test
    public void a_new_created_HashTable_must_be_empty()
    {

        final HashTable<?> hashTable = new HashTable<>();

        assertTrue( hashTable.isEmpty() );
        assertEquals( 0, hashTable.size() );
        assertEquals( 12, hashTable.capacity() );
        
    }

    @Test
    public void when_an_entry_is_added_should_be_found()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();
        assertEquals( 0, hashTable.size() );

        final int bucket = random.nextInt( 100 );
        final EmptyEntry entry = new EmptyEntry( bucket );
        hashTable.add( entry );
        assertEquals( 1, hashTable.size() );

        final EmptyEntry storedEntry = hashTable.get( bucket );
        assertNotNull( storedEntry );
        assertEquals( entry, storedEntry );

    }

    @Test
    public void when_an_entry_is_removed_it_must_disappear()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();

        final int bucket1 = random.nextInt( 100 );
        final EmptyEntry entry1 = new EmptyEntry( bucket1 );
        hashTable.add( entry1 );
        
        final int bucket2 = random.nextInt( 100 ) + 100;
        final EmptyEntry entry2 = new EmptyEntry( bucket2 );
        hashTable.add( entry2 );

        assertEquals( 2, hashTable.size() );
        final EmptyEntry storedEntry1 = hashTable.get( bucket1 );
        assertNotNull( storedEntry1 );

        final EmptyEntry storedEntry2 = hashTable.get( bucket2 );
        assertNotNull( storedEntry2 );
        
        assertEquals( entry1, storedEntry1 );
        assertEquals( entry2, storedEntry2 );

        hashTable.rem( bucket1 );
        assertNull( hashTable.get(bucket1) );
        assertNotNull( hashTable.get(bucket2) );
        assertEquals( 1, hashTable.size() );
        
        hashTable.rem( bucket2 );
        assertNull( hashTable.get(bucket1) );
        assertNull( hashTable.get(bucket2) );
        assertEquals( 0, hashTable.size() );

    }

    @Test
    public void if_we_remove_the_same_bucket_twice_nothing_happens()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();

        final int bucket1 = random.nextInt( 100 );
        final EmptyEntry entry1 = new EmptyEntry( bucket1 );
        hashTable.add( entry1 );
        
        final int bucket2 = random.nextInt( 100 );
        final EmptyEntry entry2 = new EmptyEntry( bucket2 );
        hashTable.add( entry2 );


        assertEquals( 2, hashTable.size() );
        
        hashTable.rem( bucket1 );
        assertNull( hashTable.get(bucket1) );
        assertNotNull( hashTable.get(bucket2) );
        assertEquals( 1, hashTable.size() );
        
        assertDoesNotThrow( () -> hashTable.rem(bucket1) );
        assertNull( hashTable.get(bucket1) );
        assertNotNull( hashTable.get(bucket2) );
        assertEquals( 1, hashTable.size() );
        
    }

    @Test
    public void more_than_one_entry_for_the_same_bucket_can_be_added()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();

        final int bucket = random.nextInt( 100 );
        final EmptyEntry entry1 = new EmptyEntry( bucket );
        final EmptyEntry entry2 = new EmptyEntry( bucket );

        hashTable.add( entry1 );
        assertDoesNotThrow( () -> hashTable.add(entry2) );

        assertEquals( 2, hashTable.size() );
        
        final EmptyEntry removed1 = assertDoesNotThrow( () -> hashTable.rem(bucket) );
        assertEquals( entry2, removed1 );

        assertEquals( 1, hashTable.size() );
        assertEquals( entry1, hashTable.get(bucket) );
            
        final EmptyEntry removed2 = assertDoesNotThrow( () -> hashTable.rem(bucket) );
        assertEquals( entry1, removed2 );

        assertEquals( 0, hashTable.size() );
        assertNull( hashTable.get(bucket) );
        
    
    }

    @Test
    public void when_capacity_is_reached_should_increase()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();
        assertEquals( 12, hashTable.capacity() );
        assertEquals( 0, hashTable.size() );

        for( int i = 1; i <= 12; ++i )
        {
            hashTable.add( new EmptyEntry(i) );
            assertEquals( 12, hashTable.capacity() );
            assertEquals( i, hashTable.size() );
        }

        hashTable.add( new EmptyEntry(13) );
        assertEquals( 24, hashTable.capacity() );
        assertEquals( 13, hashTable.size() );

    }

    @Test
    public void when_size_reduces_capacity_should_decrease_accordingly()
    {

        final HashTable<EmptyEntry> hashTable = new HashTable<>();
        assertEquals( 12, hashTable.capacity() );
        assertEquals( 0, hashTable.size() );

        for( int i = 1; i <= 13; ++i )
            hashTable.add( new EmptyEntry(i) );

        assertEquals( 24, hashTable.capacity() );
        assertEquals( 13, hashTable.size() );

        for( int i = 13; i >= 8; --i )
        {
            hashTable.rem( i );
            assertEquals( 24, hashTable.capacity() );
            assertEquals( i-1, hashTable.size() );
        }

        hashTable.rem( 7 );
        assertEquals( 12, hashTable.capacity() );
        assertEquals( 6, hashTable.size() );

    }

    @Test
    public void capacity_should_not_reduce_under_12()
    {

        final int size = random.nextInt( 100 ) + 13;
        final HashTable<EmptyEntry> hashTable = new HashTable<>();
        
        for( int i = 1; i <= size; ++i )
            hashTable.add( new EmptyEntry(i) );
        
        for( int i = size; i >= 1; --i )
        {
            hashTable.rem( i );
            assertEquals( i-1, hashTable.size() );
            assertTrue( hashTable.capacity() >= 12 );
        }

    }


    @Test
    public void a_Replacement_should_contain_the_provided_values()
    {

        final int b = random.nextInt( 100 );
        final int r = random.nextInt( 100 );
        final int w = random.nextInt( 100 );
        final int p = random.nextInt( 100 );
    
        final Replacement replacement = new Replacement( b, r, w, p );
        assertEquals( r, replacement.r );
        assertEquals( w, replacement.w );
        assertEquals( p, replacement.p );

    }

    @Test
    public void a_Pointer_should_contain_the_provided_values()
    {

        final int b = random.nextInt( 100 );
        final int v = random.nextInt( 100 );
    
        final Pointer pointer = new Pointer( b, v );
        assertEquals( v, pointer.value );

    }


    /* **************** */
    /*  HELPER CLASSES  */
    /* **************** */


    private static class EmptyEntry extends HashTable.Entry
    {

        public EmptyEntry( int bucket )
        {

            super( bucket );

        }

    }

}
