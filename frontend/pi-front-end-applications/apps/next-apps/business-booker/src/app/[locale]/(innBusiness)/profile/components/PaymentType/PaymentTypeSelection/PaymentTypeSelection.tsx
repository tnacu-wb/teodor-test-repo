'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  AddressInfo,
  AddressType,
  CountryCode,
  Language,
  FT_IB_PAY_PIBA_EURO,
  FT_IB_PIBA_MEMORABLE_WORD_IFRAME,
} from '@whitbread-eos/api';
import { Button, Checkbox, FormInput, CardIcon } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl, useFeatureToggle } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { PaymentCardContainer } from '~components/innBusiness/AddCardIframe/PaymentCardContainer/PaymentCardContainer';
import { FormRadioGroup } from '~components/innBusiness/FormRadioGroup/FormRadioGroup';

import { TrackableComponent } from '../../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../../ReviewChangesWrapper/ReviewChangesWrapper';
import DifferentAddressForm from '../DifferentAddressForm/DifferentAddressForm';

const PAYMENT_TYPES = {
  NEW_CARD: 'NEW_CARD',
  NEW_PIBA: 'NEW_PIBA',
};

const BILLING_TYPES = {
  PERSONAL: 'PERSONAL',
  DIFFERENT: 'DIFFERENT',
};

type PaymentFormData = {
  paymentType: typeof PAYMENT_TYPES.NEW_CARD | typeof PAYMENT_TYPES.NEW_PIBA;
  billingType: typeof BILLING_TYPES.PERSONAL | typeof BILLING_TYPES.DIFFERENT;
  prepayEnabled?: boolean;
  memorableWord?: string;
};

type PaymentFormProps = {
  onSubmit: (data: PaymentFormData & { cardDetails?: any }) => Promise<void>;
  onError: (message: string) => void;
  icons?: Record<string, string>;
  userAddress?: string;
  isLoading?: boolean;
  onCancel?: () => void;
  language?: Language;
  token?: string;
  employeeId?: string;
  profileDetails?: {
    contactDetail?: { address?: Record<string, any> };
    paymentPreference?: {
      paymentCard?: {
        cardNumber?: string;
        cardType?: string;
        cardHolderName?: string;
        expiryDate?: string;
      };
    };
  };
} & TrackableComponent;

type FormState = {
  data: {
    contactDetail?: {
      address?: {
        line1?: string;
        line2?: string;
        line3?: string;
        line4?: string;
        line5?: string;
        postCode?: string;
        countryCode?: string;
        companyName?: string | null;
        type?: string;
      };
    };
    paymentPreference?: {
      paymentCard?: {
        cardNumber?: string;
        cardType?: string;
        cardHolderName?: string;
        expiryDate?: string;
      };
    };
  };
  submittedForms: {
    addressDetails: boolean;
    companyName: boolean;
    addressType: boolean;
    paymentForm: boolean;
  };
};

const CardIconWrapper: React.FC<{ type: string; icons: Record<string, string> }> = ({
  type,
  icons,
}) => (
  <div className={styles.cardIconContainer}>
    <CardIcon type={type} icons={icons} className={styles.cardIcon} />
  </div>
);

const PaymentTypeSelection: React.FC<PaymentFormProps> = ({
  onSubmit,
  onError,
  icons = {},
  userAddress,
  isLoading = false,
  onCancel,
  language = 'en',
  token,
  employeeId,
  isUpdating,
  isEditable,
  profileDetails,
  onReviewChangesToggle,
  onIsDirtyToggle,
  isDirty,
}) => {
  const { t } = useTranslation('profile');
  const {
    [FT_IB_PAY_PIBA_EURO]: isPibaEuroEnabled,
    [FT_IB_PIBA_MEMORABLE_WORD_IFRAME]: isMemorableWordInIframe,
  } = useFeatureToggle();
  const formRef = React.useRef<HTMLFormElement | null>(null);
  const companyNameFormRef = React.useRef<HTMLFormElement | null>(null);
  const addressFormRef = React.useRef<HTMLFormElement | null>(null);
  const addressTypeFormRef = React.useRef<HTMLFormElement | null>(null);
  const [showCardPayment, setShowCardPayment] = useState(false);
  const [paymentData, setPaymentData] = useState<PaymentFormData | null>(null);
  const [addressType, setAddressType] = useState<AddressType>(AddressType.Home);
  const [formState, setFormState] = useState<FormState>({
    data: { contactDetail: { address: profileDetails?.contactDetail?.address || {} } },
    submittedForms: {
      addressDetails: false,
      companyName: false,
      addressType: false,
      paymentForm: false,
    },
  });

  const PaymentFormSchema = z
    .object({
      paymentType: z.enum([PAYMENT_TYPES.NEW_CARD, PAYMENT_TYPES.NEW_PIBA]),
      billingType: z.enum([BILLING_TYPES.PERSONAL, BILLING_TYPES.DIFFERENT]),
      prepayEnabled: z.boolean().optional(),
      memorableWord: z.string().optional(),
    })
    .refine(
      (data) => {
        if (isMemorableWordInIframe) return true;
        if (data.paymentType === PAYMENT_TYPES.NEW_PIBA && data.prepayEnabled) {
          return !!data.memorableWord && data.memorableWord.trim() !== '';
        }
        return true;
      },
      {
        message: t('registrationquestions.validation.required'),
        path: ['memorableWord'],
      }
    );

  const {
    control,
    handleSubmit,
    watch,
    formState: { errors, isSubmitting, isDirty: isFormDirty },
    setValue,
  } = useForm<PaymentFormData>({
    resolver: zodResolver(PaymentFormSchema),
    defaultValues: {
      paymentType: PAYMENT_TYPES.NEW_CARD,
      billingType: BILLING_TYPES.PERSONAL,
      prepayEnabled: false,
      memorableWord: '',
    },
  });
  const billingType = watch('billingType');
  const paymentType = watch('paymentType');
  const prepayEnabled = watch('prepayEnabled');

  React.useEffect(() => {
    if (isFormDirty) {
      onIsDirtyToggle(CHANGES_TRACKER.PAYMENT_TYPE, true);
    }
  }, [isFormDirty]);

  React.useEffect(() => {
    if (paymentType !== PAYMENT_TYPES.NEW_PIBA) {
      setValue('prepayEnabled', false);
    }
  }, [paymentType]);

  React.useEffect(() => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            type: addressType,
            companyName:
              addressType === AddressType.Business
                ? prev.data.contactDetail?.address?.companyName
                : null,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, addressType: true },
    }));
  }, [addressType]);

  const showCompanyAddress = billingType === BILLING_TYPES.DIFFERENT;
  const isBusinessAddress = addressType === AddressType.Business;
  const isPibaCard = paymentType === PAYMENT_TYPES.NEW_PIBA;
  const showMemorableWord = !isMemorableWordInIframe && isPibaCard && prepayEnabled;
  const isPibaCnp = isPibaCard && prepayEnabled;
  const shouldShowNewPibaOption =
    language === CountryCode.EN || (language === CountryCode.DE && isPibaEuroEnabled);
  const memorableWordForIframe =
    isMemorableWordInIframe || !isPibaCnp ? undefined : paymentData?.memorableWord;

  const paymentTypeItems = [
    {
      value: PAYMENT_TYPES.NEW_CARD,
      label: t('payment.paymentTypeNewDebitCreditCard'),
      icons: ['VI', 'MC', 'AX', 'DN'].map((type) => (
        <CardIconWrapper key={type} type={type} icons={icons} />
      )),
      testid: 'PaymentForm-CreditDebit',
    },
    ...(shouldShowNewPibaOption
      ? [
          {
            value: PAYMENT_TYPES.NEW_PIBA,
            label: t('payment.paymentTypeNewInnBusinessPayCard'),
            icons: [<CardIconWrapper key="PI" type="PI" icons={icons} />],
            testid: 'PaymentForm-InnBusinessPay',
          },
        ]
      : []),
  ];

  const billingAddressItems = [
    {
      value: BILLING_TYPES.PERSONAL,
      label: t('payment.add.address.same'),
      description: userAddress,
      testid: 'PaymentForm-PersonalAddress',
    },
    {
      value: BILLING_TYPES.DIFFERENT,
      label: t('payment.add.address.different'),
      testid: 'PaymentForm-DifferentAddress',
    },
  ];

  const handleAddressTypeChange = (type: AddressType) => {
    setAddressType(type);
  };

  const handleAddressType = React.useCallback(
    (data: Record<string, string>) => {
      setFormState((prev) => ({
        ...prev,
        data: {
          ...prev.data,
          contactDetail: {
            ...prev.data.contactDetail,
            address: {
              ...prev.data.contactDetail?.address,
              type: data.addressType,
              companyName: isBusinessAddress ? prev.data.contactDetail?.address?.companyName : null,
            },
          },
        },
        submittedForms: { ...prev.submittedForms, addressType: true },
      }));
    },
    [isBusinessAddress]
  );

  const handleCompanyName = React.useCallback(
    (data: Record<string, string>) => {
      setFormState((prev) => ({
        ...prev,
        data: {
          ...prev.data,
          contactDetail: {
            ...prev.data.contactDetail,
            address: {
              ...prev.data.contactDetail?.address,
              companyName: isBusinessAddress ? data.companyName : null,
            },
          },
        },
        submittedForms: { ...prev.submittedForms, companyName: true },
      }));
    },
    [isBusinessAddress]
  );

  const handleCompanyNameChange = (companyName: string) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            companyName: isBusinessAddress ? companyName : null,
          },
        },
      },
    }));
  };

  const handleCompanyAddress = (data: AddressInfo) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            line1: data.addressLine1,
            line2: data.addressLine2,
            line3: data.addressLine3,
            line4: data.addressLine4,
            line5: data.addressLine5,
            postCode: data.postCode,
            countryCode: data.country,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, addressDetails: true },
    }));
  };

  const handleCompanyAddressChange = (data: AddressInfo) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        contactDetail: {
          ...prev.data.contactDetail,
          address: {
            ...prev.data.contactDetail?.address,
            line1: data.addressLine1,
            line2: data.addressLine2,
            line3: data.addressLine3,
            line4: data.addressLine4,
            line5: data.addressLine5,
            postCode: data.postCode,
            countryCode: data.country,
          },
        },
      },
    }));
  };

  const handleFormSubmit = async (data: PaymentFormData) => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: { ...prev.submittedForms, paymentForm: true },
    }));
    setPaymentData(data);
  };

  const handleSubmitClick = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        addressDetails: !showCompanyAddress,
        companyName: !isBusinessAddress || !showCompanyAddress,
        addressType: !showCompanyAddress,
        paymentForm: false,
      },
    }));

    formRef.current?.requestSubmit();
    if (showCompanyAddress) {
      addressTypeFormRef.current?.requestSubmit();
      if (isBusinessAddress) {
        companyNameFormRef.current?.requestSubmit();
      }
      addressFormRef.current?.requestSubmit();
    }
  };

  const resetForms = React.useCallback(() => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        addressDetails: false,
        companyName: false,
        addressType: false,
        paymentForm: false,
      },
    }));
  }, []);

  React.useEffect(() => {
    if (
      !formState.submittedForms.paymentForm ||
      !formState.submittedForms.addressDetails ||
      !formState.submittedForms.companyName ||
      !formState.submittedForms.addressType
    ) {
      return;
    }

    const processSubmission = async () => {
      if (!paymentData) return;

      if (
        paymentData.paymentType === PAYMENT_TYPES.NEW_CARD ||
        paymentData.paymentType === PAYMENT_TYPES.NEW_PIBA
      ) {
        if (
          !isMemorableWordInIframe &&
          paymentData.paymentType === PAYMENT_TYPES.NEW_PIBA &&
          paymentData.prepayEnabled &&
          (!paymentData.memorableWord || paymentData.memorableWord.trim() === '')
        ) {
          onError('Memorable word is required for PIBA cards with CNP option');
          resetForms();
          return;
        }

        setShowCardPayment(true);
        return;
      }
      await onSubmit({ ...paymentData, ...formState.data });
      resetForms();
    };

    processSubmission();
  }, [formState, paymentData, isMemorableWordInIframe, onError, onSubmit, resetForms]);

  const handleCardPaymentSuccess = async (cardData: any) => {
    if (!paymentData) return;

    const paymentPreference = profileDetails?.paymentPreference?.paymentCard;

    const tokenNo = paymentPreference?.cardNumber || cardData.tokenNo;
    const cardType = paymentPreference?.cardType || cardData.cardType || 'VS';
    const cardHolderName = paymentPreference?.cardHolderName || cardData.cardHolderName || '';
    const expiryDate = paymentPreference?.expiryDate || cardData.expiryDate || '';
    const authCode = cardData.authCode || cardData.authorisationCode;
    const memorableWord = isMemorableWordInIframe ? undefined : paymentData.memorableWord;
    const isCnpEnabled = paymentData.prepayEnabled;
    const isPibaCnp = paymentData.paymentType === PAYMENT_TYPES.NEW_PIBA && isCnpEnabled;

    if (tokenNo && authCode) {
      const cardDetails = {
        tokenNo,
        cardNumber: tokenNo,
        maskedCardNumber: tokenNo ? `●●●● ●●●● ●●●● ${tokenNo.slice(-4)}` : undefined,
        cardType,
        cardHolderName,
        expiryDate,
        authCode,
        paymentId: cardData.paymentId || cardData.id,
        transactionId: cardData.transactionId || cardData.txnId,
        cnpEnabled: isCnpEnabled,
        ...(isPibaCnp && memorableWord ? { memorableWord } : {}),
      };

      const updatedFormData = {
        ...formState.data,
        paymentPreference: {
          paymentCard: {
            cardNumber: tokenNo,
            cardType,
            cardHolderName,
            expiryDate,
            cnpEnabled: isCnpEnabled,
            ...(isPibaCnp && memorableWord ? { memorableWord } : {}),
          },
        },
      };

      await onSubmit({
        ...paymentData,
        ...(isMemorableWordInIframe ? { memorableWord: undefined } : {}),
        cardDetails,
        ...updatedFormData,
      });
    } else {
      onError('Payment information is incomplete');
      handleCardPaymentCancel();
    }
  };

  const handleCardPaymentCancel = () => {
    setShowCardPayment(false);
    setPaymentData(null);
  };

  const handleCancelClick = () => {
    if (!isUpdating && isEditable && isDirty) {
      onReviewChangesToggle(CHANGES_TRACKER.PAYMENT_TYPE, true);
    } else if (onCancel) {
      onCancel();
    }
  };

  const getProfileDetailsForSubmission = () => {
    if (paymentData?.billingType === BILLING_TYPES.DIFFERENT) {
      return formState.data;
    }
    return profileDetails;
  };

  if (showCardPayment) {
    return (
      <div className={styles.paymentCardContainer} data-testid="payment-card-container">
        <div className={styles.paymentCardContainerTwo}>
          <div className={styles.headerContainer} data-testid="add-card-iframe-header">
            <h1 className={styles.headerTitle} data-testid="add-card-iframe-title">
              {t('payment.add.title')}
            </h1>
            <button
              onClick={handleCardPaymentCancel}
              className={styles.closeButton}
              aria-label="Close"
              data-testid="add-card-iframe-close-button"
            >
              <Image
                src={formatIBAssetsUrl(icons['icon.modal.close'])}
                alt="Close"
                width={20}
                height={20}
                data-testid="add-card-iframe-close-icon"
              />
            </button>
          </div>
          <PaymentCardContainer
            onSuccess={handleCardPaymentSuccess}
            onCancel={handleCardPaymentCancel}
            profileDetails={getProfileDetailsForSubmission()}
            token={token}
            employeeId={employeeId}
            isPiba={paymentData?.paymentType === PAYMENT_TYPES.NEW_PIBA}
            memorableWord={memorableWordForIframe}
            cnpEnabled={paymentData?.prepayEnabled}
            data-testid="payment-card-component"
            isPreferenceCard={true}
          />
        </div>
      </div>
    );
  }

  return (
    <div className={styles.paymentCardContainer} data-testid="payment-type-selection">
      <section className={styles.container} data-testid="payment-form-container">
        <h1 className={styles.title} data-testid="payment-form-title">
          {t('payment.add.title')}
        </h1>

        <form
          ref={formRef}
          onSubmit={handleSubmit(handleFormSubmit)}
          className={styles.form}
          data-testid="payment-form"
        >
          <div className={`${styles.formSection} order-1 -mt-6`} data-testid="billing-section">
            <p className={styles.description}>{t('payment.billingAddress')}</p>
            <Controller
              name="billingType"
              control={control}
              render={({ field }) => (
                <FormRadioGroup
                  variant="address"
                  {...field}
                  items={billingAddressItems}
                  errors={errors}
                  errorIcon={icons['icon.notification.error']}
                  selectedValue={field.value}
                  data-testid="billing-type-radio-group"
                />
              )}
            />
          </div>
          <div className={`${styles.formSection} order-3`} data-testid="payment-type-section">
            <div className={styles.paymentTypeHeader} data-testid="payment-type-header">
              <h2 className={styles.paymentTypeTitle}>{t('payment.paymentType')}</h2>
              <p className={styles.description}>{t('payment.paymentType.description')}</p>
            </div>

            <Controller
              name="paymentType"
              control={control}
              render={({ field }) => (
                <FormRadioGroup
                  variant="payment"
                  {...field}
                  items={paymentTypeItems}
                  errors={errors}
                  errorIcon={icons['icon.notification.error']}
                  selectedValue={field.value}
                  data-testid="payment-type-radio-group"
                />
              )}
            />

            {isPibaCard && (
              <div className={styles.checkboxContainer} data-testid="prepay-checkbox-container">
                <Controller
                  name="prepayEnabled"
                  control={control}
                  render={({ field: { onChange, value, ...field } }) => (
                    <Checkbox
                      className={styles.checkbox}
                      id="prepayCheckbox"
                      checked={value}
                      onCheckedChange={onChange}
                      {...field}
                      data-testid="prepay-checkbox"
                    />
                  )}
                />
                <label
                  htmlFor="prepayCheckbox"
                  className={styles.checkboxLabel}
                  data-testid="prepay-checkbox-label"
                >
                  {t('payment.paymentTypeNewInnBusinessPayCard.terms')}
                </label>
              </div>
            )}

            {showMemorableWord && (
              <div className={styles.memorableWordContainer} data-testid="memorable-word-container">
                <p className={styles.memorableWordHeading}>
                  {t('payment.authoriseCardNotPresent')}
                </p>
                <Controller
                  name="memorableWord"
                  control={control}
                  render={({ field }) => (
                    <FormInput
                      {...field}
                      id="memorableWord"
                      type="text"
                      placeholder={t('payment.memorableWord')}
                      errors={errors}
                      errorIcon={formatIBAssetsUrl(icons['icon.notification.error'])}
                      data-testid="memorable-word-input"
                    />
                  )}
                />
              </div>
            )}
          </div>
        </form>
        {showCompanyAddress && (
          <div
            data-testid="different-address-container"
            className="order-2 w-full flex flex-col gap-6"
          >
            <h4 className={styles.differentAddressTitle} data-testid="different-address-title">
              {t('profile.yourAddress')}
            </h4>
            <div style={{ marginTop: '-1.5rem' }}>
              <DifferentAddressForm
                icons={icons}
                language={language}
                addressFormRef={addressFormRef}
                companyNameFormRef={companyNameFormRef}
                addressTypeFormRef={addressTypeFormRef}
                onAddressSubmit={handleCompanyAddress}
                onAddressChange={handleCompanyAddressChange}
                onAddressTypeSubmit={handleAddressType}
                onAddressTypeChange={handleAddressTypeChange}
                onCompanyNameSubmit={handleCompanyName}
                onCompanyNameChange={handleCompanyNameChange}
                addressType={addressType}
                companyName={formState.data.contactDetail?.address?.companyName ?? ''}
                postalCode={
                  formState.data.contactDetail?.address?.postCode ||
                  profileDetails?.contactDetail?.address?.postCode
                }
                profileDetails={formState.data}
                data-testid="different-address-form"
              />
            </div>
          </div>
        )}
        <div className={`${styles.buttonContainer} order-4`} data-testid="button-container">
          <Button
            variant="primary"
            type="button"
            disabled={isLoading || isSubmitting}
            className={styles.submitButton}
            data-testid="submit-button"
            onClick={handleSubmitClick}
          >
            {t('payment.addCardDetails')}
          </Button>
          <button
            type="button"
            onClick={handleCancelClick}
            disabled={isLoading || isSubmitting}
            className={styles.cancelButton}
            data-testid="cancel-button"
          >
            {t('payment.button.cancel')}
          </button>
        </div>
      </section>
    </div>
  );
};

export default React.memo(PaymentTypeSelection);

const styles = {
  container: 'flex flex-col items-start w-full max-w-[531px] min-w-[343px] mt-16 gap-6',
  title: 'font-bold text-[1.438rem] mb-6 text-darkGrey1',
  form: 'contents',
  formSection: 'flex flex-col gap-6 w-full',
  description: 'text-darkGrey1 text-base leading-[150%]',
  paymentTypeHeader: 'flex flex-col gap-2',
  paymentTypeTitle: 'font-bold text-base leading-[120%] text-darkGrey1',
  checkboxContainer: 'flex items-start gap-3 w-full mb-4 mt-2',
  checkbox:
    'flex flex-row items-start p-[2px] w-5 h-5 flex-none order-0 flex-grow-0 border-lightGrey1',
  checkboxLabel:
    'font-normal text-base leading-[150%] text-darkGrey1 cursor-pointer w-full sm:w-[499px] min-h-[72px]',
  buttonContainer: 'flex flex-col items-start gap-4 w-full',
  submitButton:
    'w-full sm:w-[309px] h-14 bg-primaryColor rounded text-white font-semibold text-lg leading-[120%]',
  cancelButton:
    'font-medium text-base leading-[150%] text-center underline text-secondaryColor disabled:opacity-50',
  cardIconContainer: 'w-[40px] h-[24px] rounded-[2px] flex items-center justify-center',
  differentAddressTitle: 'font-semibold text-xl leading-[32px] text-darkGrey1 mt-2',
  cardIcon: 'w-full h-full object-contain',
  paymentCardContainer: 'w-full mb-8 pb-8 border-b border-gray-200 flex flex-col',
  paymentCardContainerTwo:
    'flex flex-col items-start p-0 gap-6 w-full sm:w-[343px] md:w-[531px] sm:h-[861px] md:h-auto flex-none order-7 flex-grow-0',
  memorableWordContainer: 'flex flex-col gap-4 w-full',
  memorableWordHeading: 'font-bold text-base leading-[150%] text-darkGrey1',
  memorableWordInput:
    'w-full p-3 border border-lightGrey1 rounded-md focus:outline-none focus:ring-2 focus:ring-primaryColor',
  errorMessage: 'text-red-500 text-sm mt-1',
  headerContainer: 'flex justify-between items-center my-16 w-full',
  headerTitle: 'font-bold text-[1.438rem] text-darkGrey1',
  closeButton: 'text-gray-500 hover:text-gray-700',
};
