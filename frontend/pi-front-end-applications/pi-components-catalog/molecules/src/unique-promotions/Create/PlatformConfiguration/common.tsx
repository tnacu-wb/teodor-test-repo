import {
  CountryKey,
  CountryPlatformConfig,
  PlatformConfig,
  PlatformItem,
  PlatformKey,
} from '../types';

export const COUNTRY_ROWS: { key: CountryKey; label: string }[] = [
  { key: 'GB', label: 'United Kingdom (GB)' },
  { key: 'DE', label: 'Germany (DE)' },
];

export const PLATFORM_ROWS: {
  key: PlatformKey;
  label: string;
  options: Array<'web' | 'app'>;
}[] = [
  { key: 'PI', label: 'PI', options: ['web', 'app'] },
  { key: 'CCUI', label: 'CCUI', options: ['web'] },
  { key: 'PREMIER_INN_BUSINESS', label: 'Premierinn Inn Business', options: ['web', 'app'] },
];

export const emptyPlatformItem = (): PlatformItem => ({
  enabled: false,
  selected: { web: false, app: false },
});

export const emptyCountryPlatformConfig = (): CountryPlatformConfig => ({
  enabled: false,
  platforms: {
    PI: emptyPlatformItem(),
    CCUI: emptyPlatformItem(),
    PREMIER_INN_BUSINESS: emptyPlatformItem(),
  },
});

export const defaultPlatformConfig: PlatformConfig = {
  GB: emptyCountryPlatformConfig(),
  DE: emptyCountryPlatformConfig(),
};

export const defaultCcuiPlatformItem = (): PlatformItem => ({
  enabled: true,
  selected: { web: true, app: false },
});

export const enabledCountryPlatformConfig = (): CountryPlatformConfig => ({
  enabled: true,
  platforms: {
    PI: emptyPlatformItem(),
    CCUI: defaultCcuiPlatformItem(),
    PREMIER_INN_BUSINESS: emptyPlatformItem(),
  },
});

export const toggleCountryEnabled = (
  value: PlatformConfig,
  country: CountryKey,
  enabled: boolean
): PlatformConfig => ({
  ...value,
  [country]: enabled ? enabledCountryPlatformConfig() : emptyCountryPlatformConfig(),
});

export const togglePlatformEnabled = (
  value: PlatformConfig,
  country: CountryKey,
  platform: PlatformKey,
  enabled: boolean
): PlatformConfig => {
  const currentCountry = value[country];

  const nextPlatform: PlatformItem = enabled
    ? { enabled: true, selected: { web: platform === 'CCUI' ? true : false, app: false } }
    : emptyPlatformItem();

  return {
    ...value,
    [country]: {
      ...currentCountry,
      platforms: {
        ...currentCountry.platforms,
        [platform]: nextPlatform,
      },
    },
  };
};

export const toggleSelection = (
  value: PlatformConfig,
  country: CountryKey,
  platform: PlatformKey,
  option: 'web' | 'app'
): PlatformConfig => {
  const currentCountry = value[country];
  const currentPlatform = currentCountry.platforms[platform];

  if (platform === 'CCUI') return value;

  return {
    ...value,
    [country]: {
      ...currentCountry,
      platforms: {
        ...currentCountry.platforms,
        [platform]: {
          ...currentPlatform,
          selected: {
            ...currentPlatform.selected,
            [option]: !currentPlatform.selected[option],
          },
        },
      },
    },
  };
};

export const isCountryPlatformValid = (country?: CountryPlatformConfig): boolean => {
  if (!country?.enabled) return true;

  const platformKeys = Object.keys(country.platforms ?? {}) as PlatformKey[];
  const anyPlatformEnabled = platformKeys.some((key) => country.platforms?.[key]?.enabled);
  if (!anyPlatformEnabled) return false;

  return platformKeys.every((key) => {
    const platform = country.platforms?.[key];
    if (!platform?.enabled) return true;
    return !!(platform.selected?.web || platform.selected?.app);
  });
};

export const isPlatformConfigValid = (value?: PlatformConfig): boolean => {
  if (!value) return false;
  const countries: CountryKey[] = ['GB', 'DE'];
  const anyCountryEnabled = countries.some((key) => value[key]?.enabled);
  if (!anyCountryEnabled) return false;

  return countries.every((key) => isCountryPlatformValid(value[key]));
};
