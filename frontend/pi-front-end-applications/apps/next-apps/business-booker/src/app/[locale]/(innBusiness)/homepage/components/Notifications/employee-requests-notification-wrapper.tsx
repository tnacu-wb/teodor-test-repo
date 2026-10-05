import { LOCALES } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  ID_TOKEN_COOKIE,
  getUserDetails,
  getEmployeesWithFilteringOptions,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { EmployeeRequestsNotification } from './employee-requests-notification';

type Props = {
  locale: LOCALES;
};

const LOG_PAGE_NAME = 'homepage';

export async function EmployeeRequestsNotificationWrapper({ locale }: Props) {
  const { language } = getCountryLanguageByLocale(locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId } = getDetailsFromToken(token) || {};
  const [{ t }, userDetails] = await Promise.all([
    getTranslations(language, ['notifications']),
    getUserDetails(token),
  ]);
  const isVisible = t('notifications.notification.employeeRequests.isVisible') === 'true';
  const { companyId } = userDetails || {};

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const filteredEmployees = await getEmployeesWithFilteringOptions(
    companyId,
    0,
    token,
    1,
    '',
    '',
    undefined,
    true,
    false,
    logContext
  );

  const { employees } = filteredEmployees || {};

  if (!isVisible || !(employees && employees?.length)) {
    return <></>;
  }

  return <EmployeeRequestsNotification locale={locale} count={employees.length} />;
}
