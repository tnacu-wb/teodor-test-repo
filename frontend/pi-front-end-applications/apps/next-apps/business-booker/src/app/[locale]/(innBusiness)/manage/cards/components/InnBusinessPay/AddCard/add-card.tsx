'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  FormInnB,
  Language,
  requestStatus,
  requestErrors,
  OptionType,
  URLParams,
  cardUserType,
  cardDisplayNameOptions,
  AddressCorrespondenceEnum,
  UserAccessLevels,
} from '@whitbread-eos/api';
import { FormPage, Button, useToast, SanitizedContent } from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  ParseDateToYMD,
  useTranslation,
  getPathForLocale,
  getLocaleByPathname,
  formatIBAssetsUrl,
  cn,
} from '@whitbread-eos/utils';
import {
  addressSchema,
  addCardPIBAMutation,
  IBPayCardUserSchema,
  IBPayCardLimitsSchema,
  IBPayCardDeliverySchema,
} from '@whitbread-eos/utils/server';
import countries from 'i18n-iso-countries';
import enLocale from 'i18n-iso-countries/langs/en.json';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import React, { useEffect, useRef, useState } from 'react';
import { FormProvider, useForm } from 'react-hook-form';
import { isValidPhoneNumber } from 'react-phone-number-input';
import { z } from 'zod';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges/index';
import { CardDeliveryForm } from '~components/innBusiness/forms/AddCardForms/CardDeliveryForm';
import {
  CardUserForm,
  parseDisplayName,
} from '~components/innBusiness/forms/AddCardForms/CardUserForm';
import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

type Props = {
  icons: Record<string, string>;
  accountHolderContent: React.ReactNode;
  cardHolderName: string;
  companyId: string;
  employeeId: string;
  calendarLabels: FormInnB | Record<string, never>;
  language: Language;
  accessLevel: UserAccessLevels;
  loggedEmployeeDetails: Record<string, string>;
  costCenters?: Record<string, string>[];
  isAccountHolder?: boolean;
  isCostCentreManagementEnabled?: boolean;
  sendCardsToCardholder?: boolean;
};

export function AddCard({
  cardHolderName,
  icons,
  accountHolderContent,
  companyId,
  employeeId,
  calendarLabels,
  language,
  accessLevel,
  loggedEmployeeDetails,
  costCenters = [],
  isAccountHolder = false,
  isCostCentreManagementEnabled = false,
  sendCardsToCardholder = false,
}: Props) {
  const router = useRouter();
  const { toast } = useToast();
  countries.registerLocale(enLocale);
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const idTokenCookie = getAuthCookie();
  const searchParams = useSearchParams();
  const newEmployeeId = searchParams?.get(URLParams.employeeId);
  const { t } = useTranslation(['cards', 'users']);
  const [pageStep, setPageStep] = useState(1);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const isDeliveryStep = pageStep === 2;
  const [selectedEmployee, setSelectedEmployee] = useState<OptionType | null>(null);
  const deliveryStepRef = useRef<HTMLDivElement>(null);
  const shouldScrollToDeliveryStep = useRef(false);

  const normalizeInlineLinkSpacing = (message: string) =>
    message.replaceAll(/\s*<a\b[^>]*>.*?<\/a>\s*/g, (match) => `&nbsp;${match.trim()}&nbsp;`);

  const isCurrentUserPhoneValid = (phoneNumber: string) => {
    const trimmedPhoneNumber = phoneNumber?.trim();
    if (!trimmedPhoneNumber) {
      return false;
    }

    if (trimmedPhoneNumber.startsWith('+')) {
      return isValidPhoneNumber(trimmedPhoneNumber);
    }

    return isValidPhoneNumber(trimmedPhoneNumber, language === 'de' ? 'DE' : 'GB');
  };

  const buildPhoneInvalidToastContent = () => {
    const rawMessage = t('cards.cardMgmt.phoneNumber.formatInvalid');
    return (
      <span className="block min-w-0">
        <SanitizedContent>{normalizeInlineLinkSpacing(rawMessage)}</SanitizedContent>
      </span>
    );
  };

  const costCenterOptions = [
    ...(isAccountHolder
      ? [{ value: 'none', displayValue: t('cards.cardMgmt.costCentre.assign.value.none') }]
      : []),
    ...(costCenters?.map((costCenter) => ({
      value: costCenter.costCentreUniqueCustomerId,
      displayValue: `${costCenter.costCentreCode} - ${costCenter.costCentreName}`,
    })) ?? []),
  ];

  const schema = IBPayCardUserSchema(t)
    .schema.merge(IBPayCardLimitsSchema(t).schema)
    .merge(IBPayCardDeliverySchema(t))
    .merge(addressSchema(t));

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      tetheredGuid: undefined,
      schemeCustomerId: undefined,
      schemeCountry: undefined,
      apiUserGuid: undefined,
      user: newEmployeeId ? cardUserType.existing : cardUserType.me,
      cardDisplayNameOption: {
        value: cardDisplayNameOptions.fullName,
        displayValue: t('cards.cardMgmt.addCard.displayName.options.fullName'),
      },
      cardDisplayName: '',
      creditLimit: false,
      creditLimitNumber: '',
      usageRestriction: false,
      employeeId: newEmployeeId ?? employeeId,
      startDate: undefined,
      endDate: undefined,
      delivery: AddressCorrespondenceEnum.CompanyCorrespondenceAddress,
      title: {
        displayValue: selectedEmployee?.employeeData?.title ?? '',
        value: selectedEmployee?.employeeData?.title ?? '',
      },
      firstName: selectedEmployee?.employeeData?.firstName ?? '',
      lastName: selectedEmployee?.employeeData?.lastName ?? '',
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: '',
      postCode: '',
      costCenterOption: isCostCentreManagementEnabled ? costCenterOptions?.[0] : undefined,
    },
  });
  const {
    getValues,
    trigger,
    watch,
    setValue,
    formState: { isDirty },
  } = formMethods;

  useEffect(() => {
    setValue('title', {
      displayValue: selectedEmployee?.employeeData?.title ?? '',
      value: selectedEmployee?.employeeData?.title ?? '',
    });
    setValue('firstName', selectedEmployee?.employeeData?.firstName ?? '');
    setValue('lastName', selectedEmployee?.employeeData?.lastName ?? '');
  }, [selectedEmployee, setValue]);

  const userValueWatcher = watch('user');

  useEffect(() => {
    window?._satellite?.track('addNewCard');
  }, []);

  useEffect(() => {
    if (!isDeliveryStep || !shouldScrollToDeliveryStep.current) {
      return;
    }

    shouldScrollToDeliveryStep.current = false;
    deliveryStepRef.current?.scrollIntoView({ behavior: 'smooth', block: 'start' });
  }, [isDeliveryStep]);

  const pageStepDetails = () => {
    switch (pageStep) {
      case 1:
        return {
          title: t('cards.cardMgmt.addCard.pageTitle'),
          backHref: getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`),
          backClick: undefined,
        };
      case 2:
        return {
          title: t('cards.cardMgmt.delivery.title'),
          backHref: undefined,
          backClick: () => {
            setPageStep(pageStep - 1);
          },
        };
    }
  };

  const addCard = async () => {
    const isCustomAddress =
      getValues('delivery') === AddressCorrespondenceEnum.CardholderAlternativeAddress;

    const triggerAllExcept = async (excludedFields: string[]) => {
      const allFields = Object.keys(getValues());
      const fieldsToTrigger: z.infer<typeof schema>[] = allFields.filter(
        (field) => !excludedFields.includes(field)
      );
      return await trigger(fieldsToTrigger);
    };

    const isValid = isCustomAddress
      ? await trigger()
      : await triggerAllExcept(Object.keys(addressSchema(t).shape));
    if (!isValid) {
      return;
    }
    const isCurrentUser = getValues('user') === cardUserType.me;
    if (isCurrentUser && !isCurrentUserPhoneValid(loggedEmployeeDetails.phoneNumber)) {
      toast({
        content: buildPhoneInvalidToastContent(),
        variant: 'error',
      });
      return;
    }

    setIsSubmitting(true);
    const costCenterValue = getValues('costCenterOption')?.value;
    const isCustomName = getValues('cardDisplayNameOption').value === cardDisplayNameOptions.custom;
    const hasCostCenterId = costCenterValue !== 'none' && !!costCenterValue;
    const addCardResult = await addCardPIBAMutation(
      {
        tetheredUserGuid: getValues('tetheredGuid'),
        countryCode: getValues('schemeCountry'),
        addInnBPIBACardCriteria: {
          companyAccountId: isCurrentUser ? null : companyId,
          employeeAccountId: isCurrentUser ? null : getValues('employeeId'),
          apiUserGuid: getValues('apiUserGuid'),
          schemeCustomerId: hasCostCenterId
            ? Number(costCenterValue)
            : getValues('schemeCustomerId'),
          primarySchemeCustomerId: getValues('schemeCustomerId'),
          displayName: isCustomName
            ? getValues('cardDisplayName')
            : parseDisplayName(
                getValues('cardDisplayNameOption').value,
                selectedEmployee?.employeeData?.title ?? '',
                selectedEmployee?.employeeData?.firstName ?? '',
                selectedEmployee?.employeeData?.lastName ?? ''
              ),
          cardLimit: getValues('creditLimit') ? Number(getValues('creditLimitNumber')) : null,
          restrictCardUsage: getValues('usageRestriction'),
          restrictionStart: getValues('usageRestriction')
            ? ParseDateToYMD(getValues('startDate'))
            : null,
          restrictionEnd: getValues('usageRestriction')
            ? ParseDateToYMD(getValues('endDate'))
            : null,
          cardDeliveryAddressType: getValues('delivery'),
          cardCorrespondenceAddress: isCustomAddress
            ? {
                associateAddressWithFutureCardholder: false,
                forename: getValues('firstName'),
                surname: getValues('lastName'),
                title: getValues('title').value,
                postCode: getValues('postCode'),
                line1: getValues('addressLine1'),
                line2: getValues('addressLine2'),
                line3: getValues('addressLine3'),
                line4: getValues('addressLine4'),
                countryCodeISO: countries.alpha2ToAlpha3(getValues('country')),
              }
            : null,
        },
      },
      idTokenCookie
    );
    if (addCardResult?.status === requestStatus.success) {
      router.push(getPathForLocale(locale, 'manage/cards/innbusiness-pay/add/confirmation'));
      return;
    }

    setIsSubmitting(false);
    if (addCardResult?.errorCode === requestErrors.phoneNumberInvalid) {
      toast({
        content: buildPhoneInvalidToastContent(),
        variant: 'error',
      });
      return;
    }

    router.push(getPathForLocale(locale, 'manage/cards/innbusiness-pay/add/fail'));
  };

  const onStepContinue = () => {
    shouldScrollToDeliveryStep.current = true;
    setPageStep(2);
  };

  return (
    <FormProvider {...formMethods}>
      <FormPage
        iconClassName="px-2 py-3 max-w-[3rem]"
        baseDataTestId={'Inn-Business-Pay-Add-Card'}
        backIcon={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
        backHref={pageStepDetails()?.backHref}
        title={pageStepDetails()?.title}
        onBackClick={pageStepDetails()?.backClick}
      >
        {accountHolderContent}
        <CardUserForm
          className={pageStep !== 1 ? 'hidden' : ''}
          icons={icons}
          cardHolderName={cardHolderName}
          companyId={companyId}
          defaultEmployeeId={employeeId}
          onContinue={onStepContinue}
          calendarLabels={calendarLabels}
          language={language}
          token={idTokenCookie}
          updateSelectedEmployee={(value: OptionType) => setSelectedEmployee(value)}
          accessLevel={accessLevel}
          loggedEmployeeDetails={loggedEmployeeDetails}
          costCenterOptions={costCenterOptions}
          isCostCentreManagementEnabled={isCostCentreManagementEnabled}
        />
        <div ref={deliveryStepRef} className="scroll-mt-6" data-testid="Add-Card-Delivery-Step">
          <CardDeliveryForm
            className={isDeliveryStep ? '' : 'hidden'}
            icons={icons}
            language={language}
            disableEmployeeDetails={true}
            addressComponent={<CompanyAddressFields icons={icons} language={language} />}
            isMyCard={userValueWatcher === cardUserType.me}
            sendCardsToCardholder={sendCardsToCardholder}
          />
        </div>
        <Button
          data-testid="Add-Card-Submit"
          variant="dialogDefault"
          className={cn(buttonStyle, isDeliveryStep ? '' : 'hidden')}
          onClick={() => addCard()}
          disabled={isSubmitting}
        >
          {t('cards.cardMgmt.delivery.createCardButton')}
        </Button>
        {!isSubmitting && isDirty && <ReviewChanges />}
      </FormPage>
    </FormProvider>
  );
}

const buttonStyle = 'flex w-full mt-12';
