import React from 'react';

import { render } from '../../utils/test-utils';
import { Props, WizardHeader } from './WizardHeader.component';

const mockProps: Props = {
  logoUrl: '/',
  logoRedirectUrl: 'account/login',
};

jest.mock('next/navigation', () => ({
  usePathname: () => {
    return '/';
  },
}));

describe('WizardHeader component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render WizardHeader component', () => {
    const { getByTestId } = render(<WizardHeader {...mockProps} />);

    expect(getByTestId('IB-Logo')).toBeInTheDocument();
  });

  it('should render WizardHeader component with default logo url redirect', () => {
    mockProps.logoRedirectUrl = undefined;
    const { getByTestId } = render(<WizardHeader {...mockProps} />);

    expect(getByTestId('IB-Logo')).toBeInTheDocument();
  });
});
