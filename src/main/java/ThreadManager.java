import java.util.Map;
import java.util.concurrent.*;

public class ThreadManager {

    public static <T> Future<T> executeThread(T data, Callable<T> ref) throws RejectedExecutionException,NullPointerException{
        Callable<T> c = ref;
        Future<T> holder = null;
        try (ExecutorService executorService = Executors.newFixedThreadPool(10)) {
            holder = executorService.submit(c);
            return holder;
        }catch (RejectedExecutionException | NullPointerException e){
            if (e instanceof RejectedExecutionException){
                throw new RejectedExecutionException(e.getMessage());
            }else{
                throw new NullPointerException(e.getMessage());
            }
        }
    }

}
