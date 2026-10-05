'use client';

import { InnBusinessPayPreferencesProps, PreferenceItemData } from '@whitbread-eos/api';
import { WorldlineLink } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';

export function InnBusinessPayPreferences({
  accountName,
  accountNumber,
  accountLabels,
  preferences = [],
  tetheredGuid,
  worldlinePostUrl,
  worldlineReturnUrl,
  icons,
}: InnBusinessPayPreferencesProps) {
  const baseDataTestId = 'InnBusinessPayPreferences';
  const { t } = useTranslation('profile');

  return (
    <div className={containerStyle} data-testid={baseDataTestId}>
      <div className={cardStyle}>
        <div className={cardContentStyle}>
          <div className={accountSectionStyle}>
            <div className={headerRowStyle}>
              <h2 className={headerTitleStyle}>{accountName}</h2>
              <WorldlineLink
                tetheredGuid={tetheredGuid}
                className={editLinkStyle}
                worldlinePostUrl={worldlinePostUrl}
                worldlineRequestedPage="MyContactPreferences.aspx"
                worldlineReturnUrl={worldlineReturnUrl}
                data-testid={`${baseDataTestId}-EditLink`}
                aria-label={`${t('innBusinessPay.preferences.button.edit')} ${accountName}`}
              >
                {t('innBusinessPay.preferences.button.edit')}{' '}
                <Image
                  src={formatIBAssetsUrl(icons['icon.manageEmployees-icon'])}
                  alt="arrow-right"
                  width={14}
                  height={14}
                  aria-hidden="true"
                />
              </WorldlineLink>
            </div>
            {accountNumber && (
              <p className={accountNumberStyle} data-testid={`${baseDataTestId}-AccountNumber`}>
                {accountNumber}
              </p>
            )}
            {accountLabels.length > 0 && (
              <div className={labelsContainerStyle} data-testid={`${baseDataTestId}-AccountLabels`}>
                {accountLabels.map((label: string, index: number) => (
                  <span key={`${label}-${index}`} className={labelStyle}>
                    {label}
                  </span>
                ))}
              </div>
            )}
          </div>

          {preferences.map((pref: PreferenceItemData) => (
            <div
              key={pref.id}
              className={preferenceItemStyle}
              data-testid={`${baseDataTestId}-Pref-${pref.id}`}
            >
              <h3 className={preferenceTitleStyle}>{t(pref.translationKey)}</h3>
              <p className={preferenceValueStyle}>{pref.value}</p>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

const containerStyle = 'w-full flex flex-col items-start max-w-[620px] mb-10';
const cardStyle = 'w-full bg-white border border-lightGrey4 rounded-lg p-6';
const cardContentStyle = 'flex flex-col gap-6';
const accountSectionStyle = 'flex flex-col gap-4';
const headerRowStyle = 'flex justify-between items-center';
const headerTitleStyle = 'text-xl font-semibold text-darkGrey1';
const editLinkStyle =
  'inline-flex items-center gap-1 text-secondaryColor underline focus:outline-none focus:ring-2 focus:ring-offset-2 focus:ring-secondaryColor rounded';
const accountNumberStyle = 'text-md text-darkGrey2 -mt-2';
const labelsContainerStyle = 'flex flex-wrap gap-2';
const labelStyle =
  'px-3 py-1 text-sm text-darkGrey2 border border-lightGrey4 rounded-full bg-white capitalize';
const preferenceItemStyle = 'flex flex-col gap-1';
const preferenceTitleStyle = 'text-lg font-medium text-darkGrey1';
const preferenceValueStyle = 'text-base text-darkGrey2';
