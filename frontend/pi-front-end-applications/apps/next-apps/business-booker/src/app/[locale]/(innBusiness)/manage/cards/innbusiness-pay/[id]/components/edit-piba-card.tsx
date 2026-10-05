'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  InnBpibaCardDetails,
  CARD_STATUS_WL_TYPE,
  CustomerAccountDetails,
  FormInnB,
  requestStatus,
  RegistrationRole,
  Language,
  EmployeeDetails,
  Scheme,
  CompanyDetailsResponse,
} from '@whitbread-eos/api';
import {
  FormPage,
  FormDatePicker,
  FormCheckbox,
  Notification,
  FormInput,
  Button,
  useToast,
} from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  useTranslation,
  getPathForLocale,
  getLocaleByPathname,
  formatIBAssetsUrl,
  cn,
  ParseDateToYMD,
} from '@whitbread-eos/utils';
import { IBPayCardLimitsSchema, updatePIBACardMutation } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { usePathname, useRouter } from 'next/navigation';
import React, { useState } from 'react';
import { useForm, Controller } from 'react-hook-form';

import { CardHolderRegistered } from '~components/innBusiness/CardHolderRegistered/index';
import { CardStatus } from '~components/innBusiness/CardStatus/index';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../../../components/Analytics/Analytics';
import { CancelCard } from './CancelCard/cancel-card';
import { ReplaceCard } from './ReplaceCard/replace-card';
import { ReplaceSuccessDialog } from './ReplaceCard/replace-success-dialog';

type Props = {
  icons: Record<string, string>;
  cardDetails: InnBpibaCardDetails;
  accountDetails: CustomerAccountDetails;
  calendarLabels: FormInnB | Record<string, never>;
  language: Language;
  employeeDetails?: EmployeeDetails;
  companyDetails: CompanyDetailsResponse;
  sendCardsToCardholder: boolean;
};

const tomorrow = new Date(new Date().setDate(new Date().getDate() + 1));

const dateInFiveYears = () => {
  const futureDate = new Date();
  futureDate.setFullYear(futureDate.getFullYear() + 5);
  return futureDate;
};

export function EditPIBACard({
  cardDetails,
  icons,
  accountDetails,
  calendarLabels,
  language,
  employeeDetails,
  companyDetails,
  sendCardsToCardholder,
}: Props) {
  const router = useRouter();
  const { toast } = useToast();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const idTokenCookie = getAuthCookie();
  const { t } = useTranslation(['cards']);
  const [reviewChangesEnabled, setReviewChangesEnabled] = useState(true);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [showFailError, setShowFailError] = useState(false);
  const [openReplaceCard, setOpenReplaceCard] = useState(false);
  const [openCancelCard, setOpenCancelCard] = useState(false);
  const [showSuccessDialog, setShowSuccessDialog] = useState(false);
  const isAccountHolder = accountDetails?.registrationRoles?.includes(
    RegistrationRole.AccountHolder
  );

  const startDate = cardDetails?.cardRestriction?.startDate ?? '';
  const endDate = cardDetails?.cardRestriction?.endDate ?? '';

  const schema = IBPayCardLimitsSchema(t).schema;
  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      creditLimit: !!cardDetails?.cardLimit,
      creditLimitNumber: cardDetails?.cardLimit?.toString() || '',
      usageRestriction: !!cardDetails?.cardRestriction?.restrictCardUsage,
      startDate: startDate ? new Date(startDate) : undefined,
      endDate: endDate ? new Date(endDate) : undefined,
    },
  });
  const {
    control,
    watch,
    formState: { errors, isDirty },
    setError,
    clearErrors,
    getValues,
  } = formMethods;

  const creditLimitOption = watch('creditLimit');
  const usageRestrictionOption = watch('usageRestriction');
  const startDateValue = watch('startDate');
  const endDateValue = watch('endDate');

  const expiryDate = new Date(cardDetails?.expiryDate ?? '');
  const formattedExpiryDate = `${(expiryDate.getMonth() + 1)
    .toString()
    .padStart(2, '0')}/${expiryDate.getFullYear()}`;

  const handleSave = async () => {
    const data = getValues();
    const isValid = IBPayCardLimitsSchema(t).validation(data, setError, clearErrors);
    if (!isValid) {
      return;
    }
    setReviewChangesEnabled(false);
    setIsSubmitting(true);
    const parsedData = {
      tetheredUserGuid: accountDetails?.tetheredGuid,
      cardId: cardDetails?.cardId?.toString(),
      countryCode: accountDetails?.scheme,
      updateInnBPIBACardRequest: {
        displayName: cardDetails?.cardHolderName,
        cardLimit: data.creditLimit ? Number(data.creditLimitNumber) : null,
        restrictCardUsage: data.usageRestriction,
        restrictionStart: data.usageRestriction ? ParseDateToYMD(data.startDate) : null,
        restrictionEnd: data.usageRestriction ? ParseDateToYMD(data.endDate) : null,
      },
    };
    const result = await updatePIBACardMutation(parsedData, idTokenCookie);
    if (result?.status === requestStatus.success) {
      toast({
        content: t('cards.cardMgmt.cardDetails.notification.confirmation'),
      });
      router.push(getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`));
    } else {
      setShowFailError(true);
      setIsSubmitting(false);
      window.scrollTo({ top: 0, behavior: 'smooth' });
    }
  };

  const correspondenceAddress = companyDetails?.requestedCompany?.companyDetails?.companyAddress;

  return (
    <FormPage
      iconClassName="px-2 py-3 max-w-[3rem]"
      baseDataTestId={'Inn-Business-Pay-Edit-Card'}
      backIcon={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
      backHref={getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`)}
      title={t('cards.centrallyStoredCard.card.details.title')}
    >
      {showFailError && (
        <Notification
          className={notificationStyle}
          type="error"
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          title={t('cards.cardMgmt.addCard.failure.heading')}
          message={t('cards.cardMgmt.addCard.failure.text')}
        />
      )}
      <div className={cardDetailsContainer}>
        <div
          className={cn(cardDetailsCol, 'border-r border-lightGrey3 pr-[2rem] mr-[2rem]')}
          data-testid={'Card-Number-Container'}
        >
          <span className="font-bold text-[1.25rem] mb-[0.5rem]">
            {t('cards.cardMgmt.cardDetails.cardNumber')}
          </span>
          <span data-testid={'Card-Number-Id'}>{cardDetails?.cardNumber?.slice(-8)}</span>
        </div>
        <div className={cardDetailsCol} data-testid={'Card-Expiry-Date-Container'}>
          <span className="font-bold text-[1.25rem] mb-[0.5rem]">
            {t('cards.cardMgmt.cardDetails.expiryDate')}
          </span>
          <span data-testid={'Card-Expiry-Date-Id'}>{formattedExpiryDate}</span>
        </div>
      </div>

      <div
        className={cn('form-details-box', 'flex flex-row')}
        data-testid={'Card-Status-Container'}
      >
        <span className={'font-bold'}>{t('cards.cardMgmt.columns.cardStatus')}</span>
        <CardStatus
          locale={locale}
          status={cardDetails.status as CARD_STATUS_WL_TYPE}
          isActivated={!!cardDetails.activated}
          cardDetails={cardDetails}
          icons={icons}
          accountHolder={accountDetails}
          token={idTokenCookie}
          className="ml-auto"
          onActivation={() => setReviewChangesEnabled(false)}
          afterActivation={() => setReviewChangesEnabled(true)}
        />
      </div>

      <div className={cn('form-details-box', 'mt-4')}>
        <div className="flex flex-row">
          <span className={'font-bold'}>{t('cards.cardMgmt.columns.cardHolderName')}</span>
          <CardHolderRegistered
            registered={!!cardDetails?.registeredUsers?.length}
            className={'ml-auto'}
            cardId={cardDetails?.cardId?.toString()}
            tetheredGuid={accountDetails?.tetheredGuid}
            scheme={accountDetails?.scheme ?? ('GB' as Scheme)}
            icons={icons}
            onResendingCode={(value) => setReviewChangesEnabled(value)}
          />
        </div>
        <div className={cardHolderDetails}>
          <span>{cardDetails?.cardHolderName}</span>
          <span>{employeeDetails?.emailAddress ?? ''}</span>
        </div>
      </div>

      {isAccountHolder && (
        <div className={limitsContainer}>
          <span className="font-bold text-[1.25rem] mb-8">
            {t('cards.cardMgmt.cardDetails.limits.title')}
          </span>
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
                  inputIcon={formatIBAssetsUrl(
                    t('cards.cardMgmt.cardDetails.limits.currency.icon')
                  )}
                  id="Credit-Limit-Number"
                  placeholder={t('cards.cardMgmt.cardDetails.limits.setCredit.placeholder')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                />
              )}
            />
          )}
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
                      disableDays={[{ before: tomorrow, after: endDateValue ?? dateInFiveYears() }]}
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
          {isDirty && (
            <Button
              data-testid="Edit-Card-Submit"
              variant="dialogDefault"
              className={buttonStyle}
              disabled={isSubmitting}
              onClick={handleSave}
            >
              {t('cards.centrallyStoredCard.card.edit.saveUpdates')}
            </Button>
          )}
        </div>
      )}

      <div className="w-full h-[1px] bg-lightGrey3 my-12" />
      <div>
        <button className="flex" onClick={() => setOpenReplaceCard(true)}>
          <Image
            alt={'Replace card'}
            src={formatIBAssetsUrl(t('cards.cardMgmt.cardDetails.replace.icon'))}
            width={24}
            height={24}
            data-testid="Replace-Card-Icon"
          />
          <span className={cn(linkStyle, 'ml-2')}>
            {t('cards.cardMgmt.cardDetails.replaceCard.button')}
          </span>
        </button>
        <span className={'flex mt-2 text-darkGrey2'}>
          {t('cards.cardMgmt.cardDetails.replace.description')}
        </span>
      </div>

      {isAccountHolder && cardDetails?.status !== CARD_STATUS_WL_TYPE.CANCELLED && (
        <div className="mt-8">
          <button
            data-testid="Cancel-Card-Button"
            className="flex"
            onClick={() => setOpenCancelCard(true)}
          >
            <Image
              alt={'Cancel card'}
              src={formatIBAssetsUrl(t('cards.cardMgmt.cardDetails.cancel.icon'))}
              width={24}
              height={24}
              data-testid="Cancel-Card-Icon"
            />
            <span className={cn(linkStyle, 'ml-2')}>
              {t('cards.cardMgmt.cardDetails.cancelCard.button')}
            </span>
          </button>
          <span className={'flex mt-2 text-darkGrey2'}>
            {t('cards.cardMgmt.cardDetails.resend.cancel.description')}
          </span>
        </div>
      )}
      {reviewChangesEnabled && isDirty && !openCancelCard && <ReviewChanges />}
      {openReplaceCard && (
        <ReplaceCard
          open={openReplaceCard}
          onOpenChange={(open) => setOpenReplaceCard(open)}
          icons={icons}
          cardDetails={cardDetails}
          language={language}
          employeeDetails={employeeDetails}
          accountDetails={accountDetails}
          correspondenceAddress={correspondenceAddress}
          sendCardsToCardholder={sendCardsToCardholder}
          onReplaceFail={() => {
            setShowFailError(true);
            setOpenReplaceCard(false);
            window.scrollTo({ top: 0, behavior: 'smooth' });
          }}
          onReplaceSuccess={() => {
            setShowSuccessDialog(true);
            setOpenReplaceCard(false);
            setShowFailError(false);
          }}
        />
      )}
      {openCancelCard && (
        <CancelCard
          locale={locale}
          scheme={accountDetails?.scheme ?? ('GB' as Scheme)}
          tetheredUserId={accountDetails?.tetheredGuid}
          cardId={cardDetails?.cardId?.toString()}
          open={openCancelCard}
          onOpenChange={(open) => setOpenCancelCard(open)}
          icons={icons}
        />
      )}
      {showSuccessDialog && (
        <ReplaceSuccessDialog
          open={showSuccessDialog}
          onOpenChange={(open) => setShowSuccessDialog(open)}
        />
      )}
      <Analytics
        pageName="Card Management"
        selectedSetCreditLimit={creditLimitOption}
        selectedRestrictedUsage={usageRestrictionOption}
      />
    </FormPage>
  );
}

const cardDetailsContainer = 'flex flex-row my-[3rem]';
const cardDetailsCol = 'flex flex-col grow';
const cardHolderDetails = 'flex flex-col mt-6';
const limitsContainer = 'flex flex-col mt-12';
const checkboxExtraTextStyle = 'text-sm ml-8 text-darkGrey2';
const calendarButtonStyle = '!min-w-[unset] w-full';
const calendarContainerStyle = 'min-w-[0] flex-1';
const notificationStyle = 'mt-4';
const dateLimitsStyle = 'flex gap-4 mt-4';
const linkStyle = 'text-secondaryColor underline';
const buttonStyle = 'flex w-full mt-12';
