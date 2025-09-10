package com.renda.leetcode.util;

/**
 * 链表辅助工具类，提供构建链表等常用方法。
 */
public class ListUtils {

    /**
     * 根据数组构建链表。
     * @param arr 输入数组
     * @return 链表头节点
     */
  public static ListNode buildList(int[] arr) {
    if (arr.length == 0) return null;
    ListNode head = new ListNode(arr[0]);
    ListNode current = head;
    for (int i = 1; i < arr.length; i++) {
      current.next = new ListNode(arr[i]);
      current = current.next;
    }
    return head;
  }

  /**
   * 根据数组和位置构建带环链表。
   * @param arr 输入数组
   * @param pos 环的位置，-1 表示无环
   * @return 链表头节点
   */
  public static ListNode buildList(int[] arr, int pos) {
      if (arr.length == 0) return null;
      ListNode head = new ListNode(arr[0]);
      ListNode current = head;
      ListNode cycleEntry = null;
      if (pos == 0) {
          cycleEntry = head;
      }
      for (int i = 1; i < arr.length; i++) {
          current.next = new ListNode(arr[i]);
          current = current.next;
          if (i == pos) {
              cycleEntry = current;
          }
      }
      if (pos != -1) {
          current.next = cycleEntry;
      }
      return head;
  }

}
