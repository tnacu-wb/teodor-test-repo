'use client';

import {
  CustomerAccountDetails,
  InnBusinessPayCard,
  LOCALES,
  RegistrationRole,
  requestStatus,
} from '@whitbread-eos/api';
import {
  useToast,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  ButtonVariantDescriptor,
  Dialog,
  Notification,
  Checkbox,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { getPathForLocale, formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { activatePibaCard } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import Link from 'next/link';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

interface Props {
  cardDetails: InnBusinessPayCard;
  icons: Record<string, string>;
  accountHolder: CustomerAccountDetails;
  token: string;
  locale?: LOCALES;
  onActivation?: () => void;
  afterActivation?: () => void;
}

export const ActivateCardButton = ({
  cardDetails,
  icons,
  accountHolder,
  token,
  locale,
  onActivation,
  afterActivation,
}: Readonly<Props>) => {
  const [isActivateOpen, setIsActivateOpen] = useState(false);
  const [isConfirmationModalOpen, setIsConfirmationModalOpen] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [isCardReceived, setIsCardReceived] = useState(false);
  const { t } = useTranslation('cards');
  const { toast } = useToast();
  const router = useRouter();

  const activateCard = async () => {
    setIsLoading(true);
    onActivation?.();
    const { tetheredGuid, scheme, registrationRoles } = accountHolder;
    const isOnlyCardHolder =
      registrationRoles?.length === 1 && registrationRoles?.includes(RegistrationRole.CardHolder);
    const addCardResponse = await activatePibaCard(
      tetheredGuid ?? '',
      cardDetails.cardId?.toString(),
      scheme ?? '',
      token
    );
    if (addCardResponse?.status === requestStatus.success) {
      if (isOnlyCardHolder) {
        setIsActivateOpen(false);
        setIsConfirmationModalOpen(true);
      } else {
        router.refresh();
        setIsActivateOpen(false);
        toast({
          content: t('cardMgmt.activateCard.notification.confirmation'),
        });
      }
    } else {
      setIsCardReceived(false);
      setIsActivateOpen(false);
      setIsLoading(false);
      toast({
        content: t('cardMgmt.activateCard.notification.failure'),
        variant: 'error',
      });
    }
    afterActivation?.();
  };

  const activateCardModal = (
    <Dialog open={isActivateOpen} onOpenChange={() => setIsActivateOpen(false)}>
      <DialogContent data-testid="Activate-Card-Modal" className="max-w-[650px]">
        <DialogHeader>
          <DialogTitle>{t('cardMgmt.activateCard.title')}</DialogTitle>
          <Notification
            type="warning"
            icon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            message={t('cardMgmt.activateCard.warningMessage')}
          />
        </DialogHeader>
        <div>
          <div className={boxStyle}>
            <div className="flex mb-2">
              <Image
                src={formatIBAssetsUrl(icons?.['icon.payment.piba'])}
                alt="piba icon"
                width={40}
                height={24}
                className="mr-4"
              />
              <span className="font-bold text-xl leading-6">{cardDetails.cardHolderName}</span>
            </div>
            <div>
              <span className="text-darkGrey2 mr-2">{t('cardMgmt.activateCard.cardNumber')}</span>
              <span className="font-bold">{cardDetails.cardNumber?.slice(-8)}</span>
            </div>
            <div className="flex items-center mt-[3.125rem]">
              <Checkbox
                id="card-received"
                checked={isCardReceived}
                onCheckedChange={() => setIsCardReceived(!isCardReceived)}
                className={checkboxStyle}
                data-testid={'Card-Received-Checkbox'}
                aria-checked={isCardReceived}
              />
              <span>{t('cardMgmt.activateCard.cardReceived')}</span>
            </div>
          </div>
          <div className="mt-4">
            <SanitizedContent>{t('cardMgmt.activateCard.contactUsMessage')}</SanitizedContent>
          </div>
        </div>
        <DialogFooter>
          <Button
            data-testid="Activate-Card-Cancel"
            variant="dialogOutline"
            onClick={() => setIsActivateOpen(false)}
            disabled={isLoading}
          >
            {t('cardMgmt.activateCard.cancelButton')}
          </Button>
          <Button
            data-testid="Activate-Card-Confirm"
            variant="dialogDefault"
            onClick={activateCard}
            disabled={isLoading || !isCardReceived}
          >
            {t('cardMgmt.activateCard.ActivateButton')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );

  const activateCardConfirmationModal = (
    <Dialog
      open={isConfirmationModalOpen}
      onOpenChange={() => {
        router.refresh();
        setIsConfirmationModalOpen(false);
      }}
    >
      <DialogContent data-testid="Card-Confirmation-Modal" className="max-w-[650px]">
        <DialogHeader>
          <Image
            alt="success icon"
            width={32}
            height={32}
            src={formatIBAssetsUrl(t('cardMgmt.activateCard.confirmation.icon'))}
          />
          <DialogTitle>{t('cardMgmt.activateCard.confirmation.title')}</DialogTitle>
        </DialogHeader>
        <div>{t('cardMgmt.activateCard.confirmation.message')}</div>
        <DialogFooter className={dialogFooterContainerStyle}>
          <Button
            data-testid="Back-Confirmation-Button"
            className={dialogFooterBtnStyle}
            variant="dialogOutline"
            onClick={() => {
              router.refresh();
              setIsConfirmationModalOpen(false);
            }}
          >
            {t('cardMgmt.submission.backButton')}
          </Button>
          <Link
            className={linkButtonContainerStyle}
            href={getPathForLocale(locale, 'spending/memorable-word')}
          >
            <Button
              className={dialogFooterBtnStyle}
              data-testid="Set-Memorable-Word-Button"
              variant="dialogDefault"
            >
              {t('cardMgmt.activateCard.confirmation.setWordButton')}
            </Button>
          </Link>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );

  return (
    <>
      <button
        data-testid="Activate-Card-Popup-Button"
        type="button"
        className={linkStyle}
        onClick={() => setIsActivateOpen(true)}
      >
        {t('cardMgmt.cardStatus.options.activate')}
      </button>
      {activateCardModal}
      {activateCardConfirmationModal}
    </>
  );
};

const dialogFooterContainerStyle =
  'mobile:max-w-[100%] tablet:max-w-[100%] max-w-[calc(650px-2*3rem)]';
const linkButtonContainerStyle = 'flex flex-1 mobile:w-full w-1/2';
const dialogFooterBtnStyle = `mobile:w-full ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const linkStyle = 'text-secondaryColor underline';
const boxStyle = 'flex flex-col border rounded-lg border-lightGrey3 p-6';
const checkboxStyle = 'w-5 h-5 border-lightGrey1 text-primaryColor mr-2';
