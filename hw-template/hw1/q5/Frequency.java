package q5;

import java.util.*;
import java.util.concurrent.*;

public class Frequency  {
    public static ExecutorService executorService;

    public static int parallelFreq(int x, int[] A, int numThreads){
        //your implementation goes here, return -1 if the input is not valid.
        if (A == null || numThreads < 1) return -1;

        //initialize multiple callables
        executorService = Executors.newFixedThreadPool(numThreads);

        //convert array to list
        List<Integer> totalList = new ArrayList<>();
        for (int a : A) {
            totalList.add(a);
        }

        //split list into several pieces
        ArrayList<ArrayList<Integer>> subLists = new ArrayList<ArrayList<Integer>>();

        if (A.length <= numThreads) {
            for (int i = 0; i < numThreads; ++i) {
                if (i < A.length) {
                    ArrayList<Integer> cur = new ArrayList<>();
                    cur.add(A[i]);
                    subLists.add(cur);
                }
            }
        } else {
            int listSize = A.length / numThreads;
            int remain = A.length % numThreads;
            int begin = 0;
            int end = 0;
            for (int i = 0; i < numThreads; ++i ) {
                if (i < remain) {
                    end = begin + listSize + 1;
                } else {
                    end = begin + listSize;
                }
                List<Integer> cur = totalList.subList(begin, end);
                subLists.add(new ArrayList<Integer>(cur));
                begin = end;
            }

        }

        ArrayList<Future<Integer>> futures = new ArrayList<>();
        for (ArrayList<Integer> list : subLists) {
            Future<Integer> aList = executorService.submit(new Freq(x, list));
            futures.add(aList);
        }

        int result = 0;
        for (Future<Integer> future : futures) {
            try {
                result += future.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }
        executorService.shutdown();

        return result;
    }

    public static class Freq implements Callable<Integer> {
        int x;
        ArrayList<Integer> list;

        public Freq(int x, ArrayList<Integer> list) {
            this.x = x;
            this.list = list;
        }

        @Override
        public Integer call() throws Exception {
            Integer count = 0;
            for (int num : list) {
                if (num == x) count++;
            }
            return count;
        }
    }

//    public static void main(String[] args) {
//        int[] A = new int[]{1,1,3,2,5,1,4,6,7,8,5,3,2,1};
//        System.out.println(parallelFreq(1, A, 1));
//    }
}
