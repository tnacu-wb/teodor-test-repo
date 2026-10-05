import '@testing-library/jest-dom';
import { render, fireEvent, screen } from '@testing-library/react';

import DataTableExpendableRow from './data-table-expendable-row';

const columns = [
  {
    id: 'name',
    className: 'name-col',
    render: undefined,
    label: 'Name',
  },
  {
    id: 'value',
    className: 'value-col',
    render: (cell: any) => <span data-testid="custom-render">{cell}</span>,
    label: 'Value',
  },
];

const row = {
  name: 'Test Name',
  value: 'Test Value',
  details: 'Expanded Content',
};

describe('DataTableExpendableRow', () => {
  it('renders table row and cells correctly', () => {
    render(
      <DataTableExpendableRow
        baseDataTestId="test"
        columns={columns}
        row={row}
        rowData="row-data"
        rowIndex={0}
        expendableKey="details"
      />
    );

    expect(screen.getByTestId('test-row-0')).toBeInTheDocument();
    expect(screen.getByTestId('test-row-name-0')).toHaveTextContent('Test Name');
    expect(screen.getByTestId('test-row-value-0')).toBeInTheDocument();
    expect(
      screen.getByTestId('test-row-value-0').querySelector('[data-testid="custom-render"]')
    ).toHaveTextContent('Test Value');
  });

  it('does not render expand/collapse icon or expanded row in desktop view', () => {
    render(
      <DataTableExpendableRow
        baseDataTestId="test"
        columns={columns}
        row={row}
        rowData="row-data"
        rowIndex={1}
        expendableKey="details"
        isMobileView={false}
      />
    );
    expect(screen.queryByTestId('test-row-expandable-1')).not.toBeInTheDocument();
    expect(screen.queryByTestId('test-row-expanded-1')).not.toBeInTheDocument();
  });

  it('renders expand/collapse icon in mobile view', () => {
    render(
      <DataTableExpendableRow
        baseDataTestId="test"
        columns={columns}
        row={row}
        rowData="row-data"
        rowIndex={2}
        expendableKey="details"
        isMobileView={true}
      />
    );
    expect(screen.getByTestId('test-row-expandable-2')).toBeInTheDocument();
    expect(screen.getByTestId('Expand-Collapse-Icon')).toBeInTheDocument();
  });

  it('toggles expanded row on click in mobile view', () => {
    render(
      <DataTableExpendableRow
        baseDataTestId="test"
        columns={columns}
        row={row}
        rowData="row-data"
        rowIndex={3}
        expendableKey="details"
        isMobileView={true}
      />
    );
    expect(screen.queryByTestId('test-row-expanded-3')).not.toBeInTheDocument();

    fireEvent.click(screen.getByTestId('test-row-3'));
    expect(screen.getByTestId('test-row-expanded')).toBeInTheDocument();
    expect(screen.getByTestId('test-row-expanded')).toHaveTextContent('Expanded Content');

    fireEvent.click(screen.getByTestId('test-row-3'));
    expect(screen.queryByTestId('test-row-expanded')).not.toBeInTheDocument();
  });

  it('shows collapse icon when expanded in mobile view', () => {
    render(
      <DataTableExpendableRow
        baseDataTestId="test"
        columns={columns}
        row={row}
        rowData="row-data"
        rowIndex={4}
        expendableKey="details"
        isMobileView={true}
      />
    );
    fireEvent.click(screen.getByTestId('test-row-4'));
    expect(screen.getByTestId('Expand-Collapse-Icon')).toHaveAttribute('alt', 'collapse');
  });
});
