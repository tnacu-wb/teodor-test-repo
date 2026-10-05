'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BusinessQuestionType } from '@whitbread-eos/api';
import { ManagementInformationQuestion } from '@whitbread-eos/api/src';
import { FormCheckbox, FormInput, Switch } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  typeOfQuestion: string;
  questionContent: ManagementInformationQuestion;
  onDirtyChange?: () => void;
}

export const BusinessAccountQuestionDetailsForm = ({
  onSubmit,
  icons,
  formRef,
  typeOfQuestion,
  questionContent,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const [isQuestionActive, setIsQuestionActive] = useState<boolean>(
    questionContent?.active ?? false
  );

  const schema = z.object({
    question: z
      .string()
      .trim()
      .min(1, { message: t('coMngt.questions.details.input.questionTitle.invalid') })
      .regex(/^[A-Za-z0-9\-&_ ]*$/, {
        message: t('coMngt.questions.details.input.questionTitle.validation'),
      }),
    mandatoryQuestion: z.boolean(),
  });

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    trigger,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      question: questionContent?.label ?? '',
      mandatoryQuestion: questionContent?.mandatory,
    },
  });

  useEffect(() => {
    if (isDirty || isQuestionActive !== (questionContent?.active ?? false)) {
      onDirtyChange?.();
    }
  }, [isDirty, isQuestionActive, questionContent?.active]);

  const handleCheckedChange = () => {
    setIsQuestionActive(!isQuestionActive);
  };

  const handleSubmitQuestion = (data: z.infer<typeof schema>) => {
    const finalData = {
      ...data,
      isQuestionActive: isQuestionActive,
    };
    onSubmit(finalData);
  };

  const renderQuestionDetailsForm = () => {
    return (
      <div data-testid={`BusinessAccountQuestionsForm-${typeOfQuestion}-container`}>
        <h4
          data-testid={`BusinessAccountQuestionsForm-${typeOfQuestion}-title`}
          className={headingStyle}
        >
          {t('coMngt.questions.details.question.title')}
        </h4>

        <Controller
          name="question"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id={`BusinessAccountQuestionsForm-typeOfQuestion-${typeOfQuestion}`}
              type="text"
              placeholder={
                typeOfQuestion === BusinessQuestionType.PurchaseOrder
                  ? t('coMngt.questions.details.input.purchaseOrder.label')
                  : t('coMngt.questions.details.input.customerReference.label')
              }
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('question');
              }}
            />
          )}
        />
        <span
          className={labelStyle}
          data-testid={`BusinessAccountQuestionsForm-questionTitle-${typeOfQuestion}-description`}
        >
          {t('coMngt.questions.details.input.questionTitle.description')}
        </span>
        <Controller
          name="mandatoryQuestion"
          control={control}
          render={({ field }) => (
            <FormCheckbox
              {...field}
              className="mt-8"
              id={`BusinessAccountQuestionsForm-${typeOfQuestion}-mandatoryQuestion`}
              label={
                <span data-testid="BusinessAccountQuestionsForm-mandatoryQuestion-label">
                  {t('coMngt.questions.details.mandatoryQuestion.label')}
                </span>
              }
            />
          )}
        />
        <div className={questionActiveContainerStyle}>
          <Switch
            checked={isQuestionActive}
            onCheckedChange={handleCheckedChange}
            data-testid={`BusinessAccountQuestionsForm-${typeOfQuestion}-activeQuestion-switcher`}
          />
          <div
            className={questionActiveLabelStyle}
            data-testid={`BusinessAccountQuestionsForm-${typeOfQuestion}-activeQuestion-label-container`}
          >
            <span
              data-testid={`BusinessAccountQuestionsForm-${typeOfQuestion}-activeQuestion-label`}
              className={'text-darkGrey2'}
            >
              {t('coMngt.questions.details.activeQuestion.label')}
            </span>
          </div>
        </div>
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleSubmitQuestion)}
      className={formStyle}
    >
      {renderQuestionDetailsForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-6 border border-lightGrey2 p-6 rounded-lg bg-white';
const headingStyle = 'font-bold text-base pb-8';
const labelStyle = 'pl-4 pt-2 text-xs';
const questionActiveContainerStyle = 'flex gap-4 items-center mt-8';
const questionActiveLabelStyle = 'flex gap-2 items-center';
