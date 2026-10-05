'use client';

import { Language } from '@whitbread-eos/api';
import { FormCountries, FormInput } from '@whitbread-eos/atoms/ui';
import { GLOBALS } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { getVariant } from '@whitbread-eos/utils/server';
import { useState, useEffect } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

type Props = {
  icons: Record<string, string>;
  language: Language;
  onOpen?: () => void;
  showManualEntryLink?: boolean;
  customClassName?: string;
  variant?: string;
  isIBPayApp?: boolean;
  areInputsDisabled?: boolean;
};

export function ManualAddress({
  icons,
  language,
  onOpen,
  showManualEntryLink = true,
  customClassName,
  variant,
  isIBPayApp = false,
  areInputsDisabled = false,
}: Props) {
  const { t } = useTranslation(['users', 'profile']);
  const [showForm, setShowForm] = useState(!showManualEntryLink);
  const [isCountryDisabled, setIsCountryDisabled] = useState(false);

  const {
    control,
    formState: { errors },
    trigger,
    watch,
    setValue,
    getValues,
  } = useFormContext();

  const addressLine1Variant = getVariant('addressLine1', variant);
  const addressLine2Variant = getVariant('addressLine2', variant);
  const addressLine3Variant = getVariant('addressLine3', variant);
  const addressLine4Variant = getVariant('addressLine4', variant);
  const addressLine5Variant = getVariant('addressLine5', variant);
  const countryVariant = getVariant('country', variant);
  const postCodeVariant = getVariant('postCode', variant);

  const selectedCountry = watch(getVariant('country', variant));
  const isDEForm = [GLOBALS.country.DE, GLOBALS.country.D].includes(selectedCountry);

  useEffect(() => {
    if (Object.keys(errors).length > 0 && !showForm) {
      handleFormOpen();
    }
  }, [errors]);

  useEffect(() => {
    if (selectedCountry && areInputsDisabled) {
      setIsCountryDisabled(areInputsDisabled);
    }
    if (!areInputsDisabled) {
      setIsCountryDisabled(false);
    }
  }, [selectedCountry, areInputsDisabled]);

  useEffect(() => {
    if (isDEForm) {
      setValue(countryVariant, GLOBALS.country.DE); // Handles the case when country = 'D'
      handleFormOpen();
    }
    if (isIBPayApp) {
      const currentPostCode = getValues(postCodeVariant);
      const currentAddressLine4 = getValues(addressLine4Variant);

      setValue(postCodeVariant, currentPostCode ?? '');
      setValue(addressLine4Variant, currentAddressLine4 ?? '');
    }
  }, [isDEForm]);

  const handleFormOpen = () => {
    setShowForm(true);
    if (onOpen) {
      onOpen();
    }
  };

  const addressLine4Component = (
    <Controller
      name={addressLine4Variant}
      control={control}
      render={({ field }) => (
        <FormInput
          {...field}
          disabled={areInputsDisabled}
          id={variant ? `${variant}.Address-Line-4` : 'Address-Line-4'}
          placeholder={
            isDEForm ? t('profile.profile.form.city') : t('profile.profile.form.address.line4')
          }
          errors={errors}
          errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
        />
      )}
    />
  );

  const postCodeComponent = (
    <Controller
      name={postCodeVariant}
      control={control}
      render={({ field }) => (
        <FormInput
          {...field}
          disabled={areInputsDisabled}
          aria-required
          id={variant ? `${variant}.Manual-Postcode` : 'Manual-Postcode'}
          type={'text'}
          placeholder={t('profile.profile.form.postcode')}
          errors={errors}
          errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          onBlur={() => {
            trigger(postCodeVariant);
          }}
        />
      )}
    />
  );

  return (
    <>
      {showManualEntryLink && !showForm ? (
        <button
          data-testid="IB-ManualAddress-Button"
          className={showButtonStyle}
          onClick={() => handleFormOpen()}
        >
          {t('users.userMgmt.employee.add.companyAddress.enterAddressManually')}
        </button>
      ) : (
        <div className={customClassName || formStyle}>
          <Controller
            name={addressLine1Variant}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                disabled={areInputsDisabled}
                id={variant ? `${variant}.Address-Line-1` : 'Address-Line-1'}
                placeholder={t('profile.profile.form.address.line1')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger(addressLine1Variant);
                }}
              />
            )}
          />
          <Controller
            name={addressLine2Variant}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                disabled={areInputsDisabled}
                id={variant ? `${variant}.Address-Line-2` : 'Address-Line-2'}
                placeholder={t('profile.profile.form.address.line2')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger(addressLine2Variant);
                }}
              />
            )}
          />
          <Controller
            name={addressLine3Variant}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                disabled={areInputsDisabled}
                id={variant ? `${variant}.Address-Line-3` : 'Address-Line-3'}
                placeholder={t('profile.profile.form.address.line3')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              />
            )}
          />
          {isIBPayApp && isDEForm ? postCodeComponent : addressLine4Component}

          {!isDEForm && (
            <Controller
              name={addressLine5Variant}
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  disabled={areInputsDisabled}
                  id={variant ? `${variant}.Address-Line-5` : 'Address-Line-5'}
                  placeholder={t('profile.profile.form.address.line5')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                />
              )}
            />
          )}

          {isIBPayApp && isDEForm ? addressLine4Component : postCodeComponent}

          <Controller
            name={countryVariant}
            control={control}
            render={({ field }) => (
              <FormCountries
                {...field}
                disabled={isCountryDisabled}
                initialCountry={field.value}
                id={variant ? `${variant}.Manual-Countries` : 'Manual-Countries'}
                placeholder={t('profile.profile.form.country')}
                className="w-full"
                language={language}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                onChange={(value: string) => field.onChange(value)}
              />
            )}
          />
        </div>
      )}
    </>
  );
}

const formStyle = 'flex flex-col mt-6 gap-6';
const showButtonStyle = 'text-secondaryColor underline self-start mt-4';
