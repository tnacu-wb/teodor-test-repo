import { Messaging, getToken } from 'firebase/messaging';
import { useEffect, useState } from 'react';

import { messaging } from '../firebase/firebase';

export default function useServiceWorker(enableServiceWorker: boolean) {
  const getFBToken = async (
    messaging: Messaging,
    serviceWorkerRegistration: ServiceWorkerRegistration
  ): Promise<Promise<string> | undefined> => {
    let sw: ServiceWorker | undefined;
    if (serviceWorkerRegistration.installing) {
      sw = serviceWorkerRegistration.installing;
    } else if (serviceWorkerRegistration.waiting) {
      sw = serviceWorkerRegistration.waiting;
    } else if (serviceWorkerRegistration.active) {
      sw = serviceWorkerRegistration.active;
    }
    if (sw) {
      if (sw.state === 'activated') {
        return await getToken(messaging, {
          vapidKey: process.env.NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY,
          serviceWorkerRegistration,
        });
      }
      return new Promise((resolve, reject) => {
        sw?.addEventListener('statechange', async (e) => {
          if ((e.target as ServiceWorker)?.state === 'activated') {
            try {
              const token = await getToken(messaging, {
                vapidKey: process.env.NEXT_PUBLIC_FIREBASE_KEYVAPID_KEY,
                serviceWorkerRegistration,
              });
              resolve(token);
            } catch (error) {
              reject(error);
            }
          }
        });
      });
    }
    return undefined;
  };
  const registerServiceWorker = async () => {
    if ('serviceWorker' in navigator) {
      const registration = await navigator.serviceWorker.register('/firebase-messaging-sw.js', {
        scope: '/firebase-cloud-messaging-push-scope',
      });
      return registration;
    }
  };

  const getServiceWorkerPermission = async () => {
    const permission = await Notification.requestPermission();
    if (permission === 'granted') {
      const swRegistration = await registerServiceWorker();
      const msg = messaging();
      if (swRegistration && msg) {
        await getFBToken(msg, swRegistration);
      }
    }
  };

  const [isBrowserNotificationEnabled, setIsBrowserNotificationEnabled] = useState(false);
  const handleNotificationPermission = (value: boolean) => {
    setIsBrowserNotificationEnabled(value);
  };

  useEffect(() => {
    if (enableServiceWorker && isBrowserNotificationEnabled) {
      getServiceWorkerPermission();
    }
  }, [enableServiceWorker, isBrowserNotificationEnabled]);

  return {
    handleNotificationPermission,
  };
}
