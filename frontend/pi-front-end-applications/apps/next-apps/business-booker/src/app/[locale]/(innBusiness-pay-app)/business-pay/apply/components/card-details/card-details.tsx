'use client';

import { LOCALES, requestStatus, URLParams } from '@whitbread-eos/api';
import { CardIcon, Button, Skeleton, Notification, useToast } from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardFooter, WizardPage } from '@whitbread-eos/layout';
import { analytics, getAuthCookie } from '@whitbread-eos/utils';
import { useTranslation, formatIBAssetsUrl, getPathForLocale } from '@whitbread-eos/utils';
import { addPayAppCard, updateResumeUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useRouter, useSearchParams } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';

import { Analytics } from '../analytics/analytics';
import { PayApplicationState, PayApplicationStep } from '../types';
import DeletePayAppCard from './delete-pay-app-card';

type Props = {
  locale: LOCALES;
  isTravelManager: boolean;
  isBooker: boolean;
  isBusinessPayManager: boolean;
  appInitiatorDetails: Record<string, string | number | boolean> | undefined;
  isCurrentUserInitiator: boolean;
};

export function CardDetails({
  locale,
  isTravelManager,
  isBooker,
  isBusinessPayManager,
  isCurrentUserInitiator,
  appInitiatorDetails,
}: Readonly<Props>) {
  const hasCardsPermission = isTravelManager || isBooker || isBusinessPayManager;
  const router = useRouter();
  const token = getAuthCookie();
  const searchParams = useSearchParams();
  const urlEmployeeId = searchParams?.get(URLParams.employeeId);
  const { toast } = useToast();
  const { goToStep, goToNextStep, goToPreviousStep, setWizardState, currentStep } =
    useWizardContext<PayApplicationState>();
  const { t } = useTranslation('payApplication');
  const { wizardState, icons } = useWizardContext<PayApplicationState>();
  const hasCards = !!wizardState?.cardDetails.length;
  const hasInitialCards = useRef(!!wizardState?.cardDetails.length).current;
  const attemptedToAddCardRef = useRef(false);
  const [isAddingCard, setIsAddingCard] = useState(false);
  const [showFailError, setShowFailError] = useState(false);
  const [requestPending, setRequestPending] = useState(false);

  useEffect(() => {
    if (!hasInitialCards && !attemptedToAddCardRef.current) {
      setShowFailError(false);
      attemptedToAddCardRef.current = true;
      const addCard = async () => {
        setIsAddingCard(true);
        const response = await addPayAppCard(
          wizardState?.applicationGuid,
          wizardState?.applicationId,
          wizardState?.scheme,
          {
            cardName: `${wizardState?.contactDetails?.foreName} ${wizardState?.contactDetails?.lastName}`,
            emailAddress: wizardState?.contactDetails?.email,
            foreName: wizardState?.contactDetails?.foreName,
            lastName: wizardState?.contactDetails?.lastName,
            myCard: true,
            title: wizardState?.contactDetails?.title,
          },
          token,
          isCurrentUserInitiator ? undefined : appInitiatorDetails?.participantId
        );
        if (response?.status === requestStatus.success) {
          setIsAddingCard(false);
          setWizardState((prev) => ({
            ...prev,
            cardDetails: [
              ...prev.cardDetails,
              {
                myCard: true,
                cardName: `${wizardState?.contactDetails?.foreName} ${wizardState?.contactDetails?.lastName}`,
                cardOwnerName: `${wizardState?.contactDetails?.title} ${wizardState?.contactDetails?.foreName} ${wizardState?.contactDetails?.lastName}`,
                emailAddress: wizardState?.contactDetails?.email,
                cardGuid: response?.cardGuid,
              },
            ],
          }));
        } else {
          setShowFailError(true);
          setIsAddingCard(false);
        }
      };
      addCard();
    }
  }, []);

  useEffect(() => {
    if (urlEmployeeId && currentStep.id === PayApplicationStep.CARD_DETAILS) {
      goToStep(PayApplicationStep.CARD_DETAILS_ADD_CARD);
    }
  }, [urlEmployeeId, currentStep]);

  const save = async (isSaveAndClose: boolean) => {
    setRequestPending(true);

    const result = await updateResumeUrl(
      token,
      wizardState.applicationId,
      wizardState.applicationGuid,
      PayApplicationStep.PAYMENT_DETAILS
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
      goToStep(PayApplicationStep.PAYMENT_DETAILS);
    }
  };

  const parseCardsList = (cards: any) => {
    return cards.map((card: any, key: string) => {
      return (
        <div
          key={key}
          data-testid={`App-Card-Number-${key}`}
          className="form-details-box mb-4 last:mb-0"
        >
          {hasCardsPermission && (
            <DeletePayAppCard
              cardGuid={card.cardGuid}
              applicationGuid={wizardState?.applicationGuid}
              applicationId={wizardState?.applicationId}
            />
          )}
          <div className="flex">
            <CardIcon type={'PI'} icons={icons} className={cardIcon} />
            <div className="flex flex-wrap ml-2">
              <span className="mr-2 text-darkGrey2">{t('card.details.cardHolderName.label')}</span>
              <span className="font-bold">{card.cardOwnerName}</span>
            </div>
          </div>
        </div>
      );
    });
  };

  return urlEmployeeId ? null : (
    <WizardPage
      type="form"
      formTitle={t('card.details.title')}
      showBackButton={true}
      onBackClick={goToPreviousStep}
      footer={
        <WizardFooter
          linkLabel={t('companyDetails.saveAndClose')}
          buttonLabel={t('companyDetails.continue.button')}
          onButtonClick={() => save(false)}
          onLinkClick={() => save(true)}
          linkDisabled={isAddingCard || !hasCards || requestPending}
          buttonDisabled={isAddingCard || !hasCards || requestPending}
        />
      }
    >
      <div data-testid="App-Card-Details-Container" className="flex flex-col">
        <span className="block mt-4">
          {hasCardsPermission ? t('card.details.description') : t('card.details.permission.error')}
        </span>
        <div data-testid="App-Card-List" className="mt-12">
          {showFailError && (
            <Notification
              type="error"
              className="mb-4"
              icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              title={t('application.sent.failed.message')}
              message={t('application.sent.error')}
            />
          )}
          {!hasCards && !isAddingCard && (
            <Notification
              type="warning"
              icon={formatIBAssetsUrl(icons?.['icon.notification.alert'])}
              message={t('card.delete.minimumRequirement')}
            />
          )}
          {isAddingCard && <Skeleton className="w-full h-[100px] rounded" />}
          {parseCardsList(wizardState?.cardDetails)}
        </div>
      </div>
      {hasCardsPermission && (
        <Button
          className="w-full mt-4"
          variant="alternativeDefault"
          disabled={isAddingCard}
          onClick={goToNextStep}
        >
          <Image
            src={formatIBAssetsUrl(icons?.['icon.payment.addCard'])}
            alt="pay app card icon"
            width={24}
            height={24}
          />
          <span className="ml-2">{t('card.details.addAnotherCard')}</span>
        </Button>
      )}
      {!requestPending && <ReviewChanges />}
      <Analytics pageName="Pay Application: Card Details" track="Pay Application: Card Details" />
    </WizardPage>
  );
}

const cardIcon = 'w-auto h-full object-contain w-[unset]';
