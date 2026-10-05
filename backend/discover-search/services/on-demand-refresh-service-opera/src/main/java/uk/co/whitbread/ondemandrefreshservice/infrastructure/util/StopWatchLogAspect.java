package uk.co.whitbread.ondemandrefreshservice.infrastructure.util;

import java.util.Objects;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Slf4j
public class StopWatchLogAspect {

  private static final String VOID = "void";

  @Around("@annotation(StopWatchLog)))")
  public Object log(ProceedingJoinPoint point) throws Throwable {
    long start;
    final Object result;
    if (!log.isTraceEnabled() && !log.isDebugEnabled()) {
      result = point.proceed();
      return result;
    }
    start = System.currentTimeMillis();
    result = point.proceed();
    long timeInMillis = System.currentTimeMillis() - start;
    final String argsMessage = getArgsMessage(point.getArgs());
    final String resultMessage = getResultMessage(point, result);
    final String message = String.format("StopWatch: signature=%s, time in millis=%s, args=%s, result=%s",
        point.getSignature(),
        timeInMillis,
        argsMessage,
        resultMessage);
    if (log.isTraceEnabled()) {
      log.trace(message);
    } else {
      log.debug(message);
    }
    return result;
  }

  private String getResultMessage(ProceedingJoinPoint point, Object result) {
    String resultMessage;
    if (result == null && log.isTraceEnabled()
        && point.getSignature().toString().startsWith(VOID)) {
      resultMessage = VOID;
    } else if (log.isTraceEnabled()) {
      resultMessage = Objects.toString(result);
    } else {
      resultMessage = "Use trace to see result";
    }
    return resultMessage;
  }

  private String getArgsMessage(Object[] args) {
    String argsString;
    if (args == null) {
      argsString = "no args";
    } else if (log.isTraceEnabled()) {
      argsString = ArrayUtils.toString(args);
    } else {
      argsString = "Use trace to see args";
    }
    return argsString;
  }
}
