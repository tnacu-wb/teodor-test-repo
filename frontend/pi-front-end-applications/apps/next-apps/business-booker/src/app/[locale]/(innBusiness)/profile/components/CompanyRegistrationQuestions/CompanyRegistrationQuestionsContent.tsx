'use client';

import { RegistrationQuestionWithAnswer } from '@whitbread-eos/api';
import { Button, ButtonVariantDescriptor } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';

type Props = {
  companyRegistrationQuestions: RegistrationQuestionWithAnswer[];
  baseDataTestId?: string;
  handleEditMode: () => void;
};

export function CompanyRegistrationQuestionsContent({
  companyRegistrationQuestions,
  baseDataTestId,
  handleEditMode,
}: Props) {
  const { t } = useTranslation('profile');

  const companyQuestions = companyRegistrationQuestions?.map((question, count) => {
    return (
      <div
        data-testid={`${baseDataTestId}-Company-Registration-Question-${count}`}
        key={question?.id}
        className={questionStyle}
      >
        <p data-testid={`${baseDataTestId}-Company-Registration-Question-Label`}>
          {question?.label}
        </p>
        <p data-testid={`${baseDataTestId}-Company-Registration-Answer`}>
          {question.type !== 'select'
            ? question?.answer
            : question.options?.[parseInt(question.answer) - 1]}
        </p>
      </div>
    );
  });

  return (
    <div data-testid={`${baseDataTestId}-Company-Registration-Questions-Content`}>
      <h4
        data-testid={`${baseDataTestId}-Company-Registration-Questions-Title`}
        className={questionsTitleStyle}
      >
        {t('registrationquestions.title')}
      </h4>
      {companyQuestions}
      <Button
        variant="outline"
        size="lg"
        className={buttonStyle}
        data-testid={`${baseDataTestId}-Company-Registration-Questions-Button`}
        onClick={() => handleEditMode()}
      >
        {t('registrationquestions.button.edit')}
      </Button>
    </div>
  );
}

const questionsTitleStyle = 'font-semibold text-[1.438rem]';
const questionStyle = 'text-base mt-[1.5rem]';
const buttonStyle = `mobile:min-w-full min-w-[19.313rem] mt-[1.5rem] mb-[4rem] ml-auto leading-[1.5rem] rounded-sm py-[1rem] ${ButtonVariantDescriptor.truncateWithEllipsisButton}`;
