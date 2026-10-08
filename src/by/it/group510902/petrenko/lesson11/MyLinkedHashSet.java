package by.it.group510902.petrenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyLinkedHashSet<E> implements Set<E> {

    private static class Node<E> {
        E data;
        Node<E> next;   // Для цепочки коллизий в хэш-таблице
        Node<E> before; // Для двунаправленного списка порядка добавления
        Node<E> after;  // Для двунаправленного списка порядка добавления

        Node(E data, Node<E> next) {
            this.data = data;
            this.next = next;
        }
    }

    private Node<E>[] table;    // Хэш-таблица
    private int size;           // Количество элементов
    private static final int DEFAULT_CAPACITY = 16;

    private Node<E> head;       // Первый добавленный элемент (голова списка порядка)
    private Node<E> tail;       // Последний добавленный элемент (хвост списка порядка)

    @SuppressWarnings("unchecked")
    public MyLinkedHashSet() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    // Вспомогательный метод вычисления индекса по хэш-коду
    private int getIndex(Object o) {
        if (o == null) {
            return 0;
        }
        return (o.hashCode() & 0x7FFFFFFF) % table.length;
    }

    // Расширение таблицы в 2 раза при заполнении
    @SuppressWarnings("unchecked")
    private void resize() {
        Node<E>[] oldTable = table;
        int newCapacity = oldTable.length * 2;
        table = (Node<E>[]) new Node[newCapacity];

        // Сбрасываем связи в хэш-таблице, но сохраняем общий связный список порядка добавления
        for (int i = 0; i < table.length; i++) {
            table[i] = null;
        }

        // Переставляем элементы, шагая строго по списку порядка добавления (от head к tail)
        Node<E> current = head;
        while (current != null) {
            int index = getIndex(current.data);
            current.next = table[index];
            table[index] = current;
            current = current.after;
        }
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    // Возвращает количество элементов
    @Override
    public int size() {
        return size;
    }

    // Проверяет, пуста ли коллекция
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Полная очистка
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        this.table = (Node<E>[]) new Node[DEFAULT_CAPACITY];
        this.size = 0;
        this.head = null;
        this.tail = null;
    }

    // Добавление элемента в хэш-сет
    @Override
    public boolean add(E element) {
        if (element == null) {
            return false;
        }

        // Проверяем коэффициент заполнения (Load factor > 0.75)
        if (size > table.length * 0.75) {
            resize();
        }

        int index = getIndex(element);
        Node<E> current = table[index];

        // Проверяем на дубликаты в бакете коллизий
        while (current != null) {
            if (element.equals(current.data)) {
                return false; // Элемент уже есть, ничего не делаем
            }
            current = current.next;
        }

        // Создаем новый узел и помещаем его в начало цепочки бакета
        Node<E> newNode = new Node<>(element, table[index]);
        table[index] = newNode;

        // Связываем новый узел в общий список порядка добавления (в самый конец)
        if (head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.after = newNode;
            newNode.before = tail;
            tail = newNode;
        }

        size++;
        return true;
    }

    // Удаление конкретного элемента
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
                // 1. Вырезаем элемент из цепочки коллизий хэш-таблицы
                if (previous == null) {
                    table[index] = current.next;
                } else {
                    previous.next = current.next;
                }

                // 2. Вырезаем элемент из общего списка сохранения порядка
                if (current.before != null) {
                    current.before.after = current.after;
                } else {
                    head = current.after; // Если удаляем самый первый элемент
                }

                if (current.after != null) {
                    current.after.before = current.before;
                } else {
                    tail = current.before; // Если удаляем самый последний элемент
                }

                size--;
                return true;
            }
            previous = current;
            current = current.next;
        }
        return false;
    }

    // Проверка наличия элемента за O(1)
    @Override
    public boolean contains(Object o) {
        if (o == null) {
            return false;
        }
        int index = getIndex(o);
        Node<E> current = table[index];
        while (current != null) {
            if (o.equals(current.data)) {
                return true;
            }
            current = current.next;
        }
        return false;
    }

    // Проверяет, содержит ли сет все элементы из переданной коллекции
    @Override
    public boolean containsAll(Collection<?> c) {
        for (Object item : c) {
            if (!contains(item)) {
                return false;
            }
        }
        return true;
    }

    // Добавляет все элементы из переданной коллекции в наш сет
    @Override
    public boolean addAll(Collection<? extends E> c) {
        boolean modified = false;
        for (E item : c) {
            if (add(item)) {
                modified = true;
            }
        }
        return modified;
    }

    // Удаляет из нашего сета все элементы, которые есть в переданной коллекции
    @Override
    public boolean removeAll(Collection<?> c) {
        boolean modified = false;
        for (Object item : c) {
            if (remove(item)) {
                modified = true;
            }
        }
        return modified;
    }

    // Оставляет в нашем сете только те элементы, которые есть в переданной коллекции
    @Override
    public boolean retainAll(Collection<?> c) {
        boolean modified = false;
        Node<E> current = head;
        // Идём строго по цепочке порядка добавления, чтобы безопасно удалять элементы
        while (current != null) {
            Node<E> nextNode = current.after; // Запоминаем следующий узел заранее
            if (!c.contains(current.data)) {
                remove(current.data);
                modified = true;
            }
            current = nextNode;
        }
        return modified;
    }

    // Выводит элементы строго в порядке их добавления (от head к tail)
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        Node<E> current = head;
        while (current != null) {
            sb.append(current.data);
            if (current.after != null) {
                sb.append(", ");
            }
            current = current.after;
        }
        return sb.append("]").toString();
    }

    /////////////////////////////////////////////////////////////////////////
    //////          Заглушки для остальных методов Set                ///////
    /////////////////////////////////////////////////////////////////////////

    @Override public Iterator<E> iterator() { throw new UnsupportedOperationException(); }
    @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
    @Override public <T> T[] toArray(T[] a) { throw new UnsupportedOperationException(); }
}
