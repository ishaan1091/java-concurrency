package com.concurrency.classical.problems.threadsafe.lru.cache;

import java.util.HashMap;
import java.util.Map;

public class NonThreadSafeLRUCache {

    private static class Node {
        private final Integer key;
        private Integer val;
        private Long expireAt;
        private Node next;
        private Node prev;

        private Node(Integer key, Integer val, Long expireAt) {
            this.key = key;
            this.val = val;
            this.expireAt = expireAt;
            this.next = null;
            this.prev = null;
        }

        public Integer getKey() {
            return this.key;
        }

        public Integer getVal() {
            return this.val;
        }

        public void setVal(Integer val) {
            this.val = val;
        }

        public Node getNext() {
            return this.next;
        }

        public void setNext(Node node) {
            this.next = node;
        }

        public Node getPrev() {
            return this.prev;
        }

        public void setPrev(Node node) {
            this.prev = node;
        }

        public Boolean isExpired() {
            return expireAt != -1 && System.currentTimeMillis() >= expireAt;
        }

        public void setExpireAt(Long expireAt) {
            this.expireAt = expireAt;
        }

        public void update(Integer val, Long expireAt) {
            this.setVal(val);
            this.setExpireAt(expireAt);
        }
    }

    private static class LRUCache {

        private final Map<Integer, Node> keyVsNode;
        private final Node head;
        private final Node tail;

        public LRUCache() {
            keyVsNode = new HashMap<>();
            head = new Node(0, 0, -1L);
            tail = new Node(0, 0, -1L);
        }

        public void put(Integer key, Integer val, Long expireAfter) {
            evictExpiredEntries();
            if (keyVsNode.containsKey(key)) {
                keyVsNode.get(key).update(val, System.currentTimeMillis() + expireAfter);
            } else {
                Node node = new Node(key, val, System.currentTimeMillis() + expireAfter);
                addNode(node);
                keyVsNode.put(key, node);
            }
        }

        public Integer get(Integer key) {
            evictExpiredEntries();
            if (keyVsNode.containsKey(key)) {
                return keyVsNode.get(key).getVal();
            }
            return null;
        }

        public Boolean remove(Integer key) {
            if (!keyVsNode.containsKey(key)) {
                return false;
            }
            removeNode(keyVsNode.get(key));
            keyVsNode.remove(key);
            return true;
        }

        private void addNode(Node node) {
            if (node == head || node == tail) {
                return;
            }
            head.getNext().setPrev(node);
            node.setNext(head.getNext());
            node.setPrev(head);
            head.setNext(node);
        }

        private void removeNode(Node node) {
            if (node == head || node == tail) {
                return;
            }
            node.getPrev().setNext(node.getNext());
            node.getNext().setPrev(node.getPrev());
        }

        private void evictExpiredEntries() {
            for (Map.Entry<Integer, Node> entry : keyVsNode.entrySet()) {
                if (entry.getValue().isExpired()) {
                    remove(entry.getKey());
                }
            }
        }
    }

    public static void main(String[] args) {

    }
}
