import { PathParams } from '@whitbread-eos/api';
import {
  getCommonIcons,
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getDetailsFromToken,
  getPathForLocale,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { AddBulkEmployees } from '../components/AddBulkEmployees/index';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-employees-add-bulk' as const;

export default async function AddEmployeesBulk({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ translations }, icons] = await Promise.all([
    getTranslations(language, 'users'),
    getCommonIcons(language, logContext),
  ]);

  return (
    <TranslationProvider value={translations}>
      <AddBulkEmployees icons={icons} token={token} companyId={companyId} />
    </TranslationProvider>
  );
}
