'use client';

import getConfig from 'next/config';

interface FeatureSwitchProps {
  featureSwitchKey: string;
  country?: string;
  fallbackValue?: boolean;
}

export default function useFeatureSwitch({
  featureSwitchKey,
  country,
  fallbackValue = false,
}: FeatureSwitchProps) {
  const { publicRuntimeConfig = {} } = getConfig() || {};

  if (country) {
    featureSwitchKey += `_${country.toUpperCase()}`;
  }

  const isFeatureEnabled = publicRuntimeConfig[featureSwitchKey]
    ? publicRuntimeConfig[featureSwitchKey].toLowerCase() === 'true'
    : fallbackValue;

  return isFeatureEnabled;
}
