import { readFile } from 'node:fs/promises';
import { join, sep } from 'node:path';
import { pathToFileURL } from 'node:url';

export interface ApiFixtureJsonData {
  [key: string]: unknown;
}

/**
 * API fixture data used by GraphQL/API helpers.
 */
export class ApiFixtures {
  private constructor() {}

  static readonly GRAPHQL_FIXTURES_PATH = pathToFileURL(join(__dirname, 'fixtures', 'graphql') + sep);
  static readonly PREPAYMENT_DETAILS_PATH = pathToFileURL(join(__dirname, 'fixtures', 'prepaymentDetails.json'));

  /**
   * Read a GraphQL query or mutation fixture by filename.
   * @param queryFile GraphQL fixture filename, for example getBasketByBasketReference.graphql.
   * @returns GraphQL query text.
   */
  static async readGraphqlFixture(queryFile: string): Promise<string> {
    if (!queryFile || typeof queryFile !== 'string') {
      throw new Error('queryFile must be a non-empty string.');
    }

    return readFile(new URL(queryFile, ApiFixtures.GRAPHQL_FIXTURES_PATH), 'utf8');
  }

  /** Read prepayment details fixture. */
  static async readPrepaymentDetails<T extends ApiFixtureJsonData = ApiFixtureJsonData>(): Promise<T> {
    return JSON.parse(await readFile(ApiFixtures.PREPAYMENT_DETAILS_PATH, 'utf8')) as T;
  }
}
