import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import GroupBookingsConfirmation from './GroupBookingsConfirmation';

const testContactName = 'TestName';
const testContactEmail = 'TestEmail';
const testCaseNumber = 'TestcaseNumber';
const testTitlePrefix = 'Thanks for your request,';
const testContactEmailPrefix = 'A confirmation email has been sent to';
const testCaseNumberPrefix = 'Your case number is:';
const testContent = 'We’ve received your request and aim to get in touch within the next 72 hours.';
const testBackToHome = 'Back to homepage';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => ({
    language: 'en',
    country: 'gb',
  }),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: (key: string) => {
      switch (key) {
        case 'groupBooking.confirmation.title':
          return testTitlePrefix;
        case 'groupBooking.confirmation.caseNumber':
          return testCaseNumberPrefix;
        case 'groupBooking.confirmation.email':
          return testContactEmailPrefix;
        case 'groupBooking.confirmation.content':
          return testContent;
        case 'groupBooking.confirmation.backToHome':
          return testBackToHome;
        default:
          return '';
      }
    },
  }),
}));

const mockProps = {
  contactName: testContactName,
  contactEmail: testContactEmail,
  caseNumber: testCaseNumber,
};

const renderComponent = () => {
  return render(<GroupBookingsConfirmation {...mockProps} />);
};

describe('<GroupBookingsConfirmation />', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render the contact name section', async () => {
    const { getByText } = renderComponent();

    expect(getByText(testTitlePrefix)).toBeInTheDocument();
    expect(getByText(testContactName)).toBeInTheDocument();
  });

  it('should render the case number section', async () => {
    const { getByText } = renderComponent();

    expect(getByText(testCaseNumberPrefix)).toBeInTheDocument();
    expect(getByText(testCaseNumber)).toBeInTheDocument();
  });

  it('should render the contact email section', async () => {
    const { getByText } = renderComponent();

    expect(getByText(testContactEmailPrefix)).toBeInTheDocument();
    expect(getByText(testContactEmail)).toBeInTheDocument();
  });

  it('should render the content section', async () => {
    const { getByText } = renderComponent();

    expect(getByText(testContent)).toBeInTheDocument();
  });

  it('should render the back to home button', async () => {
    const { getByRole } = renderComponent();

    expect(getByRole('button', { name: testBackToHome })).toBeInTheDocument();
  });
});
