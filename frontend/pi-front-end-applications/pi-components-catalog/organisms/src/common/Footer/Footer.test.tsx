import '@testing-library/jest-dom';
import { SITE_LEISURE } from '@whitbread-eos/api';

import { render, RenderOptions } from '../../utils/test-utils';
import type { Props } from './Footer.component';
import Footer from './Footer.component';
import FooterContainer from './Footer.container';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => null,
}));

const mockedProps: Props = {
  tabsInfo: [
    {
      name: 'Tab 1',
      intro: {
        name: 'Intro Name Tab 1',
        description: 'Intro Desc Tab 1',
      },

      columns: [
        {
          name: 'Column Title 199',
          linkItems: [
            {
              openInNewTab: true,
              name: 'Link 1',
              linkSrc: '/someSrc',
            },
            {
              openInNewTab: true,
              name: 'Link 2',
              linkSrc: '/someSrc',
            },
          ],
        },
        {
          name: 'Column Title 2',
          linkItems: [
            {
              openInNewTab: true,
              name: 'Link 3',
              linkSrc: '/someSrc',
            },
            {
              openInNewTab: true,
              name: 'Link 4',
              linkSrc: '/someSrc',
            },
          ],
        },
      ],
    },
    {
      name: 'Tab 2',
      intro: {
        name: 'Intro Name Tab 2',
        description: 'Intro Desc Tab 2',
      },

      columns: [
        {
          name: 'Column Title 1',
          linkItems: [
            {
              openInNewTab: true,
              name: 'Link 1',
              linkSrc: '/someSrc',
            },
            {
              openInNewTab: true,
              name: 'Link 2',
              linkSrc: '/someSrc',
            },
          ],
        },
        {
          name: 'Column Title 22',
          linkItems: [
            {
              openInNewTab: true,
              name: 'Link 3',
              linkSrc: '/someSrc',
            },
            {
              openInNewTab: true,
              name: 'Link 4',
              linkSrc: '/someSrc',
            },
          ],
        },
      ],
    },
  ],
  socialIcons: [{ iconSrc: 'iconSrc', label: 'fb', linkSrc: 'linkToFb' }],
  copyrightData: 'copyright data',
  isPI: true,
  language: 'en',
  baseDataTestId: 'FooterTestId',
};

jest.mock('next/config', () => () => ({
  publicRuntimeConfig: {
    NEXT_PUBLIC_ASSETS_URL: 'https://secure2.premierinn.com',
  },
}));

const tabsMock = {
  tabs: [{ ...mockedProps.tabsInfo[0] }],
  socialMediaIcons: {
    socialIcons: [
      { iconSrc: 'iconSrc', label: 'fb', linkSrc: 'linkToFb' },
      { iconSrc: 'iconSrcTwo', label: 'ig', linkSrc: 'linkToIg' },
    ],
  },
};

const mockUseQueryRequest = {
  isLoading: false,
  isError: false,
  data: {
    footer: tabsMock,
  },
  error: '',
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
  useQueryRequest: () => mockUseQueryRequest,
  useMutationRequest: () => ({
    mutation: jest.fn(),
  }),
}));

const tabsWithSingleTitle = [
  ...mockedProps.tabsInfo,
  {
    name: 'Tab 3',
    intro: {
      name: 'Intro Name Tab 3',
      description: 'Intro Desc Tab 3',
    },

    columns: [
      {
        name: '',
        linkItems: [
          {
            openInNewTab: true,
            name: 'Link 1',
            linkSrc: '/someSrc',
          },
          {
            openInNewTab: true,
            name: 'Link 2',
            linkSrc: '/someSrc',
          },
        ],
      },
      {
        name: '',
        linkItems: [
          {
            openInNewTab: true,
            name: 'Link 3',
            linkSrc: '/someSrc',
          },
          {
            openInNewTab: true,
            name: 'Link 4',
            linkSrc: '/someSrc',
          },
        ],
      },
    ],
  },
];
describe('Footer', () => {
  beforeEach(() => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = false;
    mockUseQueryRequest.error = '';
  });

  it('should render the <Footer> components corectly', function () {
    const { queryByTestId, queryAllByTestId } = render(<Footer {...mockedProps} />);

    expect(queryByTestId('FooterTestId-Wrapper')).toBeTruthy();
    expect(queryAllByTestId('FooterTestId-TabContent')).toBeTruthy();
    expect(queryAllByTestId('FooterTestId-TabContent').length).toBe(2);
    expect(queryByTestId('FooterTestId-SocialSection')).toBeTruthy();
  });

  it('should render the <Footer> with german language', function () {
    mockedProps.language = 'de';

    const { queryByTestId, queryAllByTestId } = render(
      <Footer {...mockedProps} tabsInfo={tabsWithSingleTitle} />,
      {
        initialAppData: { screenSize: 'xl' },
      } as Omit<RenderOptions, 'wrapper'>
    );

    expect(queryByTestId('FooterTestId-Wrapper')).toBeTruthy();
    expect(queryAllByTestId('FooterTestId-LinkItem').length).toBe(12);
    expect(queryByTestId('FooterTestId-SocialSection')).toBeTruthy();
  });

  it('should render the <Footer> for ccui properly', function () {
    mockedProps.isPI = false;

    const { queryByTestId } = render(<Footer {...mockedProps} />, {
      initialAppData: { screenSize: 'sm' },
    } as Omit<RenderOptions, 'wrapper'>);

    expect(queryByTestId('FooterTestId-Wrapper')).toBeTruthy();
    expect(queryByTestId('FooterTestId-SocialSection')).toBeFalsy();
  });

  it('should render loading text and hide FooterContainer wrapper when loading', () => {
    mockUseQueryRequest.isLoading = true;
    const { getByTestId, queryByTestId } = render(<FooterContainer isPremierInn />);
    expect(getByTestId('loading-message')).toBeInTheDocument();
    expect(queryByTestId('FooterTestId-Wrapper')).toBeFalsy();
  });

  it('it should render the FooterContainer with error state', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = true;
    mockUseQueryRequest.error = 'error';

    const { getByText } = render(<FooterContainer site={SITE_LEISURE} isPremierInn />);

    expect(getByText('error')).toBeInTheDocument();
  });

  it('it should render the FooterContainer with no error', () => {
    mockUseQueryRequest.isLoading = false;
    mockUseQueryRequest.isError = false;

    const { queryByTestId } = render(
      <>
        <FooterContainer site={SITE_LEISURE} isPremierInn />
        <Footer {...mockedProps} />
      </>
    );

    expect(queryByTestId('FooterTestId-Wrapper')).toBeTruthy();
  });

  it('it should render the FooterContainer if its not PremierInn', () => {
    const { queryByTestId } = render(
      <>
        <FooterContainer site={SITE_LEISURE} />
        <Footer {...mockedProps} isPI={false} language="en" />
      </>
    );

    expect(queryByTestId('FooterTestId-Wrapper')).toBeTruthy();
  });
});
