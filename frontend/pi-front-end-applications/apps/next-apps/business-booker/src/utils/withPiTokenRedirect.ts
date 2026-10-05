import { getServerSideCustomLocale, ID_TOKEN_COOKIE } from '@whitbread-eos/utils';
import { getChannelByToken } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSideProps, GetServerSidePropsContext, GetServerSidePropsResult } from 'next';

/**
 * Higher-order function for Next.js `getServerSideProps` that checks for a specific ID token cookie.
 * If the cookie is present and the associated channel is 'PI' (Premier Inn app), the user is redirected
 * to the Premier Inn homepage for their locale. Otherwise, the original `getServerSideProps` is executed.
 */
export default function withPiTokenRedirect<P extends { [key: string]: any } = any>(
  getServerSideProps: GetServerSideProps<P>
): GetServerSideProps<P> {
  return async function wrappedGetServerSideProps(
    context: GetServerSidePropsContext
  ): Promise<GetServerSidePropsResult<P>> {
    const cookies = new Cookies(context.req, context.res);
    const idTokenCookie = cookies.get(ID_TOKEN_COOKIE);
    const { country, language } = getServerSideCustomLocale(context.locale ?? 'gb');
    if (idTokenCookie && process?.env?.NEXT_PUBLIC_PI_BASE_URL) {
      const channel = getChannelByToken(idTokenCookie);
      if (channel === 'PI') {
        return {
          redirect: {
            destination: `${process.env.NEXT_PUBLIC_PI_BASE_URL}/${country}/${language}/home.html`,
            permanent: false,
          },
        };
      }
    }
    return getServerSideProps(context);
  };
}
