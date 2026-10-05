'use client';

import { CompanyDetailsResponse } from '@whitbread-eos/api';
import getConfig from 'next/config';

import { getAuthCookie } from '../getters/auth';
import { useRestQueryRequest } from './use-request';

export default function useCompanyDetails(
  companyId: string,
  sessionId: string,
  employeeId: string,
  isLoggedIn: boolean,
  companyDetails?: CompanyDetailsResponse
) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const idTokenCookie = getAuthCookie();
  const params = {
    'session-id': sessionId,
    'employee-id': employeeId,
    'company-id': companyId,
    Authorization: `Bearer ${idTokenCookie}`,
  };

  const { data } = useRestQueryRequest(
    ['CompanyDetails', idTokenCookie],
    'GET',
    `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/company/${companyId}`,
    params,
    { enabled: isLoggedIn && !!companyId && !companyDetails }
  );

  const companyDetailsData = companyDetails ?? data;

  if (companyDetailsData?.requestedCompany?.companyDetails) {
    const { companyName, alternateCompanyName } =
      companyDetailsData.requestedCompany.companyDetails;
    if (companyName?.length && alternateCompanyName?.length) {
      companyDetailsData.requestedCompany.companyDetails.companyName = alternateCompanyName;
    }
  }
  return companyDetailsData;
}
