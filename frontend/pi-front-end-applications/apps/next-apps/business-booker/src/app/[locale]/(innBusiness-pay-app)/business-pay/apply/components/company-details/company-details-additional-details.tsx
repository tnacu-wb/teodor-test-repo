'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  HotelPolicy,
  requestStatus,
  BusinessType,
  WLCountryCode,
  LOCALES,
} from '@whitbread-eos/api';
import { FormSelect } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardFooter, WizardPage } from '@whitbread-eos/layout';
import { getAuthCookie } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import {
  estimatedAccountSpendingSchema,
  hotelPolicySchema,
  updateAppCompanyDetails,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useState } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { z } from 'zod';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState, PayApplicationStep } from '../types';

type Props = {
  icons: Record<string, string>;
  estimatedMonthlySpend: string;
  locale: LOCALES;
};

export function CompanyDetailsAdditionalDetails({
  icons,
  estimatedMonthlySpend,
  locale,
}: Readonly<Props>) {
  const baseDataTestId = 'CompanyDetailsAdditionalDetails';
  const { goToPreviousStep, goToNextStep, wizardState, setWizardState } =
    useWizardContext<PayApplicationState>();
  const { t } = useTranslation('payApplication');
  const token = getAuthCookie();
  const router = useRouter();
  const { handleWorldlineError } = useWorldlineErrorHandler();

  const [requestPending, setRequestPending] = useState(false);

  const isENSchema = wizardState.scheme !== 'DE';

  const baseSchema = estimatedAccountSpendingSchema(t);

  const schema = isENSchema ? baseSchema.merge(hotelPolicySchema()) : baseSchema;

  type FormValues = z.infer<typeof schema>;
  const estimatedMonthlySpendArray = estimatedMonthlySpend
    .slice(1, -1)
    .split(/",\s*"/)
    .map((period) => period.replace(/"/g, '').trim());

  const estimatedMonthlySpendOptions = estimatedMonthlySpendArray.map((period) => ({
    value: period,
    displayValue: period,
  }));

  const hotelPolicyOptions = [
    {
      value: HotelPolicy.PI,
      displayValue: t('companyDetails.hotelPolicy.dropdown1'),
    },
    {
      value: HotelPolicy.PI_AND_OTHERS,
      displayValue: t('companyDetails.hotelPolicy.dropdown2'),
    },
    {
      value: HotelPolicy.NOT_SET,
      displayValue: t('companyDetails.hotelPolicy.dropdown3'),
    },
  ];

  const formMethods = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      estMonthlySpend: estimatedMonthlySpendOptions.find(
        (option) => option.value === wizardState.companyDetails.estMonthlySpend
      ) || { value: '', displayValue: '' },
      hotelBrandPolicy: hotelPolicyOptions.find(
        (option) => option.value === wizardState.companyDetails.hotelBrandPolicy
      ) || { value: '', displayValue: '' },
    },
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = formMethods;

  const estMonthlySpendWatcher = watch('estMonthlySpend');
  const hotelBrandPolicyWatcher = watch('hotelBrandPolicy');

  const handleSave = async (data: z.infer<typeof schema>, isSaveAndClose: boolean) => {
    setRequestPending(true);

    const isSoleTrader = [
      BusinessType.SoleTrader,
      BusinessType.SoleTrader_Registered_Merchant,
      BusinessType.SoleTrader_Merchant,
    ].includes(wizardState.companyDetails.companyType as BusinessType);

    const isSoleTraderOrPartnership = [
      BusinessType.SoleTrader,
      BusinessType.SoleTrader_Registered_Merchant,
      BusinessType.SoleTrader_Merchant,
      BusinessType.Partnership,
      BusinessType.Partnership_DE,
    ].includes(wizardState.companyDetails.companyType as BusinessType);

    const mapDynamicField = (field: any) => {
      if (!field) return '';
      return typeof field === 'object' ? field.value : field || '';
    };

    const mapAddress = (address: any) => {
      if (!address?.addressLine1 && !address?.postCode) return null;
      return {
        addressLine1: address.addressLine1,
        addressLine2: address.addressLine2 || '-',
        addressLine3: address.addressLine3,
        addressLine4: address.addressLine4,
        postcode: address.postCode,
        countryCode: address.country === 'GB' ? WLCountryCode.GB : WLCountryCode.DE,
      };
    };

    const mapBusinessTypeForBackend = (businessType: string) => {
      if (businessType === BusinessType.Partnership_General) {
        return BusinessType.LimitedCompany_DE;
      }
      return businessType;
    };

    const companyDetailsUpdateObject = {
      companyName: wizardState.accountName,
      registrationAddress: mapAddress(wizardState.companyDetails?.registrationAddress),
      correspondenceAddress: mapAddress(wizardState.companyDetails?.correspondenceAddress),
      companyType: mapBusinessTypeForBackend(wizardState.companyDetails?.companyType),
      charityNumber: wizardState.companyDetails?.charityNumber,
      timeTradingId: mapDynamicField(wizardState.companyDetails?.timeTradingId),
      companyRegNum: wizardState.companyDetails?.companyRegNum,
      parentCompanyName: wizardState.companyDetails?.parentCompanyName,
      partnerDetails: isSoleTraderOrPartnership
        ? {
            numberOfPartners: +wizardState.companyDetails?.partnerDetails?.numberOfPartners || 0,
            title: isSoleTrader
              ? wizardState.companyDetails?.nameOfEmployee?.titleEmployee
              : wizardState.companyDetails?.partnerDetails?.title,
            foreName: isSoleTrader
              ? wizardState.companyDetails?.nameOfEmployee?.firstNameEmployee
              : wizardState.companyDetails?.partnerDetails?.foreName,
            lastName: isSoleTrader
              ? wizardState.companyDetails?.nameOfEmployee?.lastNameEmployee
              : wizardState.companyDetails?.partnerDetails?.lastName,
            dateOfBirth: isSoleTrader
              ? new Date(
                  Date.UTC(
                    +wizardState.companyDetails?.dateOfBirth?.year,
                    parseInt(wizardState.companyDetails?.dateOfBirth?.month, 10) - 1,
                    +wizardState.companyDetails?.dateOfBirth?.day,
                    12,
                    0,
                    0
                  )
                ).toISOString()
              : new Date(
                  Date.UTC(
                    +wizardState.companyDetails?.partnerDetails?.dateOfBirth?.year,
                    parseInt(wizardState.companyDetails.partnerDetails?.dateOfBirth?.month, 10) - 1,
                    +wizardState.companyDetails.partnerDetails?.dateOfBirth?.day,
                    12,
                    0,
                    0
                  )
                ).toISOString(),
          }
        : null,
      estMonthlySpend: mapDynamicField(data.estMonthlySpend?.value),
      hotelBrandPolicy: mapDynamicField(data.hotelBrandPolicy?.value),
    };

    const result = await updateAppCompanyDetails(token, {
      applicationGuid: wizardState.applicationGuid,
      applicationId: wizardState.applicationId,
      scheme: wizardState.scheme,
      resumeUrl: PayApplicationStep.CARD_DETAILS,
      appCompanyDetailsDto: companyDetailsUpdateObject,
    });

    setWizardState((prev) => ({
      ...prev,
      companyDetails: {
        ...prev.companyDetails,
        estMonthlySpend: mapDynamicField(data.estMonthlySpend?.value),
        hotelBrandPolicy: mapDynamicField(data.hotelBrandPolicy?.value),
      },
    }));

    if (result?.status === requestStatus.success) {
      if (isSaveAndClose) {
        router.push(
          getPathForLocale(
            locale,
            `business-pay/pay-application-save?applicationGuid=${wizardState.applicationGuid}&applicationId=${wizardState.applicationId}`
          )
        );
      } else {
        goToNextStep();
      }
    } else {
      handleWorldlineError(result);
    }

    setRequestPending(false);
  };

  return (
    <FormProvider {...formMethods}>
      <WizardPage
        type="form"
        formTitle={t('companyDetails.title')}
        showBackButton={true}
        onBackClick={goToPreviousStep}
        footer={
          <WizardFooter
            linkLabel={t('companyDetails.saveAndClose')}
            buttonLabel={t('companyDetails.continue.button')}
            onButtonClick={handleSubmit((data) => handleSave(data, false))}
            onLinkClick={handleSubmit((data) => handleSave(data, true))}
            linkDisabled={requestPending}
            buttonDisabled={requestPending}
          />
        }
      >
        <div className={'mt-12 space-y-8'}>
          <Controller
            name="estMonthlySpend"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id={`${baseDataTestId}-estMonthlySpend`}
                placeholder={t('companyDetails.estimatedAccount.spend')}
                showLabel={false}
                errors={errors}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={estimatedMonthlySpendOptions}
              />
            )}
          />
          {isENSchema && (
            <Controller
              name="hotelBrandPolicy"
              control={control}
              render={({ field }) => (
                <FormSelect
                  {...field}
                  id={`${baseDataTestId}-hotelBrandPolicy`}
                  placeholder={t('companyDetails.hotelPolicy')}
                  showLabel={false}
                  errors={errors}
                  arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                  options={hotelPolicyOptions}
                />
              )}
            />
          )}
        </div>
        {!requestPending && <ReviewChanges />}
        <Analytics
          pageName="Pay Application: Company Details"
          errors={errors}
          monthlyAccountSpend={
            typeof estMonthlySpendWatcher === 'string'
              ? estMonthlySpendWatcher
              : estMonthlySpendWatcher?.displayValue
          }
          companyHotelPolicy={
            typeof hotelBrandPolicyWatcher === 'string'
              ? hotelBrandPolicyWatcher
              : hotelBrandPolicyWatcher?.displayValue
          }
        />
      </WizardPage>
    </FormProvider>
  );
}
