import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import ConsentNotificationModalComponent from './ConsentNotificationModal.component';
import { MAX_CLOSE_COUNT } from './ConsentNotificationModal.constants';
import ConsentNotificationModalContainer from './ConsentNotificationModal.container';

const onConsentModalClose = jest.fn();
const onConsentModalAllow = jest.fn();
const onConsentModalDeny = jest.fn();
const handleNotificationPermission = jest.fn();

jest.mock('@whitbread-eos/utils');

const mockGetCookie = jest.fn();
const mockSetCookie = jest.fn();
jest.mock('cookies', () => {
  return jest.fn().mockImplementation(() => ({
    get: mockGetCookie,
    set: mockSetCookie,
  }));
});

describe('ConsentNotificationModalContainer', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  const mockNotificationPermission = (permission) => {
    global.Notification = {
      requestPermission: jest.fn().mockResolvedValue(permission),
    };
  };

  const props = {
    handleNotificationPermission,
  };

  it('should render ConsentNotificationModalContainer', () => {
    const { getByTestId } = render(<ConsentNotificationModalContainer {...props} />);

    waitFor(() => {
      expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();
    });
  });

  it('should not render modal when notification permission is not default', () => {
    mockGetCookie.mockReturnValueOnce(4);
    mockGetCookie.mockReturnValueOnce(
      'Mon Jul 15 2024 15:21:19 GMT+0300 (Eastern European Summer Time)'
    );
    mockNotificationPermission('granted');

    const { queryByTestId } = render(<ConsentNotificationModalContainer {...props} />);

    expect(queryByTestId('pi-notification-permission-popup-modal-content')).toBeNull();
  });

  it('should not render modal when closed count is equal to max', () => {
    // Mock cookie values
    mockGetCookie.mockReturnValueOnce(MAX_CLOSE_COUNT);
    mockGetCookie.mockReturnValueOnce(null);
    mockNotificationPermission('default');

    const { queryByTestId } = render(<ConsentNotificationModalContainer {...props} />);

    expect(queryByTestId('pi-notification-permission-popup-modal-content')).toBeNull();
  });

  it('should render modal after 2 seconds when notification permission is default and closed count is less than max', async () => {
    // Mock cookie values
    mockGetCookie.mockReturnValueOnce(0);
    mockGetCookie.mockReturnValueOnce(null);
    mockNotificationPermission('default');

    const { getByTestId } = render(<ConsentNotificationModalContainer {...props} />);

    // Wait for modal to appear
    waitFor(() => {
      expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();
    });
  });

  it('should call handleNotifPermission on allow action', () => {
    const { getByTestId, getByText } = render(<ConsentNotificationModalContainer {...props} />);

    waitFor(() => {
      expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();

      const allowButton = getByText('pushNotifications.popup.allow');
      fireEvent.click(allowButton);

      expect(props.handleNotificationPermission).toHaveBeenCalledWith(true);
    });
  });

  it('should call handleNotifPermission on deny action', () => {
    const { getByTestId, getByText } = render(<ConsentNotificationModalContainer {...props} />);

    waitFor(() => {
      expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();

      const denyButton = getByText('pushNotifications.popup.dontAllow');
      fireEvent.click(denyButton);

      expect(props.handleNotificationPermission).toHaveBeenCalledWith(false);
    });
  });

  it('should call handleNotifPermission on close action', () => {
    const { getByTestId } = render(<ConsentNotificationModalContainer {...props} />);

    waitFor(() => {
      expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();

      const closeButton = getByTestId('pi-notification-permission-popup-close-btn');
      fireEvent.click(closeButton);

      expect(props.handleNotificationPermission).toHaveBeenCalledWith(false);
    });
  });
});

describe('ConsentNotificationModal component', () => {
  beforeEach(async () => {
    jest.clearAllMocks();
  });

  const props = {
    isModalVisible: true,
    onConsentModalClose,
    onConsentModalAllow,
    onConsentModalDeny,
    language: 'en',
  };
  it('should render ConsentNotificationModal component', () => {
    const { getByTestId } = render(
      <ConsentNotificationModalComponent {...props}>
        <p>Test Child</p>
      </ConsentNotificationModalComponent>
    );

    expect(getByTestId('pi-notification-permission-popup-modal-content')).toBeInTheDocument();
  });

  it('handle action from onClose button works', async () => {
    const { getByTestId } = render(
      <ConsentNotificationModalComponent {...{ ...props, language: 'de' }}>
        <p>test description</p>
      </ConsentNotificationModalComponent>
    );
    const closeButton = getByTestId('pi-notification-permission-popup-close-btn');
    fireEvent.click(closeButton);

    expect(onConsentModalClose).toBeCalled();
  });

  it('should render a ConsentNotificationModal which is closed', () => {
    const { queryByText } = render(
      <ConsentNotificationModalComponent {...{ ...props, isModalVisible: false }}>
        <p>Test Child</p>
      </ConsentNotificationModalComponent>
    );

    expect(queryByText('Title Header')).toBeFalsy();
    expect(queryByText('Test Child')).toBeFalsy();
  });

  it('should render ConsentNotificationModal with a delimiter', () => {
    const { queryByTestId } = render(
      <ConsentNotificationModalComponent {...props}>
        <p>Test Child</p>
      </ConsentNotificationModalComponent>
    );
    expect(queryByTestId('pi-notification-permission-popup-delimiter')).toBeTruthy();
  });

  it('handle action from onAllow button works', async () => {
    const { getByText } = render(<ConsentNotificationModalComponent {...props} />);
    const allowButton = getByText('pushNotifications.popup.allow');
    fireEvent.click(allowButton);

    expect(onConsentModalAllow).toBeCalled();
  });

  it('handle action from deny button works', async () => {
    const { getByText } = render(<ConsentNotificationModalComponent {...props} />);
    const denyButton = getByText('pushNotifications.popup.dontAllow');
    fireEvent.click(denyButton);

    expect(onConsentModalDeny).toBeCalled();
  });
});
