import { TextProps } from '@chakra-ui/react';
import { formatDate } from '@whitbread-eos/utils';

export enum SUBMIT_TYPE {
  SAVE = 'save',
  SUBMIT = 'submit',
}

export const cancelReturnStyles = {
  fontSize: '1rem',
  fontWeight: '600',
  color: 'var(--Greys---Dark-Grey-1, #333)',
  cursor: 'pointer',
  margin: '1.5rem 0',
  align: 'center',
  gap: '8px',
} as TextProps;

export interface PromoFormDetails {
  campaignName: string;
  operaPromoCode: string;
  isGeneric?: boolean;
  prefix?: string;
  genericPromoCode?: string;
  batchCount?: number;
  isLimitRedemptions?: boolean;
  maxRedemptionLimit?: number;
  expiryDate?: string | Date;
  codeLength: number;
  notes: string;
  hotelId: string;
  requestedBy: string;
  platformConfig: PlatformConfig;
}
export interface OnSubmitParams {
  data: PromoFormDetails;
  event: React.FormEvent<HTMLFormElement> | { preventDefault: () => void };
  userEmail?: string;
  createPromoMutation: {
    mutate: (payload: Record<string, any>) => void;
  };
}

export const onSubmitCreatePromotionForm = async ({
  data,
  event,
  userEmail,
  createPromoMutation,
}: OnSubmitParams) => {
  event.preventDefault();

  const expiryDateValue = data.expiryDate
    ? formatDate(data.expiryDate.toString(), 'yyyy-MM-dd')
    : '';
  const identifier = data.isGeneric ? data.genericPromoCode : data.prefix;
  const prefixAndCodeLength = identifier ? identifier.length + 10 : 10;
  const promotionsBatchData = {
    campaignName: data.campaignName,
    operaPromoCode: data.operaPromoCode,
    expiryDate: expiryDateValue,
    codeLength: prefixAndCodeLength,
    notes: 'notes',
    hotelId: data?.hotelId,
    requestedBy: userEmail || '',
    isMultiple: data?.isGeneric,
    prefix: data.isGeneric ? data.genericPromoCode : data.prefix,
    batchCount: data.isGeneric ? 1 : data.batchCount,
    maxRedemptionLimit: data.isLimitRedemptions ? data.maxRedemptionLimit : null,
    batchEligibilities: buildBatchEligibilities(data.platformConfig),
  };

  createPromoMutation.mutate(promotionsBatchData);
};

export const getPromoErrorMessage = (error: any): string | null => {
  const messageString = error?.response?.errors?.[0]?.message;
  if (!messageString) {
    return null;
  }
  try {
    const parsed = JSON.parse(messageString);
    return parsed?.errCode ?? null;
  } catch {
    return null;
  }
};

export type PlatformKey = 'PI' | 'CCUI' | 'PREMIER_INN_BUSINESS';
export type CountryKey = 'GB' | 'DE';

export interface PlatformItem {
  enabled: boolean;
  selected: { web: boolean; app: boolean };
}

export interface CountryPlatformConfig {
  enabled: boolean;
  platforms: Record<PlatformKey, PlatformItem>;
}

export type PlatformConfig = Record<CountryKey, CountryPlatformConfig>;

const emptyPlatformItem = (): PlatformItem => ({
  enabled: false,
  selected: { web: false, app: false },
});

const emptyCountryPlatformConfig = (): CountryPlatformConfig => ({
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

export const regFormInit = {
  campaignName: '',
  operaPromoCode: '',
  hotelId: '',
  isGeneric: false,
  prefix: '',
  genericPromoCode: '',
  batchCount: '',
  isLimitRedemptions: false,
  maxRedemptionLimit: '',
  expiryDate: '',
  platformConfig: defaultPlatformConfig,
};

export interface BatchEligibility {
  region: CountryKey;
  channel: string;
  platforms: string[];
}

const CHANNEL_CODE_MAP: Record<PlatformKey, string> = {
  PI: 'PI',
  CCUI: 'CCUI',
  PREMIER_INN_BUSINESS: 'BB',
};

const PLATFORM_CODE_MAP = {
  web: 'WEB',
  app: 'MOBILE',
} as const;

export const buildBatchEligibilities = (
  platformConfig: PlatformConfig | undefined
): BatchEligibility[] => {
  if (!platformConfig) return [];

  const countries = Object.keys(platformConfig) as CountryKey[];

  return countries.reduce<BatchEligibility[]>((acc, countryKey) => {
    const country = platformConfig[countryKey];
    if (!country?.enabled) return acc;

    const platformKeys = Object.keys(country.platforms) as PlatformKey[];

    platformKeys.forEach((platformKey) => {
      const platform = country.platforms[platformKey];
      if (!platform?.enabled) return;

      const selectedPlatforms = (Object.keys(platform.selected) as Array<'web' | 'app'>)
        .filter((option) => platform.selected[option])
        .map((option) => PLATFORM_CODE_MAP[option]);

      if (selectedPlatforms.length === 0) return;

      acc.push({
        region: countryKey,
        channel: CHANNEL_CODE_MAP[platformKey],
        platforms: selectedPlatforms,
      });
    });

    return acc;
  }, []);
};
