'use client';

import { LOCALES } from '@whitbread-eos/api';
import { WizardPage, WizardFooter, useWizardContext } from '@whitbread-eos/layout';
import { getPathForLocale, useTranslation } from '@whitbread-eos/utils';
import { useRouter } from 'next/navigation';
import { useRef, useState, useEffect } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { BusinessTypeForm } from '~components/innBusiness/forms/BusinessAccountApply/BusinessTypeForm';
import { CompanyName } from '~components/innBusiness/forms/CompanyDetailsForm/CompanyName';

import { Analytics } from '../analytics/analytics';
import { PayApplicationState } from '../types';

type Props = {
  icons: Record<string, string>;
  locale: LOCALES;
  companyType: string;
};

export function CompanyDetailsBusinessType({ icons, locale, companyType }: Props) {
  const { goToPreviousStep, wizardState, setWizardState, goToNextStep } =
    useWizardContext<PayApplicationState>();
  const baseDataTestId = 'CompanyDetailsBusinessType';
  const { t } = useTranslation('payApplication');
  const router = useRouter();

  const [formState, setFormState] = useState<Record<string, boolean>>({
    companyName: false,
    companyBusinessType: false,
  });
  const [isProcessing, setIsProcessing] = useState<boolean>(false);

  const companyNameFormRef = useRef<HTMLFormElement | null>(null);
  const companyBusinessTypeFormRef = useRef<HTMLFormElement | null>(null);

  const handleCompanyName = (data: Record<string, string>) => {
    setWizardState((prev) => ({
      ...prev,
      accountName: data.companyName,
    }));
    setFormState((prev) => ({
      ...prev,
      companyName: true,
    }));
  };
  const handleCompanyBusinessType = (data: Record<string, string>) => {
    setWizardState((prev) => ({
      ...prev,
      companyDetails: {
        ...prev.companyDetails,
        companyType: data.businessType,
      },
    }));
    setFormState((prev) => ({
      ...prev,
      companyBusinessType: true,
    }));
  };

  const handleFormSubmit = async () => {
    if (isProcessing) {
      return;
    }
    setIsProcessing(true);
    companyNameFormRef?.current?.requestSubmit();
    companyBusinessTypeFormRef?.current?.requestSubmit();
  };

  const handleReturnToHomepage = () => {
    router.push(getPathForLocale(locale, 'homepage'));
  };

  useEffect(() => {
    if (
      !formState.companyBusinessType ||
      !formState.companyName ||
      !wizardState.companyDetails.companyType
    ) {
      return;
    }
    goToNextStep();
    setIsProcessing(false);
  }, [formState, goToNextStep, wizardState.companyDetails.companyType]);

  return (
    <WizardPage
      type="form"
      formTitle={t('companyDetails.title')}
      showBackButton={true}
      onBackClick={goToPreviousStep}
      footer={
        <WizardFooter
          linkLabel={t('companyDetails.closeOut')}
          buttonLabel={t('companyDetails.continue.button')}
          onButtonClick={handleFormSubmit}
          onLinkClick={handleReturnToHomepage}
          buttonDisabled={isProcessing}
        />
      }
    >
      <div className={subtitleStyle}>{t('companyDetails.companyType')}</div>
      <div className={formStyle}>
        <div className={fieldStyle} data-testid={`${baseDataTestId}-company-details`}>
          <CompanyName
            formRef={companyNameFormRef}
            onSubmit={handleCompanyName}
            icons={icons}
            companyName={wizardState.accountName}
            variant={'payApp'}
          />
        </div>
        <div className={fieldStyle}>
          <div className={fieldLabelStyle}>{t('companyDetails.businessType')}</div>
          <BusinessTypeForm
            onSubmit={handleCompanyBusinessType}
            icons={icons}
            formRef={companyBusinessTypeFormRef}
            businessType={wizardState.companyDetails.companyType}
            companyType={companyType}
          />
        </div>
      </div>
      <ReviewChanges />
      <Analytics
        pageName="Pay Application: Company Details"
        businessType={wizardState.companyDetails.companyType}
        track={'Pay Application: Company Details'}
      />
    </WizardPage>
  );
}

const formStyle = 'mt-12 p-6 border border-lightGrey3 bg-white rounded-lg';
const fieldStyle =
  'flex flex-col gap-2 py-6 first:pt-0 last:pb-0 border-b last:border-0 border-lightGrey3';
const fieldLabelStyle = 'font-bold';
const subtitleStyle = 'mt-4';
