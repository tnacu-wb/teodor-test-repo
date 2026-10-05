'use client';

import { LOCALES, EmployeeCriteria } from '@whitbread-eos/api';

type Props = {
  userInformation: EmployeeCriteria;
  displayCompanyName?: boolean;
  companyName?: string;
  locale?: LOCALES;
  mobile?: boolean;
};

export function UserDetails({ userInformation, displayCompanyName = false, companyName }: Props) {
  return (
    <div data-testid="UserDetails-container" className={divStyle}>
      {displayCompanyName && <span data-testid="UserDetails-companyName">{companyName}</span>}
      <span data-testid="UserDetails-name">
        {`${userInformation?.title} ${userInformation?.firstName} ${userInformation?.lastName}`}
      </span>
      {userInformation?.emailAddress && (
        <span data-testid="UserDetails-emailAddress">{userInformation?.emailAddress}</span>
      )}
      {userInformation?.phoneNumber ||
        (userInformation?.mobileNumber && (
          <span data-testid="UserDetails-phoneNumber">
            {userInformation?.phoneNumber || userInformation?.mobileNumber}
          </span>
        ))}
      {userInformation?.position && (
        <span data-testid="UserDetails-jobTitle">{userInformation?.position}</span>
      )}
    </div>
  );
}

const divStyle =
  'flex flex-col items-start text-base text-darkGrey1 font-normal mobile:overflow-auto pt-2';
