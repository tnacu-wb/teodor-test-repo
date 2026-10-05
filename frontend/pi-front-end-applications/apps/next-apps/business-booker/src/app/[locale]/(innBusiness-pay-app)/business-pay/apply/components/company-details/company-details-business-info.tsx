'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BusinessType } from '@whitbread-eos/api';
import { FormInput, FormSelect, Notification, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { WizardPage, WizardFooter, useWizardContext } from '@whitbread-eos/layout';
import { GLOBALS } from '@whitbread-eos/utils';
import {
  getCountryLanguageByLocale,
  getPathForLocale,
  useTranslation,
  formatIBAssetsUrl,
  getLocaleByPathname,
} from '@whitbread-eos/utils';
import {
  companyNameSchema,
  addressSchema,
  companyNameOfEmployeeSchema,
  companyRegistrationNumberSchema,
  dateOfBirthSchema,
  registeredCharityNumberSchema,
  timeTradingSchema,
  parentCompanySchema,
  partnerDetailsSchema,
} from '@whitbread-eos/utils/server';
import { usePathname, useRouter } from 'next/navigation';
import React, { useState } from 'react';
import { useForm, FormProvider, Controller } from 'react-hook-form';
import { z } from 'zod';

import { POSTCODE_DE, POSTCODE_UK } from '~components/constants/regex';
import { AddressDetails } from '~components/innBusiness/AddressDetails';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { CompanyContainer } from '../../../../../(innBusiness)/manage/company/components/CompanyContainer';
import { Analytics } from '../analytics/analytics';
import { PayApplicationState } from '../types';
import { CompanyNameOfEmployeeForm } from './common/company-name-of-employee';
import { CompanyRegistrationNumber } from './common/company-registration-number';
import { CorrespondenceAddressSection } from './common/correspondence-address-section';
import { DateOfBirthForm } from './common/date-of-birth';
import { ParentCompanyForm } from './common/parent-company';
import { PartnerDetailsForm } from './common/partner-details-form';
import { RegisteredCharityNumberForm } from './common/registered-charity-number';
import { TimeTradingForm } from './common/time-trading';

type Props = {
  timeTrading: string;
  icons: Record<string, string>;
  titleValues: string;
};

export function CompanyDetailsBusinessInfo({ icons, timeTrading, titleValues }: Readonly<Props>) {
  const baseDataTestId = 'CompanyDetailsBusinessInfo';
  const { goToPreviousStep, goToNextStep, wizardState, setWizardState } =
    useWizardContext<PayApplicationState>();
  const { t } = useTranslation(['payApplication', 'users']);
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { language } = getCountryLanguageByLocale(locale);
  const router = useRouter();
  const fallbackCountryCode =
    language === GLOBALS.locale.DE ? GLOBALS.country.DE : GLOBALS.country.GB;

  const [companyDetailsEdit, setCompanyDetailsEdit] = useState<boolean>(false);
  const [companyNameEdit, setCompanyNameEdit] = useState<boolean>(false);
  const [userNameEdit, setUserNameEdit] = useState<boolean>(false);
  const [showCompanyCorrespondenceAddress, setShowCompanyCorrespondenceAddress] = useState<boolean>(
    !!wizardState.companyDetails.correspondenceAddress?.addressLine1
  );

  const [showSuccessNotification, setShowSuccessNotification] = useState<boolean>(false);
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  const validatePostCodeByCountry = (country: string, postCode: string, ctx: z.RefinementCtx) => {
    if (country === GLOBALS.country.DE && postCode && !POSTCODE_DE.test(postCode)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['postCode'],
        message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
      });
    } else if (country === GLOBALS.country.GB && postCode && !POSTCODE_UK.test(postCode)) {
      ctx.addIssue({
        code: z.ZodIssueCode.custom,
        path: ['postCode'],
        message: t('users.userMgmt.employee.add.companyAddress.error.invalidPostcode'),
      });
    }
  };

  const customAddressSchema = z.object({
    registrationAddress: addressSchema(t).superRefine(
      ({ country, postCode }: { country: string; postCode: string }, ctx: z.RefinementCtx) => {
        validatePostCodeByCountry(country, postCode, ctx);
      }
    ),
    correspondenceAddress: addressSchema(t)
      .superRefine(
        ({ country, postCode }: { country: string; postCode: string }, ctx: z.RefinementCtx) => {
          validatePostCodeByCountry(country, postCode, ctx);
        }
      )
      .nullable(),
  });
  let schema = companyNameSchema(t, 'payApp').merge(customAddressSchema);

  const addDateOfBirthValidation = () => {
    schema = schema.merge(dateOfBirthSchema(t)).refine(
      (data: {
        day: { value: string; displayValue: string };
        month: { value: string; displayValue: string };
        year: { value: string; displayValue: string };
      }) => {
        const { day, month, year } = data;
        const daysInMonth = new Date(Number(year.value), Number(month.value), 0).getDate();
        return Number(day.value) <= daysInMonth;
      },
      {
        message: t('payApplication.payapp.memorable.error'),
        path: ['day', 'value'],
      }
    );

    schema = schema.superRefine(
      (
        data: {
          day: { value: string; displayValue: string };
          month: { value: string; displayValue: string };
          year: { value: string; displayValue: string };
        },
        ctx: z.RefinementCtx
      ) => {
        const { day, month, year } = data;
        const dateOfBirth = new Date(
          Number(year.value),
          Number(month.value) - 1,
          Number(day.value)
        );
        const today = new Date();
        const eighteenYearsAgo = new Date(
          today.getFullYear() - 18,
          today.getMonth(),
          today.getDate()
        );

        if (dateOfBirth > eighteenYearsAgo) {
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: t('payApplication.payapp.memorable.error'),
            path: ['day', 'value'],
          });
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: t('payApplication.payapp.memorable.error'),
            path: ['month', 'value'],
          });
          ctx.addIssue({
            code: z.ZodIssueCode.custom,
            message: t('payApplication.payapp.memorable.error'),
            path: ['year', 'value'],
          });
        }
      }
    );

    return schema;
  };

  if ([BusinessType.Charity].includes(wizardState.companyDetails.companyType as BusinessType)) {
    schema = schema.merge(registeredCharityNumberSchema(t));
  }

  if (
    [
      BusinessType.LimitedCompany,
      BusinessType.LimitedCompany_DE,
      BusinessType.LimitedCompany_Partnership_Shares,
      BusinessType.LimitedCompany_Partnership,
      BusinessType.PublicLimited,
      BusinessType.PublicLimited_DE,
      BusinessType.Other_Entrepreneurial_Society,
      BusinessType.Other_Association_Incl_Charity,
      BusinessType.Partnership_DE,
      BusinessType.Partnership_General,
      BusinessType.SoleTrader_Registered_Merchant,
      BusinessType.SoleTrader_Merchant,
      BusinessType.Non_Profit_Charity,
      BusinessType.Registered_Non_Profit_Charity,
      BusinessType.Other_DE,
    ].includes(wizardState.companyDetails.companyType as BusinessType)
  ) {
    schema = schema.merge(companyRegistrationNumberSchema(t));
  }
  if (
    [
      BusinessType.PublicLimited,
      BusinessType.PublicLimited_DE,
      BusinessType.LimitedCompany_DE,
      BusinessType.Other_Entrepreneurial_Society,
      BusinessType.Other_Association_Incl_Charity,
      BusinessType.LimitedCompany_Partnership_Shares,
      BusinessType.LimitedCompany_Partnership,
      BusinessType.Partnership_DE,
      BusinessType.Partnership_General,
      BusinessType.SoleTrader_Registered_Merchant,
      BusinessType.Non_Profit_Charity,
      BusinessType.Registered_Non_Profit_Charity,
    ].includes(wizardState.companyDetails.companyType as BusinessType)
  ) {
    schema = schema.merge(parentCompanySchema()).merge(timeTradingSchema(t));
  }

  if (
    [
      BusinessType.Other_Entrepreneurial_Society,
      BusinessType.Other_Association_Incl_Charity,
      BusinessType.Other_DE,
      BusinessType.Other,
      BusinessType.LimitedCompany,
      BusinessType.LimitedCompany_DE,
      BusinessType.LimitedCompany_Partnership_Shares,
      BusinessType.LimitedCompany_Partnership,
      BusinessType.Partnership_DE,
      BusinessType.Partnership_General,
      BusinessType.Government,
      BusinessType.Government_DE,
      BusinessType.Non_Profit_Charity,
      BusinessType.Registered_Non_Profit_Charity,
    ].includes(wizardState.companyDetails.companyType as BusinessType)
  ) {
    schema = schema.merge(timeTradingSchema(t));
  }

  if (
    [
      BusinessType.SoleTrader,
      BusinessType.SoleTrader_Registered_Merchant,
      BusinessType.SoleTrader_Merchant,
    ].includes(wizardState.companyDetails.companyType as BusinessType)
  ) {
    schema = schema.merge(timeTradingSchema(t)).merge(companyNameOfEmployeeSchema(t));
    schema = addDateOfBirthValidation();
  }

  if (
    [BusinessType.Partnership_DE].includes(wizardState.companyDetails.companyType as BusinessType)
  ) {
    schema = schema.merge(partnerDetailsSchema(t));
    schema = addDateOfBirthValidation();
  }

  if ([BusinessType.Partnership].includes(wizardState.companyDetails.companyType as BusinessType)) {
    schema = schema.merge(timeTradingSchema(t)).merge(partnerDetailsSchema(t));
    schema = addDateOfBirthValidation();
  }

  const isTitleMismatch = !titleValues.includes(
    wizardState.companyDetails.nameOfEmployee?.titleEmployee
  );
  const titleOptions = titleValues.split(',').map((value) => ({
    // eslint-disable-next-line no-useless-escape
    value: value.trim().replace(/['"\[\]]/g, ''),
    // eslint-disable-next-line no-useless-escape
    displayValue: value.trim().replace(/['"\[\]]/g, ''),
  }));

  function getDefaultValuesForPartnership(
    wizardState: PayApplicationState,
    isPartnershipGbScheme: boolean
  ) {
    const { partnerDetails, timeTradingId } = wizardState.companyDetails;

    return {
      timeTradingId: timeTradingId || { value: '', displayValue: '' },
      day: {
        value: partnerDetails?.dateOfBirth.day,
        displayValue: partnerDetails?.dateOfBirth.day,
      },
      month: {
        value: partnerDetails?.dateOfBirth.month,
        displayValue: partnerDetails?.dateOfBirth.month,
      },
      year: {
        value: partnerDetails?.dateOfBirth.year,
        displayValue: partnerDetails?.dateOfBirth.year,
      },
      title: {
        value: partnerDetails?.title,
        displayValue: partnerDetails?.title,
      },
      numberOfPartners: partnerDetails?.numberOfPartners,
      foreName: partnerDetails?.foreName,
      lastName: partnerDetails?.lastName,
      ...(isPartnershipGbScheme
        ? {}
        : {
            companyRegNum: wizardState.companyDetails?.companyRegNum,
          }),
    };
  }

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: wizardState.accountName,
      registrationAddress: wizardState.companyDetails.registrationAddress,
      correspondenceAddress: wizardState.companyDetails.correspondenceAddress,
      ...([BusinessType.Charity].includes(
        wizardState.companyDetails.companyType as BusinessType
      ) && {
        charityNumber: wizardState.companyDetails.charityNumber,
      }),
      ...([
        BusinessType.LimitedCompany,
        BusinessType.LimitedCompany_DE,
        BusinessType.LimitedCompany_Partnership_Shares,
        BusinessType.LimitedCompany_Partnership,
        BusinessType.PublicLimited,
        BusinessType.PublicLimited_DE,
        BusinessType.Other_Entrepreneurial_Society,
        BusinessType.Other_Association_Incl_Charity,
        BusinessType.Partnership_DE,
        BusinessType.Partnership_General,
        BusinessType.SoleTrader_Registered_Merchant,
        BusinessType.SoleTrader_Merchant,
        BusinessType.Non_Profit_Charity,
        BusinessType.Registered_Non_Profit_Charity,
        BusinessType.Other_DE,
      ].includes(wizardState.companyDetails.companyType as BusinessType) && {
        companyRegNum: wizardState.companyDetails.companyRegNum,
        ...([
          BusinessType.PublicLimited,
          BusinessType.PublicLimited_DE,
          BusinessType.LimitedCompany_DE,
          BusinessType.Other_Entrepreneurial_Society,
          BusinessType.Other_Association_Incl_Charity,
          BusinessType.LimitedCompany_Partnership_Shares,
          BusinessType.LimitedCompany_Partnership,
          BusinessType.Partnership_DE,
          BusinessType.Partnership_General,
          BusinessType.SoleTrader_Registered_Merchant,
          BusinessType.Non_Profit_Charity,
          BusinessType.Registered_Non_Profit_Charity,
          BusinessType.Other_DE,
        ].includes(wizardState.companyDetails.companyType as BusinessType) && {
          parentCompanyName: wizardState.companyDetails.parentCompanyName,
        }),
      }),
      ...([BusinessType.Other_DE].includes(
        wizardState.companyDetails.companyType as BusinessType
      ) && {
        companyRegNum: wizardState.companyDetails.companyRegNum || '',
      }),
      ...([
        BusinessType.SoleTrader,
        BusinessType.SoleTrader_Registered_Merchant,
        BusinessType.SoleTrader_Merchant,
      ].includes(wizardState.companyDetails.companyType as BusinessType) && {
        day: {
          value: wizardState.companyDetails.dateOfBirth.day,
          displayValue: wizardState.companyDetails.dateOfBirth.day,
        },
        month: {
          value: wizardState.companyDetails.dateOfBirth.month,
          displayValue: wizardState.companyDetails.dateOfBirth.month,
        },
        year: {
          value: wizardState.companyDetails.dateOfBirth.year,
          displayValue: wizardState.companyDetails.dateOfBirth.year,
        },
        timeTradingId: wizardState.companyDetails.timeTradingId || { value: '', displayValue: '' },
        titleEmployee: isTitleMismatch
          ? { value: '', displayValue: '' }
          : {
              value: wizardState.companyDetails.nameOfEmployee?.titleEmployee || '',
              displayValue: wizardState.companyDetails.nameOfEmployee?.titleEmployee || '',
            },
        firstNameEmployee: wizardState.companyDetails.nameOfEmployee?.firstNameEmployee || '',
        lastNameEmployee: wizardState.companyDetails.nameOfEmployee?.lastNameEmployee || '',
      }),
      ...([
        BusinessType.PublicLimited,
        BusinessType.PublicLimited_DE,
        BusinessType.LimitedCompany,
        BusinessType.LimitedCompany_DE,
        BusinessType.LimitedCompany_Partnership_Shares,
        BusinessType.LimitedCompany_Partnership,
        BusinessType.Partnership_DE,
        BusinessType.Partnership_General,
        BusinessType.Other,
        BusinessType.Other_Entrepreneurial_Society,
        BusinessType.Other_Association_Incl_Charity,
        BusinessType.Other_DE,
        BusinessType.Government,
        BusinessType.Government_DE,
        BusinessType.Non_Profit_Charity,
        BusinessType.Registered_Non_Profit_Charity,
      ].includes(wizardState.companyDetails.companyType as BusinessType) && {
        timeTradingId: wizardState.companyDetails.timeTradingId || { value: '', displayValue: '' },
      }),
      ...([BusinessType.Other].includes(wizardState.companyDetails.companyType as BusinessType) && {
        timeTradingId: wizardState.companyDetails.timeTradingId || { value: '', displayValue: '' },
      }),
      ...([BusinessType.Partnership_DE].includes(
        wizardState.companyDetails.companyType as BusinessType
      ) && getDefaultValuesForPartnership(wizardState, false)),
      ...([BusinessType.Partnership].includes(
        wizardState.companyDetails.companyType as BusinessType
      ) && getDefaultValuesForPartnership(wizardState, true)),
    },
  });

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    setValue,
    watch,
    clearErrors,
  } = formMethods;

  const handleAddCorrespondenceAddress = () => {
    setShowCompanyCorrespondenceAddress(true);
    setValue('correspondenceAddress', {
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: '',
      postCode: '',
    });
  };

  const handleReturnToHomepage = () => {
    router.push(getPathForLocale(locale, 'homepage'));
  };

  const onSubmit = async (data: z.infer<typeof schema>) => {
    if (isProcessing) {
      return;
    }

    setIsProcessing(true);

    const requiresPartnerDetails = [BusinessType.Partnership_DE, BusinessType.Partnership].includes(
      wizardState.companyDetails.companyType as BusinessType
    );

    setWizardState((prev) => ({
      ...prev,
      accountName: data.companyName,
      companyDetails: {
        companyType: prev.companyDetails.companyType,
        companyRegNum: data.companyRegNum || '',
        parentCompanyName: data.parentCompanyName || '',
        nameOfEmployee: {
          titleEmployee:
            data.titleEmployee?.value ||
            wizardState.companyDetails.nameOfEmployee?.titleEmployee ||
            '',
          firstNameEmployee:
            data.firstNameEmployee ||
            wizardState.companyDetails.nameOfEmployee?.firstNameEmployee ||
            '',
          lastNameEmployee:
            data.lastNameEmployee ||
            wizardState.companyDetails.nameOfEmployee?.lastNameEmployee ||
            '',
        },
        partnerDetails: requiresPartnerDetails
          ? {
              numberOfPartners:
                data.numberOfPartners ||
                wizardState.companyDetails.partnerDetails?.numberOfPartners ||
                '',
              title: data.title?.value || wizardState.companyDetails.partnerDetails?.title || '',
              foreName: data.foreName || wizardState.companyDetails.partnerDetails?.foreName || '',
              lastName: data.lastName || wizardState.companyDetails.partnerDetails?.lastName || '',
              dateOfBirth: {
                day:
                  data.day?.value ||
                  wizardState.companyDetails.partnerDetails?.dateOfBirth?.day ||
                  '',
                month:
                  data.month?.value ||
                  wizardState.companyDetails.partnerDetails?.dateOfBirth?.month ||
                  '',
                year:
                  data.year?.value ||
                  wizardState.companyDetails.partnerDetails?.dateOfBirth?.year ||
                  '',
              },
            }
          : {
              numberOfPartners: '',
              title: '',
              foreName: '',
              lastName: '',
              dateOfBirth: {
                day: '',
                month: '',
                year: '',
              },
            },
        dateOfBirth: {
          day: data.day?.value || '',
          month: data.month?.value || '',
          year: data.year?.value || '',
        },
        estMonthlySpend: prev.companyDetails.estMonthlySpend,
        hotelBrandPolicy: prev.companyDetails.hotelBrandPolicy,
        ...data,
      },
    }));
    goToNextStep();
  };

  const handleContinueClick = () => {
    if (isProcessing) {
      return;
    }
    setIsProcessing(true);
    const submit = handleSubmit(onSubmit, () => setIsProcessing(false));
    submit();
  };

  const timeTradingWatcher = watch('timeTradingId');

  return (
    <FormProvider {...formMethods}>
      <WizardPage
        type="form"
        formTitle={t('payApplication.companyDetails.title')}
        showBackButton={true}
        onBackClick={goToPreviousStep}
        footer={
          <WizardFooter
            linkLabel={t('payApplication.companyDetails.closeOut')}
            buttonLabel={t('payApplication.companyDetails.continue.button')}
            onButtonClick={handleContinueClick}
            onLinkClick={handleReturnToHomepage}
            buttonDisabled={isProcessing}
          />
        }
      >
        {showSuccessNotification && (
          <Notification
            className={notificationStyle}
            type="success"
            icon={formatIBAssetsUrl(icons?.['icon.notification.success'])}
            message={
              <SanitizedContent>
                {t('payApplication.companyDetails.registeredNumber.success.notification')}
              </SanitizedContent>
            }
          />
        )}
        <div className={`${formStyle} ${showSuccessNotification ? '!mt-4' : ''}`}>
          <div className={fieldStyle} data-testid={`${baseDataTestId}-company-details`}>
            <CompanyContainer
              title={t('payApplication.companyDetails.companyName')}
              locale={locale}
              showEditButton={!companyNameEdit}
              onEditChange={(value) => {
                setCompanyNameEdit(value);
              }}
            />
            {companyNameEdit ? (
              <Controller
                name="companyName"
                control={control}
                render={({ field }) => (
                  <FormInput
                    {...field}
                    id="Company-name"
                    placeholder={t('payApplication.companyDetails.companyName')}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      trigger('companyName');
                    }}
                  />
                )}
              />
            ) : (
              <>
                {wizardState.accountName && (
                  <span data-testid={`${baseDataTestId}-company-name`}>
                    {wizardState.accountName}
                  </span>
                )}
              </>
            )}
          </div>
          <div className={fieldStyle}>
            <div className={fieldLabelStyle}>{t('payApplication.companyDetails.businessType')}</div>
            <div className={fieldValueStyle}>
              {wizardState.companyDetails.companyType as BusinessType}
            </div>
          </div>
        </div>
        <div className={`${formStyle} mt-6`}>
          {[
            BusinessType.SoleTrader,
            BusinessType.SoleTrader_Registered_Merchant,
            BusinessType.SoleTrader_Merchant,
          ].includes(wizardState.companyDetails.companyType as BusinessType) && (
            <>
              <div className={`${fieldStyle}`} data-testid={`${baseDataTestId}-userName`}>
                <CompanyContainer
                  title={t('payApplication.companyDetails.name')}
                  locale={locale}
                  showEditButton={!userNameEdit}
                  onEditChange={(value) => {
                    setUserNameEdit(value);
                  }}
                />
                {userNameEdit ? (
                  <CompanyNameOfEmployeeForm icons={icons} titleValues={titleValues} />
                ) : (
                  <>
                    {isTitleMismatch && (
                      <Controller
                        name="titleEmployee"
                        control={control}
                        render={({ field }) => (
                          <FormSelect
                            {...field}
                            id="titleEmployee"
                            placeholder={t('payApplication.companyDetails.name.title')}
                            className={titleSelectStyle}
                            buttonClassName={titleSelectDropdownStyle}
                            popoverClassName={titleSelectDropdownStyle}
                            showLabel={false}
                            errors={errors}
                            arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                            onBlur={() => {
                              trigger('titleEmployee');
                            }}
                            onFocus={() => {
                              clearErrors('titleEmployee');
                            }}
                            options={titleOptions}
                            onChange={(data: any) => {
                              setValue('titleEmployee', {
                                value: data.value,
                                displayValue: data.displayValue,
                              });
                            }}
                          />
                        )}
                      />
                    )}

                    {isTitleMismatch ? (
                      <div>{`${wizardState.companyDetails.nameOfEmployee.firstNameEmployee} ${wizardState.companyDetails.nameOfEmployee.lastNameEmployee}`}</div>
                    ) : (
                      <div>
                        {`${wizardState.companyDetails.nameOfEmployee?.titleEmployee} ${wizardState.companyDetails.nameOfEmployee?.firstNameEmployee} ${wizardState.companyDetails.nameOfEmployee?.lastNameEmployee}`}
                      </div>
                    )}
                  </>
                )}
              </div>
              <div
                className={`${fieldStyle} mb-6`}
                data-testid={`${baseDataTestId}-birthDayPicker`}
              >
                <span data-testid={`${baseDataTestId}-birthDayPicker-title`} className={nameStyle}>
                  {t('payApplication.companyDetails.dateOfBirth')}
                </span>
                <DateOfBirthForm icons={icons} />
              </div>
            </>
          )}

          <div
            className={`${fieldStyleWithoutBorder} !gap-0`}
            data-testid={`${baseDataTestId}-company-details`}
          >
            <CompanyContainer
              title={t('payApplication.companyDetails.registeredAddress')}
              locale={locale}
              showEditButton={!companyDetailsEdit}
              onEditChange={(value) => {
                setCompanyDetailsEdit(value);
              }}
            />
            {companyDetailsEdit ? (
              <CompanyAddressFields
                icons={icons}
                language={language}
                variant={'registrationAddress'}
                isIBPayApp
              />
            ) : (
              <>
                {[
                  BusinessType.SoleTrader,
                  BusinessType.SoleTrader_Registered_Merchant,
                  BusinessType.SoleTrader_Merchant,
                  BusinessType.Other,
                  BusinessType.Other_Entrepreneurial_Society,
                  BusinessType.Other_Association_Incl_Charity,
                  BusinessType.Other_DE,
                  BusinessType.Partnership,
                  BusinessType.Partnership_DE,
                  BusinessType.Partnership_General,
                ].includes(wizardState.companyDetails.companyType as BusinessType) && (
                  <span className={'pb-4'}>
                    <SanitizedContent>
                      {t('payApplication.companyDetails.partner.description')}
                    </SanitizedContent>
                  </span>
                )}
                <AddressDetails
                  addressInfo={wizardState.companyDetails.registrationAddress ?? undefined}
                  language={language}
                  isCompanyInformation
                />
              </>
            )}
          </div>
          <div className={`${fieldStyle} mt-0 py-0`}>
            <CorrespondenceAddressSection
              showCorrespondenceButton={!showCompanyCorrespondenceAddress}
              showCompanyCorrespondenceAddress={showCompanyCorrespondenceAddress}
              handleAddCorrespondenceAddress={handleAddCorrespondenceAddress}
              icons={icons}
              buttonStyle={buttonStyle}
              buttonIconStyle={buttonIconStyle}
              baseDataTestId={baseDataTestId}
              nameStyle={nameStyle}
              t={t}
              language={language}
            />
          </div>
        </div>
        <div className={'mt-12'}>
          {[BusinessType.Charity].includes(
            wizardState.companyDetails.companyType as BusinessType
          ) && <RegisteredCharityNumberForm icons={icons} />}
        </div>
        <div className={'mt-12'}>
          {[
            BusinessType.PublicLimited,
            BusinessType.PublicLimited_DE,
            BusinessType.LimitedCompany,
            BusinessType.LimitedCompany_DE,
            BusinessType.LimitedCompany_Partnership_Shares,
            BusinessType.LimitedCompany_Partnership,
            BusinessType.Other_Entrepreneurial_Society,
            BusinessType.Other_Association_Incl_Charity,
            BusinessType.Other_DE,
            BusinessType.Partnership_DE,
            BusinessType.Partnership_General,
            BusinessType.SoleTrader_Registered_Merchant,
            BusinessType.SoleTrader_Merchant,
            BusinessType.Non_Profit_Charity,
            BusinessType.Registered_Non_Profit_Charity,
          ].includes(wizardState.companyDetails.companyType as BusinessType) && (
            <CompanyRegistrationNumber
              icons={icons}
              scheme={wizardState.scheme}
              onLookupSuccess={(data) => {
                setShowSuccessNotification(true);
                const { postcode, ...addressWithoutPostCode } = data.address;
                setWizardState((prev) => ({
                  ...prev,
                  accountName: data.companyName,
                  companyDetails: {
                    ...prev.companyDetails,
                    registrationAddress: {
                      ...addressWithoutPostCode,
                      postCode: postcode,
                      country: data.address.countryCode || fallbackCountryCode,
                    },
                  },
                }));
                setValue('companyName', data.companyName);
                setValue('registrationAddress.addressLine1', data.address.addressLine1);
                setValue('registrationAddress.addressLine2', data.address.addressLine2 ?? '');
                setValue('registrationAddress.addressLine3', data.address.addressLine3 ?? '');
                setValue('registrationAddress.addressLine4', data.address.addressLine4 ?? '');
                setValue('registrationAddress.addressLine5', data.address.addressLine5 ?? '');
                setValue('registrationAddress.postCode', postcode);
                setValue(
                  'registrationAddress.country',
                  data.address.countryCode || fallbackCountryCode
                );
              }}
            />
          )}
        </div>
        <div className={'mt-12'}>
          {[
            BusinessType.PublicLimited,
            BusinessType.PublicLimited_DE,
            BusinessType.SoleTrader,
            BusinessType.SoleTrader_Registered_Merchant,
            BusinessType.SoleTrader_Merchant,
            BusinessType.Other,
            BusinessType.Other_Entrepreneurial_Society,
            BusinessType.Other_Association_Incl_Charity,
            BusinessType.Other_DE,
            BusinessType.LimitedCompany,
            BusinessType.LimitedCompany_DE,
            BusinessType.LimitedCompany_Partnership_Shares,
            BusinessType.LimitedCompany_Partnership,
            BusinessType.Government,
            BusinessType.Government_DE,
            BusinessType.Partnership,
            BusinessType.Partnership_DE,
            BusinessType.Partnership_General,
            BusinessType.Non_Profit_Charity,
            BusinessType.Registered_Non_Profit_Charity,
          ].includes(wizardState.companyDetails.companyType as BusinessType) && (
            <TimeTradingForm icons={icons} timeTrading={timeTrading} />
          )}
        </div>
        <div className={'mt-12'}>
          {[BusinessType.Partnership, BusinessType.Partnership_DE].includes(
            wizardState.companyDetails.companyType as BusinessType
          ) && (
            <>
              <PartnerDetailsForm icons={icons} titleValues={titleValues} />
              <div
                className={`flex flex-col gap-2 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3 mb-6`}
                data-testid={`${baseDataTestId}-birthDayPicker`}
              >
                <span data-testid={`${baseDataTestId}-birthDayPicker-title`} className={nameStyle}>
                  {t('payApplication.companyDetails.partner.dateOfBirth')}
                </span>
                <DateOfBirthForm icons={icons} />
              </div>
            </>
          )}
        </div>
        <div className={'mt-12'}>
          {[
            BusinessType.PublicLimited,
            BusinessType.PublicLimited_DE,
            BusinessType.LimitedCompany_DE,
            BusinessType.Partnership_General,
            BusinessType.Other_DE,
          ].includes(wizardState.companyDetails.companyType as BusinessType) && (
            <ParentCompanyForm icons={icons} />
          )}
        </div>
        <ReviewChanges />
        <Analytics
          pageName="Pay Application: Company Details"
          errors={errors}
          businessType={wizardState.companyDetails.companyType}
          timeTrading={
            typeof timeTradingWatcher === 'string'
              ? timeTradingWatcher
              : timeTradingWatcher?.displayValue
          }
        />
      </WizardPage>
    </FormProvider>
  );
}

const formStyle = 'mt-12 p-6 border border-lightGrey3 bg-white rounded-lg';
const fieldStyle =
  'flex flex-col gap-2 py-6 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3';
const fieldStyleWithoutBorder =
  'flex flex-col gap-2 first:pt-0 last:pb-0 last:border-0 border-lightGrey3 border-b-0 py-0';
const fieldLabelStyle = 'font-bold';
const fieldValueStyle = '';
const nameStyle =
  'whitespace-nowrap text-lg leading-[1.375rem] text-darkGrey1 font-bold flex items-center mt-[0.125rem] mr-2';
const notificationStyle = 'mt-12';
const buttonStyle = 'h-14 font-semibold text-lg mobile:w-full mobile:whitespace-normal';
const buttonIconStyle = 'mr-2';
const titleSelectStyle = 'mb-6';
const titleSelectDropdownStyle = 'w-[136px]';
