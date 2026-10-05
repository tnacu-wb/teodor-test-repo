'use client';

import { TypeOfDeleteModal } from '@whitbread-eos/api';
import { Button, Notification, SanitizedContent, CardIcon } from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import { useState, useEffect } from 'react';

import DeleteModal from '~components/innBusiness/DeleteModal/DeleteModal';

type PaymentCardProps = {
  onDelete?: () => Promise<void>;
  onAddNew?: () => void;
  icons?: Record<string, string>;
  profileDetails?: {
    contactDetail?: {
      title?: string;
      firstName?: string;
      lastName?: string;
    };
    paymentPreference?: {
      paymentCard?: {
        cardNumber?: string;
        expiryDate?: string;
        cardHolderName?: string;
        cardType?: string;
      };
    };
  };
  paymentStatus?: 'success' | 'error';
};

const CARD_TYPE_DISPLAY: Record<string, string> = {
  AC: 'Mastercard',
  AM: 'American Express',
  AT: 'InnBusiness Pay',
  DI: 'Diners Club',
  DL: 'Visa Debit',
  EL: 'Visa Debit',
  MA: 'Mastercard',
  VI: 'Visa Debit',
  MC: 'Mastercard',
  AX: 'American Express',
  PI: 'InnBusiness Pay',
  DN: 'Diners Club',
  VS: 'Visa Debit',
  BD: 'InnBusiness Pay',
  PE: 'InnBusiness Pay',
};

export default function PaymentCard({
  onAddNew,
  onDelete,
  profileDetails,
  icons = {},
  paymentStatus,
}: PaymentCardProps) {
  const { t } = useTranslation('profile');
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [showNotification, setShowNotification] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const [paymentCard, setPaymentCard] = useState(profileDetails?.paymentPreference?.paymentCard);

  useEffect(() => {
    if (paymentStatus === 'error' || paymentStatus === 'success') {
      setShowNotification(true);
    }
  }, [paymentStatus]);

  useEffect(() => {
    setPaymentCard(profileDetails?.paymentPreference?.paymentCard);
  }, [profileDetails]);

  const cardNumber = paymentCard?.cardNumber || '';
  const lastFourDigits = cardNumber.slice(-4);
  const maskedNumber = `•••• •••• •••• ${lastFourDigits}`;
  const cardHolderName = paymentCard?.cardHolderName || '';
  const expiryDate = paymentCard?.expiryDate || '';
  const cardType = paymentCard?.cardType || '';
  const displayCardType = CARD_TYPE_DISPLAY[cardType] || cardType;
  const isInnBusinessPay = ['AT', 'PI', 'BD', 'PE'].includes(cardType);
  const isPIBAEuro = ['BD', 'PE'].includes(cardType);

  const handleDeleteClick = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteConfirm = async () => {
    try {
      setIsDeleting(true);
      if (onDelete) {
        await onDelete();
      } else {
        console.error('PaymentCard: onDelete function was not provided');
      }
    } catch (error) {
      console.error('PaymentCard: Error during card deletion:', error);
    } finally {
      setIsDeleting(false);
      setIsDeleteModalOpen(false);
    }
  };

  return (
    <section data-testid="PaymentCard-Container" className={styles.container}>
      <h4 data-testid="PaymentCard-Title" className={styles.title}>
        {t('payment.title')}
      </h4>

      {showNotification && (
        <div className={styles.successNotification}>
          <Notification
            type={paymentStatus === 'success' ? 'success' : 'error'}
            icon={formatIBAssetsUrl(
              icons[
                paymentStatus === 'success'
                  ? 'icon.notification.success'
                  : 'icon.notification.error'
              ]
            )}
            message={
              <SanitizedContent>
                {t(
                  paymentStatus === 'success'
                    ? 'payment.paymentType.success'
                    : 'payment.paymentType.failure'
                )}
              </SanitizedContent>
            }
            className="w-[420px] h-[72px] gap-2 p-4 rounded border border-solid mb-4 mobile:w-full"
          />
        </div>
      )}

      <section className={styles.card} data-testid="PaymentCard-Card">
        <div className={styles.cardContent}>
          <div className={styles.cardHeaderContainer}>
            <div className={styles.cardHeaderContent}>
              <p className={styles.cardTypeText}>{displayCardType}</p>
              <p className={styles.cardNumber}>{maskedNumber}</p>
            </div>
          </div>

          <div className={styles.cardHolderContainer}>
            <p className={styles.cardHolder}>{cardHolderName}</p>
            <p className={styles.cardHolder}>
              {t('payment.card.expires')} {expiryDate}
            </p>
          </div>

          <div className={styles.cardIconContainer}>
            <div className={isInnBusinessPay ? styles.iconContainerWithBg : styles.iconContainer}>
              <CardIcon type={cardType} icons={icons} className={styles.cardIcon} />
            </div>
          </div>
        </div>
      </section>

      {isInnBusinessPay && (
        <div className={styles.notificationContainer}>
          <Notification
            type="info"
            icon={formatIBAssetsUrl(icons['icon.notification.info'])}
            title=""
            message={
              isPIBAEuro
                ? t('payment.paymentType.notification.PIBA.EURO')
                : t('payment.paymentType.notification.PIBA.UK')
            }
            className={styles.notification}
          />
        </div>
      )}

      <div className={styles.buttonContainer}>
        <button
          onClick={handleDeleteClick}
          className={styles.deleteButton}
          data-testid="PaymentCard-Delete-Button"
          disabled={isDeleting}
        >
          {t('payment.button.delete')}
        </button>

        <Button
          variant="outline"
          size="lg"
          onClick={onAddNew}
          className={styles.addButton}
          data-testid="PaymentCard-Add-New-Button"
          disabled={isDeleting}
        >
          {t('payment.button.addnew')}
        </Button>
      </div>

      <DeleteModal
        isOpen={isDeleteModalOpen}
        onClose={() => setIsDeleteModalOpen(false)}
        onConfirm={handleDeleteConfirm}
        isLoading={isDeleting}
        variant={TypeOfDeleteModal.Card}
      />
    </section>
  );
}

const styles = {
  cardTypeText: 'font-semibold text-[20px] leading-[24px] text-right text-darkGrey1 mb-1',
  container: 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3',
  title: 'font-semibold text-[1.438rem] mb-4',
  card: 'w-[420px] h-[204px] min-h-[204px] p-6 bg-white rounded-sm border border-lightGrey3 relative mobile:w-full',
  cardContent: 'flex flex-col h-full justify-between',
  cardHeaderContainer: 'flex justify-between items-start',
  cardHeaderContent: 'text-right absolute top-6 right-6',
  cardNumber: 'font-normal text-[18px] leading-[27px] text-right text-darkGrey1',
  cardHolderContainer: 'absolute bottom-6 left-6',
  cardHolder: 'font-normal text-[16px] leading-[24px] text-darkGrey1 mb-1',
  cardIconContainer: 'absolute bottom-6 right-6',
  iconContainer: 'w-[53.333333px] h-[32px] rounded-[2px] flex items-center justify-center',
  iconContainerWithBg:
    'w-[53.333333px] h-[32px] rounded-[2px] flex items-center justify-center bg-secondaryColor',
  cardIcon: 'w-full h-full object-contain',
  notificationContainer: 'mt-4 w-[420px] mobile:w-full',
  notification: 'p-4 rounded border flex items-start gap-2',
  buttonContainer: 'flex flex-col space-y-4 mt-6 mb-16',
  deleteButton: 'text-error font-medium text-left px-0 underline',
  addButton:
    'mobile:min-w-full w-[19.313rem] mt-[1.5rem] mb-[4rem] leading-[1.5rem] rounded-sm py-[1rem]',
  successNotification: 'mb-4 w-[420px] mobile:w-full',
  successNotificationContent:
    'p-4 rounded border border-successTint2 bg-successTint flex items-start gap-2',
  errorNotificationContent:
    'p-4 rounded border border-errorTint2 bg-errorTint flex items-start gap-2',
};
