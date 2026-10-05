import '@testing-library/jest-dom';
import { useTranslation } from '@whitbread-eos/utils';
import React from 'react';

import { render } from '../../utils/test-utils';
import Footer from './Footer.component';

jest.mock('@whitbread-eos/utils', () => {
  const actual = jest.requireActual('@whitbread-eos/utils');
  return {
    ...actual,
    cn: jest.fn((...args) => args.filter(Boolean).join(' ')),
    formatIBAssetsUrl: () => {
      return '/';
    },
    renderSanitizedHtml: (html: string) => html,
    useTranslation: jest.fn(() => {
      return {
        t: jest.fn().mockImplementation((str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [
                  {
                    name: 'About us',
                    linkItems: [
                      {
                        linkSrc:
                          'https://www.dit.premierinn.digital/gb/en/business-booker/why.html?intcmp=bbfooter.html',
                        openInNewTab: false,
                        name: "Why we're Premier",
                      },
                      {
                        linkSrc:
                          'https://www.dit.premierinn.digital/gb/en/business-booker/why/rates.html?intcmp=bbfooter.html',
                        openInNewTab: false,
                        name: 'Our rates',
                      },
                      {
                        linkSrc:
                          'https://www.dit.premierinn.digital/gb/en/business-booker/why/cleanliness.html?intcmp=bbfooter.html',
                        openInNewTab: false,
                        name: 'Premier Inn CleanProtect™',
                      },
                      {
                        linkSrc:
                          'https://www.dit.premierinn.digital/gb/en/business-booker/why/force-for-good.html?intcmp=bbfooter.html',
                        openInNewTab: false,
                        name: 'Force for Good',
                      },
                      {
                        linkSrc:
                          'https://www.dit.premierinn.digital/gb/en/business-booker/why/gosh-childrens-charity.html?intcmp=bbfooter.html',
                        openInNewTab: false,
                        name: 'Great Ormond Street',
                      },
                    ],
                  },
                ],
                name: 'Business Booker',
              },
            ];
          }
          if (str === 'footer.bottomLinks') {
            return [
              {
                linkSrc: '/gb/en/a.html',
                openInNewTab: false,
                name: 'InnBusiness Terms & Conditions',
              },
            ];
          }
          if (str === 'footer.socialMediaIcons') {
            return [
              {
                linkSrc: 'https://www.facebook.com/premierinn',
                iconSrc: '/content/dam/icons/resources/social/facebook-dark-square-small.svg',
                label: 'Facebook icon',
              },
            ];
          }
          return str;
        }),
      };
    }),
  };
});

describe('Footer component', () => {
  afterEach(() => {
    jest.clearAllMocks();
  });
  it('should render Footer component', () => {
    const { getByTestId } = render(<Footer />);
    expect(getByTestId('IB-Footer')).toBeInTheDocument();
  });

  it('should render correct number of links when footer.tabs has linkItems', () => {
    const { getByTestId } = render(<Footer />);
    expect(
      getByTestId('IB-Footer').querySelector('div:first-child')?.querySelectorAll('a')
    ).toHaveLength(5);
  });

  it('should not render links when footer.tabs has no linkItems', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [
                  {
                    name: 'About us',
                    linkItems: [],
                  },
                ],
                name: 'Business Booker',
              },
            ];
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    const footerElement = getByTestId('IB-Footer');
    expect(footerElement.querySelector('div:first-child')?.querySelectorAll('a')).toHaveLength(0);
  });

  it('should not render links when footer.tabs is undefined', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return undefined;
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    const footerElement = getByTestId('IB-Footer');
    expect(footerElement.querySelector('div:first-child')?.querySelectorAll('a')).toHaveLength(0);
  });

  it('should render correct number of links elements when multiple columns exist', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [
                  {
                    name: 'About us',
                    linkItems: [
                      {
                        linkSrc: 'https://example.com',
                        openInNewTab: false,
                        name: 'Example Link 1',
                      },
                    ],
                  },
                  {
                    name: 'Contact us',
                    linkItems: [
                      {
                        linkSrc: 'https://example.com/contact',
                        openInNewTab: false,
                        name: 'Example Link 2',
                      },
                    ],
                  },
                ],
                name: 'Business Booker',
              },
            ];
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    const footerElement = getByTestId('IB-Footer');
    expect(footerElement.querySelector('div:first-child')?.querySelectorAll('a')).toHaveLength(2);
  });

  it('should handle case where there are no columns', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [],
                name: 'Business Booker',
              },
            ];
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    expect(
      getByTestId('IB-Footer').querySelector('div:first-child')?.querySelectorAll('a')
    ).toHaveLength(0);
  });

  it('should handle case where there are no bottomLinks', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [
                  {
                    name: 'About us',
                    linkItems: [
                      {
                        linkSrc: 'https://example.com',
                        openInNewTab: false,
                        name: 'Example Link',
                      },
                    ],
                  },
                ],
                name: 'Business Booker',
              },
            ];
          }
          if (str === 'footer.bottomLinks') {
            return [];
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    expect(getByTestId('IB-Footer-Quick-Links').querySelectorAll('a')).toHaveLength(0);
  });

  it('should handle case where there are no socialMediaIcons', () => {
    (useTranslation as jest.Mock).mockImplementation(
      jest.fn(() => ({
        t: (str) => {
          if (str === 'footer.tabs') {
            return [
              {
                intro: {
                  name: '',
                  description: '',
                },
                columns: [
                  {
                    name: 'About us',
                    linkItems: [
                      {
                        linkSrc: 'https://example.com',
                        openInNewTab: false,
                        name: 'Example Link',
                      },
                    ],
                  },
                ],
                name: 'Business Booker',
              },
            ];
          }
          if (str === 'footer.bottomLinks') {
            return [
              {
                linkSrc: '/gb/en/a.html',
                openInNewTab: false,
                name: 'InnBusiness Terms & Conditions',
              },
            ];
          }
          if (str === 'footer.socialMediaIcons') {
            return [];
          }
          return str;
        },
      }))
    );

    const { getByTestId } = render(<Footer />);
    expect(getByTestId('IB-Social-Media-Links').querySelectorAll('a')).toHaveLength(0);
  });
});
