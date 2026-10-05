import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { FT_IB_PAY_PIBA_EURO } from '@whitbread-eos/api';
import { useFeatureToggle, getCountryLanguageByLocale } from '@whitbread-eos/utils';

import { ContactUsContent } from './ContactUsContent';

jest.mock('@whitbread-eos/utils', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations: Record<string, string> = {
        'contact.contactUs.title': 'Contact Us',
        'contact.contactUs.subtitle':
          "Whether you need help with InnBusiness Pay, your booking or your account, we're here to help.",
        'contact.contactUs.general.heading': 'Bookings and InnBusiness',
        'contact.contactUs.general.subtitle': 'General contact',
        'contact.contactUs.ibpay.heading': 'InnBusiness Pay',
        'contact.contactUs.ibpay.subtitle': 'Spending credit account',
        'contact.contactUs.general.email.description':
          'Get in touch with the Business Support team by email. We aim to respond to all email within 48 hours.',
      };
      return translations[key] || key;
    },
  }),
  formatIBAssetsUrl: () => {
    return '/';
  },
  useFeatureToggle: jest.fn(),
  getLocaleByPathname: jest.fn(),
  getCountryLanguageByLocale: jest.fn(),
  renderSanitizedHtml: jest.fn((html: string) => html),
  cn: jest.fn((...classes: string[]) => classes.filter(Boolean).join(' ')),
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useFeatureToggle: jest.fn(),
  getCountryLanguageByLocale: jest.fn(),
}));

const mockProps = {
  icons: {
    'icon.address.icon': '/icons/address.svg',
  },
};

describe('ContactUsContent', () => {
  beforeEach(() => {
    jest.mocked(useFeatureToggle).mockReturnValue({
      [FT_IB_PAY_PIBA_EURO]: true,
    });
    jest.mocked(getCountryLanguageByLocale).mockReturnValue({ language: 'en', country: 'gb' });
  });

  it('should render the component ', () => {
    render(<ContactUsContent {...mockProps} />);

    expect(screen.getByText('Contact Us')).toBeInTheDocument();
    expect(
      screen.getByText(
        "Whether you need help with InnBusiness Pay, your booking or your account, we're here to help."
      )
    ).toBeInTheDocument();
    expect(screen.getByText('Bookings and InnBusiness')).toBeInTheDocument();
    expect(screen.getByText('General contact')).toBeInTheDocument();
    expect(screen.getByText('InnBusiness Pay')).toBeInTheDocument();
    expect(screen.getByText('Spending credit account')).toBeInTheDocument();
    expect(
      screen.getByText(
        'Get in touch with the Business Support team by email. We aim to respond to all email within 48 hours.'
      )
    ).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-container')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-container-title')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-container-subtitle')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-bookings-container')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-general-contact-cards')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-general-contact-cards').children).toHaveLength(1);
    expect(screen.getByTestId('ContactUsPage-innBusiness-pay-cards')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-innBusiness-pay-cards').children).toHaveLength(3);
    expect(screen.getByTestId('ContactUsPage-email-card')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-email-business-account-card')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-phone-card')).toBeInTheDocument();
    expect(screen.getByTestId('ContactUsPage-address-card')).toBeInTheDocument();
  });

  it('should render InnBusiness Pay section when feature toggle is on', () => {
    render(<ContactUsContent {...mockProps} />);

    expect(screen.getByTestId('ContactUsPage-innBusiness-pay-container')).toBeInTheDocument();
  });

  it('should not render InnBusiness Pay section when feature toggle is off', () => {
    jest.mocked(useFeatureToggle).mockReturnValue({
      [FT_IB_PAY_PIBA_EURO]: false,
    });
    jest.mocked(getCountryLanguageByLocale).mockReturnValue({ language: 'de', country: 'de' });

    render(<ContactUsContent {...mockProps} />);

    expect(screen.queryByTestId('ContactUsPage-innBusiness-pay-container')).not.toBeInTheDocument();
  });

  it('should render the live chat card when isContactUsLiveChatCardEnabled is true', () => {
    render(<ContactUsContent {...mockProps} isContactUsLiveChatCardEnabled={true} />);
    expect(screen.getByTestId('ContactUsPage-livechat-card')).toBeInTheDocument();
  });

  it('should invoke onClick and simulate a click on the live chat container', async () => {
    // Mock the live chat container element
    const mockLiveChatElement = document.createElement('div');
    mockLiveChatElement.className = 'LPMcontainer';
    document.body.appendChild(mockLiveChatElement);

    const clickSpy = jest.spyOn(mockLiveChatElement, 'click');
    render(<ContactUsContent {...mockProps} isContactUsLiveChatCardEnabled={true} />);

    // Simulate a click on the live chat card link
    const liveChatCard = screen.getByTestId('ContactUsPage-livechat-card-button');
    liveChatCard.click();

    // Verify that the live chat container's click method was called
    expect(clickSpy).toHaveBeenCalled();

    document.body.removeChild(mockLiveChatElement);
  });

  it('should not render the live chat card when isContactUsLiveChatCardEnabled is false', () => {
    render(<ContactUsContent {...mockProps} isContactUsLiveChatCardEnabled={false} />);
    expect(screen.queryByTestId('ContactUsPage-livechat-card')).not.toBeInTheDocument();
  });
});
