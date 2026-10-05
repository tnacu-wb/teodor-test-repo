'use client';

import React, { createContext, useState, useEffect, ReactNode, useContext } from 'react';

import noop from '../utils/noop';

interface DynamicObject {
  [key: string]: boolean;
}

export type FeatureToggleContextType = {
  featureToggles: DynamicObject;
  setFeatureToggles: React.Dispatch<React.SetStateAction<DynamicObject>>;
};
export const FeatureToggleContext = createContext<FeatureToggleContextType>({
  featureToggles: {},
  setFeatureToggles: noop,
});

export function useFeatureToggleData() {
  return useContext(FeatureToggleContext);
}

interface FeatureToggleContextProviderProps {
  children: ReactNode;
  defaultFeatureToggles?: Record<string, boolean>;
}

// Reads the `ftOverride` cookie directly in the browser (e.g. `flag1=true,flag2=false`).
// Needed because SSR fragments like opera-shared-page/header are fetched server-to-server
// by the host page, so the browser's cookies never reach getServerSideProps for those routes.
const UNSAFE_OVERRIDE_KEYS = new Set(['__proto__', 'constructor', 'prototype']);

export function getBrowserFtOverrides(): DynamicObject {
  const overrides: DynamicObject = {};
  const row = document.cookie.split(/;\s*/).find((entry) => entry.startsWith('ftOverride='));
  if (!row) {
    return overrides;
  }
  let ftOverrideCookie: string;
  try {
    ftOverrideCookie = decodeURIComponent(row.slice(row.indexOf('=') + 1));
  } catch {
    return overrides;
  }
  ftOverrideCookie.split(',').forEach((pair) => {
    const [rawKey, rawValue] = pair.split('=');
    const key = rawKey?.trim();
    const value = rawValue?.trim();
    if (key && value && !UNSAFE_OVERRIDE_KEYS.has(key)) {
      overrides[key] = value === 'true';
    }
  });
  return overrides;
}

export const FeatureToggleContextProvider = ({
  children,
  defaultFeatureToggles,
}: FeatureToggleContextProviderProps) => {
  const [featureToggles, setFeatureToggles] = useState(defaultFeatureToggles ?? {});

  useEffect(() => {
    const ftOverrides = getBrowserFtOverrides();
    if (Object.keys(ftOverrides).length === 0) {
      return;
    }
    setFeatureToggles((current) => {
      // Only override flags this page already computed, matching the same
      // per-page scoping the server applies via flagsWithFallback in unleash.ts
      const scopedOverrides = Object.keys(ftOverrides).reduce<DynamicObject>((acc, key) => {
        if (Object.prototype.hasOwnProperty.call(current, key)) {
          acc[key] = ftOverrides[key];
        }
        return acc;
      }, {});
      return Object.keys(scopedOverrides).length > 0 ? { ...current, ...scopedOverrides } : current;
    });
  }, []);

  useEffect(() => {
    // Update sessionStorage whenever featureToggles changes
    sessionStorage.setItem('featureToggles', JSON.stringify(featureToggles));
  }, [featureToggles]);

  return (
    <FeatureToggleContext.Provider value={{ featureToggles, setFeatureToggles }}>
      {children}
    </FeatureToggleContext.Provider>
  );
};
