import { FlatCompat } from '@eslint/eslintrc';
import importPlugin from 'eslint-plugin-import-x';
import oxlintPlugin from 'eslint-plugin-oxlint';
import jsoncPlugin from 'eslint-plugin-jsonc';
import prettierPluginRecommended from 'eslint-plugin-prettier/recommended';
import reactPlugin from 'eslint-plugin-react';
import reactHooksPlugin from 'eslint-plugin-react-hooks';
import globals from 'globals';
import tseslint from 'typescript-eslint';

const compat = new FlatCompat({ baseDirectory: import.meta.dirname });

// eslint-plugin-oxlint's flat/import preset uses the old `import/` prefix.
// This project uses eslint-plugin-import-x (registered as `import-x`), so we
// remap the rule keys to `import-x/` so ESLint actually disables the right rules.
const importXOxlintConfig = [oxlintPlugin.configs['flat/import']]
  .flat()
  .map((c) => ({
    ...c,
    rules: Object.fromEntries(
      Object.entries(c.rules).map(([k, v]) => [k.replace(/^import\//, 'import-x/'), v])
    ),
  }));

const TILDE_ALIASES = [
  '~public',
  '~pages',
  '~components',
  '~types',
  '~store',
  '~hooks',
  '~styles',
  '~services',
  '~utils',
  '~queries',
  '~mocks',
  '~page-helper',
  '~lib',
];

export default tseslint.config(
  // Global ignores — replaces all .eslintignore files
  {
    ignores: [
      '**/node_modules',
      '**/dist',
      '**/build',
      '**/.out',
      '**/coverage',
      'apps/next-apps/*/next.config.js',
      'pi-components-catalog/api/src/types/graphql.ts',
      'pi-components-catalog/atoms/src/assets',
    ],
  },

  // Base — applies to all TS/JS files across the monorepo
  {
    files: ['**/*.{ts,tsx,js,jsx}'],
    extends: [...tseslint.configs.recommended],
    plugins: {
      'import-x': importPlugin,
    },
    settings: {
      // Configure the Node resolver to handle TypeScript extensions so that
      // extensionless relative imports (e.g. './Foo' → './Foo.ts') resolve correctly.
      'import-x/extensions': ['.ts', '.tsx', '.js', '.jsx'],
      'import-x/parsers': { '@typescript-eslint/parser': ['.ts', '.tsx'] },
      'import-x/resolver': {
        node: { extensions: ['.js', '.jsx', '.ts', '.tsx'] },
      },
    },
    languageOptions: {
      globals: {
        ...globals.browser,
        JSX: true,
        React: true,
        google: true,
      },
    },
    rules: {
      '@typescript-eslint/no-unused-vars': ['error', { caughtErrors: 'none' }], // caughtErrors defaulted to "none" in @typescript-eslint v5; v8 changed it to "all". Restoring old behaviour.
      'no-console': 'warn',
      '@typescript-eslint/no-explicit-any': 'warn', // this was "warn" in v5 @typescript-eslint/recommended but promoted to "error" in v6+; downgraded here to allow gradual cleanup.
      '@typescript-eslint/no-unused-expressions': 'off', // not present in @typescript-eslint/recommended v5; added in v6+. Turned off to match old behaviour.
      '@typescript-eslint/no-empty-object-type': 'off', // replaces the deprecated ban-types rule; introduced in typescript-eslint v8.0.0.
      '@typescript-eslint/no-duplicate-enum-values': 'warn', // introduced in typescript-eslint recommended in v5.36.0; warn rather than error to allow gradual cleanup.
      ...importPlugin.configs.recommended.rules,
      ...importPlugin.configs.typescript.rules,
      'import-x/namespace': 'off', // TypeScript already validates namespace imports at compile time; this rule produces false positives for CJS packages (e.g. firebase) where static export analysis fails.
      'import-x/default': 'off', // same reason — CJS default exports are not statically analysable; TypeScript handles this.
    },
  },

  // Prettier — global (must come after base to override formatting rules)
  prettierPluginRecommended,

  // Component library packages — React + React Hooks + jest/node globals
  {
    files: ['pi-components-catalog/**/*.{ts,tsx,js,jsx}'],
    plugins: {
      react: reactPlugin,
      'react-hooks': reactHooksPlugin,
    },
    languageOptions: {
      globals: {
        ...globals.jest,
        ...globals.node,
      },
    },
    rules: {
      ...reactPlugin.configs.recommended.rules,
      ...reactHooksPlugin.configs.recommended.rules,
      'react/jsx-uses-react': 'off',
      'react/react-in-jsx-scope': 'off',
      'react/jsx-key': 'off',
      'react/prop-types': 'off', // TypeScript types replace PropTypes; the old config relied on @typescript-eslint/parser implicitly suppressing this, but flat config requires an explicit override.
      'import-x/no-unresolved': 'off',
    },
  },

  // Next.js apps — next/core-web-vitals + tilde alias resolver
  {
    files: ['apps/next-apps/**/*.{ts,tsx,js,jsx}'],
    extends: [...compat.extends('next/core-web-vitals')],
    rules: {
      'import-x/no-unresolved': ['error', { ignore: TILDE_ALIASES }],
    },
  },

  // api package — disable ESLint rules already handled by oxlint (avoids double-reporting)
  ...[
    ...oxlintPlugin.buildFromOxlintConfigFile(
      new URL('./pi-components-catalog/api/oxlint.json', import.meta.url).pathname
    ),
    oxlintPlugin.configs['flat/typescript'],
    oxlintPlugin.configs['flat/react'],
    oxlintPlugin.configs['flat/react-hooks'],
    oxlintPlugin.configs['flat/nextjs'],
    ...importXOxlintConfig,
  ]
    .flat()
    .map((config) => ({ ...config, files: ['pi-components-catalog/api/**/*.{ts,tsx,js,jsx}'] })),


  // JSONC / JSON files
  ...jsoncPlugin.configs['flat/recommended-with-jsonc']
);
