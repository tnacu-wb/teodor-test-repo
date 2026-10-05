'use client';

import { LOCALES } from '@whitbread-eos/api';
import { InfoTooltip, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { useOutsideClick } from '@whitbread-eos/utils';
import { formatIBAssetsUrl, cn } from '@whitbread-eos/utils/server';
import Image from 'next/image';

type Props = {
  locale: LOCALES;
  baseDataTestId: string;
  mainText: string;
  infoText: string;
  iconSize?: number;
  icons: Record<string, string>;
  mainClassName?: string;
};

export function TextWithInfoTooltip({
  baseDataTestId,
  mainText,
  infoText,
  iconSize = 24,
  icons,
  mainClassName,
}: Props) {
  const {
    isOpen: isTooltipOpen,
    setIsOpen: setIsTooltipOpen,
    elementRef: tooltipRef,
    iconRef: tooltipIconRef,
  } = useOutsideClick();

  return (
    <span className={wrapperStyle}>
      <span className={cn(textStyle, mainClassName)}>{mainText}</span>
      <InfoTooltip
        ref={tooltipRef}
        className={tooltipStyle}
        content={<SanitizedContent>{infoText}</SanitizedContent>}
        testId={`${baseDataTestId}-InfoTooltip`}
        hoverVariant={true}
      >
        <Image
          alt={mainText}
          src={formatIBAssetsUrl(icons['icon.notification.info'])}
          width={iconSize}
          height={iconSize}
          className={infoTooltip}
          priority={true}
          data-testid={`${baseDataTestId}-title-icon`}
          onClick={() => setIsTooltipOpen(!isTooltipOpen)}
          ref={tooltipIconRef}
        />
      </InfoTooltip>
      <InfoTooltip
        ref={tooltipRef}
        className={tooltipStyle}
        content={<SanitizedContent>{infoText}</SanitizedContent>}
        testId={`${baseDataTestId}-Mobile-InfoTooltip`}
        open={isTooltipOpen}
        mobile
      />
    </span>
  );
}

const textStyle = 'font-semibold text-[1rem] leading-[1.5rem]';
const wrapperStyle = 'relative inline-flex items-center gap-2';
const infoTooltip = 'cursor-pointer';
const tooltipStyle =
  'w-80 mobile:w-full mobile:inline-table mobile:absolute mobile:left-0 mobile:top-full mobile:mt-2 mobile:z-20 rounded shadow-variantTooltip';
