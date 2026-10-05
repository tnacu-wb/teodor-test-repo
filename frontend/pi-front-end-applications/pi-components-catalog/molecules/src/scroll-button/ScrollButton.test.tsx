import { render } from '@testing-library/react';
import userEvent from '@testing-library/user-event';

import ScrollButton from './ScrollButton';

describe('ScrollButton', () => {
  it('renders left arrow and handles click', () => {
    const handleClick = jest.fn();
    const { getByRole } = render(<ScrollButton direction="left" onClick={handleClick} />);
    userEvent.click(getByRole('button'));
    expect(handleClick).toHaveBeenCalled();
  });

  it('renders right arrow', () => {
    const { getByLabelText } = render(<ScrollButton direction="right" onClick={jest.fn()} />);
    expect(getByLabelText('right scroll button')).toBeInTheDocument();
  });
});
