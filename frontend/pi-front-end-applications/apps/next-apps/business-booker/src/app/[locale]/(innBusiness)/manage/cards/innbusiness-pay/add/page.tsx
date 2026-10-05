import {
  PathParams,
  LOCALES,
  SearchParams,
  FT_IB_COST_CENTRE_MANAGEMENT,
  WorldlineMergedPreference,
} from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  getTranslations,
  TranslationProvider,
  ID_TOKEN_COOKIE,
  getPathForLocale,
  getAccessLevel,
  getUserDetails,
  getInnBusinessHeaderLabels,
  getCostCenterDetails,
  getServerUnleashToggles,
  getWorldlineUserPreferences,
  getDetailsFromToken,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AccountSelector } from '../../../../homepage/components/index';
import { AddCard } from '../../components/InnBusinessPay';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

const LOG_PAGE_NAME = 'manage-cards-innbusiness-pay-add' as const;

export default async function InnBusinessPayAddCard({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const headerList = await headers();
  const {
    accessLevel,
    selectedAccount: { isAccountHolder, isCostCenterHolder },
  } = await getAccessLevel(resolvedSearchParams?.account);

  if (!(isAccountHolder || isCostCenterHolder)) {
    redirect(getPathForLocale(resolvedParams?.locale, 'homepage'));
  }

  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const currentPath = headerList.get('WB-Url') ?? '';
  const flagsFallback = {
    [FT_IB_COST_CENTRE_MANAGEMENT]: false,
  };

  const cookieStore = await cookies();

  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { employeeId, companyId: logCompanyId } = getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId: logCompanyId };
  const [labels, userDetails, { translations }, accountCostCenters, worldlinePreferences, toggles] =
    await Promise.all([
      getInnBusinessHeaderLabels(language),
      getUserDetails(token),
      getTranslations(language, ['cards', 'users', 'common', 'profile', 'icons']),
      getCostCenterDetails(token, resolvedSearchParams?.account as string, logContext),
      getWorldlineUserPreferences(token, logContext),
      getServerUnleashToggles(currentPath, flagsFallback),
    ]);
  const { contactDetail, companyId, business } = userDetails;
  const icons = translations?.['icons'] ?? {};
  const isCostCentreManagementEnabled = toggles[FT_IB_COST_CENTRE_MANAGEMENT];
  const userPreferences =
    worldlinePreferences?.length > 0
      ? worldlinePreferences.find(
          (pref: WorldlineMergedPreference) =>
            pref.tetheredUserGuid === resolvedSearchParams?.account
        )
      : {};
  const sendCardsToCardholder = userPreferences?.preferenceDetails?.sendCardsToCardholder ?? false;
  return (
    <TranslationProvider value={translations}>
      <AddCard
        isAccountHolder={isAccountHolder}
        costCenters={accountCostCenters}
        language={language}
        calendarLabels={labels?.content?.form}
        employeeId={business.employeeId}
        companyId={companyId}
        icons={icons}
        cardHolderName={`${contactDetail.firstName} ${contactDetail.lastName}`}
        accountHolderContent={
          <AccountSelector
            locale={resolvedParams?.locale ?? LOCALES.EN}
            parentDataTestId={'Add-Card-Account-Holder'}
            className={accountHolderStyle}
            hideDropdown={true}
          />
        }
        accessLevel={accessLevel}
        loggedEmployeeDetails={{
          employeeId: business?.employeeId ?? '',
          title: contactDetail.title ?? '',
          firstName: contactDetail.firstName ?? '',
          lastName: contactDetail.lastName ?? '',
          emailAddress: contactDetail.email ?? '',
          phoneNumber: contactDetail.telephone ?? '',
        }}
        isCostCentreManagementEnabled={isCostCentreManagementEnabled}
        sendCardsToCardholder={sendCardsToCardholder}
      />
    </TranslationProvider>
  );
}

const accountHolderStyle = 'mt-12';
