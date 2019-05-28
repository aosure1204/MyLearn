

import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;

public class FutureTest{

    private static final Callable<Integer> callable = new Callable<Integer>() {
        @Override
        public Integer call() throws Exception {
            int result = 0;
            for(int i = 0; i < 10; i++) {
                try{
                    result = i;
                    System.out.println("workThread i = " + i);
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return result;
        }
    };

    public static void main(String[] args) {
        FutureTask<Integer> futureTask = new FutureTask<Integer>(callable);
        new Thread(futureTask).start();

        try {
            int result = (int) futureTask.get(5, TimeUnit.SECONDS);
            System.out.println("mainThread result = " + result);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}