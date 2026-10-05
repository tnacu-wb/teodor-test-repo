import { NextRequest, NextResponse } from 'next/server';
import { Environment } from './ohip/environments';
import { validateEnvironment } from './ohip/validation';
import { ApiErrorResponse, ApiSuccessResponse, isAxiosError } from './types';

/** Maximum allowed request body size in bytes (1 MB). */
const MAX_BODY_SIZE = 1 * 1024 * 1024;

/** Default OHIP environment used when a request omits `environment`. */
const DEFAULT_ENVIRONMENT: Environment = 'UAT';

/**
 * Resolves the `environment` field from a parsed request body.
 * - Absent/empty → the default environment (UAT).
 * - A valid enum value → that environment.
 * - Any other value → a 400 error response (rather than a deep 500 later).
 *
 * Check the result with `isErrorResponse` before use.
 */
export function resolveEnvironment(value: unknown): Environment | NextResponse<ApiErrorResponse> {
  if (value === undefined || value === null || value === '') {
    return DEFAULT_ENVIRONMENT;
  }
  try {
    return validateEnvironment(value);
  } catch {
    return NextResponse.json(
      {
        success: false as const,
        error: `Invalid environment. Must be one of: UAT, SIT, PERF.`,
        status: 400,
        duration: '0ms',
        timestamp: new Date().toISOString(),
      },
      { status: 400 }
    );
  }
}

/**
 * Creates a standardised success response for API routes.
 */
export function successResponse<T>(data: T, env: Environment, startTime: number): NextResponse<ApiSuccessResponse<T>> {
  return NextResponse.json({
    success: true,
    data,
    environment: env,
    duration: `${Date.now() - startTime}ms`,
    timestamp: new Date().toISOString(),
  });
}

/**
 * Extracts a user-friendly error message from an Axios error or generic error.
 */
function extractErrorMessage(error: unknown): { message: string | Record<string, unknown>; status: number } {
  if (isAxiosError(error) && error.response) {
    const { status, data } = error.response;
    if (typeof data === 'object' && data !== null) {
      const detail = (data as Record<string, unknown>).detail;
      const title = (data as Record<string, unknown>).title;
      return { message: (detail as string) || (title as string) || (data as Record<string, unknown>), status };
    }
    return { message: (data as string) || 'Request failed', status };
  }

  if (error instanceof Error) {
    return { message: error.message, status: 500 };
  }

  return { message: 'An unexpected error occurred', status: 500 };
}

/**
 * Creates a standardised error response for API routes.
 */
export function errorResponse(error: unknown, startTime: number): NextResponse<ApiErrorResponse> {
  const { message, status } = extractErrorMessage(error);
  return NextResponse.json(
    {
      success: false as const,
      error: message,
      status,
      duration: `${Date.now() - startTime}ms`,
      timestamp: new Date().toISOString(),
    },
    { status }
  );
}

/**
 * Validates that required fields are present in a request body.
 * Returns an error response if validation fails, or null if all fields are present.
 */
export function validateRequiredFields(
  body: Record<string, unknown>,
  fields: string[]
): NextResponse<ApiErrorResponse> | null {
  const missing = fields.filter((f) => body[f] === undefined || body[f] === null || body[f] === '');
  if (missing.length > 0) {
    return NextResponse.json(
      {
        success: false as const,
        error: `Missing required fields: ${missing.join(', ')}`,
        status: 400,
        duration: '0ms',
        timestamp: new Date().toISOString(),
      },
      { status: 400 }
    );
  }
  return null;
}

/**
 * Parses and validates the request body with size limits.
 * Returns the parsed body or an error NextResponse.
 */
export async function parseRequestBody(
  request: NextRequest
): Promise<Record<string, unknown> | NextResponse<ApiErrorResponse>> {
  const tooLarge = () =>
    NextResponse.json(
      {
        success: false as const,
        error: `Request body too large. Maximum size is ${MAX_BODY_SIZE / 1024}KB.`,
        status: 413,
        duration: '0ms',
        timestamp: new Date().toISOString(),
      },
      { status: 413 }
    );

  // Fast path: reject when a content-length header already exceeds the limit.
  const contentLength = request.headers.get('content-length');
  if (contentLength && parseInt(contentLength, 10) > MAX_BODY_SIZE) {
    return tooLarge();
  }

  try {
    // Read the raw body so the size cap is enforced even for chunked/streamed
    // requests that omit content-length (which would otherwise bypass the check
    // above and force parsing of an arbitrarily large payload). Measure the
    // UTF-8 byte length (not string length) so multibyte payloads are capped
    // by their real size.
    const raw = await request.text();
    if (Buffer.byteLength(raw, 'utf8') > MAX_BODY_SIZE) {
      return tooLarge();
    }

    const body = raw.length === 0 ? undefined : JSON.parse(raw);
    if (typeof body !== 'object' || body === null || Array.isArray(body)) {
      return NextResponse.json(
        {
          success: false as const,
          error: 'Request body must be a JSON object.',
          status: 400,
          duration: '0ms',
          timestamp: new Date().toISOString(),
        },
        { status: 400 }
      );
    }
    return body as Record<string, unknown>;
  } catch {
    return NextResponse.json(
      {
        success: false as const,
        error: 'Invalid JSON in request body.',
        status: 400,
        duration: '0ms',
        timestamp: new Date().toISOString(),
      },
      { status: 400 }
    );
  }
}

/**
 * Checks if a value is an error NextResponse. Used to narrow the result of
 * helpers that return either a parsed value or an error response (e.g.
 * parseRequestBody, resolveEnvironment).
 */
export function isErrorResponse<T>(value: T | NextResponse<ApiErrorResponse>): value is NextResponse<ApiErrorResponse> {
  return value instanceof NextResponse;
}
