'use client';

import { Scheme, CompanyDetailsLookup } from '@whitbread-eos/api';
import { getAuthCookie, useTranslation } from '@whitbread-eos/utils';
import { companyDetailsLookup } from '@whitbread-eos/utils/server';
import * as React from 'react';
import { useState } from 'react';
import { UseFormSetError } from 'react-hook-form';

import { Button } from '../../Button';
import { FormInput } from '../FormInput';

type Props = {
  id: string;
  name: string;
  value: string;
  errors?: Record<string, any>;
  errorIcon: string;
  placeholder: string;
  disabled?: boolean;
  setError: UseFormSetError<any>;
  onLookupSuccess?: (data: CompanyDetailsLookup) => void;
  onChange?: React.ChangeEventHandler<HTMLInputElement>;
  onBlur?: () => void;
  onFocus?: () => void;
  className?: string;
  scheme: Scheme;
};

const FormCompanyRegistrationNumber = React.forwardRef<HTMLInputElement, Props>(
  (
    {
      id,
      value,
      setError,
      onLookupSuccess = () => {
        return;
      },
      onChange = () => {
        return;
      },
      onBlur = () => {
        return;
      },
      onFocus = () => {
        return;
      },
      errors,
      errorIcon,
      name,
      placeholder,
      disabled,
      className,
      scheme,
    }: Props,
    ref
  ) => {
    const { t } = useTranslation(['payApplication']);
    const token = getAuthCookie();
    const [requestPending, setRequestPending] = useState(false);

    const handleButtonClick = async () => {
      setRequestPending(true);

      const result: { data: CompanyDetailsLookup[] } = await companyDetailsLookup(
        token,
        value,
        scheme
      );

      setRequestPending(false);

      if (result && result?.data && result.data.length > 0) {
        onLookupSuccess(result.data[0]);
      } else {
        setError(name, {
          type: 'custom',
          message: t('payApplication.companyDetails.registeredNumber.failure.notification'),
        });
      }
    };

    return (
      <div className={className}>
        <div className={headingStyle}>{t('payApplication.companyDetails.registeredNumber')}</div>

        <FormInput
          className={inputStyle}
          id={id}
          name={name}
          value={value}
          placeholder={placeholder}
          errors={errors}
          errorIcon={errorIcon}
          disabled={disabled}
          onChange={onChange}
          onFocus={onFocus}
          onBlur={onBlur}
          ref={ref}
        />

        <Button
          variant="outline"
          onClick={handleButtonClick}
          disabled={disabled || requestPending}
          className={buttonStyle}
        >
          {t('payApplication.companyDetails.findCompanyDetails')}
        </Button>
      </div>
    );
  }
);

FormCompanyRegistrationNumber.displayName = 'Form Company Registration Number';

export { FormCompanyRegistrationNumber };

const headingStyle = 'font-bold text-xl';
const inputStyle = 'mt-6';
const buttonStyle = 'w-full mt-6 font-semibold text-lg';
