'use client';

import { useEffect, useState } from 'react';

import { BUNDLE_CHOICE_OPTIONS } from '../global-constants';

export default function useSoftBundles(
  featureFlag: boolean,
  cookieValue: string,
  rooms = 1,
  children = 0
) {
  const [isSoftBundlesVisible, setIsSoftBundlesVisible] = useState(false);

  useEffect(() => {
    const isEnabled =
      featureFlag &&
      rooms === 1 &&
      children === 0 &&
      (cookieValue === BUNDLE_CHOICE_OPTIONS.class ||
        cookieValue === BUNDLE_CHOICE_OPTIONS.rate ||
        cookieValue === BUNDLE_CHOICE_OPTIONS.roomOnly);

    setIsSoftBundlesVisible(isEnabled);
  }, [featureFlag, cookieValue, rooms, children]);

  return { isSoftBundlesVisible, softBundle: cookieValue };
}
