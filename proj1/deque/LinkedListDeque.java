package deque;

import java.util.Iterator;

public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {
    private final Node sentinel = new Node(null, null, null);
    private int size;

    // Creates an empty linked list deque.
    public LinkedListDeque() {
        sentinel.prev = sentinel;
        sentinel.next = sentinel;
        size = 0;
    }

    // Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(T item) {
        Node newItem = new Node(item, sentinel, sentinel.next);
        sentinel.next.prev = newItem;
        sentinel.next = newItem;
        size += 1;
    }

    // Adds an item of type T to the back of the deque. You can assume that item is never null.
    public void addLast(T item) {
        Node newItem = new Node(item, sentinel.prev, sentinel);
        sentinel.prev.next = newItem;
        sentinel.prev = newItem;
        size += 1;
    }


    // Returns the number of items in the deque.
    public int size() {
        return size;
    }

    // Prints the items in the deque from first to last,
    // separated by a space. Once all the items have been printed, print out a new line.
    public void printDeque() {
        Node curr = sentinel.next;
        for (int i = 0; i < size; i += 1) {
            System.out.printf("%s ", curr.item);
            curr = curr.next;
        }
        System.out.print('\n');
    }

    private boolean contains(T item) {
        for (T data : this) {
            if (data.equals(item)) {
                return true;
            }
        }
        return false;
    }

    // Removes and returns the item at the front of the deque. If no such item exists, returns null.
    public T removeFirst() {
        if (size == 0) {
            return null;
        } else {
            Node first = sentinel.next;
            sentinel.next = first.next;
            sentinel.next.prev = sentinel;
            size -= 1;
            return first.item;
        }
    }

    // Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (size == 0) {
            return null;
        } else {
            Node last = sentinel.prev;
            sentinel.prev = last.prev;
            sentinel.prev.next = sentinel;
            size -= 1;
            return last.item;
        }
    }

    // Gets the item at the given index,
    // where 0 is the front, 1 is the next item, and so forth.
    // If no such item exists, returns null.
    // Must not alter the deque!
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        Node pos = sentinel.next;
        for (int i = 0; i < index; i += 1) {
            pos = pos.next;
        }
        return pos.item;
    }

    // get method using recursion
    public T getRecursive(int index) {
        if (index >= size) {
            return null;
        }
        return recurHelper(index, sentinel.next);
    }
    private T recurHelper(int index, Node first) {
        if (index == 0) {
            return first.item;
        } else {
            return recurHelper(index - 1, first.next);
        }
    }

    // The Deque objects we’ll make are iterable

    public Iterator<T> iterator() {
        return new CustomIterator();
    }

    private class CustomIterator implements Iterator<T> {

        private Node ptr;
        CustomIterator() {
            ptr = sentinel.next;
        }
        // Checks if the next element exists
        public boolean hasNext() {
            return ptr != sentinel;
        }

        // moves the cursor/iterator to next element
        public T next() {
            T data =  ptr.item;
            ptr = ptr.next;
            return  data;
        }
    }

    // Returns whether the parameter o is equal to the Deque.
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        if (!(other instanceof Deque)) {
            return false;
        }
        Deque<T> o = (Deque<T>) other;
        if (o.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); i += 1) {
            if (!o.get(i).equals(this.get(i))) {
                return false;
            }
        }
        return true;
    }


    private class Node {
        T item;
        Node prev;
        Node next;

        Node(T item, Node prev, Node next) {
            this.item = item;
            this.next = next;
            this.prev = prev;
        }
    }

}
