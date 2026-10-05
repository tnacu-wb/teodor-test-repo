import { GraphQLClient } from 'graphql-request';
import getConfig from 'next/config';

import { WB_SESSION_ID } from '../global-constants';
import { getNodeFetch } from './nodeFetch';

let cachedBrowserGqlClient: GraphQLClient | null = null;

const getGQLClientBrowser = () => {
  if (typeof window === 'undefined') {
    return null;
  }
  if (!cachedBrowserGqlClient) {
    // Cache the client
    const { publicRuntimeConfig = {} } = getConfig() || {};
    cachedBrowserGqlClient = new GraphQLClient(
      publicRuntimeConfig.NEXT_PUBLIC_GRAPHQL_ENDPOINT || '',
      {}
    );

    // Attach sessionId to the client
    const sessionIdCookie = getCookie('WB-SESSION-ID');
    if (sessionIdCookie) {
      cachedBrowserGqlClient.setHeader('WB-SESSION-ID', sessionIdCookie.value);
    }
  }

  return cachedBrowserGqlClient;
};

const getGQLClientServer = (sessionId?: string) => {
  // Rewire fetch to use node-fetch with Custom Agent
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const endpoint =
    process?.env?.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT === 'true'
      ? process?.env?.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT
      : publicRuntimeConfig.NEXT_PUBLIC_GRAPHQL_ENDPOINT || '';
  const headers: Record<string, string> = {};
  if (sessionId) {
    headers[WB_SESSION_ID] = sessionId;
  }

  return new GraphQLClient(endpoint, {
    fetch: getNodeFetch() as any,
    headers,
  });
};

export function getGQLClient(sessionId?: string): any {
  return typeof window !== 'undefined' ? getGQLClientBrowser() : getGQLClientServer(sessionId);
}

export function getCookie(name: string) {
  const allCookies = document.cookie
    .split(';')
    .map((cookie) => {
      if (!cookie) {
        return null;
      }
      const [name, value] = cookie.split('=');
      return { name: name.trim(), value: value.trim() };
    })
    .filter((c) => c !== null);
  const sessionIdCookie = allCookies.find((c) => c?.name === name);
  return sessionIdCookie;
}
