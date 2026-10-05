import { FT_PI_AUTH0_LOGIN } from '@whitbread-eos/api';
import { getBrowserFtOverrides, getSecureTwoURL } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

interface AuthIframeProps {
  country: string;
  language: string;
  isAuth0Enabled: boolean;
}

export function AuthIframe({ country, language, isAuth0Enabled }: Readonly<AuthIframeProps>) {
  const [resolvedIsAuth0Enabled, setResolvedIsAuth0Enabled] = useState(isAuth0Enabled);

  useEffect(() => {
    setResolvedIsAuth0Enabled(isAuth0Enabled);

    const override = getBrowserFtOverrides()[FT_PI_AUTH0_LOGIN];
    if (override !== undefined) {
      setResolvedIsAuth0Enabled(override);
    }
  }, [isAuth0Enabled]);

  if (resolvedIsAuth0Enabled) {
    return null;
  }

  return (
    <iframe
      src={`${getSecureTwoURL()}/${country}/${language}/common/login.html`}
      style={{ display: 'none' }}
      id="authIframe"
      title="authIframe"
    />
  );
}
