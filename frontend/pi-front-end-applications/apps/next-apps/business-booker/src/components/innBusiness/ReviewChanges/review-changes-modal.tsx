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

type ModalProps = {
  testId: string;
  open: boolean;
  onOpenChange?: () => void;
  onDiscard?: () => void;
  onContinue?: () => void;
  dialogTitle?: string;
  dialogDescription?: string;
};

export function ReviewChangesModal({
  testId,
  open,
  onOpenChange,
  onDiscard,
  onContinue,
  dialogTitle,
  dialogDescription,
}: ModalProps) {
  const { t } = useTranslation('users');

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent data-testid={testId}>
        <DialogHeader>
          <DialogTitle>
            <SanitizedContent>
              {dialogTitle || t('userMgmt.employee.edit.review.heading')}
            </SanitizedContent>
          </DialogTitle>
        </DialogHeader>
        <div>
          <SanitizedContent>
            {dialogDescription || t('userMgmt.employee.edit.review.description')}
          </SanitizedContent>
        </div>
        <DialogFooter>
          <Button
            data-testid="ReviewChanges-Discard-Button"
            variant="dialogOutline"
            onClick={onDiscard}
          >
            {t('userMgmt.employee.edit.review.cancel')}
          </Button>
          <Button
            data-testid="ReviewChanges-Continue-Button"
            variant="dialogDefault"
            onClick={onContinue}
          >
            {t('userMgmt.employee.edit.review.continue')}
          </Button>
        </DialogFooter>
      </DialogContent>
    </Dialog>
  );
}
