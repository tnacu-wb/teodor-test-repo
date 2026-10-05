import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import AccessibleRoomNotification from './AccessibleRoomNotification.component';

const accessibleRoomNotificationProps = {
  data: 'This is the accessible room notification content',
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatInnerHTMLAssetUrls: () => accessibleRoomNotificationProps.data,
}));

describe('AccessibleRoomNotification', () => {
  it('should render AccessibleRoomNotification', () => {
    const { getByTestId } = render(
      <AccessibleRoomNotification {...accessibleRoomNotificationProps} />
    );
    expect(getByTestId('hdp_accessibleRoomNotification')).toBeInTheDocument();
  });

  it('should render accessible room notification text', () => {
    const { getByTestId, getByText } = render(
      <AccessibleRoomNotification {...accessibleRoomNotificationProps} />
    );
    expect(getByTestId('hdp_accessibleRoomNotification-AlertDescription')).toBeInTheDocument();
    expect(getByText(accessibleRoomNotificationProps.data)).toBeInTheDocument();
  });

  it('should render nothing when no content is passed to component', () => {
    const { queryByTestId } = render(<AccessibleRoomNotification data={''} />);
    expect(queryByTestId('hdp_accessibleRoomNotification')).toBeNull();
  });
});
