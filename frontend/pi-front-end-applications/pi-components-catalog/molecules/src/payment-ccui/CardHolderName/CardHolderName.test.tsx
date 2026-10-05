import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import CardHolderName from './CardHolderName.component';

const mockSetValue = jest.fn();
const mockSetHasError = jest.fn();

const data = {
  cardHolderNames: {
    firstName: '',
    lastName: '',
  },
  setCardHolderNames: mockSetValue,
  setHasError: mockSetHasError,
};

describe('<CardHolderName />', () => {
  it('should render the component with default props', () => {
    const { getByTestId, getAllByRole } = render(<CardHolderName {...data} />);
    const inputFirstName: HTMLInputElement = getByTestId(
      'input-cardHolderFirstName'
    ) as HTMLInputElement;

    const inputLastName: HTMLInputElement = getByTestId(
      'input-cardHolderLastName'
    ) as HTMLInputElement;

    expect(getByTestId('cardHolderNameSection')).toBeInTheDocument();
    expect(inputFirstName).toBeInTheDocument();
    expect(inputLastName).toBeInTheDocument();
    expect(inputFirstName.value).toEqual('');
    expect(inputFirstName.placeholder).toBe('ccui.cardHolderName.firstName.placeholder');
    expect(inputLastName.value).toEqual('');
    expect(inputLastName.placeholder).toBe('ccui.cardHolderName.lastName.placeholder');
    expect(getAllByRole('textbox')).toHaveLength(2);
  });

  it('should update value on first name input change', () => {
    const { getByTestId } = render(<CardHolderName {...data} />);
    const inputFirstName: HTMLInputElement = getByTestId(
      'input-cardHolderFirstName'
    ) as HTMLInputElement;

    fireEvent.focus(inputFirstName, { target: { value: 'John' } });

    expect(inputFirstName.value).toEqual('John');

    fireEvent.change(inputFirstName, { target: { value: 'John' } });

    expect(mockSetValue).toBeCalled();
    expect(mockSetHasError).toBeCalledWith(false);
  });
  it('should update value on last name input change', () => {
    const { getByTestId } = render(<CardHolderName {...data} />);
    const inputLastName: HTMLInputElement = getByTestId(
      'input-cardHolderLastName'
    ) as HTMLInputElement;

    fireEvent.focus(inputLastName, { target: { value: 'Kennedy' } });

    expect(inputLastName.value).toEqual('Kennedy');

    fireEvent.change(inputLastName, { target: { value: 'Kennedy' } });

    expect(mockSetValue).toBeCalled();
    expect(mockSetHasError).toBeCalledWith(false);
  });

  it('should set an error state when invalid first name is typed', () => {
    const { getByTestId, getByText } = render(<CardHolderName {...data} />);
    const input: HTMLInputElement = getByTestId('input-cardHolderFirstName') as HTMLInputElement;

    fireEvent.change(input, { target: { value: '.,/,./.,/' } });
    expect(getByText('ccui.cardHolderName.firstName.invalidMessage')).toBeInTheDocument();
    expect(mockSetValue).toHaveBeenCalled();
    expect(mockSetHasError).toBeCalledWith(true);
  });

  it('should set an error message when invalid first name is typed', () => {
    const { getByTestId, getByText } = render(<CardHolderName {...data} />);
    const input: HTMLInputElement = getByTestId('input-cardHolderFirstName') as HTMLInputElement;

    fireEvent.change(input, { target: { value: '.,/,./.,/' } });
    expect(getByText('ccui.cardHolderName.firstName.invalidMessage')).toBeInTheDocument();
  });

  it('should set an error state when invalid last name is typed', () => {
    const { getByTestId, getByText } = render(<CardHolderName {...data} />);
    const input: HTMLInputElement = getByTestId('input-cardHolderLastName') as HTMLInputElement;

    fireEvent.change(input, { target: { value: '.,/,./.,/' } });

    expect(getByText('ccui.cardHolderName.lastName.invalidMessage')).toBeInTheDocument();
    expect(mockSetValue).toHaveBeenCalled();
    expect(mockSetHasError).toBeCalledWith(true);
  });

  it('should set an error message when invalid last name is typed', () => {
    const { getByTestId, getByText } = render(<CardHolderName {...data} />);
    const input: HTMLInputElement = getByTestId('input-cardHolderLastName') as HTMLInputElement;

    fireEvent.change(input, { target: { value: '.,/,./.,/' } });

    expect(getByText('ccui.cardHolderName.lastName.invalidMessage')).toBeInTheDocument();
  });

  it('should set an error state when no first name is typed', () => {
    const { getByTestId, getByText } = render(
      <CardHolderName {...data} cardHolderNames={{ firstName: 'Johnny', lastName: 'Pete' }} />
    );

    const inputLastName: HTMLInputElement = getByTestId(
      'input-cardHolderLastName'
    ) as HTMLInputElement;
    const inputFirstName: HTMLInputElement = getByTestId(
      'input-cardHolderFirstName'
    ) as HTMLInputElement;

    fireEvent.change(inputFirstName, { target: { value: '' } });
    fireEvent.change(inputLastName, { target: { value: 'Kennedy' } });

    expect(mockSetValue).toHaveBeenCalled();
    expect(getByText('ccui.cardHolderName.firstName.requiredMessage')).toBeInTheDocument();
    expect(mockSetHasError).toBeCalledWith(true);
  });

  it('should set an error state when no last name is typed', () => {
    const { getByTestId, getByText } = render(
      <CardHolderName {...data} cardHolderNames={{ firstName: 'Johnny', lastName: 'Pete' }} />
    );
    const inputLastName: HTMLInputElement = getByTestId(
      'input-cardHolderLastName'
    ) as HTMLInputElement;
    const inputFirstName: HTMLInputElement = getByTestId(
      'input-cardHolderFirstName'
    ) as HTMLInputElement;

    fireEvent.change(inputFirstName, { target: { value: 'Kennedy' } });
    fireEvent.change(inputLastName, { target: { value: '' } });

    expect(mockSetValue).toHaveBeenCalled();
    expect(getByText('ccui.cardHolderName.lastName.requiredMessage')).toBeInTheDocument();
    expect(mockSetHasError).toBeCalledWith(true);
  });

  it('should set an error state when the inputs are empty', () => {
    const { getByTestId, getByText } = render(
      <CardHolderName {...data} cardHolderNames={{ firstName: 'Johnny', lastName: 'Pete' }} />
    );
    const inputLastName: HTMLInputElement = getByTestId(
      'input-cardHolderLastName'
    ) as HTMLInputElement;
    const inputFirstName: HTMLInputElement = getByTestId(
      'input-cardHolderFirstName'
    ) as HTMLInputElement;

    fireEvent.change(inputFirstName, { target: { value: '' } });
    fireEvent.change(inputLastName, { target: { value: '' } });

    expect(mockSetHasError).toBeCalledWith(true);
    expect(getByText('ccui.cardHolderName.firstName.requiredMessage')).toBeInTheDocument();
    expect(getByText('ccui.cardHolderName.lastName.requiredMessage')).toBeInTheDocument();
  });
});
