import { LOCALES, PathParams } from '@whitbread-eos/api';
import {
  getCountryLanguageByLocale,
  TranslationProvider,
  getTranslations,
  getUserDetails,
  ID_TOKEN_COOKIE,
  getEmployeeDetails,
  getDetailsFromToken,
  getCompanyRegistrationQuestionsAndAnswers,
} from '@whitbread-eos/utils/server';
import { cookies } from 'next/headers';
import { Suspense } from 'react';

import { Analytics } from './components/Analytics/Analytics';
import { EditContactPreferencesSection } from './components/ContactPreferences/edit-contact-preferences-section';
import { EditContactPreferencesSkeleton } from './components/ContactPreferences/edit-contact-preferences-skeleton';
import { ReviewChangesWrapper } from './components/ReviewChangesWrapper';

const PAGE_NAME = 'Profile Management';
const LOG_PAGE_NAME = 'profile';

type Props = {
  params?: Promise<PathParams>;
};

export default async function Profile({ params }: Props) {
  const resolvedParams = await params;
  const baseDataTestId = 'ProfilePage';
  const { language } = getCountryLanguageByLocale(resolvedParams?.locale);
  const cookieStore = await cookies();
  const token = cookieStore.get(ID_TOKEN_COOKIE)?.value ?? '';
  const { companyId, employeeId, isBusinessPayManager, isBusinessPayUser } =
    getDetailsFromToken(token);
  const logContext = { pageName: LOG_PAGE_NAME, userId: employeeId, companyId };
  const [{ t, translations }, profileDetails, employeeDetails, companyRegistrationQuestions] =
    await Promise.all([
      getTranslations(language, ['users', 'profile', 'company', 'icons', 'layout']),
      getUserDetails(token, true),
      getEmployeeDetails(companyId, employeeId ?? '', token, logContext),
      getCompanyRegistrationQuestionsAndAnswers(companyId ?? ''),
    ]);
  const icons = translations?.['icons'] ?? {};

  const isBusinessPayRole = isBusinessPayManager || isBusinessPayUser;
  const hasPaymentCard = !!profileDetails?.paymentPreference?.paymentCard?.cardNumber;
  const registrationQuestionsProps = {
    employeeDetails: employeeDetails,
    companyRegistrationQuestions: companyRegistrationQuestions,
    companyId: companyId ?? '',
  };
  const paymentTypeProps = {
    hasPaymentCard,
    profileDetails,
    token,
    employeeId: profileDetails.business.employeeId,
    language,
  };

  return (
    <TranslationProvider value={translations}>
      <div data-testid={`${baseDataTestId}-container`} className={pageStyle}>
        <h1 className={h1Style}>{t('profile.profile.myProfile')}</h1>
        <ReviewChangesWrapper
          icons={icons}
          baseDataTestId={baseDataTestId}
          locale={resolvedParams?.locale ?? LOCALES.EN}
          {...registrationQuestionsProps}
          {...paymentTypeProps}
          isBusinessPayRole={isBusinessPayRole}
        />
        <div className="mt-8">
          <Suspense fallback={<EditContactPreferencesSkeleton />}>
            <EditContactPreferencesSection locale={resolvedParams?.locale ?? 'en'} />
          </Suspense>
        </div>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </TranslationProvider>
  );
}

const h1Style = 'text-[2.5rem] font-black leading-[2.75rem] text-secondaryColor mobile:text-4xl';
const pageStyle =
  'px-12 pt-12 pl-[4.125rem] pr-[4.125rem] min-w-[700px] mobile:min-w-full mobile:px-4 w-full [@media(min-width:120rem)]:max-w-[81.75rem] [@media(min-width:120rem)]:mx-auto';
