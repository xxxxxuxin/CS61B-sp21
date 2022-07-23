package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {


    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */
    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    /* Instance Variables */
    private Collection<Node>[] buckets;
    private HashSet<K> keys;
    private int size, numberOfBuckets;
    private final double loadFactor;
    // You should probably define some more!

    /** Constructors */
    public MyHashMap() {
        this(16);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, 0.75);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        loadFactor = maxLoad;
        numberOfBuckets = initialSize;
        size = 0;
        keys = new HashSet<>();
    }

    /**
     * Returns a new node to be placed in a hash table bucket
     */
    private Node createNode(K key, V value) {
        return new Node(key, value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] t = new Collection[tableSize];
        for (int i = 0; i < tableSize; i += 1) {
            t[i] = createBucket();
        }
        return t;
    }

    private Collection<Node>[] resize() {
        numberOfBuckets = numberOfBuckets * 2;
        MyHashMap<K, V> bigger = new MyHashMap<>(numberOfBuckets);
        for (Collection<Node> bucket : buckets) {
            for (Node n : bucket) {
                bigger.put(n.key, n.value);
            }
        }
        return bigger.buckets;
    }

    private int pos(K key) {
        return (key.hashCode()%numberOfBuckets + numberOfBuckets)%numberOfBuckets;
    }

    private void set(K key, V value) {
        Collection<Node> bucket = buckets[pos(key)];
        for (Node item : bucket) {
            if (item.key.equals(key)) {
                item.value = value;
            }
        }
    }

    @Override
    public void clear() {
        buckets = new Collection[numberOfBuckets];
        keys = new HashSet<>();
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return keys.contains(key);
    }

    @Override
    public V get(K key) {
        V value = null;
        if (!containsKey(key)) {
            return value;
        }

        Collection<Node> bucket = buckets[pos(key)];
        for (Node item : bucket) {
            if (item.key.equals(key)) {
                value = item.value;
            }
        }
        return value;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (containsKey(key)) {
            if (get(key).equals(value)) {
                return;
            } else {
                set(key, value);
                return;
            }
        }
        Collection<Node> bucket = buckets[pos(key)];
        bucket.add(createNode(key, value));
        size += 1;
        keys.add(key);

        double ratio =  size * 1.0 / numberOfBuckets;
        if (ratio > loadFactor) {
            buckets = resize();
        }

    }

    @Override
    public Set<K> keySet() {
        return keys;
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException();
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<K> iterator() {
        return keys.iterator();
    }

}
