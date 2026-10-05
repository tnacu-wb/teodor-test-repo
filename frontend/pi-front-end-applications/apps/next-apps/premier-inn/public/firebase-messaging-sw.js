const FCM_MSG = 'FCM_MSG';
const MESSAGE_TYPE_NOTIFICATION_CLICKED = 'notification-clicked"';
const MESSAGE_TYPE_PUSH_RECEIVED = 'push-received';

/**
 * Black listed pages where we won't show the notification at all when the PremierInn Website is visible in the browser.
 */
const BLACKLISTED_PAGES = [
  'reservationId=',
]

async function sleep(ms) {
  return new Promise(resolve => {
    setTimeout(resolve, ms);
  })
}

function getClientList() {
  return self.clients.matchAll({
    type: 'window',
    includeUncontrolled: true
  });
}

async function getWindowClient(url) {
  const clientList = await getClientList();

  for (const client of clientList) {
    const clientUrl = new URL(client.url, self.location.href);

    if (url.host === clientUrl.host) {
      return client;
    }
  }

  return null;
}

async function isNotificationAllowedOnPage() {
  const clientList = await getClientList();

  const openOnClickURL = new URL(self.location.origin, self.location.href);

  for (client of clientList) {
    const clientUrl = new URL(client.url, self.location.href);
    if (client.visibilityState === 'visible' && !client.url.startsWith('chrome-extension://') && clientUrl.host === openOnClickURL.host) {
      return false;
    }
  }

  return true;
}

function getNotificationPayload(eventData) {
  if (!eventData) {
    return null;
  }

  try {
    return eventData.json();
  }
  catch {
    return null;
  }
}

async function handleOnClick(event) {
  const internalPayload = event.notification.data;
  const { url = '' } = internalPayload;

  if (!url) {
    return;
  }

  const client = await self.clients.openWindow(url);
  await sleep(5000);


  if (!client || !client.postMessage) {
    return;
  }

  // Send Open Event to Firebase console
  const fcmMsgPayload = internalPayload?.[FCM_MSG];

  if (!fcmMsgPayload) {
    return;
  }

  fcmMsgPayload.mesageType = MESSAGE_TYPE_NOTIFICATION_CLICKED;
  fcmMsgPayload.isFirebaseMessaging = true;

  return client.postMessage(fcmMsgPayload);
}

const onPushNotificationRcv = async (event) => {
  const eventData = await getNotificationPayload(event.data);

  if (!eventData) {
    return null;
  }

  const allowedToShowNotification = await isNotificationAllowedOnPage();
  if (!allowedToShowNotification) {
    return null;
  }

  const notificationTitle = eventData?.notification?.title || '';

  const notificationObj = {
    ...event?.notification,
    body: eventData?.notification?.body || '',
    icon: eventData?.data?.['Icon_URL'] || eventData?.notification?.image,
    image: eventData?.notification?.image,
    actions: [
      {
        action: "open_url",
        title: "Open",
      },
    ],
    data: {
      ...event?.data,
      url: eventData?.data?.['Open_URL'] ?? 'https://www.premierinn.com/gb/en/home.html?CID=PUSH_WEB_No_Campaign',
    },
  }

  // Propagate all Notifications in data object for on Click handler
  notificationObj.data = {
    [FCM_MSG]: notificationObj,
    ...notificationObj.data
  }

  await self?.registration?.showNotification(notificationTitle, notificationObj);
}

// Overwrite Firebase default handling for clicking on notifications so we add the images and all our extras
self.addEventListener('notificationclick', function (event) {
  event.notification.close();
  event.stopImmediatePropagation();

  event.waitUntil(handleOnClick(event));
}, false);

// Overwrite the push event so we won't duplicate the default notifications
self.addEventListener('push', function (event) {
  event.preventDefault();
  event.stopImmediatePropagation();

  event.waitUntil(onPushNotificationRcv(event));
}, false)

importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/10.12.0/firebase-messaging-compat.js');

const firebaseConfig = {
  apiKey: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_API_KEY_WB_PLACEHOLDER_ENV_END",
  authDomain: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_AUTH_DOMAIN_WB_PLACEHOLDER_ENV_END",
  databaseURL: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_DATABASE_URL_WB_PLACEHOLDER_ENV_END",
  projectId: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_PROJECT_ID_WB_PLACEHOLDER_ENV_END",
  storageBucket: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_STORAGE_BUCKET_WB_PLACEHOLDER_ENV_END",
  messagingSenderId: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_MESSAGING_SENDER_ID_WB_PLACEHOLDER_ENV_END",
  appId: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_APP_ID_WB_PLACEHOLDER_ENV_END",
  measurementId: "WB_PLACEHOLDER_ENV_START_NEXT_PUBLIC_FIREBASE_MEASUREMENT_ID_WB_PLACEHOLDER_ENV_END",
};

firebase.initializeApp(firebaseConfig);

// Retrieve firebase messaging
const messaging = firebase.messaging();
