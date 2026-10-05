import { GraphQLClient } from 'graphql-request';
import getConfig from 'next/config';

let sharedGQLClient: GraphQLClient | null = null;

export function getGQLClient() {
  const { publicRuntimeConfig = {} } = getConfig() || {};

  const endpoint =
    process?.env?.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT === 'true' &&
    typeof window === 'undefined'
      ? process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT || ''
      : publicRuntimeConfig.NEXT_PUBLIC_GRAPHQL_ENDPOINT || '';

  const client = new GraphQLClient(endpoint, {});

  if (typeof window !== 'undefined') {
    // On the CLIENT SIDE we can cache the GQL client
    if (sharedGQLClient) {
      return sharedGQLClient;
    }
    sharedGQLClient = client;
  }
  // On the SERVER SIDE, we should NEVER cache the query client to ensure
  // we do not share data mistankenly between requests
  return client;
}
