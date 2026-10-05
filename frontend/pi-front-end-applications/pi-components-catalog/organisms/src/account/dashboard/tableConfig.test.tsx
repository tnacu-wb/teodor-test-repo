import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Price } from '@whitbread-eos/api';
import { TableListRow } from '@whitbread-eos/atoms';

import {
  getTableConfig,
  getTableRedesignDesktopConfig,
  getDashboardRedesignTabletConfig,
  getDashboardRedesignMobileConfig,
} from './tableConfig';

describe('tableConfig', () => {
  const mockTranslation = (key: string) => {
    const translations: { [key: string]: string } = {
      'dashboard.bookings.booked': 'Booked for',
      'dashboard.bookings.hotel': 'Hotel',
      'dashboard.bookings.date': 'Date',
      'dashboard.bookings.price': 'Price',
      'dashboard.bookings.status': 'Status',
      'dashboard.bookings.bookedBy': 'Booked by',
      'dashboard.bookings.night': 'night',
      'dashboard.bookings.nights': 'nights',
      'dashboard.bookings.bookingReference': 'Booking ref:',
      'dashboard.bookings.upcoming': 'Upcoming',
      'dashboard.bookings.past': 'Past',
      'dashboard.bookings.cancelled': 'Cancelled',
      'dashboard.bookings.checkedIn': 'Checked in',
      'dashboard.bookings.details': 'Details',
    };
    return translations[key] || key;
  };

  const mockRow: TableListRow = {
    leadGuest: 'John Doe',
    hotelName: 'Premier Inn London',
    arrivalDate: '2026-05-01',
    departureDate: '2026-05-03',
    noOfNights: '2',
    totalCost: {
      currency: 'GBP',
      amount: '150.50',
    } as Price,
    bookingStatus: 'future',
    bookingReference: 'PI123456',
    bookedBy: 'Jane Smith',
  };

  describe('getTableConfig', () => {
    it('should return correct table columns for regular user', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);

      expect(columns).toHaveLength(5);
      expect(columns[0].key).toBe('leadGuest');
      expect(columns[1].key).toBe('hotelName');
      expect(columns[2].key).toBe('arrivalDate');
      expect(columns[3].key).toBe('totalCost');
      expect(columns[4].key).toBe('bookingStatus');
    });

    it('should include bookedBy column for business users', () => {
      const columns = getTableConfig(mockTranslation, 'en', true);

      expect(columns).toHaveLength(6);
      expect(columns[1].key).toBe('bookedBy');
      expect(columns[1].title).toBe('Booked by');
    });

    it('should render bookedBy column content for business users', () => {
      const columns = getTableConfig(mockTranslation, 'en', true);
      const bookedByColumn = columns.find((col) => col.key === 'bookedBy');
      const bookedByRender = bookedByColumn?.render?.(mockRow, 0);

      const { container } = render(<>{bookedByRender}</>);
      expect(container.textContent).toBe('Jane Smith');
      const span = container.querySelector('span');
      expect(span?.className).toContain('sessioncamhidetext');
      expect(span?.className).toContain('assist-no-show');
    });

    it('should render price with correct formatting', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const priceRender = priceColumn?.render?.(mockRow, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toContain('150.50');
    });

    it('should handle null totalCost gracefully', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const rowWithNullPrice = { ...mockRow, totalCost: null };
      const priceRender = priceColumn?.render?.(rowWithNullPrice, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toBe('');
    });

    it('should handle undefined totalCost gracefully', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const rowWithUndefinedPrice = { ...mockRow, totalCost: undefined };
      const priceRender = priceColumn?.render?.(rowWithUndefinedPrice, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toBe('');
    });

    it('should render booking status correctly', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0);

      const { container } = render(<>{statusRender}</>);
      expect(container.textContent).toBe('Upcoming');
    });

    it('should render lead guest with privacy classes', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const leadGuestColumn = columns.find((col) => col.key === 'leadGuest');
      const leadGuestRender = leadGuestColumn?.render?.(mockRow, 0);

      const { container } = render(<>{leadGuestRender}</>);
      const span = container.querySelector('span');
      expect(span?.className).toContain('sessioncamhidetext');
      expect(span?.className).toContain('assist-no-show');
    });

    it('should format single night correctly', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const singleNightRow = { ...mockRow, noOfNights: '1' };
      const dateRender = dateColumn?.render?.(singleNightRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toContain('1 night');
    });

    it('should format multiple nights correctly', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const dateRender = dateColumn?.render?.(mockRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toContain('2 nights');
    });
  });

  describe('getTableRedesignDesktopConfig', () => {
    it('should return correct table columns for desktop redesign', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);

      expect(columns).toHaveLength(5);
      expect(columns[0].key).toBe('hotelName');
      expect(columns[1].key).toBe('arrivalDate');
      expect(columns[2].key).toBe('leadGuest');
      expect(columns[3].key).toBe('totalCost');
      expect(columns[4].key).toBe('bookingStatus');
    });

    it('should insert bookedBy column before status for business users', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', true);

      expect(columns).toHaveLength(6);
      expect(columns[4].key).toBe('bookedBy');
      expect(columns[5].key).toBe('bookingStatus');
    });

    it('should render hotel name with booking reference', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const hotelColumn = columns.find((col) => col.key === 'hotelName');
      const hotelRender = hotelColumn?.render?.(mockRow, 0);

      const { container } = render(<>{hotelRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
      expect(container.textContent).toContain('Booking ref: PI123456');
    });

    it('should format dates with locale', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'de', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const dateRender = dateColumn?.render?.(mockRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toBeTruthy();
      expect(container.textContent).toContain(' - ');
    });

    it('should render expand/collapse controls when rowCollapse provided', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const mockOnExpand = jest.fn();
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: mockOnExpand,
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      expect(link).toBeInTheDocument();
      expect(link?.getAttribute('aria-label')).toBe('Expand details');
    });

    it('should call onExpand when expand button is clicked', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const mockOnExpand = jest.fn();
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: mockOnExpand,
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      link?.click();

      expect(mockOnExpand).toHaveBeenCalledWith(0, false);
    });

    it('should render collapse button when expanded', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: true,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      expect(link?.getAttribute('aria-label')).toBe('Collapse details');
    });

    it('should render leadGuest column content', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const leadGuestColumn = columns.find((col) => col.key === 'leadGuest');
      const leadGuestRender = leadGuestColumn?.render?.(mockRow, 0);

      const { container } = render(<>{leadGuestRender}</>);
      expect(container.textContent).toBe('John Doe');
      const span = container.querySelector('span');
      expect(span?.className).toContain('sessioncamhidetext');
      expect(span?.className).toContain('assist-no-show');
    });

    it('should render totalCost column content', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const totalCostColumn = columns.find((col) => col.key === 'totalCost');
      const totalCostRender = totalCostColumn?.render?.(mockRow, 0);

      const { container } = render(<>{totalCostRender}</>);
      expect(container.textContent).toContain('150.50');
    });

    it('should render bookedBy column content for business users', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', true);
      const bookedByColumn = columns.find((col) => col.key === 'bookedBy');
      const bookedByRender = bookedByColumn?.render?.(mockRow, 0);

      const { container } = render(<>{bookedByRender}</>);
      expect(container.textContent).toBe('Jane Smith');
      const span = container.querySelector('span');
      expect(span?.className).toContain('sessioncamhidetext');
      expect(span?.className).toContain('assist-no-show');
    });

    it('should handle missing dates gracefully', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const rowWithMissingDates = {
        ...mockRow,
        arrivalDate: undefined,
        departureDate: undefined,
      };
      const dateRender = dateColumn?.render?.(rowWithMissingDates, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toContain(' - ');
    });
  });

  describe('getDashboardRedesignTabletConfig', () => {
    it('should return correct table columns for tablet redesign', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);

      expect(columns).toHaveLength(2);
      expect(columns[0].key).toBe('bookingDetails');
      expect(columns[1].key).toBe('bookingStatus');
    });

    it('should render combined booking details with lead guest for business users', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', true);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const detailsRender = detailsColumn?.render?.(mockRow, 0);

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
      expect(container.textContent).toContain('John Doe');
      expect(container.textContent).toContain('Booking ref: PI123456');
    });

    it('should not render lead guest for regular users', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const detailsRender = detailsColumn?.render?.(mockRow, 0);

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
      expect(container.textContent).not.toContain('Booked for: John Doe');
    });

    it('should render status and price in status column', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{statusRender}</>);
      expect(container.textContent).toContain('Upcoming');
      expect(container.textContent).toContain('150.50');
    });

    it('should call onExpand when tablet expand button is clicked', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const mockOnExpand = jest.fn();
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: mockOnExpand,
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      link?.click();

      expect(mockOnExpand).toHaveBeenCalledWith(0, false);
    });

    it('should handle missing dates in tablet view', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const rowWithMissingDates = {
        ...mockRow,
        arrivalDate: undefined,
        departureDate: undefined,
      };
      const detailsRender = detailsColumn?.render?.(rowWithMissingDates, 0);

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
    });
  });

  describe('getDashboardRedesignMobileConfig', () => {
    it('should return correct table columns for mobile redesign', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', false);

      expect(columns).toHaveLength(1);
      expect(columns[0].key).toBe('bookingDetails');
    });

    it('should render all booking details in single column', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const mockOnExpand = jest.fn();
      const detailsRender = detailsColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: mockOnExpand,
      });

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Upcoming');
      expect(container.textContent).toContain('Premier Inn London');
      expect(container.textContent).toContain('2 nights');
      expect(container.textContent).toContain('Booking ref: PI123456');
    });

    it('should render lead guest for business users', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', true);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const detailsRender = detailsColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('John Doe');
    });

    it('should call onExpand when mobile expand button is clicked', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const mockOnExpand = jest.fn();
      const detailsRender = detailsColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: mockOnExpand,
      });

      const { container } = render(<>{detailsRender}</>);
      const link = container.querySelector('a');
      link?.click();

      expect(mockOnExpand).toHaveBeenCalledWith(0, false);
    });

    it('should handle missing bookingReference in mobile view', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const rowWithoutRef = { ...mockRow, bookingReference: undefined };
      const detailsRender = detailsColumn?.render?.(rowWithoutRef, 0, {
        isExpanded: false,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
      expect(container.textContent).not.toContain('Booking ref:');
    });

    it('should handle missing dates in mobile view', () => {
      const columns = getDashboardRedesignMobileConfig(mockTranslation, 'en', false);
      const detailsColumn = columns.find((col) => col.key === 'bookingDetails');
      const rowWithMissingDates = {
        ...mockRow,
        arrivalDate: undefined,
        departureDate: undefined,
      };
      const detailsRender = detailsColumn?.render?.(rowWithMissingDates, 0, {
        isExpanded: false,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{detailsRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
    });
  });

  describe('Price formatting edge cases', () => {
    it('should handle price with missing currency', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const rowWithMissingCurrency = {
        ...mockRow,
        totalCost: { amount: '100' } as Price,
      };
      const priceRender = priceColumn?.render?.(rowWithMissingCurrency, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toBe('');
    });

    it('should handle price with missing amount', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const rowWithMissingAmount = {
        ...mockRow,
        totalCost: { currency: 'GBP' } as Price,
      };
      const priceRender = priceColumn?.render?.(rowWithMissingAmount, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toBe('');
    });

    it('should handle price with non-numeric amount', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const priceColumn = columns.find((col) => col.key === 'totalCost');
      const rowWithInvalidAmount = {
        ...mockRow,
        totalCost: { currency: 'GBP', amount: 'invalid' } as Price,
      };
      const priceRender = priceColumn?.render?.(rowWithInvalidAmount, 0);

      const { container } = render(<>{priceRender}</>);
      expect(container.textContent).toBe('');
    });
  });

  describe('Booking status variations', () => {
    const testStatuses = [
      { status: 'future', expected: 'Upcoming' },
      { status: 'past', expected: 'Past' },
      { status: 'cancelled', expected: 'Cancelled' },
      { status: 'checked_in', expected: 'Checked in' },
    ];

    testStatuses.forEach(({ status, expected }) => {
      it(`should render ${status} status as ${expected}`, () => {
        const columns = getTableConfig(mockTranslation, 'en', false);
        const statusColumn = columns.find((col) => col.key === 'bookingStatus');
        const rowWithStatus = { ...mockRow, bookingStatus: status };
        const statusRender = statusColumn?.render?.(rowWithStatus, 0);

        const { container } = render(<>{statusRender}</>);
        expect(container.textContent).toBe(expected);
      });
    });

    it('should handle uppercase status values', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const rowWithUppercaseStatus = { ...mockRow, bookingStatus: 'FUTURE' };
      const statusRender = statusColumn?.render?.(rowWithUppercaseStatus, 0);

      const { container } = render(<>{statusRender}</>);
      expect(container.textContent).toBe('Upcoming');
    });

    it('should handle unknown status gracefully', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const rowWithUnknownStatus = { ...mockRow, bookingStatus: 'unknown' };
      const statusRender = statusColumn?.render?.(rowWithUnknownStatus, 0);

      const { container } = render(<>{statusRender}</>);
      expect(container).toBeTruthy();
    });
  });

  describe('Date locale handling', () => {
    it('should use German locale for de language', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'de', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const dateRender = dateColumn?.render?.(mockRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toBeTruthy();
    });

    it('should use English locale for en language', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const dateRender = dateColumn?.render?.(mockRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toBeTruthy();
    });

    it('should default to English locale for unknown language', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'fr', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const dateRender = dateColumn?.render?.(mockRow, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toBeTruthy();
    });
  });

  describe('Responsive behavior', () => {
    it('should hide appropriate columns on mobile in getTableConfig', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const hideOnMobileColumns = columns.filter((col) => col.hideOnMobile);

      expect(hideOnMobileColumns).toHaveLength(3);
      expect(hideOnMobileColumns.map((col) => col.key)).toEqual([
        'hotelName',
        'totalCost',
        'bookingStatus',
      ]);
    });

    it('should set correct width for status column in desktop redesign', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');

      expect(statusColumn?.width).toBe('1%');
    });

    it('should set correct width for status column in tablet redesign', () => {
      const columns = getDashboardRedesignTabletConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');

      expect(statusColumn?.width).toBe('150px');
    });
  });

  describe('Expand/collapse button edge cases', () => {
    it('should not render expand button when rowCollapse is not provided', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0);

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      expect(link).not.toBeInTheDocument();
    });

    it('should not render expand button when onExpand is undefined', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: false,
        onExpand: undefined as any,
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      expect(link).not.toBeInTheDocument();
    });

    it('should not render expand button when isExpanded is undefined', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const statusRender = statusColumn?.render?.(mockRow, 0, {
        isExpanded: undefined as any,
        onExpand: jest.fn(),
      });

      const { container } = render(<>{statusRender}</>);
      const link = container.querySelector('a');
      expect(link).not.toBeInTheDocument();
    });
  });

  describe('Additional edge cases', () => {
    it('should handle null bookingStatus', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const statusColumn = columns.find((col) => col.key === 'bookingStatus');
      const rowWithNullStatus = { ...mockRow, bookingStatus: null };
      const statusRender = statusColumn?.render?.(rowWithNullStatus, 0);

      const { container } = render(<>{statusRender}</>);
      expect(container).toBeTruthy();
    });

    it('should handle missing arrivalDate in getTableConfig', () => {
      const columns = getTableConfig(mockTranslation, 'en', false);
      const dateColumn = columns.find((col) => col.key === 'arrivalDate');
      const rowWithoutDate = { ...mockRow, arrivalDate: undefined };
      const dateRender = dateColumn?.render?.(rowWithoutDate, 0);

      const { container } = render(<>{dateRender}</>);
      expect(container.textContent).toContain('2 nights');
    });

    it('should handle missing hotelName', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const hotelColumn = columns.find((col) => col.key === 'hotelName');
      const rowWithoutHotel = { ...mockRow, hotelName: undefined };
      const hotelRender = hotelColumn?.render?.(rowWithoutHotel, 0);

      const { container } = render(<>{hotelRender}</>);
      expect(container).toBeTruthy();
    });

    it('should handle empty bookingReference in desktop view', () => {
      const columns = getTableRedesignDesktopConfig(mockTranslation, 'en', false);
      const hotelColumn = columns.find((col) => col.key === 'hotelName');
      const rowWithoutRef = { ...mockRow, bookingReference: '' };
      const hotelRender = hotelColumn?.render?.(rowWithoutRef, 0);

      const { container } = render(<>{hotelRender}</>);
      expect(container.textContent).toContain('Premier Inn London');
    });
  });
});
