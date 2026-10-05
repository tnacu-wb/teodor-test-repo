'use client';

import { EmployeeCriteria, OptionType } from '@whitbread-eos/api';
import { FormInput, FormPeoplePicker } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import { useEffect } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

type Props = {
  icons: Record<string, string>;
  companyId: string;
  mainContactInformation: EmployeeCriteria;
  handleEmployeeChange: (employee: OptionType) => void;
  selectedEmployee: OptionType | null;
};

export function CompanyMainContact({
  icons,
  companyId,
  mainContactInformation,
  handleEmployeeChange,
  selectedEmployee,
}: Props) {
  const { t } = useTranslation(['company', 'users']);

  const {
    control,
    formState: { errors },
    trigger,
    setError,
    setValue,
  } = useFormContext();

  useEffect(() => {
    if (selectedEmployee) {
      setValue('jobTitle', selectedEmployee?.employeeData?.jobTitle ?? '');
      setValue('title', selectedEmployee?.employeeData?.title ?? '');
      setValue('firstName', selectedEmployee?.employeeData?.firstName ?? '');
      setValue('lastName', selectedEmployee?.employeeData?.lastName ?? '');
      setValue('emailAddress', selectedEmployee?.employeeData?.emailAddress ?? '');
      setValue('phoneNumber', selectedEmployee?.employeeData?.phoneNumber ?? '');
      setValue('mobileNumber', selectedEmployee?.employeeData?.mobileNumber ?? '');
    }
  }, [selectedEmployee]);

  return (
    <div data-testid="CompanyMainContact-company-details-container">
      <Controller
        name="employeeId"
        control={control}
        render={({ field }) => (
          <FormPeoplePicker
            {...field}
            id="PeoplePicker"
            companyId={companyId}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            placeholder={t('users.userMgmt.manageEmployees.seach.placeholder') + ' *'}
            setError={setError}
            onBlur={() => {
              trigger('employeeId');
            }}
            handleEmployeeChange={handleEmployeeChange}
            requireAdditionalDetails={true}
            populateInitialEmployee={true}
          />
        )}
      />
      <div className={`${subDivStyle} py-4`}>
        <span className={titleStyle}>{t('company.coMngt.mainContact.email.label')}</span>
        <span>
          {selectedEmployee?.employeeData?.emailAddress ?? mainContactInformation.emailAddress}
        </span>
      </div>
      <div className={`${subDivStyle} pb-4`}>
        <span className={titleStyle}>{t('company.coMngt.mainContact.contactNumber.label')}</span>
        <span>
          {(selectedEmployee?.employeeData?.phoneNumber ||
            selectedEmployee?.employeeData?.mobileNumber) ??
            (mainContactInformation.mobileNumber || mainContactInformation.phoneNumber)}
        </span>
      </div>
      <Controller
        name="jobTitle"
        control={control}
        render={({ field }) => (
          <FormInput
            {...field}
            id="CompanyMainContact-job-title"
            placeholder={t('company.coMngt.mainContact.position')}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
          />
        )}
      />
    </div>
  );
}

const titleStyle = 'font-bold';
const subDivStyle = 'flex flex-col';
