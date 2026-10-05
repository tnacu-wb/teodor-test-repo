import '@testing-library/jest-dom';
import { act, fireEvent, render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { useWizardContext, Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { YourDetails } from './your-details';

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    updateAppContactDetails: () => ({ status: 'success' }),
    updateResumeUrl: jest.fn(() => Promise.resolve(true)),
    getLocaleByPathname: () => LOCALES.EN,
    formatIBAssetsUrl: () => {
      return '/';
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/';
    },
    findError: serverUtils.findError,
  };
});

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => jest.fn(),
}));

const mockYourDetailsProps: any = {
  userDetails: {
    contactDetail: {
      title: 'Mr',
      firstName: 'Test',
      lastName: 'User',
      email: 'test@example.com',
    },
  },
  titleValues: ['Mr'],
  isCurrentUserInitiator: true,
};

const TestComponent = () => {
  const { currentStep } = useWizardContext();

  return <div data-testid={currentStep.id}></div>;
};

const mockProps = {
  icons: {},
  header: null,
  initialState: {
    contactDetails: {},
  },
  initialStepId: PayApplicationStep.YOUR_DETAILS,
  steps: [
    {
      id: PayApplicationStep.YOUR_DETAILS,
      component: <YourDetails {...mockYourDetailsProps} />,
    },
    {
      id: 'testStep2',
      component: <TestComponent />,
    },
  ],
};

describe('YourDetails component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render YourDetails component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });

  it('should go to the next step when clicking on the button', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    await act(async () => {
      fireEvent.change(getByTestId('landLineNumber-Form-Input'), {
        target: { value: '0123456789' },
      });
      fireEvent.change(getByTestId('position-Form-Input'), { target: { value: 'test' } });
    });

    await act(async () => {
      fireEvent.click(getByTestId('footer-button'));
    });

    expect(getByTestId('testStep2')).toBeInTheDocument();
  });

  it('should display initiator contact details from wizard state when present', () => {
    const customProps = {
      icons: {},
      header: null,
      initialState: {
        contactDetails: {
          title: 'Mrs',
          foreName: 'Initiator',
          lastName: 'Owner',
          position: '',
          telephone: '',
          mobile: '',
          email: 'initiator@example.com',
        },
      },
      initialStepId: PayApplicationStep.YOUR_DETAILS,
      steps: [
        {
          id: PayApplicationStep.YOUR_DETAILS,
          component: (
            <YourDetails
              {...{
                ...mockYourDetailsProps,
                titleValues: ['Mr', 'Mrs'],
                userDetails: {
                  contactDetail: {
                    title: 'Ms',
                    firstName: 'Booker',
                    lastName: 'Delegate',
                    email: 'booker@example.com',
                  },
                },
                isCurrentUserInitiator: true,
              }}
            />
          ),
        },
        {
          id: 'testStep2',
          component: <TestComponent />,
        },
      ],
    };

    const { getByText } = render(<Wizard {...customProps} />);

    expect(getByText('Mrs Initiator Owner')).toBeInTheDocument();
    expect(getByText('initiator@example.com')).toBeInTheDocument();
  });
});
