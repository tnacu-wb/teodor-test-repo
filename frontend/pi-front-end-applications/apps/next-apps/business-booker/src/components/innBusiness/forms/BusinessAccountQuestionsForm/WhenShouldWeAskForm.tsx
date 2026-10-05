'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { QuestionLocationType } from '@whitbread-eos/api';
import { FormRadioGroup } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject, useEffect } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  questionLocation?: string;
  typeOfQuestion: string;
  onDirtyChange?: () => void;
}

export const WhenShouldWeAskForm = ({
  onSubmit,
  icons,
  formRef,
  questionLocation,
  typeOfQuestion,
  onDirtyChange,
}: Readonly<Props>) => {
  const { t } = useTranslation('company');

  const schema = z.object({
    whenToAsk: z.string().min(1, { message: t('coMngt.questions.details.when.option.invalid') }),
  });

  const items = [
    {
      value: QuestionLocationType.R,
      label: t('coMngt.questions.details.when.option.registering'),
    },
    {
      value: QuestionLocationType.B,
      label: t('coMngt.questions.details.when.option.booking'),
    },
  ];

  const {
    control,
    handleSubmit,
    formState: { errors, isDirty },
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      whenToAsk: questionLocation
        ? questionLocation === QuestionLocationType.B
          ? QuestionLocationType.B
          : QuestionLocationType.R
        : '',
    },
  });

  useEffect(() => {
    if (isDirty) {
      onDirtyChange?.();
    }
  }, [isDirty]);

  const whenToAskWatch = watch('whenToAsk');
  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
  };

  const handleSubmitWhenToAsk = (data: z.infer<typeof schema>) => {
    if (!data.whenToAsk) return;
    const finalData = {
      ...data,
    };
    onSubmit(finalData);
  };

  const renderContainerForm = () => {
    return (
      <div data-testid={`WhenShouldWeAskForm-${typeOfQuestion}-container`}>
        <h4 data-testid={`WhenShouldWeAskForm-${typeOfQuestion}-title`} className={headingStyle}>
          {t('coMngt.questions.details.when.title')}
        </h4>

        <Controller
          name="whenToAsk"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={items}
              onChange={(value: string) => handleOptionChange(value, field)}
              variant="col-styled"
              selectedValue={whenToAskWatch}
              customId={`WhenShouldWeAskForm-${typeOfQuestion}`}
            />
          )}
        />
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(handleSubmitWhenToAsk)}
      className={formStyle}
    >
      {renderContainerForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mt-4 border border-lightGrey2 p-6 rounded-lg bg-white';
const headingStyle = 'font-bold text-base pb-6';
