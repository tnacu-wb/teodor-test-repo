import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { DataTablePagination } from './data-table-pagination';

const mockProps = {
  pageIndex: 1,
  totalPages: 10,
};

jest.mock('next/headers', () => ({
  headers: () => ({
    get: () => 'test',
  }),
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getSearchParams: jest.fn().mockResolvedValue(new URLSearchParams()),
    getCommonIcons: jest.fn().mockResolvedValue({
      'icon.chevron.left.purple': '/left.svg',
      'icon.chevron.right.purple': '/right.svg',
    }),
    formatIBAssetsUrl: () => '/',
  };
});

describe('DataTablePagination Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DataTablePagination component', async () => {
    const { getByTestId } = render(await DataTablePagination(mockProps));

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
  });

  it('should render pagination with mobile flag', async () => {
    const { getByTestId } = render(await DataTablePagination({ ...mockProps, mobile: true }));

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
  });

  it('should render pagination with custom className', async () => {
    const { getByTestId } = render(
      await DataTablePagination({ ...mockProps, className: 'custom-class' })
    );

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
  });

  it('should render pagination on first page', async () => {
    const { getByTestId, getAllByTestId } = render(
      await DataTablePagination({ pageIndex: 1, totalPages: 10 })
    );

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
    const links = getAllByTestId('DataTablePagination-link');
    expect(links.length).toBeGreaterThan(0);
  });

  it('should render pagination on last page', async () => {
    const { getByTestId, getAllByTestId } = render(
      await DataTablePagination({ pageIndex: 10, totalPages: 10 })
    );

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
    const links = getAllByTestId('DataTablePagination-link');
    expect(links.length).toBeGreaterThan(0);
  });

  it('should render pagination on middle page', async () => {
    const { getByTestId, getAllByTestId } = render(
      await DataTablePagination({ pageIndex: 5, totalPages: 10 })
    );

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
    const links = getAllByTestId('DataTablePagination-link');
    expect(links.length).toBeGreaterThan(0);
  });

  it('should render pagination with few pages', async () => {
    const { getByTestId, getAllByTestId } = render(
      await DataTablePagination({ pageIndex: 2, totalPages: 3 })
    );

    expect(getByTestId('DataTablePagination')).toBeInTheDocument();
    const links = getAllByTestId('DataTablePagination-link');
    expect(links.length).toBeGreaterThan(0);
  });
});
