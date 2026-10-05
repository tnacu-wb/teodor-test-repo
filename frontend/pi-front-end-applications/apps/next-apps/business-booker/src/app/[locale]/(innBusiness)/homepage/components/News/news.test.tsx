import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';
import { getTranslations } from '@whitbread-eos/utils/server';

import { News } from './news';

jest.mock('@whitbread-eos/utils/server', () => ({
  cn: jest.fn(),
  formatIBAssetsUrl: () => {
    return '/';
  },
  getTranslations: jest.fn(),
  getCountryLanguageByLocale: jest.fn(() => ({ language: 'en' })),
}));

const mockTranslation: { [key: string]: string } = {
  'homepage.home.innbusinessPay.whatsNew.heading': "What's New",
  'homepage.home.innbusinessPay.whatsNew.promo.item1.title': 'Title 1',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.description': 'Description 1',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.image': 'Image 1',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.link': 'Link 1',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.linkTarget': '_blank',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.linkLabel': 'Read more',
  'homepage.home.innbusinessPay.whatsNew.promo.item1.badge': 'New',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.title': 'Title 2',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.description': 'Description 2',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.image': 'Image 2',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.link': 'Link 2',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.linkTarget': '_blank',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.linkLabel': 'Read more',
  'homepage.home.innbusinessPay.whatsNew.promo.item2.badge': 'New',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.title': 'Title 3',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.description': 'Description 3',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.image': 'Image 3',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.link': 'Link 3',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.linkTarget': '_blank',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.linkLabel': 'Read more',
  'homepage.home.innbusinessPay.whatsNew.promo.item3.badge': 'New',
};

const mockProps = {
  locale: LOCALES.EN,
};

describe('News Component', () => {
  beforeEach(() => {
    (getTranslations as jest.Mock).mockResolvedValue({
      t: (key: keyof typeof mockTranslation) => mockTranslation[key],
      translations: { homepage: {} },
    });
  });

  it('should render the News component with articles', async () => {
    const { findByText, findAllByTestId } = render(await News(mockProps));
    const heading = await findByText(
      mockTranslation['homepage.home.innbusinessPay.whatsNew.heading']
    );
    expect(heading).toBeInTheDocument();

    const articles = await findAllByTestId(/WhatsNew-List/);
    expect(articles).toHaveLength(1);
  });

  it('should render the NewsSkeleton component', async () => {
    const { getByText } = render(await News(mockProps));
    const heading = getByText(mockTranslation['homepage.home.innbusinessPay.whatsNew.heading']);
    expect(heading).toBeInTheDocument();
  });
});
