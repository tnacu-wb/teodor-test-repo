'use client';

import {
  FormInnB,
  Language,
  SS_ALTERNATE_PATH,
  URLParams,
  OptionType,
  cardUserType,
  cardDisplayNameOptions,
  UserAccessLevels,
} from '@whitbread-eos/api';
import {
  FormDatePicker,
  FormSelect,
  Button,
  FormRadioGroup,
  FormCheckbox,
  FormPeoplePicker,
  Notification,
  FormInput,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import {
  getPathForLocale,
  GLOBALS,
  formatIBAssetsUrl,
  useTranslation,
  getLocaleByPathname,
  cn,
} from '@whitbread-eos/utils';
import {
  IBPayCardLimitsSchema,
  getAccountList,
  getSelectedAccountHolder,
  IBPayCardUserSchema,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { usePathname, useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

import { INN_BUSINESS_PAY_TAB_SEARCH_PARAM } from '~components/constants/constants';

export const parseDisplayName = (
  option: string,
  title: string,
  firstName: string,
  lastName: string
) => {
  switch (option) {
    case cardDisplayNameOptions.fullName:
      return `${firstName} ${lastName}`;
    case cardDisplayNameOptions.titleFullName:
      return `${title} ${firstName} ${lastName}`;
    case cardDisplayNameOptions.initialLastName:
      return `${firstName[0]} ${lastName}`;
    case cardDisplayNameOptions.titleInitialLastName:
      return `${title} ${firstName[0]} ${lastName}`;
  }
};

const dateInFiveYears = () => {
  const futureDate = new Date();
  futureDate.setFullYear(futureDate.getFullYear() + 5);
  return futureDate;
};

type Props = {
  icons: Record<string, string>;
  cardHolderName: string;
  companyId: string;
  defaultEmployeeId: string;
  onContinue?: () => void;
  calendarLabels: FormInnB | Record<string, never>;
  language: Language;
  token: string;
  updateSelectedEmployee: (value: OptionType) => void;
  className?: string;
  accessLevel: UserAccessLevels;
  hideContinueButton?: boolean;
  hideAddEmployeeButton?: boolean;
  loggedEmployeeDetails: Record<string, string>;
  isPayApp?: boolean;
  costCenterOptions?: Record<string, number | string>[];
  isCostCentreManagementEnabled?: boolean;
  applicationId?: string;
  applicationGuid?: string;
};

export function CardUserForm({
  cardHolderName,
  icons,
  companyId,
  defaultEmployeeId,
  onContinue,
  calendarLabels,
  language,
  token,
  updateSelectedEmployee,
  className,
  accessLevel,
  loggedEmployeeDetails,
  hideContinueButton = false,
  hideAddEmployeeButton = false,
  isPayApp = false,
  applicationId,
  applicationGuid,
  costCenterOptions = [],
  isCostCentreManagementEnabled = false,
}: Readonly<Props>) {
  const hasNoCostCenter =
    (costCenterOptions.length === 1 && costCenterOptions[0].value === 'none') ||
    costCenterOptions.length === 0;
  const hasSingleCostCenterOption =
    costCenterOptions.length === 1 && costCenterOptions[0].value !== 'none';
  const isDELanguage = language === GLOBALS.language.DE;
  const router = useRouter();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const searchParams = useSearchParams();
  const urlEmployeeId = searchParams?.get(URLParams.employeeId);
  const accountHolderNumber = searchParams?.get(INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT);
  const { t } = useTranslation(['cards', 'users', 'payApplication']);
  const [consentValue, setConsentValue] = useState<string | undefined>('yes');
  const tomorrow = new Date(new Date().setDate(new Date().getDate() + 1));
  const hasConsent = consentValue === 'yes';
  const isBooker = accessLevel === UserAccessLevels.BOOKER;
  const isSelfBooker = accessLevel === UserAccessLevels.SELF;
  const isGuest = accessLevel === UserAccessLevels.STAYER;
  const isTravelManager = accessLevel === UserAccessLevels.SUPER;
  const isBusinessPayManager = accessLevel === UserAccessLevels.BUSINESS_PAY_MANAGER;
  const isBusinessPayUser = accessLevel === UserAccessLevels.BUSINESS_PAY_USER;
  const showExistingEmployeeOption = isBooker || isTravelManager || isBusinessPayManager;
  const showAddNewEmployeeButton =
    (isTravelManager || isBusinessPayManager) && !hideAddEmployeeButton;

  const userOptions = [
    {
      value: cardUserType.me,
      label: t('cards.cardMgmt.addCard.who.options.me'),
    },
  ];

  if (showExistingEmployeeOption) {
    userOptions.push({
      value: cardUserType.existing,
      label: t('cards.cardMgmt.addCard.who.options.existing'),
    });
  }

  const displayNameOptions = [
    {
      value: cardDisplayNameOptions.fullName,
      displayValue: t('cards.cardMgmt.addCard.displayName.options.fullName'),
    },
    {
      value: cardDisplayNameOptions.titleFullName,
      displayValue: t('cards.cardMgmt.addCard.displayName.options.titleFullName'),
    },
    {
      value: cardDisplayNameOptions.initialLastName,
      displayValue: t('cards.cardMgmt.addCard.displayName.options.initialLastName'),
    },
    {
      value: cardDisplayNameOptions.titleInitialLastName,
      displayValue: t('cards.cardMgmt.addCard.displayName.options.titleInitialLastName'),
    },
    {
      value: cardDisplayNameOptions.custom,
      displayValue: t('cards.cardMgmt.addCard.displayName.options.customFormat'),
    },
  ];

  const {
    control,
    formState: { errors },
    watch,
    setError,
    setValue,
    clearErrors,
    trigger,
    getValues,
  } = useFormContext();

  const cardUserValue = watch('user');
  const employeeIdValue = watch('employeeId');
  const displayNameOption = watch('cardDisplayNameOption');
  const usageRestrictionOption = watch('usageRestriction');
  const startDateValue = watch('startDate');
  const endDateValue = watch('endDate');
  const creditLimitOption = watch('creditLimit');
  const isCustomName = displayNameOption.value === cardDisplayNameOptions.custom;
  const isCurrentUser = cardUserValue === cardUserType.me;

  useEffect(() => {
    if (isCurrentUser) {
      setConsentValue('yes');
      setValue('employeeId', defaultEmployeeId);
      updateSelectedEmployee({
        value: defaultEmployeeId,
        label: '',
        employeeData: loggedEmployeeDetails,
      });
    } else {
      setConsentValue(isDELanguage ? undefined : 'yes');
      setValue('employeeId', '');
    }
  }, [cardUserValue]);

  useEffect(() => {
    const getAccountHolderDetails = async () => {
      const accounts = await getAccountList(token);
      const selectedAccountHolder = getSelectedAccountHolder(accounts, accountHolderNumber);
      setValue('tetheredGuid', selectedAccountHolder?.tetheredGuid);
      setValue('schemeCustomerId', selectedAccountHolder?.schemeCustomerId);
      setValue('schemeCountry', selectedAccountHolder?.scheme);
      setValue('apiUserGuid', selectedAccountHolder?.apiUserGuid);
    };
    getAccountHolderDetails();
  }, []);

  useEffect(() => {
    if (isSelfBooker || isGuest || isBusinessPayUser) {
      setValue('user', cardUserType.me);
    }
  }, [isSelfBooker, isGuest, isBusinessPayUser, setValue]);

  useEffect(() => {
    if (urlEmployeeId && showExistingEmployeeOption && applicationId && applicationGuid) {
      setValue('user', cardUserType.existing);
      router.replace(
        getPathForLocale(
          locale,
          `business-pay/apply?applicationId=${applicationId}&applicationGuid=${applicationGuid}`
        )
      );
    }
  }, [urlEmployeeId, showExistingEmployeeOption, applicationId, applicationGuid]);

  const handleAddEmployeeClick = () => {
    const currentUrl = isPayApp
      ? `${window.location.origin}${window.location.pathname}?applicationId=${applicationId}&applicationGuid=${applicationGuid}`
      : `${window.location.origin}${window.location.pathname}?${INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT}=${accountHolderNumber}`;
    sessionStorage.setItem(SS_ALTERNATE_PATH, currentUrl);
    const backUrl = isPayApp
      ? `business-pay/apply?applicationId=${applicationId}&applicationGuid=${applicationGuid}`
      : `manage/cards/innbusiness-pay/add?${INN_BUSINESS_PAY_TAB_SEARCH_PARAM.ACCOUNT}=${accountHolderNumber}`;
    router.push(
      getPathForLocale(
        locale,
        `manage/employees/add?${URLParams.alternateSource}=1&backUrl=${encodeURIComponent(backUrl)}`
      )
    );
  };

  const onContinueClick = async () => {
    const isValid =
      IBPayCardUserSchema(t).validation(getValues(), setError, clearErrors) &&
      IBPayCardLimitsSchema(t).validation(getValues(), setError, clearErrors);

    if (isValid) {
      onContinue?.();
    }
  };

  const handleEmployeeChange = (employee: OptionType) => {
    updateSelectedEmployee(employee);
  };

  const separator = <div className="form-box-separator" />;

  const costCenterSelect = isCostCentreManagementEnabled && !hasNoCostCenter && (
    <>
      {separator}
      <div className={sectionStyle} data-testid="Select-Cost-Center-Section">
        <span className={sectionSubtitleStyle}>{t('cards.cardMgmt.costCentre.assign.label')}</span>
        {hasSingleCostCenterOption ? (
          <span>{costCenterOptions[0].displayValue}</span>
        ) : (
          <Controller
            name="costCenterOption"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="costCenterOption"
                errors={errors}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={costCenterOptions}
              />
            )}
          />
        )}
      </div>
    </>
  );

  const consentCheck = isDELanguage && cardUserValue !== cardUserType.me && (
    <div className={whoContainerStyle}>
      <span data-testid="Who-Container-Title" className={whoTitleStyle}>
        {t('cards.cardMgmt.consent.label')}
      </span>
      <Notification
        className={notificationStyle}
        type="info"
        icon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
        message={<SanitizedContent>{`${t('cards.cardMgmt.consent.info')}`}</SanitizedContent>}
      />
      <FormRadioGroup
        data-testid="Consent-Radio"
        items={[
          {
            value: 'no',
            label: t('cards.cardMgmt.consent.option.no'),
          },
          {
            value: 'yes',
            label: t('cards.cardMgmt.consent.option.yes'),
          },
        ]}
        onChange={(value: string) => setConsentValue(value)}
        variant="row"
        selectedValue={consentValue}
        className={'mt-4'}
      />
      {consentValue === 'no' && (
        <Notification
          className={notificationStyle}
          type="warning"
          icon={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
          message={t('cards.cardMgmt.consent.warning')}
        />
      )}
    </div>
  );

  const restrictedAccessNotification = (
    <Notification
      className="mb-6 mt-4"
      type="info"
      icon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
      title={t('cards.cardMgmt.restrictedAccess.title')}
      message={t('cards.cardMgmt.restrictedAccess.description')}
    />
  );

  const payAppNotification = (
    <Notification
      className="mb-6"
      type="info"
      icon={formatIBAssetsUrl(icons?.['icon.notification.info'])}
      message={t('payApplication.card.add.existingEmployee.info')}
    />
  );

  return (
    <div className={cn('flex flex-col', className)}>
      {!isTravelManager && !isBusinessPayManager && restrictedAccessNotification}

      {(isBooker || isTravelManager || isBusinessPayManager) && (
        <div className={whoContainerStyle}>
          <span data-testid="Who-Container-Title" className={whoTitleStyle}>
            {t('cards.cardMgmt.addCard.who.title')}
          </span>
          <Controller
            name="user"
            control={control}
            render={({ field }) => (
              <FormRadioGroup
                {...field}
                data-testid="userRadioGroup"
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                errors={errors}
                items={userOptions}
                onChange={(value: string) => field.onChange(value)}
                variant="col-styled"
                selectedValue={cardUserValue}
              />
            )}
          />
        </div>
      )}

      {showAddNewEmployeeButton && (
        <Button
          data-testid="Add-Card-New-Employee-Button"
          variant={'alternativeDefault'}
          className={newEmployeeButtonStyle}
          onClick={handleAddEmployeeClick}
        >
          <Image
            alt={'Add Employee Icon'}
            src={formatIBAssetsUrl(t('cards.icon.addEmployee-icon'))}
            width={24}
            height={24}
            className={buttonIconStyle}
          />
          {t('cards.cardMgmt.addCard.addNewEmployee')}
        </Button>
      )}

      {consentCheck}

      <Image
        width={420}
        height={220}
        alt={'Inn Business Pay Card Image'}
        src={formatIBAssetsUrl(t('cards.cardMgmt.addCard.image'))}
        unoptimized={true}
        className={'mx-auto my-12'}
      />

      {isPayApp && !isCurrentUser && hasConsent && payAppNotification}

      <div className={cn(formContainerStyle, hasConsent ? '' : 'hidden')}>
        <div data-testid="Card-Holder-Section" className={sectionStyle}>
          <span className={sectionSubtitleStyle}>
            {t('cards.cardMgmt.addCard.cardHolderName.label')}
          </span>
          {isCurrentUser && <span data-testid="Card-Holder-Name">{cardHolderName}</span>}
          <Controller
            name="employeeId"
            control={control}
            render={({ field }) => (
              <FormPeoplePicker
                {...field}
                className={isCurrentUser ? 'hidden' : ''}
                id="PeoplePicker"
                companyId={companyId}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                placeholder={t('users.userMgmt.manageEmployees.seach.placeholder') + ' *'}
                setError={setError}
                onBlur={() => {
                  trigger('employeeId');
                }}
                handleEmployeeChange={handleEmployeeChange}
              />
            )}
          />
        </div>

        {separator}

        <div className={sectionStyle}>
          <span className={sectionSubtitleStyle}>
            {t('cards.cardMgmt.addCard.displayName.label')}
          </span>
          <Controller
            name="cardDisplayNameOption"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="cardDisplayNameOption"
                placeholder={t('cards.cardMgmt.addCard.nameOnCard.label')}
                errors={errors}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={displayNameOptions}
              />
            )}
          />
          {isCustomName && (
            <Controller
              name="cardDisplayName"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  containerClassName={'mt-6'}
                  id="cardDisplayName"
                  placeholder={t('cards.cardMgmt.addCard.displayName.typeName.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                />
              )}
            />
          )}
        </div>

        {costCenterSelect}

        {separator}

        <Controller
          name="creditLimit"
          control={control}
          render={({ field }) => (
            <FormCheckbox
              {...field}
              id={'Credit-Limit'}
              className="mb-2 py-[10px]"
              label={
                <>
                  <span className="font-bold">
                    {t('cards.cardMgmt.cardDetails.limits.setCredit.label')}
                  </span>
                  <span className="ml-1">{t('cards.cardMgmt.optional')}</span>
                </>
              }
            />
          )}
        />
        <span className={checkboxExtraTextStyle}>
          {t('cards.cardMgmt.cardDetails.limits.setCreditLimit.info')}
        </span>
        {creditLimitOption && (
          <Controller
            name="creditLimitNumber"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                containerClassName={'mt-4'}
                inputIcon={formatIBAssetsUrl(t('cards.cardMgmt.cardDetails.limits.currency.icon'))}
                id="Credit-Limit-Number"
                placeholder={t('cards.cardMgmt.cardDetails.limits.setCredit.placeholder')}
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              />
            )}
          />
        )}

        {!isPayApp && (
          <>
            <Controller
              name="usageRestriction"
              control={control}
              render={({ field }) => (
                <FormCheckbox
                  {...field}
                  id={'Date-Restriction'}
                  className="mt-6 mb-2 py-[10px]"
                  label={
                    <>
                      <span className="font-bold">
                        {t('cards.cardMgmt.cardDetails.limits.retrictUsage.label')}
                      </span>
                      <span className="ml-1">{t('cards.cardMgmt.optional')}</span>
                    </>
                  }
                />
              )}
            />
            <span className={checkboxExtraTextStyle}>
              {t('cards.cardMgmt.cardDetails.limits.retrictUsage.info')}
            </span>
            {usageRestrictionOption && (
              <div>
                <Notification
                  className={notificationStyle}
                  type="info"
                  icon={formatIBAssetsUrl(icons['icon.notification.info'])}
                  message={t('cards.cardMgmt.cardDetails.limits.infoMessage')}
                />
                <div className={dateLimitsStyle}>
                  <Controller
                    name="startDate"
                    control={control}
                    render={({ field }) => (
                      <FormDatePicker
                        name={field.name}
                        value={field.value}
                        onChange={field.onChange}
                        id={'Start-Date-Picker'}
                        errors={errors}
                        errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                        placeholder={t('cards.cardMgmt.cardDetails.limits.start.date')}
                        locale={locale}
                        icons={icons}
                        calendarLabels={calendarLabels}
                        disableDays={[
                          { before: tomorrow, after: endDateValue ?? dateInFiveYears() },
                        ]}
                        disableMonthBefore={tomorrow}
                        disableMonthAfter={dateInFiveYears()}
                        containerClassName={calendarContainerStyle}
                        buttonClassName={calendarButtonStyle}
                      />
                    )}
                  />
                  <Controller
                    name="endDate"
                    control={control}
                    render={({ field }) => (
                      <FormDatePicker
                        name={field.name}
                        value={field.value}
                        onChange={field.onChange}
                        id={'End-Date-Picker'}
                        errors={errors}
                        errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                        placeholder={t('cards.cardMgmt.cardDetails.limits.end.date')}
                        locale={locale}
                        icons={icons}
                        calendarLabels={calendarLabels}
                        disableDays={[
                          { before: startDateValue ?? tomorrow, after: dateInFiveYears() },
                        ]}
                        disableMonthBefore={tomorrow}
                        disableMonthAfter={dateInFiveYears()}
                        containerClassName={calendarContainerStyle}
                        buttonClassName={calendarButtonStyle}
                      />
                    )}
                  />
                </div>
              </div>
            )}
          </>
        )}
      </div>
      {!hideContinueButton && (
        <Button
          data-testid="Continue-Button-Add-Card"
          variant="dialogDefault"
          className={buttonStyle}
          onClick={onContinueClick}
          type="button"
          disabled={!employeeIdValue || !hasConsent}
        >
          {t('cards.cardMgmt.addCard.displayName.continueButton')}
        </Button>
      )}
    </div>
  );
}

const whoContainerStyle = 'flex flex-col mt-12';
const whoTitleStyle = 'font-bold text-xl';
const buttonIconStyle = 'mr-2';
const newEmployeeButtonStyle = 'w-full mt-[1.25rem]';
const sectionStyle = 'flex flex-col';
const sectionSubtitleStyle = 'font-bold mb-4';
const formContainerStyle = 'form-details-box mb-12';
const checkboxExtraTextStyle = 'text-sm ml-8 text-darkGrey2';
const buttonStyle = 'flex w-full';
const dateLimitsStyle = 'flex gap-4 mt-4';
const notificationStyle = 'mt-4';
const calendarButtonStyle = '!min-w-[unset] w-full';
const calendarContainerStyle = 'min-w-[0] flex-1';
