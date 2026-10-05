/* eslint-disable prettier/prettier */
'use client';

import { createContext, useContext } from 'react';

import { translate } from './i18n';

type TranslationProviderProps = {
  value: any;
  children: React.ReactNode;
};

export const TranslationContext = createContext<any>(undefined);

export function TranslationProvider({ value, children }: TranslationProviderProps) {
  return <TranslationContext.Provider value={value}>{children}</TranslationContext.Provider>;
}

export function useTranslation(namespace: string | string[] = 'common') {
  const translations = useContext(TranslationContext);

  if (typeof namespace === 'string') {
    return {
      t: (key: string) => translate(translations, key, namespace),
    };
  }

  return {
    t: (key: string) => translate(translations, key),
  };
}
