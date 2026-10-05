import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { DataTableSkeleton } from './data-table-skeleton';

const mockProps = {
  columnCount: 5,
};

describe('DataTableSkeleton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DataTableSkeleton component', async () => {
    const { getByTestId } = render(
      <table>
        <tbody>
          <DataTableSkeleton {...mockProps} />
        </tbody>
      </table>
    );

    expect(getByTestId('DataTableSkeleton')).toBeInTheDocument();
  });
});
