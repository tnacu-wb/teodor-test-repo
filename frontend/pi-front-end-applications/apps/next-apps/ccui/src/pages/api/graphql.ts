import { NextApiRequest, NextApiResponse } from 'next';
import httpProxyMiddleware from 'next-http-proxy-middleware';
import getConfig from 'next/config';

import { auth0 } from '../../lib/auth0';

export default async (req: NextApiRequest, res: NextApiResponse) => {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const session = await auth0.getSession(req);

  const requestHeaders: {
    Authorization?: string;
    'apollographql-client-name'?: string;
    'apollographql-client-version'?: string;
  } = {
    'apollographql-client-name': publicRuntimeConfig.NEXT_PUBLIC_APP_NAME ?? '',
    'apollographql-client-version': publicRuntimeConfig.NEXT_PUBLIC_APOLLO_CLIENT_VERSION ?? '',
  };

  const currentAuthorization = session?.tokenSet?.accessToken
    ? `Bearer ${session?.tokenSet?.accessToken}`
    : req?.headers?.authorization;

  if (currentAuthorization) {
    requestHeaders.Authorization = currentAuthorization;
  }

  return await httpProxyMiddleware(req, res, {
    // target api url
    target: publicRuntimeConfig.NEXT_PUBLIC_GRAPHQL_ENDPOINT!,
    pathRewrite: [
      {
        // rewrite proxy api path
        patternStr: '^/api/graphql',
        replaceStr: '',
      },
    ],
    headers: requestHeaders,
  });
};
