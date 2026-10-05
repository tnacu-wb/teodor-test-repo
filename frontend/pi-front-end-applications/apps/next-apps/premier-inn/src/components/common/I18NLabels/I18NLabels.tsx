import { i18n } from 'next-i18next';
import { useRouter } from 'next/router';
import { useEffect } from 'react';

export default function I18NLabels() {
  const { locale } = useRouter();
  useEffect(() => {
    /* eslint-disable no-console */
    console.groupCollapsed(locale?.toLocaleUpperCase(), 'labels');
    console.log(JSON.stringify(i18n?.getDataByLanguage(locale ?? 'gb'), null, 2));
    console.groupEnd();
    /* eslint-enable no-console */
  }, [locale]);

  return null;
}
