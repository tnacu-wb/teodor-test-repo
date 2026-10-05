'use client';

import { EmployeeStatus } from '@whitbread-eos/api';
import { AnalyticsData, AnalyticsDataInnBusiness, AccessLevel } from '@whitbread-eos/api/src';
import { analytics } from '@whitbread-eos/utils';
import { MANAGE_TABS } from '@whitbread-eos/utils/server';
import React, { useEffect, useImperativeHandle, forwardRef } from 'react';

export const PageNames = {
  MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_CREATION: 'Manage Employees: Successful Account Creation',
  MANAGE_EMPLOYEES_SUCCESSFUL_ACCOUNT_DELETION: 'Manage Employees: Successful Account Deletion',
};

export const roleToAnalyticsCaption: Record<AccessLevel, string> = {
  [AccessLevel.Super]: 'Travel manager',
  [AccessLevel.Booker]: 'Booker',
  [AccessLevel.Self]: 'Self-booker',
  [AccessLevel.Stayer]: 'Guest',
  [AccessLevel.BusinessPayManager]: 'Business Pay Manager',
  [AccessLevel.BusinessPayUser]: 'Business Pay User',
};

export const mapRowDataToAnalyticsData = (employees: TableRowData[]): AnalyticsDataInnBusiness => {
  const analyticsCount = {
    activeUsers: 0,
    resendActivation: 0,
    deactivated: 0,
    userRole: {
      travelManagers: 0,
      booker: 0,
      selfBooker: 0,
      guest: 0,
    },
  };

  employees.forEach((employee: any) => {
    switch (employee.employeeStatus) {
      case EmployeeStatus.Active:
        analyticsCount.activeUsers++;
        break;
      case EmployeeStatus.Inactive:
        analyticsCount.resendActivation++;
        break;
      case EmployeeStatus.Deactivated:
        analyticsCount.deactivated++;
        break;
      default:
        break;
    }

    switch (employee.accessLevel) {
      case AccessLevel.Super:
        analyticsCount.userRole.travelManagers++;
        break;
      case AccessLevel.Booker:
        analyticsCount.userRole.booker++;
        break;
      case AccessLevel.Self:
        analyticsCount.userRole.selfBooker++;
        break;
      case AccessLevel.Stayer:
        analyticsCount.userRole.guest++;
        break;
      default:
        break;
    }
  });

  const innBussinessEmployeesAnalyticsData: AnalyticsDataInnBusiness = {
    activeUsers: analyticsCount.activeUsers,
    resendActivation: analyticsCount.resendActivation,
    deactivated: analyticsCount.deactivated,
    userRole: { ...analyticsCount.userRole },
  };

  return innBussinessEmployeesAnalyticsData;
};

export type EmployeeAnalyticsProps = {
  pageName?: string | null;
  rowsData?: any;
  employeeStatusAnalyticsDataExtraRows?: any;
  extraRowsRequestedCount?: number;
  availableTabs?: string[];
  innBusinessPayAccounts?: number;
  searchParams?: string;
};

export type TableRowData = {
  employeeStatus: EmployeeStatus;
  accessLevel: AccessLevel;
};

export type EmployeeAnalyticsRef = {
  setAddEmployeeAnalyticsData: (data: any) => void;
  setPageName: (pageName: string) => void;
  setValidation: (message: string | null) => void;
};

export const Analytics = forwardRef(function Analytics(
  {
    pageName,
    rowsData,
    employeeStatusAnalyticsDataExtraRows,
    availableTabs,
    innBusinessPayAccounts,
  }: EmployeeAnalyticsProps,
  ref
) {
  const removeTempAddEmployeeAnalyticsFields = () => {
    delete window?.analyticsData?.innBusiness?.addEmployee;
    delete window?.analyticsData?.innBusiness?.employee;
    delete window?.analyticsData?.innBusiness?.employeeRole;
    delete window?.analyticsData?.validation;
  };

  const parseTableRowsAndUpdateAnalytics = () => {
    const rowsData = getEmployeeTableRowsData();
    const activeSearchReturned = checkActiveSearchReturned(rowsData);
    const employeeStatusAnalytics: AnalyticsDataInnBusiness = mapRowDataToAnalyticsData(rowsData);

    updateAnalytics(employeeStatusAnalytics, activeSearchReturned);
  };

  const checkActiveSearchReturned = (rows: object[]): boolean => {
    const userSearchInUrl = new URL(window?.location.href).searchParams.get('userSearch');
    return !!userSearchInUrl && rows?.length > 0;
  };

  const getEmployeeTableRowsData = (): TableRowData[] => {
    const dataTableEl = getDataTableRootEl();
    const rowsData: TableRowData[] = [];
    const rowEls = dataTableEl?.querySelectorAll('[data-rowdata]');
    rowEls?.forEach((el) => {
      try {
        const data = el.getAttribute('data-rowdata');
        if (data) {
          const parsed = JSON.parse(data);
          rowsData.push(parsed);
        }
      } catch {
        // ignore parse errors
      }
    });
    return rowsData;
  };

  const getDataTableRootEl = () => {
    const dataTableEl = document?.querySelectorAll('[data-testid="InnBusiness-DataTable"]');
    if (dataTableEl?.length > 0) {
      return dataTableEl[0];
    }
    return null;
  };

  const updateAnalytics = (
    employeeStatusAnalytics: AnalyticsDataInnBusiness,
    activeSearchReturned = false
  ) => {
    const currentData = window?.analyticsData ?? {};
    const innBusiness = window?.analyticsData?.innBusiness ?? {};

    const innBussinessEmployeesAnalyticsData = {
      innBusiness: {
        ...innBusiness,
        ...employeeStatusAnalytics,
        activeSearchReturned: activeSearchReturned,
      },
    };

    analytics.update({
      ...currentData,
      ...innBussinessEmployeesAnalyticsData,
    });

    removeTempAddEmployeeAnalyticsFields();
  };

  useEffect(() => {
    parseTableRowsAndUpdateAnalytics();
  }, [rowsData, employeeStatusAnalyticsDataExtraRows]);

  useEffect(() => {
    if (pageName === undefined || pageName === null) {
      return;
    }

    const currentData = window?.analyticsData ?? {};
    analytics.update({
      ...currentData,
      pageName: pageName,
    });
  }, [pageName]);

  useEffect(() => {
    if (availableTabs === undefined) {
      return;
    }

    const manageEmployees = (window?.analyticsData as AnalyticsData).manageEmployees ?? {};
    const innBussinessAvailableTabs = {
      innBusiness: availableTabs.includes(MANAGE_TABS.INN_BUSINESS),
      innBusinessPay: availableTabs.includes(MANAGE_TABS.INN_BUSINESS_PAY),
    };

    analytics.update({
      manageEmployees: {
        ...manageEmployees,
        ...innBussinessAvailableTabs,
      },
    } as AnalyticsData);
  }, [availableTabs]);

  useEffect(() => {
    if (innBusinessPayAccounts === undefined) {
      return;
    }

    const innBusinessPay = (window?.analyticsData as AnalyticsData).innBusinessPay ?? {};

    analytics.update({
      innBusinessPay: {
        ...innBusinessPay,
        accounts: innBusinessPayAccounts,
      },
    } as AnalyticsData);
  }, [innBusinessPayAccounts]);

  useImperativeHandle(
    ref,
    () => ({
      setAddEmployeeAnalyticsData(data: AnalyticsDataInnBusiness) {
        const innBusiness = window?.analyticsData?.innBusiness ?? {};
        analytics.update({
          innBusiness: {
            ...innBusiness,
            ...data,
          },
        } as AnalyticsData);
      },
      setPageName(pageName: string) {
        if (pageName === undefined || pageName === null) {
          return;
        }

        const currentData = window?.analyticsData ?? {};
        analytics.update({
          ...currentData,
          pageName: pageName,
        });
      },
      setValidation(message: string | null) {
        const currentData = window?.analyticsData ?? {};

        if (message === null) {
          analytics.remove(['validation']);
        } else {
          analytics.update({
            ...currentData,
            validation: message,
          });
        }
      },
    }),
    []
  );

  return <></>;
});
