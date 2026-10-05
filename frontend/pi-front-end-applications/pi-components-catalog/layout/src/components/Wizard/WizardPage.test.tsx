import React from 'react';

import { render } from '../../utils/test-utils';
import { Wizard } from './Wizard.component';
import { Props, WizardPage } from './WizardPage.component';

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

const mockProps: Props = {};

/* eslint-disable react/display-name */
jest.mock('../Main', () => {
  return ({ children }: { children: React.ReactNode }) => <div>{children}</div>;
});

const wrapper = () => (
  <Wizard
    icons={{}}
    header={<div></div>}
    initialState={{
      value: 'test',
    }}
    initialStepId="step1"
    steps={[
      {
        id: 'step1',
        component: <WizardPage {...mockProps} />,
      },
    ]}
  />
);

describe('WizardPage component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render WizardPage component with default layout', () => {
    const { getByTestId } = render(wrapper());

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should render WizardPage component with form layout', () => {
    mockProps.type = 'form';
    const { getByTestId } = render(wrapper());

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });
});
