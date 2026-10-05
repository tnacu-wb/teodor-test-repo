'use client';

import {
  WorldlineSmsSetting,
  InnBusinessPaySectionProps,
  WorldlineMergedPreference,
  PreferenceItemData,
  CustomerAccountDetails,
} from '@whitbread-eos/api';
import { Notification } from '@whitbread-eos/atoms/ui';
import {
  useTranslation,
  getWorldlineUserPreferences,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils/server';
import { Suspense, useEffect, useState } from 'react';

import { InnBusinessPayPreferences } from './inn-business-pay-preferences';
import { InnBusinessPaySectionSkeleton } from './inn-business-pay-section-skeleton';

const getSmsStatus = (settings: WorldlineSmsSetting[] | undefined, smsType: string): boolean => {
  if (!settings) return false;
  const setting = settings.find((s) => s.smsType === smsType);
  return setting ? setting.isSmsSelected : false;
};

type ExtendedInnBusinessPaySectionProps = InnBusinessPaySectionProps & {
  account?: CustomerAccountDetails;
  worldlinePostUrl?: string;
  worldlineReturnUrl?: string;
};

export function InnBusinessPaySection({
  companyId,
  token,
  icons,
  account,
  worldlinePostUrl,
  worldlineReturnUrl,
}: ExtendedInnBusinessPaySectionProps) {
  const { t } = useTranslation('profile');
  const [accounts, setAccounts] = useState<
    Array<{
      accountName: string;
      accountNumber: string;
      accountLabels: string[];
      preferences: PreferenceItemData[];
      tetheredGuid: string;
    }>
  >([]);
  const [isLoading, setIsLoading] = useState(true);

  const isPIBAEuro = account?.scheme === 'DE';

  const createPreference = (
    id: string,
    translationKey: string,
    value: boolean | null | undefined,
    options: { true: string; false: string }
  ): PreferenceItemData => ({
    id,
    translationKey,
    value: t((value ?? false) ? options.true : options.false),
  });

  const booleanOptions = {
    sendStopAlerts: {
      true: 'innBusinessPay.preferences.options.sendText',
      false: 'innBusinessPay.preferences.options.doNotSend',
    },
    sendReplacementCards: {
      true: 'innBusinessPay.preferences.options.sendToAddress',
      false: 'innBusinessPay.preferences.options.doNotSend',
    },
    smsContact: {
      true: 'innBusinessPay.preferences.options.contactMe',
      false: 'innBusinessPay.preferences.options.doNotcontactMe',
    },
  };

  const CARD_HOLDER_SMS_CONFIG = [
    { id: 'cardNearingLimit', key: 'card.creditLimit.nearing.title', sms: 'SMS005' },
    { id: 'onOffStop', key: 'account.onOff.title', sms: 'SMS006' },
  ];

  const STANDARD_SMS_CONFIG = [
    { id: 'nearingLimit', key: 'creditLimit.nearing.title', sms: 'SMS001' },
    { id: 'onOffStop', key: 'account.onOff.title', sms: 'SMS002' },
    { id: 'cardLimitReached', key: 'creditLimit.reached.title', sms: 'SMS003' },
    { id: 'invoiceReady', key: 'invoice.review.title', sms: 'SMS004' },
  ];

  const FINANCE_USER_SMS_CONFIG = [
    { id: 'nearingLimit', key: 'creditLimit.nearing.title', sms: 'SMS001' },
    { id: 'onOffStop', key: 'account.onOff.title', sms: 'SMS002' },
    { id: 'invoiceReady', key: 'invoice.review.title', sms: 'SMS004' },
  ];
  const FINANCE_USER_AND_CARD_HOLDER_SMS_CONFIG = [
    { id: 'cardNearingLimit', key: 'card.creditLimit.nearing.title', sms: 'SMS005' },
    { id: 'onOffStop', key: 'account.onOff.title', sms: 'SMS006' },
  ];

  const mapWorldlinePreferences = (prefs: WorldlineMergedPreference[]) => {
    return prefs
      .filter((pref) => pref.tetheredUserGuid)
      .map((pref) => {
        const accountName = pref.accountName ?? '';
        const accountNumber = pref.accountNumber;
        const settings = pref.settings;
        const roles = pref.registrationRoles ?? [];

        const displayRoleLabels = roles.map((role: string) =>
          role
            .replace(/_/g, ' ')
            .split(' ')
            .map((word: string) => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase())
            .join(' ')
        );
        if (displayRoleLabels.length === 0) displayRoleLabels.push('Unknown Role');

        const isOnlyCardHolder = roles.length === 1 && roles[0] === 'CARD_HOLDER';
        const isFinanceUser = roles.includes('FINANCE_USER');
        const isAccountHolder = roles.includes('ACCOUNT_HOLDER');

        const preferences: PreferenceItemData[] = [];
        let smsConfigToUse = [];

        if (isAccountHolder) {
          preferences.push(
            createPreference(
              'receiveAccountAlerts',
              'innBusinessPay.preferences.text.alerts.title',
              pref.preferenceDetails?.showSmsStopsToCardholder,
              booleanOptions.sendStopAlerts
            )
          );
          preferences.push(
            createPreference(
              'replacementCards',
              'innBusinessPay.preferences.replacement.cards.title',
              pref.preferenceDetails?.sendCardsToCardholder,
              booleanOptions.sendReplacementCards
            )
          );
        }

        if (isFinanceUser && roles.includes('CARD_HOLDER')) {
          smsConfigToUse = FINANCE_USER_AND_CARD_HOLDER_SMS_CONFIG;
        } else if (isFinanceUser) {
          smsConfigToUse = FINANCE_USER_SMS_CONFIG;
        } else if (isOnlyCardHolder) {
          smsConfigToUse = CARD_HOLDER_SMS_CONFIG;
        } else {
          smsConfigToUse = STANDARD_SMS_CONFIG;
        }

        smsConfigToUse.forEach((config) => {
          preferences.push(
            createPreference(
              config.id,
              `innBusinessPay.preferences.${config.key}`,
              getSmsStatus(settings, config.sms),
              booleanOptions.smsContact
            )
          );
        });

        return {
          accountName,
          accountNumber,
          accountLabels: displayRoleLabels,
          preferences,
          tetheredGuid: pref.tetheredUserGuid,
        };
      });
  };

  useEffect(() => {
    if (isPIBAEuro || !token) {
      setIsLoading(false);
      return;
    }

    const fetchAccounts = async () => {
      try {
        const mergedPreferenceData = await getWorldlineUserPreferences(token);

        if (mergedPreferenceData && mergedPreferenceData.length > 0) {
          const mappedAccounts = mapWorldlinePreferences(mergedPreferenceData);
          setAccounts(mappedAccounts);
        } else {
          console.error('No InnBusiness Pay accounts found or failed to load preferences.');
        }
      } catch (err) {
        console.error('Failed to load InnBusiness Pay preferences');
      } finally {
        setIsLoading(false);
      }
    };

    fetchAccounts();
  }, [companyId, token, isPIBAEuro]);

  if (isPIBAEuro) {
    return null;
  }

  return (
    <Suspense fallback={<InnBusinessPaySectionSkeleton />}>
      {isLoading ? (
        <InnBusinessPaySectionSkeleton />
      ) : (
        <div className={containerStyle}>
          <h4 className={titleStyle}>{t('innBusinessPay.preferences.title')}</h4>

          <Notification
            type="info"
            icon={formatIBAssetsUrl(icons['icon.notification.info'])}
            message={t('innBusinessPay.preferences.notification')}
            className="mb-6 w-[620px] mobile:w-auto"
          />

          {accounts.map((account, index) => (
            <div key={index} className={accountItemStyle}>
              <InnBusinessPayPreferences
                accountName={account.accountName}
                accountNumber={account.accountNumber}
                accountLabels={account.accountLabels}
                preferences={account.preferences}
                tetheredGuid={account.tetheredGuid}
                worldlinePostUrl={worldlinePostUrl ?? ''}
                worldlineReturnUrl={worldlineReturnUrl ?? ''}
                icons={icons}
              />
            </div>
          ))}
        </div>
      )}
    </Suspense>
  );
}

const containerStyle = 'mt-10 mb-8';
const titleStyle = 'text-[20px] font-bold text-darkGrey1 mb-6';
const accountItemStyle = 'mb-6';
