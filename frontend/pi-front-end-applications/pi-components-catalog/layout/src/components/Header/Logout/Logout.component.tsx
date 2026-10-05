'use client';

import { DropdownMenuItem } from '@whitbread-eos/atoms/ui';
import { PIB_MANUAL_LOGOUT_FLAG, useTranslation } from '@whitbread-eos/utils';
import { InnBLink } from 'business-booker/src/components/innBusiness/InnBLink';
import React from 'react';

type Props = {
  isAuthActive: boolean;
  secureUrl: string;
  onLogoutClick: () => void;
  asMenuItem?: boolean;
};

const Logout: React.FC<Props> = ({ isAuthActive, secureUrl, onLogoutClick }: Props) => {
  const { t } = useTranslation();

  const handleLogout = (e: React.MouseEvent) => {
    e.preventDefault();
    if (!isAuthActive) {
      onLogoutClick();
      return;
    }

    sessionStorage?.setItem(PIB_MANUAL_LOGOUT_FLAG, 'true');
    const authIframe = document.getElementById('authIframe') as HTMLIFrameElement;
    if (authIframe?.contentWindow) {
      const message = JSON.stringify({ action: 'logout' });
      authIframe.contentWindow.postMessage(message, secureUrl);
    }
    onLogoutClick();
  };

  return (
    <DropdownMenuItem className="p-0 block w-full h-full" asChild>
      <InnBLink
        href="/logout"
        className="block !text-base px-4 py-2.5 w-full text-left hover:bg-lightGrey5"
        data-testid="Account-Logout-Link"
        onClick={handleLogout}
      >
        {t('content.authentication.logoutButton')}
      </InnBLink>
    </DropdownMenuItem>
  );
};

export default Logout;
