'use client';

import { Language, RegistrationQuestionWithAnswer } from '@whitbread-eos/api';
import React from 'react';

import { TrackableComponent } from '../ChangePassword/ChangePassword';
import { CHANGES_TRACKER } from '../ReviewChangesWrapper/ReviewChangesWrapper';
import { CompanyRegistrationQuestionsContent } from './CompanyRegistrationQuestionsContent';
import { CompanyRegistrationQuestionsForm } from './CompanyRegistrationQuestionsForm';

type Props = {
  companyRegistrationQuestions: RegistrationQuestionWithAnswer[];
  baseDataTestId?: string;
  icons: Record<string, string>;
  employeeDetails: Record<string, Record<string, string>>;
  language: Language;
  companyId: string;
} & TrackableComponent;

export function CompanyRegistrationQuestions({
  companyRegistrationQuestions,
  baseDataTestId,
  icons,
  employeeDetails,
  language,
  companyId,
  isEditable,
  isUpdating,
  isDirty,
  onIsEditableToggle,
  onIsUpdatingToggle,
  onReviewChangesToggle,
  onIsDirtyToggle,
}: Props) {
  const handleEditMode = () =>
    onIsEditableToggle(CHANGES_TRACKER.REGISTRATION_QUESTIONS, !isEditable);

  return (
    <div
      data-testid={`${baseDataTestId}-Company-Registration-Questions`}
      className={questionsContainerStyle}
    >
      {!isEditable ? (
        <CompanyRegistrationQuestionsContent
          companyRegistrationQuestions={companyRegistrationQuestions}
          baseDataTestId={baseDataTestId}
          handleEditMode={handleEditMode}
        />
      ) : (
        <CompanyRegistrationQuestionsForm
          companyRegistrationQuestions={companyRegistrationQuestions}
          baseDataTestId={baseDataTestId}
          handleEditMode={handleEditMode}
          icons={icons}
          employeeDetails={employeeDetails}
          language={language}
          companyId={companyId}
          isUpdating={isUpdating}
          isDirty={isDirty}
          onIsUpdatingToggle={onIsUpdatingToggle}
          onReviewChangesToggle={onReviewChangesToggle}
          onIsDirtyToggle={onIsDirtyToggle}
        />
      )}
    </div>
  );
}

const questionsContainerStyle =
  'leading-[2rem] text-darkGrey1 mt-[4rem] border-b-[1px] border-lightGrey3';
