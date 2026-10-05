'use client';

import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogFooter,
  Button,
  useToast,
} from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { getEmployeeDetails, sendActivationEmail } from '@whitbread-eos/utils/server';
import { useState } from 'react';

export type ResendActivationButtonProps = {
  buttonLabel: string;
  userDetails: Record<string, string>;
  companyId?: string;
  language: string;
  token?: string;
};

const ResendActivationButton = ({
  buttonLabel,
  userDetails,
  companyId,
  language,
  token,
}: ResendActivationButtonProps) => {
  const { t } = useTranslation('users');
  const { toast } = useToast();
  const baseDataTestId = 'Resend-Activation';

  const [isModalOpen, setIsModalOpen] = useState(false);
  const { firstName, lastName, email, title, employeeId } = userDetails || {};

  const handleSubmitResend = async () => {
    setIsModalOpen(false);
    const employeeData = await getEmployeeDetails(companyId, employeeId, token);

    const res = await sendActivationEmail(
      token,
      companyId,
      language,
      email,
      employeeData?.centralCardId
    );

    if (res || res === '') {
      toast({
        content: t('userMgmt.manageEmployees.resendCode.notification.success'),
      });

      window?._satellite?.track('resendActivation');

      return;
    }

    toast({
      content: t('userMgmt.manageEmployees.resendCode.notification.failure'),
      variant: 'error',
    });
  };

  return (
    <>
      <button
        onClick={() => setIsModalOpen(true)}
        className={linkStyle}
        data-testid={`${baseDataTestId}-Row-Button`}
      >
        {buttonLabel}
      </button>
      <Dialog
        open={isModalOpen}
        onOpenChange={setIsModalOpen}
        data-testid={`${baseDataTestId}-Container`}
      >
        <DialogContent className={dialogContentStyle} data-testid={`${baseDataTestId}-content`}>
          <DialogHeader data-testid={`${baseDataTestId}-Header`}>
            <DialogTitle data-testid={`${baseDataTestId}-Title`}>
              {t('userMgmt.manageEmployees.resendCode.title')}
            </DialogTitle>
          </DialogHeader>
          <div className={boxStyle} data-testid={`${baseDataTestId}-Box-container`}>
            <span className={boxTitleStyle} data-testid={`${baseDataTestId}-Box-title`}>
              {t('userMgmt.manageEmployees.resendCode.card.employeeProfile')}
            </span>
            <span
              data-testid={`${baseDataTestId}-Full-Name`}
            >{`${title} ${firstName} ${lastName}`}</span>
            <span data-testid={`${baseDataTestId}-Email`}>{email}</span>
          </div>
          <DialogFooter data-testid={`${baseDataTestId}-Footer-container`}>
            <Button
              data-testid={`${baseDataTestId}-Cancel-Button`}
              variant="dialogOutline"
              onClick={() => setIsModalOpen(false)}
            >
              {t('userMgmt.manageEmployees.resendCode.button.cancel')}
            </Button>
            <Button
              data-testid={`${baseDataTestId}-Submit-Button`}
              variant="dialogDefault"
              onClick={handleSubmitResend}
            >
              {t('userMgmt.manageEmployees.resendCode.button.submit')}
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </>
  );
};

export default ResendActivationButton;

const linkStyle = 'text-secondaryColor underline';
const dialogContentStyle = 'max-w-[700px]';
const boxStyle = 'flex flex-col border rounded-lg border-lightGrey3 p-6';
const boxTitleStyle = 'font-bold text-lg mb-6';
