'use client';

import { requestStatus } from '@whitbread-eos/api';
import {
  SanitizedContent,
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
} from '@whitbread-eos/atoms/ui';
import { useWizardContext } from '@whitbread-eos/layout';
import { getAuthCookie } from '@whitbread-eos/utils';
import { useTranslation } from '@whitbread-eos/utils';
import { deletePayAppCard } from '@whitbread-eos/utils/server';
import { useState } from 'react';

import { useWorldlineErrorHandler } from '../error-handling';
import { PayApplicationState } from '../types';

export type ResendActivationButtonProps = {
  applicationGuid: string;
  applicationId: string;
  cardGuid: string;
};

const DeletePayAppCard = ({
  cardGuid,
  applicationGuid,
  applicationId,
}: ResendActivationButtonProps) => {
  const { setWizardState } = useWizardContext<PayApplicationState>();
  const { handleWorldlineError } = useWorldlineErrorHandler();
  const { t } = useTranslation('payApplication');
  const token = getAuthCookie();
  const [isDeleting, setIsDeleting] = useState(false);

  const [isModalOpen, setIsModalOpen] = useState(false);

  const handleSubmitDelete = async () => {
    setIsDeleting(true);
    const response = await deletePayAppCard(applicationGuid, applicationId, cardGuid, token);
    if (response?.status === requestStatus.success) {
      setIsDeleting(false);
      setWizardState((prev) => ({
        ...prev,
        cardDetails: prev.cardDetails.filter((card) => card.cardGuid !== cardGuid),
      }));
      setIsModalOpen(false);
    } else {
      setIsDeleting(false);
      handleWorldlineError(response);
    }
  };

  return (
    <>
      <Button
        variant="editButton"
        className="ml-auto p-0 h-auto mb-6"
        data-testid="Delete-PayAppCard-Button"
        onClick={() => setIsModalOpen(true)}
      >
        {t('card.details.deleteCard')}
      </Button>
      <Dialog
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        data-testid="Delete-PayAppCard-Dialog"
      >
        <DialogContent
          className={dialogContentStyle}
          data-testid="Delete-PayAppCard-Dialog-Content"
        >
          <DialogHeader data-testid="Delete-PayAppCard-Dialog-Header">
            <DialogTitle data-testid="Delete-PayAppCard-Dialog-Title">
              {t('card.delete.removeCard')}
            </DialogTitle>
          </DialogHeader>
          <span data-testid="Delete-PayAppCard-Box">
            <SanitizedContent>{t('card.delete.confirmation')}</SanitizedContent>
          </span>
          <DialogFooter data-testid="Delete-PayAppCard-Dialog-Footer">
            <Button
              data-testid="Delete-PayAppCard-Cancel-Button"
              variant="dialogOutline"
              onClick={() => setIsModalOpen(false)}
              disabled={isDeleting}
            >
              {t('card.delete.cancel')}
            </Button>
            <Button
              data-testid="Delete-PayAppCard-Submit-Button"
              variant="dialogDestructive"
              onClick={handleSubmitDelete}
              disabled={isDeleting}
            >
              {t('card.delete.remove')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default DeletePayAppCard;

const dialogContentStyle = 'max-w-[700px]';
