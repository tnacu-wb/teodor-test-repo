/* eslint-disable @typescript-eslint/no-explicit-any */
import * as ReactQuery from '@tanstack/react-query';
import axios from 'axios';
import { getCookie } from 'cookies-next';
import { gql, GraphQLClient } from 'graphql-request';

import { logger } from '../logger/logger';
import {
  graphQLRequest,
  useMutationRequest,
  useQueryRequest,
  graphQLRequestRestaurants,
  useMutationRequestRestaurants,
  useQueryRequestRestaurants,
  axiosRequest,
  useRestQueryRequest,
  useRestMutationRequest,
} from './use-request';

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(() => ({
    publicRuntimeConfig: {
      NEXT_PUBLIC_GRAPHQL_ENDPOINT: 'https://api.example.com/graphql',
      NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT: 'https://api.example.com/restaurants/graphql',
      NEXT_PUBLIC_APP_NAME: 'test-app',
      NEXT_PUBLIC_APOLLO_CLIENT_VERSION: '1.0.0',
    },
  })),
}));

const GET_LAUNCHES_QUERY = gql`
  query getLaunches($limit: Int! = 5) {
    launches(limit: $limit) {
      id
      launch_year
      rocket {
        rocket_name
      }
      details
    }
  }
`;

const ADD_USER_MUTATION = gql`
  mutation addUser($userName: String!, $rocketName: String!) {
    insert_users(objects: { name: $userName, rocket: $rocketName }) {
      returning {
        id
        name
        rocket
      }
    }
  }
`;
const mockRefetch = jest.fn();
const mockUseQuery = {
  isLoading: false,
  isSuccess: true,
  isError: false,
  isFetching: false,
  error: null,
  refetch: mockRefetch,
  data: {
    launches: [
      {
        id: '13',
        launch_year: '2014',
        rocket: { rocket_name: 'Falcon 9' },
        details: 'Second GTO launch for Falcon 9.',
      },
    ],
  },
};

const responseGQ = {
  insert_users: {
    returning: [
      {
        id: '2295883a-9651-4e55-a44b-a2edeb73aada',
        name: 'test',
        rocket: 'test',
      },
    ],
  },
};

const useMutationMock = {
  isPending: true,
  isError: false,
  isSuccess: true,
  isIdle: false,
  data: { test: 'test' },
  error: null,
};

jest.mock('../logger/logger.ts', () => ({
  logger: {
    info: jest.fn(),
    error: jest.fn(),
  },
  getClientDefaultSessionTracing: () => ({ 'WB-SESSION-ID': 'TestSessionID' }),
}));

jest.mock('@tanstack/react-query', () => {
  const original: typeof ReactQuery = jest.requireActual('@tanstack/react-query');
  return {
    ...original,
    useQuery: jest.fn(() => mockUseQuery),
    useMutation: jest.fn(() => useMutationMock),
    setEndpoint: () => '',
  };
});

jest.mock('graphql-request', () => ({
  ...jest.requireActual('graphql-request'),
  request: () => responseGQ,
  setEndpoint: () => '',
  GraphQLClient: function GraphQLClient() {
    this.request = () => responseGQ;
    this.setEndpoint = () => '';
    return this;
  },
}));

jest.mock('cookies-next', () => ({
  getCookie: jest.fn().mockReturnValue(undefined),
}));

jest.mock('axios');

jest.mock('../gql/client', () => ({
  getGQLClient: jest.fn(() => ({
    request: jest.fn().mockResolvedValue(responseGQ),
    setEndpoint: jest.fn(),
  })),
}));

describe('use-request hooks', () => {
  it('graphQLRequest', async function () {
    const response = await graphQLRequest(ADD_USER_MUTATION, { limit: 1, idToken: '1' }, '123', {
      accessToken: '12',
    });
    expect(response).toBe(responseGQ);
  });

  it('graphQLRequest with basketIds in proxyOptions', async function () {
    const basketIds = JSON.stringify(['basketId1', 'basketId2']);
    const response = await graphQLRequest(ADD_USER_MUTATION, { limit: 1 }, undefined, {
      basketIds,
    });
    expect(response).toBe(responseGQ);
  });

  it('graphQLRequest with basketIds and accessToken in proxyOptions', async function () {
    const basketIds = JSON.stringify(['basketId1', 'basketId2']);
    const response = await graphQLRequest(ADD_USER_MUTATION, { limit: 1 }, undefined, {
      accessToken: 'testAccessToken',
      basketIds,
    });
    expect(response).toBe(responseGQ);
  });

  it('useQueryRequest', async function () {
    const response = useQueryRequest(
      'launchesPrefetchedOnServerAndRefetchedOnClient',
      GET_LAUNCHES_QUERY,
      { limit: 1 }
    );
    expect(response).toEqual(mockUseQuery);
  });

  it('useQueryRequest with basketIds in proxyOptions', async function () {
    const basketIds = JSON.stringify(['basketId1', 'basketId2']);
    const response = useQueryRequest(
      'launchesPrefetchedOnServerAndRefetchedOnClient',
      GET_LAUNCHES_QUERY,
      { limit: 1 },
      undefined,
      undefined,
      false,
      { basketIds }
    );
    expect(response).toEqual(mockUseQuery);
  });

  it('useMutationRequest', async function () {
    const request = useMutationRequest(ADD_USER_MUTATION);

    expect(request).toEqual({
      mutation: useMutationMock,
      isLoading: useMutationMock.isPending,
      isError: useMutationMock.isError,
      isSuccess: useMutationMock.isSuccess,
      isIdle: useMutationMock.isIdle,
      data: useMutationMock.data,
      error: useMutationMock.error,
    });
  });
});

// Restaurants-specific hooks tests
describe('Restaurants use-request hooks', () => {
  const mockUseQueryResponseRestaurants = {
    isLoading: false,
    isError: false,
    isSuccess: true,
    isFetching: false,
    error: null,
    data: {
      launches: [
        {
          id: '13',
          launch_year: '2014',
          rocket: { rocket_name: 'Falcon 9' },
          details: 'Second GTO launch for Falcon 9.',
        },
      ],
    },
    refetch: jest.fn(),
  };

  const mockResponseGQRestaurants = {
    insert_users: {
      returning: [
        {
          id: '2295883a-9651-4e55-a44b-a2edeb73aada',
          name: 'test',
          rocket: 'test',
        },
      ],
    },
  };

  const mockUseMutationRestaurants = {
    isPending: true,
    isError: false,
    isSuccess: true,
    isIdle: false,
    data: { test: 'test' },
    error: null,
  };

  beforeEach(() => {
    jest.clearAllMocks();
    // Reset mocks
    jest.spyOn(ReactQuery, 'useQuery').mockReturnValue(mockUseQueryResponseRestaurants as any);
    jest.spyOn(ReactQuery, 'useMutation').mockReturnValue(mockUseMutationRestaurants as any);
    const { getCookie } = jest.requireMock('cookies-next');
    getCookie.mockReturnValue(undefined);
  });

  it('graphQLRequestRestaurants', async function () {
    const response = await graphQLRequestRestaurants(ADD_USER_MUTATION, { limit: 1 });
    expect(response).toEqual(mockResponseGQRestaurants);
  });

  it('graphQLRequestRestaurants with custom gqlClient', async function () {
    const mockCustomClient = {
      request: jest.fn().mockResolvedValue({ custom: 'response' }),
      setEndpoint: jest.fn(),
    };

    const response = await graphQLRequestRestaurants(
      ADD_USER_MUTATION,
      { limit: 1 },
      undefined,
      undefined,
      mockCustomClient as any
    );

    expect(mockCustomClient.setEndpoint).toHaveBeenCalled();
    expect(mockCustomClient.request).toHaveBeenCalledWith(
      ADD_USER_MUTATION,
      { limit: 1 },
      expect.objectContaining({
        'apollographql-client-name': expect.any(String),
        'apollographql-client-version': expect.any(String),
      })
    );
    expect(response).toEqual({ custom: 'response' });
  });

  it('graphQLRequestRestaurants server-side with NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT', async function () {
    const originalFlag = process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
    const originalServerUrl = process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
    const originalWindow = global.window;

    delete (global as any).window;
    process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = 'true';
    process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT =
      'https://internal-restaurants.graphql.endpoint';

    const mockClient = {
      request: jest.fn().mockResolvedValue(mockResponseGQRestaurants),
      setEndpoint: jest.fn(),
    };

    await graphQLRequestRestaurants(
      ADD_USER_MUTATION,
      { limit: 1 },
      undefined,
      undefined,
      mockClient as any
    );

    expect(mockClient.setEndpoint).toHaveBeenCalledWith(
      'https://internal-restaurants.graphql.endpoint'
    );

    if (originalFlag !== undefined) {
      process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
    } else {
      delete process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
    }
    if (originalServerUrl !== undefined) {
      process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalServerUrl;
    } else {
      delete process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
    }
    (global as any).window = originalWindow;
  });

  it('useQueryRequestRestaurants', async function () {
    const response = useQueryRequestRestaurants(
      'launchesPrefetchedOnServerAndRefetchedOnClient',
      GET_LAUNCHES_QUERY,
      { limit: 1 }
    );
    expect(response).toMatchObject({
      isLoading: mockUseQueryResponseRestaurants.isLoading,
      isError: mockUseQueryResponseRestaurants.isError,
      isSuccess: mockUseQueryResponseRestaurants.isSuccess,
      isFetching: mockUseQueryResponseRestaurants.isFetching,
      error: mockUseQueryResponseRestaurants.error,
      data: mockUseQueryResponseRestaurants.data,
    });
    expect(response.refetch).toBeDefined();
  });

  it('useQueryRequestRestaurants with array queryKey', async function () {
    const response = useQueryRequestRestaurants(
      ['launches', 'list'],
      GET_LAUNCHES_QUERY,
      { limit: 5 },
      'test-token',
      { useProxyAPI: true }
    );
    expect(response).toMatchObject({
      isLoading: mockUseQueryResponseRestaurants.isLoading,
      isError: mockUseQueryResponseRestaurants.isError,
      isSuccess: mockUseQueryResponseRestaurants.isSuccess,
      isFetching: mockUseQueryResponseRestaurants.isFetching,
      error: mockUseQueryResponseRestaurants.error,
      data: mockUseQueryResponseRestaurants.data,
    });
    expect(response.refetch).toBeDefined();
  });

  it('useMutationRequestRestaurants', async function () {
    const request = useMutationRequestRestaurants(ADD_USER_MUTATION);

    expect(request).toEqual({
      mutation: mockUseMutationRestaurants,
      isLoading: mockUseMutationRestaurants.isPending,
      isError: mockUseMutationRestaurants.isError,
      isSuccess: mockUseMutationRestaurants.isSuccess,
      isIdle: mockUseMutationRestaurants.isIdle,
      data: mockUseMutationRestaurants.data,
      error: mockUseMutationRestaurants.error,
    });
  });

  it('useMutationRequestRestaurants with options and reactQueryOptions', async function () {
    const onSuccess = jest.fn();
    const request = useMutationRequestRestaurants(
      ADD_USER_MUTATION,
      { defaultOption: 'value' },
      { onSuccess }
    );

    expect(request).toEqual({
      mutation: mockUseMutationRestaurants,
      isLoading: mockUseMutationRestaurants.isPending,
      isError: mockUseMutationRestaurants.isError,
      isSuccess: mockUseMutationRestaurants.isSuccess,
      isIdle: mockUseMutationRestaurants.isIdle,
      data: mockUseMutationRestaurants.data,
      error: mockUseMutationRestaurants.error,
    });
  });
});

// Error handling tests
describe('Error handling and propagation', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    // Reset to default mocks
    (ReactQuery.useQuery as jest.Mock).mockReturnValue(mockUseQuery);
    (ReactQuery.useMutation as jest.Mock).mockReturnValue(useMutationMock);
  });

  it('should propagate errors from graphQLRequest when error occurs', async () => {
    const testError = new Error('GraphQL request failed');
    const mockClient = {
      request: jest.fn().mockRejectedValue(testError),
      setEndpoint: jest.fn(),
    };

    await expect(
      graphQLRequest(ADD_USER_MUTATION, { limit: 1 }, undefined, undefined, mockClient as any)
    ).rejects.toThrow('GraphQL request failed');
  });

  it('should log errors and re-throw in executeGraphQLRequest', async () => {
    const consoleErrorSpy = jest.spyOn(console, 'error').mockImplementation();
    const testError = {
      response: { errors: [{ message: 'Test error' }] },
      request: { variables: { limit: 1 } },
    };

    const mockClient = {
      request: jest.fn().mockRejectedValue(testError),
      setEndpoint: jest.fn(),
    };

    await expect(
      graphQLRequest(ADD_USER_MUTATION, { limit: 1 }, undefined, undefined, mockClient as any)
    ).rejects.toBe(testError);

    expect(consoleErrorSpy).toHaveBeenCalled();
    consoleErrorSpy.mockRestore();
  });

  it('useQueryRequest should throw error when isError is true and returnError is false', () => {
    const mockError = new Error('Query failed');
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: true,
      isSuccess: false,
      isFetching: false,
      data: undefined,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    expect(() => {
      useQueryRequest('testQuery', GET_LAUNCHES_QUERY, {}, {}, undefined, false);
    }).toThrow('Query failed');
  });

  it('useQueryRequest should return error when returnError is true', () => {
    const mockError = new Error('Query failed');
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: true,
      isSuccess: false,
      isFetching: false,
      data: undefined,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    const result = useQueryRequest('testQuery', GET_LAUNCHES_QUERY, {}, {}, undefined, true);

    expect(result.isError).toBe(true);
    expect(result.error).toBe(mockError);
  });

  it('useQueryRequest should log errors when query fails', () => {
    const mockError = new Error('Query failed');
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: true,
      isSuccess: false,
      isFetching: false,
      data: undefined,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    try {
      useQueryRequest('testQuery', GET_LAUNCHES_QUERY, {}, {}, 'test-token', false);
    } catch (e) {
      // Expected to throw
    }

    expect(logger.error).toHaveBeenCalledWith({
      label: 'QUERY_REQUEST_ERROR',
      err: mockError,
      msg: expect.objectContaining({
        queryKey: 'testQuery',
        accessToken: 'test-token',
      }),
    });
  });

  it('useMutationRequest should throw error when isError is true and returnError is false', () => {
    const mockError = new Error('Mutation failed');
    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce({
      isPending: false,
      isError: true,
      isSuccess: false,
      isIdle: false,
      data: undefined,
      error: mockError,
    } as any);

    expect(() => {
      useMutationRequest(ADD_USER_MUTATION, false, 'test-token');
    }).toThrow('Mutation failed');
  });

  it('useMutationRequest should return error when returnError is true', () => {
    const mockError = new Error('Mutation failed');
    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce({
      isPending: false,
      isError: true,
      isSuccess: false,
      isIdle: false,
      data: undefined,
      error: mockError,
    } as any);

    const result = useMutationRequest(ADD_USER_MUTATION, true, 'test-token');

    expect(result.isError).toBe(true);
    expect(result.error).toBe(mockError);
  });

  it('useMutationRequest should log errors when mutation fails', () => {
    const mockError = new Error('Mutation failed');
    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce({
      isPending: false,
      isError: true,
      isSuccess: false,
      isIdle: false,
      data: undefined,
      error: mockError,
    } as any);

    try {
      useMutationRequest(ADD_USER_MUTATION, false, 'test-token');
    } catch (e) {
      // Expected to throw
    }

    expect(logger.error).toHaveBeenCalledWith({
      label: 'MUTATION_REQUEST_ERROR',
      err: mockError,
      msg: expect.objectContaining({
        accessToken: 'test-token',
      }),
    });
  });

  it('useQueryRequestRestaurants should log errors when query fails', () => {
    const mockError = new Error('Restaurants query failed');
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: true,
      isSuccess: false,
      isFetching: false,
      data: undefined,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    const result = useQueryRequestRestaurants(
      'restaurantsQuery',
      GET_LAUNCHES_QUERY,
      { limit: 1 },
      'test-token'
    );

    expect(result.isError).toBe(true);
    expect(logger.error).toHaveBeenCalledWith({
      label: 'RESTAURANTS_QUERY_REQUEST_ERROR',
      err: mockError,
      msg: expect.objectContaining({
        queryKey: 'restaurantsQuery',
        accessToken: 'test-token',
      }),
    });
  });

  it('useRestQueryRequest should log errors when query fails', () => {
    const mockError = new Error('REST query failed');
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: true,
      isSuccess: false,
      isFetching: false,
      data: undefined,
      error: mockError,
      refetch: jest.fn(),
    } as any);

    const result = useRestQueryRequest('restQuery', 'GET', 'https://api.test.com/data');

    expect(result.isError).toBe(true);
    expect(logger.error).toHaveBeenCalledWith({
      label: 'REST_QUERY_REQUEST_ERROR',
      err: mockError,
      msg: expect.objectContaining({
        queryKey: 'restQuery',
        url: 'https://api.test.com/data',
      }),
    });
  });

  it('useRestMutationRequest should log errors when mutation fails', () => {
    const mockError = new Error('REST mutation failed');
    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce({
      isPending: false,
      isError: true,
      isSuccess: false,
      isIdle: false,
      data: undefined,
      error: mockError,
      status: 'error',
    } as any);

    const result = useRestMutationRequest('https://api.test.com/data', 'POST');

    expect(result.isError).toBe(true);
    expect(logger.error).toHaveBeenCalledWith({
      label: 'REST_MUTATION_REQUEST_ERROR',
      err: mockError,
      msg: expect.objectContaining({
        url: 'https://api.test.com/data',
      }),
    });
  });
});

// REST API tests
describe('REST API functions', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (ReactQuery.useQuery as jest.Mock).mockReturnValue(mockUseQuery);
    (ReactQuery.useMutation as jest.Mock).mockReturnValue(useMutationMock);
  });

  it('axiosRequest should make GET request and return data', async () => {
    const mockData = { id: 1, name: 'Test' };
    (axios as jest.MockedFunction<typeof axios>).mockResolvedValue({
      data: mockData,
      status: 200,
      statusText: 'OK',
      headers: {},
      config: {} as any,
    });

    const result = await axiosRequest({
      method: 'GET',
      url: 'https://api.test.com/data',
      params: { id: 1 },
    });

    expect(axios).toHaveBeenCalledWith(
      expect.objectContaining({
        method: 'GET',
        url: 'https://api.test.com/data',
        params: { id: 1 },
        headers: expect.objectContaining({
          Origin: undefined, // NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC not set in mock
        }),
      })
    );
    expect(result).toEqual(mockData);
  });

  it('axiosRequest should make POST request with data', async () => {
    const mockData = { success: true };
    const postData = { name: 'New Item' };
    (axios as jest.MockedFunction<typeof axios>).mockResolvedValue({
      data: mockData,
      status: 201,
      statusText: 'Created',
      headers: {},
      config: {} as any,
    });

    const result = await axiosRequest({
      method: 'POST',
      url: 'https://api.test.com/data',
      data: postData,
      headers: { 'Content-Type': 'application/json' },
    });

    expect(axios).toHaveBeenCalledWith(
      expect.objectContaining({
        method: 'POST',
        url: 'https://api.test.com/data',
        data: postData,
        headers: expect.objectContaining({
          'Content-Type': 'application/json',
        }),
      })
    );
    expect(result).toEqual(mockData);
  });

  it('useRestQueryRequest should return data on success', () => {
    const mockData = { items: [1, 2, 3] };
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: false,
      isSuccess: true,
      isFetching: false,
      data: mockData,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useRestQueryRequest('items', 'GET', 'https://api.test.com/items');

    expect(result).toMatchObject({
      isLoading: false,
      isError: false,
      isSuccess: true,
      isFetching: false,
      data: mockData,
      error: null,
    });
    expect(result.refetch).toBeDefined();
  });

  it('useRestQueryRequest should handle custom headers and body', () => {
    const mockData = { result: 'success' };
    const customHeaders = { Authorization: 'Bearer token123' };
    const body = { filter: 'active' };

    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: false,
      isSuccess: true,
      isFetching: false,
      data: mockData,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useRestQueryRequest(
      'customQuery',
      'POST',
      'https://api.test.com/search',
      customHeaders,
      {},
      body
    );

    expect(result.isSuccess).toBe(true);
    expect(result.data).toEqual(mockData);
  });

  it('useRestMutationRequest should return mutation object', () => {
    const mockMutation = {
      isPending: false,
      isError: false,
      isSuccess: true,
      isIdle: false,
      data: { id: 123 },
      error: null,
      status: 'success',
    };

    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce(mockMutation as any);

    const result = useRestMutationRequest('https://api.test.com/create', 'POST', {
      'Content-Type': 'application/json',
    });

    expect(result).toMatchObject({
      mutation: mockMutation,
      isLoading: false,
      isError: false,
      isSuccess: true,
      isIdle: false,
      data: { id: 123 },
      error: null,
      status: 'success',
    });
  });
});

// Edge cases and configuration tests
describe('Edge cases and configuration', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    (getCookie as jest.Mock).mockReturnValue(undefined);
    (ReactQuery.useQuery as jest.Mock).mockReturnValue(mockUseQuery);
    (ReactQuery.useMutation as jest.Mock).mockReturnValue(useMutationMock);
  });

  afterEach(() => {
    delete (global as any).window;
    delete (global as any).document;
  });

  it('should handle missing next/config gracefully', async () => {
    jest.resetModules();
    jest.doMock('next/config', () => () => null);

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { graphQLRequest: gqlRequest } = require('./use-request');
    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await gqlRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).toHaveBeenCalled();
  });

  it('should handle missing publicRuntimeConfig gracefully', async () => {
    jest.resetModules();
    jest.doMock('next/config', () => () => ({}));

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { graphQLRequest: gqlRequest } = require('./use-request');
    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await gqlRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).toHaveBeenCalled();
  });

  it('should use proxy API when cookies are set correctly', async () => {
    // Setup client-side environment
    (global as any).window = {};
    (global as any).document = {
      location: {
        origin: 'https://test.example.com',
      },
    };

    (getCookie as jest.Mock).mockImplementation((name: string) => {
      if (name === 'useProxyAPI') return true;
      if (name === 'hostURL') return 'https://test.example.com';
      return undefined;
    });

    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'proxy' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await graphQLRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).toHaveBeenCalledWith('https://test.example.com/api/graphql');
  });

  it('should not use proxy API when hostURL does not match origin', async () => {
    (global as any).window = {};
    (global as any).document = {
      location: {
        origin: 'https://test.example.com',
      },
    };

    (getCookie as jest.Mock).mockImplementation((name: string) => {
      if (name === 'useProxyAPI') return true;
      if (name === 'hostURL') return 'https://different.example.com';
      return undefined;
    });

    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await graphQLRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).not.toHaveBeenCalledWith('https://test.example.com/api/graphql');
  });

  it('should use server-side endpoint when NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT is true', async () => {
    delete (global as any).window;
    const originalEnv = process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
    const originalServerEndpoint = process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT;

    process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'true';
    process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'https://server.graphql.endpoint';

    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'server' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await graphQLRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).toHaveBeenCalledWith('https://server.graphql.endpoint');

    // Clean up
    if (originalEnv !== undefined) {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = originalEnv;
    } else {
      delete process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
    }
    if (originalServerEndpoint !== undefined) {
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = originalServerEndpoint;
    } else {
      delete process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT;
    }
  });

  it('should handle array queryKey in useQueryRequest', () => {
    const mockData = { test: 'data' };
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: false,
      isSuccess: true,
      isFetching: false,
      data: mockData,
      error: null,
      refetch: jest.fn(),
    } as any);

    const result = useQueryRequest(['users', 'list', { page: 1 }], GET_LAUNCHES_QUERY, {
      limit: 10,
    });

    expect(result.isSuccess).toBe(true);
    expect(result.data).toEqual(mockData);
  });

  it('should pass reactQueryOptions to useQuery', () => {
    (ReactQuery.useQuery as jest.Mock).mockReturnValueOnce({
      isLoading: false,
      isError: false,
      isSuccess: true,
      isFetching: false,
      data: {},
      error: null,
      refetch: jest.fn(),
    } as any);

    const reactQueryOptions = {
      enabled: false,
      staleTime: 5000,
      retry: 3,
    };

    useQueryRequest('testQuery', GET_LAUNCHES_QUERY, {}, reactQueryOptions);

    expect(ReactQuery.useQuery).toHaveBeenCalledWith(
      expect.objectContaining({
        enabled: false,
        staleTime: 5000,
        retry: 3,
      })
    );
  });

  it('should pass reactQueryOptions to useMutation', () => {
    (ReactQuery.useMutation as jest.Mock).mockReturnValueOnce({
      isPending: false,
      isError: false,
      isSuccess: true,
      isIdle: false,
      data: {},
      error: null,
    } as any);

    const onSuccess = jest.fn();
    const reactQueryOptions = {
      onSuccess,
      retry: 2,
    };

    useMutationRequest(ADD_USER_MUTATION, false, undefined, reactQueryOptions);

    expect(ReactQuery.useMutation).toHaveBeenCalledWith(
      expect.objectContaining({
        onSuccess,
        retry: 2,
      })
    );
  });

  it('should remove idToken from options after using it in headers', async () => {
    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    const options = { limit: 1, idToken: 'test-id-token' };

    await graphQLRequest(ADD_USER_MUTATION, options, undefined, undefined, mockClient);

    // idToken should be removed from options
    expect(options).not.toHaveProperty('idToken');
  });

  it('should prioritize accessToken over proxyOptions.accessToken', async () => {
    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await graphQLRequest(
      ADD_USER_MUTATION,
      {},
      'direct-access-token',
      { accessToken: 'proxy-access-token' },
      mockClient
    );

    // The direct accessToken should be used
    expect(mockClient.request).toHaveBeenCalledWith(
      ADD_USER_MUTATION,
      {},
      expect.objectContaining({
        Authorization: 'Bearer direct-access-token',
      })
    );
  });

  it('should use default GraphQL endpoint when no config is available', async () => {
    jest.resetModules();
    jest.doMock('next/config', () => () => ({
      publicRuntimeConfig: {},
    }));

    // eslint-disable-next-line @typescript-eslint/no-require-imports
    const { graphQLRequest: gqlRequest } = require('./use-request');
    const mockClient = {
      request: jest.fn().mockResolvedValue({ data: 'test' }),
      setEndpoint: jest.fn(),
    } as unknown as GraphQLClient;

    await gqlRequest(ADD_USER_MUTATION, {}, undefined, undefined, mockClient);

    expect(mockClient.setEndpoint).toHaveBeenCalledWith('http://localhost:3000/api/graphql');
  });
});

describe('graphql apollo Istio routing', () => {
  let originalWindow: any;

  beforeEach(() => {
    jest.clearAllMocks();
    originalWindow = (global as any).window;
    (getCookie as jest.Mock).mockReturnValue(undefined);
  });

  afterEach(() => {
    (global as any).window = originalWindow;
  });

  describe('graphQLRequest — serverConfigKey fix', () => {
    it('uses publicRuntimeConfig client endpoint when flag is false and server-side', async () => {
      delete (global as any).window;
      const originalFlag = process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'false';

      const client = { request: jest.fn().mockResolvedValue(responseGQ), setEndpoint: jest.fn() };
      await graphQLRequest(ADD_USER_MUTATION, {}, undefined, undefined, client as any);

      expect(client.setEndpoint).toHaveBeenCalledWith('https://api.example.com/graphql');

      if (originalFlag !== undefined) {
        process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
      } else {
        delete process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      }
    });

    it('uses publicRuntimeConfig client endpoint when client-side even if flag is true', async () => {
      (global as any).window = {};
      const originalFlag = process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      const originalServerUrl = process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'true';
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'https://internal.apollo/graphql';

      const client = { request: jest.fn().mockResolvedValue(responseGQ), setEndpoint: jest.fn() };
      await graphQLRequest(ADD_USER_MUTATION, {}, undefined, undefined, client as any);

      expect(client.setEndpoint).toHaveBeenCalledWith('https://api.example.com/graphql');
      expect(client.setEndpoint).not.toHaveBeenCalledWith('https://internal.apollo/graphql');

      if (originalFlag !== undefined) {
        process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
      } else {
        delete process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      }
      if (originalServerUrl !== undefined) {
        process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = originalServerUrl;
      } else {
        delete process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT;
      }
    });
  });

  describe('graphQLRequestRestaurants — serverEnvVar and serverConfigKey fix', () => {
    it('uses publicRuntimeConfig restaurants endpoint when NEXT_ENABLE flag is false and server-side', async () => {
      delete (global as any).window;
      const originalFlag = process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = 'false';

      const client = { request: jest.fn().mockResolvedValue(responseGQ), setEndpoint: jest.fn() };
      await graphQLRequestRestaurants(
        ADD_USER_MUTATION,
        { limit: 1 },
        undefined,
        undefined,
        client as any
      );

      expect(client.setEndpoint).toHaveBeenCalledWith(
        'https://api.example.com/restaurants/graphql'
      );

      if (originalFlag !== undefined) {
        process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
      } else {
        delete process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      }
    });

    it('does not use server endpoint when URL env var (not flag) is set to "true" (old buggy pattern)', async () => {
      delete (global as any).window;
      const originalServerUrl = process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      const originalFlag = process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = 'true';
      delete process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;

      const client = { request: jest.fn().mockResolvedValue(responseGQ), setEndpoint: jest.fn() };
      await graphQLRequestRestaurants(
        ADD_USER_MUTATION,
        { limit: 1 },
        undefined,
        undefined,
        client as any
      );

      expect(client.setEndpoint).toHaveBeenCalledWith(
        'https://api.example.com/restaurants/graphql'
      );

      if (originalServerUrl !== undefined) {
        process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalServerUrl;
      } else {
        delete process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      }
      if (originalFlag !== undefined) {
        process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
      }
    });

    it('uses publicRuntimeConfig restaurants endpoint when client-side even if flag is true', async () => {
      (global as any).window = {};
      const originalFlag = process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      const originalServerUrl = process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = 'true';
      process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT =
        'https://internal-restaurants.apollo/graphql';

      const client = { request: jest.fn().mockResolvedValue(responseGQ), setEndpoint: jest.fn() };
      await graphQLRequestRestaurants(
        ADD_USER_MUTATION,
        { limit: 1 },
        undefined,
        undefined,
        client as any
      );

      expect(client.setEndpoint).toHaveBeenCalledWith(
        'https://api.example.com/restaurants/graphql'
      );
      expect(client.setEndpoint).not.toHaveBeenCalledWith(
        'https://internal-restaurants.apollo/graphql'
      );

      if (originalFlag !== undefined) {
        process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalFlag;
      } else {
        delete process.env.NEXT_ENABLE_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      }
      if (originalServerUrl !== undefined) {
        process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT = originalServerUrl;
      } else {
        delete process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT;
      }
    });
  });
});
