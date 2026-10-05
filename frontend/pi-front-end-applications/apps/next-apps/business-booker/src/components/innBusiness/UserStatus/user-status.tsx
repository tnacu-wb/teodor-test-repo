import { LOCALES, EmployeeStatus } from '@whitbread-eos/api';
import {
  cn,
  getCountryLanguageByLocale,
  getTranslations,
  getLabelType,
  getStyling,
} from '@whitbread-eos/utils/server';
import Link from 'next/link';

import ResendActivationButton from './resend-activation-button';

export type UserStatusProps = {
  type: EmployeeStatus;
  locale?: LOCALES;
  className?: string;
  userDetails: Record<string, string>;
  companyId?: string;
  token?: string;
};

export async function UserStatus({
  locale,
  type,
  className,
  userDetails,
  companyId,
  token,
}: UserStatusProps) {
  const { language } = getCountryLanguageByLocale(locale);
  const { t } = await getTranslations(language, 'users');
  const baseDataTestId = 'UserStatus';

  const renderStatusElement = () => {
    const label = t(getLabelType(type));
    if (type === EmployeeStatus.Active || type === EmployeeStatus.Deactivated) return label;
    else if (type === EmployeeStatus.Inactive) {
      return (
        <ResendActivationButton
          buttonLabel={label}
          userDetails={userDetails}
          companyId={companyId}
          language={language}
          token={token}
        />
      );
    } else {
      return (
        <Link className={linkStyle} href="#">
          {label}
        </Link>
      );
    }
  };

  return (
    <span data-testid={baseDataTestId} className={className}>
      <span className={cn(bulletStyle, getStyling(type) || 'bg-error')} />
      {renderStatusElement()}
    </span>
  );
}

const bulletStyle = 'inline-block h-3 w-3 rounded-md bg-secondaryColor mr-2';

const linkStyle = 'text-secondaryColor underline';
