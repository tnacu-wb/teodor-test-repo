import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import NotificationComponent from './Notification.component';

const testSvg = <svg aria-hidden="true" />;

describe('Notification', () => {
  it('should render the component', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
      />
    );

    const liveRegion = getByRole('status');

    expect(liveRegion).toBeInTheDocument();
  });

  it('should preserve Alert container styles when sx is provided', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        description="Notification message"
        variant="info"
        svg={testSvg}
        sx={{ color: 'red' }}
      />
    );

    const liveRegion = getByRole('status');
    const computedStyles = getComputedStyle(liveRegion);
    expect(computedStyles.color).toBe('red');
    expect(computedStyles.paddingTop).toBe('1rem');
  });

  it.each(['error', 'warning'] as const)(
    'should render an alert role when the notification status is %s',
    (status) => {
      const { getByRole } = render(
        <NotificationComponent
          status={status}
          title="Notification title"
          description="Notification message"
          variant={status}
          svg={testSvg}
        />
      );

      expect(getByRole('alert')).toBeInTheDocument();
    }
  );

  it.each(['info', 'success'] as const)(
    'should render a status role when the notification status is %s',
    (status) => {
      const { getByRole } = render(
        <NotificationComponent
          status={status}
          title="Notification title"
          description="Notification message"
          variant={status}
          svg={testSvg}
        />
      );

      expect(getByRole('status')).toBeInTheDocument();
    }
  );

  it('should set aria-atomic to true for the live region', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
      />
    );

    expect(getByRole('status')).toHaveAttribute('aria-atomic', 'true');
  });

  it('should display the title if the title prop is defined', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
      />
    );
    const liveRegion = getByRole('status');
    expect(liveRegion).toHaveTextContent('Notification title');
  });

  it('should apply semantic typography to the title when textStyle is provided', () => {
    const { getByTestId } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
        textStyle="body-s-emphasis"
      />
    );

    expect(getByTestId('AlertTitle')).toHaveStyle({
      fontSize: '14px',
      fontWeight: '600',
      lineHeight: '1.4',
    });
  });

  it('should not apply semantic title typography for legacy typography props', () => {
    const { getByTestId } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
        fontSize="sm"
      />
    );

    expect(getByTestId('AlertTitle').getAttribute('style') ?? '').not.toContain('font-size: 14px');
  });

  it('should apply semantic typography to the description when descriptionTextStyle is provided', () => {
    const { getByTestId } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
        descriptionTextStyle="body-s-regular"
      />
    );

    expect(getByTestId('AlertDescription')).toHaveStyle({
      fontSize: '14px',
      fontWeight: '400',
      lineHeight: '1.4',
    });
  });

  it('should apply semantic typography to strong tags when descriptionStrongTextStyle is provided', () => {
    const { getByText } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description={
          <>
            Notification <strong>message</strong>
          </>
        }
        variant="info"
        svg={testSvg}
        descriptionTextStyle="body-s-regular"
        descriptionStrongTextStyle="body-s-emphasis"
      />
    );

    expect(getByText('message')).toHaveStyle({
      fontSize: '14px',
      fontWeight: '600',
      lineHeight: '1.4',
    });
  });

  it('should not display the title if the title is not defined', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        description="Notification message"
        variant="info"
        svg={testSvg}
      />
    );
    const liveRegion = getByRole('status');
    expect(liveRegion).not.toHaveTextContent('Notification title');
  });

  it('should always display the description', () => {
    const { getByRole } = render(
      <NotificationComponent
        status="info"
        title="Notification title"
        description="Notification message"
        variant="info"
        svg={testSvg}
      />
    );
    const liveRegion = getByRole('status');
    expect(liveRegion).toHaveTextContent(/notification message/i);
  });

  it('should not render the live region if title and description are missing', () => {
    const { queryByRole } = render(
      <NotificationComponent status="info" title="" description="" variant="info" svg={testSvg} />
    );
    const liveRegion = queryByRole('status');
    expect(liveRegion).toBeNull();
  });

  it('should call onClick prop when clicking clickable Notification component', () => {
    const handleClick = jest.fn();
    const { getByRole } = render(
      <NotificationComponent
        onClick={handleClick}
        status="info"
        description="clickable Notification"
        variant="infoGrey"
        svg={testSvg}
      />
    );
    const liveRegion = getByRole('status');
    fireEvent.click(liveRegion);

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
        svg={testSvg}
      />
    );
    const close = getByRole('button');
    expect(close).toBeInTheDocument();
  });

  it('should render the correct description when an array of strings is passed', () => {
    const description = ['Notification message 1', 'Notification message 2'];
    const { getByTestId } = render(
      <NotificationComponent status="info" description={description} variant="info" svg={testSvg} />
    );
    expect(getByTestId('AlertDescription-0')).toHaveTextContent(description[0]);
    expect(getByTestId('AlertDescription-1')).toHaveTextContent(description[1]);
  });
});
