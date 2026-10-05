import '@testing-library/jest-dom';
import { TypeOfCaller as TypeOfCallerEnum } from '@whitbread-eos/api';

import { fireEvent, render } from '../../utils/test-utils';
import TypeOfCaller from './TypeOfCaller.component';

const mockSetValue = jest.fn().mockImplementation();

const data = {
  value: '',
  setValue: mockSetValue,
};

describe('Type of caller Section ', () => {
  it('should render the component with default props', () => {
    const { getByTestId } = render(<TypeOfCaller {...data} />);
    expect(getByTestId('typeOfCaller')).toBeInTheDocument();
  });
  it('by default radio buttons are not checked ', () => {
    const { getAllByRole, getByText } = render(<TypeOfCaller {...data} />);
    const radioButtonAnyCustomer = getAllByRole('radio')[0];
    const radioButtonAccesibleCustomer = getAllByRole('radio')[1];

    expect(getByText('ccui.typeOfCaller.anyCustomer')).toBeInTheDocument();
    expect(getByText('ccui.typeOfCaller.accesibleCustomer')).toBeInTheDocument();
    expect(getAllByRole('radio')).toHaveLength(2);
    expect(radioButtonAnyCustomer).not.toBeChecked();
    expect(radioButtonAccesibleCustomer).not.toBeChecked();
  });
  it('should check if the radio button is checked', () => {
    const { getAllByRole } = render(<TypeOfCaller {...data} />);

    const radioButtonAnyCustomer = getAllByRole('radio')[0];
    const radioButtonAccesibleCustomer = getAllByRole('radio')[1];

    fireEvent.click(radioButtonAnyCustomer);
    expect(mockSetValue).toHaveBeenCalledWith(TypeOfCallerEnum.ANY_CUSTOMER);
    fireEvent.click(radioButtonAccesibleCustomer);
    expect(mockSetValue).toHaveBeenCalledWith(TypeOfCallerEnum.ACCESIBLE_CUSTOMER);
  });
});
