package uk.co.whitbread.hotel.info.command;

import org.junit.jupiter.api.Test;
import org.springframework.data.redis.RedisConnectionFailureException;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CacheFallbackCommandTest {

    public static final String GROUP_KEY = "groupkey";
    public static final String COMMAND_KEY = "commandkey";
    public static final String THREAD_POOL_KEY = "threadPoolKey";

    @Test
    public void execute() {

        String result = CacheFallbackCommand.execute(GROUP_KEY, COMMAND_KEY, THREAD_POOL_KEY,
                () -> "SUCCESS",
                () -> "FALLBACK");

        assertThat("", result, equalTo("SUCCESS"));

    }


    @Test
    public void executeAndThrowRedisConnectionException() {

        String result = CacheFallbackCommand.execute(GROUP_KEY, COMMAND_KEY, THREAD_POOL_KEY,
                () -> {
                    throw new RedisConnectionFailureException("Some Connection Problem");
                },
                () -> "FALLBACK");

        assertThat("", result, equalTo("FALLBACK"));

    }


    @Test
    public void executeAndThrowRunTimeException_ShouldNotTriggerFallBackAndThrowOriginalException() {

        assertThrows(RuntimeException.class,
                () -> CacheFallbackCommand.execute(GROUP_KEY, COMMAND_KEY, THREAD_POOL_KEY, () -> {
                            throw new RuntimeException("Some Exception From run method");
                        },
                        () -> "FALLBACK"),
                "Some Exception From run method");

    }

    @Test
    public void executeAndThrowRunTimeExceptionFromFallBack_shouldThrowExceptionFromCallback() {

        assertThrows(RuntimeException.class,
                () -> CacheFallbackCommand.execute(GROUP_KEY, COMMAND_KEY, THREAD_POOL_KEY, () -> {
                            throw new RedisConnectionFailureException("Some Other Exception");
                        },
                        () -> {
                            throw new RuntimeException("Some Other Exception From fallback");
                        }),
                "Some Other Exception From fallback");
    }


}