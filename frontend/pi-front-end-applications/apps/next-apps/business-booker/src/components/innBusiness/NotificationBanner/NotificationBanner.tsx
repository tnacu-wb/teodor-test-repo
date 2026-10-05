'use client';

import { Notification } from '@whitbread-eos/atoms/ui';
import { formatIBAssetsUrl } from '@whitbread-eos/utils';
import { usePathname } from 'next/navigation';

type ContactBanner = {
  type?: string | null;
  text?: string | null;
  date?: string | null;
  enabledPages?: (string | null)[] | null;
};

type Props = {
  contactBanner: ContactBanner | null | undefined;
  icons: Record<string, string> | null | undefined;
};

export function NotificationBanner({ contactBanner, icons }: Props) {
  const currentPathname = usePathname();
  const isEnabled =
    !!contactBanner?.text && (contactBanner.enabledPages?.includes(currentPathname) ?? false);

  if (!isEnabled) return null;

  const type = contactBanner?.type ?? 'info';
  const message = (contactBanner.text ?? '')
    .replace('{Date}', contactBanner?.date ?? '')
    .replace(/\s+/g, ' ')
    .trim();

  return (
    <div className="p-4">
      <Notification
        type={type}
        icon={formatIBAssetsUrl(icons?.[`icon.notification.${type}`])}
        message={message}
      />
    </div>
  );
}
