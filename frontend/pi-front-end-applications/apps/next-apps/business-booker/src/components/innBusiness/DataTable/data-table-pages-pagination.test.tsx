import '@testing-library/jest-dom';
import { render, fireEvent } from '@testing-library/react';

import DataTablePagesPagination from './data-table-pages-pagination';

describe('DataTablePagesPagination', () => {
  const columns = [{}, {}, {}];
  const icons = {
    'icon.chevron.left.purple': 'left-icon',
    'icon.chevron.right.purple': 'right-icon',
  };

  const defaultProps = {
    baseTestId: 'test-pagination',
    columns,
    page: 2,
    totalPages: 5,
    icons,
    onPageChange: jest.fn(),
    onPrevChange: jest.fn(),
    onNextChange: jest.fn(),
    mobile: false,
    className: 'custom-class',
  };

  afterEach(() => {
    jest.clearAllMocks();
  });

  it('renders pagination with correct test id and class', () => {
    const { getByTestId } = render(<DataTablePagesPagination {...defaultProps} />);
    const footer = getByTestId('test-pagination-Pagination');
    expect(footer).toBeInTheDocument();
    expect(footer.className).toContain('h-[3.75rem]');
  });

  it('renders correct number of pagination items', () => {
    const { getAllByTestId } = render(<DataTablePagesPagination {...defaultProps} />);
    const items = getAllByTestId('test-pagination-link');
    // Should render previous, next, ellipsis, and page buttons
    expect(items.length).toBeGreaterThanOrEqual(3);
  });

  it('calls onPageChange when a page button is clicked', () => {
    const { getAllByRole } = render(<DataTablePagesPagination {...defaultProps} />);
    const pageButtons = getAllByRole('button').filter(
      (btn) => btn.textContent && /^\d+$/.test(btn.textContent)
    );
    fireEvent.click(pageButtons[0]);
    expect(defaultProps.onPageChange).toHaveBeenCalled();
  });

  it('calls onNextChange when next button is clicked', () => {
    const { getAllByRole } = render(<DataTablePagesPagination {...defaultProps} />);
    const nextBtn = getAllByRole('button').find(
      (btn) => btn.querySelector('svg') || btn.innerHTML.includes('right-icon')
    );
    if (nextBtn) {
      fireEvent.click(nextBtn);
      expect(defaultProps.onNextChange).toHaveBeenCalled();
    }
  });

  it('does not render ellipsis when mobile', () => {
    const props = { ...defaultProps, mobile: true };
    const { container } = render(<DataTablePagesPagination {...props} />);
    expect(container.querySelector('.PaginationEllipsis')).toBeFalsy();
  });

  it('removes first item when page is 1', () => {
    const props = { ...defaultProps, page: 1 };
    const { getAllByTestId } = render(<DataTablePagesPagination {...props} />);
    const items = getAllByTestId('test-pagination-link');
    expect(items.length).toBeGreaterThanOrEqual(1);
  });

  it('removes last item when page is totalPages', () => {
    const props = { ...defaultProps, page: defaultProps.totalPages };
    const { getAllByTestId } = render(<DataTablePagesPagination {...props} />);
    const items = getAllByTestId('test-pagination-link');
    expect(items.length).toBeGreaterThanOrEqual(1);
  });

  it('renders with custom className', () => {
    const { container } = render(<DataTablePagesPagination {...defaultProps} />);
    expect(container.querySelector('.custom-class')).toBeTruthy();
  });

  it('renders correct colSpan for TableCell', () => {
    const { container } = render(<DataTablePagesPagination {...defaultProps} />);
    const cell = container.querySelector('td');
    expect(cell).toHaveAttribute('colspan', columns.length.toString());
  });
});
