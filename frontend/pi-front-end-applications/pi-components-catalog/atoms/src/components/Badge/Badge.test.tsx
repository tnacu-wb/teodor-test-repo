import Badge from '.';
import * as ChakraUI from '@chakra-ui/react';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

jest.mock('@whitbread-eos/utils', () => ({
  useSemanticTypography: () => (legacyTypography, semanticTypography) =>
    semanticTypography?.textStyle ? semanticTypography : legacyTypography,
}));

jest.mock('../../theme/adapters/semanticTypography', () => ({
  semanticTextStyles: {
    'label-s': { fontSize: '12px', fontWeight: 600 },
    'label-xl': { fontSize: '20px', fontWeight: 600 },
  },
}));

jest.mock('@chakra-ui/react', () => {
  const actual = jest.requireActual('@chakra-ui/react');

  return {
    ...actual,
    Badge: jest.fn(({ children }) => <span data-testid="chakra-badge">{children}</span>),
  };
});

const mockedChakraBadge = jest.mocked(ChakraUI.Badge);

describe('BadgeComponent', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders a Primary Badge component', () => {
    const { getByText } = render(<Badge variant="primary">Primary Badge</Badge>);
    expect(getByText('Primary Badge')).toBeInTheDocument();
    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
      }),
      undefined
    );
  });

  it('renders a Secondary Badge component', () => {
    const { getByText } = render(<Badge variant="secondary">Secondary Badge</Badge>);
    expect(getByText('Secondary Badge')).toBeInTheDocument();
  });

  it('renders a ZIP Badge component', () => {
    const { getByText } = render(<Badge variant="ZIP" />);
    expect(getByText('ZIP')).toBeInTheDocument();
  });

  it('renders a hub Badge component', () => {
    const { getByText } = render(<Badge variant="hub" />);
    expect(getByText('hub')).toBeInTheDocument();
  });

  it('resolves semantic textStyle to CSS props when semantic typography is provided', () => {
    render(
      <Badge variant="primary" textStyle="label-xl" fontSize="18px">
        Typography Badge
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontSize: '20px',
        fontWeight: 600,
      }),
      undefined
    );
  });

  it('resolves semantic textStyle to CSS props when no explicit fontSize provided', () => {
    render(
      <Badge variant="primary" textStyle="label-xl">
        Typography Badge Token Controlled
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontSize: '20px',
        fontWeight: 600,
      }),
      undefined
    );
  });

  it('uses provided fontSize inline when semantic mode is disabled', () => {
    render(
      <Badge variant="primary" fontSize="15px" badgeSemanticTypography={{}}>
        Explicit Font Size
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontSize: '15px',
      }),
      undefined
    );
  });

  it('uses explicit legacy typography split prop when semantic mode is disabled', () => {
    render(
      <Badge
        variant="primary"
        badgeLegacyTypography={{ fontSize: '14px' }}
        badgeSemanticTypography={{}}
      >
        Legacy Split
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontSize: '14px',
      }),
      undefined
    );
  });

  it('resolves explicit semantic typography split prop to CSS props', () => {
    render(
      <Badge variant="primary" badgeSemanticTypography={{ textStyle: 'label-s' }}>
        Semantic Split
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontSize: '12px',
        fontWeight: 600,
      }),
      undefined
    );
  });

  it('spreads empty object when semantic token is not found in semanticTextStyles', () => {
    render(
      <Badge variant="primary" badgeSemanticTypography={{ textStyle: 'unknown-token' }}>
        Unknown Token
      </Badge>
    );

    const call = mockedChakraBadge.mock.calls[0][0];
    expect(call.variant).toBe('primary');
    expect(call).not.toHaveProperty('fontSize');
    expect(call).not.toHaveProperty('fontWeight');
  });

  it('passes inline legacy typography props through to ChakraBadge when semantic mode is disabled', () => {
    render(
      <Badge
        variant="primary"
        fontWeight="bold"
        lineHeight="1.5"
        letterSpacing="0.1em"
        fontFamily="Arial"
        textTransform="uppercase"
        badgeSemanticTypography={{}}
      >
        Full Legacy Props
      </Badge>
    );

    expect(mockedChakraBadge).toHaveBeenCalledWith(
      expect.objectContaining({
        variant: 'primary',
        fontWeight: 'bold',
        lineHeight: '1.5',
        letterSpacing: '0.1em',
        fontFamily: 'Arial',
        textTransform: 'uppercase',
      }),
      undefined
    );
  });
});
