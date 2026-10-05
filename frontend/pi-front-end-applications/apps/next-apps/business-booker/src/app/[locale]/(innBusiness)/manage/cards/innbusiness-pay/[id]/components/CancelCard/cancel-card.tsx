'use client';

import { requestStatus, Scheme } from '@whitbread-eos/api';
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  SanitizedContent,
  Notification,
  useToast,
} from '@whitbread-eos/atoms/ui';
import {
  getAuthCookie,
  useTranslation,
  formatIBAssetsUrl,
  getPathForLocale,
} from '@whitbread-eos/utils';
import { cancelAndReplacePIBACardMutation } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useState } from 'react';

type Props = {
  locale: string;
  scheme: Scheme;
  tetheredUserId: string | undefined;
  cardId: string | undefined;
  open: boolean;
  onOpenChange: (open: boolean) => void;
  icons: Record<string, string>;
};

export function CancelCard({
  locale,
  scheme,
  tetheredUserId,
  cardId,
  open,
  onOpenChange,
  icons,
}: Props) {
  const [isCancelling, setIsCancelling] = useState(false);
  const idTokenCookie = getAuthCookie();
  const { t } = useTranslation(['cards']);
  const { toast } = useToast();
  const router = useRouter();

  const handleCancelCard = async () => {
    const data = {
      tetheredUserId,
      cardId,
      cancelAndReplaceInnBCardRequest: {
        issueReplacement: false,
        scheme,
      },
    };
    setIsCancelling(true);
    const result = await cancelAndReplacePIBACardMutation(data, idTokenCookie);
    if (result?.status === requestStatus.success) {
      window?._satellite?.track('cancelCard');
      setIsCancelling(false);
      toast({
        content: t('cards.cardMgmt.cardDetails.cancelCard.confirmation'),
        variant: 'warning',
        icon: formatIBAssetsUrl(icons['icon.notification.alert']),
      });
      router.push(getPathForLocale(locale, `manage/cards?tab=innbusiness-pay`));
    } else {
      setIsCancelling(false);
      onOpenChange(false);
      toast({
        content: t('cards.cardMgmt.addCard.failure.text'),
        variant: 'error',
      });
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={'mobile:!h-dvh'}
        data-testid={'Cancel-Card-Dialog'}
        aria-describedby={undefined}
      >
        <DialogHeader>
          <DialogTitle>{t('cards.cardMgmt.cardDetails.cancel.title')}</DialogTitle>
        </DialogHeader>
        <div className="-mt-6">
          <SanitizedContent>{t('cards.cardMgmt.cardDetails.cancelCard.title')}</SanitizedContent>
          <Notification
            className={'mt-6'}
            type="warning"
            icon={formatIBAssetsUrl(icons['icon.notification.alert'])}
            message={
              <SanitizedContent>{t('cards.cardMgmt.cardDetails.cancelCard.info')}</SanitizedContent>
            }
          />
        </div>
        <DialogFooter>
          <Button
            data-testid="CancelCardDialog-Cancel-Button"
            variant="dialogOutline"
            onClick={() => onOpenChange(false)}
          >
            {t('cards.cardMgmt.cardDetails.editCard.replaceCard.cancel')}
          </Button>
          <Button
            data-testid="CancelCardDialog-Cancel-Card-Button"
            variant="dialogDestructive"
            onClick={handleCancelCard}
            disabled={isCancelling}
          >
            {t('cards.cardMgmt.cardDetails.cancelCard.button')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
