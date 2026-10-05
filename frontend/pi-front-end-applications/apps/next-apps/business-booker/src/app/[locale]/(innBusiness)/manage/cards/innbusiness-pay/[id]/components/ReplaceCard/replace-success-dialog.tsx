'use client';

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useTranslation, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';

type Props = {
  open: boolean;
  onOpenChange: (open: boolean) => void;
};

export const ReplaceSuccessDialog = ({ open, onOpenChange }: Props) => {
  const { t } = useTranslation(['cards']);

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent
        className={'mobile:!h-dvh gap-0'}
        data-testid={'Replace-Card-Success-Dialog'}
        aria-describedby={undefined}
      >
        <DialogHeader>
          <DialogTitle>
            <div className="flex flex-col">
              <Image
                width={32}
                height={32}
                alt={'Success Icon'}
                src={formatIBAssetsUrl(t('cards.cardMgmt.activateCard.confirmation.icon'))}
              />
              <span className="mt-4">
                {t('cards.cardMgmt.cardDetails.replaceCard.confirmation.title')}
              </span>
            </div>
          </DialogTitle>
        </DialogHeader>

        <span className="mt-4">
          <SanitizedContent>
            {t('cards.cardMgmt.replaceCard.confirmation.description')}
          </SanitizedContent>
        </span>

        <DialogFooter className="mt-12">
          <div className="flex-1" />
          <Button
            data-testid="Replace-Success-Back-Button"
            variant="dialogDefault"
            onClick={() => onOpenChange(false)}
          >
            {t('cards.cardMgmt.cardDetails.confirmation.backButton')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
};
