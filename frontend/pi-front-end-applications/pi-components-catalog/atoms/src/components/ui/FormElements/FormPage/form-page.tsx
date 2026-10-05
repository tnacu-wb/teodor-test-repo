'use client';

import { cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import Link from 'next/link';

type Props = {
  baseDataTestId: string;
  title: string;
  children: React.ReactNode;
  backIcon?: string;
  iconClassName?: string;
  backHref?: string;
  isCentered?: boolean;
  onBackClick?: () => void;
  className?: string;
};

export function FormPage({
  baseDataTestId,
  title,
  children,
  backIcon,
  iconClassName,
  backHref,
  isCentered = true,
  onBackClick = () => {
    return;
  },
  className,
}: Props) {
  return (
    <div
      className={cn('form-page', pageStyle(isCentered), className)}
      data-testid={`${baseDataTestId}-page`}
    >
      <div className={containerStyle(isCentered)}>
        <div className={cn(titleWrapper, backIcon && titleWrapperBackStyle)}>
          {backIcon &&
            (backHref ? (
              <span>
                <Link className={backStyle} href={backHref} onClick={onBackClick}>
                  <Image
                    className={iconClassName}
                    alt={'Back'}
                    src={backIcon}
                    width={48}
                    height={48}
                    priority={true}
                    data-testid={`${baseDataTestId}-back-icon`}
                  />
                </Link>
              </span>
            ) : (
              <span className={backStyle}>
                <button onClick={onBackClick}>
                  <Image
                    className={iconClassName}
                    alt={'Back'}
                    src={backIcon}
                    width={48}
                    height={48}
                    priority={true}
                    data-testid={`${baseDataTestId}-back-icon`}
                  />
                </button>
              </span>
            ))}
          <h1 className={h1Style} data-testid={`${baseDataTestId}-title`}>
            {title}
          </h1>
        </div>
        {children}
      </div>
    </div>
  );
}

const pageStyle = (isCentered: boolean) =>
  `${
    isCentered ? '' : 'max-w-[825px] px-[4rem] mobile:px-0'
  } bg-lightGrey5 border-b border-lightGrey3`;
const containerStyle = (isCentered: boolean) =>
  `relative ${
    isCentered ? 'w-[420px]' : 'px-12'
  } mobile:w-full mx-auto my-12 mobile:m-0 mobile:px-4 mobile:py-6`;
const backStyle = 'mr-4 w-[3rem] block';
const h1Style =
  'flex min-w-0 flex-1 break-words text-[2.5rem] mobile:text-[1.75rem] font-black leading-[2.75rem] mobile:leading-[2.25rem] text-secondaryColor';
const titleWrapper = 'flex items-start mobile:flex-col mobile:ml-0';
const titleWrapperBackStyle = 'ml-[-4rem]';
