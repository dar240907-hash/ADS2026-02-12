package by.it.group510902.petrenko.lesson11;

import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

public class MyTreeSet<E> implements Set<E> {

    private E[] elements;       // Массив, в котором хранятся элементы в отсортированном порядке
    private int size;           // Текущее количество элементов
    private static final int DEFAULT_CAPACITY = 16;

    @SuppressWarnings("unchecked")
    public MyTreeSet() {
        this.elements = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Вспомогательный метод: увеличивает массив в 2 раза, если он заполнился
    @SuppressWarnings("unchecked")
    private void resize() {
        int newCapacity = elements.length * 2;
        E[] newElements = (E[]) new Object[newCapacity];
        for (int i = 0; i < size; i++) {
            newElements[i] = elements[i];
        }
        this.elements = newElements;
    }

    // Вспомогательный метод: ищет элемент с помощью бинарного поиска за O(log n)
    @SuppressWarnings("unchecked")
    private int binarySearch(Object o) {
        if (o == null) {
            return -1;
        }
        Comparable<? super E> key = (Comparable<? super E>) o;
        int low = 0;
        int high = size - 1;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            E midVal = elements[mid];
            int cmp = key.compareTo(midVal);

            if (cmp > 0) {
                low = mid + 1;
            } else if (cmp < 0) {
                high = mid - 1;
            } else {
                return mid; // Элемент найден, возвращаем его индекс
            }
        }
        // Если элемент не найден, возвращаем позицию, куда его нужно вставить (в отрицательном виде)
        return -(low + 1);
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    // Возвращает количество элементов в множестве
    @Override
    public int size() {
        return size;
    }

    // Проверяет, пусто ли множество
    @Override
    public boolean isEmpty() {
        return size == 0;
    }

    // Полная очистка множества
    @SuppressWarnings("unchecked")
    @Override
    public void clear() {
        this.elements = (E[]) new Object[DEFAULT_CAPACITY];
        this.size = 0;
    }

    // Проверяет, присутствует ли объект в множестве
    @Override
    public boolean contains(Object o) {
        return binarySearch(o) >= 0;
    }

    // Добавление элемента с сохранением сортировки
    @Override
    public boolean add(E element) {
        if (element == null) {
            throw new NullPointerException();
        }

        int index = binarySearch(element);
        if (index >= 0) {
            return false;
        }

        // Переводим отрицательный индекс обратно в позицию для вставки
        int insertIndex = -(index + 1);

        if (size >= elements.length) {
            resize();
        }

        // Сдвигаем все элементы вправо, освобождая место для нового
        for (int i = size; i > insertIndex; i--) {
            elements[i] = elements[i - 1];
        }

        elements[insertIndex] = element;
        size++;
        return true;
    }

    // Удаление конкретного элемента
    @Override
    public boolean remove(Object o) {
        int index = binarySearch(o);
        if (index < 0) {
            return false; // Элемент не найден
        }

        // Сдвигаем все элементы влево, затирая удаленный элемент
        for (int i = index; i < size - 1; i++) {
            elements[i] = elements[i + 1];
        }

        elements[size - 1] = null;
        size--;
        return true;
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
        for (int i = size - 1; i >= 0; i--) {
            if (!c.contains(elements[i])) {
                remove(elements[i]);
                modified = true;
            }
        }
        return modified;
    }

    // Выводит элементы в порядке возрастания
    @Override
    public String toString() {
        if (size == 0) {
            return "[]";
        }
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < size; i++) {
            sb.append(elements[i]);
            if (i < size - 1) {
                sb.append(", ");
            }
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
