import { QueryClient } from '@tanstack/react-query';
import pino from 'pino';

import {
  tracingCookie,
  getDefaultSessionTracing,
  getClientDefaultSessionTracing,
} from '../utils/tracing';

const getLogger = () => {
  const logger = pino({
    level: process.env.NEXT_PUBLIC_LOG_LEVEL || 'info',
  });

  return logger;
};

const defaultLogger = getLogger();
// eslint-disable-next-line @typescript-eslint/no-unsafe-function-type, @typescript-eslint/no-explicit-any
const instrument = ({ label, ...msg }: any, fn: Function) => {
  return async (...rest: any[]) => {
    defaultLogger.info({
      label: `${label}_START`,
      msg,
    });
    const resp = await fn(...rest);
    defaultLogger.info({
      label: `${label}_END`,
      msg,
    });

    return resp;
  };
};

const instrumentQueryClient = (queryClient: QueryClient, msg: Record<string, any> = {}) => {
  function prefetchQuery(...args: any[]) {
    const fn = instrument(
      {
        label: 'PREFETCH_QUERY',
        queryKey: args[0],
        ...msg,
      },
      queryClient.prefetchQuery.bind(queryClient)
    );

    return fn({ queryKey: args[0], queryFn: args[1] });
  }

  function fetchQuery(...args: any[]) {
    const fn = instrument(
      {
        label: 'FETCH_QUERY',
        queryKey: args[0],
        ...msg,
      },
      queryClient.fetchQuery.bind(queryClient)
    );

    return fn({ queryKey: args[0], queryFn: args[1] });
  }
  return {
    prefetchQuery: prefetchQuery.bind(queryClient),
    fetchQuery: fetchQuery.bind(queryClient),
  };
};

export {
  getLogger,
  instrument,
  instrumentQueryClient,
  tracingCookie,
  getDefaultSessionTracing,
  getClientDefaultSessionTracing,
  defaultLogger as logger,
};
