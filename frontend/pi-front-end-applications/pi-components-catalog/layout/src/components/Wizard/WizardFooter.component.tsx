'use client';

import { Button } from '@whitbread-eos/atoms/ui';
import { cn } from '@whitbread-eos/utils';
import Link from 'next/link';

export type Props = {
  linkLabel?: string;
  linkDisabled?: boolean;
  onLinkClick?: () => void;
  buttonLabel: string;
  buttonDisabled?: boolean;
  onButtonClick?: () => void;
};

export function WizardFooter({
  linkLabel,
  linkDisabled,
  onLinkClick = () => {
    return true;
  },
  buttonLabel,
  buttonDisabled,
  onButtonClick = () => {
    return true;
  },
}: Props) {
  return (
    <div className={cn(containerStyle, linkLabel && linkLayoutStyle)}>
      {linkLabel && (
        <Link
          data-testid="footer-link"
          href="#"
          className={linkStyle}
          onClick={(e) => {
            e.preventDefault();

            if (!linkDisabled) {
              onLinkClick();
            }
          }}
        >
          {linkLabel}
        </Link>
      )}
      <Button
        variant="dialogDefault"
        className={buttonStyle}
        onClick={onButtonClick}
        disabled={buttonDisabled}
        data-testid="footer-button"
      >
        {buttonLabel}
      </Button>
    </div>
  );
}

const containerStyle =
  'flex h-[120px] mobile:h-[88px] mobile:gap-4 w-full border-t border-lightGrey3 justify-center items-center mobile:px-4';
const buttonStyle = 'w-[288px] flex-initial mobile:w-auto mobile:grow h-14';
const linkLayoutStyle = 'justify-between px-[66px]';
const linkStyle = 'flex font-medium text-base underline text-secondaryColor';
