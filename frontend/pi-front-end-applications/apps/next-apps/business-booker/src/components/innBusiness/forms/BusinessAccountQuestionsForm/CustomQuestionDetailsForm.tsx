'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { ManagementInformationQuestion } from '@whitbread-eos/api';
import { FormCheckbox, FormInput, Notification } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React, { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  typeOfQuestion: string;
  questionContent: ManagementInformationQuestion;
  isNewQuestionAdded: boolean;
  onDirtyChange?: () => void;
}

export const CustomQuestionDetailsForm = ({
  onSubmit,
  icons,
  formRef,
  typeOfQuestion,
  questionContent,
  isNewQuestionAdded,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');
  const schema = z.object({
    customQuestionTitle: isNewQuestionAdded
      ? z
          .string()
          .trim()
          .min(1, { message: t('coMngt.questions.details.input.questionTitle.invalid') })
          .regex(/^[A-Za-z0-9\-&_? ]*$/, {
            message: t('coMngt.questions.details.input.questionTitle.validation'),
          })
      : z.string().trim().optional(),
    customQuestionLabel: z
      .string()
      .trim()
      .min(1, { message: t('coMngt.questions.details.input.questionTitle.invalid') })
      .regex(/^[A-Za-z0-9\-&_? ]*$/, {
        message: t('coMngt.questions.details.input.questionTitle.validation'),
      }),
    mandatoryCustomQuestion: z.boolean(),
  });

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    trigger,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      customQuestionTitle: '',
      customQuestionLabel: questionContent?.label ?? '',
      mandatoryCustomQuestion: questionContent?.mandatory ?? false,
    },
  });

  useEffect(() => {
    if (isDirty) {
      onDirtyChange?.();
    }
  }, [isDirty]);

  const handleSubmitQuestion = (data: z.infer<typeof schema>) => {
    const finalData = {
      ...data,
    };
    onSubmit(finalData);
  };

  const renderQuestionDetailsForm = () => {
    return (
      <div data-testid={`CustomQuestionDetailsForm-${typeOfQuestion}-container`}>
        <h4
          data-testid={`CustomQuestionDetailsForm-${typeOfQuestion}-title`}
          className={headingStyle}
        >
          {t('coMngt.questions.details.question.title')}
        </h4>
        {isNewQuestionAdded && (
          <div className={'mb-8'}>
            <Controller
              name="customQuestionTitle"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id={`CustomQuestionDetailsForm-typeOfQuestion-${typeOfQuestion}`}
                  type="text"
                  placeholder={t('coMngt.questions.details.input.questionTitle.label')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    if (isNewQuestionAdded) {
                      trigger('customQuestionTitle');
                    }
                  }}
                />
              )}
            />
            <span
              className={labelStyle}
              data-testid={`CustomQuestionDetailsForm-questionTitle-${typeOfQuestion}-description`}
            >
              {t('coMngt.questions.details.input.questionTitle.description')}
            </span>
          </div>
        )}
        <Controller
          name="customQuestionLabel"
          control={control}
          render={({ field }) => (
            <FormInput
              {...field}
              id={`CustomQuestionDetailsForm-typeOfQuestion-${typeOfQuestion}`}
              type="text"
              placeholder={t('coMngt.questions.details.input.questionLabel.label')}
              errors={errors}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              onBlur={() => {
                trigger('customQuestionLabel');
              }}
            />
          )}
        />
        <span
          className={labelStyle}
          data-testid={`CustomQuestionDetailsForm-questionLabel-${typeOfQuestion}-description`}
        >
          {t('coMngt.questions.details.input.questionLabel.description')}
        </span>
        {isNewQuestionAdded && (
          <Notification
            className={notificationStyle}
            type="warning"
            icon={formatIBAssetsUrl(icons['icon.notification.alert'])}
            message={t('coMngt.questions.details.notification.message')}
          />
        )}
        <Controller
          name="mandatoryCustomQuestion"
          control={control}
          render={({ field }) => (
            <FormCheckbox
              {...field}
              className="mt-8"
              id={`CustomQuestionDetailsForm-${typeOfQuestion}-mandatoryQuestion`}
              label={
                <span data-testid="CustomQuestionDetailsForm-mandatoryQuestion-label">
                  {t('coMngt.questions.details.mandatoryQuestion.label')}
                </span>
              }
            />
          )}
        />
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

const formStyle = 'flex flex-col border border-lightGrey2 p-6 rounded-lg bg-white';
const headingStyle = 'font-bold text-base pb-8';
const labelStyle = 'pl-4 pt-2 text-xs';
const notificationStyle = 'mt-4 w-full';
