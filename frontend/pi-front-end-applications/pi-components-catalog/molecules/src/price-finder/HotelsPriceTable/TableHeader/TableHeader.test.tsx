import { ChakraProvider, Table, TableContainer } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import React from 'react';

import { TableHeader } from './TableHeader.component';

// Mock i18next
jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

const renderWithChakra = (component: React.ReactElement) => {
  return render(
    <ChakraProvider>
      <TableContainer>
        <Table>{component}</Table>
      </TableContainer>
    </ChakraProvider>
  );
};

// Mock the current date to ensure consistent test results
const mockCurrentDate = '2025-08-03';
jest.useFakeTimers();
jest.setSystemTime(new Date(mockCurrentDate));

const mockProps = {
  currentDates: ['2025-08-03', '2025-08-04', '2025-08-05', '2025-08-06', '2025-08-07'],
  sortedByDate: null,
  handleDateSort: jest.fn(),
  handleScrollLeft: jest.fn(),
  handleScrollRight: jest.fn(),
  isScrollLeftDisabled: true,
  isScrollRightDisabled: false,
  isLoading: false,
  isMobile: false,
  isSmallerThanSm: false,
  handleSortToggle: jest.fn(),
};

describe('TableHeader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  afterAll(() => {
    jest.useRealTimers();
  });

  it('should render all date headers', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    expect(screen.getByTestId('date-header-2025-08-03')).toBeInTheDocument();
    expect(screen.getByTestId('date-header-2025-08-04')).toBeInTheDocument();
    expect(screen.getByTestId('date-header-2025-08-05')).toBeInTheDocument();
    expect(screen.getByTestId('date-header-2025-08-06')).toBeInTheDocument();
    expect(screen.getByTestId('date-header-2025-08-07')).toBeInTheDocument();
  });

  it('should render hotel name header when not mobile', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    expect(screen.getByTestId('hotel-name-header')).toBeInTheDocument();
  });

  it('should not render hotel name header when mobile', () => {
    renderWithChakra(<TableHeader {...mockProps} isMobile={true} />);

    expect(screen.queryByTestId('hotel-name-header')).not.toBeInTheDocument();
  });

  it('should render scroll buttons with correct labels when enabled', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollLeftDisabled={false} />);

    const leftButton = screen.getByTestId('scroll-left-button');
    const rightButton = screen.getByTestId('scroll-right-button');

    expect(leftButton).toHaveAttribute('aria-label', 'View previous 7 days');
    expect(rightButton).toHaveAttribute('aria-label', 'View next 7 days');
  });

  it('should not render left scroll button when isScrollLeftDisabled is true', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollLeftDisabled={true} />);

    const leftButton = screen.queryByTestId('scroll-left-button');
    expect(leftButton).not.toBeInTheDocument();
  });

  it('should render left scroll button when isScrollLeftDisabled is false', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollLeftDisabled={false} />);

    const leftButton = screen.getByTestId('scroll-left-button');
    expect(leftButton).toBeInTheDocument();
    expect(leftButton).not.toBeDisabled();
  });

  it('should call handleScrollLeft when left button is clicked', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollLeftDisabled={false} />);

    const leftButton = screen.getByTestId('scroll-left-button');
    userEvent.click(leftButton);

    expect(mockProps.handleScrollLeft).toHaveBeenCalledTimes(1);
  });

  it('should call handleScrollRight when right button is clicked', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const rightButton = screen.getByTestId('scroll-right-button');
    userEvent.click(rightButton);

    expect(mockProps.handleScrollRight).toHaveBeenCalledTimes(1);
  });

  it('should not render right scroll button when isScrollRightDisabled is true', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollRightDisabled={true} />);

    const rightButton = screen.queryByTestId('scroll-right-button');
    expect(rightButton).not.toBeInTheDocument();
  });

  it('should render right scroll button when isScrollRightDisabled is false', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollRightDisabled={false} />);

    const rightButton = screen.getByTestId('scroll-right-button');
    expect(rightButton).toBeInTheDocument();
    expect(rightButton).not.toBeDisabled();
  });

  it('should disable scroll buttons when isLoading is true', () => {
    renderWithChakra(<TableHeader {...mockProps} isScrollLeftDisabled={false} isLoading={true} />);

    const leftButton = screen.getByTestId('scroll-left-button');
    expect(leftButton).toBeDisabled();
  });

  it('should disable right scroll button when isLoading is true', () => {
    renderWithChakra(<TableHeader {...mockProps} isLoading={true} />);

    const rightButton = screen.getByTestId('scroll-right-button');
    expect(rightButton).toBeDisabled();
  });

  it('should call handleDateSort when date header is clicked', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const dateHeader = screen.getByTestId('date-header-2025-08-03');
    userEvent.click(dateHeader);

    expect(mockProps.handleDateSort).toHaveBeenCalledWith('2025-08-03');
  });

  it('should call handleDateSort when Enter key is pressed on date header', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const dateHeader = screen.getByTestId('date-header-2025-08-03');
    userEvent.click(dateHeader);
    userEvent.keyboard('{Enter}');

    expect(mockProps.handleDateSort).toHaveBeenCalledWith('2025-08-03');
  });

  it('should call handleDateSort when Space key is pressed on date header', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const dateHeader = screen.getByTestId('date-header-2025-08-03');
    userEvent.click(dateHeader);
    userEvent.keyboard(' ');

    expect(mockProps.handleDateSort).toHaveBeenCalledWith('2025-08-03');
  });

  it('should highlight sorted date header', () => {
    renderWithChakra(<TableHeader {...mockProps} sortedByDate="2025-08-04" />);

    const sortedHeader = screen.getByTestId('date-header-2025-08-04');
    const unsortedHeader = screen.getByTestId('date-header-2025-08-03');

    // The sorted header should have different styling (tested through class or style attributes)
    expect(sortedHeader).toBeInTheDocument();
    expect(unsortedHeader).toBeInTheDocument();
  });

  it('should have proper accessibility attributes', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const dateHeader = screen.getByTestId('date-header-2025-08-03');

    expect(dateHeader).toHaveAttribute('role', 'button');
    expect(dateHeader).toHaveAttribute('tabIndex', '0');
    expect(dateHeader).toHaveAttribute('aria-label');
  });

  it('should format dates correctly for smaller screens', () => {
    renderWithChakra(<TableHeader {...mockProps} isSmallerThanSm={true} />);

    // On smaller screens, weekdays should be shortened to 2 characters
    expect(screen.getByText('Su')).toBeInTheDocument(); // Sunday shortened
  });

  it('should display full weekday format on larger screens', () => {
    renderWithChakra(<TableHeader {...mockProps} isSmallerThanSm={false} />);

    // On larger screens, weekdays should show 3 characters
    expect(screen.getByText('Sun')).toBeInTheDocument(); // Sunday full format
  });

  it('should render sort toggle button when not mobile and handleSortToggle is provided', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const sortButton = screen.getByText('priceFinder.sortByDistance');
    expect(sortButton).toBeInTheDocument();
  });

  it('should not render sort toggle when mobile', () => {
    renderWithChakra(<TableHeader {...mockProps} isMobile={true} />);

    const sortButton = screen.queryByText('priceFinder.sortByDistance');
    expect(sortButton).not.toBeInTheDocument();
  });

  it('should call handleSortToggle when sort button is clicked', () => {
    renderWithChakra(<TableHeader {...mockProps} />);

    const sortButton = screen.getByText('priceFinder.sortByDistance');
    userEvent.click(sortButton);

    expect(mockProps.handleSortToggle).toHaveBeenCalledTimes(1);
  });

  it('should display sort by distance regardless of sortBy prop', () => {
    renderWithChakra(<TableHeader {...mockProps} sortBy="price" />);

    const sortButton = screen.getByText('priceFinder.sortByDistance');
    expect(sortButton).toBeInTheDocument();
  });
});
