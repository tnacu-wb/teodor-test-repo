package uk.co.whitbread.hotel.info.command;


import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import com.netflix.hystrix.exception.HystrixBadRequestException;
import com.netflix.hystrix.exception.HystrixRuntimeException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;

import java.util.Optional;
import java.util.function.Supplier;

@Slf4j
public  class CacheFallbackCommand<T> extends HystrixCommand<T> {

    private Supplier<T> cached;
    private Supplier<T> notCached;

    private CacheFallbackCommand(String groupKey, String commandkey, String threadPoolKey, Supplier<T> cached, Supplier<T> notcached) {

        super(Setter
                .withGroupKey(HystrixCommandGroupKey.Factory.asKey(groupKey))
                .andCommandKey(HystrixCommandKey.Factory.asKey(commandkey))
                .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey(threadPoolKey)))
        ;

        this.cached = cached;
        this.notCached = notcached;

    }

    public static <T> T execute(String groupKey, String commandkey, String threadPoolKey, Supplier<T> cached, Supplier<T> notcached) {

        try {

            return new CacheFallbackCommand<>(groupKey, commandkey, threadPoolKey, cached, notcached).execute();
        } catch (RuntimeException caughtException) {

            return handleException(caughtException);
        }


    }

    private static <T> T handleException(RuntimeException caughtException) {
        if (HystrixBadRequestException.class.isAssignableFrom(caughtException.getClass())) {

            // Throw the original cause of the exception
            throw (RuntimeException) caughtException.getCause();
        } else if (HystrixRuntimeException.class.isAssignableFrom(caughtException.getClass())) {


            HystrixRuntimeException hystrixRuntimeException = (HystrixRuntimeException) caughtException;

            Optional<Throwable> fallbackException = Optional.ofNullable(hystrixRuntimeException.getFallbackException());
            if (fallbackException.isPresent()) {
                // Throw the exception from fallback
                throw (RuntimeException) fallbackException.get();
            } else {
                // throw original exception
                throw caughtException;
            }

        } else {

            throw caughtException;
        }
    }

    @Override
    protected T run() {

        try {
            return cached.get();
        } catch (RuntimeException t) {

            // Only circuit break on Data Access Exceptions
            if (DataAccessException.class.isAssignableFrom(t.getClass())) {
                log.info(String.format("Cached method failed with %s. Try fall back.", t.getMessage()));
                throw t;
            } else {
                // Don't open the circuit breaker
                throw new HystrixBadRequestException(t.getMessage(), t);
            }
        }
    }

    @Override
    protected T getFallback() {

        log.info(String.format("Cache fallback %s called. Root Exception %s ", this.getFallbackMethodName(), this.getExecutionException()));
        if (this.isResponseTimedOut()) {
            return null;
        }
        return notCached.get();

    }
}
