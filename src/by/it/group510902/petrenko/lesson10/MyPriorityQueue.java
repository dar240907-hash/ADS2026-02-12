package by.it.group510902.petrenko.lesson10;

import java.util.Collection;
import java.util.Iterator;
import java.util.Queue;
import java.util.NoSuchElementException;

@SuppressWarnings("unchecked")public class MyPriorityQueue<E> implements Queue<E> {

    private E[] queue;
    private int size = 0;

    public MyPriorityQueue() {
        queue = (E[]) new Object[16];
    }

    private void grow() {
        int newCapacity = queue.length + (queue.length >> 1);
        E[] newQueue = (E[]) new Object[newCapacity];
        System.arraycopy(queue, 0, newQueue, 0, queue.length);
        queue = newQueue;
    }

    private void siftUp(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        while (k > 0) {
            int parent = (k - 1) >>> 1;
            E e = queue[parent];
            if (key.compareTo(e) >= 0) {
                break;
            }
            queue[k] = e;
            k = parent;
        }
        queue[k] = (E) key;
    }

    private void siftDown(int k, E x) {
        Comparable<? super E> key = (Comparable<? super E>) x;
        int half = size >>> 1;
        while (k < half) {
            int child = (k << 1) + 1;
            E c = queue[child];
            int right = child + 1;
            if (right < size && ((Comparable<? super E>) c).compareTo(queue[right]) > 0) {
                child = right;
                c = queue[child];
            }
            if (key.compareTo(c) <= 0) {
                break;
            }
            queue[k] = c;
            k = child;
        }
        queue[k] = (E) key;
    }

    private int indexOf(Object o) {
        if (o != null) {
            for (int i = 0; i < size; i++) {
                if (o.equals(queue[i])) {
                    return i;
                }
            }
        }
        return -1;
    }

    private E removeAt(int i) {
        int s = --size;
        if (s == i) {
            queue[i] = null;
        } else {
            E moved = queue[s];
            queue[s] = null;
            siftDown(i, moved);
            if (queue[i] == moved) {
                siftUp(i, moved);
            }
        }
        return null;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    @Override
    public void clear() {
        for (int i = 0; i < size; i++) {
            queue[i] = null;
        }
        size = 0;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(queue[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    @Override
    public boolean add(E element) {
        return offer(element);
    }

    @Override
    public boolean offer(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (size >= queue.length) {
            grow();
        }
        if (size == 0) {
            queue[0] = element;
            size = 1;
        } else {
            siftUp(size++, element);
        }
        return true;
    }

    @Override
    public E remove() {
        E x = poll();
        if (x != null) {
            return x;
        }
        throw new NoSuchElementException();
    }

    @Override
    public E poll() {
        if (size == 0) {
            return null;
        }
        int s = --size;
        E result = queue[0];
        E x = queue[s];
        queue[s] = null;
        if (s != 0) {
            siftDown(0, x);
        }
        return result;
    }

    @Override
    public E element() {
        E x = peek();
        if (x != null) {
            return x;
        }
        throw new NoSuchElementException();
    }

    @Override
    public E peek() {
        return (size == 0) ? null : queue[0];
    }

    @Override
    public boolean contains(Object o) {
        return indexOf(o) != -1;
    }
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object e : c) {
            if (!contains(e)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        if (c == null) {
            throw new NullPointerException();
        }
        if (c == this) {
            throw new IllegalArgumentException();
        }
        boolean modified = false;
        for (E e : c) {
            if (add(e)) {
                modified = true;
            }
        }
        return modified;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Object[] retainElements = new Object[size];
        int newSize = 0;

        // Линейно собираем элементы, которые нужно оставить
        for (int i = 0; i < size; i++) {
            if (!c.contains(queue[i])) {
                retainElements[newSize++] = queue[i];
            } else {
                modified = true;
            }
        }

        if (modified) {
            // Переносим обратно в основной массив кучи
            for (int i = 0; i < newSize; i++) {
                queue[i] = (E) retainElements[i];
            }
            for (int i = newSize; i < size; i++) {
                queue[i] = null;
            }
            size = newSize;

            // Магия: перестраиваем кучу снизу вверх (heapify)
            for (int i = (size >>> 1) - 1; i >= 0; i--) {
                siftDown(i, queue[i]);
            }
        }
        return modified;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        if (c == null) throw new NullPointerException();
        boolean modified = false;
        Object[] retainElements = new Object[size];
        int newSize = 0;

        // Линейно собираем только те элементы, которые есть в коллекции c
        for (int i = 0; i < size; i++) {
            if (c.contains(queue[i])) {
                retainElements[newSize++] = queue[i];
            } else {
                modified = true;
            }
        }

        if (modified) {
            // Переносим обратно в основной массив кучи
            for (int i = 0; i < newSize; i++) {
                queue[i] = (E) retainElements[i];
            }
            for (int i = newSize; i < size; i++) {
                queue[i] = null;
            }
            size = newSize;

            // Магия: перестраиваем кучу снизу вверх (heapify)
            for (int i = (size >>> 1) - 1; i >= 0; i--) {
                siftDown(i, queue[i]);
            }
        }
        return modified;
    }


    @Override public boolean remove(Object o) { int i = indexOf(o); if (i == -1) return false; removeAt(i); return true; }
    @Override public Iterator<E> iterator() { return null; }
    @Override public Object[] toArray() { Object[] r = new Object[size]; System.arraycopy(queue, 0, r, 0, size); return r; }
    @Override public <T> T[] toArray(T[] a) { return null; }
}
