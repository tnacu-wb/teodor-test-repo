'use client';

import { Customer, requestStatus } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { updateProfileDetails } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { ExtrasForm } from '~components/innBusiness/forms/ExtrasForm/ExtrasForm';

import { TrackableComponent } from '../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';

type Props = {
  baseDataTestId: string;
  icons: Record<string, string>;
  profileDetails: Customer;
  handleEditMode: () => void;
} & Partial<TrackableComponent>;

export function MealsAndExtrasForm({
  baseDataTestId,
  icons,
  handleEditMode,
  profileDetails,
  isUpdating,
  isDirty,
  onIsUpdatingToggle,
  onReviewChangesToggle,
  onIsDirtyToggle,
}: Props) {
  const { t } = useTranslation(['users', 'profile']);
  const idTokenCookie = getAuthCookie();
  const router = useRouter();
  const { toast } = useToast();
  const extrasFormRef = useRef<HTMLFormElement | null>(null);
  const [formState, setFormState] = useState<Record<string, any>>({
    data: {
      contactDetail: profileDetails?.contactDetail,
      paymentPreference: profileDetails?.paymentPreference,
      bookingPreference: profileDetails?.bookingPreference,
    },
    submittedForms: {
      mealsAndExtrasForm: false,
    },
  });

  const handleMealsAndExtras = (data: Record<string, any>) => {
    setFormState((prev) => ({
      ...prev,
      data: {
        ...prev.data,
        bookingPreference: {
          ...prev.data.bookingPreference,
          foodPreference: data.foodPreference,
          preselectWifi: data.preselectWifi,
        },
        paymentPreference: {
          ...prev.data.paymentPreference,
          electronicInvoiceRequired: data.electronicInvoiceRequired,
        },
      },
      submittedForms: { ...prev.submittedForms, extrasFormRef: true },
    }));
  };

  const resetSubmittedForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        extrasFormRef: false,
      },
    }));
  };

  const handleFormSubmit = async () => extrasFormRef?.current?.requestSubmit();

  useEffect(() => {
    if (!formState.submittedForms.extrasFormRef) {
      return;
    }

    const updateEmployee = async () => {
      onIsUpdatingToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, true);

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
        onIsUpdatingToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, false);
        onIsDirtyToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        router.refresh();
      } else {
        toast({
          content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
          variant: 'error',
        });
        onIsUpdatingToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, false);
        resetSubmittedForms();
      }
    };

    updateEmployee();
  }, [formState]);

  const handleCancelEdit = () => {
    if (isDirty) {
      onReviewChangesToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, true);
    } else {
      handleEditMode();
    }
  };

  const handleDirtyChange = (newValue: boolean) => {
    onIsDirtyToggle?.(CHANGES_TRACKER.MEALS_EXTRAS, newValue);
  };

  return (
    <div data-testid={`${baseDataTestId}-Meals-And-Extras-Form`}>
      <ExtrasForm
        onSubmit={handleMealsAndExtras}
        formRef={extrasFormRef}
        icons={icons}
        profileDetails={profileDetails}
        onDirtyChange={handleDirtyChange}
      />
      <div
        data-testid={`${baseDataTestId}-Meals-And-Extras-Buttons-Container`}
        className={buttonsContainerStyle}
      >
        <Button
          variant="saveUpdatesButton"
          size="lg"
          className={buttonStyle}
          data-testid={`${baseDataTestId}-Meals-And-Extras-Save-Button`}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {t('profile.extraspreferences.button.save')}
        </Button>
        <Button
          variant="link"
          size="sm"
          className={linkButtonStyle}
          data-testid={`${baseDataTestId}-Meals-And-Extras-Cancel-Button`}
          onClick={handleCancelEdit}
        >
          {t('profile.extraspreferences.button.cancel')}
        </Button>
      </div>
    </div>
  );
}

const buttonsContainerStyle = 'flex flex-col items-start';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mb-[1rem] font-semibold ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const linkButtonStyle = 'underline text-base text-[#511E62] font-medium pl-0 mb-[4rem]';
