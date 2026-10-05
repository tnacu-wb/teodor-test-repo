import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { FT_PI_RECAPTCHA_REGISTER } from '@whitbread-eos/api';
import { useFeatureToggle } from '@whitbread-eos/utils';
import getConfig from 'next/config';
import { NextRouter } from 'next/router';
import { GoogleReCaptchaProvider } from 'react-google-recaptcha-v3';

import RegisterPagePi from './page.pi';

interface Props {
  queryClient: QueryClient;
  router: NextRouter;
}

export default function Page({ queryClient, router }: Readonly<Props>) {
  const { publicRuntimeConfig = {} } = getConfig() || {};
  const { [FT_PI_RECAPTCHA_REGISTER]: isRecaptchaEnabled } = useFeatureToggle();
  return <QueryClientProvider client={queryClient}>{renderPage()}</QueryClientProvider>;

  function renderPage() {
    return isRecaptchaEnabled ? (
      <GoogleReCaptchaProvider
        reCaptchaKey={publicRuntimeConfig?.NEXT_PUBLIC_GOOGLE_CAPTCHA_API_KEY}
      >
        <RegisterPagePi queryClient={queryClient} router={router} />
      </GoogleReCaptchaProvider>
    ) : (
      <RegisterPagePi queryClient={queryClient} router={router} />
    );
  }
}
