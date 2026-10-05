import { describe, it, expect, beforeEach, afterEach, vi } from 'vitest';
import { getEnvConfig } from '@/lib/ohip/environments';

describe('getEnvConfig', () => {
  beforeEach(() => {
    vi.stubEnv('UAT_OHIP_BASE_URL', 'https://uat.example.com');
    vi.stubEnv('UAT_OHIP_CLIENT_ID', 'test-client-id');
    vi.stubEnv('UAT_OHIP_CLIENT_SECRET', 'test-secret');
    vi.stubEnv('UAT_OHIP_APP_KEY', 'test-app-key');
    vi.stubEnv('UAT_OHIP_SCOPE', 'test-scope');
    vi.stubEnv('UAT_OHIP_ENTERPRISE_ID', 'WHBPI');
  });

  afterEach(() => {
    vi.unstubAllEnvs();
  });

  it('returns config when all required variables are set', () => {
    const config = getEnvConfig('UAT');
    expect(config.baseUrl).toBe('https://uat.example.com');
    expect(config.clientId).toBe('test-client-id');
    expect(config.clientSecret).toBe('test-secret');
    expect(config.appKey).toBe('test-app-key');
    expect(config.scope).toBe('test-scope');
    expect(config.enterpriseId).toBe('WHBPI');
  });

  it('throws when required variables are missing', () => {
    vi.stubEnv('SIT_OHIP_BASE_URL', '');
    vi.stubEnv('SIT_OHIP_CLIENT_ID', '');
    vi.stubEnv('SIT_OHIP_CLIENT_SECRET', '');
    vi.stubEnv('SIT_OHIP_APP_KEY', '');
    expect(() => getEnvConfig('SIT')).toThrow('not configured');
  });

  it('defaults scope and enterpriseId to empty string when not set', () => {
    vi.stubEnv('UAT_OHIP_SCOPE', '');
    vi.stubEnv('UAT_OHIP_ENTERPRISE_ID', '');
    const config = getEnvConfig('UAT');
    expect(config.scope).toBe('');
    expect(config.enterpriseId).toBe('');
  });
});
