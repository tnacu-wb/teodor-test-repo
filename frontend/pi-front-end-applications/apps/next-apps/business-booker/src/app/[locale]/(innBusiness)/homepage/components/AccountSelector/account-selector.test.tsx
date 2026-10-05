import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { AccountSelector, AccountSelectorSkeleton } from './account-selector';

const mockProps = {
  locale: LOCALES.EN,
  parentDataTestId: 'id',
};

jest.mock('~components/innBusiness/AccountHolder/account-holder', () => {
  return { AccountHolder: () => null };
});

describe('AccountSelector Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render InnBusinessPay component', async () => {
    const { getByTestId } = render(await AccountSelector(mockProps));

    expect(getByTestId('id-InnBusinessPay-Container')).toBeInTheDocument();
  });

  it('should render InnBusinessPay Skeleton', async () => {
    const { getByTestId } = render(await AccountSelectorSkeleton());

    expect(getByTestId('AccountSelectorSkeleton')).toBeInTheDocument();
  });
});
