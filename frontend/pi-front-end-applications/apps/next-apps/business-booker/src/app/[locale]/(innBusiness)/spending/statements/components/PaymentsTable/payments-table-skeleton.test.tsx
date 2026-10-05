import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import PaymentsTableSkeleton from './payments-table-skeleton';

jest.mock('./payments-columns', () => ({
  dateLabelHeaderStyle: 'date-header',
  desktopOnlyStyle: 'desktop-only',
  labelHeaderStyle: 'label-header',
  labelStyleTextRight: 'label-text-right',
}));

jest.mock('~components/innBusiness/DataTable/data-table-client-skeleton', () => ({
  __esModule: true,
  default: ({ baseTestId, columns }: any) => (
    <div data-testid={baseTestId}>
      {columns.map((col: any, idx: number) => (
        <div key={col.label} data-testid={`column-${idx}`}>
          <span>{col.label}</span>
          <span>{col.headerClassName}</span>
        </div>
      ))}
    </div>
  ),
}));

describe('PaymentsTableSkeleton', () => {
  const t = (key: string) => `translated:${key}`;

  it('renders DataTableClientSkeleton with correct baseTestId', () => {
    render(<PaymentsTableSkeleton t={t} />);
    expect(screen.getByTestId('PaymentsTableSkeleton')).toBeInTheDocument();
  });

  it('renders all columns with translated labels', () => {
    render(<PaymentsTableSkeleton t={t} />);
    expect(screen.getByText('translated:Date')).toBeInTheDocument();
    expect(screen.getByText('translated:Description')).toBeInTheDocument();
    expect(screen.getByText('translated:Status')).toBeInTheDocument();
    expect(screen.getByText('translated:Value')).toBeInTheDocument();
  });

  it('applies correct headerClassName to each column', () => {
    render(<PaymentsTableSkeleton t={t} />);
    expect(screen.getByText('date-header')).toBeInTheDocument();
    expect(screen.getByText('desktop-only')).toBeInTheDocument();
    expect(screen.getByText('label-header')).toBeInTheDocument();
    expect(screen.getByText('label-text-right')).toBeInTheDocument();
  });
});
