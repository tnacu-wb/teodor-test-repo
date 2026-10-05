import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { CostCentresNoResults } from './cost-centres-no-results';

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  cn: (j: string) => j,
}));

describe('CostCentresNoResults', () => {
  it('renders the TableBody and TableRow with correct data-testid', () => {
    render(<CostCentresNoResults />);
    const row = screen.getByTestId('CostCentresTable-NoResults-container');
    expect(row).toBeInTheDocument();
  });

  it('renders the TableCell with correct colSpan and className', () => {
    render(<CostCentresNoResults />);
    const cell = screen.getByTestId('CostCentresTable-NoResults-container').querySelector('td');
    expect(cell).toHaveAttribute('colspan', '3');
    expect(cell).toHaveClass('px-0 border-0');
  });

  it('renders the container div with correct className', () => {
    render(<CostCentresNoResults />);
    const cell = screen.getByTestId('CostCentresTable-NoResults-container').querySelector('td');
    const containerDiv = cell?.querySelector('div');
    expect(containerDiv).toHaveClass('flex flex-col items-start justify-center m-[1.5rem]');
  });

  it('renders the title with correct text and className', () => {
    render(<CostCentresNoResults />);
    const heading = screen.getByRole('heading', { level: 2 });
    expect(heading).toHaveTextContent('costCentreMgmt.noCostCentres.message');
    expect(heading).toHaveClass('text-darkGrey1 font-bold text-[1.125rem] mb-[.5rem]');
  });
});
