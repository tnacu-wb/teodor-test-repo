import { getAccountInfoQuery, Scheme } from '@whitbread-eos/api';

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

const getAccountInfo = async (scheme: Scheme, tetheredUserGuid: string) => {
  const cookiesFn = await getNextCookies();
  const cookieStore = cookiesFn ? await cookiesFn() : null;
  const token = cookieStore?.get(ID_TOKEN_COOKIE)?.value ?? '';
  const response = await executeGraphQLQuery(
    getAccountInfoQuery(),
    {
      scheme: scheme,
      tetheredUserId: tetheredUserGuid,
    },
    (result: GqlResponse) => result?.data?.getAccountInfo,
    token,
    true,
    false
  );

  return response;
};

export default getAccountInfo;
