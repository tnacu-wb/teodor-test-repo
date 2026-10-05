'use client';

import { EmployeeDetails, LOCALES } from '@whitbread-eos/api';
import { Alert, AlertTitle } from '@whitbread-eos/atoms/ui';
import { useTranslation } from '@whitbread-eos/utils';
import { CircleCheck } from 'lucide-react';
import { useRouter } from 'next/navigation';
import { useOptimistic, useTransition, useRef } from 'react';

import { ManagePendingEmployeeItem } from './manage-pending-employee-item';

interface PendingEmployeesProps {
  employees: EmployeeDetails[];
  icons: Record<string, string>;
  locale?: LOCALES;
  token: string;
  baseDataTestId: string;
  accessLevel?: string;
}

export default function ManagePendingEmployeesClient({
  employees,
  locale,
  token,
  icons,
  baseDataTestId,
  accessLevel,
}: PendingEmployeesProps) {
  const router = useRouter();
  const [isPending, startTransition] = useTransition();
  const showNotification = useRef(false);
  const { t } = useTranslation('users');

  const [optimisticEmployeesList, setOptimisticEmployeesList] = useOptimistic(
    employees,
    (state, idToRemove) => state.filter((employee) => employee.employeeId !== idToRemove)
  );

  const handleAction = (employeeId: string) => {
    setOptimisticEmployeesList(employeeId);

    if (optimisticEmployeesList.length === 1) {
      showNotification.current = true;
    }

    startTransition(() => {
      router.refresh();
    });
  };

  return (
    (optimisticEmployeesList?.length || showNotification.current) && (
      <div data-testid={`${baseDataTestId}-container`} className={containerStyle}>
        <h2 data-testid={`${baseDataTestId}-title`} className={titleStyle}>
          {`${t('userMgmt.manageEmployees.innBusiness.pendingRequestsTitle')} (${
            optimisticEmployeesList.length
          })`}
        </h2>
        {showNotification.current && (
          <Alert variant="success" data-testid={`${baseDataTestId}-noPendingRequests`}>
            <CircleCheck className="w-4 h-4" />
            <AlertTitle className="text-sm m-0">
              {t('userMgmt.manageEmployees.innBusiness.requests.success')}
            </AlertTitle>
          </Alert>
        )}
        <ul className={listStyle} data-testid={`${baseDataTestId}-list`}>
          {optimisticEmployeesList.map((employee, index) => (
            <ManagePendingEmployeeItem
              key={employee.employeeId}
              employee={employee}
              icons={icons}
              locale={locale}
              token={token}
              dataTestId={`${baseDataTestId}-item-${index}`}
              onAction={() => handleAction(employee?.employeeId ?? '')}
              isPending={isPending}
              accessLevel={accessLevel}
            />
          ))}
        </ul>
      </div>
    )
  );
}
const titleStyle = 'text-xl font-bold';
const listStyle = 'w-full border border-lightgrey3 divide-y border-lightgrey3';
const containerStyle = 'flex flex-col gap-6';
