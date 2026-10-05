import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { Wizard, WizardHeader } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { HeaderSteps } from './header-steps';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => ({
    push: jest.fn(),
    replace: jest.fn(),
    back: jest.fn(),
    forward: jest.fn(),
    refresh: jest.fn(),
    prefetch: jest.fn(),
  }),
  usePathname: () => {
    return '/';
  },
}));

const mockProps = {
  icons: {},
  header: <WizardHeader logoUrl="/" steps={<HeaderSteps />} />,
  initialState: {},
  initialStepId: PayApplicationStep.LANDING,
  steps: [
    {
      id: PayApplicationStep.LANDING,
      component: null,
    },
    {
      id: PayApplicationStep.YOUR_DETAILS,
      component: null,
    },
  ],
};

describe('HeaderSteps component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render HeaderSteps component', () => {
    const { queryByTestId } = render(<Wizard {...mockProps} />);

    expect(queryByTestId('BusinessSteps')).not.toBeInTheDocument();
  });

  it('should render HeaderSteps component on Your details step', () => {
    mockProps.initialStepId = PayApplicationStep.YOUR_DETAILS;
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('BusinessSteps')).toBeInTheDocument();
  });
});
