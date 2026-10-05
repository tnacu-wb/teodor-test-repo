import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { validateEnvironmentConfig } from '@/lib/env-check';

describe('validateEnvironmentConfig', () => {
  const originalEnv = process.env;

  beforeEach(() => {
    vi.stubEnv('UAT_OHIP_BASE_URL', '');
    vi.stubEnv('UAT_OHIP_CLIENT_ID', '');
    vi.stubEnv('UAT_OHIP_CLIENT_SECRET', '');
    vi.stubEnv('UAT_OHIP_APP_KEY', '');
    vi.stubEnv('SIT_OHIP_BASE_URL', '');
    vi.stubEnv('SIT_OHIP_CLIENT_ID', '');
    vi.stubEnv('SIT_OHIP_CLIENT_SECRET', '');
    vi.stubEnv('SIT_OHIP_APP_KEY', '');
    vi.stubEnv('PERF_OHIP_BASE_URL', '');
    vi.stubEnv('PERF_OHIP_CLIENT_ID', '');
    vi.stubEnv('PERF_OHIP_CLIENT_SECRET', '');
    vi.stubEnv('PERF_OHIP_APP_KEY', '');
  });

  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it('returns invalid when no environments are configured', () => {
    const result = validateEnvironmentConfig();
    expect(result.valid).toBe(false);
    expect(result.configured).toHaveLength(0);
    expect(result.errors.length).toBeGreaterThan(0);
  });

  it('returns valid when UAT is fully configured', () => {
    vi.stubEnv('UAT_OHIP_BASE_URL', 'https://example.com');
    vi.stubEnv('UAT_OHIP_CLIENT_ID', 'client-id');
    vi.stubEnv('UAT_OHIP_CLIENT_SECRET', 'secret');
    vi.stubEnv('UAT_OHIP_APP_KEY', 'app-key');

    const result = validateEnvironmentConfig();
    expect(result.valid).toBe(true);
    expect(result.configured).toContain('UAT');
  });

  it('reports partial configuration as an error', () => {
    vi.stubEnv('SIT_OHIP_BASE_URL', 'https://example.com');
    // Missing CLIENT_ID, CLIENT_SECRET, APP_KEY — partial config

    const result = validateEnvironmentConfig();
    expect(result.errors.some((e) => e.includes('partially configured'))).toBe(true);
  });

  it('returns multiple configured environments', () => {
    vi.stubEnv('UAT_OHIP_BASE_URL', 'https://uat.example.com');
    vi.stubEnv('UAT_OHIP_CLIENT_ID', 'id');
    vi.stubEnv('UAT_OHIP_CLIENT_SECRET', 'secret');
    vi.stubEnv('UAT_OHIP_APP_KEY', 'key');
    vi.stubEnv('SIT_OHIP_BASE_URL', 'https://sit.example.com');
    vi.stubEnv('SIT_OHIP_CLIENT_ID', 'id');
    vi.stubEnv('SIT_OHIP_CLIENT_SECRET', 'secret');
    vi.stubEnv('SIT_OHIP_APP_KEY', 'key');

    const result = validateEnvironmentConfig();
    expect(result.valid).toBe(true);
    expect(result.configured).toContain('UAT');
    expect(result.configured).toContain('SIT');
  });
});
