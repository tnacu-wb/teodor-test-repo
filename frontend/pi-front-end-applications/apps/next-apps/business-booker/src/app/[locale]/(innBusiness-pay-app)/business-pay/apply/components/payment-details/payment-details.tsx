'use client';

import { LOCALES, requestStatus, DirectDebitOption } from '@whitbread-eos/api';
import {
  RadioGroup,
  RadioGroupItem,
  Checkbox,
  Label,
  ErrorTooltip,
  Button,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { WizardPage, WizardFooter, useWizardContext } from '@whitbread-eos/layout';
import { analytics, getAuthCookie } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import { directDebit } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useRouter } from 'next/navigation';
import { useState, useEffect, useRef } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState, PayApplicationStep } from '../types';

type PaymentMethod = 'directDebit' | 'directDebitByPost' | null;

type ValidationError = 'noSelection' | null;

type CheckboxValidationErrors = {
  wishToSetupDirectDebit: boolean;
  onlyPersonAuthorized: boolean;
  accountHolderAndPayer: boolean;
};

const getSavedPaymentMethod = (directDebitOption: string) => {
  if (directDebitOption === 'DIRECT') {
    return 'directDebit';
  }

  if (directDebitOption === 'BY_POST') {
    return 'directDebitByPost';
  }

  return null;
};

export function PaymentDetails({ locale }: { locale: LOCALES }) {
  const { goToStep, icons, wizardState, setWizardState } = useWizardContext<PayApplicationState>();
  const { t } = useTranslation('payApplication');
  const router = useRouter();
  const token = getAuthCookie();
  const { handleWorldlineError } = useWorldlineErrorHandler();
  const mandateSectionRef = useRef<HTMLDivElement>(null);

  const [selectedPaymentMethod, setSelectedPaymentMethod] = useState<PaymentMethod>(
    getSavedPaymentMethod(wizardState.directDebitOption)
  );
  const [hasShownNotification, setHasShownNotification] = useState(false);
  const [showMandateAlert, setShowMandateAlert] = useState(false);
  const [isProcessing, setIsProcessing] = useState(false);
  const [checkboxStates, setCheckboxStates] = useState({
    wishToSetupDirectDebit: false,
    onlyPersonAuthorized: false,
    accountHolderAndPayer: false,
  });
  const [validationError, setValidationError] = useState<ValidationError>(null);
  const [checkboxValidationErrors, setCheckboxValidationErrors] =
    useState<CheckboxValidationErrors>({
      wishToSetupDirectDebit: false,
      onlyPersonAuthorized: false,
      accountHolderAndPayer: false,
    });

  useEffect(() => {
    if (selectedPaymentMethod) {
      setValidationError(null);
    }

    setCheckboxStates({
      wishToSetupDirectDebit: false,
      onlyPersonAuthorized: false,
      accountHolderAndPayer: false,
    });
    setCheckboxValidationErrors({
      wishToSetupDirectDebit: false,
      onlyPersonAuthorized: false,
      accountHolderAndPayer: false,
    });

    setHasShownNotification(false);
    setShowMandateAlert(false);
  }, [selectedPaymentMethod]);

  const handlePaymentMethodChange = (value: string) => {
    setSelectedPaymentMethod(value as PaymentMethod);
  };

  const handleCheckboxChange = (checkboxName: keyof typeof checkboxStates) => {
    setCheckboxStates((prev) => ({
      ...prev,
      [checkboxName]: !prev[checkboxName],
    }));

    if (!checkboxStates[checkboxName]) {
      setCheckboxValidationErrors((prev) => ({
        ...prev,
        [checkboxName]: false,
      }));
    }
  };

  const scrollToMandateSection = () => {
    setTimeout(() => {
      if (mandateSectionRef.current) {
        mandateSectionRef.current.scrollIntoView({
          behavior: 'smooth',
          block: 'start',
        });
      }
    }, 100);
  };

  const handleDownloadMandate = () => {
    const mandateUrl =
      wizardState.scheme === 'DE' ? t('payapp.sepaMandate') : t('payapp.directDebitMandate');

    window.open(formatIBAssetsUrl(mandateUrl), '_blank', 'noopener,noreferrer');
  };

  const handleDirectDebitByPost = async () => {
    setIsProcessing(true);
    try {
      const result = await directDebit(
        token,
        wizardState.applicationId,
        wizardState.applicationGuid,
        'BY_POST',
        PayApplicationStep.SUMMARY
      );

      if (result?.status === requestStatus.success) {
        setWizardState((prev: PayApplicationState) => ({
          ...prev,
          directDebitOption: DirectDebitOption.ByPost,
        }));
        goToStep(PayApplicationStep.SUMMARY);
      } else {
        handleWorldlineError(result);
      }
    } finally {
      setIsProcessing(false);
    }
  };

  const validateAndContinue = () => {
    if (isProcessing) {
      return;
    }

    if (!selectedPaymentMethod) {
      setValidationError('noSelection');
      return;
    }

    if (selectedPaymentMethod === 'directDebit') {
      const requiredCheckboxes =
        wizardState.scheme === 'DE'
          ? ['wishToSetupDirectDebit', 'onlyPersonAuthorized', 'accountHolderAndPayer']
          : ['onlyPersonAuthorized', 'accountHolderAndPayer'];

      const newCheckboxErrors = {
        wishToSetupDirectDebit:
          wizardState.scheme === 'DE' && !checkboxStates.wishToSetupDirectDebit,
        onlyPersonAuthorized: !checkboxStates.onlyPersonAuthorized,
        accountHolderAndPayer: !checkboxStates.accountHolderAndPayer,
      };

      setCheckboxValidationErrors(newCheckboxErrors);

      const hasErrors = requiredCheckboxes.some(
        (checkbox) => newCheckboxErrors[checkbox as keyof CheckboxValidationErrors]
      );

      if (hasErrors) {
        return;
      }
      setIsProcessing(true);
      setWizardState((prev: PayApplicationState) => ({
        ...prev,
        directDebitOption: DirectDebitOption.Direct,
      }));
      goToStep(PayApplicationStep.PAYMENT_DETAILS_DIRECT_DEBIT);
    } else if (selectedPaymentMethod === 'directDebitByPost') {
      if (!hasShownNotification) {
        setHasShownNotification(true);
        setShowMandateAlert(true);
        scrollToMandateSection();
      } else {
        handleDirectDebitByPost();
      }
    }
  };

  const continueLater = () => {
    router.push(getPathForLocale(locale, 'homepage'));
  };

  const hasValidationErrors = () => {
    if (validationError) return true;

    if (selectedPaymentMethod === 'directDebit') {
      return Object.values(checkboxValidationErrors).some((error) => error);
    }

    return false;
  };

  const renderValidationError = () => {
    if (!validationError) return null;

    analytics.update({
      validation: t('payapp.directDebit.selectOption'),
    });

    return (
      <ErrorTooltip
        icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
        content={t('payapp.directDebit.selectOption')}
        open={!!validationError}
        testId="payment-method-error-tooltip"
        className="!flex"
        mobile
      />
    );
  };

  const renderDirectDebitConditions = () => {
    if (selectedPaymentMethod !== 'directDebit') return null;

    const renderCheckboxError = (hasError: boolean) => {
      if (!hasError) return null;

      analytics.update({
        validation: t('payapp.directDebit.confirmation.error'),
      });

      return (
        <ErrorTooltip
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          content={t('payapp.directDebit.confirmation.error')}
          open={true}
          testId="checkbox-error-tooltip"
          className={errorTooltipStyle}
          mobile
        />
      );
    };

    return (
      <div className={conditionsContainerStyle}>
        <div className={conditionsHeaderStyle}>
          <div className={conditionsHeaderTextStyle}>
            {t('payapp.directDebit.confirmation.conditions')}
          </div>
        </div>

        <div className={checkboxContainerStyle}>
          {wizardState.scheme === 'DE' && (
            <div>
              <div className={checkboxItemStyle}>
                <Checkbox
                  id="checkbox-wish-setup-direct-debit"
                  checked={checkboxStates.wishToSetupDirectDebit}
                  onCheckedChange={() => handleCheckboxChange('wishToSetupDirectDebit')}
                  data-testid="checkbox-wish-setup-direct-debit"
                  className={
                    checkboxValidationErrors.wishToSetupDirectDebit ? checkboxErrorStyle : ''
                  }
                />
                <Label htmlFor="checkbox-wish-setup-direct-debit" className={checkboxLabelStyle}>
                  {t('payapp.directDebit.confirmation.terms.setupOnline_de')}
                </Label>
              </div>
              {renderCheckboxError(checkboxValidationErrors.wishToSetupDirectDebit)}
            </div>
          )}

          <div>
            <div className={checkboxItemStyle}>
              <Checkbox
                id="checkbox-only-person-authorized"
                checked={checkboxStates.onlyPersonAuthorized}
                onCheckedChange={() => handleCheckboxChange('onlyPersonAuthorized')}
                data-testid="checkbox-only-person-authorized"
                className={checkboxValidationErrors.onlyPersonAuthorized ? checkboxErrorStyle : ''}
              />
              <Label htmlFor="checkbox-only-person-authorized" className={checkboxLabelStyle}>
                {t('payapp.directDebit.confirmation.terms.shared_authorisation')}
              </Label>
            </div>
            {renderCheckboxError(checkboxValidationErrors.onlyPersonAuthorized)}
          </div>

          <div>
            <div className={checkboxItemStyle}>
              <Checkbox
                id="checkbox-account-holder-payer"
                checked={checkboxStates.accountHolderAndPayer}
                onCheckedChange={() => handleCheckboxChange('accountHolderAndPayer')}
                data-testid="checkbox-account-holder-payer"
                className={checkboxValidationErrors.accountHolderAndPayer ? checkboxErrorStyle : ''}
              />
              <Label htmlFor="checkbox-account-holder-payer" className={checkboxLabelStyle}>
                {wizardState.scheme === 'DE'
                  ? t('payapp.directDebit.confirmation.terms.accountHolderAndPayer_de')
                  : t('payapp.directDebit.confirmation.terms.accountHolderAndPayer_uk')}
              </Label>
            </div>
            {renderCheckboxError(checkboxValidationErrors.accountHolderAndPayer)}
          </div>
        </div>
      </div>
    );
  };

  const renderMandateSection = () => {
    if (selectedPaymentMethod !== 'directDebitByPost') return null;

    return (
      <div
        ref={mandateSectionRef}
        className={mandateSectionContainerStyle}
        data-testid="mandate-section"
      >
        <div className={mandateContentStyle}>
          <div className={mandateTextStyle}>{t('payapp.directDebit.alternateOption')}</div>

          <div className={mandateDetailsStyle}>
            <div className={mandateDetailsLabelStyle}>{t('payapp.mandate.send')}</div>
            <div className={mandateAddressContainerStyle}>
              {t('payapp.directDirect.post.address')
                .split(',')
                .map((line: string, index: number) => (
                  <div key={index} className={mandateAddressBoldStyle}>
                    {line.trim()}
                  </div>
                ))}
            </div>
          </div>

          {showMandateAlert && (
            <div className={alertContainerStyle} data-testid="mandate-alert">
              <div className={alertContentStyle}>
                <div className={alertIconContainerStyle}>
                  <Image
                    src={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
                    alt="Alert"
                    className={alertIconStyle}
                    width={16}
                    height={16}
                  />
                </div>
                <div className={alertTextStyle}>{t('payapp.mandate.download.reminder')}</div>
              </div>
            </div>
          )}

          <Button
            variant="outline"
            onClick={handleDownloadMandate}
            className={downloadButtonStyle}
            data-testid="download-mandate-button"
          >
            <Image
              src={formatIBAssetsUrl(icons?.['icon.file.icon.purple'])}
              alt="File"
              className={downloadIconStyle}
              width={16}
              height={16}
            />
            {t('payapp.mandate.download')}
          </Button>
        </div>
      </div>
    );
  };

  return (
    <WizardPage
      type="form"
      formTitle={t('payapp.paymentDetails.title')}
      showBackButton={true}
      onBackClick={() => goToStep(PayApplicationStep.CARD_DETAILS, true)}
      footer={
        <WizardFooter
          linkLabel={t('companyDetails.closeOut')}
          buttonLabel={t('your.details.continue')}
          onButtonClick={validateAndContinue}
          onLinkClick={continueLater}
          buttonDisabled={hasValidationErrors() || isProcessing}
        />
      }
    >
      <div className={containerStyle}>
        <div className={descriptionStyle}>{t('payapp.update.directDebit')}</div>
        <div className={authNoteStyle}>{t('payapp.directDebit.auth')}</div>

        <div>
          <div className={sectionTitleContainerStyle}>
            <div className={sectionTitleStyle}>{t('payapp.directDebit.setUp')}</div>
          </div>

          <RadioGroup
            value={selectedPaymentMethod || ''}
            onValueChange={handlePaymentMethodChange}
            className={radioGroupStyle}
            hasError={validationError === 'noSelection'}
          >
            <Label
              htmlFor="radio-direct-debit"
              className={`${radioLabelBaseStyle} ${
                selectedPaymentMethod === 'directDebit' ? radioLabelSelectedStyle : ''
              }`}
            >
              <RadioGroupItem
                value="directDebit"
                id="radio-direct-debit"
                data-testid="radio-direct-debit"
                className={`${radioItemBaseStyle} ${
                  selectedPaymentMethod === 'directDebit'
                    ? radioItemSelectedStyle
                    : radioItemUnselectedStyle
                }`}
                bulletFillStyle="fill-primaryColor text-primaryColor"
              />
              <div className={radioContentStyle}>
                <div className={radioTextStyle}>{t('payapp.directDebit.setUpOption1')}</div>
                <div className={fastestBadgeStyle}>
                  {t('payapp.directDebit.setUp.fastest.label')}
                </div>
              </div>
            </Label>

            <Label
              htmlFor="radio-direct-debit-post"
              className={`${radioLabelBaseStyle} ${
                selectedPaymentMethod === 'directDebitByPost' ? radioLabelSelectedStyle : ''
              }`}
            >
              <RadioGroupItem
                value="directDebitByPost"
                id="radio-direct-debit-post"
                data-testid="radio-direct-debit-post"
                className={`${radioItemBaseStyle} ${
                  selectedPaymentMethod === 'directDebitByPost'
                    ? radioItemSelectedStyle
                    : radioItemUnselectedStyle
                }`}
                bulletFillStyle="fill-primaryColor text-primaryColor"
              />
              <div className={radioTextStyle}>{t('payapp.directDebit.setUpPost.Option2')}</div>
            </Label>
          </RadioGroup>

          <div className={validationErrorStyle}>{renderValidationError()}</div>
        </div>

        {renderDirectDebitConditions()}

        {selectedPaymentMethod === 'directDebit' && (
          <div className={enquiryTextStyle}>
            <SanitizedContent>{t('payapp.directDebit.enquiry')}</SanitizedContent>
          </div>
        )}

        {renderMandateSection()}

        <ReviewChanges />
        <Analytics
          pageName="Pay Application: Payment Details"
          track="Pay Application: Payment Details"
        />
      </div>
    </WizardPage>
  );
}

const containerStyle = 'flex flex-col';
const descriptionStyle = 'text-neutral-900 text-base font-normal leading-normal mb-4';
const authNoteStyle = 'text-neutral-900 text-base font-normal leading-normal mb-8';
const sectionTitleStyle = 'text-neutral-900 text-xl font-bold leading-normal';
const sectionTitleContainerStyle = 'mb-4';
const radioGroupStyle = 'mt-2 gap-0';
const radioLabelBaseStyle =
  'flex items-center space-x-2 cursor-pointer border border-t-0 first:border-t p-4 bg-white first:rounded-t last:rounded-b h-[unset]';
const radioLabelSelectedStyle = 'outline outline-2 outline-primaryColor -outline-offset-2';
const radioItemBaseStyle = 'border-2 self-start mt-[2px]';
const radioItemSelectedStyle = 'color-primaryColor border-primaryColor';
const radioItemUnselectedStyle = 'border-lightGrey1';
const radioContentStyle = 'flex justify-between items-center w-full';
const radioTextStyle = 'font-semibold text-base cursor-pointer text-neutral-900';
const fastestBadgeStyle = 'px-2 py-1 bg-[#1C8754] rounded text-white text-xs font-semibold';
const validationErrorStyle = 'mt-1';
const conditionsContainerStyle = 'mt-6 p-6 bg-white rounded-lg border border-neutral-200';
const conditionsHeaderStyle = 'mb-6';
const conditionsHeaderTextStyle = 'text-base font-bold text-neutral-900 leading-normal';
const checkboxContainerStyle = 'space-y-4';
const checkboxItemStyle = 'flex items-start gap-2';
const checkboxLabelStyle = 'text-zinc-800 text-base font-normal leading-normal';
const checkboxErrorStyle = 'border-red-500';
const errorTooltipStyle = '!flex mt-3';
const enquiryTextStyle = 'mt-8 text-zinc-800 text-base font-normal leading-normal';
const mandateSectionContainerStyle = 'mt-8';
const mandateContentStyle = 'space-y-6';
const mandateTextStyle = 'text-zinc-800 text-base font-normal leading-normal';
const mandateDetailsStyle = 'space-y-4';
const mandateDetailsLabelStyle = 'text-zinc-800 text-base font-normal leading-normal';
const mandateAddressContainerStyle = 'mt-2';
const mandateAddressBoldStyle = 'text-zinc-800 text-base font-bold leading-normal';
const downloadButtonStyle =
  'inline-flex items-center justify-center whitespace-nowrap ring-offset-background transition-colors focus-visible:outline-none focus-visible:ring-2 focus-visible:ring-ring focus-visible:ring-offset-2 disabled:pointer-events-none disabled:opacity-50 bg-background text-lg text-secondaryColor font-semibold border border-secondaryColor hover:text-secondaryColorHover hover:border-secondaryColorHover h-14 px-6 py-4 rounded-sm w-full';
const downloadIconStyle = 'w-6 h-6 mr-2';
const alertContainerStyle = 'mt-4 p-4 bg-orange-50 rounded border border-orange-200';
const alertContentStyle = 'flex items-start gap-2';
const alertIconContainerStyle = 'py-0.5 flex justify-start items-center';
const alertIconStyle = 'w-4 h-4';
const alertTextStyle = 'flex-1 text-sm font-normal text-zinc-800 leading-tight';
