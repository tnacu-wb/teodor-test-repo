import { PathParams, PaymentCardInfo } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getCompanyDetails,
  ID_TOKEN_COOKIE,
  getDetailsFromToken,
  getPathForLocale,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { redirect } from 'next/navigation';

import { Analytics } from '../components/Analytics/Analytics';
import { AddEditCentrallyStoredCard } from '../components/CentrallyStored/AddCentrallyStoredCard/add-edit-centrally-stored-card';

type Props = {
  params?: Promise<{ id: string } & PathParams>;
};

const LOG_PAGE_NAME = 'manage-cards-edit';

export default async function EditCDHCardPage({ params }: Props) {
  const resolvedParams = await params;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isTravelManager } = getDetailsFromToken(token);
  if (!isTravelManager) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);

  const [{ translations }, companyDetails] = await Promise.all([
    getTranslations(language, ['cards', 'users', 'profile', 'company', 'icons']),
    getCompanyDetails(companyId, token, true, logContext),
  ]);
  const icons = translations?.['icons'] ?? {};

  function findCardById(cards: PaymentCardInfo[], id = '') {
    return cards.find((card) => card.cardId === id);
  }

  const selectedCard = findCardById(
    companyDetails?.requestedCompany?.paymentDetails?.paymentCards,
    resolvedParams?.id
  );

  if (!selectedCard) {
    redirect(getPathForLocale(resolvedParams?.locale, 'manage/cards?tab=centrally-stored'));
  }

  const companyName = companyDetails?.requestedCompany?.companyDetails?.companyName;

  return (
    <TranslationProvider value={translations}>
      <AddEditCentrallyStoredCard
        icons={icons}
        language={language}
        initialAddress={
          selectedCard?.billingAddress ?? { addressLine1: '', postCode: '', country: '' }
        }
        companyName={companyName}
        token={token}
        employeeId={employeeId}
        cardDetails={selectedCard}
        companyId={companyId}
      />
      <Analytics
        cardUpdateId={resolvedParams?.id}
        cardType={selectedCard?.cardType}
        pageName={'Card Management: Card Details'}
        track={'editCards'}
      />
    </TranslationProvider>
  );
}
