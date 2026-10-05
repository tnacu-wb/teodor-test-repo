import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import type { Props } from './RestaurantMessage.component';
import RestaurantMessage from './RestaurantMessage.component';

describe('NoRestaurantMessage component', () => {
  it('should render with a title and a body', () => {
    const mockProps: Props = {
      messageTitle: 'TITLE',
      messageDescription: 'BODY',
    };

    const { getByTestId, getByText } = render(<RestaurantMessage {...mockProps} />);
    expect(getByTestId('RestaurantMessage-Wrapper')).toBeInTheDocument();
    expect(getByText(mockProps.messageTitle)).toBeInTheDocument();
    expect(getByText('BODY')).toBeInTheDocument();
  });

  it('should expose live region semantics for screen readers', () => {
    const mockProps: Props = {
      messageTitle: 'TITLE',
      messageDescription: 'BODY',
    };

    const { getByTestId } = render(<RestaurantMessage {...mockProps} />);
    const wrapper = getByTestId('RestaurantMessage-Wrapper');

    expect(wrapper).toHaveAttribute('role', 'status');
    expect(wrapper).toHaveAttribute('aria-live', 'polite');
    expect(wrapper).toHaveAttribute('aria-atomic', 'true');
    expect(wrapper).toHaveAttribute('aria-labelledby', 'RestaurantMessage-Title');
    expect(wrapper).toHaveAttribute('aria-describedby', 'RestaurantMessage-Description');
  });

  it('should not render description when it is undefined', () => {
    const mockProps: Props = {
      messageTitle: 'TITLE',
      messageDescription: undefined,
    };

    const { getByTestId, queryByTestId } = render(<RestaurantMessage {...mockProps} />);
    const wrapper = getByTestId('RestaurantMessage-Wrapper');

    expect(queryByTestId('RestaurantMessage-Description')).not.toBeInTheDocument();
    expect(wrapper).not.toHaveAttribute('aria-describedby');
  });
});
