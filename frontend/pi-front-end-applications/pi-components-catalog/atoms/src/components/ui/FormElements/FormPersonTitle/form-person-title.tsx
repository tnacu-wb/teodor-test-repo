import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import React from 'react';

import { FormSelect, IBFormSelectProps } from '../FormSelect';

const parseTitles = (titles: string) => {
  return titles
    .replace(/'/g, '')
    .split(',')
    .map((title) => ({
      value: title,
      displayValue: title,
    }));
};

interface TitleProps extends Omit<IBFormSelectProps, 'disabled'> {
  icons: Record<string, string>;
  disabled?: boolean;
  titleOptions?: string;
}

export const FormPersonTitle = React.forwardRef<HTMLButtonElement, TitleProps>(
  ({ disabled = false, icons, titleOptions, ...props }, ref) => {
    const { t } = useTranslation('users');

    return (
      <FormSelect
        {...props}
        ref={ref}
        disabled={disabled}
        errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
        arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
        className="w-[8.7rem]"
        options={parseTitles(titleOptions ?? t('userMgmt.employee.add.form.nameTitles'))}
      />
    );
  }
);

FormPersonTitle.displayName = 'FormPersonTitle';
