'use client';

import {
  Button,
  ButtonVariantDescriptor,
  Notification,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

type PaymentTypeCtaProps = {
  onClick?: () => void;
  errorMessage?: string;
  icons?: Record<string, string>;
};

export default function PaymentTypeCta({ onClick, errorMessage, icons }: PaymentTypeCtaProps) {
  const baseDataTestId = 'PaymentTypeCta';
  const { t } = useTranslation('profile');
  const [showNotification, setShowNotification] = useState(false);

  useEffect(() => {
    if (errorMessage) {
      setShowNotification(true);
    }
  }, [errorMessage]);

  const handleCloseNotification = () => {
    setShowNotification(false);
  };

  return (
    <section data-testid={`${baseDataTestId}-Container`} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Title`} className={titleStyle}>
        {t('payment.title')}
      </h4>
      {showNotification && (
        <Notification
          type="error"
          message={<SanitizedContent>{t('payment.paymentType.failure')}</SanitizedContent>}
          icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          className={notificationStyle}
          data-testid={`${baseDataTestId}-Error`}
          onClose={handleCloseNotification}
        />
      )}
      {!showNotification && (
        <p className={descriptionStyle} data-testid={`${baseDataTestId}-Description`}>
          {t('payment.description')}
        </p>
      )}
      <Button
        variant="outline"
        size="lg"
        onClick={onClick}
        className={buttonStyle}
        data-testid={`${baseDataTestId}-Add-New-Button`}
      >
        {t('payment.button.add')}
      </Button>
    </section>
  );
}

const containerStyle = 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
const titleStyle = 'font-semibold text-[1.438rem]';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[1.5rem] mb-[4rem] ml-auto leading-[1.5rem] rounded-sm py-[1rem] ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const descriptionStyle = 'leading-[1.5rem] mt-[.5rem] w-[33.2rem] mobile:w-full';
const notificationStyle =
  'w-[531px] h-auto gap-2 p-4 rounded border border-solid mb-4 mt-[1.5rem] mobile:w-full';
