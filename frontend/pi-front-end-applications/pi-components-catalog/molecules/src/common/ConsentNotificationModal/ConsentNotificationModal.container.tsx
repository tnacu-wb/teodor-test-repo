import { getCookie, setCookieWithDefaultDomain, useCustomLocale } from '@whitbread-eos/utils';
import { useEffect, useState } from 'react';

import ConsentNotificationModalComponent from './ConsentNotificationModal.component';
import {
  MAX_CLOSE_COUNT,
  ONE_DAY_IN_MINUTES,
  PUSH_REQUEST_CLOSED_COUNT,
  PUSH_REQUEST_CLOSED_TIMESTAMP,
} from './ConsentNotificationModal.constants';

export interface Props {
  handleNotificationPermission: (value: boolean) => void;
}
export default function ConsentNotificationModalContainer({
  handleNotificationPermission,
}: Readonly<Props>) {
  const [isModalOpen, setIsModalOpen] = useState(false);
  const { language } = useCustomLocale() || {};
  const pushRequestClosedCount = Number(getCookie(PUSH_REQUEST_CLOSED_COUNT) ?? 0);
  const pushRequestClosedTimestamp = getCookie(PUSH_REQUEST_CLOSED_TIMESTAMP);

  const setClosedCountCookie = (count?: number) => {
    let closedCount = pushRequestClosedCount;
    closedCount = count ?? closedCount + 1;
    setCookieWithDefaultDomain(PUSH_REQUEST_CLOSED_COUNT, closedCount, undefined);
  };

  const setClosedTimestampCookie = () => {
    const currentTimestamp = new Date().toString();
    const cookieExpiresTime = ONE_DAY_IN_MINUTES;
    setCookieWithDefaultDomain(PUSH_REQUEST_CLOSED_TIMESTAMP, currentTimestamp, cookieExpiresTime);
  };

  const handleAllowAction = () => {
    setClosedCountCookie();
    setClosedTimestampCookie();
    handleNotificationPermission(true);
    setIsModalOpen(false);
  };

  const handleDoNotAllowAction = () => {
    setClosedCountCookie(MAX_CLOSE_COUNT);
    setClosedTimestampCookie();
    handleNotificationPermission(false);
    setIsModalOpen(false);
  };

  const handleCloseAction = () => {
    setClosedCountCookie();
    setClosedTimestampCookie();
    handleNotificationPermission(false);
    setIsModalOpen(false);
  };

  // Logic for notification permission and  delayed opening of 2 seconds
  useEffect(() => {
    let timer: ReturnType<typeof setTimeout>;
    if (pushRequestClosedCount < MAX_CLOSE_COUNT && !pushRequestClosedTimestamp) {
      timer = setTimeout(() => setIsModalOpen(true), 2000);
    }

    return () => clearTimeout(timer);
  }, [pushRequestClosedCount, pushRequestClosedTimestamp]);

  return (
    <ConsentNotificationModalComponent
      isModalVisible={isModalOpen}
      onConsentModalClose={handleCloseAction}
      onConsentModalAllow={handleAllowAction}
      onConsentModalDeny={handleDoNotAllowAction}
      language={language}
    />
  );
}
