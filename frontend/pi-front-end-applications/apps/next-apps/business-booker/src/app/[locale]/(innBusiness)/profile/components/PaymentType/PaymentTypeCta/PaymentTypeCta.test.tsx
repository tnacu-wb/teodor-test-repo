import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import PaymentTypeCta from './PaymentTypeCta';

jest.mock('@whitbread-eos/atoms/ui', () => ({
  Button: ({
    children,
    className,
    variant,
    size,
    ...props
  }: {
    children: React.ReactNode;
    className: string;
    variant: string;
    size: string;
  }) => (
    <button className={className} data-variant={variant} data-size={size} {...props}>
      {children}
    </button>
  ),
  ButtonVariantDescriptor: { truncateWithEllipsisButton: '' },
}));

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'payment.title': 'Payment Type',
        'payment.description':
          'Save your card details and choose the way you receive invoices to make your next booking quicker and easier',
        'payment.button.add': 'Add Payment Method',
      };
      return translations[key] || key;
    },
  }),
  formatIBAssetsUrl: jest.fn((url) => url),
}));

describe('PaymentTypeCta', () => {
  beforeEach(() => {
    render(<PaymentTypeCta />);
  });

  it('renders the container with correct styles and test ID', () => {
    const container = screen.getByTestId('PaymentTypeCta-Container');
    expect(container).toBeInTheDocument();
    expect(container.tagName.toLowerCase()).toBe('section');
  });

  it('renders the title with correct content and styling', () => {
    const title = screen.getByTestId('PaymentTypeCta-Title');
    expect(title).toBeInTheDocument();
    expect(title).toHaveTextContent('Payment Type');
    expect(title.tagName.toLowerCase()).toBe('h4');
  });

  it('renders the description with correct content', () => {
    const description = screen.getByTestId('PaymentTypeCta-Description');
    expect(description).toBeInTheDocument();
    expect(description).toHaveTextContent(
      'Save your card details and choose the way you receive invoices to make your next booking quicker and easier'
    );
    expect(description.tagName.toLowerCase()).toBe('p');
  });

  it('renders the add payment button with correct properties', () => {
    const button = screen.getByTestId('PaymentTypeCta-Add-New-Button');

    expect(button).toBeInTheDocument();
    expect(button).toHaveTextContent('Add Payment Method');
    expect(button).toHaveAttribute('data-variant', 'outline');
    expect(button).toHaveAttribute('data-size', 'lg');
    expect(button).toHaveClass('mobile:min-w-full');
  });
});
