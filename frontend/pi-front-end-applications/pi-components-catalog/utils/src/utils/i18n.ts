import { Language } from '@whitbread-eos/api';

import {
  getCardManagementLabels,
  getContactUsLabels,
  getInnBusinessHeaderLabels,
  getFooterLabels,
  getUserManagementLabels,
  getProfileManagementLabels,
  getCompanyManagementLabels,
  getHomepageLabels,
  getSpendingLabels,
  getAuthLabels,
  getPayApplicationLabels,
  getNotificationsLabels,
  getInnBusinessLayoutLabels,
  getCommonIcons,
} from '../server';

function getDictionary(language: Language, namespace: string) {
  switch (namespace) {
    case 'auth':
      return getAuthLabels(language);
    case 'common':
      return getInnBusinessHeaderLabels(language);
    case 'contact':
      return getContactUsLabels(language);
    case 'cards':
      return getCardManagementLabels(language);
    case 'footer':
      return getFooterLabels(language);
    case 'users':
      return getUserManagementLabels(language);
    case 'profile':
      return getProfileManagementLabels(language);
    case 'company':
      return getCompanyManagementLabels(language);
    case 'homepage':
      return getHomepageLabels(language);
    case 'spending':
      return getSpendingLabels(language);
    case 'payApplication':
      return getPayApplicationLabels(language);
    case 'notifications':
      return getNotificationsLabels(language);
    case 'layout':
      return getInnBusinessLayoutLabels(language);
    case 'icons':
      return getCommonIcons(language);
    default:
      return undefined;
  }
}

export function translate(translations: any, key: string, namespace?: string) {
  let value = translations;
  if (value && namespace) {
    value = translations[namespace];
  }

  if (value && typeof value[key] === 'string') {
    return value[key];
  }

  const tokens = key.split('.');

  while (value && tokens.length) {
    const token = tokens.shift();

    if (!token) {
      break;
    }

    value = value[token];
    const tokenKey = tokens.join('.');
    if (value && typeof value[tokenKey] === 'string') {
      return value[tokenKey];
    }
  }

  return value ?? key;
}

export async function useTranslation(language: Language, namespace: string | string[] = 'common') {
  const translations: Record<string, any> = {};

  if (typeof namespace === 'string') {
    translations[namespace] = await getDictionary(language, namespace);

    return {
      t: (key: string) => translate(translations, key, namespace),
      translations,
    };
  }

  const namespaces = await Promise.all(namespace.map((x) => getDictionary(language, x)));
  namespace.forEach((x, i) => {
    translations[x] = namespaces[i];
  });

  return {
    t: (key: string) => translate(translations, key),
    translations,
  };
}
