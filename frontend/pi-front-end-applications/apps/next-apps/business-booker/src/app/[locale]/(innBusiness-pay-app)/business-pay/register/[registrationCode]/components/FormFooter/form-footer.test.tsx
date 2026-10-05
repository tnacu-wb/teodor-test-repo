import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { FormFooter } from './form-footer';

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => {
    return <img {...props} />;
  },
}));

jest.mock('next/link', () => {
  const MockLink = ({ href, children }: any) => <a href={href}>{children}</a>;
  MockLink.displayName = 'Link';
  return MockLink;
});

// Mock translation and utils
jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'auth.payApp.security.needHelp': 'Need help?',
        'auth.payApp.contact.icon': '/contact-icon.svg',
        'auth.payApp.faq.icon': '/faq-icon.svg',
        'auth.payApp.security.contact': 'Contact',
        'auth.payApp.security.faqs': 'FAQs',
      };
      return translations[key] || key;
    },
  }),
  getPathForLocale: (locale: string, path: string) => `/${locale}/${path}`,
  formatIBAssetsUrl: (url: string) => url,
}));

describe('FormFooter', () => {
  const locale = LOCALES.EN;

  it('renders the need help text', () => {
    render(<FormFooter locale={locale as any} />);
    expect(screen.getByText('Need help?')).toBeInTheDocument();
  });

  it('renders both Contact and FAQs buttons by default', () => {
    const { container } = render(<FormFooter locale={locale as any} />);
    expect(screen.getByText('Contact')).toBeInTheDocument();
    expect(screen.getByText('FAQs')).toBeInTheDocument();
    expect(container.querySelectorAll('img')).toHaveLength(2);
  });

  it('renders only Contact button when showFaqs is false', () => {
    render(<FormFooter locale={locale as any} showFaqs={false} />);
    expect(screen.getByText('Contact')).toBeInTheDocument();
    expect(screen.queryByText('FAQs')).not.toBeInTheDocument();
  });

  it('renders only FAQs button when showContact is false', () => {
    render(<FormFooter locale={locale as any} showContact={false} />);
    expect(screen.getByText('FAQs')).toBeInTheDocument();
    expect(screen.queryByText('Contact')).not.toBeInTheDocument();
  });

  it('renders neither Contact nor FAQs when both are false', () => {
    render(<FormFooter locale={locale as any} showContact={false} showFaqs={false} />);
    expect(screen.queryByText('Contact')).not.toBeInTheDocument();
    expect(screen.queryByText('FAQs')).not.toBeInTheDocument();
  });

  it('Contact button links to correct locale path', () => {
    render(<FormFooter locale={LOCALES.EN} />);
    const contactLink = screen.getByText('Contact').closest('a');
    expect(contactLink).toHaveAttribute('href', '/en-gb/contact');
  });

  it('FAQs button links to correct locale path', () => {
    render(<FormFooter locale={LOCALES.DE} />);
    const faqsLink = screen.getByText('FAQs').closest('a');
    expect(faqsLink).toHaveAttribute('href', '/de-de/faqs');
  });

  it('renders icons with correct src', () => {
    const { container } = render(<FormFooter locale={LOCALES.EN} />);
    const images = container.querySelectorAll('img');
    expect(images[0]).toHaveAttribute('src', '/contact-icon.svg');
    expect(images[1]).toHaveAttribute('src', '/faq-icon.svg');
  });
});
