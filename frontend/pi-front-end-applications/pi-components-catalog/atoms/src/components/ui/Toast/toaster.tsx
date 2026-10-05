'use client';

import Image from 'next/image';
import { usePathname } from 'next/navigation';

import { Toast, ToastClose, ToastProvider, ToastViewport } from './toast';
import { useToast } from './use-toast';

export function Toaster() {
  const { toasts } = useToast();
  const pathname = usePathname();

  return (
    <ToastProvider>
      {toasts.map(function ({ id, icon, content, ...props }) {
        return (
          <Toast key={id} {...props} data-testid="Toast">
            <div className={toastStyle}>
              {icon && (
                <Image src={icon} alt="toast-icon" width={16} height={16} className="mt-0.5 mr-2" />
              )}
              {content}
            </div>
            <ToastClose />
          </Toast>
        );
      })}
      <ToastViewport className={pathname.includes('manage') ? viewportManageStyle : ''} />
    </ToastProvider>
  );
}

const toastStyle = 'flex items-start';
const viewportManageStyle = 'mobile:bottom-[129px]';
