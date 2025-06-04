package com.renda.leetcode;

public class LC79_WordSearch {

    public boolean exist(char[][] board, String word) {
        int rows = board.length, cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (dfs(board, i, j, word.toCharArray(), 0 , visited)) return true;
            }
        }
        return false;
    }

    private boolean dfs(char[][] board, int i, int j, char[] array, int k, boolean[][] visited) {
        if (k == array.length) return true;
        if (i < 0) return false;
        if (j < 0) return false;
        if (i >= board.length) return false;
        if (j >= board[i].length) return false;
        if (visited[i][j]) return false;
        if (board[i][j] != array[k]) return false;

        visited[i][j] = true;
        boolean result = dfs(board, i+1, j, array, k+1, visited)
                || dfs(board, i, j+1, array, k+1, visited)
                || dfs(board, i-1, j, array, k+1, visited)
                || dfs(board, i, j-1, array, k+1, visited);
        visited[i][j] = false;
        return result;
    }

}

