'use client';

import { Customer, requestStatus } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { updateProfileDetails } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { RoomRequirementsForm } from '~components/innBusiness/forms/RoomRequirementsForm';

import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';

type Props = {
  baseDataTestId?: string;
  icons: Record<string, string>;
  profileDetails: Customer;
  handleEditMode: () => void;
  isUpdating: boolean;
  isDirty: boolean;
  onIsUpdatingToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onIsEditableToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onReviewChangesToggle: (area: CHANGES_TRACKER, isOpen: boolean) => void;
  onIsDirtyToggle: (area: CHANGES_TRACKER, value: boolean) => void;
};

export function RoomPreferencesForm({
  baseDataTestId,
  icons,
  handleEditMode,
  profileDetails,
  isUpdating,
  isDirty,
  onIsUpdatingToggle,
  onReviewChangesToggle,
  onIsDirtyToggle,
  onIsEditableToggle,
}: Props) {
  const { t } = useTranslation(['users', 'profile']);
  const idTokenCookie = getAuthCookie();
  const router = useRouter();
  const { toast } = useToast();
  const roomsFormRef = useRef<HTMLFormElement | null>(null);
  const [formState, setFormState] = useState<Record<string, any>>({
    data: {
      contactDetail: profileDetails?.contactDetail,
      paymentPreference: profileDetails?.paymentPreference,
      bookingPreference: profileDetails?.bookingPreference,
    },
    submittedForms: {
      roomRequirementsForm: false,
    },
  });

  const handleRoomPreferences = (data: Record<string, any>) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        bookingPreference: {
          ...prev.data.bookingPreference,
          roomRequirements: {
            ...prev.data.roomRequirements,
            adults: data.adults,
            children: data.children,
            cotRequired: data.cotRequired,
            type: data.type,
          },
        },
      },
      submittedForms: { ...prev.submittedForms, roomRequirementsForm: true },
    }));
  };

  const resetSubmittedForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        roomRequirementsForm: false,
      },
    }));
  };

  const handleFormSubmit = async () => roomsFormRef?.current?.requestSubmit();

  useEffect(() => {
    if (!formState.submittedForms.roomRequirementsForm) {
      return;
    }

    const updateEmployee = async () => {
      onIsUpdatingToggle(CHANGES_TRACKER.ROOM_PREFERENCES, true);

      const updateResponse = await updateProfileDetails(
        profileDetails?.contactDetail?.email,
        formState.data,
        idTokenCookie
      );

      if (updateResponse?.status === requestStatus.success) {
        toast({
          content: t('profile.profile.notification.success'),
        });
        handleEditMode();
        onIsUpdatingToggle(CHANGES_TRACKER.ROOM_PREFERENCES, false);
        onIsDirtyToggle(CHANGES_TRACKER.ROOM_PREFERENCES, false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        router.refresh();
      } else {
        toast({
          content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
          variant: 'error',
        });
        onIsUpdatingToggle(CHANGES_TRACKER.ROOM_PREFERENCES, false);
        resetSubmittedForms();
      }
    };

    updateEmployee();
  }, [formState]);

  const handleCancelEdit = () => {
    if (isDirty) {
      onReviewChangesToggle(CHANGES_TRACKER.ROOM_PREFERENCES, true);
    } else {
      onIsEditableToggle(CHANGES_TRACKER.ROOM_PREFERENCES, false);
    }
  };

  const handleDirtyChange = (newValue: boolean) => {
    onIsDirtyToggle(CHANGES_TRACKER.ROOM_PREFERENCES, newValue);
  };

  return (
    <div data-testid={`${baseDataTestId}-Room-Preferences-Form`}>
      <RoomRequirementsForm
        onSubmit={handleRoomPreferences}
        formRef={roomsFormRef}
        icons={icons}
        roomRequirements={profileDetails?.bookingPreference?.roomRequirements}
        onDirtyChange={handleDirtyChange}
      />
      <div
        data-testid={`${baseDataTestId}-Room-Preferences-Buttons-Container`}
        className={buttonsContainerStyle}
      >
        <Button
          variant="saveUpdatesButton"
          size="lg"
          className={buttonStyle}
          data-testid={`${baseDataTestId}-Room-Preferences-Save-Button`}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {t('profile.roomrequirements.button.save')}
        </Button>
        <Button
          variant="link"
          size="sm"
          className={linkButtonStyle}
          data-testid={`${baseDataTestId}-Room-Preferences-Cancel-Button`}
          onClick={handleCancelEdit}
        >
          {t('profile.roomrequirements.button.cancel')}
        </Button>
      </div>
    </div>
  );
}

const buttonsContainerStyle = 'flex flex-col items-start';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[2.5rem] mb-[1rem] font-semibold ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const linkButtonStyle = 'underline text-base text-[#511E62] font-medium pl-0 mb-[4rem]';
