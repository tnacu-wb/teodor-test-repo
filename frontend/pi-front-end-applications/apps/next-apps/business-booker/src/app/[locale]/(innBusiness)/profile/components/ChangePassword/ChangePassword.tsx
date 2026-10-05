'use client';

import { LOCALES, requestStatus, Customer, requestErrors } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor, Notification, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie } from '@whitbread-eos/utils';
import {
  formatIBAssetsUrl,
  updateProfileDetails,
  useTranslation,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import React, { useEffect, useRef, useState } from 'react';

import { ChangePasswordForm } from '~components/innBusiness/forms/ChangePasswordForm/ChangePasswordForm';

import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';

export type TrackableComponent = {
  onReviewChangesToggle: (area: CHANGES_TRACKER, isOpen: boolean) => void;
  onIsEditableToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onIsUpdatingToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onIsDirtyToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  isEditable: boolean;
  isUpdating: boolean;
  isDirty: boolean;
};

type Props = {
  baseDataTestId?: string;
  icons: Record<string, string>;
  profileDetails: Customer;

  locale?: LOCALES;
} & TrackableComponent;

type formType = {
  data: Record<string, any>;
  submittedForms: Record<string, boolean>;
};

export function ChangePassword({
  baseDataTestId,
  icons,
  profileDetails,
  onReviewChangesToggle,
  onIsEditableToggle,
  onIsUpdatingToggle,
  onIsDirtyToggle,
  isEditable,
  isUpdating,
  isDirty,
}: Props) {
  const { t } = useTranslation(['users', 'profile']);

  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();
  const [showEditButton, setShowEditButton] = useState<boolean>(true);
  const [isCurrentPasswordNotMatchError, setIsCurrentPasswordNotMatchError] = useState(false);
  const [formState, setFormState] = useState<formType>({
    data: {
      contactDetail: profileDetails?.contactDetail,
      paymentPreference: profileDetails?.paymentPreference,
      bookingPreference: profileDetails?.bookingPreference,
    },
    submittedForms: {
      changePassword: false,
    },
  });

  const changePasswordFormRef = useRef<HTMLFormElement | null>(null);

  useEffect(() => {
    if (isEditable === false) {
      setShowEditButton(true);
      setIsCurrentPasswordNotMatchError(false);
    }
  }, [isEditable]);

  const handleButtonClick = () => {
    setShowEditButton(false);
    onIsEditableToggle(CHANGES_TRACKER.CHANGE_PASSWORD, true);
    setIsCurrentPasswordNotMatchError(false);
  };

  const handlePasswordChange = (data: Record<string, string>) => {
    setFormState((prev) => ({
      data: {
        ...prev.data,
        password: data.currentPassword,
        newPassword: data.newPassword,
      },
      submittedForms: { ...prev.submittedForms, changePassword: true },
    }));
  };

  const handleFormSubmit = async () => {
    changePasswordFormRef?.current?.requestSubmit();
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        changePassword: false,
      },
    }));
  };

  useEffect(() => {
    if (!formState.submittedForms.changePassword) {
      return;
    }

    const updateEmployee = async () => {
      onIsUpdatingToggle(CHANGES_TRACKER.CHANGE_PASSWORD, true);

      const updateResponse = await updateProfileDetails(
        profileDetails?.contactDetail?.email,
        formState.data,
        idTokenCookie
      );

      if (updateResponse?.status === requestStatus.success) {
        toast({
          content: t('profile.profile.notification.success'),
        });
        onIsEditableToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
        onIsUpdatingToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
        onIsDirtyToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        router.refresh();
      } else {
        if (updateResponse?.error === requestErrors.currentPasswordNotMatch) {
          setIsCurrentPasswordNotMatchError(true);
          onIsUpdatingToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
        } else {
          toast({
            content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
            variant: 'error',
          });
          onIsUpdatingToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
          resetForms();
        }
      }
    };

    updateEmployee();
  }, [formState]);

  const handleCancelEdit = () => {
    if (isDirty) {
      onReviewChangesToggle(CHANGES_TRACKER.CHANGE_PASSWORD, true);
    } else {
      onIsEditableToggle(CHANGES_TRACKER.CHANGE_PASSWORD, false);
    }
  };

  const handleDirtyChange = (newValue: boolean) => {
    onIsDirtyToggle(CHANGES_TRACKER.CHANGE_PASSWORD, newValue);
  };

  return (
    <div data-testid={`${baseDataTestId}-Change-Password-Container`} className={containerStyle}>
      <h4 data-testid={`${baseDataTestId}-Change-Password-Title`} className={titleStyle}>
        {t('profile.password.preview.title')}
      </h4>
      {showEditButton && (
        <div>
          <div className={yourProfileWrapperStyle}>{t('profile.password.preview.password')}</div>
          <Button
            variant="downloadButton"
            size="downloadButton"
            className={buttonStyle}
            onClick={handleButtonClick}
            data-testid={`${baseDataTestId}-Change-Password-Button`}
          >
            {t('profile.password.changepassword.title')}
          </Button>
        </div>
      )}
      {isEditable && isCurrentPasswordNotMatchError && (
        <Notification
          className={notificationStyle}
          type="error"
          icon={formatIBAssetsUrl(icons['icon.notification.error'])}
          title={t('profile.generic.error')}
          message={t('profile.generic.error.notification')}
        />
      )}

      {isEditable && (
        <>
          <ChangePasswordForm
            onSubmit={handlePasswordChange}
            icons={icons}
            formRef={changePasswordFormRef}
            onDirtyChange={handleDirtyChange}
          />
          <div className={buttonContainerStyle}>
            <Button
              data-testid={`${baseDataTestId}-Change-Password-Update-Password`}
              variant="saveUpdatesButton"
              className={`${secondaryButtonStyle}`}
              onClick={() => handleFormSubmit()}
              disabled={isUpdating}
            >
              {t('profile.password.button.update')}
            </Button>
            <Button
              variant="editButton"
              size="newAddressButton"
              className={`${secondaryButtonStyle} justify-start`}
              onClick={handleCancelEdit}
              data-testid={`${baseDataTestId}-Change-Password-Cancel-update`}
            >
              {t('profile.password.button.cancel')}
            </Button>
          </div>
        </>
      )}
    </div>
  );
}

const containerStyle = 'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
const titleStyle = 'font-bold text-[1.438rem] mb-2';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] h-[3.5rem] mt-[1.5rem] mb-[4rem] border border-secondaryColor ml-auto bg-white ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const yourProfileWrapperStyle = 'space-y-4 mt-4';
const secondaryButtonStyle = 'flex w-1/2 mobile:w-full';
const buttonContainerStyle =
  'pb-16 w-1/2 gap-3 justify-between mobile:flex-col mobile:w-full mobile:items-center';
const notificationStyle = 'mt-6 w-1/2 mobile:w-full';
