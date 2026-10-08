package by.it.group510902.petrenko.lesson10;

import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MyLinkedList<E> implements Deque<E> {

    // Внутренний класс: один "узел" нашей цепочки списка
    private static class Node<E> {
        E data;             // Сами данные, которые мы храним
        Node<E> next;       // Ссылка на следующий узел (справа)
        Node<E> prev;       // Ссылка на предыдущий узел (слева)

        Node(E data) {
            this.data = data;
        }
    }

    private Node<E> head;   // Стрелка на самый первый узел списка
    private Node<E> tail;   // Стрелка на самый последний узел списка
    private int size;       // Текущее количество элементов в списке

    public MyLinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    // Возвращает количество элементов в списке
    @Override
    public int size() {
        return size;
    }

    // Собирает все элементы в строку вида [элемент1, элемент2]
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.next != null) {
                sb.append(", ");
            }
            current = current.next; // Переходим к следующему узлу
        }
        return sb.append("]").toString();
    }

    // Стандартное добавление (в конец)
    @Override
    public boolean add(E element) {
        addLast(element);
        return true;
    }

    // Добавление элемента в самое начало списка
    @Override
    public void addFirst(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            newNode.next = head;
            head.prev = newNode;
            head = newNode;
        }
        size++;
    }

    // Добавление элемента в самый конец списка
    @Override
    public void addLast(E element) {
        if (element == null) {
            throw new NullPointerException();
        }
        Node<E> newNode = new Node<>(element);
        if (size == 0) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    // Удаление элемента по его порядковому индексу
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }

        Node<E> current = head;
        for (int i = 0; i < index; i++) {
            current = current.next;
        }

        E savedData = current.data;
        removeNode(current);
        return savedData;
    }

    // Удаление конкретного объекта из списка
    @Override
    public boolean remove(Object o) {
        if (o == null) return false;
        Node<E> current = head;
        while (current != null) {
            if (o.equals(current.data)) {
                removeNode(current);
                return true;
            }
            current = current.next;
        }
        return false; // Если элемент не нашли
    }

    // Вспомогательный метод для удаления узла
    private void removeNode(Node<E> target) {
        if (target.prev != null) {
            target.prev.next = target.next;
        } else {
            head = target.next;
        }

        if (target.next != null) {
            target.next.prev = target.prev;
        } else {
            tail = target.prev;
        }
        size--;
    }

    // Возвращает первый элемент списка, не удаляя его
    @Override
    public E element() {
        return getFirst();
    }

    @Override
    public E getFirst() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return head.data;
    }

    // Возвращает последний элемент списка, не удаляя его
    @Override
    public E getLast() {
        if (size == 0) {
            throw new NoSuchElementException();
        }
        return tail.data;
    }

    // Забирает первый элемент списка
    @Override
    public E poll() {
        return pollFirst();
    }

    // Удаляет и возвращает первый узел списка
    @Override
    public E pollFirst() {
        if (size == 0) {
            return null; // Если пусто — возвращаем null по стандарту
        }
        E data = head.data;
        removeNode(head); // Удаляем голову списка
        return data;
    }

    // Удаляет и возвращает последний узел списка
    @Override
    public E pollLast() {
        if (size == 0) {
            return null;
        }
        E data = tail.data;
        removeNode(tail); // Удаляем хвост списка
        return data;
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Заглушки для остальных методов Deque              ///////
    /////////////////////////////////////////////////////////////////////////

    @Override public boolean offerFirst(E element) { addFirst(element); return true; }
    @Override public boolean offerLast(E element) { addLast(element); return true; }
    @Override public E removeFirst() { E res = pollFirst(); if (res == null) throw new NoSuchElementException(); return res; }
    @Override public E removeLast() { E res = pollLast(); if (res == null) throw new NoSuchElementException(); return res; }
    @Override public E peekFirst() { return size == 0 ? null : head.data; }
    @Override public E peekLast() { return size == 0 ? null : tail.data; }
    @Override public boolean removeFirstOccurrence(Object o) { return remove(o); }
    @Override public boolean removeLastOccurrence(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean offer(E element) { return offerLast(element); }
    @Override public E remove() { return removeFirst(); }
    @Override public E peek() { return peekFirst(); }
    @Override public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
    @Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public void clear() { head = null; tail = null; size = 0; }
    @Override public void push(E element) { addFirst(element); }
    @Override public E pop() { return removeFirst(); }
    @Override public boolean containsAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean contains(Object o) { throw new UnsupportedOperationException(); }
    @Override public boolean isEmpty() { return size == 0; }
    @Override public Iterator<E> iterator() { throw new UnsupportedOperationException(); }
    @Override public Iterator<E> descendingIterator() { throw new UnsupportedOperationException(); }
    @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
    @Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
}