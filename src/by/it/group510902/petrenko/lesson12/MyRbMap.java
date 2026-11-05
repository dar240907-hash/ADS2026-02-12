package by.it.group510902.petrenko.lesson12;

import java.util.SortedMap;
import java.util.Map;
import java.util.Set;
import java.util.Collection;
import java.util.Comparator;
import java.util.Objects;
import java.util.NoSuchElementException;

public class MyRbMap implements SortedMap<Integer, String> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;
        boolean color;

        // Конструктор узла дерева
        Node(Integer key, String value, boolean color, Node parent) {
            this.key = key;
            this.value = value;
            this.color = color;
            this.parent = parent;
        }
    }

    private Node root;
    private int size = 0;

    // Возвращает количество элементов в дереве
    @Override
    public int size() { return size; }

    // Проверяет, пустое ли дерево
    @Override
    public boolean isEmpty() { return size == 0; }

    // Очищает дерево полностью
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Проверяет наличие ключа в дереве
    @Override
    public boolean containsKey(Object key) {
        if (key == null) throw new NullPointerException();
        return findNode((Integer) key) != null;
    }

    // Проверяет наличие значения в дереве
    @Override
    public boolean containsValue(Object value) {
        return containsValueHelper(root, value);
    }

    // Рекурсивный поиск значения по всему дереву
    private boolean containsValueHelper(Node node, Object value) {
        if (node == null) return false;
        if (Objects.equals(node.value, value)) return true;
        return containsValueHelper(node.left, value)
                || containsValueHelper(node.right, value);
    }

    // Возвращает значение по ключу
    @Override
    public String get(Object key) {
        if (key == null) throw new NullPointerException();
        Node node = findNode((Integer) key);
        return node != null ? node.value : null;
    }

    // Добавляет или обновляет пару ключ-значение, возвращает старое значение
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();
        if (root == null) {
            root = new Node(key, value, BLACK, null);
            size++;
            return null;
        }
        Node t = root;
        Node parent;
        int cmp;
        do {
            parent = t;
            cmp = key.compareTo(t.key);
            if (cmp < 0) t = t.left;
            else if (cmp > 0) t = t.right;
            else {
                String old = t.value;
                t.value = value;
                return old;
            }
        } while (t != null);

        Node e = new Node(key, value, RED, parent);
        if (cmp < 0) parent.left = e;
        else parent.right = e;
        fixAfterInsertion(e);
        size++;
        return null;
    }

    // Удаляет элемент по ключу и возвращает его значение
    @Override
    public String remove(Object key) {
        if (key == null) throw new NullPointerException();
        Node p = findNode((Integer) key);
        if (p == null) return null;
        String old = p.value;
        deleteNode(p);
        return old;
    }

    // Возвращает минимальный ключ в дереве
    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        Node p = root;
        while (p.left != null) p = p.left;
        return p.key;
    }

    // Возвращает максимальный ключ в дереве
    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        Node p = root;
        while (p.right != null) p = p.right;
        return p.key;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean[] first = {true};
        buildString(root, sb, first);
        return sb.append('}').toString();
    }

    // Симметричный обход дерева для формирования строки
    private void buildString(Node node, StringBuilder sb, boolean[] first) {
        if (node == null) return;
        buildString(node.left, sb, first);
        if (!first[0]) sb.append(", ");
        sb.append(node.key).append('=').append(node.value);
        first[0] = false;
        buildString(node.right, sb, first);
    }

    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        if (toKey == null) throw new NullPointerException();
        return new SubMap(null, toKey);
    }

    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        if (fromKey == null) throw new NullPointerException();
        return new SubMap(fromKey, null);
    }

    @Override
    public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
        if (fromKey == null || toKey == null) throw new NullPointerException();
        return new SubMap(fromKey, toKey);
    }

    // Возвращает компаратор
    @Override
    public Comparator<? super Integer> comparator() { return null; }

    // Копирует все элементы из другой карты
    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        if (m == null) throw new NullPointerException();
        for (Map.Entry<? extends Integer, ? extends String> e : m.entrySet()) {
            put(e.getKey(), e.getValue());
        }
    }
    @Override public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override public Collection<String> values() { throw new UnsupportedOperationException(); }
    @Override public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }

    // Ищет узел по ключу обычным спуском
    private Node findNode(Integer key) {
        Node p = root;
        while (p != null) {
            int cmp = key.compareTo(p.key);
            if (cmp < 0) p = p.left;
            else if (cmp > 0) p = p.right;
            else return p;
        }
        return null;
    }

    // Левый поворот вокруг узла p
    private void rotateLeft(Node p) {
        if (p != null) {
            Node r = p.right;
            p.right = r.left;
            if (r.left != null) r.left.parent = p;
            r.parent = p.parent;
            if (p.parent == null) root = r;
            else if (p.parent.left == p) p.parent.left = r;
            else p.parent.right = r;
            r.left = p;
            p.parent = r;
        }
    }

    // Правый поворот вокруг узла p
    private void rotateRight(Node p) {
        if (p != null) {
            Node l = p.left;
            p.left = l.right;
            if (l.right != null) l.right.parent = p;
            l.parent = p.parent;
            if (p.parent == null) root = l;
            else if (p.parent.right == p) p.parent.right = l;
            else p.parent.left = l;
            l.right = p;
            p.parent = l;
        }
    }

    // Восстанавливает свойства красно-черного дерева после вставки
    private void fixAfterInsertion(Node x) {
        x.color = RED;
        while (x != null && x != root && x.parent.color == RED) {
            if (parentOf(x) == leftOf(parentOf(parentOf(x)))) {
                Node y = rightOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == rightOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateLeft(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateRight(parentOf(parentOf(x)));
                }
            } else {
                Node y = leftOf(parentOf(parentOf(x)));
                if (colorOf(y) == RED) {
                    setColor(parentOf(x), BLACK);
                    setColor(y, BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    x = parentOf(parentOf(x));
                } else {
                    if (x == leftOf(parentOf(x))) {
                        x = parentOf(x);
                        rotateRight(x);
                    }
                    setColor(parentOf(x), BLACK);
                    setColor(parentOf(parentOf(x)), RED);
                    rotateLeft(parentOf(parentOf(x)));
                }
            }
        }
        root.color = BLACK;
    }

    // Удаляет узел p из дерева
    private void deleteNode(Node p) {
        size--;
        if (p.left != null && p.right != null) {
            Node s = successor(p);
            p.key = s.key;
            p.value = s.value;
            p = s;
        }
        Node replacement = (p.left != null ? p.left : p.right);
        if (replacement != null) {
            replacement.parent = p.parent;
            if (p.parent == null) root = replacement;
            else if (p == p.parent.left) p.parent.left = replacement;
            else p.parent.right = replacement;
            p.left = p.right = p.parent = null;
            if (p.color == BLACK) fixAfterDeletion(replacement);
        } else if (p.parent == null) {
            root = null;
        } else {
            if (p.color == BLACK) fixAfterDeletion(p);
            if (p.parent != null) {
                if (p == p.parent.left) p.parent.left = null;
                else if (p == p.parent.right) p.parent.right = null;
                p.parent = null;
            }
        }
    }

    // Восстанавливает свойства красно-черного дерева после удаления
    private void fixAfterDeletion(Node x) {
        while (x != root && colorOf(x) == BLACK) {
            if (x == leftOf(parentOf(x))) {
                Node sib = rightOf(parentOf(x));
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateLeft(parentOf(x));
                    sib = rightOf(parentOf(x));
                }
                if (colorOf(leftOf(sib)) == BLACK && colorOf(rightOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(rightOf(sib)) == BLACK) {
                        setColor(leftOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateRight(sib);
                        sib = rightOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(rightOf(sib), BLACK);
                    rotateLeft(parentOf(x));
                    x = root;
                }
            } else {
                Node sib = leftOf(parentOf(x));
                if (colorOf(sib) == RED) {
                    setColor(sib, BLACK);
                    setColor(parentOf(x), RED);
                    rotateRight(parentOf(x));
                    sib = leftOf(parentOf(x));
                }
                if (colorOf(rightOf(sib)) == BLACK && colorOf(leftOf(sib)) == BLACK) {
                    setColor(sib, RED);
                    x = parentOf(x);
                } else {
                    if (colorOf(leftOf(sib)) == BLACK) {
                        setColor(rightOf(sib), BLACK);
                        setColor(sib, RED);
                        rotateLeft(sib);
                        sib = leftOf(parentOf(x));
                    }
                    setColor(sib, colorOf(parentOf(x)));
                    setColor(parentOf(x), BLACK);
                    setColor(leftOf(sib), BLACK);
                    rotateRight(parentOf(x));
                    x = root;
                }
            }
        }
        setColor(x, BLACK);
    }

    // Находит следующий по порядку узел
    private Node successor(Node t) {
        if (t == null) return null;
        if (t.right != null) {
            Node p = t.right;
            while (p.left != null) p = p.left;
            return p;
        } else {
            Node p = t.parent;
            Node ch = t;
            while (p != null && ch == p.right) {
                ch = p;
                p = p.parent;
            }
            return p;
        }
    }

    // Возвращает цвет узла
    private boolean colorOf(Node p) { return p == null ? BLACK : p.color; }
    // Возвращает родителя узла
    private Node parentOf(Node p) { return p == null ? null : p.parent; }
    // Устанавливает цвет узла
    private void setColor(Node p, boolean c) { if (p != null) p.color = c; }
    // Возвращает левого ребенка
    private Node leftOf(Node p) { return p == null ? null : p.left; }
    // Возвращает правого ребенка
    private Node rightOf(Node p) { return p == null ? null : p.right; }

    // Внутренний класс для представления части карты
    private class SubMap implements SortedMap<Integer, String> {
        private final Integer fromKey;
        private final Integer toKey;

        // Конструктор подкарты с границами
        SubMap(Integer fromKey, Integer toKey) {
            this.fromKey = fromKey;
            this.toKey = toKey;
        }

        // Проверяет, входит ли ключ в диапазон подкарты
        private boolean inRange(Integer key) {
            if (fromKey != null && key.compareTo(fromKey) < 0) return false;
            if (toKey != null && key.compareTo(toKey) >= 0) return false;
            return true;
        }

        // Возвращает значение, если ключ в диапазоне
        @Override public String get(Object key) {
            if (!(key instanceof Integer)) return null;
            Integer k = (Integer) key;
            return inRange(k) ? MyRbMap.this.get(k) : null;
        }

        // Вставляет пару, если ключ в диапазоне
        @Override public String put(Integer key, String value) {
            if (key == null) throw new NullPointerException();
            if (!inRange(key)) throw new IllegalArgumentException("Key out of range");
            return MyRbMap.this.put(key, value);
        }

        // Удаляет элемент, если ключ в диапазоне
        @Override public String remove(Object key) {
            if (!(key instanceof Integer)) return null;
            Integer k = (Integer) key;
            if (!inRange(k)) return null;
            return MyRbMap.this.remove(k);
        }

        // Проверяет наличие ключа в диапазоне
        @Override public boolean containsKey(Object key) {
            if (!(key instanceof Integer)) return false;
            Integer k = (Integer) key;
            return inRange(k) && MyRbMap.this.containsKey(k);
        }

        // Проверяет наличие значения в диапазоне
        @Override public boolean containsValue(Object value) {
            return containsValueInRange(root, value);
        }

        // Рекурсивный поиск значения только в диапазоне
        private boolean containsValueInRange(Node n, Object value) {
            if (n == null) return false;
            if (inRange(n.key) && Objects.equals(n.value, value)) return true;
            return containsValueInRange(n.left, value) || containsValueInRange(n.right, value);
        }

        // Возвращает количество элементов в диапазоне
        @Override public int size() { return countInRange(root); }

        // Рекурсивно считает элементы в диапазоне
        private int countInRange(Node n) {
            if (n == null) return 0;
            int self = inRange(n.key) ? 1 : 0;
            return self + countInRange(n.left) + countInRange(n.right);
        }

        // Проверяет, пуста ли подкарта
        @Override public boolean isEmpty() { return size() == 0; }

        // Очищает только элементы в диапазоне
        @Override public void clear() { clearInRange(root); }

        // Рекурсивно удаляет элементы в диапазоне
        private void clearInRange(Node n) {
            if (n == null) return;
            clearInRange(n.left);
            clearInRange(n.right);
            if (inRange(n.key)) MyRbMap.this.remove(n.key);
        }

        // Возвращает минимальный ключ в диапазоне
        @Override public Integer firstKey() {
            Integer r = firstInRange(root);
            if (r == null) throw new NoSuchElementException();
            return r;
        }

        // Рекурсивный поиск минимального ключа в диапазоне
        private Integer firstInRange(Node n) {
            if (n == null) return null;
            if (fromKey != null && n.key.compareTo(fromKey) < 0) return firstInRange(n.right);
            Integer l = firstInRange(n.left);
            if (l != null) return l;
            return inRange(n.key) ? n.key : firstInRange(n.right);
        }

        // Возвращает максимальный ключ в диапазоне
        @Override public Integer lastKey() {
            Integer r = lastInRange(root);
            if (r == null) throw new NoSuchElementException();
            return r;
        }

        // Рекурсивный поиск максимального ключа в диапазоне
        private Integer lastInRange(Node n) {
            if (n == null) return null;
            if (toKey != null && n.key.compareTo(toKey) >= 0) return lastInRange(n.left);
            Integer r = lastInRange(n.right);
            if (r != null) return r;
            return inRange(n.key) ? n.key : lastInRange(n.left);
        }

        // Компаратор (натуральный порядок)
        @Override public Comparator<? super Integer> comparator() { return null; }

        // Возвращает подкарту с ключами меньше toKey (в рамках текущих границ)
        @Override public SortedMap<Integer, String> headMap(Integer toKey) {
            if (toKey == null) throw new NullPointerException();
            return new SubMap(fromKey, toKey);
        }

        // Возвращает подкарту с ключами от newFromKey (в рамках текущих границ)
        @Override public SortedMap<Integer, String> tailMap(Integer newFromKey) {
            if (newFromKey == null) throw new NullPointerException();
            return new SubMap(newFromKey, this.toKey);
        }

        // Возвращает подкарту в новом диапазоне
        @Override public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) {
            if (fromKey == null || toKey == null) throw new NullPointerException();
            return new SubMap(fromKey, toKey);
        }

        // Заглушки для методов Map
        @Override public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
        @Override public Collection<String> values() { throw new UnsupportedOperationException(); }
        @Override public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }

        // Копирует элементы из другой карты (только в диапазон)
        @Override public void putAll(Map<? extends Integer, ? extends String> m) {
            if (m == null) throw new NullPointerException();
            for (Map.Entry<? extends Integer, ? extends String> e : m.entrySet()) {
                put(e.getKey(), e.getValue());
            }
        }

        @Override public String toString() {
            StringBuilder sb = new StringBuilder("{");
            boolean[] first = {true};
            appendRange(root, sb, first);
            return sb.append('}').toString();
        }

        // Симметричный обход для подкарты
        private void appendRange(Node n, StringBuilder sb, boolean[] first) {
            if (n == null) return;
            appendRange(n.left, sb, first);
            if (inRange(n.key)) {
                if (!first[0]) sb.append(", ");
                sb.append(n.key).append('=').append(n.value);
                first[0] = false;
            }
            appendRange(n.right, sb, first);
        }
    }
}