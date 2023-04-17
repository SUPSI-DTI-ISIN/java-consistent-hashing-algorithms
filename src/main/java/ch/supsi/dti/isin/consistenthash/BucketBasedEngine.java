package ch.supsi.dti.isin.consistenthash;


/**
 * Represents a Consistent hashing algorithm based on the concept of bucket.
 * 
 * @author Massimo Coluzzi
 */
public interface BucketBasedEngine
{
    
    
    /**
     * Returns the index of the bucket where the given key should be mapped.
     * 
     * @param key the key to map
     * @return the related bucket
     */
    int getBucket( String key );

    /**
     * Adds a new bucket to the engine.
     * 
     * @return the index of the added bucket
     */
    int addBucket();

    /**
     * Removes the given bucket from the engine.
     * 
     * @param b index of the bucket to remove
     * @return the removed bucket
     */
    int removeBucket( int b );

}