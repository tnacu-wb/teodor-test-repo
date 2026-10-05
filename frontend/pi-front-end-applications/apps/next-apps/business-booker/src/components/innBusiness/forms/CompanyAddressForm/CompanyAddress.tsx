'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  AddressInfo,
  Language,
  FormattedAddress,
  CountryCode,
  ShortCountry,
  LegacyCountryCode,
} from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { GLOBALS } from '@whitbread-eos/utils';
import { useTranslation } from '@whitbread-eos/utils';
import { addressSchema } from '@whitbread-eos/utils/server';
import { useState, MutableRefObject, useEffect } from 'react';
import { useForm, FormProvider, useWatch } from 'react-hook-form';
import { z } from 'zod';

import { POSTCODE_DE, POSTCODE_UK } from '~components/constants/regex';
import { AddressDetails } from '~components/innBusiness/AddressDetails';

import { CompanyAddressFields } from './CompanyAddressFields';

type AddressFormValues = z.infer<ReturnType<typeof addressSchema>>;

type Props = {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  addressData?: AddressInfo;
  language: Language;
  postalCode?: string;
  isCompanyDetailsContainer?: boolean;
  isProfilePage?: boolean;
  personalDetailsPostCode?: string;
  formRef: MutableRefObject<HTMLFormElement | null>;
  onOpen: () => void;
  onAddressChange?: (address: AddressInfo) => void;
  onDirtyChange?: (isDirty: boolean) => void;
};

export function CompanyAddress({
  onSubmit,
  icons,
  addressData,
  language,
  postalCode = '',
  formRef,
  onOpen,
  isCompanyDetailsContainer = false,
  isProfilePage = false,
  onAddressChange,
  onDirtyChange,
}: Props) {
  const { t } = useTranslation(['users']);
  const isGermanLanguage = language === CountryCode.DE.toLowerCase();
  const countryByLanguage = isGermanLanguage ? ShortCountry.DE : ShortCountry.GB;
  const countryByAddress = addressData
    ? addressData.country === LegacyCountryCode.DE
      ? ShortCountry.DE
      : addressData.country
    : '';
  const [isNewAddress, setIsNewAddress] = useState<boolean>(false);
  const [companyInitialValue, setCompanyInitialValue] = useState<boolean>(true);
  const defaultAddress: FormattedAddress = addressData
    ? {
        ...addressData,
        postalCode: isProfilePage ? postalCode : addressData.postCode,
        country: countryByAddress,
        label: '',
      }
    : {
        addressLine1: '',
        postalCode: '',
        label: '',
        country: addressData ? countryByAddress : countryByLanguage,
      };

  const clickHandler = () => {
    setIsNewAddress(true);
    setCompanyInitialValue(false);
  };

  const defaultCountry = defaultAddress?.country
    ? defaultAddress.country === 'D'
      ? GLOBALS.country.DE
      : defaultAddress.country
    : '';

  const baseSchema = addressSchema(t);
  const schema = isProfilePage
    ? baseSchema.superRefine((values: AddressFormValues, ctx: z.RefinementCtx) => {
        const { country, postCode } = values;

        const addInvalidPostcodeIssue = () => {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            path: ['postCode'],
            message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
          });
        };

        if (country === GLOBALS.country.DE && postCode && !POSTCODE_DE.test(postCode)) {
          addInvalidPostcodeIssue();
        } else if (country === GLOBALS.country.GB && postCode && !POSTCODE_UK.test(postCode)) {
          addInvalidPostcodeIssue();
        }
      })
    : baseSchema;
  const methods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: '',
      addressLine1: defaultAddress?.addressLine1 ?? '',
      addressLine2: defaultAddress?.addressLine2 ?? '',
      addressLine3: defaultAddress?.addressLine3 ?? '',
      addressLine4: defaultAddress?.addressLine4 ?? '',
      addressLine5: defaultAddress?.addressLine5 ?? '',
      postCode: defaultAddress?.postalCode ?? '',
      country: defaultCountry,
    },
  });

  // Watch for changes in address fields
  const addressLine1 = useWatch({ control: methods.control, name: 'addressLine1' });
  const addressLine2 = useWatch({ control: methods.control, name: 'addressLine2' });
  const addressLine3 = useWatch({ control: methods.control, name: 'addressLine3' });
  const addressLine4 = useWatch({ control: methods.control, name: 'addressLine4' });
  const addressLine5 = useWatch({ control: methods.control, name: 'addressLine5' });
  const postCode = useWatch({ control: methods.control, name: 'postCode' });
  const country = useWatch({ control: methods.control, name: 'country' });

  useEffect(() => {
    onAddressChange?.({
      addressLine1: addressLine1 || '',
      addressLine2: addressLine2 || '',
      addressLine3: addressLine3 || '',
      addressLine4: addressLine4 || '',
      addressLine5: addressLine5 || '',
      postCode: postCode || '',
      country: country || '',
    });

    if (
      addressLine1 !== (defaultAddress?.addressLine1 ?? '') ||
      addressLine2 !== (defaultAddress?.addressLine2 ?? '') ||
      addressLine3 !== (defaultAddress?.addressLine3 ?? '') ||
      addressLine4 !== (defaultAddress?.addressLine4 ?? '') ||
      addressLine5 !== (defaultAddress?.addressLine5 ?? '') ||
      postCode !== (defaultAddress?.postalCode ?? '') ||
      country !== defaultCountry
    ) {
      onDirtyChange?.(true);
    }
  }, [addressLine1, addressLine2, addressLine3, addressLine4, addressLine5, postCode, country]);

  const renderCompanyDetailsContainer = () => {
    return (
      <div data-testid="CompanyAddressForm-company-details-container">
        <CompanyAddressFields
          icons={icons}
          language={language}
          onOpen={onOpen}
          address={defaultAddress}
        />
      </div>
    );
  };

  const renderEntireCompanyAddressContainer = () => {
    return (
      <div data-testid="CompanyAddressForm" className={divStyle}>
        <h4 data-testid="CompanyAddressForm-heading" className={headingStyle}>
          {t('users.userMgmt.employee.add.companyAddress')}
        </h4>
        {companyInitialValue && (
          <>
            <div data-testid="CompanyAddressForm-CompanyDetails" className={companyStyle}>
              <AddressDetails addressInfo={addressData} language={language} />
              <Button
                data-testid="CompanyAddressForm-search-for-new-address"
                variant="newAddressButton"
                size="newAddressButton"
                onClick={clickHandler}
              >
                <span data-testid="CompanyAddressForm-search-for-new-address-text">
                  {t('users.userMgmt.employee.add.address.search')}
                </span>
              </Button>
            </div>
          </>
        )}
        {isNewAddress && <CompanyAddressFields icons={icons} language={language} onOpen={onOpen} />}
      </div>
    );
  };

  return (
    <FormProvider {...methods}>
      <form
        ref={formRef as MutableRefObject<HTMLFormElement>}
        onSubmit={methods.handleSubmit(onSubmit)}
      >
        {isCompanyDetailsContainer
          ? renderCompanyDetailsContainer()
          : renderEntireCompanyAddressContainer()}
      </form>
    </FormProvider>
  );
}

const divStyle = 'mt-12';
const headingStyle = 'font-bold text-xl';
const companyStyle = 'mt-6';
