package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private static class Node {
        Task item;
        Node next;
        Node prev;

        Node(Node prev, Task element, Node next) {
            this.item = element;
            this.next = next;
            this.prev = prev;
        }
    }

    private Node first;
    private Node last;
    private HashMap<Integer, Node> history = new HashMap<>();

    public void add(Task task) {
        Node node = history.get(task.getIdentifier());
        removeNode(node);
        linkLast(task);
    }

    public List<Task> getHistory() {
        return getAll();
    }

    @Override
    public void remove(int id) {
        Node node = history.get(id);
        removeNode(node);
    }

    public void linkLast(Task task) {
        final Node oldLast = last;
        final Node newNode = new Node(oldLast, task, null);
        last = newNode;
        if (oldLast == null) {
            first = newNode;
        } else {
            oldLast.next = newNode;
        }
        history.put(task.getIdentifier(), newNode);
    }

    public List<Task> getAll() {
        ArrayList<Task> historyList = new ArrayList<>();
        Node current = first;
        while (current != null) {
            historyList.add(current.item);
            current = current.next;
        }
        return historyList;
    }

    private void removeNode(Node node) {
        if (node == null) return;
        history.remove(node.item.getIdentifier());
        if (first == last) {
            first = null;
            last = null;
            return;
        }
        if (first == node) {
            first = node.next;
            first.prev = null;
            return;
        }
        if (last == node) {
            last = node.prev;
            last.next = null;
            return;
        }
        node.next.prev = node.prev;
        node.prev.next = node.next;
    }
}
