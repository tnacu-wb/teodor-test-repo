import { LOCALES } from '@whitbread-eos/api';
import {
  getRandomTracingId,
  getChannelByToken,
  DEFAULT_TRACING_COOKIE_NAME,
  ID_TOKEN_COOKIE,
} from '@whitbread-eos/utils/server/edge';
import { NextResponse, type NextRequest } from 'next/server';

export const whitelist = [
  'homepage',
  'manage',
  'spending',
  'business-pay',
  'link-innbusiness-account',
  'profile',
  'welcome',
  'account',
  'access-restricted',
  'pay-application-access-restricted',
  'contact-us',
];

export const config = {
  matcher: '/((?!api|static|.*\\..*|_next).*)',
};

export default async function middleware(req: NextRequest) {
  const pathname = req.nextUrl.pathname;

  const segments = pathname.split('/');
  const page = segments[2];
  const locale = segments[1];

  if (!whitelist.includes(page)) {
    return;
  }

  const token = req.headers.get('cookie')?.match(new RegExp(`${ID_TOKEN_COOKIE}=([^;]+)`))?.[1];
  const channel = getChannelByToken(token ?? '');
  if (token && channel === 'PI' && process?.env?.NEXT_PUBLIC_PI_BASE_URL) {
    const piUrl = `${process.env.NEXT_PUBLIC_PI_BASE_URL}/${
      locale === LOCALES.DE ? 'de/de' : 'gb/en'
    }/home.html`;
    return NextResponse.redirect(piUrl);
  }

  const requestHeaders = new Headers(req.headers);
  requestHeaders.set('WB-Url', req.nextUrl.href);

  const response = NextResponse.next({
    request: {
      headers: requestHeaders,
    },
  });
  if (!req.cookies.has(DEFAULT_TRACING_COOKIE_NAME)) {
    const tracingId = getRandomTracingId();
    response.cookies.set(DEFAULT_TRACING_COOKIE_NAME, String(tracingId), {
      httpOnly: false,
      maxAge: 10 * 60 * 60 * 100,
    });
  }

  return response;
}
