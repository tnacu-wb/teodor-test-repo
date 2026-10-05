'use client';

import {
  CountryCode,
  PAYMENT_TYPES,
  PaymentCardInfo,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_PIBA_MEMORABLE_WORD_IFRAME,
} from '@whitbread-eos/api';
import {
  FormInput,
  Notification,
  FormCheckbox,
  FormCardType,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import {
  formatIBAssetsUrl,
  useTranslation,
  cn,
  useFeatureToggle,
  getCountryLanguageByLocale,
  getLocaleByPathname,
} from '@whitbread-eos/utils';
import { usePathname } from 'next/navigation';
import React from 'react';
import { Controller, useFormContext } from 'react-hook-form';

interface Props {
  icons: Record<string, string>;
  cardDetails?: PaymentCardInfo;
}

export const CentrallyCardTypeForm = ({ icons, cardDetails }: Readonly<Props>) => {
  const { t } = useTranslation(['cards']);
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { language } = getCountryLanguageByLocale(locale);
  const {
    [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled,
    [FT_IB_PIBA_MEMORABLE_WORD_IFRAME]: isMemorableWordInIframe,
  } = useFeatureToggle();

  const {
    control,
    formState: { errors },
    trigger,
    watch,
  } = useFormContext();

  const isCnp = watch('CNP');
  const isPIBA =
    watch('cardType') === PAYMENT_TYPES.NEW_PIBA || watch('cardType') === PAYMENT_TYPES.KEEP_PIBA;
  const shouldShowNewPibaOption =
    language === CountryCode.EN || (language === CountryCode.DE && Boolean(isPibaEuroEnabled));

  return (
    <>
      <Controller
        name="cardType"
        control={control}
        render={({ field }) => (
          <FormCardType
            {...field}
            icons={icons}
            errors={errors}
            testId="Centrally-Stored-Card-Type"
            labelsLocation="cards"
            selectedValue={field.value}
            cardDetails={cardDetails}
            showNewPibaOption={shouldShowNewPibaOption}
          />
        )}
      />

      <Controller
        name="CNP"
        control={control}
        render={({ field }) => (
          <FormCheckbox
            {...field}
            id="Card-Carry"
            className="py-[10px] my-6"
            checkboxClassName="self-start mt-[2px]"
            label={t('cards.centrallyStoredCard.card.notCarriedCheckbox.label')}
          />
        )}
      />

      <Notification
        type="info"
        icon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
        message={
          <SanitizedContent>{t('cards.centrallyStoredCard.card.terms.label')}</SanitizedContent>
        }
      />

      {isPIBA && isCnp && !isMemorableWordInIframe && (
        <>
          <Controller
            name="memorableWord"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                containerClassName={cn('mt-6')}
                id="memorableWord"
                placeholder={t('cards.centrallyStoredCard.card.memorableWord.placeholder')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => trigger('memorableWord')}
              />
            )}
          />
          <span className="flex ml-4 mt-2 text-xs text-darkGrey1">
            {t('cards.centrallyStoredCard.card.memorableWord.note')}
          </span>
        </>
      )}
    </>
  );
};
