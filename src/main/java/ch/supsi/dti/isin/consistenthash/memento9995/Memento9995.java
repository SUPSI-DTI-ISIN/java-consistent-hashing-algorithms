package ch.supsi.dti.isin.consistenthash.memento9995;

import org.nerd4j.utils.lang.ToString;

/**
 * Represents the memento replacement set lookup table.
 * 
 * @author Massimo Coluzzi
 */
class Memento9995
{

    /** The minimum size of the memento table. */
    private static final int MIN_TABLE_SIZE = 1 << 4;

    /** The maximum size of the memento table. */
    private static final int MAX_TABLE_SIZE = 1 << 30;


    /** Stores the information about the removed buckets. */
    private Entry[] table;

    /** The number of removed buckets. */
    private int size;

    
    /**
     * Constructor with parameters.
     * 
     */
    Memento9995()
    {

        super();

        this.size = 0;
        this.table = new Entry[MIN_TABLE_SIZE];

    }


    /* ***************** */
    /*  DEFAULT METHODS  */
    /* ***************** */


    /**
     * Remembers that the given bucket has been removed
     * and that was replaced by the given replacer.
     * <p>
     * This method also stores the last removed bucket
     * (before the current one) to create the sequence
     * of removals.
     * 
     * @param bucket      the removed bucket
     * @param working     the size of the working set after the removal
     * @param replacer    the replacing bucket
     * @param prevRemoved the previous removed bucket
     * @return the value of the new last removed bucket
     */
    int remember( int bucket, int working, int replacer, int prevRemoved )
    {

        final Entry entry = new Entry( bucket, working, replacer, prevRemoved );
        
        add( entry, table );
        ++this.size;
        
        if( size > capacity() )
            resizeTable( table.length << 1 );

        return bucket;

    }

    /**
     * Restores the given bucket by removing it
     * from the memory.
     * <p>
     * If the memory is empty the last removed bucket
     * becomes the given bucket + 1.
     * 
     * @param bucket the bucket to restore
     * @return the new last removed bucket
     */
    int restore( int bucket )
    {

        if( isEmpty() )
            return bucket + 1;

        final Entry entry = remove( bucket );
        --this.size;

        if( size <= capacity() >> 2 )
            resizeTable( table.length >>> 1 );

        return entry.prevRemoved;
        
    }


    /**
     * Returns the entry related to the given bucket if any.
     * 
     * @param bucket the bucket to search for
     * @param entry  the resalted entry if any, {@code null} otherwise
     */
    public Entry get( int bucket )
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
                return entry;

            entry = entry.next;
        }

        return null;
        
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

        if( newTableSize < table.length && table.length <= MIN_TABLE_SIZE )
            return;

        if( newTableSize > table.length && table.length >= MAX_TABLE_SIZE )
            return;

        final Entry[] newTable = new Entry[ newTableSize ];
        for( int i = 0; i < table.length; ++i )
        {
            Entry entry = table[i];
            while( entry != null )
            {                
                
                final Entry newEntry = new Entry( entry );
                add( newEntry, newTable );

                entry = entry.next;

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
    public static class Entry implements Cloneable
    {

        /** The removed bucket. */
        public final int bucket;

        /**
         * The size of the working set after the removal of the current bucket.
         * <p>
         * This value is used to determine the size of the working set
         * after the removal of the current bucket.
         */
        public final int working;

        /**
         * Represents the bucket that will replace the current one.
         * This value also represents the size of the working set
         * after the removal of the current bucket.
         */
        public final int replacer;

        /** Keep track of the bucket removed before the current one. */
        private int prevRemoved;

        /** Used if multiple entries have the same hashcode. */
        private Entry next;


        /**
         * Constructor with parameters.
         * 
         * @param bucket      the removed bucket
         * @param working     the size of the working set after the removal
         * @param replacer    the replacing bucket
         * @param prevRemoved the previous removed bucket
         */
        private Entry( int bucket, int working, int replacer, int prevRemoved )
        {
            
            super();

            this.next        = null;
            this.bucket      = bucket;
            this.working     = working;
            this.replacer    = replacer;
            this.prevRemoved = prevRemoved;

        }

        /**
         * Constructor with parameters.
         * 
         * @param source the entry to clone.
         */
        private Entry( Entry source )
        {

            this( source.bucket, source.working, source.replacer, source.prevRemoved );

        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String toString()
        {

            return ToString.of( this )
                .print( bucket, working, replacer, prevRemoved )
                .likeTuple();

        }

   }

}
