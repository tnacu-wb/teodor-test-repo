import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { DataTablePage, DataTablePageProps } from './data-table-page';

const mockProps: DataTablePageProps = {
  pageIndex: 1,
  columns: [
    { id: 'id', label: 'id' },
    {
      id: 'name',
      label: 'name',
      render(cell) {
        return cell;
      },
    },
  ],
  getPage: async () => [{ id: 1, name: 'test' }],
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getSearchParams: () => new URLSearchParams(),
    getCommonIcons: () => {
      return { chevron: {} };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

describe('DataTablePage Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DataTablePage component', async () => {
    const { getByTestId } = render(<tbody>{await DataTablePage(mockProps)}</tbody>);

    expect(getByTestId('DataTablePage-row-0')).toBeInTheDocument();
  });
});
