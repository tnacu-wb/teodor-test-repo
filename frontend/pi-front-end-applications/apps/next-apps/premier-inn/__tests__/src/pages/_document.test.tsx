import { render } from '@testing-library/react';
import Document, { DocumentContext } from 'next/document';
import React from 'react';

import MyDocument from '~pages/_document';

jest.mock('next/document', () => {
  const originalModule = jest.requireActual('next/document');
  return {
    __esModule: true,
    ...originalModule,
    Html: ({ children }: any) => <html>{children}</html>,
    Head: ({ children }: any) => <head>{children}</head>,
    Main: (props: any) => <div {...props} />,
    NextScript: (props: any) => <div {...props} />,
  };
});

jest.mock('../../../next-i18next.config', () => ({
  i18n: {
    defaultLocale: 'gb',
  },
}));

describe('Custom Document', () => {
  let ctx: DocumentContext;

  beforeEach(() => {
    ctx = {
      locale: 'gb',
      defaultLocale: 'gb',
      req: {},
      res: {},
      pathname: '',
      query: {},
      asPath: '',
      AppTree: () => null,
      renderPage: jest.fn(),
    } as unknown as DocumentContext;

    jest.spyOn(Document, 'getInitialProps').mockResolvedValueOnce({
      html: '<div></div>',
      head: [],
      styles: [],
    });
  });

  it('should render the custom Document correctly', async () => {
    const initialProps = await MyDocument.getInitialProps(ctx);

    const { container } = render(<MyDocument {...initialProps} />);

    expect(container).toMatchSnapshot();
  });

  it('should set the correct locale in getInitialProps', async () => {
    ctx.locale = 'de';

    const initialProps = await MyDocument.getInitialProps(ctx);
    expect(initialProps.locale).toBe('de-DE');
  });

  it('should fallback to default locale in getInitialProps', async () => {
    ctx.locale = undefined;

    const initialProps = await MyDocument.getInitialProps(ctx);
    expect(initialProps.locale).toBe('en-GB');
  });

  it('should fallback to default locale in nextI18NextConfig', async () => {
    ctx.locale = undefined;
    ctx.defaultLocale = undefined;

    const initialProps = await MyDocument.getInitialProps(ctx);
    expect(initialProps.locale).toBe('en-GB');
  });
});
