'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  AddressInfo,
  Language,
  PAYMENT_TYPES,
  PAYMENT_RESPONSE_STATUS,
  PaymentCardInfo,
  requestStatus,
  FT_IB_PIBA_MEMORABLE_WORD_IFRAME,
} from '@whitbread-eos/api';
import {
  SanitizedContent,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Dialog,
  FormInput,
  Button,
  FormPage,
  useToast,
  Notification,
} from '@whitbread-eos/atoms/ui';
import {
  analytics,
  getAuthCookie,
  formatIBAssetsUrl,
  getPathForLocale,
  getLocaleByPathname,
  useTranslation,
  getSavedCardType,
  cn,
  useFeatureToggle,
} from '@whitbread-eos/utils';
import {
  companyNameSchema,
  cardTypeSchema,
  addressSchema,
  cardLabelSchema,
  updateCDHCard,
  deleteCDHCard,
} from '@whitbread-eos/utils/server';
import Image from 'next/image';
import Link from 'next/link';
import { usePathname, useRouter } from 'next/navigation';
import React, { useEffect, useState } from 'react';
import { useForm, FormProvider, Controller } from 'react-hook-form';

import { PaymentCardContainer } from '~components/innBusiness/AddCardIframe';
import { AddressDetails } from '~components/innBusiness/AddressDetails/index';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges/index';
import { CentrallyCardTypeForm } from '~components/innBusiness/forms/AddCardForms/CentrallyStored/CentrallyCardTypeForm';
import { CompanyAddressFields } from '~components/innBusiness/forms/CompanyAddressForm/CompanyAddressFields';

import { revalidateCacheOnLink } from '../../revalidate-link';

interface Props {
  icons: Record<string, string>;
  language: Language;
  initialAddress: AddressInfo;
  companyName: string;
  token: string;
  employeeId: string;
  cardDetails?: PaymentCardInfo;
  companyId?: string;
}

export const AddEditCentrallyStoredCard = ({
  icons,
  language,
  initialAddress,
  companyName,
  token,
  cardDetails,
  companyId,
}: Readonly<Props>) => {
  const idTokenCookie = getAuthCookie();
  const { [FT_IB_PIBA_MEMORABLE_WORD_IFRAME]: isMemorableWordInIframe } = useFeatureToggle();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation(['cards', 'company']);
  const { toast } = useToast();
  const router = useRouter();
  const [disableReviewModal, setDisableReviewModal] = useState(false);
  const [showFailError, setShowFailError] = useState(false);
  const [isUpdatingCard, setIsUpdatingCard] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isFormDirty, setIsFormDirty] = useState(false);
  const isEditPage = !!cardDetails;

  const schema = companyNameSchema(t)
    .merge(addressSchema(t))
    .merge(cardTypeSchema(t).schema)
    .merge(cardLabelSchema(t));

  const formMethods = useForm({
    resolver: zodResolver(schema),
    defaultValues: {
      companyName: companyName,
      cardType: cardDetails?.cardType
        ? getSavedCardType(cardDetails?.cardType)
        : PAYMENT_TYPES.NEW_CARD,
      CNP: cardDetails?.cardNotPresentRequired ?? false,
      memorableWord: cardDetails?.cardNotPresent?.businessAccountPassword ?? '',
      cardLabel: cardDetails?.cardLabel ?? '',
      addressLine1: initialAddress.addressLine1,
      addressLine2: initialAddress.addressLine2 ?? '',
      addressLine3: initialAddress.addressLine3 ?? '',
      addressLine4: initialAddress.addressLine4 ?? '',
      addressLine5: initialAddress.addressLine5 ?? '',
      country: initialAddress.country,
      postCode: initialAddress.postCode,
    },
  });

  const {
    control,
    handleSubmit,
    formState: { errors, dirtyFields },
    trigger,
    getValues,
    watch,
  } = formMethods;

  const currentPostCode = watch('postCode');
  const currentAddressLine1 = watch('addressLine1');
  const currentAddressLine2 = watch('addressLine2');
  const currentAddressLine3 = watch('addressLine3');
  const currentAddressLine4 = watch('addressLine4');
  const currentAddressLine5 = watch('addressLine5');
  const currentCountry = watch('country');
  const initialPostCode = initialAddress?.postCode ?? '';
  const initialAddressLine1 = initialAddress?.addressLine1 ?? '';
  const initialAddressLine2 = initialAddress?.addressLine2 ?? '';
  const initialAddressLine3 = initialAddress?.addressLine3 ?? '';
  const initialAddressLine4 = initialAddress?.addressLine4 ?? '';
  const initialAddressLine5 = initialAddress?.addressLine5 ?? '';
  const initialCountry = initialAddress?.country ?? '';

  useEffect(() => {
    if (
      initialPostCode !== currentPostCode ||
      initialAddressLine1 !== currentAddressLine1 ||
      initialAddressLine2 !== currentAddressLine2 ||
      initialAddressLine3 !== currentAddressLine3 ||
      initialAddressLine4 !== currentAddressLine4 ||
      initialAddressLine5 !== currentAddressLine5 ||
      initialCountry !== currentCountry ||
      Object.keys(dirtyFields).length > 0
    ) {
      setIsFormDirty(true);
    }
  }, [
    Object.keys(dirtyFields).length,
    currentPostCode,
    currentAddressLine1,
    currentAddressLine2,
    currentAddressLine3,
    currentAddressLine4,
    currentAddressLine5,
    currentCountry,
  ]);

  const [isAddressEdit, setIsAddressEdit] = useState(false);
  const [isSubmitStep, setIsSubmitStep] = useState(false);

  const cardTypeWatcher = watch('cardType');
  const isKeepingCurrentCard =
    cardTypeWatcher === PAYMENT_TYPES.KEEP_PIBA || cardTypeWatcher === PAYMENT_TYPES.KEEP_CARD;
  const isCnpPiba =
    watch('CNP') &&
    (cardTypeWatcher === PAYMENT_TYPES.NEW_PIBA || cardTypeWatcher === PAYMENT_TYPES.KEEP_PIBA);
  const memorableWordForIframe =
    isMemorableWordInIframe || !isCnpPiba ? undefined : getValues('memorableWord');

  const handleUpdateCurrentCard = async () => {
    setDisableReviewModal(true);
    setIsUpdatingCard(true);
    const updateResponse = await updateCDHCard(idTokenCookie, {
      companyId: companyId,
      cardId: cardDetails?.cardId,
      cardLabel: getValues('cardLabel'),
      cardType: cardDetails?.cardType,
      cardNumber: cardDetails?.cardNumber,
      expiryDate: cardDetails?.expiryDate,
      cardHolderName: cardDetails?.nameOnCard,
      cardToken: cardDetails?.cardToken,
      billingAddress: {
        companyName: getValues('companyName'),
        line1: getValues('addressLine1'),
        line2: getValues('addressLine2'),
        line3: getValues('addressLine3'),
        line4: getValues('addressLine4'),
        line5: getValues('addressLine5'),
        postCode: getValues('postCode'),
        countryCode: getValues('country'),
        type: 'BUSINESS',
      },
      cnpRequired: getValues('CNP'),
      memorableWord: isMemorableWordInIframe ? undefined : getValues('memorableWord'),
    });
    if (updateResponse?.status === requestStatus.success) {
      toast({
        content: t('cards.cardMgmt.cardDetails.notification.confirmation'),
      });

      const path = getPathForLocale(locale, `manage/cards?tab=centrally-stored`);

      await revalidateCacheOnLink(path);
      router.push(path);
    } else {
      handleCardPaymentError();
      setIsUpdatingCard(false);
    }
  };

  const onNextStepClick = () => {
    let isValid = true;
    if (!isMemorableWordInIframe) {
      isValid = cardTypeSchema(t).validation(
        getValues(),
        formMethods.setError,
        formMethods.clearErrors
      );
    }
    if (!isValid) {
      return;
    }

    if (isKeepingCurrentCard) {
      handleUpdateCurrentCard();
    } else {
      window.scrollTo(0, 0);
      setIsSubmitStep(true);
      setShowFailError(false);
    }
  };

  const handleCardPaymentSuccess = (result: any) => {
    if (result.paymentStatus === PAYMENT_RESPONSE_STATUS.FAILURE) {
      handleCardPaymentError();
      return;
    }
    router.push(getPathForLocale(locale, 'manage/cards?tab=centrally-stored'));
    router.refresh();
    toast({
      content: isEditPage
        ? t('cards.cardMgmt.cardDetails.notification.authorised')
        : t('cards.centrallyStoredCard.save.success'),
    });
  };

  const handleCardPaymentError = () => {
    window.scrollTo(0, 0);
    setIsSubmitStep(false);
    setShowFailError(true);
    setDisableReviewModal(false);
    analytics.update({
      validation: 'Could not save card details',
    });
  };

  const handleDeleteCard = async () => {
    setDisableReviewModal(true);
    setIsUpdatingCard(true);
    const deleteCardResponse = await deleteCDHCard(
      companyId ?? '',
      cardDetails?.cardId ?? '',
      idTokenCookie
    );
    if (deleteCardResponse?.status === requestStatus.success) {
      toast({
        content: t('cards.cardMgmt.cardDetails.delete.success'),
        variant: 'warning',
        icon: formatIBAssetsUrl(icons['icon.notification.alert']),
      });
      router.push(getPathForLocale(locale, `manage/cards?tab=centrally-stored`));
      router.refresh();
    } else {
      setDisableReviewModal(false);
      setIsUpdatingCard(false);
    }
  };

  useEffect(() => {
    const formErrors = Object.values(errors).map((error) => error?.message);
    const currentData = window?.analyticsData?.innBusiness ?? {};

    analytics.update({
      innBusiness: {
        ...currentData,
        validation: formErrors.join(', '),
      },
    });
  }, [errors]);

  const deleteCardModal = (
    <Dialog
      open={isDeleteModalOpen}
      onOpenChange={setIsDeleteModalOpen}
      data-testid={'Delete-Card-Modal'}
    >
      <DialogContent className={dialogContentStyle} data-testid={'Delete-Card-Content'}>
        <DialogHeader data-testid="Delete-Card-Header">
          <DialogTitle data-testid="Delete-Card-Title">
            {t('cards.cardMgmt.cardDetails.delete.title')}
          </DialogTitle>
          <div data-testid="Delete-Card-Box">
            <SanitizedContent replacements={{ '{companyName}': companyName }}>
              {t('cards.cardMgmt.cardDetails.delete.message')}
            </SanitizedContent>
          </div>
        </DialogHeader>
        <DialogFooter data-testid="Delete-Card-Footer">
          <Button
            data-testid={`Delete-Card-Cancel-Button`}
            variant="dialogOutline"
            onClick={() => setIsDeleteModalOpen(false)}
          >
            {t('cards.cardMgmt.cardDetails.delete.cancel')}
          </Button>
          <Button
            data-testid="Submit-Delete-Card-Button"
            variant="dialogDestructive"
            onClick={handleDeleteCard}
          >
            {t('cards.cardMgmt.cardDetails.delete.confirm')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );

  const separator = <div className="form-box-separator" />;

  const handleFormDirtyChange = (isDirty: boolean) => {
    setIsFormDirty(isDirty);
  };

  const cardDetailsElement = (
    <div className="flex flex-col mt-12">
      {showFailError && (
        <Notification
          type="error"
          className="mb-4"
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          title={t('cards.centrallyStoredCard.save.failed.title')}
          message={t('cards.centrallyStoredCard.save.failed.description')}
        />
      )}
      <div className="form-details-box">
        <span className="font-bold">{t('cards.centrallyStoredCard.card.details.title')}</span>
        <Controller
          name="cardLabel"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              containerClassName={'mt-6'}
              id="cardLabel"
              placeholder={t('cards.centrallyStoredCard.card.label.title')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => trigger('cardLabel')}
            />
          )}
        />
        {separator}
        <div className="flex mb-4">
          <span className="font-bold">
            {t('cards.centrallyStoredCard.card.billingAddress.title')}
          </span>
          {!isAddressEdit && (
            <Button
              variant="editButton"
              size="editButton"
              data-testid="Centrally-Card-Address-Edit"
              className="ml-auto"
              onClick={() => {
                setIsAddressEdit(true);
              }}
            >
              {t('cards.centrallyStoredCard.card.billingAddress.edit')}
            </Button>
          )}
        </div>
        {isAddressEdit ? (
          <CompanyAddressFields
            icons={icons}
            language={language}
            onDirtyChange={handleFormDirtyChange}
          />
        ) : (
          <AddressDetails
            addressInfo={initialAddress}
            language={language}
            isCompanyInformation
            companyName={companyName}
          />
        )}
        {separator}
        <CentrallyCardTypeForm icons={icons} cardDetails={cardDetails} />
      </div>
      <Button
        type="submit"
        variant="default"
        data-testid={'Submit-Add-Card'}
        className={cn(buttonStyle, 'mt-12')}
        disabled={isUpdatingCard}
      >
        {isEditPage && isKeepingCurrentCard
          ? t('cards.centrallyStoredCard.card.edit.saveUpdates')
          : t('cards.centrallyStoredCard.card.submit.label')}
      </Button>
      {isEditPage && (
        <>
          <Link href={getPathForLocale(locale, 'manage/cards?tab=centrally-stored')}>
            <Button
              variant="alternativeDefault"
              data-testid={'Cancel-Edit-Card'}
              className={cn(buttonStyle, 'mt-3')}
              disabled={isUpdatingCard}
            >
              {t('cards.centrallyStoredCard.card.edit.cancelUpdates')}
            </Button>
          </Link>
          <div className="w-full h-[1px] bg-lightGrey3 my-12"></div>
          <div className="flex">
            <Image
              alt={'Delete Card Icon'}
              src={formatIBAssetsUrl(icons['icon.dropdown.close'])}
              width={15}
              height={15}
              data-testid="delete-card-icon"
            />
            <Button
              data-testid="Delete-Card-Modal-Open-Button"
              variant="editButton"
              className="ml-3 p-0 h-auto"
              onClick={() => {
                setIsDeleteModalOpen(true);
              }}
            >
              {t('cards.centrallyStoredCard.card.edit.deleteCard.linkLabel')}
            </Button>
          </div>
          {deleteCardModal}
        </>
      )}
    </div>
  );

  const cardIframeStepElement = (
    <>
      <div className="form-details-box mt-12 gap-6">
        <div className="flex flex-col">
          <span className="font-bold mb-4">{t('cards.centrallyStoredCard.card.label.title')}</span>
          <span>{getValues('cardLabel')}</span>
        </div>
        <div className="flex flex-col">
          <span className="font-bold mb-4">
            {t('cards.centrallyStoredCard.card.billingAddress.title')}
          </span>
          <AddressDetails
            addressInfo={{
              addressLine1: getValues('addressLine1'),
              addressLine2: getValues('addressLine2'),
              addressLine3: getValues('addressLine3'),
              addressLine4: getValues('addressLine4'),
              addressLine5: getValues('addressLine5'),
              postCode: getValues('postCode'),
              country: getValues('country'),
            }}
            language={language}
            isCompanyInformation
            companyName={getValues('companyName')}
          />
        </div>
        <Button
          variant="editButton"
          size="editButton"
          data-testid="Edit-Card-Information-Button"
          className="justify-start"
          onClick={() => {
            setIsSubmitStep(false);
          }}
        >
          {t('cards.centrallyStoredCard.card.edit.title')}
        </Button>
      </div>
      <div className="form-details-box mt-4 px-0">
        <span className="font-bold mb-4 px-6">
          {t('cards.centrallyStoredCard.card.details.title')}
        </span>
        <PaymentCardContainer
          onSuccess={handleCardPaymentSuccess}
          onCancel={handleCardPaymentError}
          onLoading={() => {
            setDisableReviewModal(true);
          }}
          profileDetails={{
            contactDetail: {
              address: {
                line1: getValues('addressLine1'),
                line2: getValues('addressLine2'),
                line3: getValues('addressLine3'),
                line4: getValues('addressLine4'),
                line5: getValues('addressLine5'),
                countryCode: getValues('country'),
                postCode: getValues('postCode'),
                companyName: getValues('companyName'),
              },
            },
          }}
          cardId={cardDetails?.cardId ?? ''}
          token={token}
          isPiba={getValues('cardType') === PAYMENT_TYPES.NEW_PIBA}
          memorableWord={memorableWordForIframe}
          cnpEnabled={getValues('CNP')}
          data-testid="add-card-component"
          isPreferenceCard={false}
          cardLabel={getValues('cardLabel')}
          hideBorder={true}
        />
      </div>
    </>
  );

  return (
    <FormProvider {...formMethods}>
      <FormPage
        iconClassName="px-2 py-3 max-w-[3rem]"
        baseDataTestId={'Centrally-Stored-Add-Card'}
        backIcon={formatIBAssetsUrl(icons['icon.arrow.left.purple'])}
        backHref={getPathForLocale(locale, 'manage/cards?tab=centrally-stored')}
        title={
          isEditPage
            ? t('cards.centrallyStoredCard.card.edit.page.title')
            : t('cards.centrallyStoredCard.card.add.title')
        }
      >
        <form onSubmit={handleSubmit(onNextStepClick)}>
          {isSubmitStep ? cardIframeStepElement : cardDetailsElement}
          {!disableReviewModal && isFormDirty && <ReviewChanges />}
        </form>
      </FormPage>
    </FormProvider>
  );
};

const buttonStyle = 'flex w-full';
const dialogContentStyle = 'max-w-[700px]';
