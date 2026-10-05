import React from 'react';

import { render } from '../../utils/test-utils';
import { WizardProps, Wizard } from './Wizard.component';

jest.mock('next/navigation', () => ({
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => '/test-path',
}));

const mockProps: WizardProps<any> = {
  initialState: {},
  initialStepId: 'step1',
  steps: [
    {
      id: 'step1',
      component: null,
    },
  ],
  header: null,
  icons: {},
};

/* eslint-disable react/display-name */
jest.mock('../Main', () => {
  return ({ children }: { children: React.ReactNode }) => <div>{children}</div>;
});

describe('Wizard component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render Wizard component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard')).toBeInTheDocument();
  });

  it('should render Wizard component with invalid steps', () => {
    mockProps.steps = [];
    expect(() => render(<Wizard {...mockProps} />)).toThrow();
  });
});
