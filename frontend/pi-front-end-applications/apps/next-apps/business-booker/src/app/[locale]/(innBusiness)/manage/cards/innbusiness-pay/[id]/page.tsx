import {
  PathParams,
  SearchParams,
  FT_IB_WL_CARD_EDIT,
  WorldlineMergedPreference,
} from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  getPIBACardDetails,
  ID_TOKEN_COOKIE,
  getSelectedAccountHolder,
  getInnBusinessHeaderLabels,
  getServerUnleashToggles,
  getPathForLocale,
  getDetailsFromToken,
  getEmployeesWithFilteringOptions,
  getCompanyDetails,
  getAccountList,
  getWorldlineUserPreferences,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { EditPIBACard } from './components/edit-piba-card';

type Props = {
  params?: Promise<{ id: string } & PathParams>;
  searchParams?: Promise<SearchParams>;
};

const LOG_PAGE_NAME = 'manage-cards-innbusiness-pay-detail';

export default async function EditWLCardPage({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';

  const { companyId, employeeId, isTravelManager, isBooker } = getDetailsFromToken(token);
  const hasEmployeesDataAccess = isTravelManager || isBooker;
  const headerList = await headers();
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const currentPath = headerList.get('WB-Url') ?? '';

  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };

  const [labels, { translations }, companyDetails, accounts, worldlinePreferences, toggles] =
    await Promise.all([
      getInnBusinessHeaderLabels(language),
      getTranslations(language, ['cards', 'icons', 'users', 'common', 'profile']),
      getCompanyDetails(companyId, token, true, logContext),
      getAccountList(token, {}, logContext),
      getWorldlineUserPreferences(token),
      getServerUnleashToggles(currentPath, {
        [FT_IB_WL_CARD_EDIT]: false,
      }),
    ]);
  const userPreferences =
    worldlinePreferences?.length > 0
      ? worldlinePreferences.find(
          (pref: WorldlineMergedPreference) =>
            pref.tetheredUserGuid === resolvedSearchParams?.account
        )
      : {};
  const sendCardsToCardholder = userPreferences?.preferenceDetails?.sendCardsToCardholder ?? false;
  const isWLCardEditEnabled = toggles?.[FT_IB_WL_CARD_EDIT];

  if (!isWLCardEditEnabled) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const selectedAccount = getSelectedAccountHolder(accounts, resolvedSearchParams?.account ?? '');

  const cardDetails = await getPIBACardDetails(
    token,
    resolvedParams?.id,
    resolvedSearchParams?.account,
    selectedAccount?.scheme,
    logContext
  );

  if (!cardDetails) {
    redirect(getPathForLocale(resolvedParams?.locale, 'error'));
  }

  const fallBackEmail = cardDetails?.registeredUsers?.[0]?.displayName?.match(/\(([^)]+)\)/)?.[1];
  const cardHolderEmail = cardDetails?.email || fallBackEmail;

  const searchEmployees =
    cardHolderEmail && !cardDetails?.myCard && hasEmployeesDataAccess
      ? await getEmployeesWithFilteringOptions(
          companyId,
          5,
          token,
          undefined,
          cardHolderEmail,
          '',
          undefined,
          false,
          false,
          logContext
        )
      : { employees: [{ emailAddress: cardHolderEmail ?? '' }] };

  const employeeDetails = searchEmployees?.employees?.[0];

  const icons = translations?.['icons'] ?? {};

  return (
    <TranslationProvider value={translations}>
      <EditPIBACard
        icons={icons}
        cardDetails={cardDetails}
        accountDetails={selectedAccount}
        calendarLabels={labels?.content?.form}
        language={language}
        employeeDetails={employeeDetails}
        companyDetails={companyDetails}
        sendCardsToCardholder={sendCardsToCardholder}
      />
    </TranslationProvider>
  );
}
