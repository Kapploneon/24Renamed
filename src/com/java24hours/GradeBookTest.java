package com.java24hours;

public class GradeBookTest {

    public static void main(String[] args)
    {
        GradeBook myGradeBook1 = new GradeBook("class 1");
        GradeBook myGradeBook2 = new GradeBook("class 2");
        myGradeBook1.displayMessage();
        myGradeBook2.displayMessage();

        System.out.printf("gradeBook1 course name is: %s\n",myGradeBook1.getCourseName());
        System.out.printf("gradeBook2 course name is: %s\n",myGradeBook2.getCourseName());

        int counter1 = 0;

        int[] c = new int[12];

        while (++counter1 <= 10) {
            System.out.printf("%d ", counter1);
//            ++counter;
        }

        System.out.println();


        for(int counter = 1; counter <= 10; ++counter)
        {
            System.out.printf("%d ", counter);
        }

        System.out.println();

        System.out.printf("%s%40s\n ", "Year", "Amount on deposit");

        for(int counter = 1; counter <= 10; ++counter)
        {
            System.out.printf("%4d%40d\n", counter, counter);
        }

        System.out.println("hahahah "+Math.sqrt(900.00));

    }

}
