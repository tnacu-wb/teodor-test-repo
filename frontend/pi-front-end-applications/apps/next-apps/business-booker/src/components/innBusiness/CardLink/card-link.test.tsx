import '@testing-library/jest-dom/extend-expect';
import { render } from '@testing-library/react';
import React from 'react';

import CardLink from './card-link';

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  formatIBAssetsUrl: () => '/',
}));

describe('CardLink Component', () => {
  const defaultProps = {
    icon: 'test-icon.png',
    alt: 'Test Icon',
    title: 'Test Title',
    subtitle: 'Test Subtitle',
    href: '/test-link',
    baseDataTestId: 'card-link',
  };

  it('renders without crashing', () => {
    const { getByTestId } = render(<CardLink {...defaultProps} />);
    expect(getByTestId(defaultProps.baseDataTestId)).toBeInTheDocument();
  });

  it('displays the correct title and subtitle', () => {
    const { getByText } = render(<CardLink {...defaultProps} />);
    expect(getByText(defaultProps.title)).toBeInTheDocument();
    expect(getByText(defaultProps.subtitle)).toBeInTheDocument();
  });

  it('renders the image with correct alt text', () => {
    const { getByAltText } = render(<CardLink {...defaultProps} />);
    expect(getByAltText(defaultProps.alt)).toBeInTheDocument();
  });

  it('has the correct href attribute', () => {
    const { getByRole } = render(<CardLink {...defaultProps} />);
    expect(getByRole('link')).toHaveAttribute('href', defaultProps.href);
  });

  it('should not show external link icon', () => {
    const { queryByTestId } = render(<CardLink {...defaultProps} />);

    expect(queryByTestId(`${defaultProps.baseDataTestId}-external-icon`)).not.toBeInTheDocument();
  });

  it('should show external link icon', () => {
    const { queryByTestId } = render(
      <CardLink {...{ ...defaultProps, href: 'http://something.com', isExternalHref: true }} />
    );

    expect(queryByTestId(`${defaultProps.baseDataTestId}-external-icon`)).toBeInTheDocument();
  });

  it('should render default variant', () => {
    const { queryByTestId } = render(
      <CardLink {...{ ...defaultProps, variant: 'default', buttonText: 'Click Me' }} />
    );

    expect(queryByTestId(`${defaultProps.baseDataTestId}-button`)).not.toBeInTheDocument();
  });

  it('should render button link variant', () => {
    const { getByTestId } = render(
      <CardLink {...{ ...defaultProps, variant: 'button-link', buttonText: 'Click Me' }} />
    );

    expect(getByTestId(`${defaultProps.baseDataTestId}-button`)).toBeInTheDocument();
  });
});
