import '@testing-library/jest-dom';
import { formatDataTestId } from '@whitbread-eos/utils';

import { render } from '../../utils/test-utils';
import LoginFormFooter from './LoginFormFooter';

const mockCustomLocale = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useCustomLocale: () => mockCustomLocale(),
}));

const baseDataTestId = 'businessBookerLoginFooter';
const textTestId = 'businessAccountCardText';
const linkTestId = 'logInSignUpLink';
const imgTestId = 'businessAccountCardImg';

const mockedProps = {
  labels: undefined,
};

const Component = () => {
  return <LoginFormFooter {...mockedProps} />;
};

describe('Business Booker Login footer', function () {
  beforeAll(() => {
    mockCustomLocale.mockReturnValue({
      country: 'gb',
    });

    jest.clearAllMocks();
  });
  afterAll(() => {
    jest.resetAllMocks();
  });

  it('should render LoginFormFooter component', () => {
    const { getByTestId } = render(<Component />);
    expect(
      getByTestId(formatDataTestId(baseDataTestId, 'businessAccountCardContainer'))
    ).toBeInTheDocument();
  });
  it('should render Business Account Card text', () => {
    const { getByTestId } = render(<Component />);
    expect(
      getByTestId(formatDataTestId(baseDataTestId, 'businessAccountCardText'))
    ).toBeInTheDocument();
  });
  it('should render Business Account Card image', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, imgTestId))).toBeInTheDocument();
  });
  it('should render Business Account Card link', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, 'logInSignUpLink'))).toBeInTheDocument();
  });

  it('should display text & link for Business Account Card on English version', () => {
    const { getByTestId } = render(<Component />);
    expect(getByTestId(formatDataTestId(baseDataTestId, textTestId))).toBeInTheDocument();
    expect(getByTestId(formatDataTestId(baseDataTestId, linkTestId))).toBeInTheDocument();
    expect(getByTestId(formatDataTestId(baseDataTestId, imgTestId))).toBeInTheDocument();
  });
  it('should not display text & link for Business Account Card on German version', () => {
    mockCustomLocale.mockReturnValue({
      country: 'de',
    });
    const { queryByTestId } = render(<Component />);
    expect(queryByTestId(formatDataTestId(baseDataTestId, textTestId))).not.toBeInTheDocument();
    expect(queryByTestId(formatDataTestId(baseDataTestId, linkTestId))).not.toBeInTheDocument();
    expect(queryByTestId(formatDataTestId(baseDataTestId, imgTestId))).not.toBeInTheDocument();
  });
});
