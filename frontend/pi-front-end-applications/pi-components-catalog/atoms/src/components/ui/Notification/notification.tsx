import { cn } from '@whitbread-eos/utils';
import Image from 'next/image';
import * as React from 'react';

interface Props {
  title: string;
  message: string;
  icon: string;
  type: string;
  className?: string;
}

const Notification = ({ title, icon, message, type = 'default', className }: Readonly<Props>) => {
  const getNotificationStyle = () => {
    if (type === 'error') {
      return errorStyle;
    }

    if (type === 'warning') {
      return warningStyle;
    }

    if (type === 'info') {
      return infoStyle;
    }

    return defaultStyle;
  };

  return (
    <div className={cn(notificationStyle, getNotificationStyle(), className)}>
      <Image className={iconStyle} src={icon} alt={'notification icon'} width={16} height={16} />
      <div className={textStyle}>
        {title && <span className={titleStyle}>{title}</span>}
        {message && <span>{message}</span>}
      </div>
    </div>
  );
};

export { Notification };

const notificationStyle = 'flex p-4 border rounded gap-2 w-full';
const iconStyle = 'w-4 h-4 overflow-hidden shrink-0';
const textStyle =
  'flex flex-col text-sm [&_a]:text-secondaryColor [&_a]:underline [&_a:hover]:no-underline';
const titleStyle = 'font-semibold';
const defaultStyle = 'border-successTint2 bg-successTint';
const errorStyle = 'border-errorTint bg-tooltipError';
const warningStyle = 'bg-notificationAlertBg border-notificationAlertBorder';
const infoStyle = 'border-tooltipInfoTint bg-tooltipInfo';
