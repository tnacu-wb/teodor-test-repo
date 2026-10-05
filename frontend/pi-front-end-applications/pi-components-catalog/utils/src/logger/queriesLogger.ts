import { FetchQueryOptions, QueryClient } from '@tanstack/react-query';
import Cookies from 'cookies';
import { ParsedUrlQuery } from 'querystring';

import { getLoggedInUserInfo, ID_TOKEN_COOKIE } from '../getters';
import { getDefaultSessionTracing, logger } from './logger';

export interface Query {
  queryKey: string[];
  executionTime: number;
  error: string;
}

export interface LoggingObject {
  label: string;
  pageLink: string | undefined;
  params: ParsedUrlQuery | undefined;
  idTokenCookie: string | undefined;
  ccuiSession?: string;
  sessionTracing: {
    'WB-SESSION-ID': string;
  };
}

export default class QueriesLogger {
  queries: Query[] = [];
  queryClient: QueryClient;
  prefetchQuery: (...args: any[]) => Promise<any>;
  fetchQuery: (...args: any[]) => Promise<any>;
  loggingObject: LoggingObject;
  req: any;
  res: any;
  query: ParsedUrlQuery;
  label: string;
  ccuiSession?: string;
  logQueries: (endExecutionTime: number) => void;

  constructor(
    queryClient: QueryClient,
    req: any,
    res: any,
    query: ParsedUrlQuery,
    label: string,
    ccuiSession?: string
  ) {
    const cookies = new Cookies(req, res);
    const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
    const sessionTracing = getDefaultSessionTracing(cookies);

    this.queryClient = queryClient;
    this.query = query;
    this.label = label;
    this.loggingObject = {
      label: label,
      pageLink: req.url,
      params: query,
      idTokenCookie,
      ccuiSession,
      sessionTracing,
    };

    const startExecutionTime = performance.now();
    this.prefetchQuery = (...args: any[]) => {
      const fn = this.instrument(queryClient.prefetchQuery.bind(queryClient));

      return fn({ queryKey: args[0], queryFn: args[1] });
    };

    this.fetchQuery = (...args: any[]) => {
      const fn = this.instrument(queryClient.fetchQuery.bind(queryClient));

      return fn({ queryKey: args[0], queryFn: args[1] });
    };

    this.logQueries = (endExecutionTime: number) => {
      const { label, pageLink, params, idTokenCookie, sessionTracing } = this.loggingObject;

      const {
        name,
        profile: { sessionId, isBusiness, accessLevel, employeeId, companyId },
        cdhCompanyId,
        cdhEmployeeId,
        operaCompanyId,
      } = getLoggedInUserInfo(idTokenCookie ?? '');

      const userInfo = {
        name: this.loggingObject?.ccuiSession ?? name,
        sessionId,
        isBusiness,
        accessLevel,
        employeeId,
        companyId,
        cdhCompanyId,
        cdhEmployeeId,
        operaCompanyId,
      };

      return logger.info({
        label,
        pageLink,
        params,
        userInfo,
        totalExecutionTime: endExecutionTime - startExecutionTime,
        queries: this.queries,
        ...sessionTracing,
      });
    };
  }

  instrument(fn: (options: FetchQueryOptions) => void) {
    return async (options: any) => {
      const start = performance.now();
      const queryDetails = {
        executionTime: 0,
        error: '',
      };

      try {
        return fn(options);
      } catch (err) {
        queryDetails.error = err as string;
      } finally {
        queryDetails.executionTime = performance.now() - start;
        this.queries.push({ queryKey: options.queryKey, ...queryDetails });
      }
    };
  }
}
