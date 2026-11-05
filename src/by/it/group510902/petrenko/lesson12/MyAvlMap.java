package by.it.group510902.petrenko.lesson12;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

public class MyAvlMap implements Map<Integer, String> {

    // Внутренний класс узла АВЛ-дерева
    private static class Node {
        Integer key;
        String value;
        int height;
        Node left;
        Node right;

        Node(Integer key, String value) {
            this.key = key;
            this.value = value;
            this.height = 1;
        }
    }

    private Node root;
    private int size;

    public MyAvlMap() {
        this.root = null;
        this.size = 0;
    }

    // Вспомогательные методы для АВЛ-балансировки
    private int height(Node node) {
        return node == null ? 0 : node.height;
    }

    private int getBalance(Node node) {
        return node == null ? 0 : height(node.left) - height(node.right);
    }

    private void updateHeight(Node node) {
        if (node != null) {
            node.height = 1 + Math.max(height(node.left), height(node.right));
        }
    }

    // Правый поворот вокруг узла
    private Node rotateRight(Node node) {
        Node leftChild = node.left;
        Node rightOfLeft = leftChild.right;

        leftChild.right = node;
        node.left = rightOfLeft;

        updateHeight(node);
        updateHeight(leftChild);

        return leftChild;
    }

    // Левый поворот вокруг узла
    private Node rotateLeft(Node node) {
        Node rightChild = node.right;
        Node leftOfRight = rightChild.left;

        rightChild.left = node;
        node.right = leftOfRight;

        updateHeight(node);
        updateHeight(rightChild);

        return rightChild;
    }

    // Балансировка узла
    private Node balance(Node node) {
        updateHeight(node);
        int balance = getBalance(node);

        // Левый левый случай
        if (balance > 1 && getBalance(node.left) >= 0) {
            return rotateRight(node);
        }
        // Левый правый случай
        if (balance > 1 && getBalance(node.left) < 0) {
            node.left = rotateLeft(node.left);
            return rotateRight(node);
        }
        // Правый правый случай
        if (balance < -1 && getBalance(node.right) <= 0) {
            return rotateLeft(node);
        }
        // Правый левый случай
        if (balance < -1 && getBalance(node.right) > 0) {
            node.right = rotateRight(node.right);
            return rotateLeft(node);
        }

        return node;
    }

    /////////////////////////////////////////////////////////////////////////
    //////               Обязательные к реализации методы             ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public int size() {
        return this.size;
    }

    @Override
    public boolean isEmpty() {
        return this.size == 0;
    }

    @Override
    public void clear() {
        this.root = null;
        this.size = 0;
    }

    @Override
    public boolean containsKey(Object key) {
        if (!(key instanceof Integer)) {
            return false;
        }
        return findNode(root, (Integer) key) != null;
    }

    private Node findNode(Node node, Integer key) {
        if (node == null) return null;
        int cmp = key.compareTo(node.key);
        if (cmp < 0) return findNode(node.left, key);
        else if (cmp > 0) return findNode(node.right, key);
        else return node;
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        Node node = findNode(root, (Integer) key);
        return node == null ? null : node.value;
    }

    @Override
    public String put(Integer key, String value) {
        if (key == null) {
            throw new NullPointerException("Ключ не может быть null");
        }

        // Вспомогательный массив, чтобы вытащить старое значение из рекурсии
        String[] oldValueContainer = new String[1];
        root = putHelper(root, key, value, oldValueContainer);
        return oldValueContainer[0];
    }

    private Node putHelper(Node node, Integer key, String value, String[] oldValue) {
        if (node == null) {
            size++;
            return new Node(key, value);
        }

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = putHelper(node.left, key, value, oldValue);
        } else if (cmp > 0) {
            node.right = putHelper(node.right, key, value, oldValue);
        } else {
            oldValue[0] = node.value;
            node.value = value;
            return node; // Балансировка не требуется, если структура дерева не изменилась
        }

        return balance(node);
    }

    @Override
    public String remove(Object key) {
        if (!(key instanceof Integer)) {
            return null;
        }
        String[] oldValueContainer = new String[1];
        root = removeHelper(root, (Integer) key, oldValueContainer);
        return oldValueContainer[0];
    }

    private Node removeHelper(Node node, Integer key, String[] oldValue) {
        if (node == null) return null;

        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            node.left = removeHelper(node.left, key, oldValue);
        } else if (cmp > 0) {
            node.right = removeHelper(node.right, key, oldValue);
        } else {
            oldValue[0] = node.value;
            size--;

            if (node.left == null || node.right == null) {
                node = (node.left != null) ? node.left : node.right;
            } else {
                Node minNode = findMin(node.right);
                node.key = minNode.key;
                node.value = minNode.value;
                // Сдвигаем удаление на место найденного минимума (старое значение сохранять не нужно)
                String[] dummy = new String[1];
                node.right = removeHelper(node.right, minNode.key, dummy);
                size++; // Компенсируем уменьшение размера внутри вложенного вызова
            }
        }

        if (node == null) return null;
        return balance(node);
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    @Override
    public String toString() {
        if (isEmpty()) {
            return "{}";
        }
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        buildStringInOrder(root, sb);
        sb.setLength(sb.length() - 2);
        sb.append("}");
        return sb.toString();
    }

    private void buildStringInOrder(Node node, StringBuilder sb) {
        if (node == null) return;

        buildStringInOrder(node.left, sb);
        sb.append(node.key).append("=").append(node.value).append(", ");
        buildStringInOrder(node.right, sb);
    }

    /////////////////////////////////////////////////////////////////////////
    //////        Необязательные методы (заглушки для Map)            ///////
    /////////////////////////////////////////////////////////////////////////

    @Override
    public boolean containsValue(Object value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void putAll(Map<? extends Integer, ? extends String> m) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Integer> keySet() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> values() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<Entry<Integer, String>> entrySet() {
        throw new UnsupportedOperationException();
    }
}

