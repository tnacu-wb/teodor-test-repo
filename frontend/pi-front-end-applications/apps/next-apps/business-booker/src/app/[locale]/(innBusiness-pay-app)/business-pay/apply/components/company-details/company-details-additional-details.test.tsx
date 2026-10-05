import '@testing-library/jest-dom';
import { render, waitFor, fireEvent } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { Wizard } from '@whitbread-eos/layout';

import { PayApplicationStep } from '../types';
import { CompanyDetailsAdditionalDetails } from './company-details-additional-details';

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
  estimatedMonthlySpend: '',
  locale: LOCALES.EN,
  header: null,
  initialState: {
    companyDetails: {
      companyName: '',
      companyBusinessType: '',
      estMonthlySpend: '£500',
      hotelBrandPolicy: '',
    },
  },
  initialStepId: PayApplicationStep.COMPANY_DETAILS_ADDITIONAL_DETAILS,
  steps: [
    {
      id: PayApplicationStep.COMPANY_DETAILS_ADDITIONAL_DETAILS,
      component: (
        <CompanyDetailsAdditionalDetails
          icons={{}}
          estimatedMonthlySpend={'£500'}
          locale={LOCALES.EN}
        />
      ),
    },
  ],
};

describe('CompanyDetailsAdditionalDetails component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CompanyDetailsAdditionalDetails component', () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);

    expect(getByTestId('wizard-page')).toBeInTheDocument();
  });
  it('should render CompanyDetailsAdditionalDetails component and click on save and close', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const wizardPage = getByTestId('wizard-page');
    const saveAndClose = getByTestId('footer-link');

    expect(wizardPage).toBeInTheDocument();
    expect(saveAndClose).toBeInTheDocument();

    await waitFor(async () => {
      fireEvent.click(saveAndClose);
    });
  });
  it('should render CompanyDetailsAdditionalDetails component and click on continue', async () => {
    const { getByTestId } = render(<Wizard {...mockProps} />);
    const wizardPage = getByTestId('wizard-page');
    const continueButton = getByTestId('footer-button');

    expect(wizardPage).toBeInTheDocument();
    expect(continueButton).toBeInTheDocument();

    await waitFor(async () => {
      fireEvent.click(continueButton);
    });
  });
});
