import { analytics, setPageAnalytics } from '@whitbread-eos/utils';
import { format } from 'date-fns';
import { useEffect } from 'react';

interface UseAppAnalyticsProps {
  language: string;
  pathname: string;
  query: Record<string, string | string[] | undefined>;
}

/**
 * Custom hook to handle analytics initialization and updates
 * Consolidates all analytics-related logic from _app.tsx
 */
export const useAppAnalytics = ({ language, pathname, query }: UseAppAnalyticsProps) => {
  const currentTime = format(new Date(), 'HH:mm');
  const currencyCode = language === 'en' ? 'gbp' : 'eur';
  const isWindowDefined = typeof window !== 'undefined';

  // Update analytics with language
  useEffect(() => {
    analytics.update({
      language,
    });
  }, [language]);

  // Update analytics page name and type on route change
  useEffect(() => {
    setPageAnalytics(pathname, 'PI', query, language);
  }, [pathname, query, language]);

  // Update analytics with current time and currency
  useEffect(() => {
    analytics.update({
      currentTime: currentTime,
      currencyCode: currencyCode,
    });
  }, [currentTime, currencyCode]);

  // Update analytics with page URL
  useEffect(() => {
    if (isWindowDefined) {
      analytics.update({
        pageURL: window.location.href,
      });
    }
  }, [isWindowDefined, pathname]); // Use pathname as dependency instead of window.location.href
};
