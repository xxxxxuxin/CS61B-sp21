package deque;

import java.util.Iterator;

public class ArrayDeque<T>  implements Deque<T>,  Iterable<T>   {
    private int size, nextFirst, nextLast;
    private T[] items;

    public ArrayDeque() {
        items = (T[]) new Object[16];
        size = 0;
        nextFirst = 0;
        nextLast = 1;
    }

    private void resize(int capacity) {
        T[] tmp = (T[]) new Object[capacity];
        int pointer = next(nextFirst);
        for (int i = 1; i <= size; i += 1) {
            tmp[i] = items[pointer];
            pointer = next(pointer);
        }
        items = tmp;
        nextFirst = 0;
        nextLast = size + 1;
    }


    // Adds an item of type T to the front of the deque. You can assume that item is never null.
    public void addFirst(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextFirst] = item;
        nextFirst = prev(nextFirst);
        size += 1;
    }

    // Adds an item of type T to the back of the deque. You can assume that item is never null.
    public void addLast(T item) {
        if (size == items.length) {
            resize(size * 2);
        }
        items[nextLast] = item;
        nextLast = next(nextLast);
        size += 1;
    }

    // Returns the number of items in the deque.
    public int size() {
        return size;
    }

    // Prints the items in the deque from first to last,
    // separated by a space. Once all the items have been printed, print out a new line.
    public void printDeque() {
        int pointer = next(nextFirst);
        for (int i = 0; i < size; i += 1) {
            System.out.printf("%s ", items[pointer]);
            pointer = next(pointer);
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
        }
        int first = next(nextFirst);
        T res = items[first];
        items[first] = null;
        nextFirst = first;
        size -= 1;
        if (items.length >= 16 && size / (double) items.length < 0.25) {
            resize(size + 10);
        }
        return res;
    }

    // Removes and returns the item at the back of the deque. If no such item exists, returns null.
    public T removeLast() {
        if (size == 0) {
            return null;
        }
        int last = prev(nextLast);
        T res = items[last];
        items[last] = null;
        nextLast = last;
        size -= 1;
        if (items.length >= 16 && size / (double) items.length < 0.25) {
            resize(size + 10);
        }
        return res;
    }

    // Gets the item at the given index,
    // where 0 is the front, 1 is the next item, and so forth.
    // If no such item exists, returns null.
    // Must not alter the deque!
    public T get(int index) {
        if (index >= size) {
            return null;
        }
        int pointer = next(nextFirst + index);
        return items[pointer];
    }

    // The Deque objects we’ll make are iterable

    public Iterator<T> iterator() {
        return new ArrayIterator();
    }

    private class ArrayIterator implements Iterator<T> {

        int ptr;
        ArrayIterator() {
            ptr = 0;
        }

        public boolean hasNext() {
            return ptr < size;
        }

        public T next() {
            int index = (nextFirst + 1 + ptr) % items.length;
            ptr = ptr + 1;
            return items[index];
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

    private int next(int pointer) {
        return (pointer + 1) % items.length;
    }

    private int prev(int pointer) {
        return (pointer - 1 + items.length) % items.length;
    }
}
