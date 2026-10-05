const mockPublicRuntimeConfig = {
  NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://client.graphql.endpoint',
};

const mockGraphQLClient = jest.fn().mockImplementation((endpoint: string) => ({
  endpoint,
  request: jest.fn(),
}));

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: mockPublicRuntimeConfig,
}));

jest.mock('graphql-request', () => ({
  GraphQLClient: mockGraphQLClient,
}));

describe('getGQLClient', () => {
  const originalWindow = global.window;
  const originalEnv = process.env;

  beforeEach(() => {
    mockGraphQLClient.mockClear();
    // Reset environment variables
    process.env = { ...originalEnv };
    // Reset modules to clear the shared client cache
    jest.resetModules();
    // Re-setup mocks
    jest.doMock('next/config', () => () => ({
      publicRuntimeConfig: mockPublicRuntimeConfig,
    }));
    jest.doMock('graphql-request', () => ({
      GraphQLClient: mockGraphQLClient,
    }));
  });

  afterEach(() => {
    process.env = originalEnv;
    global.window = originalWindow;
  });

  describe('Client-side behavior', () => {
    beforeEach(() => {
      // Ensure window is defined for client-side tests
      if (typeof global.window === 'undefined') {
        // eslint-disable-next-line @typescript-eslint/ban-ts-comment
        // @ts-ignore
        global.window = {} as Window & typeof globalThis;
      }
    });

    it('should create a GraphQL client with the public runtime config endpoint', () => {
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');
      const client = getClient();

      expect(mockGraphQLClient).toHaveBeenCalledWith('https://client.graphql.endpoint', {});
      expect(client).toBeDefined();
    });

    it('should cache the client on subsequent calls', () => {
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      const client1 = getClient();
      const client2 = getClient();

      // The same client instance should be returned
      expect(client1).toBe(client2);
      // Note: The GraphQLClient constructor is still called each time,
      // but the cached instance is returned instead of the new one
      expect(mockGraphQLClient).toHaveBeenCalledTimes(2);
    });
  });

  describe('Server-side behavior', () => {
    beforeEach(() => {
      // eslint-disable-next-line @typescript-eslint/ban-ts-comment
      // @ts-ignore
      delete global.window;
    });

    afterEach(() => {
      global.window = originalWindow;
    });

    it('should create a GraphQL client with the public runtime config endpoint when server endpoint is not enabled', () => {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'false';

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');
      const client = getClient();

      expect(mockGraphQLClient).toHaveBeenCalledWith('https://client.graphql.endpoint', {});
      expect(client).toBeDefined();
    });

    it('should use server endpoint when NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT is true', () => {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'true';
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'https://server.graphql.endpoint';

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');
      const client = getClient();

      expect(mockGraphQLClient).toHaveBeenCalledWith('https://server.graphql.endpoint', {});
      expect(client).toBeDefined();
    });

    it('should use empty string when server endpoint is enabled but NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT is not set', () => {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'true';
      delete process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT;

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');
      const client = getClient();

      expect(mockGraphQLClient).toHaveBeenCalledWith('', {});
      expect(client).toBeDefined();
    });

    it('should NOT cache the client on subsequent calls on server-side', () => {
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      const client1 = getClient();
      const client2 = getClient();

      expect(client1).not.toBe(client2);
      expect(mockGraphQLClient).toHaveBeenCalledTimes(2);
    });

    it('should create new client instances for each request to prevent data sharing', () => {
      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      const clients = [];
      for (let i = 0; i < 3; i++) {
        clients.push(getClient());
      }

      expect(clients[0]).not.toBe(clients[1]);
      expect(clients[1]).not.toBe(clients[2]);
      expect(mockGraphQLClient).toHaveBeenCalledTimes(3);
    });
  });

  describe('Edge cases', () => {
    it('should handle missing publicRuntimeConfig gracefully', () => {
      jest.resetModules();
      jest.doMock('next/config', () => () => ({}));

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      const client = getClient();

      expect(client).toBeDefined();
    });

    it('should handle null config from next/config', () => {
      jest.resetModules();
      jest.doMock('next/config', () => () => null);

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      const client = getClient();

      expect(client).toBeDefined();
    });

    it('should use empty string endpoint when publicRuntimeConfig.NEXT_PUBLIC_GRAPHQL_ENDPOINT is not set', () => {
      jest.resetModules();
      mockGraphQLClient.mockClear();
      jest.doMock('next/config', () => () => ({
        publicRuntimeConfig: {},
      }));

      // Remock GraphQLClient
      jest.doMock('graphql-request', () => ({
        GraphQLClient: mockGraphQLClient,
      }));

      // eslint-disable-next-line @typescript-eslint/no-require-imports
      const { getGQLClient: getClient } = require('./client');

      getClient();

      expect(mockGraphQLClient).toHaveBeenCalledWith('', {});
    });
  });
});
