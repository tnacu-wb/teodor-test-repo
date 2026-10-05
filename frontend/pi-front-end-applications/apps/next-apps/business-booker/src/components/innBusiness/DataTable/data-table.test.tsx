import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { DataTable, DataTableProps } from './data-table';

const mockProps: DataTableProps = {
  pageIndex: 0,
  columns: [{ id: 'id', label: 'label' }],
  getPage: async () => [{ id: 1 }],
  getTotal: async () => 1,
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getSearchParams: () => new URLSearchParams(),
  };
});

jest.mock('./data-table-page', () => {
  return { DataTablePage: () => null };
});

describe('DataTable Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DataTable component', async () => {
    const { getByTestId } = render(await DataTable(mockProps));

    expect(getByTestId('InnBusiness-DataTable')).toBeInTheDocument();
  });

  it('should render DataTable component with pageIndex greater than total', async () => {
    mockProps.pageIndex = 2;
    const { getByTestId } = render(await DataTable(mockProps));

    expect(getByTestId('InnBusiness-DataTable')).toBeInTheDocument();
  });
});
