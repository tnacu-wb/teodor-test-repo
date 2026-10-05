'use client';

import { useEffect } from 'react';

import { useFeatureToggleData } from '../store';

interface DynamicObject {
  [key: string]: boolean;
}

/**
 * Custom React hook for managing feature toggles.
 * @param {DynamicObject} [featToggles] - Optional object representing feature toggles
 * if passed it will update the featureToggle context and return featureToggles
 * if not it will just return feature toggles.
 * @returns {DynamicObject} An object representing feature toggles.
 * @example
 * // To update the feature toggles in context and get the new value
 * const defaultToggles = {
 *   feature1: true,
 *   feature2: false,
 * };
 * const featureToggles = useFeatureToggle(defaultToggles);
 *
 * // To just get the feature toggles without default feature toggles
 * const featureToggles = useFeatureToggle();
 */
export default function useFeatureToggle(featToggles?: DynamicObject): DynamicObject {
  const { featureToggles, setFeatureToggles } = useFeatureToggleData();

  useEffect(() => {
    if (featToggles) {
      setFeatureToggles({ ...featToggles });
    }
  }, [setFeatureToggles, featToggles]);

  return featureToggles;
}
