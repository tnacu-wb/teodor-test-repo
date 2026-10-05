import { describe, it, expect } from 'vitest';
import { NextResponse } from 'next/server';
import { resolveEnvironment, isErrorResponse } from '@/lib/api-utils';

async function status(res: NextResponse) {
  return (await res.json()) as { success: boolean; error: string; status: number };
}

describe('resolveEnvironment', () => {
  it('defaults to UAT when environment is absent', () => {
    expect(resolveEnvironment(undefined)).toBe('UAT');
    expect(resolveEnvironment(null)).toBe('UAT');
    expect(resolveEnvironment('')).toBe('UAT');
  });

  it('returns the valid environment as-is', () => {
    expect(resolveEnvironment('UAT')).toBe('UAT');
    expect(resolveEnvironment('SIT')).toBe('SIT');
    expect(resolveEnvironment('PERF')).toBe('PERF');
  });

  it('returns a 400 error response for an invalid truthy value (not a 500 later)', async () => {
    const result = resolveEnvironment('PROD');
    expect(isErrorResponse(result)).toBe(true);
    const json = await status(result as NextResponse);
    expect(json.status).toBe(400);
    expect(json.error).toContain('Invalid environment');
  });

  it('rejects non-string values', async () => {
    for (const bad of [123, {}, [], true]) {
      const result = resolveEnvironment(bad);
      expect(isErrorResponse(result)).toBe(true);
      expect((result as NextResponse).status).toBe(400);
    }
  });

  it('is case-sensitive (lowercase is rejected)', async () => {
    const result = resolveEnvironment('uat');
    expect(isErrorResponse(result)).toBe(true);
    expect((result as NextResponse).status).toBe(400);
  });
});
