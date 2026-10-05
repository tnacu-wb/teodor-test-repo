'use client';

import { InfoTooltip, SanitizedContent } from '@whitbread-eos/atoms/ui';
import { useOutsideClick, formatIBAssetsUrl } from '@whitbread-eos/utils';
import Image from 'next/image';

type Props = {
  icons: Record<string, string>;
  title?: string;
  message: string;
};

export function PageSubtitle({ icons, title, message }: Props) {
  const baseDataTestId = 'InnBusinessTab';
  const {
    isOpen: isTooltipOpen,
    setIsOpen: setIsTooltipOpen,
    elementRef: tooltipRef,
    iconRef: tooltipIconRef,
  } = useOutsideClick();

  return (
    <>
      <div className={titleStyle} data-testid={`${baseDataTestId}-title-section`}>
        {title && title !== '' && (
          <span className={titleStyle} data-testid={`${baseDataTestId}-title`}>
            {title}
          </span>
        )}
        <InfoTooltip
          className={tooltipStyle}
          content={<SanitizedContent>{message}</SanitizedContent>}
          testId={`${baseDataTestId}-InfoTooltip`}
          hoverVariant={true}
        >
          <Image
            alt={title || 'Info icon'}
            tabIndex={0}
            src={formatIBAssetsUrl(icons['icon.notification.info'])}
            width={26}
            height={26}
            className={infoTooltip}
            priority={true}
            data-testid={`${baseDataTestId}-title-icon`}
            onClick={() => setIsTooltipOpen(!isTooltipOpen)}
            ref={tooltipIconRef}
          />
        </InfoTooltip>
      </div>
      <InfoTooltip
        ref={tooltipRef}
        className={tooltipStyle}
        content={<SanitizedContent>{message}</SanitizedContent>}
        testId={`${baseDataTestId}-InfoTooltip`}
        open={isTooltipOpen}
        arrowClassName="right-[18px]"
        mobile
      />
    </>
  );
}

const infoTooltip = 'ml-2 cursor-pointer';
const titleStyle = 'flex items-center text-xl font-bold';
const tooltipStyle = 'w-80 mobile:w-[17rem] rounded shadow-variantTooltip';
