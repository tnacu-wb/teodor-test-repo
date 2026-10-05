'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { LOCALES } from '@whitbread-eos/api';
import { FormInput, Button, Alert, AlertDescription, Skeleton } from '@whitbread-eos/atoms/ui';
import { WizardPage } from '@whitbread-eos/layout';
import {
  setPageAnalytics,
  useInnBusinessLogin,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { formatIBAssetsUrl, getPathForLocale, useTranslation } from '@whitbread-eos/utils';
import { TriangleAlert, Loader2 } from 'lucide-react';
import Link from 'next/link';
import { usePathname } from 'next/navigation';
import { useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import checkValidRedirect from '~utils/checkValidRedirect';

import Analytics from '../../../register/components/Analytics/analytics';

type Props = {
  icons: Record<string, string>;
  baseDataTestId?: string;
  locale: LOCALES;
  secureUrl: string;
  isRedirectAfterLoginEnabled?: boolean;
};

const PAGE_NAME = 'Log In';

export const LoginForm = ({
  baseDataTestId,
  icons,
  locale,
  secureUrl,
  isRedirectAfterLoginEnabled,
}: Props) => {
  const [isPageLoaded, setIsPageLoaded] = useState(false);
  const { t } = useTranslation(['auth']);
  const { language, country } = getCountryLanguageByLocale(locale);
  const schema = z.object({
    email: z.string().email(t('auth.signin.email.input.error')),
    password: z.string().min(8, t('auth.signin.password.input.error')),
  });
  const [redirectURL, setRedirectURL] = useState<string | null>(null);
  const pathname = usePathname();

  useEffect(() => {
    if (typeof window !== 'undefined') {
      const params = new URLSearchParams(window.location.search);
      // URLSearchParams sets encodeURIComponent automatically
      setRedirectURL(params.get('redirectURL'));
    }
  }, []);

  useEffect(() => {
    setIsPageLoaded(true);
  }, []);

  useEffect(() => {
    if (pathname) {
      setPageAnalytics(pathname, 'PIB', {}, language);
    }
  }, [pathname, language]);

  type FormValues = z.infer<typeof schema>;
  const methods = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: {
      email: '',
      password: '',
    },
  });

  const {
    control,
    formState: { errors },
    trigger,
    handleSubmit,
  } = methods;

  const isValidRedirect = checkValidRedirect(redirectURL ?? '');

  // Decide where to redirect after login
  const afterLoginRedirect =
    isRedirectAfterLoginEnabled && isValidRedirect
      ? decodeURIComponent(redirectURL!)
      : getPathForLocale(locale, 'homepage');

  const { isError, isSubmitting, handleLogin } = useInnBusinessLogin(
    afterLoginRedirect,
    secureUrl,
    schema
  );

  const formErrors = Object.keys(errors).length > 0 ? Object.values(errors) : [];
  const analyticsValidation = formErrors
    ? (formErrors.map((error) => error?.message) as string[])
    : undefined;

  if (!isPageLoaded) {
    return <LoginFormSkeleton baseDataTestId={baseDataTestId} t={t} />;
  }

  return (
    <>
      <iframe
        src={`${secureUrl}/${country}/${language}/business-booker/common/login.html`}
        style={{ display: 'none' }}
        id="authIframe"
        title="InnBusiness authentication"
        data-testid={`${baseDataTestId}-Iframe`}
      ></iframe>
      <WizardPage
        type="form"
        formTitle={t('auth.signin.page.title')}
        data-testid={baseDataTestId}
        showBackButton={false}
      >
        <p className={'text-neutral-900 pt-[0.6rem] pb-[2.5rem]'}>
          {t('auth.signin.page.subtitle')}
        </p>
        <form
          id={`${baseDataTestId}-Form`}
          onSubmit={handleSubmit(handleLogin)}
          className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%] pb-[2.5rem]"
          data-testid={`${baseDataTestId}-Form`}
          autoComplete="off"
          autoCorrect="off"
        >
          {isError && (
            <Alert variant="amber" data-testid={`${baseDataTestId}-error-alert`}>
              <TriangleAlert className="w-4 h-4" />
              <AlertDescription>
                {t('auth.signin.submit.error.label')}{' '}
                <Link href={getPathForLocale(locale, 'account/forgot')} className={linkStyle}>
                  {t('auth.signin.submit.error.link')}
                </Link>
              </AlertDescription>
            </Alert>
          )}
          <Controller
            name="email"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                id="email"
                type={'text'}
                placeholder={t('auth.signin.email.input.placeholder')}
                errors={errors}
                errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
                onBlur={() => {
                  trigger('email');
                }}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  field.onChange(e);
                  trigger('email');
                }}
              />
            )}
          />
          <Controller
            name="password"
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                id="password"
                type={'password'}
                placeholder={t('auth.signin.password.input.placeholder')}
                errors={errors}
                errorIcon={icons ? formatIBAssetsUrl(icons?.['icon.notification.error']) : ''}
                onBlur={() => {
                  trigger('password');
                }}
                onChange={(e: React.ChangeEvent<HTMLInputElement>) => {
                  field.onChange(e);
                  trigger('password');
                }}
              />
            )}
          />
          <Link
            href={getPathForLocale(locale, 'account/forgot')}
            className={linkStyle}
            data-testid={`${baseDataTestId}-ForgotPassword`}
            prefetch
          >
            {t('auth.signin.forgotten.link')}
          </Link>
          <Button
            type="submit"
            data-testid={`${baseDataTestId}-Button`}
            variant="default"
            className="min-w-[18rem] mobile:w-full"
            disabled={isSubmitting || !isPageLoaded}
          >
            {t('auth.signin.submit.label')}
            {isSubmitting && <Loader2 className="animate-spin ml-4" />}
          </Button>
        </form>
        <p>
          {t('auth.signin.create.label')}{' '}
          <Link
            href={getPathForLocale(locale, 'account/register')}
            className={linkStyle}
            data-testid={`${baseDataTestId}-CreateAccount`}
            prefetch
          >
            {t('auth.signin.create.link')}
          </Link>
        </p>
        <Analytics pageName={PAGE_NAME} validation={analyticsValidation} />
      </WizardPage>
    </>
  );
};

type LoginFormSkeletonProps = {
  baseDataTestId?: string;
  t: (key: string) => string;
};

export const LoginFormSkeleton = ({ baseDataTestId, t }: LoginFormSkeletonProps) => (
  <WizardPage type="form" formTitle={t('auth.signin.page.title')} showBackButton={false}>
    <div
      className="text-neutral-900 pt-[0.6rem] pb-[2.5rem]"
      data-testid={`${baseDataTestId}-Skeleton`}
    >
      <Skeleton className="h-6 w-full bg-neutral-200 dark:bg-neutral-300 mb-2" />
      <Skeleton className="h-6 w-40 bg-neutral-200 dark:bg-neutral-300" />
    </div>
    <form className="flex flex-col gap-4 max-w-[26.25rem] mobile:max-w-[100%] pb-[2.5rem]">
      <Skeleton className="h-14 w-full bg-neutral-200 dark:bg-neutral-300" />
      <Skeleton className="h-14 w-full bg-neutral-200 dark:bg-neutral-300 mb-2" />
      <Skeleton className="h-6 w-40 bg-neutral-200 dark:bg-neutral-300 mb-2" />
      <Skeleton className="h-14 w-full bg-neutral-200 dark:bg-neutral-300" />
    </form>
    <div>
      <Skeleton className="h-6 w-64 bg-neutral-200 dark:bg-neutral-300" />
    </div>
  </WizardPage>
);

const linkStyle = 'text-secondaryColor underline';
