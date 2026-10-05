import '@testing-library/jest-dom';
import React from 'react';

import { Alert } from '../../../assets/icons';
import { render, fireEvent } from '../../../utils/test-utils';
import NotificationComponent from './Notification.component';

describe('Notification', () => {
  it('should render the component', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={<Alert />}
      />
    );

    const alert = getByRole('alert');

    expect(alert).toBeInTheDocument();
  });
  it('should display the title if the title prop is defined', () => {
    const { getByRole } = render(
      <NotificationComponent
        isInnerHTML={true}
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={<Alert />}
      />
    );
    const alert = getByRole('alert');
    expect(alert).toHaveTextContent('Notification title');
  });
  it('should not display the title if the title is not defined', () => {
    const { getByRole } = render(
      <NotificationComponent
        isInnerHTML={true}
        status="info"
        description="Notification message"
        variant="info"
        svg={<Alert />}
      />
    );
    const alert = getByRole('alert');
    expect(alert).not.toHaveTextContent('Notification title');
  });
  it('should always display the description', () => {
    const { getByRole } = render(
      <NotificationComponent
        isInnerHTML={true}
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={<Alert />}
      />
    );
    const alert = getByRole('alert');
    expect(alert).toHaveTextContent(/notification message/i);
  });
  it('should not display alert if title and description are missing', () => {
    const { queryByRole } = render(
      <NotificationComponent
        isInnerHTML={false}
        status="info"
        title=""
        description=""
        variant="info"
        svg={<Alert />}
      />
    );
    const alert = queryByRole('alert');
    expect(alert).toBeNull();
  });
  it('should call onClick prop when clicking clickable Notification component', () => {
    const handleClick = jest.fn();
    const { getByRole } = render(
      <NotificationComponent
        onClick={handleClick}
        status="info"
        description="clickable Notification"
        variant="infoGrey"
        svg={<Alert />}
      />
    );
    const alert = getByRole('alert');
    fireEvent.click(alert);

    expect(handleClick).toHaveBeenCalledTimes(1);
  });
  it('should display the close button if the onClick and showCloseButton props are enabled', () => {
    const handleClick = jest.fn();
    const { getByRole } = render(
      <NotificationComponent
        onClick={handleClick}
        showCloseButton={true}
        status="info"
        description="clickable Notification"
        variant="infoGrey"
        svg={<Alert />}
      />
    );
    const close = getByRole('button');
    expect(close).toBeInTheDocument();
  });

  it('should render the correct description when an array of strings is passed', () => {
    const description = ['Notification message 1', 'Notification message 2'];
    const { getByTestId } = render(
      <NotificationComponent status="info" description={description} variant="info" svg={<div />} />
    );
    expect(getByTestId('AlertDescription-0')).toHaveTextContent(description[0]);
    expect(getByTestId('AlertDescription-1')).toHaveTextContent(description[1]);
  });
});
