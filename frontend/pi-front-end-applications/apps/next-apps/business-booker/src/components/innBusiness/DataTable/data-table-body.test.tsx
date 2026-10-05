import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import DataTableBody from './data-table-body';

const mockColumns = [
  { key: 'name', label: 'Name', id: 'name' },
  { key: 'age', label: 'Age', id: 'age' },
];

const mockRows = [
  { name: 'Alice', age: 30 },
  { name: 'Bob', age: 25 },
];

jest.mock('./data-table-row', () => ({
  __esModule: true,
  default: ({ baseDataTestId, rowIndex }: any) => (
    <tr data-testid={`mock-row-${rowIndex}`} data-base={baseDataTestId} />
  ),
}));

jest.mock('./data-table-expendable-row', () => ({
  __esModule: true,
  default: ({ baseDataTestId, rowIndex }: any) => (
    <tr data-testid={`mock-expendable-row-${rowIndex}`} data-base={baseDataTestId} />
  ),
}));

describe('DataTableBody', () => {
  it('renders DataTableRow for each row when isExpendableWith is not provided', () => {
    render(
      <table>
        <DataTableBody baseDataTestId="test-table" rows={mockRows} columns={mockColumns} />
      </table>
    );
    expect(screen.getByTestId('mock-row-0')).toBeInTheDocument();
    expect(screen.getByTestId('mock-row-1')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-expendable-row-0')).not.toBeInTheDocument();
  });

  it('renders DataTableExpendableRow for each row when isExpendableWith is provided', () => {
    render(
      <table>
        <DataTableBody
          baseDataTestId="test-table"
          rows={mockRows}
          columns={mockColumns}
          isExpendableWith="details"
        />
      </table>
    );
    expect(screen.getByTestId('mock-expendable-row-0')).toBeInTheDocument();
    expect(screen.getByTestId('mock-expendable-row-1')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-row-0')).not.toBeInTheDocument();
  });

  it('passes isMobileView prop to DataTableExpendableRow', () => {
    render(
      <table>
        <DataTableBody
          baseDataTestId="test-table"
          rows={mockRows}
          columns={mockColumns}
          isExpendableWith="details"
          isMobileView={true}
        />
      </table>
    );
    expect(screen.getByTestId('mock-expendable-row-0')).toHaveAttribute('data-base', 'test-table');
  });

  it('renders no rows if rows prop is empty', () => {
    render(
      <table>
        <DataTableBody baseDataTestId="test-table" rows={[]} columns={mockColumns} />
      </table>
    );
    expect(screen.queryByTestId('mock-row-0')).not.toBeInTheDocument();
    expect(screen.queryByTestId('mock-expendable-row-0')).not.toBeInTheDocument();
  });
});
