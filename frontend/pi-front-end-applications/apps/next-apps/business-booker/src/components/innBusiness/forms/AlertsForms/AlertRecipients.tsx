'use client';

import { FormPeoplePicker } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl, useTranslation } from '@whitbread-eos/utils';
import Image from 'next/image';
import { useState, useEffect } from 'react';
import { Controller, useFormContext } from 'react-hook-form';

type Employee = {
  id: string;
  name: string;
  email: string;
};

type EmployeeOption = {
  value: string;
  label: string;
  employeeData?: {
    emailAddress?: string;
    [key: string]: string | undefined;
  };
};

type Props = {
  icons: Record<string, string>;
  companyId: string;
};

export const AlertRecipients = ({ icons, companyId }: Props) => {
  const { t } = useTranslation(['company', 'global']);
  const maxRecipients = 10;

  const {
    control,
    setValue,
    trigger,
    setError,
    formState: { errors },
    watch,
  } = useFormContext();

  const selectedRecipients = watch('recipients') || [];
  const [isSearchDisabled, setIsSearchDisabled] = useState(false);

  useEffect(() => {
    const maxReached = selectedRecipients.length >= maxRecipients;
    setIsSearchDisabled(maxReached);

    if (maxReached) {
      setValue('employee', '');
    }
  }, [selectedRecipients, maxRecipients, setValue]);

  const handleEmployeeChange = (selectedEmployee: EmployeeOption | null) => {
    if (!selectedEmployee) return;

    const emailMatch = selectedEmployee.label.match(/\(([^)]+)\)/);
    const email = emailMatch?.[1] || '';

    if (selectedRecipients.some((r: Employee) => r.email.toLowerCase() === email.toLowerCase())) {
      setValue('employee', '');
      return;
    }

    if (selectedRecipients.length >= maxRecipients) {
      setIsSearchDisabled(true);
      setValue('employee', '');
      return;
    }

    const newRecipient = {
      id: selectedEmployee.value,
      name: selectedEmployee.label.split(' (')[0],
      email,
    };

    const newRecipients = [...selectedRecipients, newRecipient];
    setValue('recipients', newRecipients, { shouldValidate: true, shouldDirty: true });
    setValue('employee', '');
  };

  const handleRemoveRecipient = (recipientId: string) => {
    const newRecipients = selectedRecipients.filter((r: Employee) => r.id !== recipientId);
    setValue('recipients', newRecipients, { shouldValidate: true, shouldDirty: true });
  };

  return (
    <div data-testid="AlertRecipients-container" className={styles.container}>
      <Controller
        name="employee"
        control={control}
        render={({ field }: { field: { value: string; onChange: (value: string) => void } }) => (
          <FormPeoplePicker
            {...field}
            id="AlertRecipients-people-picker"
            companyId={companyId}
            placeholder={t('company.coMngt.alerts.recipients.placeholder') as string}
            errors={errors}
            errorIcon={formatIBAssetsUrl(icons?.['icon.notification.error'])}
            setError={setError}
            onBlur={() => {
              trigger('employee');
            }}
            handleEmployeeChange={handleEmployeeChange}
            disabled={isSearchDisabled}
            className={styles.picker}
          />
        )}
      />

      {selectedRecipients.length > 0 && (
        <div data-testid="AlertRecipients-selected-list" className={styles.selectedList}>
          {selectedRecipients.map((recipient: Employee) => (
            <div
              key={recipient.id}
              data-testid={`AlertRecipients-badge-${recipient.id}`}
              className={styles.recipientBadge}
            >
              <span className={styles.recipientText}>{recipient.email}</span>
              <button
                type="button"
                onClick={() => handleRemoveRecipient(recipient.id)}
                aria-label={`Remove ${recipient.email}`}
                data-testid={`AlertRecipients-remove-${recipient.id}`}
                className={styles.removeButton}
              >
                <Image
                  src={formatIBAssetsUrl(icons?.['icon.notification.dismiss'])}
                  alt="Remove"
                  width={16}
                  height={16}
                />
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

const styles = {
  container:
    'w-full max-w-[620px] mobile:w-full flex flex-col justify-start items-start gap-6 mobile:gap-4',
  selectedList: 'self-stretch flex flex-wrap gap-2 w-full',
  recipientBadge: 'bg-lightGrey5 rounded-full inline-flex items-center h-8',
  recipientText: 'text-darkGrey2 text-base pl-3 pr-0 mobile:max-w-[calc(100%-24px)]',
  removeButton: 'p-0 border-none bg-transparent cursor-pointer h-full px-2 flex items-center',
  picker: 'self-stretch w-full',
};
