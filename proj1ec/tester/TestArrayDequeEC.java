package tester;

import static org.junit.Assert.*;
import org.junit.Test;
import student.StudentArrayDeque;
import edu.princeton.cs.algs4.StdRandom;


public class TestArrayDequeEC {

    @Test
    public void randomDequeTest(){
        ArrayDequeSolution<Integer> correct = new ArrayDequeSolution<>();
        StudentArrayDeque<Integer> broken = new StudentArrayDeque<>();

        int N = 1000;
        String msg = "";
        for (int i=0; i<N; i+=1){
            int operationNumber = StdRandom.uniform(0, 4);
            if (operationNumber == 0){
                int randVal = StdRandom.uniform(0, 100);
                correct.addFirst(randVal);
                broken.addFirst(randVal);
                msg = msg + "addFirst(" + randVal + ")\n";
            }else if(operationNumber == 1){
                int randVal = StdRandom.uniform(0, 100);
                correct.addLast(randVal);
                broken.addLast(randVal);
                msg = msg + "addLast(" + randVal + ")\n";
            }else if(operationNumber == 2){
                if(correct.size()==0 || broken.size()==0){
                    continue;
                }
                msg = msg + "removeFirst()\n";
                assertEquals(msg, correct.removeFirst(), broken.removeFirst());
            }else if(operationNumber == 3){
                if(correct.size()==0 || broken.size()==0){
                    continue;
                }
                msg = msg + "removeLast()\n";
                assertEquals(msg, correct.removeLast(), broken.removeLast());
            }
        }
    }
}
