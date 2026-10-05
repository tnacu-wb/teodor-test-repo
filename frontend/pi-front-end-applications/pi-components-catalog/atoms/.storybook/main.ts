import type { StorybookConfig } from '@storybook/react-webpack5';
import path from 'path';
import { fileURLToPath } from 'url';
import webpack from 'webpack';

const __dirname = path.dirname(fileURLToPath(import.meta.url));

const nodeModuleFallbacks = {
  dns: false,
  fs: false,
  http: false,
  net: false,
  path: false,
  stream: false,
  tls: false,
  url: false,
  util: false,
  zlib: false,
  crypto: false,
} as const;

const config: StorybookConfig = {
  framework: {
    name: '@storybook/react-webpack5',
    options: {},
  },
  // Disable composed external Storybooks (e.g. auto-discovered package refs).
  refs: {
    '@chakra-ui/react': {
      disable: true,
    },
  },
  stories: [
    {
      directory: '../src/components/restaurants',
      files: '**/*.stories.@(js|jsx|ts|tsx)',
      titlePrefix: 'Chakra UI/Restaurants',
    },
    {
      directory: '../src/components/ui',
      files: '**/*.stories.@(js|jsx|ts|tsx)',
      titlePrefix: 'Shadcn',
    },
    {
      directory: '../src/components',
      files: '!(restaurants|ui)/**/*.stories.@(js|jsx|ts|tsx)',
      titlePrefix: 'Chakra UI/Premier Inn',
    },
  ],
  addons: ['@storybook/addon-docs', '@storybook/addon-designs'],
  webpackFinal: async (webpackConfig) => {
    webpackConfig.module?.rules?.push({
      test: /\.(ts|tsx|js|jsx)$/,
      exclude: /node_modules/,
      use: {
        loader: 'babel-loader',
        options: {
          // Use the shared catalog Babel config and avoid atoms/babel.config.js, which is
          // intentionally named to prevent Storybook from consuming the atoms build wrapper.
          configFile: '../config/babel.config.js',
        },
      },
    });

    webpackConfig.resolve = webpackConfig.resolve || {};
    webpackConfig.resolve.extensions = [
      '.tsx',
      '.ts',
      '.jsx',
      '.js',
      ...(webpackConfig.resolve.extensions || []),
    ];

    webpackConfig.resolve.alias = {
      ...(webpackConfig.resolve.alias || {}),
      'next/image': path.resolve(__dirname, './next-image-mock.tsx'),
      '@whitbread-eos/utils$': path.resolve(__dirname, './utils-storybook-bridge.ts'),
    };

    webpackConfig.resolve.fallback = {
      ...(Array.isArray(webpackConfig.resolve.fallback) ? {} : webpackConfig.resolve.fallback),
      ...nodeModuleFallbacks,
    };

    webpackConfig.plugins = webpackConfig.plugins || [];
    webpackConfig.plugins.push(
      new webpack.NormalModuleReplacementPlugin(/^node:/, (resource) => {
        resource.request = resource.request.replace(/^node:/, '');
      })
    );

    return webpackConfig;
  },
};

export default config;
