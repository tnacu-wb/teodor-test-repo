'use client';

import { useState, useCallback, useEffect } from 'react';

import { useIframe } from './hooks/useIframe';

export type IframeData = {
  paymentRedirect: string | null;
  providerUrl: string | null;
  template: string | null;
  sessionId: string;
};

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

type AddCardIframeProps = {
  iframeData: IframeData;
  onSuccess: (data: any) => void;
  onError: (message: string) => void;
  onLoading?: () => void;
  isPiba?: boolean;
  profileDetails?: ProfileDetails;
  hideBorder?: boolean;
};

export const AddCardIframe = ({
  iframeData,
  onSuccess,
  onError,
  onLoading,
  isPiba = false,
  profileDetails,
  hideBorder = false,
}: AddCardIframeProps) => {
  const [formData, setFormData] = useState({
    cardHolderName: profileDetails?.paymentPreference?.paymentCard?.cardHolderName ?? '',
    expiryDate: profileDetails?.paymentPreference?.paymentCard?.expiryDate ?? '',
    cardNumber: profileDetails?.paymentPreference?.paymentCard?.cardNumber ?? '',
    cardType: profileDetails?.paymentPreference?.paymentCard?.cardType ?? '',
  });

  const handleSuccess = useCallback(
    (data: any) => {
      onSuccess({
        ...data,
        cardHolderName:
          profileDetails?.paymentPreference?.paymentCard?.cardHolderName ||
          formData.cardHolderName ||
          data.cardHolderName,
        expiryDate:
          profileDetails?.paymentPreference?.paymentCard?.expiryDate ||
          formData.expiryDate ||
          data.expiryDate,
        cardNumber:
          profileDetails?.paymentPreference?.paymentCard?.cardNumber ||
          formData.cardNumber ||
          data.cardNumber ||
          data.tokenNo ||
          '',
        cardType:
          profileDetails?.paymentPreference?.paymentCard?.cardType ||
          formData.cardType ||
          data.cardType,
      });
    },
    [formData, onSuccess, profileDetails]
  );

  useEffect(() => {
    const handleMessage = (event: MessageEvent) => {
      if (typeof event.data !== 'string') return;

      const message = event.data.toLowerCase();

      if (message.includes('submitted')) {
        onLoading?.();
      }

      try {
        const data = JSON.parse(event.data);

        if (
          !profileDetails?.paymentPreference?.paymentCard &&
          (data.cardHolderName || data.expiryDate || data.cardNumber || data.cardType)
        ) {
          setFormData((prev) => ({
            ...prev,
            cardHolderName: data.cardHolderName || prev.cardHolderName,
            expiryDate: data.expiryDate || prev.expiryDate,
            cardNumber: data.cardNumber || prev.cardNumber,
            cardType: data.cardType || prev.cardType,
          }));
        }

        if (
          data.paymentId ||
          data.authCode ||
          data.tokenNo ||
          (data.type === 'form_data_final' && (data.paymentId || data.authCode))
        ) {
          handleSuccess(data);
        }
      } catch (error) {
        if (message.includes('success') || message.includes('complete')) {
          handleSuccess({ message, status: 'success' });
        } else if (message.includes('error') || message.includes('fail')) {
          onError(event.data);
        }
      }
    };

    window.addEventListener('message', handleMessage);
    return () => window.removeEventListener('message', handleMessage);
  }, [handleSuccess, onError, profileDetails]);

  const iframeContainerRef = useIframe({
    iframeData: iframeData?.paymentRedirect ?? '',
    providerUrl: iframeData?.providerUrl ?? '',
    iframeId: 'payment-iframe',
    isPiba,
    hideBorder,
  });

  return (
    <div
      ref={iframeContainerRef}
      className={styles.iframeContainer}
      data-testid="add-card-iframe-element"
    />
  );
};

const styles = {
  iframeContainer: 'w-full h-full',
};
