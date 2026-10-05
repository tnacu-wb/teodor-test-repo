import { test, expect } from '@playwright/test';
import { GraphQLClient } from '../../../src/api';
import { getEnvironmentConfig } from '../../../config/environments';

/**
 * Environment Integration Tests
 *
 * Validates the live environment configuration and GraphQL client wiring used by the
 * Playwright QA suite. This intentionally targets the current public contract instead
 * of the removed `clientFactory` helper from the legacy design.
 */

function createRequestStub() {
  return {
    post: async () => {
      throw new Error('request stub should not be used in environment config tests');
    },
  } as any;
}

test.describe('Environment integration @api', () => {
  test('getEnvironmentConfig throws for invalid env value', () => {
    expect(() => getEnvironmentConfig({ env: 'invalid_env' as any, app: 'pi' })).toThrow(/Unknown environment: "invalid_env"/);
  });

  test('default config resolves to uat when env is omitted', () => {
    const config = getEnvironmentConfig({ app: 'pi' });

    expect(config.apiBaseUrl).toBe('https://api.uat.premierinn.digital/graphql');
    expect(config.baseUrl).toBe('https://www.uat.premierinn.digital/gb/en');
  });

  test('dit config is accepted as valid', () => {
    const config = getEnvironmentConfig({ env: 'dit', app: 'pi' });

    expect(config.apiBaseUrl).toBe('https://api.dit.premierinn.digital/graphql');
    expect(config.baseUrl).toBe('https://www.dit.premierinn.digital/gb/en');
  });

  test('uat config is accepted as valid', () => {
    const config = getEnvironmentConfig({ env: 'uat', app: 'pi' });

    expect(config.apiBaseUrl).toBe('https://api.uat.premierinn.digital/graphql');
    expect(config.baseUrl).toBe('https://www.uat.premierinn.digital/gb/en');
  });

  test('GraphQLClient can be created with the resolved environment endpoint', () => {
    const request = createRequestStub();
    const client = new GraphQLClient(request, getEnvironmentConfig({ env: 'uat', app: 'pi' }).apiBaseUrl);

    expect(client).toBeInstanceOf(GraphQLClient);
  });
});
