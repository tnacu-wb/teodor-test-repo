import { render, act } from '@testing-library/react';
import { AccessLevel, EmployeeStatus } from '@whitbread-eos/api/src';
import { MANAGE_TABS } from '@whitbread-eos/utils/server';
import React, { createRef } from 'react';

import { Analytics, mapRowDataToAnalyticsData, TableRowData } from './EmployeeAnalytics';

const mockTableDataRows = [
  {
    accessLevel: AccessLevel.Super,
    employeeStatus: EmployeeStatus.Active,
  },
  {
    accessLevel: AccessLevel.Booker,
    employeeStatus: EmployeeStatus.Inactive,
  },
  {
    accessLevel: AccessLevel.Self,
    employeeStatus: EmployeeStatus.Deactivated,
  },
  {
    accessLevel: AccessLevel.Stayer,
    employeeStatus: EmployeeStatus.Active,
  },
  {
    accessLevel: AccessLevel.Super,
    employeeStatus: EmployeeStatus.Inactive,
  },
];

beforeEach(() => {
  window.analyticsData = {};
});

describe('mapRowDataToAnalyticsData', () => {
  it('should map row data to analytics data correctly', () => {
    const rows = [...mockTableDataRows];
    const result = mapRowDataToAnalyticsData(rows);
    expect(result).toHaveProperty('userRole');
    expect(result).toHaveProperty('activeUsers');

    expect(result.userRole).toEqual({
      travelManagers: 2,
      booker: 1,
      selfBooker: 1,
      guest: 1,
    });

    expect(result.activeUsers).toBe(2);
    expect(result.resendActivation).toBe(2);
    expect(result.deactivated).toBe(1);
  });

  it('should handle missing or invalid row data gracefully', () => {
    const rows: Array<TableRowData> = [];
    const result = mapRowDataToAnalyticsData(rows);
    expect(result).toEqual(expect.any(Object));
  });
});

describe('Analytics component', () => {
  const defaultProps = {
    pageName: 'Test Page',
    rowsData: [...mockTableDataRows],
    employeeStatusAnalyticsDataExtraRows: [],
    availableTabs: [MANAGE_TABS.INN_BUSINESS],
    innBusinessPayAccounts: 0,
  };

  it('renders without crashing', () => {
    render(<Analytics {...defaultProps} />);
  });

  it('exposes imperative methods via ref', () => {
    const ref = createRef<any>();
    render(<Analytics {...defaultProps} ref={ref} />);
    expect(ref.current).toBeDefined();
    expect(typeof ref.current.setAddEmployeeAnalyticsData).toBe('function');
    expect(typeof ref.current.setPageName).toBe('function');
    expect(typeof ref.current.setValidation).toBe('function');
  });

  it('setAddEmployeeAnalyticsData updates analyticsData', () => {
    const ref = createRef<any>();
    render(<Analytics {...defaultProps} ref={ref} />);
    act(() => {
      ref.current.setAddEmployeeAnalyticsData({
        addEmployee: true,
        employeeRole: 'Self-booker',
        employee: 'individual',
      });
    });
    expect(window.analyticsData.innBusiness).toMatchObject({
      addEmployee: true,
      employeeRole: 'Self-booker',
      employee: 'individual',
    });
  });

  it('setPageName updates analyticsData', () => {
    const ref = createRef<any>();
    render(<Analytics {...defaultProps} ref={ref} />);
    act(() => {
      ref.current.setPageName('New Page Name');
    });
    expect(window.analyticsData.pageName).toBe('New Page Name');
  });

  it('setValidation updates analyticsData', () => {
    const ref = createRef<any>();
    render(<Analytics {...defaultProps} ref={ref} />);
    act(() => {
      ref.current.setValidation('Some validation');
    });
    expect(window?.analyticsData?.validation).toBe('Some validation');
  });
});
