'use client';

import {
  InnBusinessServerSideProps,
  BUSINESS_BOOKER_USER_ROLES,
  CustomerAccountDetails,
  PageName,
  FT_DYNATRACE_RUM_COOKIE_CONSENT,
  FT_ONE_TRUST_COOKIE_CONSENT,
} from '@whitbread-eos/api';
import { LiveAssistScriptEmbed } from '@whitbread-eos/atoms';
import { Toaster, Analytics } from '@whitbread-eos/atoms/ui';
import {
  Header,
  Sidebar,
  SidebarMobile,
  Main,
  BusinessStepType,
  Footer,
  EditSearch,
  Search,
  CookieConsentDialog,
  CookieConsentProvider,
} from '@whitbread-eos/layout';
import { CONSENT_COOKIE } from '@whitbread-eos/molecules';
import {
  FeatureToggleContextProvider,
  getCookie,
  useCustomLocaleAppRouter,
  formatIBAssetsUrl,
  TranslationProvider,
  cn,
  getDynatraceConsentPaths,
  getLocaleByPathname,
  isOneTrustCookieConsentActive,
  RolesRequired,
  syncDynatraceConsentFromCookie,
} from '@whitbread-eos/utils';
import { usePathname, useSearchParams } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { ReviewChangesModal } from '../ReviewChanges';
import {
  BusinessBookerPageContent,
  BusinessBookerPageContentProps,
} from './BusinessBookerPageContent';

export interface ExtendedServerSideProps extends InnBusinessServerSideProps {
  isTethered: boolean;
}
interface Props {
  children: React.ReactNode;
  serverSideProps: ExtendedServerSideProps;
  businessStepType?: BusinessStepType;
  showSidebar?: boolean;
  showFooter?: boolean;
  showEditSearch?: boolean;
  collapsedSidebar?: boolean;
  isBusinessBookerPage?: boolean;
  businessBookerPageOptions?: BusinessBookerPageContentProps;
  headerLogoOnly?: boolean;
  featureToggle?: Record<string, boolean>;
  secureUrl?: string;
  token?: string;
  serverConsentCookie?: boolean;
  pageName?: string;
  mainId?: string;
}

const InnBusinessLayout = ({
  children,
  serverSideProps,
  businessStepType,
  showSidebar = true,
  showFooter = false,
  showEditSearch = false,
  collapsedSidebar = false,
  isBusinessBookerPage = false,
  businessBookerPageOptions,
  headerLogoOnly = false,
  featureToggle = {},
  secureUrl = '',
  token,
  serverConsentCookie = undefined,
  pageName,
  mainId,
}: Readonly<Props>) => {
  const {
    labels,
    icons,
    cards,
    users,
    accounts,
    spending,
    userDetails,
    companyDetails,
    footer,
    language,
    searchRules,
    layout,
    notifications,
    isAccountHolder,
    isCardHolder,
    isTethered,
  } = serverSideProps;
  const translations = { common: labels, cards, footer, users, spending, layout, notifications };
  const companyType = companyDetails?.requestedCompany?.companyType;

  const [isEditSearchVisible, setIsEditSearchVisible] = useState(showEditSearch);
  const [isReviewChangesOpen, setIsReviewChangesOpen] = useState(false);
  const searchUrl = useRef('');
  const consentCookie =
    typeof serverConsentCookie === 'boolean'
      ? serverConsentCookie
      : (getCookie(CONSENT_COOKIE) ?? false);

  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const countryLanguage = useCustomLocaleAppRouter();
  const isOneTrustActive = isOneTrustCookieConsentActive(
    featureToggle?.[FT_ONE_TRUST_COOKIE_CONSENT],
    locale
  );
  const isDynatraceRumCookieConsentEnabled =
    featureToggle?.[FT_DYNATRACE_RUM_COOKIE_CONSENT] ?? false;

  useEffect(() => {
    if (isOneTrustActive) {
      return;
    }

    syncDynatraceConsentFromCookie({
      isEnabled: isDynatraceRumCookieConsentEnabled,
      paths: getDynatraceConsentPaths(countryLanguage.language, countryLanguage.country),
      domain: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    });
  }, [
    countryLanguage.country,
    countryLanguage.language,
    isDynatraceRumCookieConsentEnabled,
    isOneTrustActive,
  ]);

  const hideEditSearch = () => {
    setIsEditSearchVisible(false);
  };

  const handleSearchClick = (url: string) => {
    if (document.querySelector('div.form-page')) {
      searchUrl.current = url;
      setIsReviewChangesOpen(true);
      return;
    }

    window.location.href = url;
  };

  const searchParams = useSearchParams();

  let account =
    accounts?.find(
      (acc: CustomerAccountDetails) => acc.tetheredGuid === searchParams?.get('account')
    ) ?? null;
  if (!account && accounts?.length) {
    account = accounts?.[0];
  }

  const renderSearchMobile = () => {
    const userRole = userDetails?.business?.accessLevel;
    const globalLabels = labels?.content?.global || {};
    return (
      !isEditSearchVisible && (
        <div className={searchContainerStyle}>
          <RolesRequired
            userRole={userRole}
            requiredRoles={[
              BUSINESS_BOOKER_USER_ROLES.SUPER,
              BUSINESS_BOOKER_USER_ROLES.BOOKER,
              BUSINESS_BOOKER_USER_ROLES.SELF,
            ]}
          >
            {pageName !== PageName.PRICE_FINDER && (
              <Search
                mobile={true}
                formLabels={labels.content.form}
                locale={locale}
                language={language ?? 'en'}
                icons={icons}
                addRoomIcon={formatIBAssetsUrl(globalLabels?.addRoomIcon)}
                userRole={userRole}
                globalLabels={globalLabels}
                hideEditSearch={hideEditSearch}
                userDetails={userDetails}
                searchRules={searchRules}
                onSearchButtonClick={handleSearchClick}
              />
            )}
          </RolesRequired>
        </div>
      )
    );
  };

  return (
    <>
      <TranslationProvider value={translations}>
        <FeatureToggleContextProvider defaultFeatureToggles={featureToggle}>
          <Header
            logoUrl={formatIBAssetsUrl(labels.content.header.image)}
            companyLabel={companyDetails?.requestedCompany?.companyDetails?.companyName}
            accountName={{
              firstName: userDetails.contactDetail.firstName,
              lastName: userDetails.contactDetail.lastName,
            }}
            languages={labels.content.countries}
            userRole={userDetails.business.accessLevel}
            searchFormLabels={labels.content.form}
            icons={icons}
            globalLabels={labels.content.global}
            businessStepType={businessStepType}
            hideEditSearch={hideEditSearch}
            logoOnly={headerLogoOnly}
            userDetails={userDetails}
            searchRules={searchRules}
            secureUrl={secureUrl}
            onSearchButtonClick={handleSearchClick}
            isTethered={serverSideProps.isTethered}
            token={token}
            scheme={account?.scheme}
            pageName={pageName}
          />

          {showSidebar && (
            <Sidebar
              userRole={userDetails.business.accessLevel}
              isAccountHolder={isAccountHolder}
              isCardHolder={isCardHolder}
              menuLabels={labels.layout.menu || {}}
              icons={icons}
              collapsed={collapsedSidebar}
              accounts={accounts}
              companyType={companyType}
              isTethered={isTethered}
            />
          )}

          <Main
            id={mainId}
            hasSidebar={showSidebar}
            collapsedSidebar={collapsedSidebar}
            className={cn(
              isBusinessBookerPage && businessBookerPageStyle,
              isBusinessBookerPage &&
                businessBookerPageOptions?.isHotelDetailsPage &&
                hotelDetailsPageStyle
            )}
          >
            {!businessStepType && (
              <>
                {renderSearchMobile()}
                {isEditSearchVisible && pageName !== PageName.PRICE_FINDER && (
                  <EditSearch
                    searchIcon={formatIBAssetsUrl(labels.content.form.searchIcon)}
                    handleEditClick={hideEditSearch}
                    language={language ?? 'en'}
                  />
                )}
              </>
            )}
            {children}
            {showFooter && <Footer />}
          </Main>

          {showSidebar && (
            <SidebarMobile
              userRole={userDetails.business.accessLevel}
              isAccountHolder={isAccountHolder}
              isCardHolder={isCardHolder}
              isTethered={isTethered}
              menuLabels={labels.layout.menu || {}}
              icons={icons}
              accounts={accounts}
              companyType={companyType}
            />
          )}

          <ReviewChangesModal
            testId="ReviewChangesSearch"
            open={isReviewChangesOpen}
            onOpenChange={() => setIsReviewChangesOpen(false)}
            onDiscard={() => {
              setIsReviewChangesOpen(false);
              window.location.href = searchUrl.current;
            }}
            onContinue={() => setIsReviewChangesOpen(false)}
          />
        </FeatureToggleContextProvider>
      </TranslationProvider>

      {isBusinessBookerPage && <BusinessBookerPageContent {...businessBookerPageOptions} />}
      {!isBusinessBookerPage && <LiveAssistScriptEmbed isInnBusinessContext={true} />}
      {!consentCookie && !isBusinessBookerPage && !isOneTrustActive && (
        <CookieConsentProvider countryLanguageResolver={() => countryLanguage}>
          <CookieConsentDialog
            isDynatraceRumCookieConsentEnabled={isDynatraceRumCookieConsentEnabled}
          />
        </CookieConsentProvider>
      )}

      <Toaster />
      {!isBusinessBookerPage && (
        <Analytics userDetails={userDetails} pathName={pathname} searchParams={searchParams} />
      )}
    </>
  );
};

export default InnBusinessLayout;

const businessBookerPageStyle = 'items-center';
const hotelDetailsPageStyle = 'pt-2 mobile:pt-0';
const searchContainerStyle =
  'hidden mobile:block mobile:w-full px-[1rem] bg-lightGrey5 border-b-[1px] border-solid border-lightGrey3';
