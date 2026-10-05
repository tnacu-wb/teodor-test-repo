'use client';

import { Scheme } from '@whitbread-eos/api';
import { Button, WorldlineLink } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import Image from 'next/image';
import { ReactNode } from 'react';

type Props = {
  tetheredGuid: string;
  baseDataTestId: string;
  postUrl?: string;
  icon?: string;
  iconSvg?: ReactNode;
  prefixIconSvg?: ReactNode;
  text: string;
  page: string;
  variant?: string;
  size?: string;
  returnUrl?: string;
  scheme?: Scheme;
  analyticsLogEvent?: string;
  parentButtonStyle?: string;
};

type WlFunction = (event: Event) => void;

export function WordlineButton({
  tetheredGuid,
  baseDataTestId,
  text,
  page,
  postUrl = '',
  icon = '',
  iconSvg = null,
  prefixIconSvg = null,
  variant = 'outline',
  size = 'lg',
  returnUrl = '',
  scheme = 'GB' as Scheme,
  parentButtonStyle = '',
  analyticsLogEvent,
}: Props) {
  const renderButton = (handleClick: WlFunction) => (
    <Button
      variant={variant}
      size={size}
      className={variant === 'link' ? 'inline' : parentButtonStyle || buttonStyle}
      data-testid={`${baseDataTestId}-Button`}
      onClick={(e: Event) => {
        analyticsLogEvent && window._satellite?.track(analyticsLogEvent);
        handleClick(e);
      }}
    >
      {prefixIconSvg}
      <span>{text}</span>
      {icon && <Image alt={text} src={formatIBAssetsUrl(icon)} width={16} height={16} />}
      {iconSvg}
    </Button>
  );

  return (
    <WorldlineLink
      tetheredGuid={tetheredGuid}
      data-testid={`${baseDataTestId}-WL-Link`}
      worldlinePostUrl={postUrl ?? process.env.NEXT_PUBLIC_WORLDLINE_POST_URL}
      worldlineRequestedPage={page}
      worldlineReturnUrl={returnUrl ?? process.env.NEXT_PUBLIC_WORLDLINE_RETURN_URL}
      renderButton={renderButton}
      formClassName={variant === 'link' ? 'inline' : ''}
      scheme={scheme}
    >
      {text}
    </WorldlineLink>
  );
}

const buttonStyle = 'w-full md:w-auto flex flex-row items-center gap-2';
