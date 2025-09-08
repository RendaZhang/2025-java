package com.renda.leetcode.problems;

import com.renda.leetcode.core.LeetCodeProblem;
import java.util.ArrayDeque;

/**
 * LeetCode 200 - Number of Islands.
 */
public class LC200_NumberOfIslands implements LeetCodeProblem {

    public int numIslands(char[][] grid) {
        int m = grid.length, n = grid[0].length;
        int count = 0;

        // 四个方向：上、下、左、右
        int[][] directions = {{-1,0},{1,0},{0,-1},{0,1}};
        ArrayDeque<int[]> stack = new ArrayDeque<>();

        for (int i = 0; i < m; i++) {
            for (int j = 0; j < n; j++) {
                // 遇到陆地，进行 DFS
                if (grid[i][j] == '1') {
                    count++;
                    stack.push(new int[]{i, j});
                    grid[i][j] = '0'; // 标记为已访问
                    while (!stack.isEmpty()) {
                        int[] curr = stack.pop();
                        for (int[] dir : directions) {
                            int x = curr[0] + dir[0];
                            int y = curr[1] + dir[1];
                            if (x >= 0 && x < m && y >= 0 && y < n && grid[x][y] == '1') {
                                stack.push(new int[]{x, y});
                                grid[x][y] = '0'; // 标记为已访问
                            }
                        }
                    }
                }
            }
        }

        return count;
    }

    @Override
    public String problemNumber() {
        return "200";
    }

    @Override
    public void run() {
        char[][] grid = {{'1','1','0','0','0'},{'1','1','0','0','0'},{'0','0','1','0','0'},{'0','0','0','1','1'}};
        System.out.println("LC200 Number of Islands: " + numIslands(grid));
    }
}
