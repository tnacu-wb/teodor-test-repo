'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { requestStatus } from '@whitbread-eos/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  FormCheckbox,
  useToast,
} from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  useTranslation,
  cn,
  formatIBAssetsUrl,
  getPathForLocale,
  getLocaleByPathname,
} from '@whitbread-eos/utils';
import { getPIBACardDetails, resendCodeMutation } from '@whitbread-eos/utils/server';
import { useRouter, usePathname } from 'next/navigation';
import React from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  tetheredGuid: string | undefined;
  cardId: string | undefined;
  scheme: string;
  icons: Record<string, string>;
};

export function ResendCode({ open, onOpenChange, tetheredGuid, cardId, scheme, icons }: Props) {
  const [cardDetails, setCardDetails] = React.useState<any>(null);
  const [isResendingCode, setIsResendingCode] = React.useState(false);
  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();
  const pathname = usePathname();
  const locale = getLocaleByPathname(pathname);
  const { t } = useTranslation(['cards']);

  const fetchCardDetails = async () => {
    const details = await getPIBACardDetails(idTokenCookie, cardId, tetheredGuid, scheme);
    setCardDetails(details);
  };

  React.useEffect(() => {
    if (!open) return;
    fetchCardDetails();
  }, [open]);

  const formMethods = useForm({
    resolver: zodResolver(
      z.object({
        sendEmailCopy: z.boolean().optional(),
      })
    ),
    defaultValues: {
      sendEmailCopy: false,
    },
  });

  const { control, watch } = formMethods;

  const sendEmailCopyValue = watch('sendEmailCopy');

  const handleResendCode = async () => {
    const data = {
      tetheredUserGuid: tetheredGuid,
      cardId,
      inviteCardHolderRequest: {
        registrationInfoEmailAddress: cardDetails?.email ?? '',
        registrationInfoForename: cardDetails?.firstName ?? '',
        registrationInfoSurname: cardDetails?.lastName ?? '',
        registrationInfoTitle: cardDetails?.title ?? '',
        scheme,
        sendMeCopyOfInvite: sendEmailCopyValue,
      },
    };
    setIsResendingCode(true);
    const result = await resendCodeMutation(data, idTokenCookie);
    if (result?.status === requestStatus.success) {
      setIsResendingCode(false);
      toast({
        content: t('cards.cardMgmt.cardDetails.resend.code.reSentText'),
        icon: formatIBAssetsUrl(icons['icon.notification.success']),
      });
      onOpenChange(false);
      cardId &&
        pathname?.includes(cardId) &&
        router.push(getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`));
    } else {
      setIsResendingCode(false);
      onOpenChange(false);
      toast({
        content: t('cards.cardMgmt.addCard.failure.text'),
        variant: 'error',
      });
    }
  };

  const cardHolderEmail = cardDetails?.email || '';
  const cardHolderName = cardDetails?.cardHolderName || '';

  return cardDetails === null ? null : (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={'mobile:!h-dvh'}
        data-testid={'Resend-Code-Dialog'}
        aria-describedby={undefined}
      >
        <DialogHeader>
          <DialogTitle>{t('cards.cardMgmt.cardDetails.resend.code.title')}</DialogTitle>
        </DialogHeader>
        <div className="-mt-6">
          <span>{t('cards.cardMgmt.cardDetails.resend.code.description')}</span>
          <div className={cn('form-details-box', 'mt-12')}>
            <div className="flex flex-row">
              <span className={'font-bold'}>{t('cards.cardMgmt.columns.cardHolderName')}</span>
            </div>
            <div className={cardHolderDetails}>
              {cardHolderName && <span>{cardHolderName}</span>}
              {cardHolderEmail && <span>{cardHolderEmail}</span>}
            </div>
          </div>
          <div className="mt-6">
            <Controller
              name="sendEmailCopy"
              control={control}
              render={({ field }) => (
                <FormCheckbox
                  {...field}
                  id={'Send-Email-Copy'}
                  label={<span>{t('cards.cardMgmt.cardDetails.resend.code.invite.copy')}</span>}
                />
              )}
            />
          </div>
        </div>
        <DialogFooter>
          <Button
            data-testid="ResendCodeDialog-Cancel-Button"
            variant="dialogOutline"
            onClick={() => onOpenChange(false)}
          >
            {t('cards.cardMgmt.cardDetails.editCard.replaceCard.cancel')}
          </Button>
          <Button
            data-testid="ResendCodeDialog-Resend-Code-Button"
            variant="dialogDefault"
            onClick={handleResendCode}
            disabled={isResendingCode}
          >
            {t('cards.cardMgmt.cardDetails.resend.code.button')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}

const cardHolderDetails = 'flex flex-col mt-6';
