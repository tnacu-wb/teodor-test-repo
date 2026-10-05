import { describe, it, expect } from '@jest/globals';

import { PlatformConfig } from '../types';
import {
  COUNTRY_ROWS,
  defaultPlatformConfig,
  emptyCountryPlatformConfig,
  emptyPlatformItem,
  isCountryPlatformValid,
  isPlatformConfigValid,
  PLATFORM_ROWS,
  toggleCountryEnabled,
  togglePlatformEnabled,
  toggleSelection,
} from './common';

const buildConfig = (overrides: Partial<PlatformConfig> = {}): PlatformConfig => ({
  ...defaultPlatformConfig,
  ...overrides,
});

describe('Platform configuration common utils', () => {
  it('COUNTRY_ROWS Lists GB and DE with there display labels', () => {
    expect(COUNTRY_ROWS).toEqual([
      { key: 'GB', label: 'United Kingdom (GB)' },
      { key: 'DE', label: 'Germany (DE)' },
    ]);
  });
  it('PLATFORM_ROWS List platforms rows with option sets ', () => {
    expect(PLATFORM_ROWS).toEqual([
      { key: 'PI', label: 'PI', options: ['web', 'app'] },
      { key: 'CCUI', label: 'CCUI', options: ['web'] },
      { key: 'PREMIER_INN_BUSINESS', label: 'Premierinn Inn Business', options: ['web', 'app'] },
    ]);
  });
  it('emptyPlatformItem returns disabled with no selections', () => {
    expect(emptyPlatformItem()).toEqual({
      enabled: false,
      selected: { web: false, app: false },
    });
  });

  it('emptyCountryPlatformConfig returns a disabled country with all three platforms empty', () => {
    const result = emptyCountryPlatformConfig();
    expect(result.enabled).toBe(false);
    expect(result.platforms).toEqual({
      PI: emptyPlatformItem(),
      CCUI: emptyPlatformItem(),
      PREMIER_INN_BUSINESS: emptyPlatformItem(),
    });
  });

  it('toggleCountryEnabled enables the country and preserves the other country untouched', () => {
    const value = buildConfig();
    const result = toggleCountryEnabled(value, 'GB', true);
    expect(result.GB.enabled).toBe(true);
    expect(result.DE).toEqual(value.DE);
  });

  it('toggleCountryEnabled resets the country to empty when disabled', () => {
    const value = buildConfig({
      GB: { enabled: true, platforms: emptyCountryPlatformConfig().platforms },
    });
    const result = toggleCountryEnabled(value, 'GB', false);
    expect(result.GB).toEqual(emptyCountryPlatformConfig());
  });

  it('toggleCountryEnabled does not mutate the original input', () => {
    const value = buildConfig();
    toggleCountryEnabled(value, 'GB', true);
    expect(value.GB.enabled).toBe(false);
  });

  it('togglePlatformEnabled auto-selects web when enabling CCUI', () => {
    const value = buildConfig();
    const result = togglePlatformEnabled(value, 'GB', 'CCUI', true);
    expect(result.GB.platforms.CCUI).toEqual({
      enabled: true,
      selected: { web: true, app: false },
    });
  });

  it('togglePlatformEnabled does not auto-select anything for a non-CCUI platform', () => {
    const value = buildConfig();
    const result = togglePlatformEnabled(value, 'GB', 'PI', true);
    expect(result.GB.platforms.PI).toEqual({ enabled: true, selected: { web: false, app: false } });
  });

  it('togglePlatformEnabled resets the platform to empty when disabled', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          PI: { enabled: true, selected: { web: true, app: true } },
        },
      },
    });
    const result = togglePlatformEnabled(value, 'GB', 'PI', false);
    expect(result.GB.platforms.PI).toEqual(emptyPlatformItem());
  });

  it('togglePlatformEnabled leaves sibling platforms in the same country untouched', () => {
    const value = buildConfig();
    const result = togglePlatformEnabled(value, 'GB', 'PI', true);

    expect(result.GB.platforms.CCUI).toEqual(emptyPlatformItem());
    expect(result.GB.platforms.PREMIER_INN_BUSINESS).toEqual(emptyPlatformItem());
  });

  it('toggleSelection returns the input unchanged for CCUI (early return)', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          CCUI: { enabled: true, selected: { web: true, app: false } },
        },
      },
    });
    const result = toggleSelection(value, 'GB', 'CCUI', 'web');
    expect(result).toBe(value);
  });

  it('toggleSelection toggles web from false to true for PI', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          PI: { enabled: true, selected: { web: false, app: false } },
        },
      },
    });
    const result = toggleSelection(value, 'GB', 'PI', 'web');
    expect(result.GB.platforms.PI.selected).toEqual({ web: true, app: false });
  });

  it('toggleSelection toggles app from false to true for Premierinn Inn Business', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          PREMIER_INN_BUSINESS: { enabled: true, selected: { web: false, app: false } },
        },
      },
    });
    const result = toggleSelection(value, 'GB', 'PREMIER_INN_BUSINESS', 'app');
    expect(result.GB.platforms.PREMIER_INN_BUSINESS.selected).toEqual({ web: false, app: true });
  });

  it('toggleSelection toggles a selected option back off', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          PI: { enabled: true, selected: { web: true, app: false } },
        },
      },
    });
    const result = toggleSelection(value, 'GB', 'PI', 'web');
    expect(result.GB.platforms.PI.selected).toEqual({ web: false, app: false });
  });

  it('isCountryPlatformValidreturns true when country is undefined', () => {
    expect(isCountryPlatformValid(undefined)).toBe(true);
  });

  it('isCountryPlatformValid returns true when country.enabled is false', () => {
    expect(isCountryPlatformValid(emptyCountryPlatformConfig())).toBe(true);
  });

  it('isCountryPlatformValid returns false when enabled but "platforms" is missing (defensive ?? {} branch)', () => {
    // @ts-expect-error - intentionally malformed to exercise the `?? {}` fallback
    expect(isCountryPlatformValid({ enabled: true })).toBe(false);
  });

  it('isCountryPlatformValid returns false when enabled but no platform inside it is enabled', () => {
    const country = { enabled: true, platforms: emptyCountryPlatformConfig().platforms };
    expect(isCountryPlatformValid(country)).toBe(false);
  });

  it('isCountryPlatformValid returns false when an enabled platform has no "selected" object at all (defensive ?. branch)', () => {
    const country = {
      enabled: true,
      platforms: {
        ...emptyCountryPlatformConfig().platforms,
        PI: { enabled: true, selected: undefined as any },
      },
    };
    expect(isCountryPlatformValid(country)).toBe(false);
  });

  it('isCountryPlatformValid returns false when an enabled platform has web=false and app=false', () => {
    const country = {
      enabled: true,
      platforms: {
        ...emptyCountryPlatformConfig().platforms,
        PI: { enabled: true, selected: { web: false, app: false } },
      },
    };
    expect(isCountryPlatformValid(country)).toBe(false);
  });

  it('isCountryPlatformValid returns true when an enabled platform has web=true', () => {
    const country = {
      enabled: true,
      platforms: {
        ...emptyCountryPlatformConfig().platforms,
        CCUI: { enabled: true, selected: { web: true, app: false } },
      },
    };
    expect(isCountryPlatformValid(country)).toBe(true);
  });

  it('isCountryPlatformValid returns true when a disabled platform sits alongside a valid enabled one', () => {
    const country = {
      enabled: true,
      platforms: {
        ...emptyCountryPlatformConfig().platforms,
        CCUI: { enabled: true, selected: { web: true, app: false } },
      },
    };
    expect(isCountryPlatformValid(country)).toBe(true);
  });

  it('isPlatformConfigValid returns false when value is undefined', () => {
    expect(isPlatformConfigValid(undefined)).toBe(false);
  });

  it('isPlatformConfigValid returns false when no country is enabled', () => {
    expect(isPlatformConfigValid(buildConfig())).toBe(false);
  });

  it('isPlatformConfigValid returns false when the only enabled country is invalid', () => {
    const value = buildConfig({
      GB: { enabled: true, platforms: emptyCountryPlatformConfig().platforms },
    });
    expect(isPlatformConfigValid(value)).toBe(false);
  });

  it('isPlatformConfigValid returns true when at least one enabled country is fully valid', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          CCUI: { enabled: true, selected: { web: true, app: false } },
        },
      },
    });
    expect(isPlatformConfigValid(value)).toBe(true);
  });

  it('isPlatformConfigValid returns false if one enabled country is valid but another enabled country is invalid', () => {
    const value = buildConfig({
      GB: {
        enabled: true,
        platforms: {
          ...emptyCountryPlatformConfig().platforms,
          CCUI: { enabled: true, selected: { web: true, app: false } },
        },
      },
      DE: { enabled: true, platforms: emptyCountryPlatformConfig().platforms },
    });
    expect(isPlatformConfigValid(value)).toBe(false);
  });
});
