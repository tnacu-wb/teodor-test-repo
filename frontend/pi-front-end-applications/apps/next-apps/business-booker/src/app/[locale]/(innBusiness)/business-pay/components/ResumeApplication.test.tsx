import '@testing-library/jest-dom';
import { render, fireEvent, act } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { ResumeApplication } from './ResumeApplication';

const mockPush = jest.fn();
jest.mock('next/navigation', () => ({
  useRouter: jest.fn(() => ({
    push: mockPush,
    refresh: jest.fn(),
  })),
}));

jest.mock('../../../(innBusiness)/manage/cards/components/revalidate-link', () => ({
  revalidateCacheOnLink: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  formatIBAssetsUrl: jest.fn((url) => url),
  useTranslation: jest.fn(() => ({
    t: jest.fn((key) => key),
  })),
  getPathForLocale: jest.fn((locale, path) => `/${locale}/${path}`),
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({ children, onClick, ...props }: { children: React.ReactNode; onClick: any }) => (
    <button onClick={onClick} {...props}>
      {children}
    </button>
  ),
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ alt, ...props }: any) => <img alt={alt} {...props} />,
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({ children, onClick, ...props }: { children: React.ReactNode; onClick: any }) => (
    <button onClick={onClick} {...props}>
      {children}
    </button>
  ),
  FormPeoplePicker: ({ handleEmployeeChange, ...props }: { handleEmployeeChange: any }) => (
    <input
      {...props}
      data-testid="PeoplePicker-Form-Input"
      onChange={() =>
        handleEmployeeChange &&
        handleEmployeeChange({
          value: 'employee1',
          label: 'Test Employee',
          employeeData: {
            emailAddress: 'employee@example.com',
            title: 'Mr',
            firstName: 'Test',
            lastName: 'Employee',
          },
        })
      }
    />
  ),
  Notification: ({ message }: { message: string }) => <div>{message}</div>,
  SanitizedContent: ({ children }: { children: React.ReactNode; replacements: any }) => (
    <div>{children}</div>
  ),
}));

const mockProps = {
  companyName: 'Test Company Ltd',
  applicationReference: 123456,
  startedBy: 'john.doe@test.com',
  companyId: 'COMP_123',
  icons: {
    'icon.arrow.left.purple': 'arrow-left-icon.svg',
  },
  participants: [
    {
      participantId: 1,
      initiator: true,
      email: 'john.doe@test.com',
    },
  ],
  locale: LOCALES.EN,
  applicationGuid: 'GUID_123',
  applicationId: 'APP_123',
  token: 'TOKEN_123',
  showShareAppWithColleague: true,
};

describe('ResumeApplication component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockPush.mockClear();
  });

  it('should render ResumeApplication component', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const baseComponent = getByTestId('ResumeApplication-container');
    expect(baseComponent).toBeInTheDocument();

    const companyName = getByTestId('ResumeApplication-application-details-company-name');
    expect(companyName).toHaveTextContent('Test Company Ltd');

    const applicationReference = getByTestId('ResumeApplication-application-details-reference');
    expect(applicationReference).toHaveTextContent('123456');

    const startedBy = getByTestId('ResumeApplication-application-details-started-by');
    expect(startedBy).toHaveTextContent('john.doe@test.com');
  });

  it('should render title section with back arrow and title', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const titleSection = getByTestId('ResumeApplication-title');
    expect(titleSection).toBeInTheDocument();

    const backArrow = getByTestId('ResumeApplication-back-arrow');
    expect(backArrow).toBeInTheDocument();
  });

  it('should render resume application button', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const bottomButton = getByTestId('ResumeApplication-bottom-button');
    expect(bottomButton).toBeInTheDocument();

    const resumeButton = getByTestId('ResumeApplication-resume-application');
    expect(resumeButton).toBeInTheDocument();
  });

  it('should navigate to homepage when back arrow is clicked', async () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const backArrow = getByTestId('ResumeApplication-back-arrow');

    await act(async () => {
      fireEvent.click(backArrow);
    });

    expect(mockPush).toHaveBeenCalledWith('/en-gb/homepage');
  });

  it('should navigate to application page when resume button is clicked', async () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const resumeButton = getByTestId('ResumeApplication-resume-application');

    await act(async () => {
      fireEvent.click(resumeButton);
    });

    expect(mockPush).toHaveBeenCalledWith(
      '/en-gb/business-pay/apply?applicationId=APP_123&applicationGuid=GUID_123'
    );
  });

  it('should handle different locale correctly', async () => {
    const propsWithDifferentLocale = {
      ...mockProps,
      locale: LOCALES.DE,
    };

    const { getByTestId } = render(<ResumeApplication {...propsWithDifferentLocale} />);

    const backArrow = getByTestId('ResumeApplication-back-arrow');

    await act(async () => {
      fireEvent.click(backArrow);
    });

    expect(mockPush).toHaveBeenCalledWith('/de-de/homepage');
  });

  it('should pass correct props to BaseApplicationComponent', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const baseComponent = getByTestId('ResumeApplication-container');
    expect(baseComponent).toBeInTheDocument();

    expect(getByTestId('ResumeApplication-application-details-company-name')).toHaveTextContent(
      'Test Company Ltd'
    );
    expect(getByTestId('ResumeApplication-application-details-reference')).toHaveTextContent(
      '123456'
    );
    expect(getByTestId('ResumeApplication-application-details-started-by')).toHaveTextContent(
      'john.doe@test.com'
    );
  });

  it('should handle undefined locale gracefully', async () => {
    const propsWithUndefinedLocale = {
      ...mockProps,
      locale: undefined,
    };

    const { getByTestId } = render(<ResumeApplication {...propsWithUndefinedLocale} />);

    const backArrow = getByTestId('ResumeApplication-back-arrow');

    await act(async () => {
      fireEvent.click(backArrow);
    });

    expect(mockPush).toHaveBeenCalledWith('/en-gb/homepage');
  });

  it('should render with all optional props', () => {
    const propsWithAllOptionalProps = {
      ...mockProps,
      language: { code: 'en', name: 'English' } as any,
    };

    const { getByTestId } = render(<ResumeApplication {...propsWithAllOptionalProps} />);

    const baseComponent = getByTestId('ResumeApplication-container');
    expect(baseComponent).toBeInTheDocument();
  });

  it('should render back arrow image with correct props', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);

    const backArrow = getByTestId('ResumeApplication-back-arrow');
    const image = backArrow.querySelector('img');

    expect(image).toBeInTheDocument();
    expect(image).toHaveAttribute('alt', 'Back arrow');
    expect(image).toHaveAttribute('width', '26');
    expect(image).toHaveAttribute('height', '26');
  });

  it('should show share form when showShareAppWithColleague is true', () => {
    const { getByTestId } = render(<ResumeApplication {...mockProps} />);
    const shareWithColleagueForm = getByTestId('ResumeApplication-form-share-with-colleague');
    expect(shareWithColleagueForm).toBeInTheDocument();
  });

  it('should hide share form when showShareAppWithColleague is false', () => {
    const updatedProps = {
      ...mockProps,
      showShareAppWithColleague: false,
    };
    const { queryByTestId } = render(<ResumeApplication {...updatedProps} />);
    const shareWithColleagueForm = queryByTestId('ResumeApplication-form-share-with-colleague');
    expect(shareWithColleagueForm).not.toBeInTheDocument();
  });
});
