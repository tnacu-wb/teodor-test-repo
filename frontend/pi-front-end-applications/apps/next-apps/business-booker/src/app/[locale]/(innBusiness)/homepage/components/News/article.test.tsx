import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import { Article, ArticleSkeleton, NewsArticle } from './article';

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
  };
});

const mockArticle: NewsArticle = {
  title: 'Test Title',
  description: 'Test Description',
  image: 'test-image.jpg',
  badge: 'Test Badge',
  link: 'https://example.com',
  linkTarget: '_blank',
  optionalLink: 'https://example.com',
  optionalLinkTarget: '_blank',
  optionalLinkLabel: 'Read More',
};

describe('Article Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders the article with all props', () => {
    const { getByTestId, getByText } = render(
      <Article article={mockArticle} baseDataTestId="test" index={0} />
    );

    expect(getByTestId('test-Article-0')).toBeInTheDocument();
    expect(getByTestId('test-ArticleImage-0')).toBeInTheDocument();
    expect(getByText('Test Title')).toBeInTheDocument();
    expect(getByText('Test Description')).toBeInTheDocument();
    expect(getByText('Test Badge')).toBeInTheDocument();
    expect(getByText('Read More')).toBeInTheDocument();
  });

  it('renders the article without optional props', () => {
    const articleWithoutOptionalProps: NewsArticle = {
      title: 'Test Title',
      description: 'Test Description',
      image: 'test-image.jpg',
    };

    const { getByTestId, getByText, queryByText } = render(
      <Article article={articleWithoutOptionalProps} baseDataTestId="test" index={1} />
    );

    expect(getByTestId('test-Article-1')).toBeInTheDocument();
    expect(getByTestId('test-ArticleImage-1')).toBeInTheDocument();
    expect(getByText('Test Title')).toBeInTheDocument();
    expect(getByText('Test Description')).toBeInTheDocument();
    expect(queryByText('Test Badge')).not.toBeInTheDocument();
    expect(queryByText('Read More')).not.toBeInTheDocument();
  });

  it('renders the ArticleSkeleton', () => {
    const { getByTestId } = render(<ArticleSkeleton />);

    expect(getByTestId('Article-Skeleton')).toBeInTheDocument();
  });
});
