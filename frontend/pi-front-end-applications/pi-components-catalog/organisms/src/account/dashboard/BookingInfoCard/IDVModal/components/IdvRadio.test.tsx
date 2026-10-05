import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import IdvRadioComponent from './IdvRadio.component';

const mockedProps = {
  text: 'Test',
  value: true,
  options: [
    { text: 'Test1', value: true },
    { text: 'Test2', value: false },
  ],
  onChange: jest.fn(),
  dataTestId: 'TestIdvRadio',
};

describe('Idv Radio component', () => {
  it('should render the idv radio label with props', () => {
    const { getByTestId } = render(<IdvRadioComponent {...mockedProps} />);

    expect(getByTestId('TestIdvRadio-Label')).toBeInTheDocument();
  });
  it('should render the idv radio group with props', () => {
    const { getByTestId } = render(<IdvRadioComponent {...mockedProps} />);

    expect(getByTestId('TestIdvRadio-RadioGroup')).toBeInTheDocument();
  });

  it('should render 2 radio buttons in group', () => {
    const { getByTestId } = render(<IdvRadioComponent {...mockedProps} />);

    expect(getByTestId('TestIdvRadio-RadioOption-TRUE')).toBeInTheDocument();
    expect(getByTestId('TestIdvRadio-RadioOption-FALSE')).toBeInTheDocument();
  });
});
