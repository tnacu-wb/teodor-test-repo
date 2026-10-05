'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import {
  RegistrationCodeInfo,
  AuthenticationQuestion,
  LOCALES,
  AuthenticationAnswer,
} from '@whitbread-eos/api';
import {
  FormInput,
  Button,
  Alert,
  AlertDescription,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useWizardContext, WizardPage } from '@whitbread-eos/layout';
import { formatIBAssetsUrl, useTranslation, getPathForLocale } from '@whitbread-eos/utils';
import { authenticateRegistration } from '@whitbread-eos/utils/server';
import { Info } from 'lucide-react';
import { useState } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import { FormFooter } from '../FormFooter';
import { RegistrationState } from '../types';

type Props = {
  locale: LOCALES;
  icons: Record<string, string>;
  baseDataTestId: string;
  registrationInfo: RegistrationCodeInfo;
  token: string;
};

const getSchemaByQuestions = (questions: AuthenticationQuestion[], t: (key: string) => string) => {
  let schemaRules = {};
  questions.forEach((question: AuthenticationQuestion) => {
    schemaRules = {
      ...schemaRules,
      [`question${String(question.questionId)}`]: z.string().min(1, {
        message: t('auth.payApp.validation.required'),
      }),
    };
  });
  return z.object(schemaRules);
};

const getDefaultValuesByQuestions = (questions: AuthenticationQuestion[]) => {
  let defaultValues = {};
  questions.forEach((question: AuthenticationQuestion) => {
    defaultValues = {
      ...defaultValues,
      [`question${String(question.questionId)}`]: '',
    };
  });
  return defaultValues;
};

export const QuestionsForm = ({
  locale,
  baseDataTestId,
  icons,
  registrationInfo,
  token,
}: Props) => {
  const { setWizardState, goToNextStep } = useWizardContext<RegistrationState>();
  const [isError, setIsError] = useState(false);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const { t } = useTranslation(['auth']);
  const questions = registrationInfo?.authenticationQuestions ?? [];
  const isCostCenterUser = ['CostCenterUser', 'CostCentreHolder'].includes(
    registrationInfo?.registrationRole ?? ''
  );
  let errorMessage = t('auth.payApp.error.authentication.general');
  switch (registrationInfo?.registrationRole) {
    case 'CostCentreHolder':
    case 'CostCenterUser':
      errorMessage = t('auth.payApp.costCenter.pin.invalid');
      break;
    case 'Cardholder':
      errorMessage = t('auth.payApp.error.authentication.cardholder');
      break;
    default:
      errorMessage = t('auth.payApp.error.authentication.general');
      break;
  }

  const schema = getSchemaByQuestions(questions, t);
  type FormValues = z.infer<typeof schema>;
  const methods = useForm<FormValues>({
    resolver: zodResolver(schema),
    defaultValues: getDefaultValuesByQuestions(questions),
  });

  const {
    control,
    formState: { errors },
    trigger,
    handleSubmit,
  } = methods;

  const handleSubmitForm = async (data: FormValues) => {
    setIsError(false);
    setIsSubmitting(true);

    try {
      const answers: AuthenticationAnswer[] = questions.map((question: AuthenticationQuestion) => ({
        questionId: question.questionId as number,
        answer: data[`question${String(question.questionId)}` as keyof FormValues],
      }));
      const result = await authenticateRegistration(
        token,
        registrationInfo.registrationCode,
        answers
      );
      if (!result || !result?.registrationPrePopulatedItems) {
        setIsError(true);
        return;
      }

      setWizardState({
        prepopulatedItems: result.registrationPrePopulatedItems,
        authenticationAnswers: answers,
      });
      goToNextStep();
    } catch {
      setIsError(true);
    } finally {
      setIsSubmitting(false);
    }
  };

  const getTitleByRole = () => {
    if ((registrationInfo?.registrationRole as string) === 'AccountHolder') {
      return t('auth.payApp.security.question.description');
    }
    return t('auth.payApp.securityQuestion.title');
  };

  const handleGoBack = () => {
    window.location.href = getPathForLocale(locale, 'business-pay/register');
  };

  return (
    <WizardPage
      type="form"
      formTitle={getTitleByRole()}
      data-testid={baseDataTestId}
      showBackButton={true}
      onBackClick={handleGoBack}
    >
      <p className={'text-neutral-900 pt-4 pb-2 text-lg'}>{t('auth.payApp.security.question')}</p>
      <p className={'text-neutral-900 pt-2 pb-12'}>
        {t('auth.payApp.security.question.description.valid')}
      </p>
      <form
        id={`${baseDataTestId}-Form`}
        onSubmit={handleSubmit(handleSubmitForm)}
        className="flex flex-col gap-6 max-w-[26.25rem] mobile:max-w-[100%] mb-6"
        data-testid={`${baseDataTestId}-Form`}
      >
        {isError && (
          <Alert variant="red" data-testid={`${baseDataTestId}-error-alert`}>
            <button
              className="absolute top-4 right-4 text-gray-500 hover:text-gray-700 text-sm z-10"
              onClick={() => setIsError(false)}
              data-testid={`${baseDataTestId}-Error-CloseButton`}
            >
              ✕
            </button>
            <Info className="w-4 h-4" />
            <AlertDescription>
              <SanitizedContent
                replacements={{
                  '{contactUsLink}': getPathForLocale(locale, 'contact'),
                }}
              >
                {errorMessage}
              </SanitizedContent>
            </AlertDescription>
          </Alert>
        )}
        {questions.map((question: AuthenticationQuestion) => (
          <div key={question.questionId}>
            <h4
              data-testid={`${baseDataTestId}-Question-Label-${String(question.questionId)}`}
              className={labelStyle}
            >
              {question.question?.replace(/^./, question.question[0].toUpperCase())}
            </h4>
            {isCostCenterUser && (
              <p className="mb-6">{t('auth.payApp.costCenter.pin.notAvailable')}</p>
            )}
            <Controller
              name={`question${String(question.questionId)}` as never}
              control={control}
              render={({ field }) => (
                <FormInput
                  {...field}
                  id={`question${String(question.questionId)}`}
                  type="text"
                  placeholder={t('auth.payApp.security.question.answer')}
                  errors={errors}
                  errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
                  onBlur={() => {
                    trigger(`question${String(question.questionId)}` as never);
                  }}
                />
              )}
            />
          </div>
        ))}
        <Button
          type="submit"
          data-testid={`${baseDataTestId}-Button`}
          variant="saveUpdatesButton"
          className="min-w-[18rem] mobile:w-full mt-6"
          disabled={isSubmitting}
        >
          {t('auth.payApp.continueButton')}
        </Button>
      </form>
      <FormFooter locale={locale} />
    </WizardPage>
  );
};

const labelStyle = 'text-xl font-bold mb-4';
