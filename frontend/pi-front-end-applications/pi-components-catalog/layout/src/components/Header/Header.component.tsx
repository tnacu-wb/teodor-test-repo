'use client';

import {
  CountryInnB,
  BUSINESS_BOOKER_USER_ROLES,
  FormInnB,
  GlobalInnB,
  Customer,
  SearchRules,
  Scheme,
  PageName,
  LOCALES,
} from '@whitbread-eos/api';
import {
  Button,
  DropdownMenu,
  DropdownMenuContent,
  DropdownMenuTrigger,
  DropdownMenuItem,
} from '@whitbread-eos/atoms/ui';
import {
  getInitials,
  RolesRequired,
  useTranslation,
  formatIBAssetsUrl,
  cn,
  getAuthCookie,
  getPathForLocale,
  useGetCountryLanguage,
} from '@whitbread-eos/utils';
import { getLocaleByPathname } from '@whitbread-eos/utils/server';
import ExistingAccountModal from 'business-booker/src/components/innBusiness/ExistingAccountModal/ExistingAccountModal';
import InnBLink from 'business-booker/src/components/innBusiness/InnBLink/inn-b-link';
import { LinkAccountButton } from 'business-booker/src/components/innBusiness/LinkAccountButton';
import Image from 'next/image';
import { usePathname, useRouter } from 'next/navigation';
import { useState } from 'react';

import { BusinessSteps, BusinessStepType } from './BusinessSteps';
import LanguageMenu from './LanguageMenu';
import Logout from './Logout';
import Search from './Search';

interface AccountName {
  firstName: string;
  lastName: string;
}

interface HeaderProps {
  logoUrl: string;
  companyLabel: string;
  accountName: AccountName;
  languages: CountryInnB[];
  userRole: string;
  searchFormLabels: FormInnB | Record<string, never>;
  icons: Record<string, string>;
  globalLabels: GlobalInnB | Record<string, never>;
  businessStepType?: BusinessStepType;
  hideEditSearch?: () => void;
  logoOnly?: boolean;
  userDetails: Customer;
  searchRules: SearchRules;
  secureUrl?: string;
  onSearchButtonClick: (url: string) => void;
  isTethered?: boolean;
  token?: string;
  scheme?: Scheme;
  pageName?: string;
}

const Header = ({
  logoUrl,
  companyLabel,
  accountName,
  languages,
  userRole = '',
  searchFormLabels = {},
  icons,
  globalLabels,
  businessStepType,
  hideEditSearch = () => {
    return;
  },
  logoOnly,
  userDetails,
  searchRules,
  secureUrl = '',
  onSearchButtonClick,
  isTethered = false,
  token,
  scheme = 'GB' as Scheme,
  pageName,
}: Readonly<HeaderProps>) => {
  const idTokenCookie = token ?? getAuthCookie();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { language } = useGetCountryLanguage();
  const { t } = useTranslation(['common', 'notifications, layout']);
  const { firstName, lastName } = accountName;
  const accountInitials = getInitials(firstName, lastName);
  const [profileDropdownOpen, setProfileDropdownOpen] = useState(false);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const router = useRouter();
  const isGerman = locale === LOCALES.DE;
  const renderSearch = () => {
    return (
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
            formLabels={searchFormLabels}
            locale={locale}
            language={language}
            addRoomIcon={formatIBAssetsUrl(globalLabels?.addRoomIcon)}
            icons={icons}
            userRole={userRole}
            globalLabels={globalLabels}
            hideEditSearch={hideEditSearch}
            userDetails={userDetails}
            searchRules={searchRules}
            onSearchButtonClick={onSearchButtonClick}
          />
        )}
      </RolesRequired>
    );
  };

  const renderUserDetails = () => {
    return (
      <>
        <div className={rightContainerStyle}>
          <h1 className={companyLabelStyle} data-testid="Company-Name">
            {companyLabel}
          </h1>
          <LanguageMenu
            languages={languages}
            language={language}
            enIcon={formatIBAssetsUrl(
              languages?.find((lang) => lang?.code?.toLowerCase() === 'gb')?.flagUrl ?? ''
            )}
            deIcon={formatIBAssetsUrl(
              languages?.find((lang) => lang?.code?.toLowerCase() === 'de')?.flagUrl ?? ''
            )}
          />
          <DropdownMenu
            open={profileDropdownOpen}
            onOpenChange={(open: boolean) => setProfileDropdownOpen(open)}
          >
            <DropdownMenuTrigger asChild>
              <Button
                variant="secondary"
                className={
                  profileButtonStyle + ' focus-visible:ring-2 focus-visible:ring-primaryColor'
                }
                data-testid="Account-Menu-Button"
              >
                {accountInitials}
              </Button>
            </DropdownMenuTrigger>
            <DropdownMenuContent
              className={profileDropdownStyle}
              align="end"
              alignOffset={-5}
              sideOffset={5}
              avoidCollisions={true}
              collisionPadding={10}
              data-testid="Account-Menu-Dropdown"
              forceMount
            >
              {!isGerman && !isTethered && (
                <DropdownMenuItem
                  className="p-0"
                  onSelect={() => {
                    setProfileDropdownOpen(false);
                    setIsModalOpen(true);
                  }}
                >
                  <LinkAccountButton
                    className={linkAccountButtonStyle + ' bg-transparent'}
                    variant="alternativeDefault"
                    data-testid={`LinkAccount-Button`}
                    token={idTokenCookie ?? ''}
                    scheme={scheme}
                    setIsModalOpen={setIsModalOpen}
                    tabIndex={-1} // prevent focus, let menu item keep focus
                  >
                    {t('notifications.notification.account.exists.link.linkInnBusinessPayAccount')}
                  </LinkAccountButton>
                </DropdownMenuItem>
              )}
              <DropdownMenuItem
                className="p-0 block w-full h-full"
                onSelect={() => {
                  router.push(getPathForLocale(locale, 'profile'));
                }}
              >
                <InnBLink
                  href={getPathForLocale(locale, 'profile')}
                  className="block text-base px-4 py-2.5 hover:bg-lightGrey5"
                  data-testid="Account-Profile-Link"
                  onClick={() => setProfileDropdownOpen(false)}
                >
                  {t('common.content.authentication.myProfile')}
                </InnBLink>
              </DropdownMenuItem>
              <Logout
                isAuthActive={true}
                secureUrl={secureUrl}
                onLogoutClick={() => {
                  setProfileDropdownOpen(false);
                }}
              />
            </DropdownMenuContent>
          </DropdownMenu>
        </div>
        {isModalOpen && (
          <ExistingAccountModal
            isModalOpen={isModalOpen}
            locale={locale}
            onClose={() => setIsModalOpen(false)}
          />
        )}
      </>
    );
  };

  return (
    <header className={headerStyle}>
      <div className={cn(headerContainerStyle, businessStepType ? businessStepStyle : '')}>
        <InnBLink
          href={getPathForLocale(locale, 'homepage')}
          data-testid="IB-Logo"
          className={
            'focus-visible:ring-primaryColor rounded !ring-primaryColor outline-primaryColor'
          }
        >
          <Image
            src={logoUrl}
            alt={t('common.content.header.imageAlt')}
            className={logoStyle}
            width={160}
            height={48}
          />
        </InnBLink>

        {!logoOnly &&
          (businessStepType ? (
            <BusinessSteps type={businessStepType} />
          ) : (
            <>
              {renderSearch()}
              {renderUserDetails()}
            </>
          ))}
      </div>
    </header>
  );
};

export default Header;

const logoStyle = 'w-40 h-12 max-w-none';
const headerStyle =
  'fixed mobile:static top-0 flex flex-col justify-center px-[4.125rem] py-4 border-b border-lightGrey3 h-headerHeight w-full bg-background mobile:h-auto mobile:px-4 z-40';
const headerContainerStyle = 'flex items-center justify-between';
const businessStepStyle = 'mobile:flex-col mobile:items-start';
const rightContainerStyle = 'flex justify-end gap-4 items-center w-[30%] mobile:w-full';
const companyLabelStyle =
  'text-lg leading-6 font-medium mobile:hidden tablet:w-[9.375rem] tablet:line-clamp-2';
const profileButtonStyle =
  'focus-visible:ring-0 p-0 focus-visible:ring-offset-0 rounded-full w-11 h-11 bg-lightGrey5 rounded-full hover:bg-initial text-base text-darkGrey1 font-semibold shrink-0';
const profileDropdownStyle =
  'min-w-[12.5rem] shadow p-0 box-border border-lightGrey3 rounded mobile:min-w-[15rem] mobile:max-w-[calc(100vw-2rem)]';
const linkAccountButtonStyle =
  'text-darkGrey1 font-normal rounded-none border-0 py-1 border-b border-lightGrey3 block text-base px-4 hover:bg-lightGrey5 hover:border-lightGrey3 whitespace-normal text-left w-full';
