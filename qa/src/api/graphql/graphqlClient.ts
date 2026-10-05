/**
 * GraphQL Client — Core queries-only client built on Playwright's APIRequestContext.
 *
 * Provides typed GraphQL query execution with:
 * - Configurable extra headers (auth headers are set by the factory, not here)
 * - Retry logic for 5xx server errors only (queries are idempotent)
 * - Configurable timeout (default 30s)
 * - Clear error messages distinguishing HTTP vs GraphQL failures
 *
 * No mutation method is exposed — the client is queries-only.
 *
 * @module graphqlClient
 */

import type { APIRequestContext } from '@playwright/test';

// ─── Types ──────────────────────────────────────────────────────────────────────

/** Options for configuring the GraphQL client. */
export interface GraphQLClientOptions {
  /** Request timeout in milliseconds. Default: 30000 */
  timeout?: number;
  /** Number of retries for 5xx errors. Default: 3 */
  retries?: number;
  /** Initial retry delay in milliseconds (doubles per attempt). Default: 1000 */
  retryDelay?: number;
  /** Additional headers to include with every request. */
  headers?: Record<string, string>;
}

/** Shape of a standard GraphQL JSON response body. */
interface GraphQLResponseBody {
  data?: unknown;
  errors?: GraphQLResponseError[];
}

/** Shape of a single error entry in a GraphQL response. */
interface GraphQLResponseError {
  message: string;
  extensions?: { errorType?: string; [key: string]: unknown };
  locations?: Array<{ line: number; column: number }>;
  path?: Array<string | number>;
  [key: string]: unknown;
}

// ─── Error Classes ──────────────────────────────────────────────────────────────

/** Error thrown for HTTP-level and GraphQL-level failures. */
export class GraphQLError extends Error {
  constructor(
    message: string,
    public readonly operation: string,
    public readonly variables: Record<string, unknown> | undefined,
    public readonly errors?: unknown[],
    public readonly response?: unknown,
  ) {
    super(message);
    this.name = 'GraphQLError';
  }
}

/** Error thrown when parameter or response validation fails. */
export class ValidationError extends Error {
  constructor(
    message: string,
    public readonly expected: unknown,
    public readonly actual: unknown,
    public readonly field?: string,
  ) {
    super(message);
    this.name = 'ValidationError';
  }
}

// ─── Client ─────────────────────────────────────────────────────────────────────

/**
 * Queries-only GraphQL client built on Playwright's APIRequestContext.
 *
 * Passes through whatever headers are configured in `options.headers` (the factory
 * is responsible for setting authentication headers like x-api-key / X-WHIT-API-KEY).
 * Retries only server-side (5xx) failures with exponential backoff.
 */
export class GraphQLClient {
  private readonly timeout: number;
  private readonly retries: number;
  private readonly retryDelay: number;
  private readonly extraHeaders: Record<string, string>;

  constructor(
    private readonly request: APIRequestContext,
    private readonly baseURL: string,
    private readonly options?: GraphQLClientOptions,
  ) {
    this.timeout = options?.timeout ?? 30_000;
    this.retries = options?.retries ?? 3;
    this.retryDelay = options?.retryDelay ?? 1_000;
    this.extraHeaders = options?.headers ?? {};
  }

  /**
   * Execute a GraphQL query and return the typed response data.
   *
   * @param query - The GraphQL query string (must be a non-empty string).
   * @param variables - Optional variables for the query.
   * @returns The typed response data (the `data` field from the GraphQL response).
   * @throws {ValidationError} When required parameters are missing or invalid.
   * @throws {GraphQLError} When the request fails at HTTP or GraphQL level.
   */
  async query<T>(query: string, variables?: Record<string, unknown>): Promise<T> {
    // Parameter validation
    if (typeof query !== 'string' || query.trim().length === 0) {
      throw new ValidationError(
        'The "query" parameter must be a non-empty string containing a GraphQL query.',
        'non-empty string',
        query === '' ? '(empty string)' : typeof query === 'string' ? `"${query.trim()}"` : String(query),
        'query',
      );
    }

    if (typeof this.baseURL !== 'string' || this.baseURL.trim().length === 0) {
      throw new ValidationError(
        'The "baseURL" must be a non-empty string. Ensure the GraphQL client was constructed with a valid endpoint URL.',
        'non-empty URL string',
        this.baseURL === '' ? '(empty string)' : String(this.baseURL),
        'baseURL',
      );
    }

    const operationExcerpt = this.extractOperationExcerpt(query);
    let lastError: GraphQLError | undefined;

    for (let attempt = 0; attempt <= this.retries; attempt++) {
      if (attempt > 0) {
        const delay = this.retryDelay * Math.pow(2, attempt - 1);
        console.error(
          `[GraphQLClient] [${new Date().toISOString()}] Retry attempt ${attempt}/${this.retries} for "${operationExcerpt}" — waiting ${delay}ms before next request.`,
        );
        await this.sleep(delay);
      }

      try {
        return await this.executeRequest<T>(query, variables, operationExcerpt);
      } catch (error) {
        if (error instanceof GraphQLError) {
          lastError = error;

          // Only retry on 5xx / server-side errors
          if (!this.isRetryableError(error)) {
            this.logFailure(operationExcerpt, variables, error);
            throw error;
          }

          // Log the retryable error details
          if (attempt < this.retries) {
            console.error(
              `[GraphQLClient] [${new Date().toISOString()}] Retryable error on attempt ${attempt + 1}/${this.retries + 1} for "${operationExcerpt}": ${error.message}`,
            );
          }
        } else {
          // Unexpected error — wrap and throw immediately (don't retry)
          const wrapped = new GraphQLError(
            `Unexpected error executing query "${operationExcerpt}": ${error instanceof Error ? error.message : String(error)}`,
            operationExcerpt,
            variables,
            undefined,
            undefined,
          );
          this.logFailure(operationExcerpt, variables, wrapped);
          throw wrapped;
        }
      }
    }

    // All retries exhausted
    const exhaustedError = lastError ?? new GraphQLError(
      `All ${this.retries + 1} attempts failed for query "${operationExcerpt}"`,
      operationExcerpt,
      variables,
    );
    this.logFailure(operationExcerpt, variables, exhaustedError);
    throw exhaustedError;
  }

  // ─── Private Helpers ────────────────────────────────────────────────────────

  /**
   * Execute a single GraphQL request (no retry logic).
   */
  private async executeRequest<T>(
    query: string,
    variables: Record<string, unknown> | undefined,
    operationExcerpt: string,
  ): Promise<T> {
    const response = await this.request.post(this.baseURL, {
      headers: {
        'Content-Type': 'application/json',
        ...this.extraHeaders,
      },
      data: { query, variables },
      timeout: this.timeout,
    });

    const status = response.status();
    let body: unknown;

    try {
      body = await response.json();
    } catch {
      // Response body is not valid JSON
      const textBody = await response.text().catch(() => '<unreadable>');
      throw new GraphQLError(
        `HTTP ${status} — invalid JSON response for query "${operationExcerpt}": ${textBody.slice(0, 200)}`,
        operationExcerpt,
        variables,
        undefined,
        { status, body: textBody },
      );
    }

    // Check for HTTP-level errors (4xx / 5xx)
    if (status >= 400) {
      const isRetryable = status >= 500;
      const responseExcerpt = JSON.stringify(body).slice(0, 1000);
      throw new GraphQLError(
        `HTTP ${status} ${isRetryable ? '(server error, retryable)' : '(client error, non-retryable)'} for query "${operationExcerpt}": ${responseExcerpt}`,
        operationExcerpt,
        variables,
        undefined,
        { status, body },
      );
    }

    // Check for GraphQL-level errors
    const graphqlBody = body as GraphQLResponseBody;
    if (graphqlBody.errors && graphqlBody.errors.length > 0) {
      const isRetryable = this.hasRetryableGraphQLError(graphqlBody.errors);
      const errorMessages = graphqlBody.errors
        .map((e) => e.message)
        .join('; ');

      throw new GraphQLError(
        `GraphQL error${isRetryable ? ' (retryable)' : ' (non-retryable)'} for query "${operationExcerpt}": ${errorMessages}`,
        operationExcerpt,
        variables,
        graphqlBody.errors,
        { status, body },
      );
    }

    // Return the data field
    if (graphqlBody.data === undefined || graphqlBody.data === null) {
      throw new GraphQLError(
        `GraphQL response has no "data" field for query "${operationExcerpt}"`,
        operationExcerpt,
        variables,
        undefined,
        { status, body },
      );
    }

    return graphqlBody.data as T;
  }

  /**
   * Determine if a GraphQLError is retryable (5xx HTTP or 5xx-class GraphQL errorType).
   */
  private isRetryableError(error: GraphQLError): boolean {
    // Check HTTP status in the response
    const resp = error.response as { status?: number } | undefined;
    if (resp?.status !== undefined && resp.status >= 500) {
      return true;
    }

    // Check GraphQL errors for 5xx errorType in extensions
    if (error.errors && Array.isArray(error.errors)) {
      return this.hasRetryableGraphQLError(error.errors as GraphQLResponseError[]);
    }

    return false;
  }

  /**
   * Check if any GraphQL error has a 5xx-class errorType in extensions.
   */
  private hasRetryableGraphQLError(errors: GraphQLResponseError[]): boolean {
    return errors.some((err) => {
      const errorType = err.extensions?.errorType;
      if (typeof errorType === 'string') {
        // Match errorType containing "5" indicating 5xx (e.g., "500", "503", "INTERNAL_SERVER_ERROR")
        return /^5\d{2}$/.test(errorType) || errorType.startsWith('5');
      }
      return false;
    });
  }

  /**
   * Extract a short excerpt from the query string for use in error messages.
   * Attempts to find the operation name, falls back to first 80 chars.
   */
  private extractOperationExcerpt(query: string): string {
    // Try to extract the operation name (e.g., "query GetBasket" -> "GetBasket")
    const match = query.match(/(?:query|mutation|subscription)\s+(\w+)/);
    if (match) {
      return match[1];
    }
    // Fall back to first 80 characters of the query, trimmed
    return query.replace(/\s+/g, ' ').trim().slice(0, 80);
  }

  /**
   * Log full details of a failed operation for debugging.
   * Includes timestamp, URL, operation, sanitized variables, HTTP status, and response body.
   */
  private logFailure(
    operation: string,
    variables: Record<string, unknown> | undefined,
    error: GraphQLError,
  ): void {
    const resp = error.response as { status?: number; body?: unknown } | undefined;
    console.error(`[GraphQLClient] [${new Date().toISOString()}] Request failed:`, {
      url: this.baseURL,
      operation,
      variables: this.sanitizeVariables(variables),
      status: resp?.status,
      responseBody: resp?.body,
      errors: error.errors,
      message: error.message,
    });
  }

  /**
   * Sanitize variables for logging — redact fields that could contain sensitive data
   * (tokens, passwords, keys) to avoid leaking PII in logs.
   */
  private sanitizeVariables(
    variables: Record<string, unknown> | undefined,
  ): Record<string, unknown> | undefined {
    if (!variables) return undefined;

    const sensitiveKeys = /password|token|secret|key|auth|credential|card/i;
    const sanitized: Record<string, unknown> = {};

    for (const [key, value] of Object.entries(variables)) {
      if (sensitiveKeys.test(key)) {
        sanitized[key] = '[REDACTED]';
      } else {
        sanitized[key] = value;
      }
    }

    return sanitized;
  }

  /**
   * Sleep for the given number of milliseconds.
   */
  private sleep(ms: number): Promise<void> {
    return new Promise((resolve) => setTimeout(resolve, ms));
  }
}
