package com.netsec;

public class test {
    public static void main(String arg[]){
        StringXORer xoRer = new StringXORer();
        String en = xoRer.encode("Hello","Java");
        System.out.println(en);
        String de = xoRer.decode(en,"Java");
        System.out.println(de);
    }

}
