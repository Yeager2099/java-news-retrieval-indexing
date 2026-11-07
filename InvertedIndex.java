/*
 * I attest that the code in this file is entirely my own except for the starter
 * code provided with the assignment and the following exceptions:
 * <Enter all external resources and collaborations here. Note external code may
 * reduce your score but appropriate citation is required to avoid academic
 * integrity violations. Please see the Course Syllabus as well as the
 * university code of academic integrity:
 *  https://catalog.upenn.edu/pennbook/code-of-academic-integrity/ >
 * Signed,
 * Author: Yeager Ye
 * Penn email: <[EMAIL_REDACTED]> 
 * Date: 2025-09-29
 */

 import java.util.*;

 public class InvertedIndex {
 
     // Root of the BST
     private BSTNode root;
 
     // private static inner class representing a node in BST
     private static class BSTNode {
         String keyWord;
         Set<Integer> documentIDs;
         BSTNode left, right;
 
         BSTNode(String keyWord, int docID) {
             this.keyWord = keyWord;
             this.documentIDs = new HashSet<>();
             this.documentIDs.add(docID);
         }
     }
 
     // DO NOT CHANGE STOP_WORDS
     private static final Set<String> STOP_WORDS = Set.of(
             "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when",
             "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such",
             "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just",
             "don", "should", "now", "said", "announced", "company", "industry", "technology", "system", "application",
             "software", "update", "service"
     );
 
     /**
      * Adds a document into the index
      */
     public void addDocument(int docID, String text) {
         if (text == null || text.isEmpty()) return;
 
         String[] tokens = text.toLowerCase().split("[^a-z0-9]+"); // split non-alphanumeric
         for (String token : tokens) {
             if (token.isEmpty() || STOP_WORDS.contains(token)) continue;
             root = insert(root, token, docID);
         }
     }
 
     // helper insert into BST
     private BSTNode insert(BSTNode node, String word, int docID) {
         if (node == null) {
             return new BSTNode(word, docID);
         }
         int cmp = word.compareTo(node.keyWord);
         if (cmp < 0) {
             node.left = insert(node.left, word, docID);
         } else if (cmp > 0) {
             node.right = insert(node.right, word, docID);
         } else {
             node.documentIDs.add(docID);
         }
         return node;
     }
 
     /**
      * Search for documents containing all query words
      */
     public Set<Integer> search(String query) {
         Set<Integer> result = new HashSet<>();
         if (query == null || query.isEmpty()) return result;
 
         String[] tokens = query.toLowerCase().split("[^a-z0-9]+");
         List<Set<Integer>> postings = new ArrayList<>();
 
         for (String token : tokens) {
             if (token.isEmpty() || STOP_WORDS.contains(token)) continue;
             Set<Integer> docs = find(root, token);
             if (docs != null) postings.add(docs);
         }
 
         if (postings.isEmpty()) return result;
 
         // intersect all postings
         result.addAll(postings.get(0));
         for (int i = 1; i < postings.size(); i++) {
             result.retainAll(postings.get(i));
         }
 
         return result;
     }
 
     // helper search in BST
     private Set<Integer> find(BSTNode node, String word) {
         if (node == null) return null;
         int cmp = word.compareTo(node.keyWord);
         if (cmp < 0) return find(node.left, word);
         else if (cmp > 0) return find(node.right, word);
         else return node.documentIDs;
     }
 
     /**
      * Remove a document ID from all nodes
      */
     public void removeDocument(int docID) {
         removeHelper(root, docID);
     }
 
     private void removeHelper(BSTNode node, int docID) {
         if (node == null) return;
         node.documentIDs.remove(docID);
         removeHelper(node.left, docID);
         removeHelper(node.right, docID);
     }
 
     /**
      * Get the inverted index as a map
      */
     public Map<String, Set<Integer>> getIndex() {
         Map<String, Set<Integer>> map = new TreeMap<>();
         inorder(root, map);
         return map;
     }
 
     private void inorder(BSTNode node, Map<String, Set<Integer>> map) {
         if (node == null) return;
         inorder(node.left, map);
         map.put(node.keyWord, new HashSet<>(node.documentIDs));
         inorder(node.right, map);
     }
 }
 