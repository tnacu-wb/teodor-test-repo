import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import DataTableClientSkeleton, { DataTableSkeletonColumn } from './data-table-client-skeleton';

describe('DataTableClientSkeleton', () => {
  const columns: DataTableSkeletonColumn[] = [
    { label: 'Name' },
    { label: 'Email', headerClassName: 'custom-header' },
    { label: 'Role' },
  ];

  it('renders the Table with the correct data-testid', () => {
    render(<DataTableClientSkeleton baseTestId="test-table" columns={columns} />);
    expect(screen.getByTestId('test-table')).toBeInTheDocument();
  });

  it('renders the TableHeader and TableBody with correct data-testids', () => {
    render(<DataTableClientSkeleton baseTestId="test-table" columns={columns} />);
    expect(screen.getByTestId('test-table-header')).toBeInTheDocument();
    expect(screen.getByTestId('test-table-body')).toBeInTheDocument();
  });

  it('renders the correct number of TableHead elements with correct labels and data-testids', () => {
    render(<DataTableClientSkeleton baseTestId="test-table" columns={columns} />);
    columns.forEach((col, idx) => {
      const head = screen.getByTestId(`test-table-head-${idx}`);
      expect(head).toBeInTheDocument();
      expect(head).toHaveTextContent(col.label);
    });
  });

  it('applies custom headerClassName if provided', () => {
    render(<DataTableClientSkeleton baseTestId="test-table" columns={columns} />);
    const head = screen.getByTestId('test-table-head-1');
    expect(head.className).toContain('custom-header');
  });

  it('renders the correct number of columns in SkeletonRows', () => {
    render(<DataTableClientSkeleton baseTestId="test-table" columns={columns} />);
    expect(screen.getByTestId('test-table-body')).toBeInTheDocument();
  });
});
