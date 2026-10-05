import { getNotificationsV2Query } from '@whitbread-eos/api';

import { GqlResponse } from '../..';
import { executeGraphQLQuery, ID_TOKEN_COOKIE } from '../../..';

// Dynamic import helper for next/headers to avoid bundling in client components
const getNextCookies = async () => {
  if (typeof window !== 'undefined') return null;
  try {
    const { cookies } = await import('next/headers');
    return cookies;
  } catch {
    return null;
  }
};

const getNotificationsV2 = async (authToken?: string | null) => {
  let token = authToken ?? '';

  if (authToken === undefined) {
    const cookiesFn = await getNextCookies();
    const cookieStore = cookiesFn ? await cookiesFn() : null;
    token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';
  }

  if (!token.trim()) {
    return null;
  }

  const response = await executeGraphQLQuery(
    getNotificationsV2Query(),
    {},
    (result: GqlResponse) => result?.data?.getNotificationsV2,
    token,
    true,
    true
  );

  return response;
};

export default getNotificationsV2;
