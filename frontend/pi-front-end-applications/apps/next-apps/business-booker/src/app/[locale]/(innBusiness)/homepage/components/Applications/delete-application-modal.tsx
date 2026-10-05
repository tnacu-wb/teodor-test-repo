'use client';

import {
  Button,
  Dialog,
  DialogContent,
  DialogFooter,
  DialogHeader,
  DialogTitle,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import React from 'react';

export interface DeleteApplicationModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  companyName: string;
  isLoading?: boolean;
}

export function DeleteApplicationModal({
  isOpen,
  onClose,
  onConfirm,
  companyName,
  isLoading = false,
}: DeleteApplicationModalProps) {
  const { t } = useTranslation(['homepage']);

  const content = {
    title: t('homepage.home.innbusinessPay.applications.deleteModal.title'),
    subtitle: t('homepage.home.innbusinessPay.applications.deleteModal.description'),
    cancelButton: t('homepage.home.innbusinessPay.applications.deleteModal.cancel'),
    confirmButton: t('homepage.home.innbusinessPay.applications.deleteModal.confirm'),
  };

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent data-testid="DeleteApplicationModal">
        <DialogHeader>
          <DialogTitle>{content.title}</DialogTitle>
          <p>
            <SanitizedContent replacements={{ '{companyName}': companyName }}>
              {content.subtitle}
            </SanitizedContent>
          </p>
        </DialogHeader>
        <DialogFooter>
          <Button
            data-testid="DeleteApplicationModal-CancelButton"
            variant="dialogOutline"
            onClick={onClose}
            disabled={isLoading}
          >
            {content.cancelButton}
          </Button>
          <Button
            data-testid="DeleteApplicationModal-DeleteButton"
            variant="dialogDestructive"
            onClick={onConfirm}
            disabled={isLoading}
          >
            {content.confirmButton}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
