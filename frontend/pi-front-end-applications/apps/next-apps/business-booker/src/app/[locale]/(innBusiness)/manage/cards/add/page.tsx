import { PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getDetailsFromToken,
  ID_TOKEN_COOKIE,
  getCompanyDetails,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';

import { Analytics } from '../components/Analytics/Analytics';
import { AddEditCentrallyStoredCard } from '../components/CentrallyStored/AddCentrallyStoredCard/add-edit-centrally-stored-card';

type Props = {
  params?: Promise<PathParams>;
};

const LOG_PAGE_NAME = 'manage-cards-add';

export default async function CentrallyStoredAddCardPage({ params }: Props) {
  const resolvedParams = await params;
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId } = getDetailsFromToken(token);

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const [{ translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['cards', 'users', 'profile', 'company', 'icons']),
    getCompanyDetails(companyId, token, true, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};

  const { companyName, companyAddress } = companyDetails?.requestedCompany?.companyDetails ?? {};

  return (
    <TranslationProvider value={translations}>
      <AddEditCentrallyStoredCard
        icons={icons}
        language={language}
        initialAddress={companyAddress}
        companyName={companyName}
        token={token}
        employeeId={employeeId}
      />
      <Analytics track={'addNewCard'} />
    </TranslationProvider>
  );
}
