'use client';

/* eslint-disable @typescript-eslint/no-explicit-any */
import { queryOptions, useMutation, UseMutationResult, useQuery } from '@tanstack/react-query';
import axios, { type RawAxiosRequestHeaders, AxiosResponse } from 'axios';
import { getCookie } from 'cookies-next';
import { randomBytes } from 'crypto';
import { GraphQLClient } from 'graphql-request';
import getConfig from 'next/config';

import { getGQLClient } from '../gql/client';
import { getClientDefaultSessionTracing, logger } from '../logger/logger';

interface Options {
  [key: string]: any;
  idToken?: string;
  proxyOptions?: ProxyOptions;
}

interface ErrorDetails {
  errorMessage: string;
  params: { [key: string]: any };
  endpoint: string;
  host?: string;
}

export interface ProxyOptions {
  useProxyAPI?: boolean;
  host?: string;
  cookie?: string;
  accessToken?: string;
  basketIds?: string; // JSON string of encoded basket IDs array from cookie
}

interface GraphQLEndpointConfig {
  serverEnvVar: string;
  serverConfigKey?: string;
  clientConfigKey: string;
  defaultUrl?: string;
}

interface GraphQLRequestConfig {
  endpointConfig: GraphQLEndpointConfig;
  enableErrorHandling?: boolean;
  customHeadersBuilder?: (baseHeaders: Record<string, string>) => Record<string, any>;
}

/**
 * Build base Apollo GraphQL headers
 */
function buildApolloHeaders(): Record<string, string> {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  return {
    'apollographql-client-name': publicRuntimeConfig.NEXT_PUBLIC_APP_NAME ?? '',
    'apollographql-client-version': publicRuntimeConfig.NEXT_PUBLIC_APOLLO_CLIENT_VERSION ?? '',
    Referer: `http://localhost:3000/`,
    'X-Dev-Nonce': randomBytes(16).toString('base64'),
  };
}

/**
 * Get GraphQL endpoint URL with proxy support
 */
function getGraphQLEndpoint(config: GraphQLEndpointConfig): string {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const useProxyAPICookie = getCookie('useProxyAPI');
  const hostURLCookie = String(getCookie('hostURL'));

  // Check if proxy API should be used
  if (useProxyAPICookie === true && hostURLCookie === document?.location?.origin) {
    return `${document?.location?.origin ?? ''}/api/graphql`;
  }

  // Determine URL based on environment (server-side vs client-side)
  const isServerSide = typeof window === 'undefined';
  const useServerEndpoint = process?.env?.[config.serverEnvVar] === 'true' && isServerSide;

  if (useServerEndpoint) {
    return process.env[config.serverConfigKey ?? config.clientConfigKey] ?? '';
  }

  return publicRuntimeConfig[config.clientConfigKey] || config.defaultUrl || '';
}

async function executeGraphQLRequest<TOptions = any>(
  gqlTemplateString: string,
  options: TOptions | undefined,
  config: GraphQLRequestConfig,
  gqlClient?: GraphQLClient
) {
  const { publicRuntimeConfig = {} } = getConfig() || {};

  // Build headers
  const baseHeaders = buildApolloHeaders();
  const requestHeaders = config.customHeadersBuilder
    ? config.customHeadersBuilder(baseHeaders)
    : baseHeaders;

  // Get endpoint
  const url = getGraphQLEndpoint(config.endpointConfig);

  // Execute request
  const client = gqlClient ?? getGQLClient();
  client.setEndpoint(url);

  if (config.enableErrorHandling) {
    const errorDetails: ErrorDetails = {
      errorMessage: '',
      params: {},
      endpoint: publicRuntimeConfig[config.endpointConfig.clientConfigKey],
      host: undefined,
    };

    try {
      return await client.request(gqlTemplateString, options, requestHeaders);
    } catch (e: any) {
      errorDetails.errorMessage = e.response;
      errorDetails.params = e.request?.variables;
      console.error(JSON.stringify(errorDetails));
      // Re-throw to maintain error flow
      throw e;
    }
  } else {
    return await client.request(gqlTemplateString, options, requestHeaders);
  }
}

export async function graphQLRequest(
  gqlTemplateString: string,
  options?: Options,
  accessToken?: string,
  proxyOptions?: ProxyOptions,
  gqlClient?: GraphQLClient
) {
  return executeGraphQLRequest(
    gqlTemplateString,
    options,
    {
      endpointConfig: {
        serverEnvVar: 'NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT',
        serverConfigKey: 'NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT',
        clientConfigKey: 'NEXT_PUBLIC_GRAPHQL_ENDPOINT',
        defaultUrl: 'http://localhost:3000/api/graphql',
      },
      enableErrorHandling: true,
      customHeadersBuilder: (baseHeaders) => {
        const headers: Record<string, any> = { ...baseHeaders };

        // Add authorization header
        const accessTokenAuthorization = accessToken ?? proxyOptions?.accessToken;
        if (accessTokenAuthorization) {
          headers.Authorization = `Bearer ${accessTokenAuthorization}`;
        }
        if (options?.idToken) {
          headers.Authorization = `Bearer ${options.idToken}`;
          delete options.idToken;
        }

        // Add custom wb-basket-id header
        if (proxyOptions?.basketIds) {
          headers['wb-basket-id'] = proxyOptions.basketIds;
        }

        return headers;
      },
    },
    gqlClient
  );
}

export function useQueryRequest(
  queryKey: string | any[],
  gqlTemplateString: string,
  options?: Options,
  reactQueryOptions?: any,
  accessToken?: string,
  returnError = false,
  proxyOptions?: ProxyOptions
) {
  const qOptions = queryOptions({
    queryKey,
    queryFn: () => graphQLRequest(gqlTemplateString, options, accessToken, proxyOptions),
    ...reactQueryOptions,
  });
  const { isLoading, isError, isSuccess, isFetching, data, error, refetch } = useQuery(qOptions);

  if (isError) {
    const sessionTracing = getClientDefaultSessionTracing();

    logger.error({
      label: 'QUERY_REQUEST_ERROR',
      err: error,
      msg: {
        queryKey,
        accessToken,
        ...sessionTracing,
      },
    });
  }

  if (!returnError && isError) {
    throw error;
  }

  return {
    isLoading,
    isError,
    isSuccess,
    isFetching,
    data: data as any,
    error,
    refetch,
  };
}

export function useMutationRequest(
  gqlTemplateString: string,
  returnError = false,
  accessToken?: string,
  reactQueryOptions?: any
) {
  const mutation = useMutation({
    mutationFn: (options?: Options) => graphQLRequest(gqlTemplateString, options, accessToken),
    ...reactQueryOptions,
  });

  if (mutation.error) {
    const sessionTracing = getClientDefaultSessionTracing();

    logger.error({
      label: 'MUTATION_REQUEST_ERROR',
      err: mutation.error,
      msg: {
        accessToken,
        ...sessionTracing,
      },
    });
  }

  if (!returnError && mutation.isError) {
    throw mutation.error;
  }

  return {
    mutation: mutation as UseMutationResult<any, Error, any, unknown>,
    isLoading: mutation.isPending,
    isError: mutation.isError,
    isSuccess: mutation.isSuccess,
    isIdle: mutation.isIdle,
    data: mutation.data as any,
    error: mutation.error,
  };
}

interface AxiosRequest {
  method: string;
  url: string;
  data?: any;
  params?: any;
  headers?: RawAxiosRequestHeaders;
}

export async function axiosRequest({ method, url, data, params, headers }: AxiosRequest) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  return axios({
    method,
    url,
    params,
    data,
    headers: {
      Origin: publicRuntimeConfig.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC,
      ...headers,
    },
  }).then((response: AxiosResponse<any, any>) => response.data);
}

export function useRestQueryRequest(
  queryKey: string | any[],
  method: string,
  url: string,
  headers?: RawAxiosRequestHeaders,
  reactQueryOptions?: any,
  body?: any
) {
  const qOptions = queryOptions({
    queryKey,
    queryFn: () => axiosRequest({ method, url, headers, data: body }),
    ...reactQueryOptions,
  });
  const { isLoading, isError, isSuccess, isFetching, data, error, refetch } = useQuery(qOptions);

  if (isError) {
    const sessionTracing = getClientDefaultSessionTracing();

    logger.error({
      label: 'REST_QUERY_REQUEST_ERROR',
      err: error,
      msg: {
        queryKey,
        url,
        ...sessionTracing,
      },
    });
  }

  return {
    isLoading,
    isError,
    isSuccess,
    isFetching,
    data: data as any,
    error,
    refetch,
  };
}

export function useRestMutationRequest(
  url: string,
  method: string,
  headers?: RawAxiosRequestHeaders
) {
  const mutation = useMutation({
    mutationFn: (data?: Options) => axiosRequest({ method, url, data, headers }),
  });

  if (mutation.error) {
    const sessionTracing = getClientDefaultSessionTracing();

    logger.error({
      label: 'REST_MUTATION_REQUEST_ERROR',
      err: mutation.error,
      msg: {
        url,
        ...sessionTracing,
      },
    });
  }

  return {
    mutation,
    isLoading: mutation.isPending,
    isError: mutation.isError,
    isSuccess: mutation.isSuccess,
    isIdle: mutation.isIdle,
    data: mutation.data as any,
    error: mutation.error,
    status: mutation.status,
  };
}

// Restaurants-specific request functions
type RestaurantsGenericFunction = (...args: any[]) => any;

interface RestaurantsOptions {
  [key: string]: string | number | boolean | RestaurantsGenericFunction | undefined;
}

export async function graphQLRequestRestaurants(
  gqlTemplateString: string,
  options?: RestaurantsOptions,
  _accessToken?: string | undefined,
  _proxyOptions?: ProxyOptions | undefined,
  gqlClient?: GraphQLClient
) {
  return executeGraphQLRequest(
    gqlTemplateString,
    options,
    {
      endpointConfig: {
        serverEnvVar: 'NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT',
        serverConfigKey: 'NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT',
        clientConfigKey: 'NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT',
      },
      enableErrorHandling: false,
      // No custom headers needed - just use base Apollo headers
    },
    gqlClient
  );
}

export function useQueryRequestRestaurants(
  queryKey: string | any[],
  gqlTemplateString: string,
  options?: RestaurantsOptions,
  accessToken?: string,
  proxyOptions?: ProxyOptions,
  reactQueryOptions?: any
) {
  const qOptions = queryOptions({
    queryKey,
    queryFn: () => graphQLRequestRestaurants(gqlTemplateString, options, accessToken, proxyOptions),
    ...reactQueryOptions,
  });
  const { isLoading, isError, isSuccess, isFetching, data, error, refetch } = useQuery(qOptions);

  if (isError) {
    const sessionTracing = getClientDefaultSessionTracing();

    logger.error({
      label: 'RESTAURANTS_QUERY_REQUEST_ERROR',
      err: error,
      msg: {
        queryKey,
        accessToken,
        ...sessionTracing,
      },
    });
  }

  return {
    isLoading,
    isError,
    isSuccess,
    isFetching,
    data,
    error,
    refetch,
  };
}

export function useMutationRequestRestaurants(
  gqlTemplateString: string,
  options?: RestaurantsOptions,
  reactQueryOptions?: any
) {
  const mutation = useMutation({
    mutationFn: (mutationOptions?: RestaurantsOptions) =>
      graphQLRequestRestaurants(gqlTemplateString, mutationOptions),
    ...reactQueryOptions,
  });

  return {
    mutation,
    isLoading: mutation.isPending,
    isError: mutation.isError,
    isSuccess: mutation.isSuccess,
    isIdle: mutation.isIdle,
    data: mutation.data,
    error: mutation.error,
  };
}
