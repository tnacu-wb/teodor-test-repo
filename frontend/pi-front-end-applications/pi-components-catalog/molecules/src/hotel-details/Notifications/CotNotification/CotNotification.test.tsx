import '@testing-library/jest-dom';

import { render } from '../../../utils/test-utils';
import CotNotification from './CotNotification.component';

describe('CotNotification', () => {
  it('should render available text when cotAvailable is true', async () => {
    const { getByText, getByTestId } = render(<CotNotification cotAvailable={true} />);
    expect(getByTestId('cot-notification')).toBeInTheDocument();
    expect(getByText('booking.cot.available')).toBeInTheDocument();
  });

  it('should render unavailable text when cotAvailable is false', async () => {
    const { getByText, getByTestId } = render(<CotNotification cotAvailable={false} />);
    expect(getByTestId('cot-notification')).toBeInTheDocument();
    expect(getByText('booking.cot.notAvailable')).toBeInTheDocument();
  });
});
