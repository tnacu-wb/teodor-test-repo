'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BusinessType } from '@whitbread-eos/api';
import { FormRadioGroup } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { MutableRefObject } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

interface Props {
  onSubmit: (data: any) => void;
  icons: Record<string, string>;
  formRef: MutableRefObject<HTMLFormElement | null>;
  businessType: string;
  companyType: string;
}

export const BusinessTypeForm = ({
  onSubmit,
  icons,
  formRef,
  businessType,
  companyType,
}: Readonly<Props>) => {
  const { t } = useTranslation('payApplication');

  const schema = z.object({
    businessType: z.string().min(1, { message: t('payapp.directDebit.selectOption') }),
  });

  const excludedTypes = [BusinessType.Non_Profit_Assoc, BusinessType.SoleTrader_Proprietorship];
  const companyTypeOptions = JSON.parse(companyType)
    .filter((type: BusinessType) => !excludedTypes.includes(type))
    .sort((a: string, b: string) => a.localeCompare(b));

  const items = companyTypeOptions.map((type: string) => ({
    value: type,
    label: type,
  }));

  const {
    control,
    handleSubmit,
    formState: { errors },
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      businessType: businessType,
    },
  });

  const businessTypeWatch = watch('businessType');

  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
  };

  const renderContainerForm = () => {
    return (
      <div data-testid="BusinessTypeForm-container">
        <Controller
          name="businessType"
          control={control}
          render={({ field }) => (
            <FormRadioGroup
              {...field}
              errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
              errors={errors}
              items={items}
              onChange={(value: string) => handleOptionChange(value, field)}
              variant="col-styled"
              selectedValue={businessTypeWatch}
              customId="BusinessTypeForm"
              radioGroupItemClass={radioGroupStyle}
            />
          )}
        />
      </div>
    );
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      onSubmit={handleSubmit(onSubmit)}
      className={formStyle}
    >
      {renderContainerForm()}
    </form>
  );
};

const formStyle = 'flex flex-col mobile:pr-0 pr-6 pb-6 bg-white';
const radioGroupStyle = 'mobile:h-full';
