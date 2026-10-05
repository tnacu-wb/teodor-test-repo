'use client';

import { LOCALES, Scheme } from '@whitbread-eos/api';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import Image from 'next/image';
import React, { useState } from 'react';

import { ExistingAccountModal } from '../ExistingAccountModal';
import { LinkAccountButton } from '../LinkAccountButton';

type Props = {
  locale: LOCALES;
  token: string;
};

const BenefitsBoxes = ({ token, locale }: Props) => {
  const { t } = useTranslation('cards');
  const baseDataTestId = 'BenefitsBoxes';
  const scheme = (locale === LOCALES.EN ? 'GB' : 'DE') as Scheme;
  const [isModalOpen, setIsModalOpen] = useState(false);

  const renderBenefitsBox = (icon = '/', title: string, subtitle: string) => {
    return (
      <div className={benefitBoxStyle}>
        <Image width={48} height={48} alt={t(title)} src={formatIBAssetsUrl(t(icon))} />
        <span className={benefitTitleStyle}>{t(title)}</span>
        <span className={benefitSubtitleStyle}>{t(subtitle)}</span>
      </div>
    );
  };

  return (
    <div>
      <div className={benefitsContainerStyle} data-testid={`${baseDataTestId}-Container`}>
        {renderBenefitsBox(
          'cardMgmt.creditBox.icon',
          'cardMgmt.creditBox.title',
          'cardMgmt.creditBox.subtitle'
        )}
        {renderBenefitsBox(
          'cardMgmt.expenseBox.icon',
          'cardMgmt.expenseBox.title',
          'cardMgmt.expenseBox.subtitle'
        )}
        {renderBenefitsBox(
          'cardMgmt.invoicesBox.icon',
          'cardMgmt.invoicesBox.title',
          'cardMgmt.invoicesBox.subtitle'
        )}
      </div>

      <div className={linkAccountContainer} data-testid={`${baseDataTestId}-LinkAccountContainer`}>
        <div className={linkAccountTextStyle}>
          <span className={linkAccountTitleStyle}>{t('cardMgmt.linkAccountBanner.title')}</span>
          <span>{t('cardMgmt.linkAccountBanner.subtitle')}</span>
        </div>
        <div>
          <LinkAccountButton
            className={linkAccountButtonStyle}
            variant="alternativeDefault"
            data-testid={`${baseDataTestId}-Button`}
            token={token}
            scheme={scheme}
            setIsModalOpen={setIsModalOpen}
          >
            {t('cardMgmt.linkAccountBanner.linkAccountButton')}
          </LinkAccountButton>
        </div>
        {isModalOpen && (
          <ExistingAccountModal
            isModalOpen={isModalOpen}
            locale={locale}
            onClose={() => setIsModalOpen(false)}
          />
        )}
      </div>
    </div>
  );
};

export default BenefitsBoxes;

const benefitsContainerStyle = 'flex gap-6 mobile:gap-4 mobile:flex-col mb-[3rem]';
const benefitBoxStyle =
  'flex flex-col rounded-lg border border-lightGrey3 px-6 pb-6 pt-4 gap-4 flex-1';
const benefitTitleStyle = 'text-[1.438rem] font-bold leading-8 text-darkGrey1';
const benefitSubtitleStyle = 'text-darkGrey1';

const linkAccountContainer = 'flex flex-col max-w-[38.75rem] gap-8 mobile:w-full mobile:max-w-none';
const linkAccountTextStyle = 'flex flex-col gap-2 text-darkGrey1';
const linkAccountTitleStyle = 'text-xl leading-6 font-bold';
const linkAccountButtonStyle = 'px-8 mobile:w-full mobile:max-w-none';
