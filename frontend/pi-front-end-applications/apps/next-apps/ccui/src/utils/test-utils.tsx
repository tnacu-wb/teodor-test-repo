import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { render, RenderOptions } from '@testing-library/react';
import i18n from 'i18next';
import { ReactElement, ReactNode, useState } from 'react';
import { I18nextProvider, initReactI18next } from 'react-i18next';

import { AppData, AppDataProvider } from '~store/AppDataContext';
import { Session, CCUI_ROLES } from '~types/general';

i18n.use(initReactI18next).init({
  lng: 'en',
  fallbackLng: 'en',

  ns: ['common', 'examples'],
  defaultNS: 'common',

  interpolation: {
    escapeValue: false,
  },
});

interface Props {
  children: ReactNode;
  initialAppData?: AppData;
}

function AllTheProviders({ children, initialAppData }: Readonly<Props>) {
  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  const [queryClient, _] = useState(() => new QueryClient());
  return (
    <AppDataProvider initialAppData={initialAppData}>
      <I18nextProvider i18n={i18n}>
        <QueryClientProvider client={queryClient}>{children}</QueryClientProvider>
      </I18nextProvider>
    </AppDataProvider>
  );
}

const customRender = (ui: ReactElement, options?: Omit<RenderOptions, 'wrapper'>) => {
  return render(ui, {
    wrapper: (props) => {
      return AllTheProviders({ ...props, ...options });
    },
  });
};

export const mockGetI18NLabels = () =>
  Promise.resolve({
    isLoading: false,
    isError: false,
    error: { message: '' },
    data: {},
  });

export const mockGetSession = (): Session => ({
  user: {
    wb_account_locale: 'en',
    given_name: 'CCUI',
    family_name: 'Agent1',
    nickname: 'ccui.agent@whitbread.com',
    name: 'CCUI Agent1',
    picture:
      'https://s.gravatar.com/avatar/7a673e71efa2f77a55669af8a0c1dfa3?s=480&r=pg&d=https%3A%2F%2Fcdn.auth0.com%2Favatars%2Fca.png',
    updated_at: '2024-01-16T12:39:35.337Z',
    email: 'ccui.agent@whitbread.com',
    email_verified: false,
    [CCUI_ROLES]: ['ccui_agent'],
  },
  idToken: 'idtoken',
  accessToken: 'accesstoken',
});

export const mockGetProxyOptions = () => ({
  useProxyAPI: true,
  cookie: 'cookie value here',
  host: 'http://localhost:3000',
  accessToken: 'accesstoken',
});

/* eslint-disable import-x/export */
export * from '@testing-library/react';
export { default as userEvent } from '@testing-library/user-event';
export { customRender as render };
/* eslint-enable */
