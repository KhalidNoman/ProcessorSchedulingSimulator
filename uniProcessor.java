import java.util.*;
import java.io.*;


public class uniProcessor {

    static class element{
        int arrival;
        int priority;
        int burst;

        public element(int arrival, int priority, int burst){
            this.arrival = arrival;
            this.priority = priority;
            this.burst = burst;
        }

        public int getArrival(){
            return arrival;
        }
        public int getPriority(){
            return priority;
        }
        public int getBurst(){
            return burst;
        }
        public void setBurst(int burst){ this.burst = burst; }

        @Override
        public String toString() {
            return arrival + " " + priority + " " + burst;
        }
    }

    public static void main(String[] args) throws Exception{

        Scanner sc = new Scanner(new File("C:\\Users\\Khalid\\Desktop\\input.txt"));
        int size = sc.nextInt();

        PriorityQueue<element> pq = new PriorityQueue<element>(size,
                (element1, element2) -> Integer.compare(element1.getPriority(), element2.getPriority()));

        if(size > 100)
            System.out.println("Too large");
        else {
            for(int i = 0; i < size; i++){
                int arr = sc.nextInt(), prt = sc.nextInt(), brst = sc.nextInt();
                element temp = new element(arr, prt, brst);
                pq.add(temp);
            }

            while(!pq.isEmpty())
                System.out.println(pq.remove());
        }
    }

}
