import type { ContactBanner } from '@whitbread-eos/api';
import {
  Alert,
  Error,
  Info,
  Notification,
  NotificationStatus,
  Success,
} from '@whitbread-eos/atoms';
import { useEffect, useState } from 'react';

interface Props {
  contactBanner?: ContactBanner | null;
}

function getNotificationProps(type?: string | null): {
  variant: string;
  status: NotificationStatus;
  svg: JSX.Element;
} {
  const notificationType = type?.toLowerCase() ?? 'info';

  switch (notificationType) {
    case 'alert':
      return { variant: notificationType, status: 'warning', svg: <Alert /> };
    case 'error':
      return { variant: notificationType, status: notificationType, svg: <Error /> };
    case 'success':
      return { variant: notificationType, status: notificationType, svg: <Success /> };
    default:
      return { variant: 'info', status: 'info', svg: <Info /> };
  }
}

export default function ContactBannerNotification({ contactBanner }: Readonly<Props>) {
  const [currentPath, setCurrentPath] = useState('');

  useEffect(() => {
    setCurrentPath(window.location.pathname.toLowerCase());
  }, []);

  const enabledPages = Array.from(contactBanner?.enabledPages ?? [], (page) =>
    String(page).toLowerCase()
  );
  const isPageEnabled = enabledPages.includes(currentPath);

  if (!contactBanner?.text || !currentPath || !isPageEnabled) {
    return null;
  }

  const description = contactBanner.text
    .replace(/\{date\}/gi, contactBanner.date ?? '')
    .replace(/\s+/g, ' ')
    .trim();

  if (!description) {
    return null;
  }

  return (
    <Notification
      prefixDataTestId="ContactBannerNotification"
      description={description}
      {...getNotificationProps(contactBanner.type)}
      isInnerHTML
    />
  );
}
