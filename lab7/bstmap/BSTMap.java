package bstmap;

import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {


    private Node root;
    int size = 0;

    @Override
    public void clear() {
        size = 0;
        root = null;
    }

    @Override
    public boolean containsKey(K key) {
        if (root == null) {
            return false;
        }
        return root.get(root, key) != null;
    }

    @Override
    public V get(K key) {
        if (root == null) {
            return null;
        }
        Node tmp = root.get(root, key);
        if (tmp != null) {
            return tmp.val;
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void put(K key, V value) {
        if (root == null) {
            root = new Node(key, value);
            size += 1;
        } else {
            root = root.put(root, key, value);
        }
    }

    private class Node {
        K key;
        V val;
        Node left, right;

        Node(K k, V v) {
            key = k;
            val = v;
        }

        public Node get(Node n, K k) {
            if (n == null) {
                return null;
            }
            if (k.equals(n.key)) {
                return n;
            }else if (k.compareTo(n.key) < 0) {
                return get(n.left, k);
            } else {
                return get(n.right,k);
            }
        }

        public Node put(Node n, K k, V v) {
            if (n == null) {
                size += 1;
                return new Node(k, v);
            }
            if (k.compareTo(n.key) < 0) {
                n.left = put(n.left, k, v);
            } else if (k.compareTo(n.key) > 0) {
                n.right = put(n.right, k, v);
            }
            return n;
        }
    }

    @Override
    public Set<K> keySet() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Override
    public V remove(K key) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Override
    public V remove(K key, V value) {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }

    @Override
    public Iterator<K> iterator() {
        throw new UnsupportedOperationException("Unsupported Operation.");
    }
}
