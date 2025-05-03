package ch.supsi.dti.isin.consistenthash.memento9995;

import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.LongSummaryStatistics;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import ch.supsi.dti.isin.Contract;
import ch.supsi.dti.isin.consistenthash.ConsistentHash;
import ch.supsi.dti.isin.hashfunction.HashFunction;

/**
 * Test suite for the class {@link Memento9995Engine}.
 * 
 * @author Massimo Coluzzi
 */
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
public class Memento9995EngineTests implements Contract<Memento9995Engine> {

    public static final Random random = new Random();

    /* ******************* */
    /* INTERFACE METHODS */
    /* ******************* */

    /**
     * Creates a new {@link Memento9995Engine} with the given size.
     * 
     * @param size size of the cluster
     * @return new instance of {@link Memento9995Engine}
     */
    public Memento9995Engine sampleValue(int size) {

        return new Memento9995Engine(size, ConsistentHash.DEFAULT_HASH_FUNCTION);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Memento9995Engine sampleValue() {

        return sampleValue(10);

    }

    /* ************** */
    /*  TEST METHODS  */
    /* ************** */


    @Test
    public void a_new_created_engine_should_have_the_expected_size() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        assertEquals(size, engine.bArraySize());
        assertEquals(size, engine.size());

    }

    @Test
    public void adding_a_new_bucket_in_a_full_cluster_should_increase_the_size() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        engine.addBucket();

        assertEquals(size + 1, engine.size());
        assertEquals(size + 1, engine.bArraySize());

    }

    @Test
    public void adding_a_new_bucket_should_return_the_bucket_id_accordingly() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        final int bucket = engine.addBucket();

        assertEquals(size, bucket);

    }

    @Test
    public void adding_a_removed_bucket_should_return_the_bucket_id_accordingly() {

        final int size = random.nextInt(100) + 10;
        final int toRemove = random.nextInt(size - 1);

        final Memento9995Engine engine = sampleValue(size);
        final int removed = engine.removeBucket(toRemove);
        final int bucket = engine.addBucket();

        assertEquals(removed, bucket);

    }

    @Test
    public void removing_and_adding_the_last_bucket_should_work_as_expected() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);

        final int expected = size - 1;
        final int removed = engine.removeBucket(expected);

        assertEquals(expected, removed);
        assertEquals(size - 1, engine.size());
        assertEquals(size - 1, engine.bArraySize());

        final int bucket = engine.addBucket();
        assertEquals(removed, bucket);
        assertEquals(size, engine.size());
        assertEquals(size, engine.bArraySize());

    }

    @Test
    public void removing_the_first_bucket_should_change_the_size_but_not_the_bArraySize() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        engine.removeBucket(0);

        assertEquals(size - 1, engine.size());
        assertEquals(size, engine.bArraySize());

    }

    @Test
    public void removing_the_last_bucket_should_change_both_the_size_and_the_bArraySize() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        engine.removeBucket(size - 1);

        assertEquals(size - 1, engine.size());
        assertEquals(size - 1, engine.bArraySize());

    }

    @Test
    public void removing_buckets_except_the_last_should_not_change_the_bArraySize() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);
        
        for( int b = 0; b < size - 1; ++b )
        {

            engine.removeBucket( b );
   
            assertEquals(size - b - 1, engine.size());
            assertEquals(size, engine.bArraySize());

        }

    }

    @Test
    public void removing_an_existing_bucket_should_return_the_bucket_id_accordingly() {

        final int size = random.nextInt(100) + 10;
        final int toRemove = random.nextInt(size);

        final Memento9995Engine engine = sampleValue(size);
        final int bucket = engine.removeBucket(toRemove);
        assertEquals(toRemove, bucket);

    }

    @Test
    public void when_buckets_except_the_last_are_removed_should_be_restored_in_reverse_order() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);

        final List<Integer> toRemove = IntStream.range(0, size - 1).boxed().collect(toList());
        Collections.shuffle(toRemove);

        toRemove.subList(0, size - 2).stream().mapToInt(Integer::intValue).forEach(engine::removeBucket);
        Collections.reverse(toRemove);

        toRemove.subList(1, size - 1).stream().mapToInt(Integer::intValue).forEach(removed -> {

            final int added = engine.addBucket();
            assertEquals(removed, added);

        });

    }

    @Test
    public void removing_and_adding_buckets_should_work_as_expected() {

        final int size = 6;
        final Memento9995Engine engine = sampleValue(size);

        engine.removeBucket(0);
        engine.removeBucket(3);
        engine.removeBucket(1);
        engine.removeBucket(4);
        engine.removeBucket(5);

        assertEquals(5, engine.addBucket());
        assertEquals(4, engine.addBucket());
        assertEquals(1, engine.addBucket());
        assertEquals(3, engine.addBucket());
        assertEquals(0, engine.addBucket());

    }
    
    @Test
    public void memento_restores_the_buckets_in_reverse_order() {

        final int size = random.nextInt(100) + 10;
        final Memento9995Engine engine = sampleValue(size);

        final List<Integer> toRemove = IntStream.range(0, size).boxed().collect(toList());
        Collections.shuffle(toRemove);

        for( int i = 1; i < size; ++i )
            engine.removeBucket( toRemove.get(i) );

        for( int i = size - 1; i > 0; --i )
        {
            final int bucket = engine.addBucket();
            assertEquals( toRemove.get(i), bucket );
        }

    }

    @Test
    public void
    removing_the_last_bucket_after_removing_the_first_should_be_restored_properly()
    {

        final int size = 10;
        final Memento9995Engine engine = sampleValue( size );

        engine.removeBucket( 0 );
        engine.removeBucket( size-1 );

        assertEquals( size - 2, engine.size() );
        assertEquals( size, engine.bArraySize() );

        engine.addBucket();
        engine.addBucket();

        assertEquals( size, engine.size() );
        assertEquals( size, engine.bArraySize() );

    }

    @Test
    public void if_the_cluster_has_one_bucket_all_the_keys_should_land_to_such_a_bucket() {

        final Memento9995Engine engine = sampleValue(1);

        final Random random = new Random();
        for (int i = 0; i < 100; ++i) {

            final String key = String.valueOf(random.nextInt());
            final int bucket = engine.getBucket(key);
            assertEquals(0, bucket);

        }

    }

    @Test
    public void if_all_buckets_are_removed_except_one_all_the_keys_should_land_to_such_a_bucket() {

        final int size = random.nextInt(100) + 1;
        final Memento9995Engine engine = sampleValue(size);

        final List<Integer> toRemove = IntStream.range(0, size).boxed().collect(toList());
        Collections.shuffle(toRemove);

        toRemove.subList(1, size).stream().mapToInt(Integer::intValue).forEach(engine::removeBucket);

        final Random random = new Random();
        for (int i = 0; i < 100; ++i) {

            final String key = String.valueOf(random.nextInt());
            final int bucket = engine.getBucket(key);
            assertEquals(toRemove.get(0), bucket);

        }

    }

    @Test
    public void if_the_cluster_has_multiple_buckets_each_bucket_should_get_some_key() {

        final Memento9995Engine engine = sampleValue();

        final Map<Integer, AtomicInteger> map = IntStream
                .range(0, 10)
                .boxed()
                .collect(
                        Collectors.toMap(Function.identity(), i -> new AtomicInteger()));

        for (int i = 0; i < 1000; ++i) {

            final String key = String.valueOf(random.nextInt());
            final int bucket = engine.getBucket(key);

            final AtomicInteger count = map.get(bucket);
            assertNotNull(count);

            count.incrementAndGet();

        }

        map.values().stream().forEach(count -> {
            assertTrue(count.get() > 0);
        });

    }

    @Test
    public void if_a_bucket_is_removed_only_the_keys_related_to_such_a_bucket_should_move() {

        final int size = 10;
        final Memento9995Engine engine = sampleValue(size);
        final Map<String, Integer> map = new HashMap<>();

        for (int i = 0; i < 1000; ++i) {

            final String key = String.valueOf(random.nextInt());
            final int bucket = engine.getBucket(key);

            map.put(key, bucket);

        }

        final int removed = random.nextInt(size);
        engine.removeBucket(removed);

        map.keySet().forEach(key -> {

            final int previous = map.get(key);
            final int current = engine.getBucket(key);

            if (previous == removed)
                assertNotEquals(previous, current);
            else
                assertEquals(previous, current);

        });

    }

    @Test
    public void the_property_of_minimal_distruption_should_hold() {

        final int size = 100;
        final int keyCount = 1000;

        final Memento9995Engine engine = sampleValue(size);

        final Map<String, int[]> map = new HashMap<>();
        final List<String> keys = IntStream.generate(random::nextInt).limit(keyCount).mapToObj(String::valueOf)
                .collect(toList());

        recordOriginalKeyPositions(keys, 1, map, engine);

        final int removed = random.nextInt(size);
        engine.removeBucket(removed);
        for (String key : keys) {

            final int previous = map.get(key)[0];
            final int current = engine.getBucket(key);

            if (previous == removed)
                assertNotEquals(previous, current);
            else
                assertEquals(previous, current);

        }

        recordKeyPositions(keys, 0, map, engine);
        engine.addBucket();
        for (String key : keys) {

            final int previous = map.get(key)[1];
            final int current = engine.getBucket(key);

            if (current == removed)
                assertNotEquals(previous, current);
            else
                assertEquals(previous, current);

        }

    }


     @Test
    public void test_proposition_18_when_almost_all_nodes_are_removed() {

        int n = 100_000; // size of the b-array
        int w = 1; // number of working buckets
        int r = n - w; // number of removed buckets

        int numLookups = 100;
        int numIterations = 100;
        long seed = 0x27bc8b24419e36ffL;

        LongSummaryStatistics nestedLoopStatistics = new LongSummaryStatistics();
        SplittableRandom random = new SplittableRandom(seed);

        for (int k = 0; k < numIterations; ++k) {

            Memento9995Engine engine = new Memento9995Engine(n, HashFunction.create(HashFunction.Algorithm.XX));

            // remove randomly r buckets based on Fisher-Yates shuffling
            int[] buckets = IntStream.range(0, n).toArray();
            boolean[] bucketIsRemoved = new boolean[n]; // just for verification that same bucket is removed only once
            for (int i = 0; i < r; ++i) {
                int j = i + random.nextInt(n - i);
                int bucketToRemove = buckets[j];
                assertFalse(bucketIsRemoved[bucketToRemove]);
                buckets[j] = buckets[i];
                engine.removeBucket(bucketToRemove);
                bucketIsRemoved[bucketToRemove] = true;
            }
            assertEquals(n, engine.bArraySize());
            assertEquals(w, engine.size());

            // do some lookups
            for (int a = 0; a < numLookups; ++a) {
                Memento9995Engine.Debug debug = new Memento9995Engine.Debug();
                String key = String.valueOf(random.nextLong());
                assertEquals(0, debug.nestedLoopCounter);
                engine.getBucket(key, debug);
                nestedLoopStatistics.accept(debug.nestedLoopCounter);
            }
        }

        assertEquals(numIterations * numLookups, nestedLoopStatistics.getCount());
        double omega = nestedLoopStatistics.getAverage(); // average number of nested loop iterations

        double x  = Math.log(n / (double)w);
        double expectation = Math.pow(x, 2.); // see Proposition 18 in paper
        double standardDeviation = Math.pow(x, 3./2.); // see Proposition 18 in paper

        assertTrue(omega <= expectation + 100 * standardDeviation); // works
        System.out.println("omega = " + omega);
        
    }


    /* **************** */
    /*  HELPER METHODS  */
    /* **************** */

    private void recordOriginalKeyPositions(List<String> keys, int iterations, Map<String, int[]> map,
            Memento9995Engine engine) {

        for (String key : keys) {

            final int bucket = engine.getBucket(key);

            final int[] buckets = new int[iterations + 1];
            buckets[0] = bucket;

            map.put(key, buckets);

        }

    }

    private void recordKeyPositions(List<String> keys, int iteration, Map<String, int[]> map, Memento9995Engine engine) {

        for (String key : keys) {

            final int bucket = engine.getBucket(key);
            map.get(key)[iteration + 1] = bucket;

        }

    }

}
