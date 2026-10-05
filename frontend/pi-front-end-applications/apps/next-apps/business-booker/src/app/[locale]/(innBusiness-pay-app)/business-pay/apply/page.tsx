import {
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  PathParams,
  LOCALES,
  FT_IB_REDIRECT_AFTER_LOGIN,
  FT_ONE_TRUST_COOKIE_CONSENT,
  Scheme,
  SearchParams,
  WLCountryCode,
  ApplicationDetailsResponse,
  ApplicationAddress,
  AddressInfo,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_DD_STATUS_POLLING,
  PayApplicationStatus,
} from '@whitbread-eos/api';
import { Toaster, Analytics } from '@whitbread-eos/atoms/ui';
import { CookieConsentClientWrapper, Wizard, WizardHeader } from '@whitbread-eos/layout';
import {
  getServerUnleashToggles,
  getCountryLanguageByLocale,
  ID_TOKEN_COOKIE,
  CONSENT_COOKIE,
  getTranslations,
  TranslationProvider,
  formatIBAssetsUrl,
  getPathForLocale,
  getAppLookupData,
  getPayApplicationDetails,
  normalizeAddress,
  getUserDetails,
  getCompanyDetails,
  getInnBusinessHeaderLabels,
  getDetailsFromToken,
  isOneTrustCookieConsentActive,
} from '@whitbread-eos/utils/server';
import { cookies, headers } from 'next/headers';
import { redirect } from 'next/navigation';

import { AuthGuard } from '~components/innBusiness/AuthGuard/auth-guard';
import getPageUrlBeforeRedirectToLogin from '~utils/getPageUrlBeforeRedirectToLogin';

import ApplicationSent from './components/app-sent/app-sent';
import { AddPayAppCard } from './components/card-details/add-pay-app-card';
import { CardDetails } from './components/card-details/card-details';
import { CompanyDetailsAdditionalDetails } from './components/company-details/company-details-additional-details';
import { CompanyDetailsBusinessInfo } from './components/company-details/company-details-business-info';
import { CompanyDetailsBusinessType } from './components/company-details/company-details-business-type';
import { HeaderSteps } from './components/header-steps';
import { Landing } from './components/landing';
import { PaymentDetailsDirectDebit, PaymentDetails } from './components/payment-details';
import { Summary } from './components/summary';
import { PayApplicationState, PayApplicationStep } from './components/types';
import { YourDetails } from './components/your-details';

const PAGE_LABEL = 'IB | PAYAPP | Pay Application Apply';
const LOG_PAGE_NAME = 'pay-application-apply';

type Props = {
  params?: Promise<PathParams>;
  searchParams?: Promise<SearchParams>;
};

export default async function PayApplicationApply({ params, searchParams }: Props) {
  const resolvedParams = await params;
  const resolvedSearchParams = await searchParams;
  const locale = (resolvedParams?.locale?.toLowerCase() as LOCALES) ?? '';
  const headersList = await headers();
  if (!locale || !Object.values(LOCALES).includes(locale)) {
    redirect(`/${LOCALES.EN}/homepage`);
  }

  const flagsFallback = {
    [FT_DYNATRACE_RUM_COOKIE_CONSENT]: false,
    [FT_IB_PAY_PIBA_EURO]: false,
    [FT_IB_DD_STATUS_POLLING]: false,
    [FT_IB_REDIRECT_AFTER_LOGIN]: false,
    [FT_ONE_TRUST_COOKIE_CONSENT]: false,
  };
  const currentPath = headersList.get('WB-Url') ?? '';
  const toggles = await getServerUnleashToggles(PAGE_LABEL, flagsFallback, currentPath, {});
  const isRedirectAfterLoginEnabled = toggles?.[FT_IB_REDIRECT_AFTER_LOGIN] ?? false;

  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  if (!token) {
    getPageUrlBeforeRedirectToLogin(currentPath, locale, isRedirectAfterLoginEnabled);
  }
  const {
    accessLevel,
    isTravelManager,
    isBooker,
    isBusinessPayManager,
    profile: { employeeId },
    companyId,
  } = getDetailsFromToken(token);
  const logContext = {
    pageName: LOG_PAGE_NAME,
    userId: employeeId,
    companyId: companyId,
  };
  const scheme = (locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;
  const { language } = getCountryLanguageByLocale(locale);
  const [
    { t, translations },
    labels,
    userDetails,
    companyDetails,
    { title, timeTrading, estimatedMonthlySpend, tradingStyle },
  ] = await Promise.all([
    getTranslations(language, [
      'common',
      'payApplication',
      'company',
      'users',
      'profile',
      'cards',
      'icons',
    ]),
    getInnBusinessHeaderLabels(language),
    getUserDetails(token),
    getCompanyDetails(companyId, token, false, logContext),
    getAppLookupData(scheme, token),
  ]);
  const icons = translations?.['icons'] ?? {};

  if (!userDetails) {
    redirect(getPathForLocale(locale, 'error'));
  }

  if (!companyDetails) {
    redirect(getPathForLocale(locale, 'error'));
  }

  const mapAddress = (apiAddress?: ApplicationAddress): AddressInfo | null => {
    if (!apiAddress) {
      return null;
    }

    return {
      addressLine1: apiAddress.addressLine1 ?? '',
      addressLine2: apiAddress.addressLine2 ?? '',
      addressLine3: apiAddress.addressLine3 ?? '',
      addressLine4: apiAddress.addressLine4 ?? '',
      addressLine5: '',
      country: apiAddress.countryCode === WLCountryCode.GB ? 'GB' : 'DE',
      postCode: apiAddress.postcode ?? '',
    };
  };

  const mapDate = (apiDate?: string) => {
    if (!apiDate) {
      return null;
    }

    const date = new Date(apiDate);
    const day = date.getDate();
    const month = date.getMonth();
    const year = date.getFullYear();

    return {
      day: String(day),
      month: String(month + 1),
      year: String(year),
    };
  };

  const mapNumber = (apiNumber?: number) => {
    if (apiNumber === undefined) {
      return null;
    }

    return String(apiNumber);
  };

  const mapDynamicField = (apiValue?: string) => {
    if (!apiValue) {
      return null;
    }

    return { value: apiValue, displayValue: apiValue };
  };

  const { companyAddress, companyName } = companyDetails.requestedCompany.companyDetails;

  const nameOfCompany = companyName || userDetails?.contactDetail?.address?.companyName;

  const applicationGUID = resolvedSearchParams?.applicationGuid ?? '';
  const applicationId = resolvedSearchParams?.applicationId ?? '';

  const { title: userTitle, firstName, lastName, email } = userDetails.contactDetail;

  const { business } = userDetails;

  const normalizedCompanyAddress = normalizeAddress(companyAddress);

  let apiData: ApplicationDetailsResponse | null = null;
  if (applicationGUID && applicationId) {
    apiData = await getPayApplicationDetails(
      token,
      applicationGUID,
      applicationId,
      scheme,
      logContext
    );
  }

  if (apiData?.status === PayApplicationStatus.Cancelled) {
    redirect(getPathForLocale(locale.toLowerCase() as LOCALES, 'error'));
  }

  const initialState: PayApplicationState = {
    hostedPageGuid: apiData?.hostedPageGuid ?? '',
    directDebitOption: apiData?.directDebitOption ?? '',
    applicationGuid: apiData?.applicationGuid ?? '',
    applicationId: apiData?.applicationId ?? '',
    accountName: apiData?.accountName ?? nameOfCompany,
    scheme: (apiData?.scheme as Scheme) ?? scheme,
    isSubmitted: apiData?.status === PayApplicationStatus.Submitted,
    contactDetails: {
      title: apiData?.contactDetails?.title ?? '',
      foreName: apiData?.contactDetails?.foreName ?? '',
      lastName: apiData?.contactDetails?.lastName ?? '',
      position: apiData?.contactDetails?.position ?? '',
      telephone: apiData?.contactDetails?.telephone ?? '',
      mobile: apiData?.contactDetails?.mobile ?? '',
      email: apiData?.contactDetails?.email ?? '',
    },
    companyDetails: {
      registrationAddress:
        mapAddress(apiData?.companyDetails?.registrationAddress) ?? normalizedCompanyAddress,
      correspondenceAddress: mapAddress(apiData?.companyDetails?.correspondenceAddress),
      companyType: apiData?.companyDetails?.companyType ?? '',
      charityNumber: apiData?.companyDetails?.charityNumber ?? '',
      dateOfBirth: mapDate(apiData?.companyDetails?.partnerDetails?.dateOfBirth) ?? {
        day: '',
        month: '',
        year: '',
      },
      timeTradingId: mapDynamicField(apiData?.companyDetails?.timeTradingId) ?? '',
      nameOfEmployee: {
        titleEmployee: apiData?.companyDetails?.partnerDetails?.title ?? userTitle,
        firstNameEmployee: apiData?.companyDetails?.partnerDetails?.foreName ?? firstName,
        lastNameEmployee: apiData?.companyDetails?.partnerDetails?.lastName ?? lastName,
      },
      companyRegNum: apiData?.companyDetails?.companyRegNum ?? '',
      parentCompanyName: apiData?.companyDetails?.parentCompanyName ?? '',
      partnerDetails: {
        numberOfPartners:
          mapNumber(apiData?.companyDetails?.partnerDetails?.numberOfPartners) ?? '',
        title: apiData?.companyDetails?.partnerDetails?.title ?? '',
        foreName: apiData?.companyDetails?.partnerDetails?.foreName ?? '',
        lastName: apiData?.companyDetails?.partnerDetails?.lastName ?? '',
        dateOfBirth: mapDate(apiData?.companyDetails?.partnerDetails?.dateOfBirth) ?? {
          day: '',
          month: '',
          year: '',
        },
      },
      estMonthlySpend: apiData?.companyDetails?.estMonthlySpend ?? '',
      hotelBrandPolicy: apiData?.companyDetails?.hotelBrandPolicy ?? '',
    },
    cardDetails: apiData?.cardDetails?.length ? apiData.cardDetails : [],
    participants: apiData?.participants?.length ? apiData.participants : [],
  };

  const appInitiatorDetails = apiData?.participants?.find(
    (participant) => participant.initiator === true
  );

  const initiatorFullName =
    [initialState.contactDetails?.foreName, initialState.contactDetails?.lastName]
      .filter(Boolean)
      .join(' ') ||
    (appInitiatorDetails?.name?.toString().trim() ?? '') ||
    [firstName, lastName].filter(Boolean).join(' ');

  let initialStepId = PayApplicationStep.LANDING;
  if (apiData) {
    if (apiData.status === PayApplicationStatus.Submitted) {
      initialStepId = PayApplicationStep.APPLICATION_SENT;
    } else {
      initialStepId = apiData.resumeUrl
        ? (apiData.resumeUrl as PayApplicationStep)
        : PayApplicationStep.YOUR_DETAILS;
    }
  }

  const loggedEmployeeDetails = {
    employeeId: business?.employeeId ?? '',
    title: userTitle ?? '',
    firstName: firstName ?? '',
    lastName: lastName ?? '',
    emailAddress: email ?? '',
    employeeIdNumber: employeeId,
  };

  const normalizeEmail = (value?: string) => value?.trim().toLowerCase() ?? '';
  const isCurrentUserInitiator =
    !appInitiatorDetails?.email ||
    normalizeEmail(loggedEmployeeDetails?.emailAddress) ===
      normalizeEmail(appInitiatorDetails.email);

  const cookieConsent = cookieStore.get(CONSENT_COOKIE)?.value ?? '';
  const secureUrl = process?.env?.NEXT_PUBLIC_SECURE2_URL ?? '';
  const directDebitStatusCheckInterval = process?.env?.NEXT_PUBLIC_DD_STATUS_POLLING_INTERVAL;
  const isOneTrustActive = isOneTrustCookieConsentActive(
    toggles?.[FT_ONE_TRUST_COOKIE_CONSENT],
    locale
  );

  return (
    <TranslationProvider value={translations}>
      <Wizard<PayApplicationState>
        icons={icons}
        header={
          <WizardHeader
            logoUrl={formatIBAssetsUrl(t('common.content.header.image'))}
            steps={<HeaderSteps />}
          />
        }
        initialState={initialState}
        initialStepId={initialStepId}
        steps={[
          {
            id: PayApplicationStep.LANDING,
            component: (
              <Landing
                userDetails={userDetails}
                isPibaEuroEnabled={toggles?.[FT_IB_PAY_PIBA_EURO]}
              />
            ),
          },
          {
            id: PayApplicationStep.YOUR_DETAILS,
            component: (
              <YourDetails
                titleValues={JSON.parse(title)}
                userDetails={userDetails}
                isCurrentUserInitiator={isCurrentUserInitiator}
              />
            ),
          },
          {
            id: PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE,
            component: (
              <CompanyDetailsBusinessType
                icons={icons}
                locale={locale}
                companyType={tradingStyle}
              />
            ),
          },
          {
            id: PayApplicationStep.COMPANY_DETAILS,
            component: (
              <CompanyDetailsBusinessInfo
                icons={icons}
                timeTrading={timeTrading}
                titleValues={title}
              />
            ),
          },
          {
            id: PayApplicationStep.COMPANY_DETAILS_ADDITIONAL_DETAILS,
            component: (
              <CompanyDetailsAdditionalDetails
                icons={icons}
                estimatedMonthlySpend={estimatedMonthlySpend}
                locale={locale}
              />
            ),
          },
          {
            id: PayApplicationStep.CARD_DETAILS,
            component: (
              <CardDetails
                locale={locale}
                isTravelManager={isTravelManager}
                isBooker={isBooker}
                isBusinessPayManager={isBusinessPayManager}
                isCurrentUserInitiator={isCurrentUserInitiator}
                appInitiatorDetails={appInitiatorDetails}
              />
            ),
          },
          {
            id: PayApplicationStep.CARD_DETAILS_ADD_CARD,
            component: (
              <AddPayAppCard
                locale={locale}
                cardHolderName={initiatorFullName}
                companyId={companyId}
                language={language}
                calendarLabels={labels?.content?.form}
                accessLevel={accessLevel}
                loggedEmployeeDetails={loggedEmployeeDetails}
                isCurrentUserInitiator={isCurrentUserInitiator}
              />
            ),
          },
          {
            id: PayApplicationStep.PAYMENT_DETAILS,
            component: <PaymentDetails locale={locale} />,
          },
          {
            id: PayApplicationStep.PAYMENT_DETAILS_DIRECT_DEBIT,
            component: (
              <PaymentDetailsDirectDebit
                locale={locale}
                iframeUrl={process.env.NEXT_PUBLIC_WORLDLINE_DIRECT_DEBIT_URL!}
                isStatusPollingEnabled={toggles?.[FT_IB_DD_STATUS_POLLING]}
                statusCheckInterval={Number(directDebitStatusCheckInterval) || 5000}
              />
            ),
          },
          {
            id: PayApplicationStep.SUMMARY,
            component: <Summary locale={locale} />,
          },
          {
            id: PayApplicationStep.APPLICATION_SENT,
            component: <ApplicationSent />,
          },
          {
            id: PayApplicationStep.APPLICATION_SENT_FAILED,
            component: null,
          },
        ]}
      />
      <Toaster />
      <CookieConsentClientWrapper
        show={!cookieConsent && !isOneTrustActive}
        shouldSyncDynatraceConsent={!isOneTrustActive}
        isDynatraceRumCookieConsentEnabled={toggles?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false}
      />
      <Analytics userDetails={userDetails} />
      <AuthGuard secureUrl={secureUrl} locale={locale} />
    </TranslationProvider>
  );
}
