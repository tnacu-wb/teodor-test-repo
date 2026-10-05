import '@testing-library/jest-dom';
import { useFeatureToggle } from '@whitbread-eos/utils';

import { fireEvent, render } from '../../utils/test-utils';
import SummerPromo from './SummerPromo.component';

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  useFeatureToggle: jest.fn(),
  renderSanitizedHtml: jest.fn((html) => html),
  formatAssetsUrl: jest.fn((url) => url),
  formatDataTestId: jest.fn((id) => id),
  useCustomLocale: jest.fn(() => ({ language: 'en' })),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

let mockProps = {
  promotionBanner: {
    enabled: true,
    icon: '/content/dam/global/icons/common/price-tag.svg',
    title: 'Summer Sale: 20% off',
    description: 'Save 20% off when you stay 3 nights or more until 30 September 2025.',
    terms:
      "<a href='/gb/en/terms/booking-terms-and-conditions.html'>Terms and conditions apply</a>",
    srpNotificationTitle: 'Select a hotel to see if discount applies to your stay.',
    srpNotificationText: "Prices shown here don't include your discount yet.",
  },
  showNotification: true,
};

describe('SummerPromo', () => {
  it('should not display anything if feature flag is false', function () {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_summer_sale_promotion_banner: false,
    });

    const { queryByTestId } = render(
      <>
        <SummerPromo {...mockProps} />
      </>
    );
    const banner = queryByTestId('SummerPromoContainer');

    expect(banner).toBeNull();
  });

  it('should display bannner if feature flag is true', function () {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_summer_sale_promotion_banner: true,
    });

    const { queryByTestId } = render(
      <>
        <SummerPromo {...mockProps} />
      </>
    );
    const banner = queryByTestId('SummerPromoContainer');

    expect(banner).toBeInTheDocument();
  });

  it('should render correctly at 767px screen width', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_summer_sale_promotion_banner: true,
    });

    window.innerWidth = 767;
    window.dispatchEvent(new Event('resize'));

    const { queryByTestId } = render(
      <>
        <SummerPromo {...mockProps} />
      </>
    );
    const chevronDownIcon = queryByTestId('chevronDownIcon');
    expect(chevronDownIcon).toBeInTheDocument();

    if (chevronDownIcon) {
      fireEvent.click(chevronDownIcon);
    }
    const chevronUpIcon = queryByTestId('chevronUpIcon');
    expect(chevronUpIcon).toBeInTheDocument();
  });

  it('should render correctly at 767px screen width with showNotification false', () => {
    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_summer_sale_promotion_banner: true,
    });

    window.innerWidth = 767;
    window.dispatchEvent(new Event('resize'));

    mockProps = {
      ...mockProps,
      showNotification: false,
    };

    const { queryByTestId } = render(
      <>
        <SummerPromo {...mockProps} />
      </>
    );

    const termslink = queryByTestId('termsLink');
    expect(termslink).toBeInTheDocument();
  });
});
