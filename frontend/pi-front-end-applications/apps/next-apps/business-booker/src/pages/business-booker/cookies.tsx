import { getServerSideCustomLocale } from '@whitbread-eos/utils';
import { isInnBusinessApp } from '@whitbread-eos/utils/server';
import Cookies from 'cookies';
import { GetServerSidePropsContext } from 'next';
import { useEffect } from 'react';

type Props = {
  consentCookies: {
    consentGiven: boolean;
    permissionExperience: boolean;
    permissionMarketing: boolean;
    permissionPerformance: boolean;
  };
};

export default function CookiesPage({ consentCookies }: Props) {
  useEffect(() => {
    if (window.parent !== window) {
      const targetOrigin = window.location.origin;
      window.parent.postMessage(
        {
          type: 'COOKIES_IFRAME_LOADED',
          consentCookies: consentCookies,
        },
        targetOrigin
      );
    }
  }, [consentCookies]);

  return <></>;
}

CookiesPage.getLayout = function getLayout() {
  return <></>;
};

export async function getServerSideProps({ locale = 'gb', ...props }: GetServerSidePropsContext) {
  const cookies = new Cookies(props.req, props.res);
  const { language, country } = getServerSideCustomLocale(locale);
  const isInnBusiness = isInnBusinessApp(props.req.headers.host);

  const consentCookies = {
    consentGiven: !!cookies.get('consent_cookie') || false,
    permissionExperience: cookies.get('permissionExperience') === 'true',
    permissionMarketing: cookies.get('permissionMarketing') === 'true',
    permissionPerformance: cookies.get('permissionPerformance') === 'true',
  };

  if (!isInnBusiness) {
    return {
      redirect: {
        destination: `/${country}/${language}/business-booker/home.html`,
        permanent: false,
      },
    };
  }

  return { props: { consentCookies } };
}
