'use client';

import { LOCALES } from '@whitbread-eos/api';
import { SanitizedContent, Button } from '@whitbread-eos/atoms/ui';
import { WizardPage, useWizardContext } from '@whitbread-eos/layout';
import { useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import Link from 'next/link';
import { useRouter } from 'next/navigation';

import { RegisterValidationState } from '../../page';
import Analytics from '../Analytics/analytics';

interface Props {
  locale: LOCALES;
}

const PAGE_NAME = 'Confirm Account';

const AccountExistsManager = ({ locale }: Props) => {
  const baseDataTestId = 'AccountExistsManager';
  const router = useRouter();
  const { t } = useTranslation('auth');

  const { goToPreviousStep } = useWizardContext<RegisterValidationState>();

  return (
    <WizardPage
      type="form"
      formTitle={t('signup.accountExists.emailTaken.heading')}
      showBackButton={true}
      onBackClick={goToPreviousStep}
    >
      <div data-testid={`${baseDataTestId}-wrapper`} className={wrapperStyle}>
        <div className={contentContainerStyle}>
          <p data-testid={`${baseDataTestId}-description`}>
            <SanitizedContent>{t('signup.accountExists.emailTaken.description')}</SanitizedContent>
          </p>
        </div>
        <div className={contentContainerStyle}>
          <Button
            onClick={() => {
              router.push(getPathForLocale(locale, 'account/login'));
            }}
            data-testid={`${baseDataTestId}-Button`}
            variant="default"
          >
            {t('signup.accountExists.emailTaken.loginButton')}
          </Button>
          <Link
            data-testid={`${baseDataTestId}-forgot-password`}
            className={forgotPasswordStyle}
            href={getPathForLocale(locale, 'account/forgot')}
          >
            {t('signup.accountExists.emailTaken.forgotPassword')}
          </Link>
        </div>
      </div>
      <Analytics pageName={PAGE_NAME} />
    </WizardPage>
  );
};

export default AccountExistsManager;

const wrapperStyle = 'w-full flex justify-center gap-12 flex flex-col mobile:pt-6 mobile:px-4 pt-4';
const contentContainerStyle = 'flex flex-col gap-4';
const forgotPasswordStyle =
  'text-base p-0 h-[1.5rem] underline text-secondaryColor underline-offset-2 cursor-pointer self-center';
