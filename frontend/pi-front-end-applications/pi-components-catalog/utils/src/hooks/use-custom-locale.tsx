'use client';

import { useRouter } from 'next/router';

export default function useCustomLocale() {
  const router = useRouter();
  const localeLanguage = router?.locale === 'gb' ? 'en' : 'de';

  const language = router?.locale ? localeLanguage : 'en';
  const country = language === 'en' ? 'gb' : 'de';

  return { country, language };
}
