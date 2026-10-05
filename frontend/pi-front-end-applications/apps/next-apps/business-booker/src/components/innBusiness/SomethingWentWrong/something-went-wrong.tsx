'use client';

import { LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getPayApplicationLabels,
} from '@whitbread-eos/utils/server';
import { useParams } from 'next/navigation';
import { useCallback, useState, useEffect } from 'react';

import { SomethingWentWrongContent } from './something-went-wrong-content';

type Props = {
  className?: string;
  testId?: string;
  onRetry?: () => void;
};

export function SomethingWentWrong({
  className = '',
  testId = 'SomethingWentWrong',
  onRetry,
}: Props) {
  const params = useParams();
  const locale = (params?.locale as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const [translations, setTranslations] = useState<Record<string, string> | undefined>(undefined);
  const [isLoadingTranslations, setIsLoadingTranslations] = useState(true);

  useEffect(() => {
    const fetchTranslations = async () => {
      try {
        const payApplicationLabels = await getPayApplicationLabels(language);
        if (payApplicationLabels) {
          setTranslations(payApplicationLabels);
        } else {
          console.error('Failed to load translations');
        }
      } catch (err) {
        console.error('Failed to load translations', err);
      } finally {
        setIsLoadingTranslations(false);
      }
    };

    fetchTranslations();
  }, [language]);

  const handleRetry = useCallback(() => {
    if (onRetry) {
      onRetry();
      return;
    }

    if (typeof window !== 'undefined') {
      window.location.reload();
    }
  }, [onRetry]);

  const renderSomethingWentWrongContent = () =>
    isLoadingTranslations ? null : (
      <TranslationProvider value={translations}>
        <SomethingWentWrongContent
          className={className}
          testId={testId}
          handleRetry={handleRetry}
        />
      </TranslationProvider>
    );

  return renderSomethingWentWrongContent();
}
