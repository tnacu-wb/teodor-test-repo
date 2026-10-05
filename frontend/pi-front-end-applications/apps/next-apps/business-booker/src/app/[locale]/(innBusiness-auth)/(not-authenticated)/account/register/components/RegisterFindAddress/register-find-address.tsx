'use client';

import { FormInput, Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { Controller, useFormContext } from 'react-hook-form';

type Props = {
  onSubmit?: (data: any) => void;
  icons: Record<string, string>;
};

export function RegisterFindAddress({ onSubmit, icons }: Props) {
  const { t } = useTranslation('users');
  const {
    control,
    formState: { errors },
    trigger,
  } = useFormContext();

  return (
    <div data-testid={'RegisterForm-findAddress'} className={containerStyle}>
      <Controller
        name="postCode"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="postCode"
            type={'text'}
            aria-required
            placeholder={t('userMgmt.employee.add.companyAddress.postcode')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            upperCase={true}
            onBlur={() => {
              trigger('postCode');
            }}
          />
        )}
      />
      <Button
        variant="findAddressButton"
        size="findAddressButton"
        className={buttonStyle}
        type="button"
        onClick={onSubmit}
        data-testid={'RegisterForm-findAddressButton'}
      >
        <span> {t('userMgmt.employee.button.findpostcode')}</span>
      </Button>
    </div>
  );
}

const buttonStyle = 'border border-secondaryColor mobile:px-4';
const containerStyle = 'grid gap-4 grid-cols-2 gap-4';
