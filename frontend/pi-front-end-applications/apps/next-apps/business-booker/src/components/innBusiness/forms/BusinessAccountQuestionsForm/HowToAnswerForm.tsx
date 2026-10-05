'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BBAnswerType, ManagementInformationAnswerType } from '@whitbread-eos/api';
import { Button, FormInput, FormRadioGroup } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect, useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  questionId: string;
  answers: ManagementInformationAnswerType;
  onDirtyChange?: () => void;
}

export const HowToAnswerForm = ({
  onSubmit,
  icons,
  formRef,
  questionId,
  answers,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');

  const sanitizeAnswer = (value: string) => value?.trim?.() ?? '';
  const [newAnswersArray, setNewAnswersArray] = useState<string[]>(
    (answers.answers ?? [])
      .map((answer) => sanitizeAnswer(answer))
      .filter((answer): answer is string => Boolean(answer))
  );
  const schema = z.object({
    howToAnswer: z
      .string()
      .min(1, { message: t('coMngt.questions.details.howToAnswer.option.invalid') }),
    presetAnswer: z
      .string()
      .trim()
      .refine(
        (value) => {
          if (howToAnswerWatch === BBAnswerType.U && !newAnswersArray.length) {
            return !!value;
          }
          return true;
        },
        { message: t('coMngt.questions.details.input.presetAnswer.invalid') }
      ),
  });

  const items = [
    {
      value: BBAnswerType.U,
      label: t('coMngt.questions.details.answer.option.preset'),
    },
    {
      value: BBAnswerType.F,
      label: t('coMngt.questions.details.answer.option.own'),
    },
  ];

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    watch,
    trigger,
    resetField,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      howToAnswer: answers.answerType
        ? answers.answerType === BBAnswerType.U
          ? BBAnswerType.U
          : BBAnswerType.F
        : '',
      presetAnswer: '',
    },
  });

  useEffect(() => {
    if (isDirty) {
      onDirtyChange?.();
    }
  }, [isDirty]);

  const howToAnswerWatch = watch('howToAnswer');
  const presetAnswerWatch = watch('presetAnswer');

  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
  };

  const handleAddAnswer = () => {
    const trimmedPresetAnswer = sanitizeAnswer(presetAnswerWatch);
    if (trimmedPresetAnswer === '') return;
    setNewAnswersArray([...newAnswersArray, trimmedPresetAnswer]);
    resetField('presetAnswer');
  };

  const handleDeleteAnswer = (index: number) => {
    setNewAnswersArray(newAnswersArray.filter((_, itemIndex) => itemIndex !== index));
    onDirtyChange?.();
  };

  const handleSubmitCustomQuestion = (data: z.infer<typeof schema>) => {
    if (!data.howToAnswer) return;
    if (data.howToAnswer === BBAnswerType.U && newAnswersArray.length === 0) {
      return;
    }
    const trimmedAnswers =
      data.howToAnswer === BBAnswerType.U
        ? newAnswersArray.map((answer) => sanitizeAnswer(answer)).filter(Boolean)
        : null;
    const finalData = {
      answerType: data.howToAnswer,
      answers: trimmedAnswers,
    };

    onSubmit(finalData);
  };

  const renderContainerForm = () => {
    return (
      <div data-testid={`HowToAnswerForm-${questionId}-container`}>
        <h4 data-testid={`HowToAnswerForm-${questionId}-title`} className={headingStyle}>
          {t('coMngt.questions.details.answer.title')}
        </h4>

        <Controller
          name="howToAnswer"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={items}
              onChange={(value: string) => handleOptionChange(value, field)}
              variant="col-styled"
              selectedValue={howToAnswerWatch}
              customId={`HowToAnswerForm-${questionId}`}
              radioGroupItemClass={radioGroupStyle}
            />
          )}
        />
        {howToAnswerWatch === BBAnswerType.U && (
          <div
            data-testid={`HowToAnswerForm-${questionId}-preset-answer-container`}
            className={containersMarginTop}
          >
            <Controller
              name="presetAnswer"
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id={`HowToAnswerForm-typeOfQuestion-${questionId}`}
                  type="text"
                  placeholder={t('coMngt.questions.details.input.presetAnswer.label')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    trigger('presetAnswer');
                  }}
                />
              )}
            />
            <Button
              data-testid={`HowToAnswerForm-${questionId}-add-answer-button`}
              variant="buttonBGWhiteBorderSecondary"
              size="buttonBGWhiteBorderSecondary"
              className={buttonStyle}
              onClick={() => handleAddAnswer()}
            >
              {t('coMngt.questions.details.addAnswerButton')}
            </Button>
            <div data-testid={`HowToAnswerForm-${questionId}-answer-list`}>
              {newAnswersArray?.map((answer: string, index: number) => (
                <div
                  key={`${answer}-${index}`}
                  data-testid={`HowToAnswerForm-${questionId}-answer-${answer}-${index}`}
                  className={'flex justify-between pt-6'}
                >
                  <span
                    key={`${answer}-${index}`}
                    data-testid={`HowToAnswerForm-${questionId}-answer-text-${answer}-${index}`}
                  >
                    {answer}
                  </span>
                  <Button
                    data-testid={`HowToAnswerForm-${questionId}-answer-button-${answer}-${index}`}
                    variant="newAddressButton"
                    size="footerButtons"
                    onClick={() => handleDeleteAnswer(index)}
                  >
                    {t('coMngt.questions.details.deleteAnswerLink')}
                  </Button>
                </div>
              ))}
            </div>
          </div>
        )}
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleSubmitCustomQuestion)}
      className={formStyle}
    >
      {renderContainerForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-4 border border-lightGrey2 p-6 rounded-lg bg-white';
const headingStyle = 'font-bold text-base pb-6';
const buttonStyle = 'flex w-full mt-6 border border-secondaryColor ml-auto';
const containersMarginTop = 'mt-6';
const radioGroupStyle = 'mobile:h-full';
