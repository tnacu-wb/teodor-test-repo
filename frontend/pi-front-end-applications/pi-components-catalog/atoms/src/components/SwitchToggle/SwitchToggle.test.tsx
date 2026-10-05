import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import SwitchToggle from './SwitchToggle.component';

describe('SwitchToggle', () => {
  it('render the SwitchToggle Component', () => {
    const onToggle = jest.fn();
    const { getByTestId } = render(
      <SwitchToggle first="Map" baseDataTestId="MapGridToggle" second="Grid" onToggle={onToggle}>
        Primary
      </SwitchToggle>
    );
    const toggle = getByTestId('MapGridToggle-SwitchToggle');
    expect(getByTestId('MapGridToggle-first')).toHaveStyle('backgroundColor: "#00798e"');
    toggle.click();
    expect(getByTestId('MapGridToggle-second')).toHaveStyle('backgroundColor: "#00798e"');

    fireEvent.keyUp(toggle, {
      key: 'Enter',
    });
    expect(getByTestId('MapGridToggle-first')).toHaveStyle('backgroundColor: "#00798e"');

    fireEvent.keyUp(toggle, {
      key: ' ',
    });
    expect(getByTestId('MapGridToggle-second')).toHaveStyle('backgroundColor: "#00798e"');
  });
});
