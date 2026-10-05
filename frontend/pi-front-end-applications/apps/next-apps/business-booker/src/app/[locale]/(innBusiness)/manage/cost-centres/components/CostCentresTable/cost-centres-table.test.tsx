import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';
import { CustomerAccountDetails, Scheme } from '@whitbread-eos/api';
import React from 'react';

import CostCentresTable from './cost-centres-table';

const mockT = (key: string) => key;
jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({ t: mockT }),
  formatIBAssetsUrl: (url: string) => url,
  cn: (j: string) => j,
}));

jest.mock('next/image', () => {
  const MockImage = (props: any) => <img {...props} />;
  MockImage.displayName = 'Image';
  return MockImage;
});

const baseDataTestId = 'cost-centres-table';
const icons = { 'icon.manageEmployees-icon': '/icon.png' };
const account: CustomerAccountDetails = {
  tetheredGuid: 'guid',
  scheme: 'scheme' as Scheme,
};
const worldlinePostUrl = 'https://post.url';
const worldlineReturnUrl = 'https://return.url';

const initialItems = [
  {
    accountUniqueCustomerId: '19000922',
    costCentreUniqueCustomerId: '19002693',
    costCentreCode: '1234',
    costCentreName: 'Cost centre Dan',
  },
  {
    accountUniqueCustomerId: '19000922',
    costCentreUniqueCustomerId: '19002818',
    costCentreCode: '143683658aaaaaaaaaaaaaaaaaaaaaaaaaaa143683658aaaaa',
    costCentreName: 'ccoTest',
  },
];

describe('CostCentresTable', () => {
  it('renders table with correct number of items per page', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={initialItems}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    expect(screen.getByTestId(`${baseDataTestId}-DataTableClient`)).toBeInTheDocument();
    expect(screen.getAllByTestId(/row-actions/)).toHaveLength(2);
  });

  it('renders no results component when initialItems is empty', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={[]}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    expect(screen.getByTestId('CostCentresTable-NoResults-container')).toBeInTheDocument();
  });

  it('renders row status', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={initialItems}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    expect(screen.getAllByTestId(`${baseDataTestId}-row-status-successful`)[0]).toBeInTheDocument();
    expect(
      screen.getAllByLabelText('costCentreMgmt.costCentreStatus.options.active')[0]
    ).toBeInTheDocument();
  });

  it('renders edit action with correct icon and link', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={initialItems}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    const editLinks = screen.getAllByTestId(
      'cost-centres-table-row-action-edit-143683658aaaaaaaaaaaaaaaaaaaaaaaaaaa143683658aaaaa-icon'
    );
    expect(editLinks).toHaveLength(1);
  });

  it('handles pagination and changes page', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={initialItems}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    const nextButton = screen.getByRole('button', { name: /1/i });
    fireEvent.click(nextButton);
    expect(screen.getAllByTestId(/row-actions/)).toHaveLength(2);
  });

  it('disables pagination if items fit on one page', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={[initialItems[0]]}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    expect(screen.queryByRole('button', { name: /next/i })).not.toBeInTheDocument();
  });

  it('renders with loading false', () => {
    render(
      <CostCentresTable
        baseDataTestId={baseDataTestId}
        initialItems={initialItems}
        pageSize={2}
        icons={icons}
        account={account}
        worldlinePostUrl={worldlinePostUrl}
        worldlineReturnUrl={worldlineReturnUrl}
      />
    );
    expect(screen.getByTestId(`${baseDataTestId}-DataTableClient`)).toBeInTheDocument();
  });
});
