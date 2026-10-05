import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { WordlineButton } from './wordline-button';

jest.mock('next/image', () => ({
  __esModule: true,
  default: ({ alt, ...props }: { alt: string }) => <img alt={alt} {...props} />,
}));

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({
    children,
    className,
    ...props
  }: {
    children: React.ReactNode;
    className?: string;
  }) => (
    <button className={className} {...props}>
      {children}
    </button>
  ),
  WorldlineLink: ({ renderButton }: { renderButton: (onClick: jest.Mock) => React.ReactNode }) =>
    renderButton(jest.fn()),
}));

const mockProps = {
  tetheredGuid: '',
  baseDataTestId: 'BaseId',
  icon: '',
  text: 'Go to WL',
  page: '',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    useTranslation: jest.fn(() => ({
      t: (key: string) => key,
    })),
    getSearchParams: () => new URLSearchParams(),
    businessTetherLogin: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

window._satellite = {
  track: jest.fn(),
};

describe('WordlineButton Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render wordline button', async () => {
    const { getByTestId } = render(<WordlineButton {...mockProps} />);

    expect(getByTestId(`${mockProps.baseDataTestId}-Button`)).toBeInTheDocument();
  });

  it('should call analyticsLogEvent when provided', async () => {
    const { getByTestId } = render(
      <WordlineButton {...mockProps} analyticsLogEvent="makePayment" />
    );

    const button = getByTestId(`${mockProps.baseDataTestId}-Button`);
    button.click();

    expect(window._satellite.track).toHaveBeenCalledWith('makePayment');
  });

  it('should not fail when analyticsLogEvent prop is not provided', async () => {
    const { getByTestId } = render(<WordlineButton {...mockProps} />);

    const button = getByTestId(`${mockProps.baseDataTestId}-Button`);
    expect(() => button.click()).not.toThrow();
  });

  it('should apply default button spacing when parentButtonStyle is not provided', async () => {
    const { getByTestId } = render(<WordlineButton {...mockProps} />);

    const button = getByTestId(`${mockProps.baseDataTestId}-Button`);
    expect(button.className).toContain('gap-2');
  });

  it('should use inline styling for link variant', async () => {
    const { getByTestId } = render(<WordlineButton {...mockProps} variant="link" />);

    const button = getByTestId(`${mockProps.baseDataTestId}-Button`);
    expect(button.className).toContain('inline');
  });

  it('should render prefix, icon, and iconSvg when provided', async () => {
    const { getByAltText, getByTestId } = render(
      <WordlineButton
        {...mockProps}
        icon="icon-path"
        prefixIconSvg={<span data-testid="prefix-icon" />}
        iconSvg={<span data-testid="suffix-icon" />}
      />
    );

    expect(getByTestId('prefix-icon')).toBeInTheDocument();
    expect(getByAltText(mockProps.text)).toBeInTheDocument();
    expect(getByTestId('suffix-icon')).toBeInTheDocument();
  });
});
