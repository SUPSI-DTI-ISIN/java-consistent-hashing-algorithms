package ch.supsi.dti.isin.consistenthash.recall_v1;

import org.nerd4j.utils.lang.ToString;

/**
 * Represents the replacement set lookup table.
 * 
 * @param <E> the table entry implementation to use
 * 
 * @author Massimo Coluzzi
 */
class HashTableV1<E extends HashTableV1.Entry>
{

    /** The minimum size of the memento table. */
    private static final int MIN_TABLE_SIZE = 1 << 4;

    /** The maximum size of the memento table. */
    private static final int MAX_TABLE_SIZE = 1 << 30;


    /** Stores the entries of the table. */
    private Entry[] table;

    /** The number of entries in the table. */
    private int size;

    
    /**
     * Constructor with parameters.
     * 
     */
    HashTableV1()
    {

        super();

        this.table = new Entry[MIN_TABLE_SIZE];

    }


    /* ***************** */
    /*  DEFAULT METHODS  */
    /* ***************** */


    /**
     * Returns the entry related to the given bucket if any.
     * 
     * @param bucket the bucket to search for
     * @@return the resalted entry if any, {@code null} otherwise
     */
    E get( int bucket )
    {

        /*
         * We used the same approach adopted by java.util.HashMap
         * to compute the index. It is proven to be efficient
         * in the majority of the cases.
         */
        final int hash   = bucket ^ bucket >>> 16;
        final int index  = (table.length - 1) & hash;

        Entry entry = table[index];
        while( entry != null )
        {
            if( entry.bucket == bucket )
            {
                @SuppressWarnings("unchecked")
                final E e = (E) entry;
                return e;
            }

            entry = entry.next;
        }

        return null;
        
    }

    /**
     * Adds the given entry. If an entry for the same bucket
     * already exists, it will be duplicated.
     * 
     * @param entry the entry to add
     */
    void add( E entry )
    {

        if( ++size > capacity() && table.length < MAX_TABLE_SIZE )
            resizeTable( table.length << 1 );

        add( entry, this.table );

    }

    /**
     * Removes the entry mapped to the give bucket, if any.
     * If there are multiple entries for the same bucket,
     * only one entry will be removed.
     * 
     * @param bucket the key of the entry to remove
     */
    E rem( int bucket )
    {

        @SuppressWarnings("unchecked")
        final E e = (E) remove( bucket );
        if( e == null )
            return null;

        if( --size <= capacity() >> 2 && table.length > MIN_TABLE_SIZE )
            resizeTable( table.length >> 1 );

        return e;

    }

    /**
     * Returns {@code true} if the replacement set is empty.
     * 
     * @return {@code true} if empty, {@code false} otherwise
     */
    boolean isEmpty()
    {

        return size <= 0;

    }

    /**
     * Returns the size of the replacement set.
     * 
     * @return the size of the replacement set
     */
    int size()
    {

        return size;

    }

    /**
     * Returns the size of the lookup table used to implement the replacement set.
     * 
     * @return the size of the lookup table used to implement the replacement set
     */
    int capacity()
    {

        /* 
         * We want to keep a load factor of 0.75 to have an average access time of O(1).
         * For this reason, the declared capacity is 75% of the actual capacity.
         */
        return (table.length >>> 2) * 3;

    }


    /* ***************** */
    /*  PRIVATE METHODS  */
    /* ***************** */


    /**
     * Adds a new entry to the given table.
     * <p>
     * This method is used to add entries to the lookup table
     * during common operations and to add entries to the new
     * lookup table during resize.
     * <p>
     * We assume the algorithm to be used properly.
     * Therefore, we do not handle the case of the same entry
     * being added twice.
     * 
     * @param entry the entry to add
     * @param table the table to be modified
     */
    private void add( Entry entry, Entry[] table )
    {

        /*
         * We used the same approach adopted by java.util.HashMap
         * to compute the index. It is proven to be efficient
         * in the majority of the cases.
         */
        final int bucket = entry.bucket;
        final int hash   = bucket ^ bucket >>> 16;
        final int index  = (table.length - 1) & hash;

        entry.next = table[index];
        table[index] = entry;
        
    }



    /**
     * Removes the given bucket from the lookup table.
     * 
     * @param bucket the bucket to remove
     * @return the related entry in the table
     */
    private Entry remove( int bucket )
    {

        final int hash   = bucket ^ bucket >>> 16;
        final int index  = (table.length - 1) & hash;

        Entry entry = table[index];
        if( entry == null )
            return null;
        
        Entry prev = null;
        while( entry != null && entry.bucket != bucket )
        {
            prev = entry;
            entry = entry.next;
        }

        if( entry == null )
            return null;
        
        if( prev == null )
            table[index] = entry.next;
        else
            prev.next = entry.next;

        entry.next = null;

        return entry;
        
    }

    /**
     * Resizes the lookup table by creating a new table and cloning
     * the entries in the old table into the new one.
     * 
     * @param newTableSize the size of the new lookup table
     */
    private void resizeTable( int newTableSize )
    {

        /*
         * We create the table with the new size
         * and migrate every entry from the old
         * table to the new one.
         */
        final Entry[] newTable = new Entry[ newTableSize ];
        for( int i = 0; i < table.length; ++i )
        {

            Entry next = table[i];
            while( next != null )
            {                
                
                final Entry entry = next;
                next = entry.next;
                add( entry, newTable );

            }

        }

        this.table = newTable;

    }

    
    /* *************** */
    /*  INNER CLASSES  */
    /* *************** */


    /**
     * Represents an entry in the lookup table.
     * 
     * @author Massimo Coluzzi
     */
    static abstract class Entry
    {

        /** The bucket this entry refers to. */
        final int bucket;

        /** Used if multiple entries have the same hashcode. */
        private Entry next;


        /**
         * Constructor with parameters.
         * 
         * @param bucket      the removed bucket
         */
        protected Entry( int bucket )
        {
            
            super();

            this.next        = null;
            this.bucket      = bucket;

        }

    }

    /**
     * Represents the replacement of a failed bucket.
     * 
     * @author Massimo Coluzzi
     */
    static class Replacement extends Entry
    {

        /**
         * Represents the bucket that will replace the current one.
         * This value also represents the size of the working set
         * after the removal of the current bucket.
         */
        int r;

        /** Size of the working set after the removal of the bucket. */
        final int w;

        /** Keep track of the bucket removed before the current one. */
        final int p;


        /**
         * Constructor with parameters.
         * 
         * @param b the removed bucket
         * @param r the replacing bucket
         * @param w number of working buckets after removing bucket b.
         * @param p the previous removed bucket
         */
        Replacement( int b, int r, int w, int p )
        {
            
            super( b );

            this.r = r;
            this.w = w;
            this.p = p;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {

            return ToString.of( this )
                .print( bucket, r, p )
                .likeTuple();

        }

    }

    /**
     * Represents the reverse mapping between
     * a replacing bucket and the failed bucket
     * replaced by it.
     * 
     * @author Massimo Coluzzi
     */
    static class Pointer extends Entry
    {

        /**
         * Represents the failed bucket replaced by
         * the current one.
         */
        final int value;


        /**
         * Constructor with parameters.
         * 
         * @param bucket the pointed bucket
         * @param value  the pointing bucket
         */
        Pointer( int bucket, int value )
        {
            
            super( bucket );

            this.value = value;

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {

            return ToString.of( this )
                .print( bucket, value )
                .likeTuple();

        }

    }

}
