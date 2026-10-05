'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES, AddressInfo, requestStatus, OptionType } from '@whitbread-eos/api';
import { Button, useToast, FormInput } from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  getCountryLanguageByLocale,
  useTranslation,
  formatIBAssetsUrl,
} from '@whitbread-eos/utils';
import {
  updateCompanyDetails,
  addressSchema,
  companyNameSchema,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Controller, FormProvider, useForm } from 'react-hook-form';
import { z } from 'zod';

import { AddressDetails } from '~components/innBusiness/AddressDetails';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { UserDetails } from '~components/innBusiness/UserDetails';
import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { CompanyAdditionalDetails } from './CompanyAdditionalDetails';
import { CompanyContainer } from './CompanyContainer';
import { CompanyDeleteInfo } from './CompanyDeleteInfo';
import { CompanyMainContact } from './CompanyMainContact';

type Props = {
  icons: Record<string, string>;
  mainContact: {
    title: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    phoneNumber: string;
    mobileNumber: string;
    position: string;
    id: string;
    address?: AddressInfo;
  };
  additionalDetails: {
    companySector: string;
    averageMonthlyBooking: string;
    numberOfEmployee: string;
  };
  companyName: string;
  alternateCompanyName: string;
  companyId: string;
  companyAddress?: AddressInfo;
  locale: LOCALES;
  mobile?: boolean;
};
type BadgeType = {
  mainContact: boolean;
  additionalInfo: boolean;
};

export function CompanyInformation({
  mainContact,
  icons,
  companyAddress,
  additionalDetails,
  locale,
  companyName,
  companyId,
}: Props) {
  const baseDataTestId = 'CompanyInformation';
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = useTranslation(['company']);
  const { toast } = useToast();
  const router = useRouter();
  const idTokenCookie = getAuthCookie();

  const [selectedEmployee, setSelectedEmployee] = useState<OptionType | null>(null);
  const [isFormDirty, setIsFormDirty] = useState(false);

  const [showBadge, setShowBadge] = useState<BadgeType>({
    mainContact: false,
    additionalInfo: false,
  });

  const [companyDetailsEdit, setCompanyDetailsEdit] = useState<boolean>(false);
  const [mainContactEdit, setMainContactEdit] = useState<boolean>(false);
  const [additionalDetailsEdit, setAdditionalDetailsEdit] = useState<boolean>(false);
  const [isUpdating, setIsUpdating] = useState(false);
  const [isReviewChangesOpen, setIsReviewChangesOpen] = useState(false);
  const [isMainContactReviewChangesOpen, setIsMainContactReviewChangesOpen] = useState(false);

  const schema = companyNameSchema(t)
    .merge(addressSchema(t))
    .merge(
      z
        .object({
          employeeId: z.string(),
          jobTitle: z.string().optional(),
          title: z.string().optional(),
          firstName: z.string().optional(),
          lastName: z.string().optional(),
          emailAddress: z.string().optional(),
          phoneNumber: z.string().optional(),
          mobileNumber: z.string().optional(),
        })
        .merge(
          z.object({
            companySector: z.string().optional(),
            averageMonthlyBooking: z.string().optional(),
            numberOfEmployee: z.string().optional(),
          })
        )
    );

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: companyName ?? '',
      addressLine1: companyAddress?.addressLine1 ?? '',
      addressLine2: companyAddress?.addressLine2 ?? '',
      addressLine3: companyAddress?.addressLine3 ?? '',
      addressLine4: companyAddress?.addressLine4 ?? '',
      addressLine5: companyAddress?.addressLine5 ?? '',
      country: companyAddress?.country ?? '',
      postCode: companyAddress?.postCode ?? '',
      //MAIN CONTACT:
      employeeId: mainContact.id ?? '',
      jobTitle: mainContact.position ?? '',
      title: mainContact?.title ?? '',
      firstName: mainContact?.firstName ?? '',
      lastName: mainContact?.lastName ?? '',
      emailAddress: mainContact?.emailAddress ?? '',
      phoneNumber: mainContact?.phoneNumber ?? '',
      mobileNumber: mainContact?.mobileNumber ?? '',
      //ADDITIONAL DETAILS
      companySector: additionalDetails?.companySector ?? '',
      averageMonthlyBooking: additionalDetails?.averageMonthlyBooking ?? '',
      numberOfEmployee: additionalDetails?.numberOfEmployee ?? '',
    },
  });

  const {
    control,
    formState: { errors, isDirty },
    trigger,
    getValues,
    reset,
    watch,
  } = formMethods;

  useEffect(() => {
    if (isDirty) {
      setIsFormDirty(true);
    }
  }, [isDirty]);

  const addressLine1 = watch('addressLine1');
  const addressLine2 = watch('addressLine2');
  const addressLine3 = watch('addressLine3');
  const addressLine4 = watch('addressLine4');
  const addressLine5 = watch('addressLine5');
  const postCode = watch('postCode');
  const country = watch('country');

  useEffect(() => {
    if (
      addressLine1 !== (companyAddress?.addressLine1 ?? '') ||
      addressLine2 !== (companyAddress?.addressLine2 ?? '') ||
      addressLine3 !== (companyAddress?.addressLine3 ?? '') ||
      addressLine4 !== (companyAddress?.addressLine4 ?? '') ||
      addressLine5 !== (companyAddress?.addressLine5 ?? '') ||
      postCode !== (companyAddress?.postCode ?? '') ||
      country !== (companyAddress?.country ?? '')
    ) {
      setIsFormDirty(true);
    }
  }, [addressLine1, addressLine2, addressLine3, addressLine4, addressLine5, postCode, country]);

  const mainContactValidation =
    !getValues('firstName') ||
    !getValues('lastName') ||
    !getValues('emailAddress') ||
    !getValues('title') ||
    !getValues('jobTitle') ||
    !(getValues('phoneNumber') || getValues('mobileNumber'));

  useEffect(() => {
    setShowBadge((prevState) => ({
      ...prevState,
      mainContact: mainContactValidation,
    }));
  }, [mainContactValidation]);

  const isAdditionalInfoIncomplete =
    !getValues('companySector') ||
    !getValues('averageMonthlyBooking') ||
    !getValues('numberOfEmployee');

  useEffect(() => {
    setShowBadge((prevState) => ({
      ...prevState,
      additionalInfo: isAdditionalInfoIncomplete,
    }));
  }, [isAdditionalInfoIncomplete]);

  const showBottomButtons = companyDetailsEdit || mainContactEdit || additionalDetailsEdit;

  const handleDiscardChanges = () => {
    setIsReviewChangesOpen(false);
    setIsMainContactReviewChangesOpen(false);
    setCompanyDetailsEdit(false);
    setMainContactEdit(false);
    setAdditionalDetailsEdit(false);
    reset();
  };

  const updateCompany = async () => {
    const isValid = await trigger();
    if (!isValid) return;

    if (isUpdating) return;
    setIsUpdating(true);

    const updateResponse = await updateCompanyDetails(
      companyId,
      {
        companyName: getValues('companyName'),
        alternateCompanyName: getValues('companyName'),
        companySector: getValues('companySector'),
        averageMonthlyBooking: getValues('averageMonthlyBooking'),
        numberOfEmployee: getValues('numberOfEmployee'),
        companyAddress: {
          addressLine1: getValues('addressLine1'),
          addressLine2: getValues('addressLine2'),
          addressLine3: getValues('addressLine3'),
          addressLine4: getValues('addressLine4'),
          addressLine5: getValues('addressLine5'),
          country: getValues('country'),
          postCode: getValues('postCode'),
        },
        mainContact: {
          id: getValues('employeeId'),
          position: getValues('jobTitle'),
          title: getValues('title'),
          firstName: getValues('firstName'),
          lastName: getValues('lastName'),
          emailAddress: getValues('emailAddress'),
          phoneNumber: getValues('phoneNumber'),
          mobileNumber: getValues('mobileNumber'),
        },
      },
      idTokenCookie
    );

    if (updateResponse?.status === requestStatus.success) {
      toast({
        content: t('company.notification.message.save'),
      });
      setCompanyDetailsEdit(false);
      setMainContactEdit(false);
      setAdditionalDetailsEdit(false);
      setSelectedEmployee(null);
      window.scrollTo({ top: 0, behavior: 'smooth' });
      router.refresh();
    } else {
      toast({
        content: t('company.notification.message.error'),
        variant: 'error',
      });
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
    setIsUpdating(false);
  };

  const handleEmployeeChange = (employee: OptionType) => {
    setSelectedEmployee(employee);
  };

  const showRegularReviewChanges =
    !isUpdating && (companyDetailsEdit || mainContactEdit || additionalDetailsEdit) && isFormDirty;

  const showMainContactReviewChanges = !(
    mainContact?.title &&
    mainContact?.firstName &&
    mainContact?.lastName &&
    mainContact?.emailAddress &&
    mainContact?.address?.addressLine1 &&
    mainContact?.address?.postCode &&
    mainContact?.address?.country
  );

  useEffect(() => {
    setIsMainContactReviewChangesOpen(!showMainContactReviewChanges);
  }, [showMainContactReviewChanges]);

  return (
    <FormProvider {...formMethods}>
      <div data-testid={`${baseDataTestId}-container`}>
        <div data-testid={`${baseDataTestId}-display-company-info-widget`} className={widgetStyle}>
          <CompanyContainer
            title={'coMngt.companyInfo.title'}
            locale={locale}
            showEditButton={!companyDetailsEdit}
            onEditChange={(value) => {
              setCompanyDetailsEdit(value);
            }}
          />
          {companyDetailsEdit ? (
            <div className="flex flex-col mt-2 mobile:mt-4 w-full">
              <Controller
                name="companyName"
                control={control}
                render={({ field }) => (
                  <FormInput
                    {...field}
                    id="Company-name"
                    placeholder={t('company.coMngt.companyInfo.company.name') + ' *'}
                    errors={errors}
                    errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                    onBlur={() => {
                      trigger('companyName');
                    }}
                  />
                )}
              />
              <CompanyAddressFields
                icons={icons}
                language={language}
                address={{
                  addressLine1: '',
                  country: '',
                  label: '',
                  postalCode: getValues('postCode'),
                }}
              />
            </div>
          ) : (
            <AddressDetails
              addressInfo={{
                addressLine1: getValues('addressLine1'),
                addressLine2: getValues('addressLine2'),
                addressLine3: getValues('addressLine3'),
                addressLine4: getValues('addressLine4'),
                addressLine5: getValues('addressLine5'),
                country: getValues('country'),
                postCode: getValues('postCode'),
              }}
              language={language}
              isCompanyInformation
              companyName={getValues('companyName')}
            />
          )}
        </div>
        <div
          data-testid={`${baseDataTestId}-display-main-contact-widget`}
          className={`${widgetStyle} mt-6`}
        >
          <CompanyContainer
            title={'coMngt.mainContact.title'}
            isIncomplete={showBadge.mainContact}
            locale={locale}
            showEditButton={!mainContactEdit}
            onEditChange={(value) => {
              setMainContactEdit(value);
            }}
          />
          {mainContactEdit ? (
            <CompanyMainContact
              icons={icons}
              companyId={companyId}
              mainContactInformation={mainContact}
              handleEmployeeChange={handleEmployeeChange}
              selectedEmployee={selectedEmployee}
            />
          ) : (
            <UserDetails
              userInformation={{
                title: getValues('title'),
                firstName: getValues('firstName'),
                lastName: getValues('lastName'),
                emailAddress: getValues('emailAddress'),
                phoneNumber: getValues('phoneNumber'),
                mobileNumber: getValues('mobileNumber'),
                position: getValues('jobTitle'),
              }}
            />
          )}
        </div>
        <div
          data-testid={`${baseDataTestId}-display-company-additional-details-widget`}
          className={`${widgetStyle} mt-6`}
        >
          <CompanyContainer
            title={'coMngt.additionalDetails.title'}
            isIncomplete={showBadge?.additionalInfo}
            locale={locale}
            showEditButton={!additionalDetailsEdit}
            onEditChange={(value) => {
              setAdditionalDetailsEdit(value);
            }}
          />
          <CompanyAdditionalDetails
            icons={icons}
            additionalDetailsInformation={additionalDetails}
            isEditMode={additionalDetailsEdit}
          />
        </div>

        {showBottomButtons && (
          <div className={buttonContainerStyle}>
            <Button
              data-testid={`${baseDataTestId}-Discard-changes-Company`}
              variant="editButton"
              size="newAddressButton"
              className={`${buttonStyle} justify-start mobile:justify-center order-1 mobile:order-2`}
              onClick={() => (isFormDirty ? setIsReviewChangesOpen(true) : handleDiscardChanges())}
            >
              {t('company.coMngt.discardChangesLink')}
            </Button>
            <Button
              data-testid={`${baseDataTestId}-Save-updates-Company`}
              variant="saveUpdatesButton"
              className={`${buttonStyle} order-2 mobile:order-1`}
              onClick={updateCompany}
              disabled={isUpdating}
            >
              {t('company.coMngt.saveUpdatesButton')}
            </Button>
          </div>
        )}

        <div
          data-testid={`${baseDataTestId}-delete-company-account-info-widget`}
          className={`${widgetStyle} rounded mt-12`}
        >
          <CompanyDeleteInfo icons={icons} locale={locale} />
        </div>
      </div>
      {showRegularReviewChanges && (
        <ReviewChanges
          isOpen={isReviewChangesOpen}
          onDiscard={handleDiscardChanges}
          onContinue={() => setIsReviewChangesOpen(false)}
          testId="ReviewChanges"
        />
      )}
      {showMainContactReviewChanges && (
        <ReviewChanges
          isOpen={isMainContactReviewChangesOpen}
          onDiscard={handleDiscardChanges}
          onContinue={() => setIsMainContactReviewChangesOpen(false)}
          dialogTitle={t('company.coMngt.mainContact.update.title')}
          dialogDescription={t('company.coMngt.mainContact.update.subtitle')}
          testId="MainContactReviewChanges"
        />
      )}
    </FormProvider>
  );
}

const widgetStyle = 'w-1/2 mobile:w-full p-6 rounded-lg bg-white border border-lightGrey3';
const buttonStyle = 'flex w-full';
const buttonContainerStyle =
  'mt-4 w-1/2 flex flex-row gap-3 justify-between mobile:flex-col mobile:w-full mobile:items-center';
