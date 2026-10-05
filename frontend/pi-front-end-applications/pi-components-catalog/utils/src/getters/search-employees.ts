import { QueryClient } from '@tanstack/react-query';
import getConfig from 'next/config';

import { axiosRequest } from '../hooks/use-request';
import { getAuthCookie, getLoggedInUserInfo } from './auth';

interface Props {
  awaitingApproval: boolean;
  bookingChannel: string;
  page: number;
  searchCriteria: string;
  size: number;
  queryClient: QueryClient;
}
export async function getListOfEmployees({
  awaitingApproval,
  bookingChannel,
  page,
  searchCriteria,
  size,
  queryClient,
}: Props) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const idTokenCookie = getAuthCookie();
  let listEmployees = [];

  if (idTokenCookie) {
    const { cdhEmployeeId, sessionId, cdhCompanyId } = getLoggedInUserInfo(idTokenCookie);

    if (cdhCompanyId && cdhEmployeeId && sessionId) {
      listEmployees = await queryClient.fetchQuery({
        queryKey: ['SearchEmployees', cdhCompanyId, searchCriteria],
        queryFn: () =>
          axiosRequest({
            method: 'GET',
            url: `${publicRuntimeConfig.NEXT_PUBLIC_REST_API}/companies/${cdhCompanyId}/employees?awaitingApproval=${awaitingApproval}&bookingChannel=${bookingChannel}&page=${page}&searchCriteria=${searchCriteria}&size=${size}`,
            headers: {
              Authorization: `Bearer ${idTokenCookie}`,
              'session-id': sessionId,
              'employee-id': cdhEmployeeId,
              'company-id': cdhCompanyId,
              bookingchannel: bookingChannel,
            },
          }),
      });
    }
  }

  return listEmployees?.employees ?? listEmployees; // Fix the issue DNRQ-66746
}
