'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { RegistrationQuestionWithAnswer } from '@whitbread-eos/api';
import { FormSelect, FormInput } from '@whitbread-eos/atoms/ui';
import { analytics } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, useTranslation, cn } from '@whitbread-eos/utils';
import { defaultQuestionsAndSchema, parseAnswersObj } from '@whitbread-eos/utils/server';
import { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  questions?: RegistrationQuestionWithAnswer[];
  formRef?: MutableRefObject<HTMLFormElement | null>;
  isProfileForm?: boolean;
  onDirtyChange?: (isDirty: boolean) => void;
}

const parseOptions = (options: string[] | null) => {
  return options?.map((option, index) => ({
    value: (index + 1).toString(),
    displayValue: option,
  }));
};

export const RegistrationQuestionsForm = ({
  onSubmit,
  icons,
  questions = [],
  formRef,
  isProfileForm,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation(['users', 'profile']);

  const { questionsSchemaObj, defaultQuestionsObj } = defaultQuestionsAndSchema(
    questions,
    t('users.userMgmt.employee.edit.error.registration.question'),
    t('profile.registrationquestions.validation.required'),
    isProfileForm,
    false
  );

  const schema = z.object(questionsSchemaObj);

  const {
    control,
    handleSubmit,
    formState: { errors },
    trigger,
    watch,
  } = useForm<Record<string, string>>({
    resolver: zodResolver(schema),
    defaultValues: defaultQuestionsObj,
  });

  useEffect(() => {
    const subscription = watch(() => {
      onDirtyChange?.(true);
    });
    return () => subscription.unsubscribe();
  }, [watch, onDirtyChange]);

  const updateAnalytics = () => {
    const registrationQuestionsErrors = Object.values(errors).map((error) => error?.message);
    const currentValidation = window?.analyticsData?.validation ?? '';
    const hasPasswordValidation = currentValidation.split(';').length > 1;
    const passwordValidation = hasPasswordValidation ? `${currentValidation.split(';')[0]};` : '';

    const registrationValidation =
      registrationQuestionsErrors.length > 0
        ? ` registrationQuestions: ${registrationQuestionsErrors.join(', ')}`
        : '';

    analytics.update({
      validation: `${passwordValidation}${registrationValidation}`.trim(),
    });
  };

  if (isProfileForm) {
    updateAnalytics();
  }

  const renderQuestions = questions?.map((question, count) => {
    return (
      <div data-testid={`Registration-Question-${count}`} key={question.id}>
        <span
          data-testid={`Registration-Question-Label-${count}`}
          className={!isProfileForm ? questionHeadingStyle : profileQuestionHeadingStyle}
        >
          {`${question.label}${question.mandatory ? ' *' : ''}`}
        </span>
        {question.type === 'select' ? (
          <Controller
            name={question.id}
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                showLabel={false}
                id={question.id}
                className={isProfileForm && profileInputStyle}
                placeholder={
                  !isProfileForm ? t('users.userMgmt.employee.add.registration.question') : ''
                }
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
                options={parseOptions(question.options)}
                onBlur={() => {
                  trigger(question.id);
                }}
              />
            )}
          />
        ) : (
          <Controller
            name={question.id as string}
            control={control}
            render={({ field }) => (
              <FormInput
                {...field}
                showLabel={false}
                id={question.id}
                className={isProfileForm && profileInputStyle}
                type={'text'}
                placeholder={
                  !isProfileForm ? t('users.userMgmt.employee.add.registration.answer') : ''
                }
                errors={errors}
                errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                onBlur={() => {
                  trigger(question.id);
                }}
              />
            )}
          />
        )}
      </div>
    );
  });

  const excludedKeys = ['customerReferenceAnswer', 'purchaseOrderAnswer'];

  const handleFormSubmit = (data: z.infer<typeof schema>) => {
    const answersObject = parseAnswersObj(data, excludedKeys);

    onSubmit(answersObject);
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleFormSubmit)}
      className={cn(
        formStyle,
        isProfileForm ? profileFormStyle : '',
        !isProfileForm && questions?.length === 0 ? 'hidden' : ''
      )}
    >
      {(isProfileForm || questions?.length > 0) && (
        <>
          <h4
            data-testid="Registration-Questions-Heading"
            className={!isProfileForm ? headingStyle : profileHeadingStyle}
          >
            {!isProfileForm
              ? t('users.userMgmt.employee.add.registration.heading')
              : t('profile.registrationquestions.button.edit')}
          </h4>
          {renderQuestions}
        </>
      )}
    </form>
  );
};

const formStyle = 'flex flex-col gap-6 mt-12';
const headingStyle = 'font-bold text-xl';
const questionHeadingStyle = 'flex font-bold text-xl mb-6';

const profileFormStyle = 'mobile:w-full w-1/2';
const profileHeadingStyle = 'font-semibold text-[1.438rem]';
const profileQuestionHeadingStyle = 'flex font-bold text-base mb-[1rem]';
const profileInputStyle = 'mobile:min-w-full mr-auto';
