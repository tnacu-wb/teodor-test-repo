import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import DataTableRow from './data-table-row';

describe('DataTableRow', () => {
  const baseDataTestId = 'test-table';
  const rowIndex = 1;
  const columns = [
    {
      id: 'name',
      className: 'name-class',
      render: undefined,
      label: 'Name',
    },
    {
      id: 'age',
      className: 'age-class',
      render: (cell: any) => <span>Age: {cell}</span>,
      label: 'Age',
    },
  ];
  const rowData = 'row-data';
  const row = {
    name: 'John Doe',
    age: 30,
  };

  it('renders a TableRow with correct data attributes', () => {
    render(
      <DataTableRow
        baseDataTestId={baseDataTestId}
        rowIndex={rowIndex}
        columns={columns}
        rowData={rowData}
        row={row}
      />
    );
    const tableRow = screen.getByTestId(`${baseDataTestId}-row-${rowIndex}`);
    expect(tableRow).toBeInTheDocument();
    expect(tableRow).toHaveAttribute('data-rowdata', rowData);
  });

  it('renders TableCell for each column with correct testid and className', () => {
    render(
      <DataTableRow
        baseDataTestId={baseDataTestId}
        rowIndex={rowIndex}
        columns={columns}
        rowData={rowData}
        row={row}
      />
    );
    columns.forEach((column) => {
      const cell = screen.getByTestId(`${baseDataTestId}-row-${column.id}-${rowIndex}`);
      expect(cell).toBeInTheDocument();
      expect(cell.className).toContain(column.className);
    });
  });

  it('renders cell value directly if no render function is provided', () => {
    render(
      <DataTableRow
        baseDataTestId={baseDataTestId}
        rowIndex={rowIndex}
        columns={columns}
        rowData={rowData}
        row={row}
      />
    );
    expect(screen.getByText('John Doe')).toBeInTheDocument();
  });

  it('renders cell using render function if provided', () => {
    render(
      <DataTableRow
        baseDataTestId={baseDataTestId}
        rowIndex={rowIndex}
        columns={columns}
        rowData={rowData}
        row={row}
      />
    );
    expect(screen.getByText('Age: 30')).toBeInTheDocument();
  });
});
