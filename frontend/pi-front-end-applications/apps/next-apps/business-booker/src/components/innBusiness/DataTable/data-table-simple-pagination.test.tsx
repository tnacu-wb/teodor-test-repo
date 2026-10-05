import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';

import DataTableSimplePagination from './data-table-simple-pagination';

describe('DataTableSimplePagination', () => {
  const defaultProps = {
    isLoading: false,
    baseTestId: 'data-table',
    columns: [{}, {}, {}],
    page: 1,
    hasNext: true,
    handlePreviousPageClick: jest.fn(),
    handleNextPageClick: jest.fn(),
    icons: {
      'icon.chevron.left.purple': '/left-icon.svg',
      'icon.chevron.right.purple': '/right-icon.svg',
    },
  };

  it('renders pagination component', () => {
    render(<DataTableSimplePagination {...defaultProps} />);
    expect(screen.getByTestId('data-table-Pagination')).toBeInTheDocument();
  });

  it('renders "Next" button when hasNext is true', () => {
    render(<DataTableSimplePagination {...defaultProps} />);
    expect(screen.getByTestId('data-table-Next')).toBeInTheDocument();
  });

  it('does not render "Previous" button when page is 1', () => {
    render(<DataTableSimplePagination {...defaultProps} />);
    expect(screen.queryByTestId('data-table-Prev')).not.toBeInTheDocument();
  });

  it('renders "Previous" button when page is greater than 1', () => {
    render(<DataTableSimplePagination {...defaultProps} page={2} />);
    expect(screen.getByTestId('data-table-Prev')).toBeInTheDocument();
  });

  it('calls handlePreviousPageClick when "Previous" button is clicked', () => {
    const handlePreviousPageClick = jest.fn();
    render(
      <DataTableSimplePagination
        {...defaultProps}
        page={2}
        handlePreviousPageClick={handlePreviousPageClick}
      />
    );
    fireEvent.click(screen.getByTestId('data-table-Prev'));
    expect(handlePreviousPageClick).toHaveBeenCalledTimes(1);
  });

  it('calls handleNextPageClick when "Next" button is clicked', () => {
    const handleNextPageClick = jest.fn();
    render(
      <DataTableSimplePagination {...defaultProps} handleNextPageClick={handleNextPageClick} />
    );
    fireEvent.click(screen.getByTestId('data-table-Next'));
    expect(handleNextPageClick).toHaveBeenCalledTimes(1);
  });

  it('does not render "Next" button when hasNext is false', () => {
    render(<DataTableSimplePagination {...defaultProps} hasNext={false} />);
    expect(screen.queryByTestId('data-table-Next')).not.toBeInTheDocument();
  });
});
