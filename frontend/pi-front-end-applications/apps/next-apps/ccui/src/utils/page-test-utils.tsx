import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, RenderOptions } from '@testing-library/react';
import i18n from 'i18next';
import { ReactElement, ReactNode, useState } from 'react';
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

interface AllTheProvidersProps {
  children: ReactNode;
}

function AllTheProviders({ children }: Readonly<AllTheProvidersProps>) {
  const [queryClient] = useState(() => new QueryClient());
  return (
    <I18nextProvider i18n={i18n as any}>
      <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
    </I18nextProvider>
  );
}

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) => {
  return render(ui, {
    wrapper: (props) => {
      return AllTheProviders({ ...props, ...options });
    },
  });
};

/* eslint-disable import-x/export */
export * from '@testing-library/react';
export { default as userEvent } from '@testing-library/user-event';
export { customRender as render };
/* eslint-enable */
