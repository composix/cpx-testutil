package nl.composix.core;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Map.Entry;

public class SubIterator<T> implements Iterator<T>, Iterable<T> {
    private final Iterator<Entry<Integer,T>> root;
    private final SubIterator<T> parent;

    private int depth;

    private SubIterator<T> iterator;
    private T current;

    public SubIterator(Iterator<Entry<Integer,T>> root) {
        this.root = root;
        this.parent = null;
        this.depth = 0;
    }
    
    public SubIterator(Iterator<Entry<Integer,T>> root, SubIterator<T> parent) {
        this.root = root;
        this.parent = parent;
        this.depth = parent.getDepth();
    }

    public int getDepth() {
        return depth;
    }

    public void setCurrent(int depth, T current) {
        if (this.current != null) {
            throw new IllegalStateException();
        }
        if (depth > lowerBound()) {
            this.current = current;
        }
        parent.setCurrent(depth, current);
    }

    @Override
    public boolean hasNext() {
        if (current == null) {
            try {
                current = next();
            } catch(NoSuchElementException e) {
                return false;
            }
        }
        return true;
    }

    @Override
    public T next() {
        T result = current;
        current = null;
        if (result != null) {
            return result;
        }
        Entry<Integer,T> entry = null;
        while(root.hasNext() && (entry = root.next()).getKey() > lowerBound()) {
            if (depth < 0 || entry.getKey() <= depth) {
                depth = entry.getKey();
                return entry.getValue();
            }
        }
        if (entry != null && parent != null) {
            parent.setCurrent(entry.getKey(), entry.getValue());
        }
        throw new NoSuchElementException();
    }

    @Override
    public SubIterator<T> iterator() {
        if (iterator == null) {
            iterator = new SubIterator<>(root, this);
        }
        return iterator;
    }

    private int lowerBound() {
        return parent == null ? -1 : parent.getDepth();
    }
}
