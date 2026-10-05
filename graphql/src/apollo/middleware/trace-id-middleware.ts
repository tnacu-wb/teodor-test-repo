import { v4 as uuidv4 } from 'uuid';
import { getAsyncLocalStorage } from '../log/logger';

// @ts-ignore
export const traceIdMiddleware = (req: any, res: any, next: NextFunction) => {
  const asyncLocalStorage = getAsyncLocalStorage();

  let xAmznTraceId;
  if (!req.headers['x-amzn-trace-id']) {
    xAmznTraceId = uuidv4();
    req.headers['x-amzn-trace-id'] = xAmznTraceId;
  } else {
    xAmznTraceId = req.headers['x-amzn-trace-id'];
  }

  // Setting in the response.
  res.setHeader('x-amzn-trace-id', xAmznTraceId);

  const traceparent = req.headers['traceparent'];
  let traceId = '';

  if (traceparent) {
    const parts = traceparent.split('-');
    if (parts.length === 4) {
      const trace_id = parts[1];
      const span_id = parts[2];
      if (trace_id && span_id) {
        traceId = `[${trace_id},${span_id}] x-amzn-trace-id: ${xAmznTraceId}`;
      }
    }
  }

  if (!traceId || traceId.trim() === '') {
    traceId = `x-amzn-trace-id: ${xAmznTraceId}`;
  }

  asyncLocalStorage.run({ traceId }, () => {
    next();
  });
};
