'use client';

import {
  Language,
  ManagementInformationQuestion,
  requestStatus,
  BusinessQuestionType,
  BusinessQuestionDefaultHeader,
  BusinessQuestionDefaultQuestionId,
} from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { updateBusinessQuestions } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useRef, useState, useEffect } from 'react';

import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { BusinessAccountQuestionDetailsForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/BusinessAccountQuestionDetailsForm';
import { WhenShouldWeAskForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/WhenShouldWeAskForm';

import { QuestionDirtyChangeHandler, useQuestionDirtyState } from './use-question-dirty-state';

type BusinessQuestionDetailsData = {
  isQuestionActive: boolean;
  question: string;
  mandatoryQuestion: boolean;
};

type WhenToAskData = {
  whenToAsk: NonNullable<ManagementInformationQuestion['location']>;
};

type FormState = {
  data: ManagementInformationQuestion;
  submittedForms: {
    questionDetails: boolean;
    whenToAsk: boolean;
  };
};

type Props = {
  icons: Record<string, string>;
  language?: Language;
  companyId: string;
  typeOfQuestion: string;
  questionContent: ManagementInformationQuestion;
  onCollapse: (value: boolean) => void;
  questionTrackerId?: string;
  isNavigationGuardOwner?: boolean;
  onQuestionDirtyChange?: QuestionDirtyChangeHandler;
};

export function BusinessEmployeeQuestions({
  icons,
  typeOfQuestion,
  questionContent,
  companyId,
  onCollapse,
  questionTrackerId,
  isNavigationGuardOwner,
  onQuestionDirtyChange,
}: Props) {
  const baseDataTestId = 'BusinessEmployeeQuestions';

  const { t } = useTranslation(['company', 'users', 'profile']);
  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();

  const questionDetailsFormRef = useRef<HTMLFormElement | null>(null);
  const whenToAskFormRef = useRef<HTMLFormElement | null>(null);

  const [formState, setFormState] = useState<FormState>({
    data: questionContent,
    submittedForms: {
      questionDetails: false,
      whenToAsk: false,
    },
  });
  const [isUpdating, setIsUpdating] = useState(false);
  const [isReviewChangesOpen, setIsReviewChangesOpen] = useState(false);
  const { isFormDirty, updateDirtyState } = useQuestionDirtyState({
    questionTrackerId,
    onQuestionDirtyChange,
  });

  const handleQuestionDetails = (data: BusinessQuestionDetailsData) => {
    setFormState((prev) => ({
      data: {
        ...prev.data,
        active: data.isQuestionActive,
        label: data.question,
        mandatory: data.mandatoryQuestion,
        managementHeader:
          questionContent?.managementHeader ||
          (typeOfQuestion === BusinessQuestionType.CustomerReference
            ? BusinessQuestionDefaultHeader.CustomerReference
            : BusinessQuestionDefaultHeader.PurchaseOrder),
      },
      submittedForms: { ...prev.submittedForms, questionDetails: true },
    }));
  };

  const handleWhenToAsk = (data: WhenToAskData) => {
    setFormState((prev) => ({
      data: { ...prev.data, location: data.whenToAsk },
      submittedForms: { ...prev.submittedForms, whenToAsk: true },
    }));
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        questionDetails: false,
        whenToAsk: false,
      },
    }));
  };

  const handleDiscardChanges = () => {
    setIsReviewChangesOpen((prev) => !prev);
  };
  const handleEditMode = () => {
    updateDirtyState(false);
    onCollapse(false);
    window.scrollTo({ top: 0, behavior: 'smooth' });
  };

  const getQuestionId = () => {
    if (questionContent?.questionId === BusinessQuestionDefaultQuestionId.PurchaseOrder)
      return BusinessQuestionType.PurchaseOrder;
    if (questionContent?.questionId === BusinessQuestionDefaultQuestionId.CustomerReference)
      return BusinessQuestionType.CustomerReference;
    return typeOfQuestion === BusinessQuestionType.CustomerReference
      ? BusinessQuestionType.CustomerReference
      : BusinessQuestionType.PurchaseOrder;
  };

  useEffect(() => {
    if (!formState.submittedForms.questionDetails || !formState.submittedForms.whenToAsk) {
      return;
    }

    const updateQuestions = async () => {
      setIsUpdating(true);

      const questionId = getQuestionId();

      const updateResponse = await updateBusinessQuestions(
        companyId,
        questionId,
        formState.data,
        idTokenCookie
      );

      if (updateResponse?.status === requestStatus.success) {
        toast({
          content: t('company.notification.message.save'),
        });
        setIsUpdating(false);
        updateDirtyState(false);
        onCollapse(false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        router.refresh();
      } else {
        toast({
          content: t('company.notification.message.error'),
          variant: 'error',
        });
        setIsUpdating(false);
        window.scrollTo({ top: 0, behavior: 'smooth' });
        resetForms();
      }
    };

    if (!isUpdating) {
      updateQuestions();
    }
  }, [formState]);

  const handleFormSubmit = async () => {
    questionDetailsFormRef?.current?.requestSubmit();
    whenToAskFormRef?.current?.requestSubmit();
  };

  return (
    <div className={containerStyle} data-testid={`${baseDataTestId}-${typeOfQuestion}-container`}>
      <BusinessAccountQuestionDetailsForm
        onSubmit={handleQuestionDetails}
        icons={icons}
        formRef={questionDetailsFormRef}
        typeOfQuestion={typeOfQuestion}
        questionContent={questionContent}
        onDirtyChange={() => updateDirtyState(true)}
      />
      <WhenShouldWeAskForm
        onSubmit={handleWhenToAsk}
        icons={icons}
        formRef={whenToAskFormRef}
        questionLocation={questionContent?.location}
        typeOfQuestion={typeOfQuestion}
        onDirtyChange={() => updateDirtyState(true)}
      />

      <div className={buttonContainerStyle}>
        <Button
          data-testid={`${baseDataTestId}-${typeOfQuestion}-save-changes-button`}
          variant="dialogDefault"
          className={buttonStyle}
          onClick={() => handleFormSubmit()}
          disabled={isUpdating}
        >
          {t('company.coMngt.saveUpdatesButton')}
        </Button>
        <Button
          data-testid={`${baseDataTestId}-${typeOfQuestion}-discard-changes-button`}
          variant="newAddressButton"
          size="footerButtons"
          onClick={() => (isFormDirty ? handleDiscardChanges() : handleEditMode())}
          className={buttonStyle}
        >
          {t('company.coMngt.questions.details.discardChangesButton')}
        </Button>
      </div>
      {!isUpdating && isFormDirty && (
        <ReviewChanges
          isOpen={isReviewChangesOpen}
          navigationGuardEnabled={isNavigationGuardOwner}
          onDiscard={handleEditMode}
          onContinue={handleDiscardChanges}
        />
      )}
    </div>
  );
}

const buttonStyle = 'flex w-full';
const buttonContainerStyle = 'mt-4 mb-6 flex flex-col gap-4';
const containerStyle = 'relative w-[420px] mobile:w-full mx-auto my-0 mobile:m-0';
