'use client';

import { Scheme } from '@whitbread-eos/api';
import { Button } from '@whitbread-eos/atoms/ui';
import { appPreCheck } from '@whitbread-eos/utils/server';
import { useRouter } from 'next/navigation';
import React, { useState, useEffect } from 'react';

type LinkButtonProps = {
  children: React.ReactNode;
  variant: string;
  className?: string;
  scheme: Scheme;
  token: string;
  setIsModalOpen: (isOpen: boolean) => void;
  tabIndex?: number;
};

type appCheckResult = {
  isTetheredUser: boolean;
  applicationGuid?: string;
};

const LinkAccountButton: React.FC<LinkButtonProps> = ({
  children,
  token,
  scheme,
  setIsModalOpen,
  tabIndex,
  ...props
}) => {
  const router = useRouter();

  const [appCheckResult, setAppCheckResult] = useState<appCheckResult | null>(null);

  const handleLinkAccountButtonClick = async (): Promise<void> => {
    if (appCheckResult?.isTetheredUser === false) {
      setIsModalOpen(true);
    } else {
      window?._satellite?.track('linkAccount');
      router.push(`${process?.env?.NEXT_PUBLIC_WORLDLINE_HOST ?? ''}/BBLinkCode.aspx`);
    }
  };

  useEffect(() => {
    const response = appCheckResult;
    const checkApp = async () => {
      const response = await appPreCheck(token, scheme);
      setAppCheckResult(response);
    };
    if (!response) {
      checkApp();
    }
  }, [token, scheme]);

  return (
    <Button
      {...props}
      data-testid="link-account-button"
      variant={props.variant ?? 'default'}
      onClick={handleLinkAccountButtonClick}
      tabIndex={tabIndex}
    >
      {children}
    </Button>
  );
};

export default LinkAccountButton;
