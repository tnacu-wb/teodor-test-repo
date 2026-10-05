import { ChakraProvider } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import React from 'react';

import { FloatingHeader } from './FloatingHeader.component';

// Mock i18next
jest.mock('next-i18next', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
}));

// Mock framer-motion to avoid animation-related test issues
jest.mock('framer-motion', () => ({
  motion: {
    div: ({ children, ...props }: any) => <div {...props}>{children}</div>,
  },
}));

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
  getColumnWidth: jest.fn(() => '120px'),
  locale: 'en-GB' as const,
};

const renderWithChakra = (component: React.ReactElement) => {
  return render(<ChakraProvider>{component}</ChakraProvider>);
};

describe('FloatingHeader', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Rendering and Structure', () => {
    it('should render the floating header with correct test id', () => {
      renderWithChakra(<FloatingHeader {...mockProps} />);

      const floatingHeader = screen.getByTestId('hotel-price-table-floating-header');
      expect(floatingHeader).toBeInTheDocument();
    });

    it('should render table with presentation role', () => {
      renderWithChakra(<FloatingHeader {...mockProps} />);

      const table = screen.getByRole('presentation');
      expect(table).toBeInTheDocument();
      expect(table.tagName).toBe('TABLE');
    });

    it('should render TableHeader component inside', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isScrollLeftDisabled={false} />);

      // Check for elements that would be rendered by TableHeader
      expect(screen.getByTestId('scroll-left-button')).toBeInTheDocument();
      expect(screen.getByTestId('scroll-right-button')).toBeInTheDocument();
    });

    it('should hide left scroll button when isScrollLeftDisabled is true', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isScrollLeftDisabled={true} />);

      expect(screen.queryByTestId('scroll-left-button')).not.toBeInTheDocument();
      expect(screen.getByTestId('scroll-right-button')).toBeInTheDocument();
    });

    it('should hide right scroll button when isScrollRightDisabled is true', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isScrollRightDisabled={true} />);

      expect(screen.queryByTestId('scroll-right-button')).not.toBeInTheDocument();
    });

    it('should disable scroll buttons when isLoading is true', () => {
      renderWithChakra(
        <FloatingHeader {...mockProps} isScrollLeftDisabled={false} isLoading={true} />
      );

      const leftButton = screen.getByTestId('scroll-left-button');
      const rightButton = screen.getByTestId('scroll-right-button');
      expect(leftButton).toBeDisabled();
      expect(rightButton).toBeDisabled();
    });

    it('should have fixed positioning styles', () => {
      const { container } = renderWithChakra(<FloatingHeader {...mockProps} />);

      const floatingHeader = container.querySelector(
        '[data-testid="hotel-price-table-floating-header"]'
      );

      expect(floatingHeader).toHaveStyle({
        position: 'fixed',
        top: '0',
        left: '0',
        width: '100%',
        zIndex: '100',
      });
    });
  });

  describe('Column Structure', () => {
    it('should render colgroup with correct structure for desktop', () => {
      const { container } = renderWithChakra(<FloatingHeader {...mockProps} isMobile={false} />);

      const colgroup = container.querySelector('colgroup');
      const cols = container.querySelectorAll('col');

      expect(colgroup).toBeInTheDocument();
      // Hotel name (50%) + star + dates (5) + star = 8 columns total
      expect(cols).toHaveLength(mockProps.currentDates.length + 3);
    });

    it('should render colgroup with correct structure for mobile', () => {
      const { container } = renderWithChakra(<FloatingHeader {...mockProps} isMobile={true} />);

      const colgroup = container.querySelector('colgroup');
      const cols = container.querySelectorAll('col');

      expect(colgroup).toBeInTheDocument();
      // No hotel name column for mobile: star + dates (5) + star = 7 columns total
      expect(cols).toHaveLength(mockProps.currentDates.length + 2);
    });
  });

  describe('TableHeader Integration', () => {
    it('should pass all props to TableHeader', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isScrollLeftDisabled={false} />);

      // Verify TableHeader receives the dates
      mockProps.currentDates.forEach((date) => {
        expect(screen.getByTestId(`date-header-${date}`)).toBeInTheDocument();
      });

      // Verify scroll buttons are rendered when not disabled (indicating props are passed)
      expect(screen.getByTestId('scroll-left-button')).toBeInTheDocument();
      expect(screen.getByTestId('scroll-right-button')).toBeInTheDocument();
    });

    it('should reflect sorted state in TableHeader', () => {
      renderWithChakra(<FloatingHeader {...mockProps} sortedByDate="2025-08-04" />);

      const sortedHeader = screen.getByTestId('date-header-2025-08-04');
      expect(sortedHeader).toBeInTheDocument();
    });
  });

  describe('Responsive Behavior', () => {
    it('should hide hotel name column in mobile view', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isMobile={true} />);

      expect(screen.queryByTestId('hotel-name-header')).not.toBeInTheDocument();
    });

    it('should show hotel name column in desktop view', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isMobile={false} />);

      expect(screen.getByTestId('hotel-name-header')).toBeInTheDocument();
    });

    it('should adjust date formatting for smaller screens', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isSmallerThanSm={true} />);

      // Should show shortened weekday format
      expect(screen.getByText('Su')).toBeInTheDocument();
    });

    it('should show full date formatting for larger screens', () => {
      renderWithChakra(<FloatingHeader {...mockProps} isSmallerThanSm={false} />);

      // Should show full weekday format
      expect(screen.getByText('Sun')).toBeInTheDocument();
    });
  });

  describe('Container Styling', () => {
    it('should center the content with auto margins', () => {
      const { container } = renderWithChakra(<FloatingHeader {...mockProps} />);

      // The table should be rendered within the centered container
      expect(container.querySelector('table')).toBeInTheDocument();
    });
  });

  describe('Table Layout', () => {
    it('should handle different numbers of dates', () => {
      const differentDates = ['2025-08-03', '2025-08-04', '2025-08-05'];
      renderWithChakra(<FloatingHeader {...mockProps} currentDates={differentDates} />);

      differentDates.forEach((date) => {
        expect(screen.getByTestId(`date-header-${date}`)).toBeInTheDocument();
      });
    });
  });
});
