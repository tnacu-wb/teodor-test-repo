import { setCookie } from 'cookies-next';

interface ProxyOptions {
  useProxyAPI?: boolean;
  cookie?: string;
  host?: string;
}

export function setProxyOptionsCookies(proxyOptions: ProxyOptions, props: any) {
  const { req, res } = props || {};
  setCookie('useProxyAPI', proxyOptions.useProxyAPI, { req, res, maxAge: 60 * 60 * 1000 }); // 1h
  setCookie('hostURL', proxyOptions.host, { req, res, maxAge: 60 * 60 * 1000 }); // 1h
}

export function getProxyOptions({ req }: any, session: any) {
  const isLocalhost = req?.headers?.host?.includes('localhost');
  const cookie = req?.headers?.cookie;
  const protocol = `${isLocalhost ? 'http' : 'https'}://`;
  const host = `${protocol}${req?.headers?.host ?? ''}`;

  return {
    useProxyAPI: true,
    cookie,
    host,
    accessToken: session?.tokenSet?.accessToken,
  };
}
