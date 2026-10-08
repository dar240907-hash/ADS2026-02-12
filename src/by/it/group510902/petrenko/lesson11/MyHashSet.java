package by.it.group510902.petrenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyHashSet<E> implements Set<E> {

    private static class Node<E> {
        E data;
        Node<E> next;

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E>[] table;    // Массив цепочек
    private int size;           // Текущее количество элементов в хэш-сете
    private static final int DEFAULT_CAPACITY = 16; // Начальный размер таблицы

    @SuppressWarnings("unchecked")
    public MyHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Вспомогательный метод для вычисления индекса в массиве по хэш-коду объекта
    private int getIndex(Object o) {
        if (o == null) {
            return 0;
        }
        return (o.hashCode() & 0x7FFFFFFF) % table.length;
    }

    // Вспомогательный метод для расширения хэш-таблицы при её заполнении
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        table = (Node<E>[]) new Node[newCapacity];
        size = 0;

        for (int i = 0; i < oldTable.length; i++) {
            Node<E> current = oldTable[i];
            while (current != null) {
                add(current.data);
                current = current.next;
            }
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    // Возвращает текущее количество уникальных элементов
    @Override
    public int size() {
        return size;
    }

    // Полная очистка хэш-сета
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Проверяет, пуст ли хэш-сет
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Добавляет элемент в множество, если его там еще нет
    @Override
    public boolean add(E element) {
        if (element == null) {
            return false;
        }

        if (size > table.length * 0.75) {
            resize();
        }

        int index = getIndex(element);
        Node<E> current = table[index];

        // Проверяем, нет ли уже такого элемента в текущей цепочке коллизий
        while (current != null) {
            if (element.equals(current.data)) {
                return false;
            }
            current = current.next;
        }
        Node<E> newNode = new Node<>(element, table[index]);
        table[index] = newNode;
        size++;
        return true;
    }

    // Удаляет элемент из множества
    @Override
    public boolean remove(Object o) {
        if (o == null) {
            return false;
        }

        int index = getIndex(o);
        Node<E> current = table[index];
        Node<E> previous = null;

        while (current != null) {
            if (o.equals(current.data)) {
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }
                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }

        int index = getIndex(o);
        Node<E> current = table[index];

        // Ищем объект по цепочке коллизий
        while (current != null) {
            if (o.equals(current.data)) {
                return true; // Нашли совпадение
            }
            current = current.next;
        }
        return false; // Не нашли
    }

    // Форматированный вывод множества в квадратных скобках через запятую
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        boolean first = true;

        // Обходим весь массив бакетов и все цепочки
        for (int i = 0; i < table.length; i++) {
            Node<E> current = table[i];
            while (current != null) {
                if (!first) {
                    sb.append(", ");
                }
                sb.append(current.data);
                first = false;
                current = current.next;
            }
        }
        sb.append("]");
        return sb.toString();
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Заглушки для остальных методов Set                ///////
    /////////////////////////////////////////////////////////////////////////

    @Override public Iterator<E> iterator() { throw new UnsupportedOperationException(); }
    @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
    @Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
    @Override public boolean containsAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean addAll(Collection<? extends E> c) { throw new UnsupportedOperationException(); }
    @Override public boolean retainAll(Collection<?> c) { throw new UnsupportedOperationException(); }
    @Override public boolean removeAll(Collection<?> c) { throw new UnsupportedOperationException(); }
}
