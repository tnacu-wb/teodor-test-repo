import { describe, it, expect } from 'vitest';
import { NextRequest, NextResponse } from 'next/server';
import { parseRequestBody, isErrorResponse } from '@/lib/api-utils';

const MAX_BODY_SIZE = 1 * 1024 * 1024; // keep in sync with api-utils

/**
 * Build a NextRequest with a JSON body. When `stripContentLength` is set the
 * content-length header is removed to emulate a chunked/streamed request.
 */
function makeRequest(body: string, opts: { stripContentLength?: boolean } = {}): NextRequest {
  const headers = new Headers({ 'content-type': 'application/json' });
  if (!opts.stripContentLength) {
    headers.set('content-length', String(Buffer.byteLength(body)));
  }
  return new NextRequest('https://tool.local/api/test', {
    method: 'POST',
    headers,
    body,
  });
}

async function readJson(res: NextResponse) {
  return (await res.json()) as { success: boolean; error: string; status: number };
}

describe('parseRequestBody', () => {
  it('parses a valid JSON object body', async () => {
    const result = await parseRequestBody(makeRequest(JSON.stringify({ hotelId: 'WHBPI' })));
    expect(isErrorResponse(result)).toBe(false);
    expect(result).toEqual({ hotelId: 'WHBPI' });
  });

  it('rejects a body that exceeds the size limit via content-length (413)', async () => {
    const big = JSON.stringify({ blob: 'x'.repeat(MAX_BODY_SIZE + 10) });
    const result = await parseRequestBody(makeRequest(big));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(413);
    expect(json.error).toContain('too large');
  });

  it('still rejects an oversized body when content-length is absent (chunked bypass)', async () => {
    // Regression: without content-length the fast-path check is skipped, so the
    // raw-length guard must catch it. Otherwise an arbitrarily large payload
    // would be parsed.
    const big = JSON.stringify({ blob: 'x'.repeat(MAX_BODY_SIZE + 10) });
    const result = await parseRequestBody(makeRequest(big, { stripContentLength: true }));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(413);
    expect(json.error).toContain('too large');
  });

  it('accepts a body just under the limit with no content-length', async () => {
    const body = JSON.stringify({ blob: 'x'.repeat(1000) });
    const result = await parseRequestBody(makeRequest(body, { stripContentLength: true }));
    expect(isErrorResponse(result)).toBe(false);
  });

  it('rejects invalid JSON (400)', async () => {
    const result = await parseRequestBody(makeRequest('{ not valid json'));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(400);
    expect(json.error).toContain('Invalid JSON');
  });

  it('rejects a JSON array (must be an object) (400)', async () => {
    const result = await parseRequestBody(makeRequest(JSON.stringify([1, 2, 3])));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(400);
    expect(json.error).toContain('must be a JSON object');
  });

  it('rejects a JSON primitive (400)', async () => {
    const result = await parseRequestBody(makeRequest('"just a string"'));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(400);
  });

  it('rejects an empty body (400)', async () => {
    const result = await parseRequestBody(makeRequest('', { stripContentLength: true }));
    expect(isErrorResponse(result)).toBe(true);
    const json = await readJson(result as NextResponse);
    expect(json.status).toBe(400);
  });
});
