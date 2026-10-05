'use client';

import { Language, AddressCorrespondenceEnum } from '@whitbread-eos/api';
import { FormRadioGroup, FormInput, FormPersonTitle, Notification } from '@whitbread-eos/atoms/ui';
import { GLOBALS } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation, cn } from '@whitbread-eos/utils';
import { useEffect } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

type Props = {
  icons: Record<string, string>;
  addressComponent: React.ReactNode;
  language: Language;
  className?: string;
  disableEmployeeDetails?: boolean;
  isMyCard: boolean;
  sendCardsToCardholder?: boolean;
  sectionTitle?: string;
  sectionTitleClassName?: string;
  formContainerClassName?: string;
};

export function CardDeliveryForm({
  icons,
  addressComponent,
  language,
  className,
  disableEmployeeDetails = false,
  isMyCard,
  sendCardsToCardholder,
  sectionTitle,
  sectionTitleClassName,
  formContainerClassName,
}: Props) {
  const { t } = useTranslation(['users, cards']);
  const isDELanguage = language === GLOBALS.language.DE;
  const {
    control,
    formState: { errors },
    watch,
    clearErrors,
    trigger,
    setValue,
  } = useFormContext();

  const deliveryAddressType = watch('delivery');

  const correspondenceAddressOption = {
    value: AddressCorrespondenceEnum.CompanyCorrespondenceAddress,
    label: t('cards.cardMgmt.delivery.where.options.companyCorrespondence'),
  };
  const isAlternativeAddressDisabled = isMyCard || !sendCardsToCardholder;
  const alternativeAddressOption = {
    value: AddressCorrespondenceEnum.CardholderAlternativeAddress,
    label: `${t('cards.cardMgmt.delivery.where.options.cardHolderAddress')} ${
      isAlternativeAddressDisabled ? t('cards.cardMgmt.delivery.where.options.notAvailable') : ''
    }`,
    disabled: isAlternativeAddressDisabled,
  };

  useEffect(() => {
    if (isAlternativeAddressDisabled) {
      setValue('delivery', AddressCorrespondenceEnum.CompanyCorrespondenceAddress);
    }
  }, [isAlternativeAddressDisabled]);

  const isCustomAddress =
    deliveryAddressType === AddressCorrespondenceEnum.CardholderAlternativeAddress;

  useEffect(() => {
    clearErrors();
  }, [deliveryAddressType]);

  return (
    <>
      <div className={cn(containerStyle, className)}>
        <span
          data-testid="Delivery-Title"
          className={cn(containerTitleStyle, sectionTitleClassName)}
        >
          {sectionTitle ?? t('cards.cardMgmt.delivery.where.title')}
        </span>
        <Controller
          name="delivery"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              data-testid="userRadioGroup"
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={
                isDELanguage
                  ? [correspondenceAddressOption]
                  : [correspondenceAddressOption, alternativeAddressOption]
              }
              onChange={(value: string) => field.onChange(value)}
              variant="col-styled"
              selectedValue={deliveryAddressType}
            />
          )}
        />
      </div>
      <div className={!isCustomAddress ? 'hidden' : ''}>
        <Notification
          className={notificationStyle}
          type="warning"
          icon={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
          message={t('cards.cardManagement.warning.alternativeAddress')}
        />
        <div className={cn(formContainerStyle, formContainerClassName)}>
          <span className={sectionSubtitleStyle}>
            {t('cards.cardMgmt.delivery.where.cardHolderAddress.title')}
          </span>
          <Controller
            name="title"
            control={control}
            render={({ field }) => (
              <FormPersonTitle
                {...field}
                disabled={disableEmployeeDetails}
                id="Title"
                placeholder={t('users.userMgmt.employee.add.form.title')}
                errors={errors}
                onBlur={() => {
                  trigger('title');
                }}
                onFocus={() => {
                  clearErrors('title');
                }}
                icons={icons}
              />
            )}
          />
          <Controller
            name="firstName"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                disabled={disableEmployeeDetails}
                containerClassName={'mt-6'}
                id="First-Name"
                type={'text'}
                placeholder={t(
                  'cards.cardMgmt.delivery.where.cardHolderAddress.form.firstName.label'
                )}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger('firstName');
                }}
              />
            )}
          />
          <Controller
            name="lastName"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                disabled={disableEmployeeDetails}
                containerClassName={'mt-6'}
                id="Last-Name"
                type={'text'}
                placeholder={t(
                  'cards.cardMgmt.delivery.where.cardHolderAddress.form.lastName.label'
                )}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger('lastName');
                }}
              />
            )}
          />
          {addressComponent}
        </div>
      </div>
    </>
  );
}

const containerStyle = 'flex flex-col mt-12';
const containerTitleStyle = 'font-bold text-xl';
const formContainerStyle = 'form-details-box mt-4';
const sectionSubtitleStyle = 'font-bold mb-6';
const notificationStyle = 'mt-12';
