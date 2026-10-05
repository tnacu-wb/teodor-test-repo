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
    *.gbqofs.io;

  font-src 'self'
    https://use.typekit.net
    gateway.foresee.com
    fonts.gstatic.com;

  frame-src *.premierinn.com
    *.premierinn.digital
    cdn.appdynamics.com
    *.demdex.net
    *.3cint.com
    *.whitbread.digital
    *.eckoh.uk;

  img-src *.premierinn.com
    *.premierinn.digital
    data:
    'self'
    maps.googleapis.com
    maps.gstatic.com
    *.ggpht.com
    *.googleadservices.com
    *.google-analytics.com;

  media-src *.premierinn.com;

  script-src *.premierinn.com
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
    *.gbqofs.com;

  style-src 'self'
    'unsafe-inline'
    *.typekit.net
    gateway.foresee.com
    fonts.googleapis.com;

  form-action *.premierinn.com
    *.premierinn.digital
    *.3cint.com
    *.eckoh.uk;

  navigate-to *.premierinn.com
    *.premierinn.digital;
`;

const securityHeaders = [
  {
    key: 'Content-Security-Policy',
    value: ContentSecurityPolicy.replace(/\s{2,}/g, ' ').trim(),
  },
];

const nextConfig = {
  async redirects() {
    return [
      {
        source: '/',
        destination: '/gb/en',
        permanent: true,
      },
    ];
  },
  async rewrites() {
    return {
      beforeFiles: [
        {
          source: '/de/guest-details',
          destination: '/guest-details',
          locale: false,
        },
        {
          source: '/de/payment',
          destination: '/payment',
          locale: false,
        },
        {
          source: '/de/confirmation',
          destination: '/confirmation',
          locale: false,
        },
        {
          source: '/de/amend/payment',
          destination: '/amend/payment',
          locale: false,
        },
      ],
      fallback: [
        {
          source: '/:country/:language/home.html',
          destination: '/:country/:language',
          locale: false,
        },
        {
          source: '/:locale*/search.html/:path*',
          destination: '/:locale*/search/:path*',
          locale: false,
        },
        {
          source: '/:country/:language/amend/details.html',
          destination: '/:country/:language/amend/details',
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
          source: '/:country/:language',
          destination: '/',
          locale: false,
        },
        {
          source: '/:country/:language/:path*',
          destination: '/:path*',
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
    MY_SECRET: 'my secret',
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    CCUI_APPD_BRUM_API_KEY: process.env.CCUI_APPD_BRUM_API_KEY,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_FS_MEAL_RESTRICTIONS: process.env.NEXT_FS_MEAL_RESTRICTIONS,
    NEXT_FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES:
      process.env.NEXT_FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES,
    NEXT_FS_DISPLAY_GUEST_ACCOUNT: process.env.NEXT_FS_DISPLAY_GUEST_ACCOUNT,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_FS_CONFIRM_EMAIL_ADDRESS_CCUI: process.env.NEXT_FS_CONFIRM_EMAIL_ADDRESS_CCUI,
    NEXT_FS_SHOW_AMEND_PAYMENT_PAGE: process.env.NEXT_FS_SHOW_AMEND_PAYMENT_PAGE,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
  },
  publicRuntimeConfig: {
    // Will be available on both server and client
    API_ENDPOINT: '/myapi/version/1',
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    CCUI_APPD_BRUM_API_KEY: process.env.CCUI_APPD_BRUM_API_KEY,
    ECKOH_IFRAME_SRC: process.env.ECKOH_IFRAME_SRC,
    ECKOH_CLIENT_ID: process.env.ECKOH_CLIENT_ID,
    ECKOH_ENV: process.env.ECKOH_ENV,
    ECKOH_PROD: process.env.ECKOH_PROD,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_FS_MEAL_RESTRICTIONS: process.env.NEXT_FS_MEAL_RESTRICTIONS,
    NEXT_PUBLIC_REST_API: process.env.NEXT_PUBLIC_REST_API,
    NEXT_FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES:
      process.env.NEXT_FS_ENABLE_SEARCH_ACCOUNT_SAVE_CHANGES,
    NEXT_FS_DISPLAY_GUEST_ACCOUNT: process.env.NEXT_FS_DISPLAY_GUEST_ACCOUNT,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_FS_CONFIRM_EMAIL_ADDRESS_CCUI: process.env.NEXT_FS_CONFIRM_EMAIL_ADDRESS_CCUI,
    NEXT_PUBLIC_LOG_ENDPOINT: process.env.NEXT_PUBLIC_LOG_ENDPOINT,
    NEXT_FS_SHOW_AMEND_PAYMENT_PAGE: process.env.NEXT_FS_SHOW_AMEND_PAYMENT_PAGE,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
  },
  httpAgentOptions: {
    keepAlive: true,
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
  outputFileTracingRoot: path.join(__dirname, '../../../'),
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
    minimumCacheTTL: 300,
  },
  serverExternalPackages: [
    'unleash-client',
  ],
  webpack: (config, { buildId, dev, isServer, defaultLoaders, webpack }) => {
    config.resolve.modules = [path.resolve(__dirname, 'node_modules'), 'node_modules'];
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
