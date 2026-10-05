'use client';

import { LOCALES } from '@whitbread-eos/api';
import {
  getCommonIcons,
  getCountryLanguageByLocale,
  TranslationProvider,
  getPayApplicationLabels,
  getInnBusinessHeaderLabels,
} from '@whitbread-eos/utils/server';
import { useParams } from 'next/navigation';
import { useEffect, useState } from 'react';

import { MaintenanceContent } from './maintenance-content';

export function Maintenance() {
  const params = useParams();
  const locale = (params?.locale as LOCALES) ?? LOCALES.EN;
  const { language } = getCountryLanguageByLocale(locale);
  const [icons, setIcons] = useState<Record<string, string> | undefined>(undefined);
  const [translations, setTranslations] = useState<Record<string, string> | undefined>(undefined);
  const [isLoadingTranslations, setIsLoadingTranslations] = useState(true);

  useEffect(() => {
    const fetchTranslations = async () => {
      try {
        const [payApplicationLabels, headerLabels] = await Promise.all([
          getPayApplicationLabels(language),
          getInnBusinessHeaderLabels(language),
        ]);
        if (payApplicationLabels && headerLabels) {
          setTranslations({ ...payApplicationLabels, ...headerLabels });
        } else {
          console.error('Failed to load translations');
        }
      } catch (err) {
        console.error('Failed to load translations', err);
      } finally {
        setIsLoadingTranslations(false);
      }
    };

    const fetchCommonIcons = async () => {
      try {
        const iconsData = await getCommonIcons(language);
        if (iconsData) {
          setIcons(iconsData);
        } else {
          console.error('Failed to load common icons');
        }
      } catch (err) {
        console.error('Failed to load common icons', err);
      }
    };

    fetchTranslations();
    fetchCommonIcons();
  }, [language]);

  const renderMaintenanceContent = () =>
    isLoadingTranslations ? null : (
      <TranslationProvider value={translations}>
        <MaintenanceContent locale={locale} icons={icons} />
      </TranslationProvider>
    );

  return renderMaintenanceContent();
}
