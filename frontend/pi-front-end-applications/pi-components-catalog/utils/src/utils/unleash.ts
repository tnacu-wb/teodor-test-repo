import Cookies from 'cookies';
import { ReadonlyRequestCookies } from 'next/dist/server/web/spec-extension/adapters/request-cookies';
import { ParsedUrlQuery } from 'querystring';
import type { Unleash } from 'unleash-client';

import { ID_TOKEN_COOKIE } from '../global-constants';
import { logger } from '../logger/logger';
import getLoggedInUserInfo from './getLoggedInUserInfo';
import { TracingObject } from './tracing';

export interface DynamicObject {
  [key: string]: boolean;
}

export interface DynamicContext {
  [key: string]: string | boolean;
}

function getUnleashUserInfo(token: string, ccuiSession?: string) {
  const {
    name,
    profile: { sessionId, isBusiness, accessLevel, employeeId, companyId },
    cdhCompanyId,
    cdhEmployeeId,
    operaCompanyId,
  } = getLoggedInUserInfo(token);

  return {
    name: ccuiSession ?? name,
    sessionId,
    isBusiness,
    accessLevel,
    employeeId,
    companyId,
    cdhCompanyId,
    cdhEmployeeId,
    operaCompanyId,
  };
}

export async function getUnleashTogglesServerOrClient(
  cookies: Cookies | ReadonlyRequestCookies,
  label: string,
  flagsWithFallback: DynamicObject,
  url: string,
  query: ParsedUrlQuery | URLSearchParams,
  sessionTracing: TracingObject,
  context: DynamicContext = {},
  ccuiSession?: string
) {
  const flags: DynamicObject = {};
  const token = cookies.get(ID_TOKEN_COOKIE);
  const idTokenCookie = typeof token === 'string' ? token : (token?.value ?? '');
  const ftOverride = cookies.get('ftOverride');
  const ftOverrideCookie = typeof ftOverride === 'string' ? ftOverride : (ftOverride?.value ?? '');
  const ftCookieObj: DynamicObject = {};

  const userInfo = getUnleashUserInfo(idTokenCookie, ccuiSession);

  const unleashLogger = {
    label,
    pageLink: url,
    params: query,
    userInfo,
    flags,
    error: '',
    ...sessionTracing,
  };

  // Get the feature toggle override cookie used for testing
  if (ftOverrideCookie) {
    ftOverrideCookie.split(',').forEach((fs: string) => {
      const [key, value] = fs.split('=');
      if (key && value) {
        ftCookieObj[key] = value === 'true';
      }
    });
  }

  const userIdCookie = cookies.get('email');
  const userId = typeof userIdCookie === 'string' ? userIdCookie : (userIdCookie?.value ?? '');
  const unleashContext = {
    ...context,
    sessionId: sessionTracing['WB-SESSION-ID'], // needed for stickiness
    userId,
    environment: process.env.NEXT_ENV,
  };

  let client: Unleash | null = null;
  try {
    client = global?.unleashClient ?? null;
  } catch {
    unleashLogger.error = 'Unleash client not initialized, using fallback flags';
    logger.warn({ label: 'UNLEASH_NOT_INITIALIZED', msg: 'Using fallback flags' });
  }

  // Override cookie takes highest precedence (for testing); falls back to SDK or defaults
  Object.keys(flagsWithFallback).forEach((flag: string) => {
    if (flag in ftCookieObj) {
      flags[flag] = ftCookieObj[flag];
    } else if (client) {
      flags[flag] = client.isEnabled(flag, unleashContext);
    } else {
      flags[flag] = flagsWithFallback[flag];
    }
  });

  logger.info(unleashLogger);

  return flags;
}
