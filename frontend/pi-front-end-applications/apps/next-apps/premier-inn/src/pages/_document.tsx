import { CountryCode, LanguageLocaleCode } from '@whitbread-eos/api';
import Document, { Html, Head, Main, NextScript, DocumentContext } from 'next/document';

import nextI18NextConfig from '../../next-i18next.config';

class MyDocument extends Document {
  static async getInitialProps(ctx: DocumentContext) {
    const initialProps = await Document.getInitialProps(ctx);
    // Get the current language from next-i18next or fallback
    const currentLocale = ctx.locale || ctx.defaultLocale || nextI18NextConfig.i18n.defaultLocale;
    return {
      ...initialProps,
      locale: currentLocale === CountryCode.DE ? LanguageLocaleCode.DE : LanguageLocaleCode.GB,
    };
  }

  render() {
    return (
      <Html lang={this.props.locale}>
        <Head />
        <body>
          <Main />
          <NextScript />
        </body>
      </Html>
    );
  }
}

export default MyDocument;
