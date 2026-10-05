'use client';

import { Scheme } from '@whitbread-eos/api';
import { getCookie, ID_TOKEN_COOKIE, useTranslation } from '@whitbread-eos/utils';
import { businessTetherLogin } from '@whitbread-eos/utils/server';
import { ReactElement, useRef, useState, MouseEvent } from 'react';

interface Props {
  tetheredGuid: string;
  children: React.ReactNode;
  className?: string;
  worldlinePostUrl: string;
  worldlineRequestedPage: string;
  worldlineReturnUrl: string;
  formClassName?: string;
  renderButton?: (onClick: RenderButtonFunction) => ReactElement;
  scheme?: Scheme;
}

type RenderButtonFunction = (e: MouseEvent<HTMLElement>) => void;

export function WorldlineLink({
  tetheredGuid,
  children,
  className,
  worldlinePostUrl,
  worldlineRequestedPage,
  worldlineReturnUrl,
  formClassName = '',
  renderButton = undefined,
  scheme = 'GB' as Scheme,
}: Props) {
  const [showError, setShowError] = useState(false);
  const formRef = useRef<HTMLFormElement>(null);
  const { t } = useTranslation(['layout']);

  const token = getCookie(ID_TOKEN_COOKIE);
  const returnUrlText = t('layout.innbusinessLayout.backToPremierInnBusiness');

  const handleLinkClick = async (e: React.MouseEvent<HTMLElement>) => {
    e.preventDefault();

    if (!formRef.current) {
      return;
    }

    try {
      const data = await businessTetherLogin(token, tetheredGuid, scheme);

      formRef.current['TokenSessionId'].value = data.sessionId;
      formRef.current['TokenTimestamp'].value = data.timestamp;
      formRef.current['TokenNonce'].value = data.nonce;
      formRef.current['TokenHash'].value = data.hash;
      formRef.current.submit();
    } catch (error) {
      setShowError(true);
    }
  };

  return (
    <form method="POST" action={worldlinePostUrl} ref={formRef} className={formClassName}>
      <input name="TokenSessionId" type="hidden" />
      <input name="TokenTimestamp" type="hidden" />
      <input name="TokenNonce" type="hidden" />
      <input name="TokenHash" type="hidden" />
      <input name="EndWebSessionOnReturn" type="hidden" value="true" />
      <input name="RequestedPage" type="hidden" defaultValue={worldlineRequestedPage} />
      <input name="ReturnUrlText" type="hidden" defaultValue={returnUrlText} />
      <input name="ReturnUrl" type="hidden" defaultValue={worldlineReturnUrl} />
      {renderButton ? (
        renderButton(handleLinkClick)
      ) : (
        <button type="button" className={className} onClick={(e) => handleLinkClick(e)}>
          {children}
        </button>
      )}
      {showError && <p data-testid="error">Error logging in</p>}
    </form>
  );
}
