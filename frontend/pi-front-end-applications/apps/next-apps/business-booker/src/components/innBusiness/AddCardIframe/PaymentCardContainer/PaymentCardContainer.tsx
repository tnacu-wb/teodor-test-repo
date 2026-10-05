'use client';

import { Skeleton } from '@whitbread-eos/atoms/ui';
import { useTranslation, getCountryLanguageByLocale } from '@whitbread-eos/utils';
import { saveCard } from '@whitbread-eos/utils/server';
import { useParams } from 'next/navigation';
import { useState, useEffect, useCallback, useRef } from 'react';

import { AddCardIframe, IframeData } from '~components/innBusiness/AddCardIframe/AddCardIframe';

type ProfileDetails = {
  contactDetail?: {
    address?: Record<string, any>;
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

type PaymentCardContainerProps = {
  onSuccess: (cardData?: unknown) => void;
  onCancel: () => void;
  onLoading?: () => void;
  token?: string;
  employeeId?: string;
  profileDetails?: ProfileDetails;
  isPiba?: boolean;
  memorableWord?: string;
  cnpEnabled?: boolean;
  isPreferenceCard: boolean;
  cardId?: string;
  cardLabel?: string;
  hideBorder?: boolean;
};

export const PaymentCardContainer = ({
  onLoading,
  onSuccess,
  onCancel,
  token,
  employeeId,
  profileDetails,
  isPiba = false,
  memorableWord,
  cnpEnabled = false,
  isPreferenceCard,
  cardLabel,
  hideBorder = false,
  cardId = '',
}: PaymentCardContainerProps) => {
  const { t } = useTranslation('profile');
  const params = useParams();
  const { language, country } = getCountryLanguageByLocale(params?.locale as string);
  const [iframeData, setIframeData] = useState<IframeData | null>(null);
  const paymentAttemptedRef = useRef(false);

  useEffect(() => {
    const initiatePayment = async () => {
      if (paymentAttemptedRef.current) return;
      paymentAttemptedRef.current = true;
      try {
        await saveCard({
          cardId,
          token: token ?? '',
          employeeId: employeeId ?? '',
          profileDetails: profileDetails ?? { contactDetail: { address: {} } },
          t,
          isPiba,
          language,
          country,
          cnpEnabled,
          ...(memorableWord ? { memorableWord } : {}),
          onSuccess: (data: IframeData) => {
            if (!data || (!data.paymentRedirect && !data.providerUrl && !data.template)) {
              onCancel();
              return;
            }
            setIframeData(data);
          },
          onError: () => {
            onCancel();
          },
          isPreferenceCard,
          cardLabel,
        });
      } catch (error) {
        onCancel();
      }
    };

    initiatePayment();
  }, []);

  const handleIframeSuccess = useCallback(
    (receivedCardData: any) => {
      const cardDetails = {
        ...receivedCardData,
        cardHolderName:
          profileDetails?.paymentPreference?.paymentCard?.cardHolderName ??
          receivedCardData.cardHolderName,
        expiryDate:
          profileDetails?.paymentPreference?.paymentCard?.expiryDate ?? receivedCardData.expiryDate,
        cardNumber:
          profileDetails?.paymentPreference?.paymentCard?.cardNumber ??
          receivedCardData.cardNumber ??
          receivedCardData.tokenNo,
        cardType:
          profileDetails?.paymentPreference?.paymentCard?.cardType ?? receivedCardData.cardType,
      };

      if (
        cardDetails.paymentId ||
        cardDetails.authCode ||
        cardDetails.tokenNo ||
        receivedCardData.status === 'success'
      ) {
        onSuccess(cardDetails);
      } else {
        onCancel();
      }
    },
    [onSuccess, onCancel, profileDetails]
  );

  const handleIframeError = useCallback(() => {
    onCancel();
  }, [onCancel]);

  return iframeData ? (
    <AddCardIframe
      iframeData={iframeData}
      onSuccess={handleIframeSuccess}
      onError={handleIframeError}
      onLoading={onLoading}
      isPiba={isPiba}
      profileDetails={profileDetails}
      data-testid="add-card-iframe"
      hideBorder={hideBorder}
    />
  ) : (
    <Skeleton className="w-full h-[600px]" />
  );
};
