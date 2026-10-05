'use client';

import { useRouter } from 'next/router';

export default function useIsExternalSearch(thirdPartiesParam: string): boolean {
  const router = useRouter();
  if ((thirdPartiesParam?.trim() || '') === '') {
    return false;
  }

  const thirdParties = thirdPartiesParam.split(',');
  const cIdParam = router?.query?.CID as string;
  if (cIdParam) {
    const cId = cIdParam.split('_')[0];
    return thirdParties.includes(cId);
  }

  return false;
}
