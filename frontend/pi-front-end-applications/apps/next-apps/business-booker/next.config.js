/** @type {import('next').NextConfig} */

const pc = require('picocolors');
const path = require('path');
const packageJson = require('./package.json');
const { i18n } = require('./next-i18next.config');
const NEXTJS_IGNORE_ESLINT = process.env.NEXTJS_IGNORE_ESLINT === '1' || false;
const NEXTJS_IGNORE_TYPECHECK = process.env.NEXTJS_IGNORE_TYPECHECK === '1' || false;
const disableSourceMaps = process.env.NEXT_DISABLE_SOURCEMAPS === 'true';

if (disableSourceMaps) {
  console.info(
    `${pc.green(
      'notice'
    )}- Sourcemaps generation have been disabled through NEXT_DISABLE_SOURCEMAPS`
  );
}

const ContentSecurityPolicy = `
  default-src *.premierinn.com
    *.premierinn.digital
    'self'
    *.google-analytics.com
    *.demdex.net
    analytics.foresee.com
    *.eum-appdynamics.com
    *.emergyalabs.com
    maps.googleapis.com
    *.whitbread.co.uk
    *.gbqofs.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.gbqofs.io;

  font-src 'self'
    https://use.typekit.net
    gateway.foresee.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    fonts.gstatic.com;

  frame-src *.premierinn.com
    *.premierinn.digital
    cdn.appdynamics.com
    *.demdex.net
    *.3cint.com
    *.whitbread.digital
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.whitbread.co.uk;

  img-src *.premierinn.com
    *.premierinn.digital
    data:
    'self'
    maps.googleapis.com
    maps.gstatic.com
    *.ggpht.com
    *.googleadservices.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.google-analytics.com;

  media-src *.premierinn.com
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.liveperson.net;

  script-src *.premierinn.com
    *.premierinn.digital
    assets.adobedtm.com
    cdn.appdynamics.com
    'self'
    'unsafe-eval'
    'unsafe-inline'
    *.google-analytics.com
    gateway.foresee.com
    pixel.everesttech.net
    maps.googleapis.com
    *.googleadservices.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.gbqofs.com;

  style-src 'self'
    'unsafe-inline'
    *.typekit.net
    gateway.foresee.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    fonts.googleapis.com;

  form-action *.premierinn.com
    *.premierinn.digital
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.3cint.com;

  navigate-to *.premierinn.com
    *.liveperson.net
    *.lpsnmedia.net
    service.eu1.liveassistfor365.com
    *.premierinn.digital;
`;

const securityHeaders = [
  {
    key: 'Content-Security-Policy',
    value: ContentSecurityPolicy.replace(/\s{2,}/g, ' ').trim(),
  },
];

const REDIRECT_PATHS = ['/en', '/en-gb', '/de-de', '/de', '/gb', '/gb/en', '/de/de'];
const GERMAN_PATHS = ['/de', '/de/de', '/de-de'];

const nextConfig = {
  async redirects() {
    const businessRedirects = REDIRECT_PATHS.map((path) => ({
      source: path,
      destination: GERMAN_PATHS.includes(path) ? '/de-de/homepage' : '/en-gb/homepage',
      permanent: true,
      has: [
        {
          type: 'host',
          value: '(business\\..+)',
        },
      ],
    }));

    return [
      {
        source: '/',
        destination: '/gb/en',
        permanent: true,
        missing: [
          {
            type: 'host',
            value: '(business\\..+)',
          },
        ],
      },
      {
        source: '/',
        destination: '/en-gb/homepage',
        permanent: true,
        has: [
          {
            type: 'host',
            value: '(business\\..+)',
          },
        ],
      },
      ...businessRedirects,
      {
        source: '/homepage',
        destination: '/en-GB/homepage',
        permanent: true,
      },
      {
        source: '/etc/clientlibs/wb/loginauth0.ACSHASH8009ecbae2b022074cd5d9d18b926d9b.js',
        destination: '/loginauth.js',
        permanent: true,
      },
    ];
  },
  async rewrites() {
    return {
      beforeFiles: [
        {
          source: '/de/business-booker/:bookingFlowId/guest-details',
          destination:
            '/business-booker/booking-business/guest-details?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/de/business-booker/:bookingFlowId/payment',
          destination: '/business-booker/booking-business/payment?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/calendar:ext(\\.html)?',
          destination: '/:country/:language/price-finder',
          locale: false,
        },
        {
          source: '/:country/:language/kalender:ext(\\.html)?',
          destination: '/:country/:language/price-finder',
          locale: false,
        },
        {
          source: '/de/business-booker/:bookingFlowId/confirmation',
          destination:
            '/business-booker/booking-business/confirmation?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:locale/account/register-finance',
          destination: '/:locale/account/register?type=BP',
        },
      ],
      fallback: [
        {
          source: '/:country/:language/business-booker/home.html',
          destination: '/:country/:language/',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/common/login.html',
          destination: '/login.html',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/booking/bookingconfig.js',
          destination: '/bookingconfig.js',
          locale: false,
        },
        {
          source: '/:locale*/business-booker/search.html/:path*',
          destination: '/:locale*/business-booker/search/:path*',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/:bookingFlowId/guest-details',
          destination:
            '/:country/:language/business-booker/booking-business/guest-details?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/booking-business/dashboard.html',
          destination: '/:country/:language/business-booker/booking-business/dashboard',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/account/dashboard.html',
          destination: '/:country/:language/business-booker/account/dashboard',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/payment',
          destination: '/:country/:language/business-booker/booking-business/payment',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/confirmation',
          destination: '/:country/:language/business-booker/booking-business/confirmation',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/amend/details.html',
          destination: '/:country/:language/business-booker/amend/details',
          locale: false,
        },
        {
          source: '/:country/:language/business-booker/:path*',
          destination: '/business-booker/:path*',
          locale: false,
        },
        {
          source: '/:country/:language/amend/details.html',
          destination: '/:country/:language/amend/details',
          locale: false,
        },
        {
          source: '/:country/:language/:path*',
          destination: '/:path*',
          locale: false,
        },
        {
          source: '/:country/:language',
          destination: '/',
          locale: false,
        },
        {
          source: '/gb/en',
          destination: '/',
          locale: false,
        },
        {
          source: '/de/de',
          destination: '/',
          locale: false,
        },
      ],
    };
  },
  //TODO: uncomment when tested
  //async headers() {
  //return [
  //{
  //source: '/:path*',
  //headers: securityHeaders,
  //},
  //];
  //},
  reactStrictMode: true,
  productionBrowserSourceMaps: !disableSourceMaps,
  serverRuntimeConfig: {
    // Will only be available on the server side
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY,
    NEXT_PUBLIC_REST_API: process.env.NEXT_PUBLIC_REST_API,
    BB_APPD_BRUM_API_KEY: process.env.BB_APPD_BRUM_API_KEY,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_SECURE2_URL: process.env.NEXT_PUBLIC_SECURE2_URL,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_ENABLE_AMEND_PAY_NOW_PI_BB: process.env.NEXT_ENABLE_AMEND_PAY_NOW_PI_BB,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_FS_ULTIMATE_WIFI: process.env.NEXT_FS_ULTIMATE_WIFI,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_PUBLIC_APPLEPAY_SCRIPT_URL: process.env.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_WORLDLINE_HOST: process.env.NEXT_PUBLIC_WORLDLINE_HOST,
    NEXT_PUBLIC_WORLDLINE_HOST_DE: process.env.NEXT_PUBLIC_WORLDLINE_HOST_DE,
    NEXT_PUBLIC_USER_PILOT_APP_TOKEN: process.env.NEXT_PUBLIC_USER_PILOT_APP_TOKEN,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_PUBLIC_PI_BASE_URL: process.env.NEXT_PUBLIC_PI_BASE_URL,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
  },
  publicRuntimeConfig: {
    // Will be available on both server and client
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_ROUTE_PREFIX: process.env.NEXT_PUBLIC_ROUTE_PREFIX,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY,
    NEXT_PUBLIC_REST_API: process.env.NEXT_PUBLIC_REST_API,
    BB_APPD_BRUM_API_KEY: process.env.BB_APPD_BRUM_API_KEY,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_SECURE2_URL: process.env.NEXT_PUBLIC_SECURE2_URL,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_ENABLE_AMEND_PAY_NOW_PI_BB: process.env.NEXT_ENABLE_AMEND_PAY_NOW_PI_BB,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_PUBLIC_LOG_ENDPOINT: process.env.NEXT_PUBLIC_LOG_ENDPOINT,
    NEXT_FS_ULTIMATE_WIFI: process.env.NEXT_FS_ULTIMATE_WIFI,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_PUBLIC_APPLEPAY_SCRIPT_URL: process.env.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_WORLDLINE_HOST: process.env.NEXT_PUBLIC_WORLDLINE_HOST,
    NEXT_PUBLIC_WORLDLINE_HOST_DE: process.env.NEXT_PUBLIC_WORLDLINE_HOST_DE,
    NEXT_PUBLIC_USER_PILOT_APP_TOKEN: process.env.NEXT_PUBLIC_USER_PILOT_APP_TOKEN,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_PUBLIC_PI_BASE_URL: process.env.NEXT_PUBLIC_PI_BASE_URL,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
    NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL: process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL,
    NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB: process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB,
    NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE: process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE,
  },
  httpAgentOptions: {
    keepAlive: true,
  },
  outputFileTracingRoot: path.join(__dirname, '../../../'),
  experimental: {
    esmExternals: false,
  },
  typescript: {
    ignoreBuildErrors: NEXTJS_IGNORE_TYPECHECK,
  },
  eslint: {
    ignoreDuringBuilds: NEXTJS_IGNORE_ESLINT,
    dirs: ['src'],
  },
  env: {
    APP_NAME: packageJson.name,
    APP_VERSION: packageJson.version,
    BUILD_TIME: new Date().getTime().toString(10),
  },
  i18n,
  output: 'standalone',
  images: {
    domains: [
      'www.premierinn.com',
      'premierinn.com',
      'secure2.premierinn.com',
      'qagreen.premierinn.digital',
      'www.qagreen.premierinn.digital',
      'dev.premierinn.digital',
      'www.dev.premierinn.digital',
      'dit.premierinn.digital',
      'www.dit.premierinn.digital',
      'sit.premierinn.digital',
      'www.sit.premierinn.digital',
      'demo.premierinn.digital',
      'www.demo.premierinn.digital',
      'uat.premierinn.digital',
      'www.uat.premierinn.digital',
      'preprod.premierinn.digital',
      'www.preprod.premierinn.digital',
      'perf.premierinn.digital',
      'www.perf.premierinn.digital',
      'localhost',
    ],
  },
  serverExternalPackages: [
    'unleash-client',
  ],
  // assetPrefix removed: Next.js 15 standalone server doesn't serve static files at the prefixed path.
  // The app runs on its own subdomain so assets are served directly at /_next/static/.
  webpack: (config, { buildId, dev, isServer, defaultLoaders, webpack }) => {
    config.resolve.modules = [path.resolve(__dirname, 'node_modules'), 'node_modules'];
    config.resolveLoader.modules = [path.resolve(__dirname, 'node_modules'), 'node_modules'];
    config.resolve.fallback = {
      ...config.resolve.fallback, // if you miss it, all the other options in fallback, specified
      // by next.js will be dropped. Doesn't make much sense, but how it is
      fs: false, // the solution
      net: false, // the solution
      async_hooks: false, // the solution
      util: false,
      perf_hooks: false,
      diagnostics_channel: false,
      tls: false,
      console: false,
      worker_threads: false,
      http2: false,
      dns: false,
      http: false,
      https: false,
      path: false,
      crypto: false,
      'timers/promises': false,
    };
    config.plugins.push(
      new webpack.NormalModuleReplacementPlugin(/node:/, (resource) => {
        resource.request = resource.request.replace(/^node:/, '');
      })
    );
    return config;
  },
};

if (process.env.ANALYZE === 'true') {
  const withBundleAnalyzer = require('@next/bundle-analyzer')({
    enabled: true,
  });
  module.exports = withBundleAnalyzer(nextConfig);
} else {
  module.exports = nextConfig;
}
