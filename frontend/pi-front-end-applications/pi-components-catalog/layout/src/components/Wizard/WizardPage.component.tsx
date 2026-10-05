'use client';

import { FormPage } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, cn } from '@whitbread-eos/utils';
import { ReactNode } from 'react';

import { useWizardContext } from './useWizardContext';

export type Props = {
  type?: 'default' | 'form';
  children?: ReactNode;
  footer?: ReactNode;
  formTitle?: string;
  showBackButton?: boolean;
  onBackClick?: () => void;
  className?: string;
};

export function WizardPage({
  type = 'default',
  children,
  footer,
  formTitle,
  showBackButton = true,
  onBackClick,
  className,
}: Props) {
  const { icons } = useWizardContext();

  const renderBody = () => {
    if (type === 'form') {
      return (
        <FormPage
          baseDataTestId="wizard"
          title={formTitle}
          iconClassName="px-2 py-3 max-w-[3rem]"
          backIcon={showBackButton ? formatIBAssetsUrl(icons['icon.arrow.left.purple']) : undefined}
          backHref={showBackButton ? '#' : undefined}
          className={cn(formStyle, className)}
          onBackClick={onBackClick}
        >
          {children}
        </FormPage>
      );
    }

    return (
      <div className={cn(bodyStyle, className)} data-testid="wizard-page">
        {children}
      </div>
    );
  };

  return (
    <div className={containerStyle}>
      {renderBody()}
      {footer}
    </div>
  );
}

const containerStyle = 'flex flex-col grow';
const bodyStyle = 'grow bg-lightGrey5';
const formStyle = 'grow border-b-0';
