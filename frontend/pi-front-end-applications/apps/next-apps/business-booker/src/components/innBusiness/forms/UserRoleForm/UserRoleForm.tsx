'use client';

import { zodResolver } from '@hookform/resolvers/zod';
import { BUSINESS_BOOKER_USER_ROLES, EmployeeStatus } from '@whitbread-eos/api';
import {
  FormRadioGroup,
  FormSelect,
  InfoTooltip,
  Notification,
  SanitizedContent,
} from '@whitbread-eos/atoms/ui';
import { useOutsideClick } from '@whitbread-eos/utils';
import { useTranslation } from '@whitbread-eos/utils';
import { getLabelType, getDetailsFromToken } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { useEffect, useState, MutableRefObject } from 'react';
import { Controller, useForm } from 'react-hook-form';
import { z } from 'zod';

import ResendActivationButton from '~components/innBusiness/UserStatus/resend-activation-button';

type Props = {
  onSubmit: (data: Record<string, string>) => void;
  infoTooltipIcon: string;
  arrowIcon: string;
  errorIcon: string;
  notificationInfo: string;
  employeeStatus?: string;
  userDetails?: {
    title: string;
    firstName: string;
    lastName: string;
    emailAddress: string;
    employeeId: string;
    accessLevel: string;
  };
  isMainContact?: boolean;
  language: string;
  isEditEmployeePage?: boolean;
  formRef: MutableRefObject<HTMLFormElement | null>;
  token?: string;
  companyId?: string;
  showLastTMError: boolean;
  setShowLastTMError: (value: boolean) => void;
  onDirtyChange?: (isDirty: boolean) => void;
};

export function UserRoleForm({
  onSubmit,
  infoTooltipIcon,
  arrowIcon,
  notificationInfo,
  employeeStatus = EmployeeStatus.Inactive, //Initial status for Add employee
  userDetails,
  isMainContact = false,
  isEditEmployeePage,
  formRef,
  language,
  token,
  companyId,
  showLastTMError,
  setShowLastTMError,
  errorIcon,
  onDirtyChange,
}: Props) {
  const { t } = useTranslation('users');
  const [showMainContactError, setShowMainContactError] = useState(false);
  const {
    isOpen: isTooltipOpen,
    setIsOpen: setIsTooltipOpen,
    elementRef: tooltipRef,
    iconRef: tooltipIconRef,
  } = useOutsideClick();
  const { isBusinessPayManager } = getDetailsFromToken(token);
  const bbRoles = [
    {
      value: BUSINESS_BOOKER_USER_ROLES.SUPER,
      label: t('userMgmt.employee.add.settings.role.travelManager'),
    },
    {
      value: BUSINESS_BOOKER_USER_ROLES.BOOKER,
      label: t('userMgmt.manageEmployees.accountRole.Booker'),
    },
    {
      value: BUSINESS_BOOKER_USER_ROLES.SELF,
      label: t('userMgmt.manageEmployees.accountRole.SelfBooker'),
    },
    {
      value: BUSINESS_BOOKER_USER_ROLES.STAYER,
      label: t('userMgmt.manageEmployees.accountRole.Guest'),
    },
  ];

  const bpRoles = [
    {
      value: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_MANAGER,
      label: t('userMgmt.manageEmployees.accountRole.bpManager'),
    },
    {
      value: BUSINESS_BOOKER_USER_ROLES.BUSINESS_PAY_USER,
      label: t('userMgmt.manageEmployees.accountRole.bpUser'),
    },
  ];
  const items = isBusinessPayManager ? bpRoles : bbRoles;

  const schema = z.object({
    userStatus: z.object({
      displayValue: z.string(),
      value: z.string(),
    }),
    userRole: z.string(),
  });
  const statusLabel = getLabelType(employeeStatus, true);

  const {
    control,
    handleSubmit,
    setError,
    formState: { errors },
    clearErrors,
    watch,
  } = useForm<z.infer<typeof schema>>({
    resolver: zodResolver(schema),
    defaultValues: {
      userStatus: { value: employeeStatus, displayValue: t(statusLabel) },
      userRole: userDetails?.accessLevel ?? BUSINESS_BOOKER_USER_ROLES.SUPER,
    },
  });

  useEffect(() => {
    const subscription = watch(() => {
      onDirtyChange?.(true);
    });
    return () => subscription.unsubscribe();
  }, [watch, onDirtyChange]);

  useEffect(() => {
    if (showLastTMError) {
      setError('userRole', {
        type: 'manual',
        message: t('userMgmt.employee.edit.error.lastTravelMgr'),
      });
      formRef?.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
    }
  }, [showLastTMError]);

  const handleStatusChange = (value: { value: string; displayValue: string }, field: any) => {
    if (
      isMainContact &&
      (value.value === EmployeeStatus.Deactivated || value.value === EmployeeStatus.Purged)
    ) {
      setError('userStatus.displayValue', {
        type: 'manual',
        message: t('userMgmt.employee.edit.error.isMainContact'),
      });
      return;
    }
    clearErrors('userStatus');
    field.onChange(value);
  };

  const handleOptionChange = (value: string, field: any) => {
    field.onChange(value);
    if (showLastTMError) {
      clearErrors('userRole');
      setShowLastTMError(false);
    }
  };

  const handleFormSubmit = async (data: z.infer<typeof schema>) => {
    const { userStatus, userRole } = data;

    // Check if employee is main contact when trying to deactivate or purge
    if (
      isEditEmployeePage &&
      isMainContact &&
      (userStatus.value === EmployeeStatus.Deactivated ||
        userStatus.value === EmployeeStatus.Purged)
    ) {
      setShowMainContactError(true);
      formRef?.current?.scrollIntoView({ behavior: 'smooth', block: 'center' });
      return;
    }

    const roleObject = {
      employeeStatus: userStatus.value,
      accessLevel: userRole,
    };
    onSubmit(roleObject);
  };

  const getStatusOptions = (employeeStatus: string) => {
    switch (employeeStatus) {
      case EmployeeStatus.Active:
        return [
          {
            displayValue: t('userMgmt.account.status.active'),
            value: EmployeeStatus.Active,
          },
          {
            displayValue: t('userMgmt.account.status.deactivated'),
            value: EmployeeStatus.Deactivated,
          },
        ];
      case EmployeeStatus.Inactive:
        return [
          {
            displayValue: t('userMgmt.account.status.inactive'),
            value: EmployeeStatus.Inactive,
          },
          {
            displayValue: t('userMgmt.account.status.deactivated'),
            value: EmployeeStatus.Deactivated,
          },
        ];
      case EmployeeStatus.Deactivated:
        return [
          {
            displayValue: t('userMgmt.account.status.deactivated'),
            value: EmployeeStatus.Deactivated,
          },
          {
            displayValue: t('userMgmt.account.status.delete'),
            value: EmployeeStatus.Purged,
          },
        ];
      default:
        return [];
    }
  };

  return (
    <form
      ref={formRef as MutableRefObject<HTMLFormElement>}
      className={formStyle}
      onSubmit={handleSubmit(handleFormSubmit)}
      data-testid="user-role-form"
    >
      <h4 data-testid="User-Role-Heading" className={headingStyle}>
        {t('userMgmt.employee.add.settings.heading')}
      </h4>

      {isEditEmployeePage && (
        <>
          <div className={accountRoleStyle}>
            <h3 data-testid="User-Status-Sub-Heading" className={subHeadingStyle}>
              {t('userMgmt.manageEmployees.accountStatus')}
            </h3>

            {showMainContactError && (
              <Notification
                type="error"
                icon={errorIcon}
                data-testid="notification-error"
                message={t('userMgmt.employee.edit.error.isMainContact')}
                className="mt-4"
              />
            )}

            {employeeStatus === EmployeeStatus.Inactive && (
              <Notification
                type="info"
                icon={notificationInfo}
                title={t('userMgmt.account.pending.activation')}
                message={
                  <>
                    <SanitizedContent>{`${t(
                      'userMgmt.account.activation.message'
                    )}`}</SanitizedContent>
                    <ResendActivationButton
                      buttonLabel={t('userMgmt.manageEmployees.activation.email')}
                      userDetails={{
                        title: userDetails?.title ?? '',
                        firstName: userDetails?.firstName ?? '',
                        lastName: userDetails?.lastName ?? '',
                        email: userDetails?.emailAddress ?? '',
                        employeeId: userDetails?.employeeId ?? '',
                      }}
                      language={language}
                      token={token}
                      companyId={companyId}
                    />
                  </>
                }
                className={'mt-4'}
              />
            )}

            <InfoTooltip
              content={
                <SanitizedContent>
                  {t('userMgmt.manageEmployee.accountStatus.toolTip')}
                </SanitizedContent>
              }
              className={infoTooltipStyle}
              testId="User-Status-Info-Tooltip"
              hoverVariant={true}
            >
              <Image
                alt={'User Status matrix'}
                src={infoTooltipIcon}
                className={infoTooltipIconStyle}
                width={26}
                height={26}
                priority={true}
                data-testid="User-Status-Info-Tooltip-Icon"
                onClick={() => setIsTooltipOpen(!isTooltipOpen)}
                ref={tooltipIconRef}
              />
            </InfoTooltip>
          </div>

          <InfoTooltip
            ref={tooltipRef}
            className={infoTooltipStyle}
            content={
              <SanitizedContent>
                {t('userMgmt.manageEmployee.accountStatus.toolTip')}
              </SanitizedContent>
            }
            testId="User-Status-Info-Tooltip"
            open={isTooltipOpen}
            arrowClassName="right-[1px]"
            mobile
          />

          <Controller
            name="userStatus"
            control={control}
            render={({ field }) => (
              <FormSelect
                {...field}
                id="userStatus"
                data-testid="userStatus-select"
                arrowIcon={arrowIcon}
                className={'mt-4'}
                options={getStatusOptions(employeeStatus ?? '')}
                errors={errors}
                errorIcon={errorIcon}
                errorTestId="userStatus-form-error"
                onChange={(value: { value: string; displayValue: string }) =>
                  handleStatusChange(value, field)
                }
              />
            )}
          />
        </>
      )}

      <div className={accountRoleStyle}>
        <h3 data-testid="User-Role-Sub-Heading" className={subHeadingStyle}>
          {t('userMgmt.employee.add.settings.role')}
          {' *'}
        </h3>

        <InfoTooltip
          content={
            <SanitizedContent>
              {t('userMgmt.manageEmployees.employeeManagement.tooltipMsg')}
            </SanitizedContent>
          }
          className={infoTooltipStyle}
          testId="User-Role-Info-Tooltip"
          hoverVariant={true}
        >
          <Image
            alt={'User role matrix'}
            src={infoTooltipIcon}
            className={infoTooltipIconStyle}
            width={26}
            height={26}
            priority={true}
            data-testid="User-Role-Info-Tooltip-Icon"
            onClick={() => setIsTooltipOpen(!isTooltipOpen)}
            ref={tooltipIconRef}
          />
        </InfoTooltip>
      </div>

      <InfoTooltip
        ref={tooltipRef}
        className={infoTooltipStyle}
        content={
          <SanitizedContent>
            {t('userMgmt.manageEmployees.employeeManagement.tooltipMsg')}
          </SanitizedContent>
        }
        testId="User-Role-Info-Tooltip"
        open={isTooltipOpen}
        arrowClassName="right-[1px]"
        mobile
      />

      <Controller
        name="userRole"
        control={control}
        render={({ field }) => (
          <FormRadioGroup
            {...field}
            data-testid="User-Roles-Container-List"
            errorIcon={errorIcon}
            errors={errors}
            items={items}
            onChange={(value: string) => handleOptionChange(value, field)}
          />
        )}
      />
    </form>
  );
}

const formStyle = 'mt-12';
const headingStyle = 'font-bold text-xl';
const subHeadingStyle = 'font-bold text-lg mt-8';
const accountRoleStyle = 'relative';
const infoTooltipIconStyle = 'absolute right-0 top-0';
const infoTooltipStyle = 'w-80 mobile:w-full rounded shadow-variantTooltip';
