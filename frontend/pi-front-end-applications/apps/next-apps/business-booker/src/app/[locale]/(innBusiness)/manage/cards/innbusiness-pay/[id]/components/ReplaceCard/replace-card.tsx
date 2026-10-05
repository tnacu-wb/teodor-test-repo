'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  InnBpibaCardDetails,
  Language,
  EmployeeDetails,
  CustomerAccountDetails,
  AddressCorrespondenceEnum,
  requestStatus,
  CardReplaceAddress,
} from '@whitbread-eos/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  FormRadioGroup,
  SanitizedContent,
  Notification,
} from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import {
  IBPayCardDeliverySchema,
  cancelAndReplacePIBACardMutation,
  replaceCardMutation,
  addressSchema,
} from '@whitbread-eos/utils/server';
import countries from 'i18n-iso-countries';
import enLocale from 'i18n-iso-countries/langs/en.json';
import { useState } from 'react';
import { Controller, useForm, FormProvider } from 'react-hook-form';
import { z } from 'zod';

import { CardDeliveryForm } from '~components/innBusiness/forms/AddCardForms/CardDeliveryForm';
import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { Analytics } from '../../../../components/Analytics/Analytics';

const enum StepsEnum {
  reason = 'reason',
  delivery = 'delivery',
}

const enum reason {
  lost = 'This card has been lost or stolen',
  damaged = "This card is damaged or doesn't work",
}

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  icons: Record<string, string>;
  cardDetails: InnBpibaCardDetails;
  language: Language;
  employeeDetails?: EmployeeDetails;
  accountDetails: CustomerAccountDetails;
  correspondenceAddress?: CardReplaceAddress;
  onReplaceFail: () => void;
  onReplaceSuccess: () => void;
  sendCardsToCardholder: boolean;
};

export const getErrorMessage = (error: any): string | null => {
  if (!error || typeof error !== 'object') {
    return null;
  }
  if (typeof error?.message === 'string') {
    return error.message;
  }
  for (const key of Object.keys(error)) {
    const errorMessage = getErrorMessage(error[key]);
    if (errorMessage) {
      return errorMessage;
    }
  }
  return null;
};

export const ReplaceCard = ({
  open,
  onOpenChange,
  icons,
  cardDetails,
  language,
  employeeDetails,
  accountDetails,
  correspondenceAddress,
  sendCardsToCardholder,
  onReplaceFail,
  onReplaceSuccess,
}: Props) => {
  countries.registerLocale(enLocale);

  const idTokenCookie = getAuthCookie();

  const { t } = useTranslation(['cards', 'users']);
  const [step, setStep] = useState<StepsEnum>(StepsEnum.reason);
  const [isReplaceInProgress, setIsReplaceInProgress] = useState(false);

  const schema: any = z
    .object({
      reason: z.string().min(1),
    })
    .merge(IBPayCardDeliverySchema(t))
    .merge(addressSchema(t));

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      reason: reason.lost,
      delivery: AddressCorrespondenceEnum.CompanyCorrespondenceAddress,
      title: {
        displayValue: employeeDetails?.title ?? '',
        value: employeeDetails?.title ?? '',
      },
      firstName: employeeDetails?.firstName ?? '',
      lastName: employeeDetails?.lastName ?? '',
      addressLine1: '',
      addressLine2: '',
      addressLine3: '',
      addressLine4: '',
      addressLine5: '',
      country: '',
      postCode: '',
    },
  });

  const {
    control,
    formState: { errors },
    watch,
    getValues,
    trigger,
  } = formMethods;

  const deliveryTypeWatcher = watch('delivery');
  const titleWatcher = watch('title');
  const firstNameWatcher = watch('firstName');
  const lastNameWatcher = watch('lastName');
  const addressLine1Watcher = watch('addressLine1');
  const postCodeWatcher = watch('postCode');

  const values = getValues();
  const isCustomAddress =
    deliveryTypeWatcher === AddressCorrespondenceEnum.CardholderAlternativeAddress;
  const analyTicsCardReplaceAddress = isCustomAddress
    ? {
        title: values.title.value,
        forename: values.firstName,
        surname: values.lastName,
        addressLine1: values.addressLine1,
        addressLine2: values.addressLine2,
        addressLine3: values.addressLine3,
        addressLine4: values.addressLine4,
        addressLine5: values.addressLine5,
        postCode: values.postCode,
        country: countries.alpha2ToAlpha3(values.country) || '',
      }
    : correspondenceAddress;
  const errorMessages = Object.values(errors ?? {})
    .map((error) => getErrorMessage(error))
    .filter(Boolean) as string[];
  const analyticsValidation =
    errorMessages.length > 1 ? errorMessages.join(', ') : errorMessages[0] || '';

  const handleContinue = async () => {
    if (step === StepsEnum.reason) {
      setStep(StepsEnum.delivery);
      return;
    } else {
      const triggerAllExcept = async (excludedFields: string[]) => {
        const allFields = Object.keys(getValues());
        const fieldsToTrigger: z.infer<typeof schema>[] = allFields.filter(
          (field) => !excludedFields.includes(field)
        );
        return await trigger(fieldsToTrigger);
      };

      const isValid = isCustomAddress
        ? await trigger()
        : await triggerAllExcept([
            ...Object.keys(addressSchema(t).shape),
            ...Object.keys(IBPayCardDeliverySchema(t).shape),
          ]);

      if (!isValid) {
        return;
      }
      setIsReplaceInProgress(true);

      const data = {
        tetheredUserId: accountDetails?.tetheredGuid,
        cardId: cardDetails?.cardId?.toString(),
        cancelAndReplaceInnBCardRequest: {
          issueReplacement: true,
          scheme: accountDetails?.scheme,
          cardDeliveryAddressType: values.delivery,
          cardCorrespondenceAddress: isCustomAddress
            ? {
                title: values.title.value,
                forename: values.firstName,
                surname: values.lastName,
                line1: values.addressLine1,
                line2: values.addressLine2,
                line3: values.addressLine3,
                line4: values.addressLine4,
                postCode: values.postCode,
                countryCodeISO: countries.alpha2ToAlpha3(values.country),
                associateAddressWithFutureCardholder: false,
              }
            : null,
        },
      };
      const replaceCardRequestInfo = isCustomAddress
        ? {
            shouldDespatchToCardholder: true,
            contactDetails: {
              title: values.title.value,
              foreName: values.firstName,
              lastName: values.lastName,
            },
            address: {
              addressLine1: values.addressLine1,
              addressLine2: values.addressLine2,
              addressLine3: values.addressLine3,
              addressLine4: values.addressLine4,
              postcode: values.postCode,
              countryCode: values.country,
            },
          }
        : { shouldDespatchToCardholder: false };
      const replaceCardData = {
        tetheredUserGuid: accountDetails?.tetheredGuid,
        cardId: cardDetails?.cardId?.toString(),
        scheme: accountDetails?.scheme,
        replaceCardRequest: accountDetails?.scheme === 'DE' ? {} : replaceCardRequestInfo,
      };
      const replaceResponse =
        reasonValue === reason.damaged
          ? await replaceCardMutation(replaceCardData, idTokenCookie)
          : await cancelAndReplacePIBACardMutation(data, idTokenCookie);
      if (replaceResponse?.status === requestStatus.success) {
        window?._satellite?.track('replaceCard');
        setIsReplaceInProgress(false);
        onReplaceSuccess();
      } else {
        setIsReplaceInProgress(false);
        onReplaceFail();
      }
    }
  };

  const reasonValue = watch('reason');

  const hasCompletedAlternativeAddressDetails =
    titleWatcher?.value &&
    firstNameWatcher &&
    lastNameWatcher &&
    addressLine1Watcher &&
    postCodeWatcher;
  const isContinueButtonDisabled =
    !reasonValue ||
    isReplaceInProgress ||
    (isCustomAddress && !hasCompletedAlternativeAddressDetails);

  const stepOneContent = (
    <>
      <span className="flex font-bold mb-6 text-xl">
        {t('cards.cardMgmt.cardDetails.editCard.replaceCard.reason')}
      </span>
      <Controller
        name="reason"
        control={control}
        render={({ field }) => (
          <FormRadioGroup
            {...field}
            data-testid="Reason-Radio-Group"
            errors={errors}
            selectedValue={field.value}
            items={[
              {
                value: reason.lost,
                label: t('cards.cardMgmt.cardDetails.editCard.replaceCard.lost'),
              },
              {
                value: reason.damaged,
                label: t('cards.cardMgmt.cardDetails.editCard.replaceCard.damaged'),
              },
            ]}
            variant="col-styled"
          />
        )}
      />
      {reasonValue === reason.lost && (
        <div className={'mt-12'}>
          <SanitizedContent>
            {t('cards.cardMgmt.cardDetails.cancelCard.cardHolder.title')}
          </SanitizedContent>
          <Notification
            className={'mt-6'}
            type="warning"
            icon={formatIBAssetsUrl(icons['icon.notification.alert'])}
            message={
              <SanitizedContent>{t('cards.cardMgmt.cardDetails.cancelCard.info')}</SanitizedContent>
            }
          />
        </div>
      )}
      {reasonValue === reason.damaged && (
        <div className={'mt-12'}>
          <SanitizedContent>{t('cards.cardMgmt.cardDetails.replaceCard.title')}</SanitizedContent>
        </div>
      )}
    </>
  );

  const deliveryStepContent = (
    <CardDeliveryForm
      isMyCard={!!cardDetails?.myCard}
      icons={icons}
      language={language}
      addressComponent={<CompanyAddressFields icons={icons} language={language} />}
      sectionTitle={t('cards.cardMgmt.cardDetails.editCard.replaceCard.delivery.title')}
      sectionTitleClassName="flex font-bold mb-6 text-xl"
      formContainerClassName="p-0 bg-transparent border-0"
      className="mt-0"
      sendCardsToCardholder={sendCardsToCardholder}
    />
  );

  return (
    <>
      <Dialog open={open} onOpenChange={onOpenChange}>
        <DialogContent
          className={'mobile:!h-dvh mobile:max-h-[unset] max-h-[90%] overflow-y-auto'}
          data-testid={'Replace-Card-Dialog'}
          aria-describedby={undefined}
        >
          <DialogHeader>
            <DialogTitle>{t('cards.cardMgmt.cardDetails.editCard.replaceCard.title')}</DialogTitle>
          </DialogHeader>
          <FormProvider {...formMethods}>
            <div>{step === StepsEnum.reason ? stepOneContent : deliveryStepContent}</div>
          </FormProvider>
          <DialogFooter>
            <Button
              data-testid="Replace-Card-Cancel-Button"
              variant="dialogOutline"
              onClick={() => onOpenChange(false)}
            >
              {t('cards.cardMgmt.cardDetails.editCard.replaceCard.cancel')}
            </Button>
            <Button
              data-testid="Replace-Card-Continue-Button"
              variant={step === StepsEnum.reason ? 'dialogDefault' : 'dialogDestructive'}
              onClick={handleContinue}
              disabled={isContinueButtonDisabled}
            >
              {step === StepsEnum.reason
                ? t('cards.cardMgmt.cardDetails.editCard.replaceCard.continue')
                : t('cards.cardMgmt.cardDetails.editCard.replaceCard.button')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
      <Analytics
        pageName="Card Management"
        cardReplaceReason={reasonValue}
        cardReplaceAddress={step === StepsEnum.reason ? null : analyTicsCardReplaceAddress}
        validation={analyticsValidation}
      />
    </>
  );
};
