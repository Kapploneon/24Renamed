/*
 * Copyright (c) 2018. Lorem ipsum dolor sit amet, consectetur adipiscing elit.
 * Morbi non lorem porttitor neque feugiat blandit. Ut vitae ipsum eget quam lacinia accumsan.
 * Etiam sed turpis ac ipsum condimentum fringilla. Maecenas magna.
 * Proin dapibus sapien vel ante. Aliquam erat volutpat. Pellentesque sagittis ligula eget metus.
 * Vestibulum commodo. Ut rhoncus gravida arcu.
 */

package com.Algo.LeetCode;

class Solution {

    private ListNode addTwoNumbers(ListNode l1, ListNode l2) {
        int xInt = 0;
        int carry = 0;
        ListNode result = new ListNode(0);
        ListNode head = result;
        int mult;
        boolean x = true;

        do{

            xInt = l1.val + l2.val + carry;
            if(xInt > 9){
                result.val = xInt % 10;
                carry = 1;
            }else{
                result.val = xInt % 10;
                carry = 0;
            }

            if(l1.next != null && l2.next != null){
                result.next = new ListNode(0);
                result = result.next;
            }

            if(l1.next != null){
                l1 = l1.next;
            }
            else{
                x = false;
                l1 = new ListNode(0);
            }

            if(l2.next != null){
                l2 = l2.next;
                x = true;
            }
            else{
                l2 = new ListNode(0);
            }

            if(carry == 1)
                x = true;

        }while(x);

        return head;
    }

        private int getNumber(ListNode l1) {
            int x = 0;
            if (l1 != null) {
                int multi = 1;
                do {
                    x = x + l1.val * multi;
                    multi = multi * 10;
                    l1 = l1.next;
                } while (l1 != null);

                if(l1.next != null)
                    l1 = l1.next;
                else
                    l1 = l1;
            }
            return x;
        }

        private ListNode convertNumber(int x){
            ListNode result = new ListNode(x);
            ListNode nex;

            while (x > 0){
                result.val = x % 10;
                x = x / 10;
                if(x > 0) {
                    nex = new ListNode(x);
                    result.next = nex;
                    result = nex;
                }
            }

            return result;

        }

    public static void main(String arg[]){
            Solution x = new Solution();
            ListNode l1 = new ListNode(9);

            ListNode l2 = new ListNode(1);
            l2.next = new ListNode(9);
            l2.next.next = new ListNode(9);
            l2.next.next.next = new ListNode(9);
            l2.next.next.next.next = new ListNode(9);
            l2.next.next.next.next.next = new ListNode(9);
            l2.next.next.next.next.next.next = new ListNode(9);
            l2.next.next.next.next.next.next.next = new ListNode(9);
            l2.next.next.next.next.next.next.next.next = new ListNode(9);
            l2.next.next.next.next.next.next.next.next.next = new ListNode(9);

            ListNode result;

            result = x.addTwoNumbers(l1,l2);

            while (result.next != null) {
                System.out.println(result.val);
                result = result.next;
            }
            System.out.println(result.val);

    }
}

class ListNode {
    int val;
    ListNode next;

    ListNode(int x){
        val = x;
    }
}
