'use client';

import { CountryCode, FormattedAddress, Language } from '@whitbread-eos/api';
import { FormInput, Button, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { GLOBALS } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { getPostCodeAddresses } from '@whitbread-eos/utils/server';
import React, { useCallback, useState, useMemo } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

import { AddressDropdown } from '~components/innBusiness/forms/CompanyAddressForm/AddressDropdown';
import { ManualAddress } from '~components/innBusiness/forms/CompanyAddressForm/ManualAddress';

import { RegisterFindAddress } from '../RegisterFindAddress/register-find-address';
import SocialMediaHandleFields from './social-media-handle-fields';

type AddressFieldsProps = {
  icons: Record<string, string>;
  baseDataTestId?: string;
  language: Language;
  showSocialMedia?: boolean;
};

const RegisterAddressFields = ({
  icons,
  baseDataTestId,
  language,
  showSocialMedia = false,
}: AddressFieldsProps) => {
  const {
    control,
    formState: { errors },
    trigger,
    setError,
    watch,
    setValue,
  } = useFormContext();
  const isGermanLanguage = language === CountryCode.DE.toLowerCase();
  const isDEForm = watch('country') === GLOBALS.country.DE;
  const [addressOptions, setAddressOptions] = useState([]);
  const [postCodeSearch, setPostCodeSearch] = useState(!isGermanLanguage);
  const [isAddressDropdownVisible, setIsAddressDropdownVisible] = useState(false);
  const { t } = useTranslation(['users', 'auth']);

  const socialMediaOptions = [
    { value: 'website', displayValue: 'Website' },
    { value: 'facebook', displayValue: 'Facebook' },
    { value: 'instagram', displayValue: 'Instagram' },
    { value: 'linkedin', displayValue: 'LinkedIn' },
    { value: 'twitter', displayValue: 'X (formerly Twitter)' },
    { value: 'other', displayValue: 'Other' },
  ];
  const mapSocialMediaOptions = useMemo(
    () => ({
      website: 'URL',
      facebook: 'Account Name',
      instagram: 'Account Name',
      linkedin: 'Account Name',
      twitter: 'Account Name',
      other: 'Other company profile',
    }),
    []
  );
  const selectedSocialMediaValue = watch(
    'socialMediaType.value'
  ) as keyof typeof mapSocialMediaOptions;

  const defaultPlaceholder = mapSocialMediaOptions[selectedSocialMediaValue] || 'URL';
  const [socialMediaValuePlaceholder, setSocialMediaValuePlaceholder] =
    useState(defaultPlaceholder);

  const handlePostCodeSubmit = useCallback(async () => {
    const postCodeValue = control._formValues.postCode;
    const postCodeAddresses = await getPostCodeAddresses(postCodeValue);
    setIsAddressDropdownVisible(true);
    window?._satellite?.track('enterAddress');

    if (postCodeAddresses !== null && postCodeAddresses?.length > 0) {
      const mappedOptions =
        postCodeAddresses?.map((item: Record<string, string>) => ({
          value: item.id,
          displayValue: item.addressText,
        })) ?? [];
      setAddressOptions(mappedOptions);
      setValue('selectAddress', mappedOptions[0], {
        shouldTouch: true,
      });
    } else {
      setAddressOptions([]);
      setError('postCode', {
        type: 'manual',
        message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
      });
    }
  }, [control, setError, setValue]);

  const handleAddressChange = useCallback(
    (address: FormattedAddress) => {
      if (address) {
        setValue('addressLine1', address.addressLine1 ?? '');
        setValue('addressLine2', address.addressLine2 ?? '');
        setValue('addressLine3', address.addressLine3 ?? '');
        setValue('addressLine4', address.addressLine4 ?? '');
        setValue('addressLine5', address.addressLine5 ?? '');
        if (address.companyName) {
          setValue('companyName', address.companyName);
        }
        setValue('postCode', address.postalCode ?? '');
        setValue('country', address.country ?? '');
        trigger();
      }
    },
    [setValue]
  );

  const handleSocialMediaChange = useCallback(
    (socialMedia: { value: keyof typeof mapSocialMediaOptions }) => {
      setValue('socialMediaType', socialMedia);
      setSocialMediaValuePlaceholder(mapSocialMediaOptions[socialMedia.value]);
      setValue('socialMediaValue', '');
    },
    [setValue, mapSocialMediaOptions]
  );

  const displayPostCodeFinder = postCodeSearch && !(isDEForm && isGermanLanguage);

  const handleManualAddressToggle = useCallback(() => {
    setPostCodeSearch(false);
    window?._satellite?.track('enterAddress');
  }, []);

  const renderFindAddressFields = () => {
    return (
      <div className={!displayPostCodeFinder ? 'hidden' : ''}>
        <RegisterFindAddress onSubmit={handlePostCodeSubmit} icons={icons} />
        {isAddressDropdownVisible && (
          <AddressDropdown
            icons={icons}
            dropdownOptions={addressOptions}
            onAddressChange={handleAddressChange}
          />
        )}
        {postCodeSearch && (
          <p>
            or
            <Button
              className="pl-[0.625rem] text-[1rem] text-secondaryColor hover:no-underline"
              onMouseDown={(e: React.MouseEvent<HTMLButtonElement>) => {
                e.preventDefault();
                handleManualAddressToggle();
              }}
              onKeyDown={(e: React.KeyboardEvent<HTMLButtonElement>) => {
                if (e.key === 'Enter' || e.key === ' ') {
                  e.preventDefault();
                  handleManualAddressToggle();
                }
              }}
              data-testid={`${baseDataTestId}-Button`}
              variant="link"
            >
              {t('auth.signup.accountCreation.manualAddress.link')}
            </Button>
          </p>
        )}
      </div>
    );
  };

  const renderManualAddress = () => {
    return (
      <div className={`flex flex-col gap-6${displayPostCodeFinder ? ' hidden' : ''}`}>
        {!postCodeSearch && !isDEForm && (
          <div>
            <h3 className={headingStyle}>{t('auth.signup.accountCreation.manualAddress.title')}</h3>
            <p>{t('auth.signup.accountCreation.manualAddress.description')}</p>
            <Button
              className="p-0 text-[1rem] text-secondaryColor hover:no-underline"
              onClick={() => {
                setPostCodeSearch(true);
                setIsAddressDropdownVisible(false);
              }}
              data-testid={`${baseDataTestId}-Button`}
              variant="link"
            >
              <SanitizedContent>
                {t('auth.signup.accountCreation.postcodeLookup.link')}
              </SanitizedContent>
            </Button>
          </div>
        )}
        <ManualAddress
          icons={icons}
          language={language}
          showManualEntryLink={false}
          customClassName="flex flex-col gap-6"
          areInputsDisabled={false}
        />
        <Controller
          name="uniqueTaxpayerReference"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              showLabel={true}
              id="uniqueTaxpayerReference"
              type={'text'}
              placeholder={t('auth.signup.accountCreation.taxpayerReference.placeholder')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
              onBlur={() => {
                trigger('uniqueTaxpayerReference');
              }}
            />
          )}
        />

        {showSocialMedia && (
          <SocialMediaHandleFields
            headingStyle={headingStyle}
            subHeadingStyle={subHeadingStyle}
            icons={icons}
            socialMediaOptions={socialMediaOptions}
            socialMediaValuePlaceholder={socialMediaValuePlaceholder}
            handleSocialMediaChange={handleSocialMediaChange}
          />
        )}
      </div>
    );
  };
  return (
    <>
      {renderFindAddressFields()}
      {renderManualAddress()}
    </>
  );
};

export default RegisterAddressFields;

const headingStyle = 'text-[1.125rem] font-bold pt-5';
const subHeadingStyle = 'text-[1.5rem] pb-[0.5rem]';
