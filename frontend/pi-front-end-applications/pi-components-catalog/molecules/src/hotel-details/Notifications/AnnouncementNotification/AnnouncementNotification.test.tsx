import '@testing-library/jest-dom';
import type { Announcement } from '@whitbread-eos/api';

import { render } from '../../../utils/test-utils';
import { AnnouncementNotification } from './AnnouncementNotification';

const mockAnnouncement = {
  endDate: '21/07/2099',
  showAnnouncement: 'true',
  startDate: '08/01/2021',
  text: 'Get all the latest updates on our response to COVID-19 and see how we’re keeping guests safe with ourPremier Inn CleanProtect promise.',
  title: '',
  type: 'info',
};

const announcementNotificationProps = {
  announcement: mockAnnouncement,
};

describe('AnnouncementNotification', () => {
  it('should render AnnouncementNotification', () => {
    const { getByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        channel="PI"
        arrivalDate="2000-1-1"
        departureDate="2099-1-2"
      />
    );
    expect(getByTestId('announcement-notification-info')).toBeInTheDocument();
  });

  it('should render AnnouncementNotification with departure overlapping date', () => {
    const { getByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        channel="PI"
        arrivalDate="2000-1-1"
        departureDate="2099-1-2"
      />
    );
    expect(getByTestId('announcement-notification-info')).toBeInTheDocument();
  });

  it('should render AnnouncementNotification with both overlapping dates', () => {
    const { getByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        channel="PI"
        arrivalDate="2098-1-1"
        departureDate="2099-1-2"
      />
    );
    expect(getByTestId('announcement-notification-info')).toBeInTheDocument();
  });

  it('should render AnnouncementNotification with arrival overlapping date', () => {
    const { getByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        channel="PI"
        arrivalDate="2098-1-1"
        departureDate="2100-1-2"
      />
    );
    expect(getByTestId('announcement-notification-info')).toBeInTheDocument();
  });

  it('should not render AnnouncementNotification when stay dates dont overlap', () => {
    const { queryByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        channel="PI"
        arrivalDate="2000-1-1"
        departureDate="2000-1-2"
      />
    );
    expect(queryByTestId('announcement-notification-info')).not.toBeInTheDocument();
  });

  it('should render warning announcement notification', () => {
    const { getByTestId, getByText } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        arrivalDate="2000-1-1"
        departureDate="2099-1-2"
        announcement={{ ...announcementNotificationProps.announcement, type: 'warning' }}
      />
    );
    expect(getByTestId('announcement-notification-warning')).toBeInTheDocument();
    expect(getByText(announcementNotificationProps.announcement.text)).toBeInTheDocument();
  });

  it('should not render info announcement notification if no arrivalDate or departureDate have been provided', () => {
    const { queryByTestId, queryByText } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        announcement={{ ...announcementNotificationProps.announcement, type: 'info' }}
      />
    );
    expect(queryByTestId('announcement-notification-info')).not.toBeInTheDocument();
    expect(queryByText(announcementNotificationProps.announcement.text)).not.toBeInTheDocument();
  });

  it('should render nothing when no announcement is given', () => {
    const { queryByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        announcement={{} as Announcement}
      />
    );
    expect(queryByTestId('announcement-notification-info')).toBeNull();
    expect(queryByTestId('announcement-notification-warning')).toBeNull();
  });

  it('should render nothing when current date is not between startDate and endDate', () => {
    const { queryByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        announcement={{ ...announcementNotificationProps.announcement, endDate: '21/07/2022' }}
      />
    );
    expect(queryByTestId('announcement-notification-info')).toBeNull();
    expect(queryByTestId('announcement-notification-warning')).toBeNull();
  });

  it('should render nothing when text is an empty string', () => {
    const { queryByTestId } = render(
      <AnnouncementNotification
        {...announcementNotificationProps}
        announcement={{ ...announcementNotificationProps.announcement, text: '' }}
      />
    );
    expect(queryByTestId('announcement-notification-info')).toBeNull();
    expect(queryByTestId('announcement-notification-warning')).toBeNull();
  });
});
