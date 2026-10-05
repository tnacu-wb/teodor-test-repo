import { renderHook } from '@testing-library/react';
import React from 'react';

import { useTranslation, TranslationProvider } from './i18nClient';

const mockTranslations = {
  common: {
    label: 'testLabel',
  },
  cards: {
    label: 'testLabel',
  },
  contact: {
    label: 'testLabel',
  },
};

const createWrapper = (value: any) => {
  return function CreatedWrapper({ children }: { children: React.ReactNode }) {
    return <TranslationProvider value={value}>{children}</TranslationProvider>;
  };
};

describe('useTranslation', () => {
  it('should load labels for common namespace', () => {
    const { result } = renderHook(() => useTranslation(), {
      wrapper: createWrapper(mockTranslations),
    });
    const { t } = result.current;

    expect(t('label')).toBe('testLabel');
  });

  it('should load labels for cards namespace', () => {
    const { result } = renderHook(() => useTranslation('cards'), {
      wrapper: createWrapper(mockTranslations),
    });
    const { t } = result.current;

    expect(t('label')).toBe('testLabel');
  });

  it('should load labels for contact namespace', () => {
    const { result } = renderHook(() => useTranslation('contact'), {
      wrapper: createWrapper(mockTranslations),
    });
    const { t } = result.current;

    expect(t('label')).toBe('testLabel');
  });

  it('should load labels for multiple namespaces', () => {
    const { result } = renderHook(() => useTranslation(['common', 'cards']), {
      wrapper: createWrapper(mockTranslations),
    });
    const { t } = result.current;

    expect(t('common.label')).toBe('testLabel');
    expect(t('cards.label')).toBe('testLabel');
    expect(t('contact.label')).toBe('testLabel');
  });
});
