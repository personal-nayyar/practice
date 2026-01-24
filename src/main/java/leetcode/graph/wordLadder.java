package leetcode.graph;

import java.util.*;

public class wordLadder {
    // Helper: check if two words differ by exactly one letter
    private boolean diffByOne(String a, String b) {
        if (a.length() != b.length()) return false;

        int diff = 0;
        for (int i = 0; i < a.length(); i++) {
            if (a.charAt(i) != b.charAt(i)) {
                diff++;
                if (diff > 1) return false;  // more than 1 difference
            }
        }
        return diff == 1; // must be exactly 1 difference
    }

    public int ladderLength(String beginWord, String endWord, List<String> wordList) {
        // If endWord is not in the list, no transformation is possible
        if (!wordList.contains(endWord)) return 0;

        // 1️⃣ Build an array of all words (include beginWord if needed)
        List<String> allWords = new ArrayList<>(wordList);

        // Ensure beginWord is also part of the node set
        if (!allWords.contains(beginWord)) {
            allWords.add(beginWord);
        }

        int n = allWords.size();

        // Index of beginWord and endWord in allWords list
        int beginIndex = allWords.indexOf(beginWord);
        int endIndex = allWords.indexOf(endWord);

        // 2️⃣ Build graph: adjacency list
        // graph[i] = list of neighbors (indices) of word i
        List<List<Integer>> graph = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            graph.add(new ArrayList<>());
        }

        // For every pair of words, if they differ by one letter, connect them
        for (int i = 0; i < n; i++) {
            String wi = allWords.get(i);
            for (int j = i + 1; j < n; j++) {
                String wj = allWords.get(j);
                if (diffByOne(wi, wj)) {
                    graph.get(i).add(j); // undirected edge
                    graph.get(j).add(i);
                }
            }
        }

        // 3️⃣ BFS from beginIndex to find shortest path to endIndex
        Queue<Integer> queue = new LinkedList<>();
        boolean[] visited = new boolean[n];
        int[] dist =  new int[n];

        queue.add(beginIndex);
        visited[beginIndex] =  true;
        dist[beginIndex] = 0;

        while (!queue.isEmpty()){
            int curr = queue.poll();
//            System.out.println(curr);

            if (curr == endIndex){
                return dist[curr];
            }


            for(int nei : graph.get(curr)){
                if (!visited[nei]){
                    queue.add(nei);
                    visited[nei] =true;
                    dist[nei] =  dist[curr]+1;
                }
            }
        }
        // If we never reached endWord
        return 0;
    }
}
