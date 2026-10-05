import { RequestCache } from 'undici';

import { DEFAULT_TRACING_COOKIE_NAME } from '../../global-constants';
import { getCookie } from '../../gql/client';
import { logger } from '../../logger/logger';
import { getLocalhostHeadersAsync } from '../../utils/localhostHeaders';

// Dynamic import helper for next/headers to avoid bundling in client components
const getNextHeaders = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { headers } = await import('next/headers');
    return headers;
  } catch {
    return null;
  }
};

const getNextCookies = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { cookies } = await import('next/headers');
    return cookies;
  } catch {
    return null;
  }
};

export type GraphQLRequestLogContext = {
  operationName?: string;
  pageName?: string;
  userId?: string;
  companyId?: string;
};

const deriveOperationName = (query: string, providedName?: string) => {
  if (providedName) {
    return providedName;
  }

  const match = query.match(/\b(query|mutation)\s+([A-Za-z0-9_]+)/);
  return match?.[2] ?? 'anonymous';
};

const buildLogContext = (
  operationName: string,
  variables: Record<string, any>,
  logContext?: GraphQLRequestLogContext
) => ({
  operationName,
  variables,
  pageName: logContext?.pageName,
  userId: logContext?.userId,
  companyId: logContext?.companyId,
});

export const executeGraphQLQuery = async (
  query: string,
  variables: Record<string, any>,
  selector: (result: any) => any,
  token?: string,
  revalidateCache = false,
  returnErrors = false,
  headers: Record<string, string> = {},
  logContext?: GraphQLRequestLogContext,
  contentRequest = false
) => {
  const canLog = isServerSide();
  const operationName = deriveOperationName(query, logContext?.operationName);
  const startTime = performance.now();
  let success = false;
  const expirationTime = process?.env?.NEXT_APP_STATIC_CONTENT_CACHE_TTL ?? 0;

  const cacheOptions = revalidateCache
    ? { cache: 'no-cache' as RequestCache }
    : { next: { revalidate: Number(expirationTime) } };

  try {
    const headersObj = await getHeaders(token, headers, contentRequest);
    const isAppRouterEnv = await isAppRouter();

    let fetchResponse;
    let data;

    if (isServerSide() && isAppRouterEnv) {
      const { cachedFetch } = await import('./cachedFetch');

      fetchResponse = await cachedFetch(
        getEndpoint(),
        JSON.stringify(headersObj),
        JSON.stringify({ query, variables }),
        JSON.stringify(cacheOptions)
      );

      if (!fetchResponse) {
        return null;
      }

      data = fetchResponse;
    } else {
      fetchResponse = await fetch(getEndpoint(), {
        method: 'POST',
        headers: headersObj,
        body: JSON.stringify({
          query,
          variables,
        }),
        ...cacheOptions,
      });

      if (!fetchResponse.ok) {
        return null;
      }

      data = await fetchResponse.json();
    }

    if (!revalidateCache && data?.errors?.length > 0) {
      fetchResponse = await fetch(getEndpoint(), {
        method: 'POST',
        headers: headersObj,
        body: JSON.stringify({
          query,
          variables,
        }),
        next: { revalidate: 1 },
      });

      if (!fetchResponse.ok) {
        return null;
      }

      data = await fetchResponse.json();
    }

    if (canLog && data?.errors && data?.errors?.length > 0) {
      logger.error({
        label: 'GRAPHQL_QUERY_ERROR',
        ...buildLogContext(operationName, variables, logContext),
        errors: data.errors,
      });
    }

    if (!returnErrors && (!data?.data || data?.errors?.length > 0)) {
      return null;
    }

    const selected = selector(data);
    success = selected !== null;

    return selected;
  } catch (error) {
    if (canLog) {
      logger.error({
        label: 'GRAPHQL_QUERY_REQUEST_FAILED',
        err: error,
        ...buildLogContext(operationName, variables, logContext),
      });
    }
    return null;
  } finally {
    if (logContext?.pageName && canLog) {
      const durationMs = Math.round(performance.now() - startTime);
      const variablesForLog = operationName === 'getAccountList' ? variables : undefined;
      logger.info({
        label: 'PAGE_GRAPHQL_REQUEST',
        pageName: logContext.pageName,
        operationName,
        userId: logContext.userId,
        companyId: logContext.companyId,
        variables: variablesForLog,
        durationMs,
        success,
      });
    }
  }
};

export const executeContentGraphQLQuery = (
  query: string,
  variables: Record<string, any>,
  selector: (result: any) => any,
  logContext?: GraphQLRequestLogContext
) => executeGraphQLQuery(query, variables, selector, undefined, false, false, {}, logContext, true);

export const executeGraphQLMutation = async (
  mutation: string,
  variables: Record<string, any>,
  selector: (result: any) => any,
  token?: string,
  headers: Record<string, string> = {},
  logContext?: GraphQLRequestLogContext
) => {
  const canLog = isServerSide();
  const operationName = deriveOperationName(mutation, logContext?.operationName);
  try {
    const headersObj = await getHeaders(token, headers);

    const res = await fetch(getEndpoint(), {
      method: 'POST',
      headers: headersObj,
      body: JSON.stringify({
        query: mutation,
        variables,
      }),
      cache: 'no-cache',
    });

    if (!res.ok) {
      return null;
    }

    const result = await res.json();

    if (canLog && result?.errors && result?.errors?.length > 0) {
      logger.error({
        label: 'GRAPHQL_MUTATION_ERROR',
        ...buildLogContext(operationName, variables, logContext),
        errors: result.errors,
      });
    }

    return selector(result);
  } catch (error) {
    if (canLog) {
      logger.error({
        label: 'GRAPHQL_MUTATION_REQUEST_FAILED',
        err: error,
        ...buildLogContext(operationName, variables, logContext),
      });
    }
    return null;
  }
};

const isServerSide = () => typeof window === 'undefined';

const getEndpoint = () => {
  if (isServerSide() && process?.env?.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT === 'true') {
    return process?.env?.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT ?? '';
  }

  return process?.env?.NEXT_PUBLIC_GRAPHQL_ENDPOINT ?? '';
};

export const getHeaders = async (
  token?: string,
  headers: Record<string, string> = {},
  contentRequest?: boolean | undefined
) => {
  const localhostHeaders = await getLocalhostHeadersAsync(isServerSide());
  const sessionId = await getSessionId();
  const isAppRouterEnv = await isAppRouter();

  let forwardedHeaders = {};
  if (isServerSide() && isAppRouterEnv) {
    const nextHeadersFn = await getNextHeaders();
    if (nextHeadersFn) {
      const headersStore = await nextHeadersFn();
      forwardedHeaders = {
        'x-forwarded-for': headersStore.get('x-forwarded-for') ?? '',
        'True-Client-IP': headersStore.get('True-Client-IP') ?? '',
      };
    }
  }

  return {
    'Content-Type': 'application/json',
    'apollographql-client-name': process?.env?.NEXT_PUBLIC_APP_NAME ?? '',
    'apollographql-client-version': process?.env?.NEXT_PUBLIC_APOLLO_CLIENT_VERSION ?? '',
    ...localhostHeaders,
    ...forwardedHeaders,
    ...headers,
    ...(token && !contentRequest && { Authorization: `Bearer ${token}` }),
    ...(sessionId && !contentRequest && { [DEFAULT_TRACING_COOKIE_NAME]: sessionId }),
  };
};

const isAppRouter = async () => {
  if (typeof window !== 'undefined') return false;
  const nextHeadersFn = await getNextHeaders();
  if (!nextHeadersFn) return false;
  try {
    await nextHeadersFn();
    return true;
  } catch {
    return false;
  }
};

const getSessionId = async () => {
  const isAppRouterEnv = await isAppRouter();
  if (isServerSide() && isAppRouterEnv) {
    const nextCookiesFn = await getNextCookies();
    if (nextCookiesFn) {
      const cookieStore = await nextCookiesFn();
      return cookieStore.get(DEFAULT_TRACING_COOKIE_NAME)?.value;
    }
    return undefined;
  } else if (!isServerSide()) {
    return getCookie('WB-SESSION-ID')?.value;
  } else {
    return undefined;
  }
};
