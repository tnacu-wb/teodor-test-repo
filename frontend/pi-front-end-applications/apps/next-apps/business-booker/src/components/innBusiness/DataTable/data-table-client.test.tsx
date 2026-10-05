import '@testing-library/jest-dom';
import { render, screen, waitFor } from '@testing-library/react';

import {
  DataTableClient as DataTableClientWithSkeleton,
  DataTableClient,
  PaginationType,
} from './data-table-client';

jest.mock('./data-table-header', () => ({
  __esModule: true,
  default: (props: any) => <thead data-testid="mock-header">{JSON.stringify(props)}</thead>,
}));

jest.mock('./data-table-body', () => ({
  __esModule: true,
  default: (props: any) => <tbody data-testid="mock-body">{JSON.stringify(props)}</tbody>,
}));

describe('DataTableClient', () => {
  const columns = [{ id: 'col1', label: 'Column 1' }];
  const items = [{ col1: 'Value 1' }];
  const results = Array.from({ length: 16 }, (_, index) => ({ col1: `Value ${index + 1}` }));
  const baseDataTestId = 'test-table';

  beforeEach(() => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 1400,
    });
  });

  it('renders Table with correct data-testid and className', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={false}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    const table = screen.getByTestId('test-table-DataTableClient');
    expect(table).toBeInTheDocument();
    expect(table).toHaveClass('table-fixed');
  });

  it('passes correct props to DataTableHeader and DataTableBody', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={true}
        isExpendableWith="expand"
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId('mock-header').textContent).toContain(
      '"columns":[{"id":"col1","label":"Column 1"}]'
    );
    expect(screen.getByTestId('mock-header').textContent).toContain('"isExpendableWith":"expand"');
    expect(screen.getByTestId('mock-header').textContent).toContain('"isMobileView":false');
    expect(screen.getByTestId('mock-body').textContent).toContain('"rows":[{"col1":"Value 1"}]');
    expect(screen.getByTestId('mock-body').textContent).toContain('"isExpendableWith":"expand"');
    expect(screen.getByTestId('mock-body').textContent).toContain('"isMobileView":false');
  });

  it('sets isMobileView to true when window.innerWidth < 1280', () => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 1000,
    });
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={false}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId('mock-header').textContent).toContain('"isMobileView":true');
    expect(screen.getByTestId('mock-body').textContent).toContain('"isMobileView":true');
  });

  it('updates isMobileView on window resize', async () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={false}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId('mock-header').textContent).toContain('"isMobileView":false');
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 1000,
    });
    window.dispatchEvent(new Event('resize'));
    await waitFor(() => {
      expect(screen.getByTestId('mock-header').textContent).toContain('"isMobileView":true');
    });
  });

  it('calls onPageChange if provided (no-op test)', () => {
    const onPageChange = jest.fn();
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={true}
        onPageChange={onPageChange}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(onPageChange).not.toHaveBeenCalled();
  });

  it('renders noResultsComponent when items is empty', () => {
    const NoResults = () => <div data-testid="no-results">No Results</div>;
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={[]}
        baseDataTestId={baseDataTestId}
        hasNext={false}
        noResultsComponent={<NoResults />}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId('no-results')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-body')).not.toBeInTheDocument();
  });

  it('does not render noResultsComponent when items is not empty', () => {
    const NoResults = () => <div data-testid="no-results">No Results</div>;
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={false}
        noResultsComponent={<NoResults />}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.queryByTestId('no-results')).not.toBeInTheDocument();
    expect(screen.getByTestId('mock-body')).toBeInTheDocument();
  });

  it('renders DataTableSkeleton when isLoading is true and items is empty', () => {
    jest.resetModules();
    const DataTableSkeletonMock = jest.fn(() => <tr data-testid="skeleton-row"></tr>);
    jest.doMock('./data-table-skeleton', () => ({
      __esModule: true,
      DataTableSkeleton: DataTableSkeletonMock,
    }));

    render(
      <DataTableClientWithSkeleton
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={[]}
        baseDataTestId="test-table"
        hasNext={false}
        isLoading={true}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId('DataTableSkeleton')).toBeInTheDocument();
    jest.dontMock('./data-table-skeleton');
  });

  it('does not render DataTableSkeleton when isLoading is true but items is not empty', () => {
    jest.resetModules();
    const DataTableSkeletonMock = jest.fn(() => <tr data-testid="skeleton-row"></tr>);
    jest.doMock('./data-table-skeleton', () => ({
      __esModule: true,
      DataTableSkeleton: DataTableSkeletonMock,
    }));

    render(
      <DataTableClientWithSkeleton
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={[{ col1: 'Value 1' }]}
        baseDataTestId="test-table"
        hasNext={false}
        isLoading={true}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.queryByTestId('skeleton-row')).not.toBeInTheDocument();
    jest.dontMock('./data-table-skeleton');
  });

  it('does not render DataTableSkeleton when isLoading is false and items is empty and noResultsComponent is provided', () => {
    jest.resetModules();
    const DataTableSkeletonMock = jest.fn(() => <tr data-testid="skeleton-row"></tr>);
    jest.doMock('./data-table-skeleton', () => ({
      __esModule: true,
      DataTableSkeleton: DataTableSkeletonMock,
    }));

    const NoResults = () => <div data-testid="no-results">No Results</div>;

    render(
      <DataTableClientWithSkeleton
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={[]}
        baseDataTestId="test-table"
        hasNext={false}
        isLoading={false}
        noResultsComponent={<NoResults />}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.queryByTestId('skeleton-row')).not.toBeInTheDocument();
    expect(screen.getByTestId('no-results')).toBeInTheDocument();
    jest.dontMock('./data-table-skeleton');
  });
  it('renders table pagination correctly', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={[{ col1: 'Value 1' }]}
        baseDataTestId="PaymentsTable"
        hasNext={true}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    const pagination = screen.getByTestId('PaymentsTable-DataTableClient-Pagination');
    expect(pagination).toBeInTheDocument();
  });

  it('does not render pagination when there are less than 15 results', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={[{ col1: 'Value 1' }]}
        baseDataTestId="PaymentsTable"
        hasNext={false}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(
      screen.queryByTestId('PaymentsTable-DataTableClient-Pagination')
    ).not.toBeInTheDocument();
  });
  it('calls onPageChange when the next button is clicked', () => {
    const onPageChange = jest.fn();
    render(
      <DataTableClient
        isLoading={false}
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={results}
        baseDataTestId="PaymentsTable"
        hasNext={true}
        onPageChange={onPageChange}
        icons={{
          'icon.chevron.left.purple': 'left-icon',
          'icon.chevron.right.purple': 'right-icon',
        }}
      />
    );

    const nextButton = screen.getByTestId('PaymentsTable-DataTableClient-Next');
    nextButton.click();

    expect(onPageChange).toHaveBeenCalledTimes(1);
  });

  it('calls onPageChange when the previous button is clicked', async () => {
    const onPageChange = jest.fn();
    render(
      <DataTableClient
        isLoading={false}
        columns={[{ id: 'col1', label: 'Column 1' }]}
        items={results}
        baseDataTestId="PaymentsTable"
        hasNext={true}
        onPageChange={onPageChange}
        icons={{
          'icon.chevron.left.purple': 'left-icon',
          'icon.chevron.right.purple': 'right-icon',
        }}
      />
    );

    // Simulate moving to page 2
    const nextButton = screen.getByTestId('PaymentsTable-DataTableClient-Next');
    await waitFor(() => nextButton.click());

    // Simulate clicking the previous button
    const prevButton = screen.getByTestId('PaymentsTable-DataTableClient-Prev');
    await waitFor(() => prevButton.click());

    expect(onPageChange).toHaveBeenCalledTimes(2);
  });
});

describe('DataTableClient - PaginationType.PAGES', () => {
  const columns = [{ id: 'col1', label: 'Column 1' }];
  const items = Array.from({ length: 16 }, (_, index) => ({ col1: `Value ${index + 1}` }));
  const baseDataTestId = 'PagesTable';

  beforeEach(() => {
    Object.defineProperty(window, 'innerWidth', {
      writable: true,
      configurable: true,
      value: 1400,
    });
  });

  it('renders DataTablePagesPagination when paginationType is PAGES and totalPages is provided', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={true}
        paginationType={PaginationType.PAGES}
        totalPages={5}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );
    expect(screen.getByTestId(`${baseDataTestId}-DataTableClient`)).toBeInTheDocument();
  });

  it('does not render DataTablePagesPagination if paginationType is PAGES but totalPages is not provided', () => {
    render(
      <DataTableClient
        isLoading={false}
        columns={columns}
        items={items}
        baseDataTestId={baseDataTestId}
        hasNext={true}
        paginationType={PaginationType.PAGES}
        icons={{ edit: 'edit-icon', delete: 'delete-icon' }}
      />
    );

    expect(screen.getByTestId(`${baseDataTestId}-DataTableClient`)).toBeInTheDocument();
  });
});
