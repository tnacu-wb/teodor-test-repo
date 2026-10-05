'use client';

import { PayApplicationDetails } from '@whitbread-eos/api';
import { useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { deleteApplication } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import React, { useCallback, useState } from 'react';

import { DeleteApplicationModal } from './delete-application-modal';

export interface DeleteActionProps {
  application: PayApplicationDetails;
  baseDataTestId: string;
}

export default function DeleteAction({
  application: { accountName, applicationId, applicationGuid, scheme },
  baseDataTestId,
}: DeleteActionProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const { t } = useTranslation(['homepage']);
  const token = getAuthCookie();
  const router = useRouter();
  const { toast } = useToast();

  const handleDelete = useCallback(() => {
    setIsOpen(true);
  }, []);

  const handleClose = useCallback(() => {
    setIsOpen(false);
  }, []);

  const handleConfirm = useCallback(async () => {
    try {
      setIsDeleting(true);

      const result = await deleteApplication(applicationId, applicationGuid, scheme, token);

      if (result) {
        router.refresh();
      } else {
        toast({
          content: t('Something went wrong. Please try again.'),
          variant: 'error',
        });
      }
    } finally {
      setIsOpen(false);
      setIsDeleting(false);
    }
  }, [applicationId, applicationGuid, router, token, toast]);

  return (
    <>
      <p className={deleteStyle}>
        <span className={linkStyle} data-testid={baseDataTestId} onClick={handleDelete}>
          {t('homepage.home.innbusinessPay.applications.delete.link')}
        </span>
      </p>
      <DeleteApplicationModal
        companyName={accountName ?? ''}
        isOpen={isOpen}
        onClose={handleClose}
        onConfirm={handleConfirm}
        isLoading={isDeleting}
      />
    </>
  );
}

const deleteStyle = 'flex-[0.4]';
const linkStyle = 'underline text-secondaryColor underline-offset-2 cursor-pointer';
