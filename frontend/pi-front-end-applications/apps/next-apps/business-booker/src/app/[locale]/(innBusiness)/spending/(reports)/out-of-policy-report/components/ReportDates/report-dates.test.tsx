import '@testing-library/jest-dom';
import { render, fireEvent, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getOutOfPolicyReport } from '@whitbread-eos/utils/server';

import ReportDates from './report-dates';

jest.mock('@whitbread-eos/atoms/ui', () => {
  const actual = jest.requireActual('@whitbread-eos/atoms/ui');

  return {
    ...actual,
    SingleDatePickerUi: ({
      placeholder,
      onDateChange,
    }: {
      placeholder: string;
      onDateChange: (value: Date | undefined) => void;
    }) => {
      const isStartDate = placeholder === 'spending.reporting.start.date';

      return (
        <button
          type="button"
          data-testid={isStartDate ? 'mock-start-date-picker' : 'mock-end-date-picker'}
          onClick={() =>
            onDateChange(new Date(isStartDate ? '2026-06-01T12:00:00Z' : '2026-06-15T12:00:00Z'))
          }
        >
          {placeholder}
        </button>
      );
    },
  };
});

jest.mock('@whitbread-eos/utils', () => {
  return {
    ...jest.requireActual('@whitbread-eos/utils'),
  };
});

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getOutOfPolicyReport: jest.fn().mockResolvedValue({
      data: {
        outOfPolicyReport: {
          downloadUrl: 'mock-url',
          fileName: 'mock-file',
        },
      },
    }),
  };
});

const mockProps = {
  icons: {},
  token: 'mock-token',
};

const calendarLabels = {
  adultsHelperText: '',
  calendarIcon: undefined,
  childrenHelperText: '',
  cotLimit: undefined,
  datePicker: {
    checkOut: undefined,
    done: '',
    reset: '',
    months: [
      'January',
      'February',
      'March',
      'April',
      'May',
      'June',
      'July',
      'August',
      'September',
      'October',
      'November',
      'December',
    ],
    weekdaysShort: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat', 'Sun'],
  },
  guestIcon: undefined,
  hotelsLabel: undefined,
  includeCot: undefined,
  invalidLocation: undefined,
  invalidNights: undefined,
  invalidRooms: undefined,
  removeRoom: undefined,
  room: '',
  roomType: undefined,
  search: undefined,
  searchEdit: undefined,
  searchIcon: undefined,
  where: undefined,
  whereDismissIcon: undefined,
  whereIcon: undefined,
};

describe('ReportDates Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render ReportDates component', async () => {
    const { getByTestId, getByText } = render(
      <ReportDates
        baseDataTestId={'OutOfPolicyReportPage'}
        calendarLabels={calendarLabels}
        companyId="mock-company-id"
        {...mockProps}
      />
    );

    expect(getByTestId('OutOfPolicyReportPage-container')).toBeInTheDocument();
    expect(getByTestId('OutOfPolicyReportPage-Single-Date-Picker')).toBeInTheDocument();
    expect(getByText('spending.reporting.start.date')).toBeInTheDocument();
    expect(getByText('spending.reporting.end.date')).toBeInTheDocument();
    expect(getByTestId('OutOfPolicyReportPage-Generate-Report')).toBeInTheDocument();
  });

  it('should generate report if dates are selected', async () => {
    const { getByTestId } = render(
      <ReportDates
        baseDataTestId={'OutOfPolicyReportPage'}
        calendarLabels={calendarLabels}
        companyId="mock-company-id"
        {...mockProps}
      />
    );

    fireEvent.click(getByTestId('mock-start-date-picker'));
    fireEvent.click(getByTestId('mock-end-date-picker'));

    fireEvent.click(getByTestId('OutOfPolicyReportPage-Generate-Report'));

    await waitFor(() => {
      expect(getOutOfPolicyReport).toHaveBeenCalled();
    });
  });

  it('should show error if dates are not selected', async () => {
    const { getByTestId, getAllByText } = render(
      <ReportDates
        baseDataTestId={'OutOfPolicyReportPage'}
        calendarLabels={calendarLabels}
        companyId="mock-company-id"
        {...mockProps}
      />
    );

    fireEvent.click(getByTestId('OutOfPolicyReportPage-Generate-Report'));

    await waitFor(() => {
      expect(getAllByText('report.error.missing.dates')[0]).toBeInTheDocument();
    });
  });

  it('should render the RadioGroup component', async () => {
    const { getByTestId } = render(
      <ReportDates
        baseDataTestId={'OutOfPolicyReportPage'}
        calendarLabels={calendarLabels}
        companyId="mock-company-id"
        {...mockProps}
      />
    );

    expect(getByTestId('OutOfPolicyReportPage-RadioGroup')).toBeInTheDocument();
  });
});
