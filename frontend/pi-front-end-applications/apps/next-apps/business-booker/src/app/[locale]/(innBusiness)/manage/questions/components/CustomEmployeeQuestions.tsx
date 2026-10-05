'use client';

import {
  Language,
  ManagementInformationQuestion,
  requestStatus,
  TypeOfDeleteModal,
} from '@whitbread-eos/api';
import { Button, useToast } from '@whitbread-eos/atoms/ui';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import {
  updateCompanyUserQuestion,
  createCompanyUserQuestion,
  deleteCompanyUserQuestion,
} from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import { useRef, useState, useEffect } from 'react';

import DeleteModal from '~components/innBusiness/DeleteModal/DeleteModal';
import { ReviewChanges } from '~components/innBusiness/ReviewChanges';
import { CustomQuestionDetailsForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/CustomQuestionDetailsForm';
import { HowToAnswerForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/HowToAnswerForm';
import { WhenShouldWeAskForm } from '~components/innBusiness/forms/BusinessAccountQuestionsForm/WhenShouldWeAskForm';

import { QuestionDirtyChangeHandler, useQuestionDirtyState } from './use-question-dirty-state';

type CustomQuestionDetailsData = {
  customQuestionTitle?: string;
  customQuestionLabel: string;
  mandatoryCustomQuestion: boolean;
};

type WhenToAskData = {
  whenToAsk: NonNullable<ManagementInformationQuestion['location']>;
};

type ManagementInformationAnswerPayload = {
  answerType?: NonNullable<
    ManagementInformationQuestion['managementInformationAnswer']
  >['answerType'];
  answers?: string[] | null;
};

type HowToAnswerData = {
  answerType: ManagementInformationAnswerPayload['answerType'];
  answers: ManagementInformationAnswerPayload['answers'];
};

type FormStateData = Omit<ManagementInformationQuestion, 'managementInformationAnswer'> & {
  managementInformationAnswer?: ManagementInformationAnswerPayload;
};

type FormState = {
  data: FormStateData;
  submittedForms: {
    questionDetails: boolean;
    whenToAsk: boolean;
    howToAnswer: boolean;
  };
};

type Props = {
  icons: Record<string, string>;
  language?: Language;
  companyId: string;
  questionContent: ManagementInformationQuestion;
  onCollapse: (value: boolean) => void;
  onDelete: (questionId: string) => void;
  questionTrackerId?: string;
  isNavigationGuardOwner?: boolean;
  onQuestionDirtyChange?: QuestionDirtyChangeHandler;
};

export function CustomEmployeeQuestions({
  icons,
  questionContent,
  companyId,
  onCollapse,
  onDelete,
  questionTrackerId,
  isNavigationGuardOwner,
  onQuestionDirtyChange,
}: Props) {
  const baseDataTestId = 'CustomEmployeeQuestions';

  const { t } = useTranslation(['company', 'users', 'profile']);
  const idTokenCookie = getAuthCookie();
  const { toast } = useToast();
  const router = useRouter();

  const { questionId, location } = questionContent;

  const questionDetailsFormRef = useRef<HTMLFormElement | null>(null);
  const whenToAskFormRef = useRef<HTMLFormElement | null>(null);
  const howToAnswerFormRef = useRef<HTMLFormElement | null>(null);

  const [formState, setFormState] = useState<FormState>({
    data: questionContent,
    submittedForms: {
      questionDetails: false,
      whenToAsk: false,
      howToAnswer: false,
    },
  });
  const [isUpdating, setIsUpdating] = useState(false);
  const [isReviewChangesOpen, setIsReviewChangesOpen] = useState(false);
  const [isDeleteModalOpen, setIsDeleteModalOpen] = useState(false);
  const [isDeleting, setIsDeleting] = useState(false);
  const { isFormDirty, updateDirtyState } = useQuestionDirtyState({
    questionTrackerId,
    onQuestionDirtyChange,
  });

  const handleQuestionDetails = (data: CustomQuestionDetailsData) => {
    setFormState((prev) => ({
      data: {
        ...prev.data,
        active: true,
        managementHeader: isNewQuestionAdded
          ? data.customQuestionTitle
          : prev.data.managementHeader,
        label: data.customQuestionLabel,
        mandatory: data.mandatoryCustomQuestion,
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
  const handleHowToAnswer = (data: HowToAnswerData) => {
    const nextManagementInformationAnswer: ManagementInformationAnswerPayload = {
      answerType: data.answerType,
      answers: data.answers,
    };

    setFormState((prev) => ({
      data: {
        ...prev.data,
        managementInformationAnswer: nextManagementInformationAnswer,
      },
      submittedForms: { ...prev.submittedForms, howToAnswer: true },
    }));
  };

  const resetForms = () => {
    setFormState((prev) => ({
      ...prev,
      submittedForms: {
        questionDetails: false,
        whenToAsk: false,
        howToAnswer: false,
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
  const isNewQuestionAdded = !questionContent?.questionId;

  const handleResponse = (
    updateResponse: { status: string },
    onDelete?: (questionId: string) => void
  ) => {
    const scrollTo = () => {
      return window.scrollTo({ top: 0, behavior: 'smooth' });
    };
    if (updateResponse?.status === requestStatus.success) {
      toast({
        content: t('company.notification.message.save'),
      });
      setIsUpdating(false);
      updateDirtyState(false);
      onCollapse(false);
      scrollTo();
      onDelete && onDelete(questionId ?? '');
      router.refresh();
    } else {
      toast({
        content: t('company.notification.message.error'),
        variant: 'error',
      });
      setIsUpdating(false);
      scrollTo();
      resetForms();
    }
  };
  useEffect(() => {
    if (
      !formState.submittedForms.questionDetails ||
      !formState.submittedForms.whenToAsk ||
      !formState.submittedForms.howToAnswer
    ) {
      return;
    }

    const omitKey = (data: FormStateData, key: string) =>
      Object.fromEntries(Object.entries(data).filter(([k]) => k !== key));

    const updateCustomQuestions = async () => {
      setIsUpdating(true);
      const filteredData = omitKey(formState.data, 'questionTrackerId');
      const updateResponse = await updateCompanyUserQuestion(
        companyId,
        questionId,
        filteredData,
        idTokenCookie
      );

      handleResponse(updateResponse);
    };

    const addCustomQuestions = async () => {
      setIsUpdating(true);
      const filteredData = omitKey(formState.data, 'questionTrackerId');
      const updateResponse = await createCompanyUserQuestion(
        companyId,
        filteredData,
        idTokenCookie
      );

      handleResponse(updateResponse);
    };

    if (!isUpdating) {
      isNewQuestionAdded ? addCustomQuestions() : updateCustomQuestions();
    }
  }, [isUpdating, formState]);

  const handleFormSubmit = async () => {
    questionDetailsFormRef?.current?.requestSubmit();
    whenToAskFormRef?.current?.requestSubmit();
    howToAnswerFormRef?.current?.requestSubmit();
  };
  const handleDeleteQuestionClick = () => {
    setIsDeleteModalOpen(true);
  };

  const handleDeleteQuestionConfirm = async () => {
    try {
      setIsDeleting(true);
      const updateResponse = await deleteCompanyUserQuestion(
        companyId,
        questionId ?? '',
        idTokenCookie
      );
      handleResponse(updateResponse, onDelete);
    } finally {
      setIsDeleting(false);
      setIsDeleteModalOpen(false);
      setIsReviewChangesOpen(false);
      updateDirtyState(false);
    }
  };

  return (
    <>
      {!isNewQuestionAdded && (
        <div className={deleteQuestionButtonContainerStyle}>
          <Button
            variant="editButton"
            size="newAddressButton"
            onClick={handleDeleteQuestionClick}
            data-testid={`${baseDataTestId}-Delete-question-${questionId}`}
          >
            {t('company.coMngt.questions.details.deleteQuestion.link')}
          </Button>
        </div>
      )}
      <div
        className={`${containerStyle} ${isNewQuestionAdded ? 'mt-6' : ''}`}
        data-testid={`${baseDataTestId}-${
          isNewQuestionAdded ? 'newQuestion' : questionId
        }-container`}
      >
        <CustomQuestionDetailsForm
          onSubmit={handleQuestionDetails}
          icons={icons}
          formRef={questionDetailsFormRef}
          typeOfQuestion={questionId ?? ''}
          questionContent={questionContent}
          isNewQuestionAdded={isNewQuestionAdded}
          onDirtyChange={() => updateDirtyState(true)}
        />
        <WhenShouldWeAskForm
          onSubmit={handleWhenToAsk}
          icons={icons}
          formRef={whenToAskFormRef}
          questionLocation={location}
          typeOfQuestion={questionId ?? ''}
          onDirtyChange={() => updateDirtyState(true)}
        />
        <HowToAnswerForm
          onSubmit={handleHowToAnswer}
          icons={icons}
          formRef={howToAnswerFormRef}
          questionId={questionId ?? ''}
          answers={questionContent?.managementInformationAnswer ?? {}}
          onDirtyChange={() => updateDirtyState(true)}
        />

        <div className={buttonContainerStyle}>
          <Button
            data-testid={`${baseDataTestId}-${questionId}-save-changes-button`}
            variant="dialogDefault"
            className={buttonStyle}
            onClick={() => handleFormSubmit()}
            disabled={isUpdating}
          >
            {t('company.coMngt.saveUpdatesButton')}
          </Button>
          <Button
            data-testid={`${baseDataTestId}-${questionId}-discard-changes-button`}
            variant="newAddressButton"
            size="footerButtons"
            onClick={() => (isFormDirty ? handleDiscardChanges() : handleEditMode())}
            className={buttonStyle}
          >
            {t('company.coMngt.questions.details.discardChangesButton')}
          </Button>
        </div>
        <DeleteModal
          isOpen={isDeleteModalOpen}
          onClose={() => setIsDeleteModalOpen(false)}
          onConfirm={handleDeleteQuestionConfirm}
          isLoading={isDeleting}
          variant={TypeOfDeleteModal.Question}
        />
        {!isUpdating && isFormDirty && (
          <ReviewChanges
            isOpen={isReviewChangesOpen}
            navigationGuardEnabled={isNavigationGuardOwner}
            onDiscard={handleEditMode}
            onContinue={handleDiscardChanges}
          />
        )}
      </div>
    </>
  );
}

const buttonStyle = 'flex w-full';
const buttonContainerStyle = 'mt-4 mb-6 flex flex-col gap-4';
const containerStyle = 'relative w-[420px] flex flex-col mobile:w-full mx-auto';
const deleteQuestionButtonContainerStyle = 'flex justify-end my-4 mobile:mt-3 h-6';
