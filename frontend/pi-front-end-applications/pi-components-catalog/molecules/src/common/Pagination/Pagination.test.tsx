import { useMediaQuery } from '@chakra-ui/react';
import { render, screen, fireEvent } from '@testing-library/react';

import { Pagination, PaginationProps } from './Pagination.component';

jest.mock('@chakra-ui/react', () => ({
  ...jest.requireActual('@chakra-ui/react'),
  useMediaQuery: jest.fn(),
}));

const mockOnPageChange = jest.fn();

const renderPagination = (props: Partial<PaginationProps> = {}) => {
  return render(
    <Pagination currentPage={1} totalPages={10} onPageChange={mockOnPageChange} {...props} />
  );
};

const mockedUseMediaQuery = useMediaQuery as jest.MockedFunction<typeof useMediaQuery>;

describe('Pagination', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockedUseMediaQuery.mockReturnValue([false]); // Default to desktop
  });

  it('should not render when totalPages is 1 or less', () => {
    renderPagination({ totalPages: 1 });
    expect(screen.queryByTestId('pagination')).not.toBeInTheDocument();
  });

  it('should render pagination with navigation buttons when totalPages > 1', () => {
    renderPagination({ totalPages: 5 });
    expect(screen.getByTestId('pagination')).toBeInTheDocument();
    expect(screen.getByTestId('pagination-prev')).toBeInTheDocument();
    expect(screen.getByTestId('pagination-next')).toBeInTheDocument();
  });

  describe('Button states', () => {
    it('should disable previous button on first page and next button on last page', () => {
      const { unmount } = renderPagination({ currentPage: 1, totalPages: 5 });
      expect(screen.getByTestId('pagination-prev')).toBeDisabled();
      unmount();

      renderPagination({ currentPage: 5, totalPages: 5 });
      expect(screen.getByTestId('pagination-next')).toBeDisabled();
    });

    it('should enable both buttons on middle pages', () => {
      renderPagination({ currentPage: 3, totalPages: 5 });
      expect(screen.getByTestId('pagination-prev')).not.toBeDisabled();
      expect(screen.getByTestId('pagination-next')).not.toBeDisabled();
    });
  });

  describe('Desktop page display logic', () => {
    it('should show all pages when totalPages <= 6', () => {
      renderPagination({ totalPages: 5 });
      for (let i = 1; i <= 5; i++) {
        expect(screen.getByTestId(`pagination-page-${i}`)).toBeInTheDocument();
      }
      expect(screen.queryByTestId('pagination-ellipsis')).not.toBeInTheDocument();
    });

    it('should show early pages pattern with ellipsis', () => {
      renderPagination({ currentPage: 1, totalPages: 10 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-5')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-ellipsis')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-10')).toBeInTheDocument();
    });

    it('should show late pages pattern with ellipsis', () => {
      renderPagination({ currentPage: 10, totalPages: 10 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-ellipsis')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-6')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-10')).toBeInTheDocument();
    });

    it('should show middle pages pattern with double ellipsis', () => {
      renderPagination({ currentPage: 5, totalPages: 10 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      const ellipses = screen.getAllByTestId('pagination-ellipsis');
      expect(ellipses).toHaveLength(2);
      expect(screen.getByTestId('pagination-page-4')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-5')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-6')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-10')).toBeInTheDocument();
    });
  });

  describe('Mobile page display logic', () => {
    beforeEach(() => {
      mockedUseMediaQuery.mockReturnValue([true]); // Mobile
    });

    it('should show all pages when totalPages <= 6', () => {
      renderPagination({ totalPages: 6 });
      for (let i = 1; i <= 6; i++) {
        expect(screen.getByTestId(`pagination-page-${i}`)).toBeInTheDocument();
      }
      expect(screen.queryByTestId('pagination-ellipsis')).not.toBeInTheDocument();
    });

    it('should show early pages pattern dynamically based on current page', () => {
      // For currentPage=1, should show "1 2 ... 8"
      renderPagination({ currentPage: 1, totalPages: 8 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-2')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-ellipsis')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-8')).toBeInTheDocument();
      expect(screen.queryByTestId('pagination-page-3')).not.toBeInTheDocument();
    });

    it('should show pages up to current+1 for early pages (currentPage=3)', () => {
      // For currentPage=3, should show "1 2 3 4 ... 8"
      renderPagination({ currentPage: 3, totalPages: 8 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-2')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-3')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-4')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-ellipsis')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-8')).toBeInTheDocument();
    });

    it('should show late pages pattern: "1 ... last-2 last-1 last"', () => {
      renderPagination({ currentPage: 8, totalPages: 8 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-ellipsis')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-6')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-7')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-8')).toBeInTheDocument();
    });

    it('should show middle pages pattern with double ellipsis', () => {
      renderPagination({ currentPage: 5, totalPages: 10 });
      expect(screen.getByTestId('pagination-page-1')).toBeInTheDocument();
      const ellipses = screen.getAllByTestId('pagination-ellipsis');
      expect(ellipses).toHaveLength(2);
      expect(screen.getByTestId('pagination-page-4')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-5')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-6')).toBeInTheDocument();
      expect(screen.getByTestId('pagination-page-10')).toBeInTheDocument();
    });
  });

  describe('Active page and interactions', () => {
    it('should mark current page as active with aria-current', () => {
      renderPagination({ currentPage: 3, totalPages: 5 });
      const page3Button = screen.getByTestId('pagination-page-3');
      expect(page3Button).toHaveAttribute('aria-current', 'page');

      const page1Button = screen.getByTestId('pagination-page-1');
      expect(page1Button).not.toHaveAttribute('aria-current');
    });

    it('should call onPageChange when clicking page numbers and nav buttons', () => {
      renderPagination({ currentPage: 3, totalPages: 5 });

      fireEvent.click(screen.getByTestId('pagination-page-2'));
      expect(mockOnPageChange).toHaveBeenCalledWith(2);

      fireEvent.click(screen.getByTestId('pagination-prev'));
      expect(mockOnPageChange).toHaveBeenCalledWith(2);

      fireEvent.click(screen.getByTestId('pagination-next'));
      expect(mockOnPageChange).toHaveBeenCalledWith(4);
    });

    it('should not call onPageChange for disabled buttons or ellipsis', () => {
      renderPagination({ currentPage: 1, totalPages: 10 });

      fireEvent.click(screen.getByTestId('pagination-prev'));
      fireEvent.click(screen.getByTestId('pagination-ellipsis'));
      expect(mockOnPageChange).not.toHaveBeenCalled();
    });
  });

  describe('Accessibility', () => {
    it('should have proper aria-labels', () => {
      renderPagination();
      expect(screen.getByTestId('pagination-prev')).toHaveAttribute(
        'aria-label',
        'Go to previous page'
      );
      expect(screen.getByTestId('pagination-next')).toHaveAttribute(
        'aria-label',
        'Go to next page'
      );
      expect(screen.getByTestId('pagination-page-1')).toHaveAttribute('aria-label', 'Go to page 1');
    });
  });

  it('should handle isDisabled prop', () => {
    renderPagination({ isDisabled: true, totalPages: 5 });
    expect(screen.getByTestId('pagination-prev')).toBeDisabled();
    expect(screen.getByTestId('pagination-next')).toBeDisabled();
    expect(screen.getByTestId('pagination-page-1')).toBeDisabled();
  });
});
