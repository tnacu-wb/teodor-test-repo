import GraphiQLExplorer from 'graphiql-explorer';
import fetch from 'isomorphic-unfetch';

import {
  fetcher,
  makeDefaultArg,
  getDefaultScalarArgValue,
  getOperationKind,
  getOperationName,
} from './graphql';

jest.mock('isomorphic-unfetch', () => jest.fn());
const mockedFetch = jest.mocked(fetch);

jest.mock('graphiql-explorer', () => ({
  __esModule: true,
  default: {
    defaultValue: jest.fn(() => ({ kind: 'StringValue', value: 'default' })),
  },
}));

describe('graphql utilities', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('fetcher', () => {
    const mockGraphQLParams = {
      query: '{ users { id name } }',
      variables: { id: 1 },
    };

    beforeEach(() => {
      process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT = 'https://api.example.com/graphql';
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'https://server.example.com/graphql';
    });

    it('should fetch GraphQL data from client endpoint', async () => {
      const mockResponse = { data: { users: [{ id: 1, name: 'John' }] } };
      mockedFetch.mockResolvedValueOnce({
        json: jest.fn().mockResolvedValueOnce(mockResponse),
      } as any);

      const result = await fetcher(mockGraphQLParams);

      expect(mockedFetch).toHaveBeenCalledWith('https://api.example.com/graphql', {
        method: 'post',
        headers: {
          Accept: 'application/json',
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(mockGraphQLParams),
      });
      expect(result).toEqual(mockResponse);
    });

    it('should use client endpoint when NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT is false', async () => {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'false';
      const mockResponse = { data: { users: [] } };
      mockedFetch.mockResolvedValueOnce({
        json: jest.fn().mockResolvedValueOnce(mockResponse),
      } as any);

      await fetcher(mockGraphQLParams);

      expect(mockedFetch).toHaveBeenCalledWith(
        'https://api.example.com/graphql',
        expect.any(Object)
      );
    });

    it('should use client endpoint when window is defined (browser context)', async () => {
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT = 'true';
      const mockResponse = { data: { users: [] } };
      mockedFetch.mockResolvedValueOnce({
        json: jest.fn().mockResolvedValueOnce(mockResponse),
      } as any);

      await fetcher(mockGraphQLParams);

      expect(mockedFetch).toHaveBeenCalledWith(
        'https://api.example.com/graphql',
        expect.any(Object)
      );
    });

    it('should handle fetch errors', async () => {
      const error = new Error('Network error');
      mockedFetch.mockRejectedValueOnce(error);

      await expect(fetcher(mockGraphQLParams)).rejects.toThrow('Network error');
    });

    it('should stringify GraphQL parameters correctly', async () => {
      const complexParams = {
        query: 'mutation { createUser(input: $input) { id } }',
        variables: {
          input: {
            name: 'Jane',
            email: 'jane@example.com',
            nested: { value: 123 },
          },
        },
      };
      mockedFetch.mockResolvedValueOnce({
        json: jest.fn().mockResolvedValueOnce({ data: {} }),
      } as any);

      await fetcher(complexParams);

      expect(mockedFetch).toHaveBeenCalledWith(
        expect.any(String),
        expect.objectContaining({
          body: JSON.stringify(complexParams),
        })
      );
    });
  });

  describe('makeDefaultArg', () => {
    it('should return true for GitHub Connection types with "first" argument', () => {
      const parentField = {
        name: 'repositories',
        type: {
          name: 'GitHubRepositoryConnection',
        },
      } as any;

      const arg = { name: 'first' } as any;

      const result = makeDefaultArg(parentField, arg);

      expect(result).toBe(true);
    });

    it('should return true for GitHub Connection types with "orderBy" argument', () => {
      const parentField = {
        name: 'issues',
        type: {
          name: 'GitHubIssueConnection',
        },
      } as any;

      const arg = { name: 'orderBy' } as any;

      const result = makeDefaultArg(parentField, arg);

      expect(result).toBe(true);
    });

    it('should return false for GitHub Connection types with other arguments', () => {
      const parentField = {
        name: 'repositories',
        type: {
          name: 'GitHubRepositoryConnection',
        },
      } as any;

      const arg = { name: 'after' } as any;

      const result = makeDefaultArg(parentField, arg);

      expect(result).toBe(false);
    });

    it('should return false for non-GitHub types', () => {
      const parentField = {
        name: 'users',
        type: {
          name: 'UserConnection',
        },
      } as any;

      const arg = { name: 'first' } as any;

      const result = makeDefaultArg(parentField, arg);

      expect(result).toBe(false);
    });

    it('should return false for GitHub types that do not end with Connection', () => {
      const parentField = {
        name: 'repository',
        type: {
          name: 'GitHubRepository',
        },
      } as any;

      const arg = { name: 'first' } as any;

      const result = makeDefaultArg(parentField, arg);

      expect(result).toBe(false);
    });
  });

  describe('getDefaultScalarArgValue', () => {
    it('should return graphql-js for GitHubRepository name argument', () => {
      const parentField = {
        type: { name: 'GitHubRepository' },
      } as any;
      const arg = { name: 'name' } as any;
      const argType = {} as any;

      const result = getDefaultScalarArgValue(parentField, arg, argType);

      expect(result).toEqual({ kind: 'StringValue', value: 'graphql-js' });
    });

    it('should return graphql for GitHubRepository owner argument', () => {
      const parentField = {
        type: { name: 'GitHubRepository' },
      } as any;
      const arg = { name: 'owner' } as any;
      const argType = {} as any;

      const result = getDefaultScalarArgValue(parentField, arg, argType);

      expect(result).toEqual({ kind: 'StringValue', value: 'graphql' });
    });

    it('should return graphql for NpmPackage name argument', () => {
      const parentField = {
        type: { name: 'NpmPackage' },
      } as any;
      const arg = { name: 'name' } as any;
      const argType = {} as any;

      const result = getDefaultScalarArgValue(parentField, arg, argType);

      expect(result).toEqual({ kind: 'StringValue', value: 'graphql' });
    });

    it('should return GraphiQLExplorer default value for other cases', () => {
      const parentField = {
        type: { name: 'User' },
      } as any;
      const arg = { name: 'id' } as any;
      const argType = {} as any;

      GraphiQLExplorer.defaultValue.mockReturnValueOnce({ kind: 'StringValue', value: 'default' });

      const result = getDefaultScalarArgValue(parentField, arg, argType);

      expect(GraphiQLExplorer.defaultValue).toHaveBeenCalledWith(argType);
      expect(result).toEqual({ kind: 'StringValue', value: 'default' });
    });

    it('should fall back to GraphiQLExplorer default for GitHubRepository with unhandled argument', () => {
      const parentField = {
        type: { name: 'GitHubRepository' },
      } as any;
      const arg = { name: 'id' } as any;
      const argType = {} as any;

      GraphiQLExplorer.defaultValue.mockReturnValueOnce({ kind: 'IntValue', value: '1' });

      const result = getDefaultScalarArgValue(parentField, arg, argType);

      expect(result).toEqual({ kind: 'IntValue', value: '1' });
    });
  });

  describe('getOperationKind', () => {
    it('should return "query" for query operations', () => {
      const def = {
        kind: 'OperationDefinition',
        operation: 'query',
      };

      const result = getOperationKind(def);

      expect(result).toBe('query');
    });

    it('should return "mutation" for mutation operations', () => {
      const def = {
        kind: 'OperationDefinition',
        operation: 'mutation',
      };

      const result = getOperationKind(def);

      expect(result).toBe('mutation');
    });

    it('should return "subscription" for subscription operations', () => {
      const def = {
        kind: 'OperationDefinition',
        operation: 'subscription',
      };

      const result = getOperationKind(def);

      expect(result).toBe('subscription');
    });

    it('should return "fragment" for fragment definitions', () => {
      const def = {
        kind: 'FragmentDefinition',
        name: { value: 'UserFields' },
      };

      const result = getOperationKind(def);

      expect(result).toBe('fragment');
    });

    it('should return "unknown" for unrecognized definition kinds', () => {
      const def = {
        kind: 'SomeOtherKind',
      };

      const result = getOperationKind(def);

      expect(result).toBe('unknown');
    });
  });

  describe('getOperationName', () => {
    it('should return operation name for named operations', () => {
      const def = {
        kind: 'OperationDefinition',
        name: { value: 'GetUsers' },
      };

      const result = getOperationName(def);

      expect(result).toBe('GetUsers');
    });

    it('should return fragment name for named fragments', () => {
      const def = {
        kind: 'FragmentDefinition',
        name: { value: 'UserFields' },
      };

      const result = getOperationName(def);

      expect(result).toBe('UserFields');
    });

    it('should return "unknown" for unnamed operations', () => {
      const def = {
        kind: 'OperationDefinition',
        name: null,
      };

      const result = getOperationName(def);

      expect(result).toBe('unknown');
    });

    it('should return "unknown" for unnamed fragments', () => {
      const def = {
        kind: 'FragmentDefinition',
        name: null,
      };

      const result = getOperationName(def);

      expect(result).toBe('unknown');
    });

    it('should return "unknown" for undefined name', () => {
      const def = {
        kind: 'OperationDefinition',
      };

      const result = getOperationName(def);

      expect(result).toBe('unknown');
    });

    it('should return "unknown" for unrecognized definition kinds', () => {
      const def = {
        kind: 'SomeOtherKind',
        name: { value: 'SomeName' },
      };

      const result = getOperationName(def);

      expect(result).toBe('unknown');
    });
  });
});
