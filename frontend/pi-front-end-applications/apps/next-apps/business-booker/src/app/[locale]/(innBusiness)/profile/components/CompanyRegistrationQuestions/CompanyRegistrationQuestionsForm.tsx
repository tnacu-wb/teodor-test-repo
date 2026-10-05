'use client';

import { Language, RegistrationQuestionWithAnswer, requestStatus } from '@whitbread-eos/api';
import { Button, useToast, ButtonVariantDescriptor } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { updateEmployeeDetails } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useEffect, useRef, useState } from 'react';

import { RegistrationQuestionsForm } from '~components/innBusiness/forms/RegistrationQuestionsForm/RegistrationQuestionsForm';

import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';

type Props = {
  companyRegistrationQuestions: RegistrationQuestionWithAnswer[];
  baseDataTestId?: string;
  handleEditMode: () => void;
  icons: Record<string, string>;
  employeeDetails: Record<string, Record<string, string>>;
  language: Language;
  companyId: string;
  isUpdating: boolean;
  isDirty: boolean;
  onIsUpdatingToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onReviewChangesToggle: (area: CHANGES_TRACKER, value: boolean) => void;
  onIsDirtyToggle: (area: CHANGES_TRACKER, value: boolean) => void;
};

export function CompanyRegistrationQuestionsForm({
  companyRegistrationQuestions,
  baseDataTestId,
  handleEditMode,
  icons,
  employeeDetails,
  language,
  companyId,
  isUpdating,
  isDirty,
  onIsUpdatingToggle,
  onReviewChangesToggle,
  onIsDirtyToggle,
}: Props) {
  const { t } = useTranslation(['users', 'profile']);
  const { toast } = useToast();
  const router = useRouter();
  const idTokenCookie = getAuthCookie();
  const questionsFormRef = useRef<HTMLFormElement | null>(null);

  const {
    firstName,
    lastName,
    emailAddress,
    phoneNumber,
    title,
    address: {
      addressLine1,
      addressLine2,
      addressLine3,
      addressLine4,
      addressLine5,
      country,
      postCode,
    },
    id,
    centralCardId,
    accessLevel,
    employeeStatus,
  } = employeeDetails;

  const [formState, setFormState] = useState({
    data: {
      firstName,
      lastName,
      emailAddress,
      phoneNumber,
      title,
      centralCardId,
      address: {
        addressLine1,
        addressLine2,
        addressLine3,
        addressLine4,
        addressLine5,
        country,
        postCode,
      },
      accessLevel,
      employeeStatus,
      employeeAnswers: {},
    },
  });
  const employeeAnswersLength = Object.keys(formState.data.employeeAnswers).length;

  const handleRegistrationQuestionsDetails = (data: any) => {
    setFormState((prev) => ({
      data: { ...prev.data, employeeAnswers: data },
    }));
  };

  const handleFormSubmit = async () => questionsFormRef?.current?.requestSubmit();

  const handleIsDirty = (newValue: boolean) => {
    onIsDirtyToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, newValue);
  };

  useEffect(() => {
    if (employeeAnswersLength === 0) {
      return;
    }
    const updateEmployee = async () => {
      onIsUpdatingToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, true);
      const updateResponse = await updateEmployeeDetails(
        companyId,
        id ?? '',
        language?.toUpperCase(),
        formState.data,
        idTokenCookie
      );

      if (updateResponse?.status === requestStatus.success) {
        toast({
          content: t('profile.profile.notification.success'),
        });
        onIsUpdatingToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, false);
        onIsDirtyToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        router.refresh();
        handleEditMode();
      } else {
        toast({
          content: t('users.userMgmt.manageEmployees.resendCode.notification.failure'),
          variant: 'error',
        });
        onIsUpdatingToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, false);
      }
    };

    updateEmployee();
  }, [formState]);

  const handleCancelEdit = () => {
    if (isDirty) {
      onReviewChangesToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, true);
    } else {
      handleEditMode();
    }
  };

  return (
    <div data-testid={`${baseDataTestId}-Company-Registration-Questions-Form`}>
      <RegistrationQuestionsForm
        formRef={questionsFormRef}
        questions={companyRegistrationQuestions}
        icons={icons}
        isProfileForm={true}
        onSubmit={handleRegistrationQuestionsDetails}
        onDirtyChange={handleIsDirty}
      />
      <div
        data-testid={`${baseDataTestId}-Company-Registration-Questions-Buttons-Container`}
        className={buttonsContainerStyle}
      >
        <Button
          variant="saveUpdatesButton"
          size="lg"
          className={buttonStyle}
          data-testid={`${baseDataTestId}-Company-Registration-Questions-Save-Button`}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {t('profile.registrationquestions.button.save')}
        </Button>
        <Button
          variant="link"
          size="sm"
          className={linkButtonStyle}
          data-testid={`${baseDataTestId}-Company-Registration-Questions-Cancel-Button`}
          onClick={handleCancelEdit}
        >
          {t('profile.registrationquestions.button.cancel')}
        </Button>
      </div>
    </div>
  );
}

const buttonsContainerStyle = 'flex flex-col items-start';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[2.5rem] mb-[1rem] font-semibold ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
const linkButtonStyle = 'underline text-base text-[#511E62] font-medium pl-0 mb-[4rem]';
