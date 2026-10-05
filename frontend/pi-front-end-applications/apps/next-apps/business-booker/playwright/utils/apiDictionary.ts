import { config } from '@WB-playwright/config';
import { basicAuth, aemAuth, AEM_BASE_URL } from '@WB-playwright/constants';
import * as request from 'superagent';

/**
 * Basic token
 */
export function getBasicToken(user: string, password: string) {
  const token = user + ':' + password;
  // Base64 Encoding
  const hash = Buffer.from(token).toString('base64');
  return `Basic ${hash}`;
}

/**
 * Fetch AEM hotel directory dictionary
 */
export async function fetchDictionary(relativeUrl: string) {
  const username = basicAuth.username;
  const password = aemAuth.password;
  const baseUrl = AEM_BASE_URL;
  const url = `${baseUrl}${relativeUrl}`;
  const authorization = password ? getBasicToken(username, password) : null;
  try {
    const response = await request.get(url).set('Authorization', authorization || '');
    return JSON.parse(response.text) || response.body;
  } catch (err) {
    console.log(err);
    throw err;
  }
}

/**
 * Fetch AEM label dictionary
 */
export async function fetchLabelsDictionary(language: string) {
  language = language || config.LANGUAGE;
  const relativePath = `/etc/designs/global/dictionaries/labels/i18n.jsondict.${language}`;
  return await fetchDictionary(relativePath);
}

/**
 * This dictionary contains generic information labels, not related to BART or Opera (ex: labels from home page)
 */
export async function fetchGenericDictionary(language: string, country: string) {
  const relativePath = `/${country}/${language}/index.header.data`;
  return await fetchDictionary(relativePath);
}

/**
 * Fetch Inn business layout dictionary from AEM
 */
export async function fetchInnBusinessLayoutDictionary(language: string) {
  const relativePath = `/etc/designs/global/dictionaries/innbusiness/common-layout/i18n.jsondict.${language}`;
  return await fetchDictionary(relativePath);
}

export async function fetchInnBusinessUserManagementDictionary(language: string) {
  const relativePath = `/etc/designs/global/dictionaries/innbusiness/user-management/i18n.jsondict.${language}`;
  return await fetchDictionary(relativePath);
}

export async function fetchInnBusinessCardManagementDictionary(language: string) {
  const relativePath = `/etc/designs/global/dictionaries/innbusiness/card-management/i18n.jsondict.${language}`;
  return await fetchDictionary(relativePath);
}
