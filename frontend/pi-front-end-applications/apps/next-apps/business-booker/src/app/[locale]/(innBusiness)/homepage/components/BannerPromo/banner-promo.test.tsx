import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { BannerPromo } from './banner-promo';

const mockProps = {
  language: 'en',
  dataTestId: 'Test123',
  title: 'Title test',
  description: 'Description test',
  link: 'Link test',
  bannerImage: '',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => [],
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    getPathForLocale: () => {
      return '/';
    },
  };
});

describe('BannerPromo', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should render BannerPromo component', async () => {
    const { getByTestId } = render(await BannerPromo(mockProps));

    expect(getByTestId('Test123-BannerPromo')).toBeInTheDocument();
  });

  it('should render BannerPromo component with image and no title/description', async () => {
    mockProps.bannerImage = 'test';
    mockProps.title = '';
    mockProps.description = '';
    const { getByTestId } = render(await BannerPromo(mockProps));

    expect(getByTestId('Test123-BannerPromo')).toBeInTheDocument();
  });

  it('should render BannerPromo component with image and no link', async () => {
    mockProps.bannerImage = 'test';
    mockProps.link = '';
    const { getByTestId } = render(await BannerPromo(mockProps));

    expect(getByTestId('Test123-BannerPromo')).toBeInTheDocument();
  });
});
