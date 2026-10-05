'use client';

import {
  AccessLevel,
  ApproveRejectRequest,
  EmployeeDetails,
  IBFormSelectOption,
  LOCALES,
} from '@whitbread-eos/api';
import { FormSelect, Button } from '@whitbread-eos/atoms/ui';
import {
  formatIBAssetsUrl,
  useTranslation,
  getCountryLanguageByLocale,
} from '@whitbread-eos/utils';
import { approveRejectEmployee } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import React, { useCallback, useState } from 'react';

interface Props {
  employee: EmployeeDetails;
  icons: Record<string, string>;
  locale?: LOCALES;
  token: string;
  dataTestId: string;
  onAction?: () => void;
  isPending?: boolean;
  accessLevel?: string;
}

export function ManagePendingEmployeeItem({
  employee,
  icons,
  locale,
  token,
  dataTestId,
  onAction,
  isPending = false,
  accessLevel,
}: Props) {
  const { language } = getCountryLanguageByLocale(locale || LOCALES.EN);
  const { t } = useTranslation('users');
  const isBusinessPayManager = accessLevel === AccessLevel.BusinessPayManager;

  const router = useRouter();

  let selectOptions: IBFormSelectOption[];
  if (isBusinessPayManager) {
    selectOptions = [
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.bpManager'),
        value: AccessLevel.BusinessPayManager,
      },
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.bpUser'),
        value: AccessLevel.BusinessPayUser,
      },
    ];
  } else {
    selectOptions = [
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.travelManager'),
        value: AccessLevel.Super,
      },
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.booker'),
        value: AccessLevel.Booker,
      },
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.selfBooker'),
        value: AccessLevel.Self,
      },
      {
        displayValue: t('userMgmt.manageEmployees.innBusiness.role.guest'),
        value: AccessLevel.Stayer,
      },
    ];
  }

  const [selectedAccessLevel, setSelectedAccessLevel] = useState<IBFormSelectOption>(() => {
    const defaultSelectedAccessLevel = isBusinessPayManager
      ? AccessLevel.BusinessPayUser
      : AccessLevel.Self;
    return (
      selectOptions.find((option) => option.value === defaultSelectedAccessLevel) ||
      selectOptions[0]
    );
  });

  const handleSubmit = useCallback(
    async (approved: boolean) => {
      await approveRejectEmployee(
        {
          accessLevel: selectedAccessLevel.value,
          approved,
          email: employee.emailAddress,
          language,
        } as ApproveRejectRequest,
        token
      );

      onAction?.();

      router.refresh();
    },
    [token, selectedAccessLevel, employee.emailAddress, language, router, onAction]
  );

  return (
    <li data-testid={`${dataTestId}-listItem`} className={listItemStyle}>
      <span data-testid={`${dataTestId}-email`} className={emailAddressStyle}>
        {employee.emailAddress}
      </span>
      <div className={selectCellStyle} data-testid={`${dataTestId}-select`}>
        <FormSelect
          id={`${dataTestId}-select`}
          value={selectedAccessLevel}
          buttonClassName={selectStyle}
          onChange={(value: IBFormSelectOption) => {
            setSelectedAccessLevel(value);
          }}
          options={selectOptions}
          arrowIcon={formatIBAssetsUrl(icons?.['icon.chevron.down'])}
        />
      </div>
      <div className={buttonCellStyle}>
        <Button
          data-testid={`${dataTestId}-reject`}
          className={rejectButtonStyle}
          variant="editButton"
          onClick={() => handleSubmit(false)}
          disabled={isPending}
        >
          {t('userMgmt.manageEmployees.innBusiness.rejectRequest')}
        </Button>
        <Button
          data-testid={`${dataTestId}-approve`}
          className={approveButtonStyle}
          variant="saveUpdatesButton"
          onClick={() => handleSubmit(true)}
          disabled={isPending}
        >
          {t('userMgmt.manageEmployees.innBusiness.acceptRequest')}
        </Button>
      </div>
    </li>
  );
}

const listItemStyle = 'py-2 mobile:py-3 px-4 grid grid-cols-3 gap-4';
const emailAddressStyle = 'mobile:col-span-2 col-span-1 truncate self-center';
const selectCellStyle = 'col-span-1 mobile:col-span-1 place-content-center';
const selectStyle =
  '[&>button]:h-8 [&>button]:rounded-none [&>button]:border-lightGrey3 w-full max-w-[11.25rem] mobile:max-w-none font-normal [&>button]:py-1 [&>button]:px-2 [&>button]:pr-[2.5rem]';
const buttonCellStyle =
  'flex gap-4 justify-end mobile:justify-between mobile:col-span-full col-span-1';
const rejectButtonStyle = 'h-11 px-0';
const approveButtonStyle = 'h-11 font-semibold';
