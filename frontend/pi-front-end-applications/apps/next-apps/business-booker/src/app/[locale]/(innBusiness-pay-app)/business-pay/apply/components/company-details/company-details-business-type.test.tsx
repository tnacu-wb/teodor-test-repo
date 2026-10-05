import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import userEvent from '@testing-library/user-event';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { CompanyDetailsBusinessType } from './company-details-business-type';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

const mockProps = {
  icons: {},
  companyType: '["test","test","test"]',
  header: null,
  initialState: {
    companyDetails: {
      companyName: '',
      companyBusinessType: '',
    },
  },
  initialStepId: PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE,
  steps: [
    {
      id: PayApplicationStep.COMPANY_DETAILS_BUSINESS_TYPE,
      component: (
        <CompanyDetailsBusinessType
          icons={{}}
          locale={LOCALES.EN}
          companyType='["test","test","test"]'
        />
      ),
    },
  ],
};

describe('CompanyDetailsBusinessType component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyDetailsBusinessType component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });
  it('should render CompanyDetailsBusinessType component and click on continue', async () => {
    const user = userEvent.setup();
    const { getByTestId, getAllByRole } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');
    const companyNameInput = getByTestId('Company-name-Form-Input');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
      expect(companyNameInput).toBeInTheDocument();
    });
    await user.type(companyNameInput, 'Stefan 2');
    const formRadioGroup = getByTestId('BusinessTypeForm-container');

    await waitFor(() => {
      expect(formRadioGroup).toBeInTheDocument();
    });

    const firstRadioButton = getAllByRole('radio')[0];

    await waitFor(() => {
      fireEvent.click(firstRadioButton);
    });

    await waitFor(() => {
      expect(firstRadioButton).toBeChecked();
    });

    await waitFor(() => {
      fireEvent.click(wizardContinueButton);
    });
  });
  it('should render CompanyDetailsBusinessType component and click on Close', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardCloseLink = getByTestId('footer-link');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardCloseLink).toBeInTheDocument();
    });

    await waitFor(() => {
      fireEvent.click(wizardCloseLink);
    });
  });
  it('should render CompanyDetailsBusinessType component and click on continue with radio selected', async () => {
    const { getByTestId, getAllByRole } = render(<Wizard {...mockProps} />);

    const wizardContainer = getByTestId('wizard-page');
    const wizardContinueButton = getByTestId('footer-button');
    const companyNameInput = getByTestId('Company-name-Form-Input');

    await waitFor(() => {
      expect(wizardContainer).toBeInTheDocument();
      expect(wizardContinueButton).toBeInTheDocument();
      expect(companyNameInput).toBeInTheDocument();
    });

    const formRadioGroup = getByTestId('BusinessTypeForm-container');
    await waitFor(() => {
      expect(formRadioGroup).toBeInTheDocument();
    });

    const firstRadioButton = getAllByRole('radio')[0];

    await waitFor(() => {
      fireEvent.click(firstRadioButton);
    });

    await waitFor(() => {
      expect(firstRadioButton).toBeChecked();
    });

    await waitFor(() => {
      fireEvent.click(wizardContinueButton);
    });
  });
});
