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
    *.whitbread.co.uk;

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
    cdn-ukwest.onetrust.com
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
    *.3cint.com;

  navigate-to *.premierinn.com
    *.premierinn.digital;
`;

const securityHeaders = [
  {
    key: 'Content-Security-Policy',
    value: ContentSecurityPolicy.replace(/\s{2,}/g, ' ').trim(),
  },
];

// !!! Dont add any Javascript logic in this config method
// Do the JS logics above and use them below like disableSourceMaps
const nextConfig = {
  async redirects() {
    return [
      {
        source: '/',
        destination: '/gb/en',
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
          source: '/:country/:language/:bookingFlowId/ancillaries',
          destination: '/ancillaries?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/:bookingFlowId/guest-details',
          destination: '/guest-details?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/account/dashboard.html',
          destination: '/:country/:language/account/dashboard',
          locale: false,
        },
        {
          source: '/:country/:language/account/register.html',
          destination: '/:country/:language/account/register',
          locale: false,
        },
        {
          source: '/:country/:language/:bookingFlowId/payment',
          destination: '/payment?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/:bookingFlowId/confirmation',
          destination: '/confirmation?bookingFlowId=:bookingFlowId',
          locale: false,
        },
        {
          source: '/:country/:language/calendar:ext(\\.html)?',
          destination: '/:country/:language/price-finder',
          locale: false,
        },
        {
          source: '/:country/:language/calendar/:path*',
          destination: '/:country/:language/price-finder/:path*',
          locale: false,
        },
        {
          source: '/:country/:language/kalender:ext(\\.html)?',
          destination: '/:country/:language/price-finder',
          locale: false,
        },
        {
          source: '/:country/:language/kalender/:path*',
          destination: '/:country/:language/price-finder/:path*',
          locale: false,
        },
        {
          source: '/:country/:language/addtowallet/apple',
          destination: '/:country/:language/addtowallet',
          locale: false,
        },
        {
          source: '/:country/:language/why/groups/form.html',
          destination: '/:country/:language/group-bookings',
          locale: false,
        },
        {
          source: '/:country/:language/common/login.html',
          destination: '/login.html',
          locale: false,
        },
        {
          source: '/:country/:language/booking/bookingconfig.js',
          destination: '/bookingconfig.js',
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
  optimizeFonts: true,
  swcMinify: true, // Enable SWC minification for better compression
  // Remove console.logs in production (except error and warn)
  compiler: {
    removeConsole:
      process.env.NODE_ENV === 'production'
        ? {
            exclude: ['error', 'warn'],
          }
        : false,
  },
  serverRuntimeConfig: {
    // Will only be available on the server side
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_PUBLIC_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_PUBLIC_SERVER_RESTAURANTS_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_OCCASION_NAME: process.env.NEXT_PUBLIC_OCCASION_NAME,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY,
    NEXT_PUBLIC_REST_API: process.env.NEXT_PUBLIC_REST_API,
    PI_APPD_BRUM_API_KEY: process.env.PI_APPD_BRUM_API_KEY,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_SECURE2_URL: process.env.NEXT_PUBLIC_SECURE2_URL,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `${process.env.NEXT_PUBLIC_ASSETS_URL?.startsWith('http') ? process.env.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC : `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_PUBLIC_GOOGLE_CAPTCHA_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_CAPTCHA_API_KEY,
    NEXT_PUBLIC_DISPLAY_REGISTER_GDP_GB: process.env.NEXT_PUBLIC_DISPLAY_REGISTER_GDP_GB,
    NEXT_PUBLIC_DISPLAY_REGISTER_GDP_DE: process.env.NEXT_PUBLIC_DISPLAY_REGISTER_GDP_DE,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_ENABLE_AMEND_PAY_NOW_PI_BB: process.env.NEXT_ENABLE_AMEND_PAY_NOW_PI_BB,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_PUBLIC_REGISTER_LOGIN_DELAY: process.env.NEXT_PUBLIC_REGISTER_LOGIN_DELAY,
    NEXT_PUBLIC_REGISTRATION_REDIRECT_GB: process.env.NEXT_PUBLIC_REGISTRATION_REDIRECT_GB,
    NEXT_PUBLIC_REGISTRATION_REDIRECT_DE: process.env.NEXT_PUBLIC_REGISTRATION_REDIRECT_DE,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_PUBLIC_APPLEPAY_SCRIPT_URL: process.env.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL,
    NEXT_IMAGE_UNOPTIMIZED: process.env.NEXT_IMAGE_UNOPTIMIZED,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_PUBLIC_FIREBASE_API_KEY: process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
    NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
    NEXT_PUBLIC_FIREBASE_DATABASE_URL: process.env.NEXT_PUBLIC_FIREBASE_DATABASE_URL,
    NEXT_PUBLIC_FIREBASE_PROJECT_ID: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
    NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET,
    NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
    NEXT_PUBLIC_FIREBASE_APP_ID: process.env.NEXT_PUBLIC_FIREBASE_APP_ID,
    NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID: process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID,
    NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY: process.env.NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY,
    NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE: process.env.NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_HOTELS_API_URL: process.env.NEXT_PUBLIC_HOTELS_API_URL,
    NEXT_PUBLIC_AMAZON_CHAT_URL_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_URL_GB,
    NEXT_PUBLIC_AMAZON_CHAT_ID_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_ID_GB,
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB,
    NEXT_PUBLIC_AMAZON_CHAT_URL_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_URL_DE,
    NEXT_PUBLIC_AMAZON_CHAT_ID_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_ID_DE,
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE,
    NEXT_PUBLIC_AMAZON_CHAT_ENABLED: process.env.NEXT_PUBLIC_AMAZON_CHAT_ENABLED,
    NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL: process.env.NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL,
    NEXT_PUBLIC_DLP_AKAMAI_CACHE_TTL: process.env.NEXT_PUBLIC_DLP_AKAMAI_CACHE_TTL,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_FS_SEO_BREADCRUMB_PI: process.env.NEXT_FS_SEO_BREADCRUMB_PI,
    NEXT_FS_META_ARRIVAL_DAY_KEYWORD_PI: process.env.NEXT_FS_META_ARRIVAL_DAY_KEYWORD_PI,
    NEXT_PUBLIC_GOOGLE_MAP_ID_DLP: process.env.NEXT_PUBLIC_GOOGLE_MAP_ID_DLP,
    NEXT_PUBLIC_GOOGLE_MAP_ID_SRP: process.env.NEXT_PUBLIC_GOOGLE_MAP_ID_SRP,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION: process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
    NEXT_PUBLIC_AUTH0_CONNECTION: process.env.NEXT_PUBLIC_AUTH0_CONNECTION,
    NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API: process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API,
  },
  publicRuntimeConfig: {
    // Will be available on both server and client
    NEXT_PUBLIC_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT: process.env.NEXT_PUBLIC_RESTAURANTS_GRAPHQL_ENDPOINT,
    NEXT_PUBLIC_OCCASION_NAME: process.env.NEXT_PUBLIC_OCCASION_NAME,
    NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC: `${process.env.NEXT_PUBLIC_ASSETS_URL?.startsWith('http') ? process.env.NEXT_PUBLIC_ASSETS_URL_WITHOUT_BASIC : `https://${process.env.NEXT_PUBLIC_ASSETS_URL}`}`,
    NEXT_PUBLIC_ASSETS_URL: process.env.NEXT_PUBLIC_ASSETS_URL,
    NEXT_PUBLIC_ROUTE_PREFIX: process.env.NEXT_PUBLIC_ROUTE_PREFIX,
    NEXT_PUBLIC_GOOGLE_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_MAPS_API_KEY,
    NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_STATIC_MAPS_API_KEY,
    NEXT_PUBLIC_REST_API: process.env.NEXT_PUBLIC_REST_API,
    PI_APPD_BRUM_API_KEY: process.env.PI_APPD_BRUM_API_KEY,
    NEXT_PUBLIC_SNOWDROP_BASE_URL: process.env.NEXT_PUBLIC_SNOWDROP_BASE_URL,
    NEXT_PUBLIC_SECURE2_URL: process.env.NEXT_PUBLIC_SECURE2_URL,
    NEXT_PUBLIC_COOKIES_DOMAIN: process.env.NEXT_PUBLIC_COOKIES_DOMAIN,
    NEXT_PUBLIC_GOOGLE_CAPTCHA_API_KEY: process.env.NEXT_PUBLIC_GOOGLE_CAPTCHA_API_KEY,
    NEXT_PUBLIC_DISPLAY_REGISTER_GDP_GB: process.env.NEXT_PUBLIC_DISPLAY_REGISTER_GDP_GB,
    NEXT_PUBLIC_DISPLAY_REGISTER_GDP_DE: process.env.NEXT_PUBLIC_DISPLAY_REGISTER_GDP_DE,
    NEXT_IMAGE_UNOPTIMIZED: process.env.NEXT_IMAGE_UNOPTIMIZED,
    NEXT_ENABLE_AMEND_PAY_NOW_PI_BB: process.env.NEXT_ENABLE_AMEND_PAY_NOW_PI_BB,
    NEXT_FS_SILENT_SUBSTITUTION: process.env.NEXT_FS_SILENT_SUBSTITUTION,
    NEXT_APP_STATIC_CONTENT_CACHE_TTL: process.env.NEXT_APP_STATIC_CONTENT_CACHE_TTL,
    NEXT_PUBLIC_LOG_ENDPOINT: process.env.NEXT_PUBLIC_LOG_ENDPOINT,
    NEXT_PUBLIC_REGISTER_LOGIN_DELAY: process.env.NEXT_PUBLIC_REGISTER_LOGIN_DELAY,
    NEXT_PUBLIC_REGISTRATION_REDIRECT_GB: process.env.NEXT_PUBLIC_REGISTRATION_REDIRECT_GB,
    NEXT_PUBLIC_REGISTRATION_REDIRECT_DE: process.env.NEXT_PUBLIC_REGISTRATION_REDIRECT_DE,
    NEXT_OPENSEARCH_LOGS: process.env.NEXT_OPENSEARCH_LOGS,
    NEXT_PUBLIC_APPLEPAY_SCRIPT_URL: process.env.NEXT_PUBLIC_APPLEPAY_SCRIPT_URL,
    NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL: process.env.NEXT_APP_UNLEASH_DEFINITIONS_CACHE_TTL,
    NEXT_PUBLIC_FIREBASE_API_KEY: process.env.NEXT_PUBLIC_FIREBASE_API_KEY,
    NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN: process.env.NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN,
    NEXT_PUBLIC_FIREBASE_DATABASE_URL: process.env.NEXT_PUBLIC_FIREBASE_DATABASE_URL,
    NEXT_PUBLIC_FIREBASE_PROJECT_ID: process.env.NEXT_PUBLIC_FIREBASE_PROJECT_ID,
    NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET: process.env.NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET,
    NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID: process.env.NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID,
    NEXT_PUBLIC_FIREBASE_APP_ID: process.env.NEXT_PUBLIC_FIREBASE_APP_ID,
    NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID: process.env.NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID,
    NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY: process.env.NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY,
    NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE: process.env.NEXT_PUBLIC_BREAKFAST_PROMO_PACKAGE,
    NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE: process.env.NEXT_PUBLIC_APPLEPAY_GOOGLEPAY_ENABLE,
    NEXT_PUBLIC_HOTELS_API_URL: process.env.NEXT_PUBLIC_HOTELS_API_URL,
    NEXT_PUBLIC_AMAZON_CHAT_URL_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_URL_GB,
    NEXT_PUBLIC_AMAZON_CHAT_ID_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_ID_GB,
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB: process.env.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_GB,
    NEXT_PUBLIC_AMAZON_CHAT_URL_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_URL_DE,
    NEXT_PUBLIC_AMAZON_CHAT_ID_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_ID_DE,
    NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE: process.env.NEXT_PUBLIC_AMAZON_CHAT_SNIPPET_ID_DE,
    NEXT_PUBLIC_AMAZON_CHAT_ENABLED: process.env.NEXT_PUBLIC_AMAZON_CHAT_ENABLED,
    NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL: process.env.NEXT_PUBLIC_AMAZON_CHAT_AUTH_URL,
    NEXT_PUBLIC_DLP_AKAMAI_CACHE_TTL: process.env.NEXT_PUBLIC_DLP_AKAMAI_CACHE_TTL,
    NEXT_PUBLIC_ACCOUNT_SERVICE: process.env.NEXT_PUBLIC_ACCOUNT_SERVICE,
    NEXT_PUBLIC_APP_NAME: process.env.NEXT_PUBLIC_APP_NAME,
    NEXT_PUBLIC_APOLLO_CLIENT_VERSION: packageJson.version,
    NEXT_FS_SEO_BREADCRUMB_PI: process.env.NEXT_FS_SEO_BREADCRUMB_PI,
    NEXT_FS_META_ARRIVAL_DAY_KEYWORD_PI: process.env.NEXT_FS_META_ARRIVAL_DAY_KEYWORD_PI,
    NEXT_PUBLIC_GOOGLE_MAP_ID_DLP: process.env.NEXT_PUBLIC_GOOGLE_MAP_ID_DLP,
    NEXT_PUBLIC_GOOGLE_MAP_ID_SRP: process.env.NEXT_PUBLIC_GOOGLE_MAP_ID_SRP,
    NEXT_PUBLIC_REDIS_HOST: process.env.NEXT_PUBLIC_REDIS_HOST,
    NEXT_PUBLIC_REDIS_PORT: process.env.NEXT_PUBLIC_REDIS_PORT,
    NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT:
      process.env.NEXT_ENABLE_SERVER_APOLLO_GRAPHQL_ENDPOINT,
    NEXT_OVERWRITE_METARATE: process.env.NEXT_OVERWRITE_METARATE,
    NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION: process.env.NEXT_PUBLIC_ADD_TO_WALLET_ENCRYPTION,
    NEXT_PUBLIC_DYNATRACE_GB: process.env.NEXT_PUBLIC_DYNATRACE_GB,
    NEXT_PUBLIC_DYNATRACE_DE: process.env.NEXT_PUBLIC_DYNATRACE_DE,
    NEXT_PUBLIC_AUTH0_CONNECTION: process.env.NEXT_PUBLIC_AUTH0_CONNECTION,
    NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API: process.env.NEXT_PUBLIC_PAYMENT_ORCHESTRATION_API,
    NEXT_PUBLIC_DATATRANS_SECURE_FIELDS_URL: process.env.NEXT_PUBLIC_DATATRANS_SECURE_FIELDS_URL,
    NEXT_PUBLIC_DATATRANS_PAYPAL_BUTTON_URL: process.env.NEXT_PUBLIC_DATATRANS_PAYPAL_BUTTON_URL,
    NEXT_PUBLIC_DATATRANS_PAYMENT_BUTTON_URL: process.env.NEXT_PUBLIC_DATATRANS_PAYMENT_BUTTON_URL,
    NEXT_PUBLIC_DATATRANS_MERCHANT_ID: process.env.NEXT_PUBLIC_DATATRANS_MERCHANT_ID,
    NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID: process.env.NEXT_PUBLIC_GOOGLE_PAY_MERCHANT_ID,
    NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL: process.env.NEXT_PUBLIC_ONE_TRUST_SCRIPT_URL,
    NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB: process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_GB,
    NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE: process.env.NEXT_PUBLIC_ONE_TRUST_DOMAIN_SCRIPT_DE,
  },
  httpAgentOptions: {
    keepAlive: true,
  },
  outputFileTracing: true,
  typescript: {
    ignoreBuildErrors: NEXTJS_IGNORE_TYPECHECK,
  },
  eslint: {
    ignoreDuringBuilds: NEXTJS_IGNORE_ESLINT,
    dirs: ['src'],
  },
  images: {
    domains: [
      'badly-lover-replaced-garage.trycloudflare.com',
      'www.premierinn.com',
      'premierinn.com',
      'secure2.premierinn.com',
      'qagreen.premierinn.digital',
      'www.qagreen.premierinn.digital',
      'https://www.dev.premierinn.digital/',
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
    // This has to be enabled when the custom loader is used across all environments
    // loader: 'custom',
    // loaderFile: './imageLoader.js',
  },
  output: 'standalone',
  experimental: {
    outputFileTracingRoot: path.join(__dirname, '../../../'),
  },
  env: {
    APP_NAME: packageJson.name,
    APP_VERSION: packageJson.version,
    BUILD_TIME: new Date().getTime().toString(10),
  },
  i18n,
  serverExternalPackages: ['unleash-client'],
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
