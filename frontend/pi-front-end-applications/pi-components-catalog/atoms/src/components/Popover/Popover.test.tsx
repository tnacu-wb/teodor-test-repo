import { fireEvent, render, waitFor } from '../../utils/test-utils';
import Popover from './Popover.component';

describe('Popover', () => {
  it('should render a Popover', () => {
    const props = {
      trigger: <button>Trigger me!</button>,
    };
    const { getByText } = render(<Popover triggerItem={props.trigger}>Hello</Popover>);
    const button = getByText('Trigger me!');
    fireEvent.click(button);
    waitFor(() => {
      expect(button).toBeInTheDocument();
    });
  });
});
