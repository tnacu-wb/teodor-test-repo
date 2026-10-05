import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import AddSubtract from './AddSubtract.component';

const value = 0;
const mockCallSubtract = jest.fn();
const mockCallPlus = jest.fn();

const props = {
  onSubtract: mockCallSubtract,
  onPlus: mockCallPlus,
  label: 'test',
  isSubtractDisable: value === 0,
  value: value,
  isPlusDisable: value >= 2,
  isEditable: false,
};

describe('AddSubtract', () => {
  beforeEach(() => {
    jest.resetAllMocks();
  });
  it('render the <AddSubtract/> with default props', () => {
    props.isEditable = false;
    const { getByText, getByTestId, queryByTestId } = render(<AddSubtract {...props} />);
    expect(getByText(value)).toBeInTheDocument();
    expect(getByTestId('Label')).toBeInTheDocument();
    expect(getByTestId('Value')).toBeInTheDocument();
    expect(getByTestId('SubtractButton')).toBeInTheDocument();
    expect(getByTestId('AddButton')).toBeInTheDocument();
    expect(queryByTestId('inputValue')).not.toBeInTheDocument();
  });

  it('render the <AddSubtract/> with data-testid', () => {
    const { getByTestId } = render(<AddSubtract {...props} prefixDataTestId="test" />);

    expect(getByTestId('test-Label')).toBeInTheDocument();
    expect(getByTestId('test-SubtractButton')).toBeInTheDocument();
    expect(getByTestId('test-Value')).toBeInTheDocument();
    expect(getByTestId('test-AddButton')).toBeInTheDocument();
  });

  it('render the <AddSubtract/> and subtract is disabled and plus enabled', () => {
    const { getByTestId } = render(<AddSubtract {...props} />);
    const subtractButton = getByTestId('SubtractButton');
    const plusButton = getByTestId('AddButton');

    expect(subtractButton).toBeDisabled();
    expect(plusButton).toBeEnabled();

    fireEvent.click(plusButton);
    expect(mockCallPlus).toBeCalledTimes(1);

    fireEvent.click(subtractButton);
    expect(mockCallSubtract).toBeCalledTimes(0);
  });

  it('render the <AddSubtract/> and plus is disabled and subtract enabled', () => {
    const { getByTestId } = render(
      <AddSubtract {...props} isPlusDisable={true} isSubtractDisable={false} />
    );
    const subtractButton = getByTestId('SubtractButton');
    const plusButton = getByTestId('AddButton');

    expect(plusButton).toBeDisabled();
    expect(subtractButton).toBeEnabled();

    fireEvent.click(plusButton);
    expect(mockCallPlus).toBeCalledTimes(0);

    fireEvent.click(subtractButton);
    expect(mockCallSubtract).toBeCalledTimes(1);
  });

  it('render the editable <AddSubtract/> component with input field', () => {
    props.isEditable = true;

    const { getByTestId, queryByTestId, queryByText } = render(<AddSubtract {...props} />);
    expect(getByTestId('Label')).toBeInTheDocument();
    expect(getByTestId('SubtractButton')).toBeInTheDocument();
    expect(getByTestId('AddButton')).toBeInTheDocument();
    // editable text field present
    expect(getByTestId('inputValue')).toBeInTheDocument();
    // non-editable text not present
    expect(queryByTestId('Value')).not.toBeInTheDocument();
    expect(queryByText(value)).not.toBeInTheDocument();
  });
});
