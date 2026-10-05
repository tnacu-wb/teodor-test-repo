'use client';

import { TypeOfDeleteModal } from '@whitbread-eos/api';
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

interface DeleteModalProps {
  isOpen: boolean;
  onClose: () => void;
  onConfirm: () => void;
  isLoading?: boolean;
  variant: TypeOfDeleteModal;
}
interface ModalContent {
  title: string;
  subtitle: string;
  cancelButton: string;
  confirmButton: string;
}

export default function DeleteModal({
  isOpen,
  onClose,
  onConfirm,
  isLoading = false,
  variant,
}: DeleteModalProps) {
  const { t } = useTranslation(['profile', 'company']);

  const contentModal: Record<TypeOfDeleteModal, ModalContent> = {
    [TypeOfDeleteModal.Card]: {
      title: t('profile.profile.deleteCard.confirm'),
      subtitle: t('profile.profile.deleteCard.confirmation'),
      cancelButton: t('profile.profile.deleteCard.cancel'),
      confirmButton: t('profile.profile.deleteCard.confirm'),
    },
    [TypeOfDeleteModal.Question]: {
      title: t('company.coMngt.questions.delete.title'),
      subtitle: t('company.coMngt.questions.delete.subtitle'),
      cancelButton: t('company.coMngt.questions.delete.cancelButton'),
      confirmButton: t('company.coMngt.questions.delete.DeleteButton'),
    },
  };
  const content: ModalContent = contentModal[variant];

  return (
    <Dialog open={isOpen} onOpenChange={onClose}>
      <DialogContent data-testid={`DeleteModal-${variant}`}>
        <DialogHeader>
          <DialogTitle>{content.title}</DialogTitle>
        </DialogHeader>
        <div>
          <SanitizedContent>{content.subtitle}</SanitizedContent>
        </div>
        <DialogFooter>
          <Button
            data-testid={`DeleteModal-${variant}-CancelButton`}
            variant="dialogOutline"
            onClick={onClose}
            disabled={isLoading}
          >
            {content.cancelButton}
          </Button>
          <Button
            data-testid={`DeleteModal-${variant}-DeleteButton`}
            variant="dialogDestructive"
            onClick={onConfirm}
            disabled={isLoading}
            isLoading={isLoading}
          >
            {content.confirmButton}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
