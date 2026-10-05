'use client';

import { LOCALES, requestStatus } from '@whitbread-eos/api';
import { Skeleton, useToast } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardFooter, WizardPage } from '@whitbread-eos/layout';
import { analytics, getAuthCookie } from '@whitbread-eos/utils';
import { useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import { directDebit, updateResumeUrl, getDdSepaFormStatus } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useState } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState, PayApplicationStep } from '../types';

type Props = {
  locale: LOCALES;
  iframeUrl: string;
  isStatusPollingEnabled: boolean;
  statusCheckInterval: number;
};

enum DirectDebitStatus {
  PENDING = 'Pending',
  COMPLETE = 'Complete',
}

export function PaymentDetailsDirectDebit({
  locale,
  iframeUrl,
  isStatusPollingEnabled,
  statusCheckInterval,
}: Readonly<Props>) {
  const baseDataTestId = 'DirectDebit';
  const { t } = useTranslation('payApplication');
  const { wizardState, goToStep, setWizardState } = useWizardContext<PayApplicationState>();
  const token = getAuthCookie();
  const { handleWorldlineError } = useWorldlineErrorHandler();
  const [requestPending, setRequestPending] = useState(true);
  const [hostedPageGuid, setHostedPageGuid] = useState('');
  const [directDebitComplete, setDirectDebitComplete] = useState(false);
  const { toast } = useToast();
  const router = useRouter();

  useEffect(() => {
    const getData = async () => {
      setRequestPending(true);

      const result = await directDebit(
        token,
        wizardState.applicationId,
        wizardState.applicationGuid,
        'DIRECT',
        PayApplicationStep.PAYMENT_DETAILS
      );

      if (result?.status === requestStatus.success) {
        setWizardState((prev) => ({
          ...prev,
          hostedPageGuid: result.data?.hostedPageGuid ?? '',
        }));
        setHostedPageGuid(result.data.hostedPageGuid);
      } else {
        handleWorldlineError(result);
      }

      setRequestPending(false);
    };

    getData();
  }, []);

  useEffect(() => {
    let intervalId: ReturnType<typeof setInterval>;
    const checkDdStatus = async () => {
      try {
        const result = await getDdSepaFormStatus(token, hostedPageGuid, wizardState.scheme);
        if (result?.status === DirectDebitStatus.COMPLETE) {
          setDirectDebitComplete(true);
          clearInterval(intervalId);
        }
      } catch {
        setDirectDebitComplete(false);
      }
    };

    if (isStatusPollingEnabled && hostedPageGuid) {
      intervalId = setInterval(checkDdStatus, statusCheckInterval);
    }

    return () => {
      if (intervalId) {
        clearInterval(intervalId);
      }
    };
  }, [hostedPageGuid, isStatusPollingEnabled, statusCheckInterval]);

  const save = async (isSaveAndClose: boolean) => {
    setRequestPending(true);

    const result = await updateResumeUrl(
      token,
      wizardState.applicationId,
      wizardState.applicationGuid,
      PayApplicationStep.SUMMARY
    );

    setRequestPending(false);

    if (!result) {
      toast({
        content: t('notification.message.error'),
        variant: 'error',
      });

      analytics.update({
        validation: t('notification.message.error'),
      });

      return;
    }

    if (isSaveAndClose) {
      router.push(
        getPathForLocale(
          locale,
          `business-pay/pay-application-save?applicationGuid=${wizardState.applicationGuid}&applicationId=${wizardState.applicationId}`
        )
      );
    } else {
      goToStep(PayApplicationStep.SUMMARY);
    }
  };

  const continueLater = () => {
    router.push(getPathForLocale(locale, 'homepage'));
  };

  return (
    <WizardPage
      className="flex"
      footer={
        <WizardFooter
          linkLabel={t('companyDetails.closeOut')}
          buttonLabel={t('your.details.continue')}
          linkDisabled={requestPending}
          buttonDisabled={requestPending || (isStatusPollingEnabled && !directDebitComplete)}
          onLinkClick={() => (isStatusPollingEnabled ? continueLater() : save(true))}
          onButtonClick={() => save(false)}
        />
      }
    >
      {!hostedPageGuid ? (
        <div className={skeletonContainerStyle} data-testid={`${baseDataTestId}-Iframe-Loading`}>
          <Skeleton className={firstSkeletonStyle} />

          <Skeleton className={secondSkeletonStyle} />
          <Skeleton className={secondSkeletonStyle} />
        </div>
      ) : (
        <iframe
          src={`${iframeUrl}${hostedPageGuid}`}
          id="directDebitIframe"
          data-testid={`${baseDataTestId}-Iframe`}
          className={iframeStyle}
        ></iframe>
      )}
      {!requestPending && <ReviewChanges />}
      <Analytics pageName="Pay Application: Payment Details" />
    </WizardPage>
  );
}

const skeletonContainerStyle = 'bg-white p-12 mx-16 my-12 grow';
const firstSkeletonStyle = 'h-10 w-3/4 mb-10';
const secondSkeletonStyle = 'h-40 mb-10';
const iframeStyle = 'mx-16 my-12 grow';
