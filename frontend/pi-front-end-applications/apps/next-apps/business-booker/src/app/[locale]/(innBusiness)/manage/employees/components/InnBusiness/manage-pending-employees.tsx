import { LOCALES, SearchEmployeeResponse } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getCommonIcons,
  ID_TOKEN_COOKIE,
  getEmployeesWithFilteringOptions,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import React from 'react';

import ManagePendingEmployeesClient from './manage-pending-employees-client';

interface Props {
  companyId: string;
  locale?: LOCALES;
  accessLevel?: string;
}

export async function ManagePendingEmployees({ locale, companyId, accessLevel }: Props) {
  const baseDataTestId = 'ManagePendingEmployees';
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { language } = getCountryLanguageByLocale(locale);
  const icons = await getCommonIcons(language);

  const pendingRequests: SearchEmployeeResponse = await getEmployeesWithFilteringOptions(
    companyId,
    0,
    token,
    1,
    undefined,
    undefined,
    undefined,
    true,
    false
  );

  return (
    <ManagePendingEmployeesClient
      employees={pendingRequests?.employees ?? []}
      locale={locale}
      token={token}
      icons={icons}
      baseDataTestId={baseDataTestId}
      accessLevel={accessLevel}
    />
  );
}
