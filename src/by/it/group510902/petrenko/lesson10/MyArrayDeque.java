package by.it.group510902.petrenko.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyArrayDeque<E> implements Deque<E> {
    private E[] elements;       // Массив, в котором хранятся элементы
    private int head;           // Индекс начала очереди (первый элемент)
    private int tail;           // Индекс конца очереди (куда пойдет следующий элемент)
    private int size;           // Текущее количество элементов в очереди
    private static final int DEFAULT_CAPACITY = 16; // Начальный размер массива

    @SuppressWarnings("unchecked")
    public MyArrayDeque() {
        // Создаем массив объектов и приводим к типу E[]
        this.elements = (E[]) new Object[DEFAULT_CAPACITY];
        this.head = 0;
        this.tail = 0;
        this.size = 0;
    }

    // Вспомогательный метод: увеличивает массив в 2 раза, если он заполнился
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = elements.length * 2;
        E[] newElements = (E[]) new Object[newCapacity];
        // Копируем элементы по кругу из старого массива в начало нового
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[(head + i) % elements.length];
        }
        this.elements = newElements;
        this.head = 0;
        this.tail = size;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    // Возвращает текущее количество элементов
    @Override
    public int size() {
        return size;
    }

    // Собирает все элементы очереди в строку вида [1, 2, 3]
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[(head + i) % elements.length]);
            if (i < size - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }

    // Обычное добавление (в конец)
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Добавление элемента в самое начало очереди
    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException(); // Запрещаем хранить null
        }
        if (size == elements.length) {
            resize(); // Если массив заполнен, увеличиваем его
        }
        // Сдвигаем указатель head влево по кругу
        head = (head - 1 + elements.length) % elements.length;
        elements[head] = element;
        size++;
    }

    // Добавление элемента в самый конец очереди
    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        if (size == elements.length) {
            resize();
        }
        elements[tail] = element;
        // Сдвигаем указатель tail вправо по кругу
        tail = (tail + 1) % elements.length;
        size++;
    }

    // Возвращает первый элемент очереди
    @Override
    public E element() {
        return getFirst();
    }

    // Смотрит на первый элемент в начале (head), не удаляя его
    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException(); // Если пусто — кидаем ошибку
        }
        return elements[head];
    }

    // Смотрит на последний элемент в конце, не удаляя его
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        // Вычисляем индекс последнего элемента
        int lastIndex = (tail - 1 + elements.length) % elements.length;
        return elements[lastIndex];
    }

    // Удаляет первый элемент очереди
    @Override
    public E poll() {
        return pollFirst();
    }

    // Удаляет первый элемент из начала очереди
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null; // Если пусто — возвращаем null (без ошибок)
        }
        E result = elements[head];
        elements[head] = null; // Зануляем ячейку для очистки памяти
        head = (head + 1) % elements.length; // Двигаем head вправо
        size--;
        return result;
    }

    // Удаляет последний элемент из конца очереди
    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        // Сдвигаем tail влево, чтобы встать на последний элемент
        tail = (tail - 1 + elements.length) % elements.length;
        E result = elements[tail];
        elements[tail] = null; // Очищаем память
        size--;
        return result;
    }

    /////////////////////////////////////////////////////////////////////////
    ////// Заглушки для остальных методов интерфейса Deque (уровень А) //////
    /////////////////////////////////////////////////////////////////////////

    @Override public boolean offerFirst(E element) { addFirst(element); return true; }
    @Override public boolean offerLast(E element) { addLast(element); return true; }
    @Override public E removeFirst() { E result = pollFirst(); if (result == null) throw new NoSuchElementException(); return result; }
    @Override public E removeLast() { E result = pollLast(); if (result == null) throw new NoSuchElementException(); return result; }
    @Override public E peekFirst() { return size == 0 ? null : elements[head]; }
    @Override public E peekLast() { return size == 0 ? null : elements[(tail - 1 + elements.length) % elements.length]; }
    @Override public boolean removeFirstOccurrence(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean removeLastOccurrence(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean offer(E element) { return offerLast(element); }
    @Override public E remove() { return removeFirst(); }
    @Override public E peek() { return peekFirst(); }
    @Override public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
    @Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public void clear() { head = 0; tail = 0; size = 0; elements = (E[]) new Object[DEFAULT_CAPACITY]; }
    @Override public void push(E element) { addFirst(element); } // Исправлено: метод должен возвращать void
    @Override public E pop() { return removeFirst(); }
    @Override public boolean remove(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean containsAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean contains(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public Iterator<E> iterator() { throw new UnsupportedOperationException(); }
    @Override public Iterator<E> descendingIterator() { throw new UnsupportedOperationException(); }
    @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
    @Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
}
