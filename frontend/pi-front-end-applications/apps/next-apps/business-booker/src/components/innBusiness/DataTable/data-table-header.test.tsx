import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import DataTableHeader from './data-table-header';

const columns = [
  { id: 'name', label: 'Name', headerClassName: 'header-name' },
  { id: 'age', label: 'Age', headerClassName: 'header-age' },
];

describe('DataTableHeader', () => {
  it('renders table header and row with correct test ids', () => {
    render(<DataTableHeader columns={columns} baseTestId="test" />);
    expect(screen.getByTestId('test-header')).toBeInTheDocument();
    expect(screen.getByTestId('test-row')).toBeInTheDocument();
  });

  it('renders all column headers with correct labels and test ids', () => {
    render(<DataTableHeader columns={columns} baseTestId="test" />);
    columns.forEach((column) => {
      const head = screen.getByTestId(`test-head-${column.id}`);
      expect(head).toBeInTheDocument();
      expect(head).toHaveTextContent(column.label);
    });
  });

  it('applies headerClassName to each column', () => {
    render(<DataTableHeader columns={columns} baseTestId="test" />);
    columns.forEach((column) => {
      const head = screen.getByTestId(`test-head-${column.id}`);
      expect(head.className).toContain(column.headerClassName);
    });
  });

  it('renders expandable column header when isExpendableWith and isMobileView are true', () => {
    render(
      <DataTableHeader
        columns={columns}
        baseTestId="test"
        isExpendableWith="expand"
        isMobileView={true}
      />
    );
    expect(screen.getByTestId('test-head-expandable')).toBeInTheDocument();
  });

  it('does not render expandable column header when isExpendableWith is not provided', () => {
    render(<DataTableHeader columns={columns} baseTestId="test" isMobileView={true} />);
    expect(screen.queryByTestId('test-head-expandable')).not.toBeInTheDocument();
  });

  it('does not render expandable column header when isMobileView is false', () => {
    render(
      <DataTableHeader
        columns={columns}
        baseTestId="test"
        isExpendableWith="expand"
        isMobileView={false}
      />
    );
    expect(screen.queryByTestId('test-head-expandable')).not.toBeInTheDocument();
  });
});
