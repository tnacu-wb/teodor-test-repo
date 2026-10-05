import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import IdvCheckbox from './IdvCheckbox.component';

const mockedProps = {
  text: 'TestRadio',
  value: 'ValueRadio',
  dataTestId: 'Idv',
  partOfSearch: true,
};

describe('Idv Checbkox component', () => {
  it('should render the idv checkbox with props', () => {
    const { getByTestId } = render(<IdvCheckbox {...mockedProps} />);

    expect(getByTestId('Idv-Checkbox')).toBeInTheDocument();
  });

  it('should render labels ', () => {
    const { getByText } = render(<IdvCheckbox {...mockedProps} />);

    expect(getByText('TestRadio')).toBeInTheDocument();
    expect(getByText('ValueRadio')).toBeInTheDocument();
  });
});
