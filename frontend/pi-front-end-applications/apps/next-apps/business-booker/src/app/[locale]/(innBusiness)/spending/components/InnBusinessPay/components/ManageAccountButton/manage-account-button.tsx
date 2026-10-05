'use client';

import {
  CountryCode,
  CustomerAccountDetails,
  FT_IB_PAY_PIBA_EURO,
  LOCALES,
  RegistrationRole,
  Scheme,
} from '@whitbread-eos/api';
import { Button, WorldlineLink } from '@whitbread-eos/atoms/ui';
import {
  getPathForLocale,
  useFeatureToggle,
  useTranslation,
  cn,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { ChevronDown, Ellipsis, SquareArrowOutUpRight } from 'lucide-react';
import Link from 'next/link';
import React, { useState } from 'react';

import { ExistingAccountModal } from '~components/innBusiness/ExistingAccountModal';
import { LinkAccountButton } from '~components/innBusiness/LinkAccountButton';
import {
  ResponsiveDropdown,
  ReponsiveDropdownLinkWrapper,
} from '~components/innBusiness/ResponsiveDropdown';

interface Props {
  isTravelManager: boolean;
  locale: LOCALES;
  account: CustomerAccountDetails | null;
  wlReturnUrl?: string;
  wlPostUrl?: string;
  isAccountSuspended?: boolean;
  token: string;
  isBusinessPayManager?: boolean;
}

export default function ManageAccountButton({
  isTravelManager,
  locale,
  account,
  wlPostUrl,
  wlReturnUrl,
  isAccountSuspended = false,
  token,
  isBusinessPayManager = false,
}: Props) {
  const roles = account?.registrationRoles ?? [];
  const baseDataTestId = 'ManageAccountButton';
  const { [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled } = useFeatureToggle();
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [isDropdownOpen, setIsDropdownOpen] = useState(false);

  const { t } = useTranslation('spending');
  const { language } = getCountryLanguageByLocale(locale);
  const isDeLanguage = language === CountryCode.DE;

  const shouldRenderMemorableWordLink =
    roles.includes(RegistrationRole.AccountHolder) ||
    roles.includes(RegistrationRole.FinanceUser) ||
    roles.includes(RegistrationRole.CardHolder) ||
    roles.includes(RegistrationRole.CostCentreUser);
  const shouldRenderEditAccountLink = roles.includes(RegistrationRole.AccountHolder);

  const isTravelOrBusinessPayManager = isTravelManager || isBusinessPayManager;
  const shouldRenderManageEmployeesLink =
    isTravelOrBusinessPayManager && roles.includes(RegistrationRole.AccountHolder);

  const shouldRenderManageCardsLink =
    roles.includes(RegistrationRole.AccountHolder) ||
    roles.includes(RegistrationRole.CardHolder) ||
    roles.includes(RegistrationRole.CostCentreUser);

  const shouldRenderViewOffersLink = roles.includes(RegistrationRole.AccountHolder);
  const shouldRenderLinkAccountLink =
    roles.includes(RegistrationRole.AccountHolder) ||
    roles.includes(RegistrationRole.FinanceUser) ||
    roles.includes(RegistrationRole.CardHolder);

  const shouldRenderApplyLink =
    isTravelOrBusinessPayManager &&
    (roles.includes(RegistrationRole.AccountHolder) ||
      roles.includes(RegistrationRole.FinanceUser) ||
      roles.includes(RegistrationRole.CardHolder) ||
      roles.includes(RegistrationRole.CostCentreUser)) &&
    !(isDeLanguage && !isPibaEuroEnabled);

  const shouldRenderManageGroup =
    shouldRenderEditAccountLink ||
    shouldRenderManageEmployeesLink ||
    shouldRenderManageCardsLink ||
    shouldRenderViewOffersLink;
  const shouldRenderApplyGroup = shouldRenderLinkAccountLink || shouldRenderApplyLink;

  const trackMemorableWord = () => {
    window?._satellite?.track('memorableWord');
  };

  const renderLinks = () => {
    return (
      <>
        {shouldRenderMemorableWordLink && (
          <div className={groupItemsStyle}>
            <ReponsiveDropdownLinkWrapper>
              <Link
                href={getPathForLocale(
                  locale,
                  `spending/memorable-word${
                    account?.tetheredGuid ? `?account=${account?.tetheredGuid}` : ''
                  }`
                )}
                className={linkStyle}
                onClick={trackMemorableWord}
              >
                {t('spending.manage.account.memorable.word.setOrReset')}
              </Link>
            </ReponsiveDropdownLinkWrapper>
          </div>
        )}

        {shouldRenderManageGroup && (
          <div className={groupItemsStyle}>
            {shouldRenderEditAccountLink && (
              <ReponsiveDropdownLinkWrapper>
                <WorldlineLink
                  formClassName="w-full"
                  tetheredGuid={account?.tetheredGuid ?? ''}
                  worldlinePostUrl={wlPostUrl ?? ''}
                  worldlineRequestedPage={'Account.aspx'}
                  worldlineReturnUrl={wlReturnUrl ?? ''}
                  scheme={account?.scheme ?? 'GB'}
                  renderButton={(
                    onWorldlineLinkClick: (e: React.MouseEvent<HTMLElement>) => void
                  ) => (
                    <button
                      onClick={onWorldlineLinkClick}
                      className={cn(
                        linkStyle,
                        'text-left flex justify-between items-center w-full'
                      )}
                    >
                      {t('spending.manage.account.accountDetails')}
                      <SquareArrowOutUpRight size={24} />
                    </button>
                  )}
                ></WorldlineLink>
              </ReponsiveDropdownLinkWrapper>
            )}
            {shouldRenderManageEmployeesLink && (
              <ReponsiveDropdownLinkWrapper>
                <Link
                  href={getPathForLocale(locale, 'manage/employees?tab=innbusiness-pay')}
                  className={linkStyle}
                >
                  {t('spending.manage.account.manageEmployees')}
                </Link>
              </ReponsiveDropdownLinkWrapper>
            )}
            {shouldRenderManageCardsLink && (
              <ReponsiveDropdownLinkWrapper>
                <Link
                  href={getPathForLocale(locale, 'manage/cards?tab=innbusiness-pay')}
                  className={linkStyle}
                >
                  {t('spending.manage.account.cardManagement')}
                </Link>
              </ReponsiveDropdownLinkWrapper>
            )}
            {shouldRenderViewOffersLink && (
              <ReponsiveDropdownLinkWrapper>
                <WorldlineLink
                  formClassName="w-full"
                  tetheredGuid={account?.tetheredGuid ?? ''}
                  worldlinePostUrl={wlPostUrl ?? ''}
                  worldlineRequestedPage={'ViewMyOffers.aspx'}
                  worldlineReturnUrl={wlReturnUrl ?? ''}
                  scheme={account?.scheme ?? 'GB'}
                  renderButton={(
                    onWorldlineLinkClick: (e: React.MouseEvent<HTMLElement>) => void
                  ) => (
                    <button
                      onClick={onWorldlineLinkClick}
                      className={cn(
                        linkStyle,
                        'text-left flex justify-between items-center w-full'
                      )}
                    >
                      {t('spending.manage.account.viewOffers')}
                      <SquareArrowOutUpRight size={24} />
                    </button>
                  )}
                ></WorldlineLink>
              </ReponsiveDropdownLinkWrapper>
            )}
          </div>
        )}

        {shouldRenderApplyGroup && (
          <div className={groupItemsStyle}>
            {shouldRenderLinkAccountLink && (
              <ReponsiveDropdownLinkWrapper
                shouldCloseDropdown
                closeDropdown={() => setIsDropdownOpen(false)}
              >
                <LinkAccountButton
                  variant="link"
                  className={`${linkStyle} no-underline hover:no-underline flex justify-between items-center w-full`}
                  setIsModalOpen={setIsModalOpen}
                  token={token}
                  scheme={account?.scheme ?? ('GB' as Scheme)}
                >
                  {t('spending.manage.account.link')}
                  <SquareArrowOutUpRight size={24} />
                </LinkAccountButton>
              </ReponsiveDropdownLinkWrapper>
            )}
            {shouldRenderApplyLink && (
              <ReponsiveDropdownLinkWrapper>
                <Link href={getPathForLocale(locale, 'business-pay/apply')} className={linkStyle}>
                  {t('spending.manage.account.create')}
                </Link>
              </ReponsiveDropdownLinkWrapper>
            )}
          </div>
        )}
      </>
    );
  };

  return (
    <>
      <ResponsiveDropdown
        dataTestId={`${baseDataTestId}`}
        renderItems={renderLinks}
        isOpen={isDropdownOpen}
        toggleOpen={(open: boolean) => setIsDropdownOpen(open)}
      >
        <Button
          variant={'grey'}
          className={`${buttonStyle} ${isAccountSuspended ? accountSuspendedButtonStyle : ''}`}
          disabled={isAccountSuspended}
        >
          <span className="mobile:hidden">{t('spending.summary.manage.account')}</span>
          <ChevronDown className="mobile:hidden" size={24} />
          <Ellipsis className="hidden mobile:block" size={24} />
        </Button>
      </ResponsiveDropdown>
      {isModalOpen && (
        <ExistingAccountModal
          isModalOpen={isModalOpen}
          locale={locale}
          onClose={() => setIsModalOpen(false)}
        />
      )}
    </>
  );
}

const buttonStyle = 'flex w-auto items-center gap-3 text-lg px-4 h-[40px]';
const accountSuspendedButtonStyle =
  'disabled:bg-lightGrey3 disabled:text-darkGrey2 mobile:disabled:text-lightGrey1 disabled:border-lightGrey3 disabled:!opacity-100';
const linkStyle =
  'py-[0.625rem] px-6 font-semibold sm:px-4 sm:font-medium text-base text-darkGrey1';
const groupItemsStyle =
  'flex flex-col pt-[0.375rem] border-t border-t-lightGrey3 first-of-type:border-t-0 first-of-type:pt-0';
