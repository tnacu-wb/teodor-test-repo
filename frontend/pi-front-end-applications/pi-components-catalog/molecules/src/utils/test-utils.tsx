import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, RenderOptions } from '@testing-library/react';
import i18n from 'i18next';
import React, { FC, ReactElement, useState } from 'react';
import { I18nextProvider, initReactI18next } from 'react-i18next';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',

  ns: ['common', 'examples'],
  defaultNS: 'common',

  interpolation: {
    escapeValue: false,
  },

  resources: {
    en: {
      common: {},
      examples: {},
    },
    de: {
      common: {},
      examples: {},
    },
  },
});

const AllTheProviders: FC<any> = ({ children }) => {
  const [queryClient] = useState(() => new QueryClient());
  return (
    <I18nextProvider i18n={i18n}>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </I18nextProvider>
  );
};

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) => {
  return render(ui, {
    wrapper: (props) => {
      return AllTheProviders({ ...props, ...options });
    },
  });
};

// eslint-disable-next-line import-x/export -- intentionally overrides render with customRender below
export * from '@testing-library/react';
export { default as userEvent } from '@testing-library/user-event';
// eslint-disable-next-line import-x/export -- intentionally overrides render with customRender below
export { customRender as render };
