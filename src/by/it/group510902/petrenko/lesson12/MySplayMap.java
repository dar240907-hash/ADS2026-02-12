package by.it.group510902.petrenko.lesson12;

import java.util.*;

public class MySplayMap implements NavigableMap<Integer, String> {

    private static class Node {
        Integer key;
        String value;
        Node left, right, parent;

        // Конструктор узла splay-дерева
        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
        }
    }

    private Node root;
    private int size = 0;

    // Один поворот узла x наверх (малый поворот)
    private void rotate(Node x) {
        Node p = x.parent;
        Node g = p.parent;
        if (g != null) {
            if (g.left == p) g.left = x;
            else g.right = x;
        } else {
            root = x;
        }
        x.parent = g;
        if (p.left == x) {
            p.left = x.right;
            if (x.right != null) x.right.parent = p;
            x.right = p;
        } else {
            p.right = x.left;
            if (x.left != null) x.left.parent = p;
            x.left = p;
        }
        p.parent = x;
    }

    // Поднимает узел x в корень
    private void splay(Node x) {
        if (x == null) return;
        while (x.parent != null) {
            Node p = x.parent;
            Node g = p.parent;
            if (g != null) {
                if ((g.left == p) == (p.left == x)) {
                    rotate(p);
                    rotate(x);
                } else {
                    rotate(x);
                    rotate(x);
                }
            } else {
                rotate(x);
            }
        }
        root = x;
    }

    // Ищет узел по ключу и делает его корнем
    private Node findNode(Integer key) {
        Node cur = root;
        Node last = null;
        while (cur != null) {
            last = cur;
            int cmp = key.compareTo(cur.key);
            if (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else {
                splay(cur);
                return root;
            }
        }
        if (last != null) splay(last);
        return null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("{");
        boolean[] first = {true};
        inOrder(root, sb, first);
        return sb.append('}').toString();
    }

    // Симметричный обход дерева для формирования строки
    private void inOrder(Node node, StringBuilder sb, boolean[] first) {
        if (node == null) return;
        inOrder(node.left, sb, first);
        if (!first[0]) sb.append(", ");
        sb.append(node.key).append('=').append(node.value);
        first[0] = false;
        inOrder(node.right, sb, first);
    }

    // Вставляет пару ключ-значение и делает узел корнем
    @Override
    public String put(Integer key, String value) {
        if (key == null) throw new NullPointerException();
        if (root == null) {
            root = new Node(key, value);
            size++;
            return null;
        }
        Node cur = root;
        Node parent = null;
        int cmp = 0;
        while (cur != null) {
            parent = cur;
            cmp = key.compareTo(cur.key);
            if (cmp < 0) cur = cur.left;
            else if (cmp > 0) cur = cur.right;
            else {
                String old = cur.value;
                cur.value = value;
                splay(cur);
                return old;
            }
        }
        Node newNode = new Node(key, value);
        newNode.parent = parent;
        if (cmp < 0) parent.left = newNode;
        else parent.right = newNode;
        splay(newNode);
        size++;
        return null;
    }

    // Удаляет элемент по ключу и делает соседний узел корнем
    @Override
    public String remove(Object keyObj) {
        if (!(keyObj instanceof Integer)) return null;
        Integer key = (Integer) keyObj;
        Node n = findNode(key);
        if (n == null) return null;

        String old = n.value;
        splay(n);
        if (n.left == null) {
            root = n.right;
            if (root != null) root.parent = null;
        } else {
            Node leftTree = n.left;
            leftTree.parent = null;
            Node maxLeft = leftTree;
            while (maxLeft.right != null) maxLeft = maxLeft.right;
            splay(maxLeft);
            maxLeft.right = n.right;
            if (n.right != null) n.right.parent = maxLeft;
            root = maxLeft;
        }
        size--;
        return old;
    }

    // Возвращает значение по ключу (и поднимает его в корень)
    @Override
    public String get(Object keyObj) {
        if (!(keyObj instanceof Integer)) return null;
        Node n = findNode((Integer) keyObj);
        return n == null ? null : n.value;
    }

    // Проверяет наличие ключа
    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) return false;
        return findNode((Integer) key) != null;
    }

    // Проверяет наличие значения
    @Override
    public boolean containsValue(Object value) {
        return containsValueHelper(root, value);
    }

    // Рекурсивный поиск значения
    private boolean containsValueHelper(Node n, Object value) {
        if (n == null) return false;
        if (Objects.equals(n.value, value)) return true;
        return containsValueHelper(n.left, value) || containsValueHelper(n.right, value);
    }

    // Возвращает количество элементов
    @Override
    public int size() { return size; }

    // Очищает дерево
    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    // Проверяет, пустое ли дерево
    @Override
    public boolean isEmpty() { return size == 0; }

    // Возвращает минимальный ключ (и поднимает его в корень)
    @Override
    public Integer firstKey() {
        if (root == null) throw new NoSuchElementException();
        Node cur = root;
        while (cur.left != null) cur = cur.left;
        splay(cur);
        return cur.key;
    }

    // Возвращает максимальный ключ (и поднимает его в корень)
    @Override
    public Integer lastKey() {
        if (root == null) throw new NoSuchElementException();
        Node cur = root;
        while (cur.right != null) cur = cur.right;
        splay(cur);
        return cur.key;
    }

    // Возвращает часть карты с ключами меньше toKey
    @Override
    public SortedMap<Integer, String> headMap(Integer toKey) {
        return new SubMap(null, toKey);
    }

    // Возвращает часть карты с ключами от fromKey и больше
    @Override
    public SortedMap<Integer, String> tailMap(Integer fromKey) {
        return new SubMap(fromKey, null);
    }

    // Возвращает наибольший ключ, строго меньший заданного
    @Override
    public Integer lowerKey(Integer key) {
        Node cur = root;
        Integer result = null;
        while (cur != null) {
            if (key.compareTo(cur.key) > 0) {
                result = cur.key;
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }
        return result;
    }

    // Возвращает наибольший ключ, меньший или равный заданному
    @Override
    public Integer floorKey(Integer key) {
        Node cur = root;
        Integer result = null;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.key;
            if (cmp > 0) {
                result = cur.key;
                cur = cur.right;
            } else {
                cur = cur.left;
            }
        }
        return result;
    }

    // Возвращает наименьший ключ, больший или равный заданному
    @Override
    public Integer ceilingKey(Integer key) {
        Node cur = root;
        Integer result = null;
        while (cur != null) {
            int cmp = key.compareTo(cur.key);
            if (cmp == 0) return cur.key;
            if (cmp < 0) {
                result = cur.key;
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return result;
    }

    // Возвращает наименьший ключ, строго больший заданного
    @Override
    public Integer higherKey(Integer key) {
        Node cur = root;
        Integer result = null;
        while (cur != null) {
            if (key.compareTo(cur.key) < 0) {
                result = cur.key;
                cur = cur.left;
            } else {
                cur = cur.right;
            }
        }
        return result;
    }

    // Компаратор (натуральный порядок)
    @Override public Comparator<? super Integer> comparator() { return null; }

    // Заглушка для subMap
    @Override public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) { throw new UnsupportedOperationException(); }

    // Заглушки для коллекций
    @Override public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
    @Override public Collection<String> values() { throw new UnsupportedOperationException(); }
    @Override public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }

    // Заглушка для putAll
    @Override public void putAll(Map<? extends Integer, ? extends String> m) { throw new UnsupportedOperationException(); }

    // Заглушки для методов NavigableMap, возвращающих Entry
    @Override public Map.Entry<Integer, String> lowerEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> floorEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> ceilingEntry(Integer key) { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> higherEntry(Integer key) { throw new UnsupportedOperationException(); }

    // Заглушки для first/last/poll Entry
    @Override public Map.Entry<Integer, String> firstEntry() { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> lastEntry() { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> pollFirstEntry() { throw new UnsupportedOperationException(); }
    @Override public Map.Entry<Integer, String> pollLastEntry() { throw new UnsupportedOperationException(); }

    // Заглушки для обратного порядка и наборов
    @Override public NavigableMap<Integer, String> descendingMap() { throw new UnsupportedOperationException(); }
    @Override public NavigableSet<Integer> navigableKeySet() { throw new UnsupportedOperationException(); }
    @Override public NavigableSet<Integer> descendingKeySet() { throw new UnsupportedOperationException(); }

    // Заглушки для NavigableMap-методов с флагами inclusive
    @Override public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) { throw new UnsupportedOperationException(); }
    @Override public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) { throw new UnsupportedOperationException(); }
    @Override public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) { throw new UnsupportedOperationException(); }


    // Внутренний класс для представления части карты
    private class SubMap implements NavigableMap<Integer, String> {
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

        // Возвращает количество элементов в диапазоне
        @Override public int size() {
            return countInRange(root);
        }

        // Рекурсивный подсчет элементов в диапазоне
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
            if (inRange(n.key)) MySplayMap.this.remove(n.key);
        }

        // Возвращает значение, если ключ в диапазоне
        @Override public String get(Object key) {
            if (!(key instanceof Integer)) return null;
            Integer k = (Integer) key;
            return inRange(k) ? MySplayMap.this.get(k) : null;
        }

        // Вставляет пару, если ключ в диапазоне
        @Override public String put(Integer key, String value) {
            if (!inRange(key)) throw new IllegalArgumentException();
            return MySplayMap.this.put(key, value);
        }

        // Удаляет элемент, если ключ в диапазоне
        @Override public String remove(Object key) {
            if (!(key instanceof Integer)) return null;
            Integer k = (Integer) key;
            if (!inRange(k)) return null;
            return MySplayMap.this.remove(k);
        }

        // Проверяет наличие ключа в диапазоне
        @Override public boolean containsKey(Object key) {
            if (!(key instanceof Integer)) return false;
            Integer k = (Integer) key;
            return inRange(k) && MySplayMap.this.containsKey(k);
        }

        // Проверяет наличие значения в диапазоне
        @Override public boolean containsValue(Object value) {
            return containsValueInRange(root, value);
        }

        // Рекурсивный поиск значения в диапазоне
        private boolean containsValueInRange(Node n, Object value) {
            if (n == null) return false;
            if (inRange(n.key) && Objects.equals(n.value, value)) return true;
            return containsValueInRange(n.left, value) || containsValueInRange(n.right, value);
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

        // Возвращает наибольший ключ в диапазоне, строго меньший заданного
        @Override public Integer lowerKey(Integer key) {
            Integer r = lowerInRange(root, key);
            return r;
        }

        // Рекурсивный поиск в диапазоне
        private Integer lowerInRange(Node n, Integer key) {
            if (n == null) return null;
            if (inRange(n.key) && n.key.compareTo(key) < 0) {
                Integer right = lowerInRange(n.right, key);
                return right != null ? right : n.key;
            }
            return lowerInRange(n.left, key);
        }

        // Возвращает наибольший ключ в диапазоне, меньший или равный заданному
        @Override public Integer floorKey(Integer key) {
            Integer r = floorInRange(root, key);
            return r;
        }

        // Рекурсивный поиск в диапазоне
        private Integer floorInRange(Node n, Integer key) {
            if (n == null) return null;
            if (inRange(n.key) && n.key.compareTo(key) <= 0) {
                Integer right = floorInRange(n.right, key);
                return right != null ? right : n.key;
            }
            return floorInRange(n.left, key);
        }

        // Возвращает наименьший ключ в диапазоне, больший или равный заданному
        @Override public Integer ceilingKey(Integer key) {
            Integer r = ceilingInRange(root, key);
            return r;
        }

        // Рекурсивный поиск в диапазоне
        private Integer ceilingInRange(Node n, Integer key) {
            if (n == null) return null;
            if (inRange(n.key) && n.key.compareTo(key) >= 0) {
                Integer left = ceilingInRange(n.left, key);
                return left != null ? left : n.key;
            }
            return ceilingInRange(n.right, key);
        }

        // Возвращает наименьший ключ в диапазоне, строго больший заданного
        @Override public Integer higherKey(Integer key) {
            Integer r = higherInRange(root, key);
            return r;
        }

        // Рекурсивный поиск в диапазоне
        private Integer higherInRange(Node n, Integer key) {
            if (n == null) return null;
            if (inRange(n.key) && n.key.compareTo(key) > 0) {
                Integer left = higherInRange(n.left, key);
                return left != null ? left : n.key;
            }
            return higherInRange(n.right, key);
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

        // Заглушки для интерфейса NavigableMap в SubMap
        @Override public Comparator<? super Integer> comparator() { return null; }
        @Override public SortedMap<Integer, String> subMap(Integer fromKey, Integer toKey) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<Integer, String> headMap(Integer toKey) { throw new UnsupportedOperationException(); }
        @Override public SortedMap<Integer, String> tailMap(Integer fromKey) { throw new UnsupportedOperationException(); }

        @Override public Set<Integer> keySet() { throw new UnsupportedOperationException(); }
        @Override public Collection<String> values() { throw new UnsupportedOperationException(); }
        @Override public Set<Map.Entry<Integer, String>> entrySet() { throw new UnsupportedOperationException(); }
        @Override public void putAll(Map<? extends Integer, ? extends String> m) { throw new UnsupportedOperationException(); }

        @Override public Map.Entry<Integer, String> lowerEntry(Integer key) { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> floorEntry(Integer key) { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> ceilingEntry(Integer key) { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> higherEntry(Integer key) { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> firstEntry() { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> lastEntry() { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> pollFirstEntry() { throw new UnsupportedOperationException(); }
        @Override public Map.Entry<Integer, String> pollLastEntry() { throw new UnsupportedOperationException(); }

        @Override public NavigableMap<Integer, String> descendingMap() { throw new UnsupportedOperationException(); }
        @Override public NavigableSet<Integer> navigableKeySet() { throw new UnsupportedOperationException(); }
        @Override public NavigableSet<Integer> descendingKeySet() { throw new UnsupportedOperationException(); }

        @Override public NavigableMap<Integer, String> subMap(Integer fromKey, boolean fromInclusive, Integer toKey, boolean toInclusive) { throw new UnsupportedOperationException(); }
        @Override public NavigableMap<Integer, String> headMap(Integer toKey, boolean inclusive) { throw new UnsupportedOperationException(); }
        @Override public NavigableMap<Integer, String> tailMap(Integer fromKey, boolean inclusive) { throw new UnsupportedOperationException(); }
    }
}