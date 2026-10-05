import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';

import { render, userEvent } from '../../utils/test-utils';
import CreateAccount from './CreateAccount.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: () => ({
    t: () => jest.fn(),
  }),
}));

const mockCustomLocale = jest.fn();
const mockNavigateToSignup = jest.fn();
const mockFeatureToggle = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
  useFeatureToggle: () => mockFeatureToggle(),
  useAuth0Navigation: () => ({ navigateToSignup: mockNavigateToSignup }),
}));

const mockRouter = {
  push: jest.fn(),
};
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockRouter,
}));

const mockProps = {
  basketReference: '1234567890',
};

const mockBookerDetails = {
  email: 'test@example.com',
  firstName: 'John',
  lastName: 'Doe',
};

describe('Create Account', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockCustomLocale.mockReturnValue({
      country: 'gb',
      language: 'en',
    });
    mockFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: false });
  });

  it('should display the component', () => {
    const { getByTestId } = render(<CreateAccount {...mockProps} />);
    expect(getByTestId('createAccount-heading')).toBeInTheDocument();
    expect(getByTestId('createAccount-text')).toBeInTheDocument();
  });

  it('should load register page', async () => {
    const { getByTestId } = render(<CreateAccount {...mockProps} />);

    await userEvent.click(getByTestId('createAccount-button'));

    const url = 'gb/en/account/register?reservationId=1234567890';
    expect(mockRouter.push).toHaveBeenCalledWith(expect.stringContaining(url));
  });

  it('should navigate to Auth0 signup with booker details when FT_PI_AUTH0_LOGIN is enabled', async () => {
    mockFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

    const { getByTestId } = render(
      <CreateAccount {...mockProps} bookerDetails={mockBookerDetails} />
    );

    await userEvent.click(getByTestId('createAccount-button'));

    expect(mockNavigateToSignup).toHaveBeenCalledWith({
      email: mockBookerDetails.email,
      firstName: mockBookerDetails.firstName,
      lastName: mockBookerDetails.lastName,
      basketReference: mockProps.basketReference,
    });
    expect(mockRouter.push).not.toHaveBeenCalled();
  });

  it('should navigate to Auth0 signup without booker details when none are provided', async () => {
    mockFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

    const { getByTestId } = render(<CreateAccount {...mockProps} />);

    await userEvent.click(getByTestId('createAccount-button'));

    expect(mockNavigateToSignup).toHaveBeenCalledWith({
      email: undefined,
      firstName: undefined,
      lastName: undefined,
      basketReference: mockProps.basketReference,
    });
    expect(mockRouter.push).not.toHaveBeenCalled();
  });

  it('should navigate to Auth0 signup with an empty basketReference when basketReference is null', async () => {
    mockFeatureToggle.mockReturnValue({ [FT_PI_AUTH0_LOGIN]: true });

    const { getByTestId } = render(<CreateAccount basketReference={null} />);

    await userEvent.click(getByTestId('createAccount-button'));

    expect(mockNavigateToSignup).toHaveBeenCalledWith({
      email: undefined,
      firstName: undefined,
      lastName: undefined,
      basketReference: '',
    });
    expect(mockRouter.push).not.toHaveBeenCalled();
  });
});
