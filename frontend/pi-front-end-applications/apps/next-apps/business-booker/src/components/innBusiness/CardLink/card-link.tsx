'use client';

import { Button } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils/server';
import { SquareArrowOutUpRight } from 'lucide-react';
import Image from 'next/image';
import Link from 'next/link';

export interface CardLinkProps {
  icon: string;
  alt: string;
  title: string;
  subtitle: string;
  href?: string;
  isExternalHref?: boolean;
  onClick?: (e: React.MouseEvent<HTMLAnchorElement>) => void;
  baseDataTestId: string;
  variant?: 'default' | 'button-link';
  buttonText?: string;
}

const CardLink = ({
  icon,
  baseDataTestId,
  alt,
  title,
  subtitle,
  href,
  onClick,
  isExternalHref = false,
  variant = 'default',
  buttonText,
}: CardLinkProps) => {
  const renderCard = () => {
    return (
      <div
        className={`${cardStyle} ${
          variant === 'default' && 'hover:shadow-[0_0.5rem_0.938rem_0.063rem_rgba(0,0,0,0.15)]'
        }`}
        data-testid={`${baseDataTestId}-CardWrapper`}
      >
        <div className={cardHeader}>
          <Image src={formatIBAssetsUrl(icon)} alt={alt} width={48} height={48} />
          {isExternalHref && (
            <div className={externalLinkIconWrapper}>
              <SquareArrowOutUpRight
                className={externalLinkIcon}
                data-testid={`${baseDataTestId}-external-icon`}
                size={14}
              />
            </div>
          )}
        </div>
        <h3 className={titleStyle}>{title}</h3>
        <p className={subtitleStyle}>{subtitle}</p>
        {variant === 'button-link' && (
          <div className="flex flex-col h-full justify-end">
            <Link
              data-testid={baseDataTestId}
              href={href ?? ''}
              onClick={handleClick}
              passHref={isExternalHref}
            >
              <Button
                variant="cardLinkButton"
                className="w-full"
                data-testid={`${baseDataTestId}-button`}
              >
                {buttonText}
              </Button>
            </Link>
          </div>
        )}
      </div>
    );
  };

  const handleClick = (e: React.MouseEvent<HTMLAnchorElement>) => {
    if (onClick && isExternalHref) {
      e.preventDefault();
      onClick(e);
    }
  };

  return (
    <>
      {variant === 'default' && (
        <Link
          data-testid={baseDataTestId}
          href={href ?? ''}
          onClick={handleClick}
          passHref={isExternalHref}
        >
          {renderCard()}
        </Link>
      )}
      {variant === 'button-link' && renderCard()}
    </>
  );
};

const cardStyle =
  'bg-white border border-lightgrey3 rounded-lg p-6 pt-4 gap-12 h-full w-full hover:shadow-[0_0.5rem_0.938rem_0.063rem_rgba(0,0,0,0.15)] gap-4 flex flex-col';
const titleStyle = 'text-[1.438rem] font-bold text-secondaryColor';
const subtitleStyle = 'text-base font-normal';
const cardHeader = 'flex justify-between';
const externalLinkIconWrapper = 'mt-[0.5rem]';
const externalLinkIcon = 'text-secondaryColor m-[0.313rem]';

export default CardLink;
