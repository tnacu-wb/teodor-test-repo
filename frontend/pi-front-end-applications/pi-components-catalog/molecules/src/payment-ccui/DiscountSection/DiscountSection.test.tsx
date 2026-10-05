import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import DiscountSection from './DiscountSection.component';

const mockSetValue = jest.fn();
const mockSetDiscountValidationError = jest.fn();
const mockonDiscountUpdate = jest.fn();
const mockValidateDiscountValue = jest.fn();

const data = {
  setValue: mockSetValue,
  discountValue: '10',
  currency: '£',
  language: 'en',
  onDiscountUpdate: mockonDiscountUpdate,
  isDiscountServerError: false,
  discountServerError: new Error(),
  isDisabled: false,
  validateDiscountValue: mockValidateDiscountValue,
  discountValidationError: '',
  setDiscountValidationError: mockSetDiscountValidationError,
};

describe('<DiscountSection />', () => {
  it('should render the component with default props', () => {
    const { getByTestId } = render(<DiscountSection {...data} />);
    const input: HTMLInputElement = getByTestId('input-discount') as HTMLInputElement;

    expect(getByTestId('discountSection')).toBeInTheDocument();
    expect(input.value).toEqual('10');
    expect(input.placeholder).toBe('ccui.payment.discount.placeHolder');
    expect(getByTestId('discountInput-c£')).toBeInTheDocument();
  });

  it('should render the input for component', () => {
    const { getByTestId } = render(<DiscountSection {...data} />);
    expect(getByTestId('input-discount')).toBeInTheDocument();
  });

  it('should render the value inside the input', () => {
    const { getByTestId } = render(<DiscountSection {...data} />);
    const input: HTMLInputElement = getByTestId('input-discount') as HTMLInputElement;
    mockSetValue('111');
    fireEvent.focus(input, { target: { value: '111' } });

    expect(input.value).toBe('111');
  });

  it('should render the currency changed', () => {
    const { getByTestId } = render(<DiscountSection {...data} currency="€" />);
    expect(getByTestId('discountInput-c€')).toBeInTheDocument();
  });

  //   TODO: update test when component will have AEM integration
  it('should render the title and description', () => {
    const { queryByText, container } = render(<DiscountSection {...data} />);

    // Title
    expect(queryByText('ccui.payment.discount.title')).toBeTruthy();
    // Description
    expect(queryByText('ccui.payment.discount.description')).toBeTruthy();

    // Title tag
    expect(container.querySelector('h3')).toBeTruthy();
    // Description tag
    expect(container.querySelector('h6')).toBeTruthy();
  });

  it('should call the onDiscountChange method', async () => {
    const { getByTestId } = render(<DiscountSection {...data} />);

    const input = getByTestId('input-discount');

    expect(input).toBeInTheDocument();

    fireEvent.change(input, { target: { value: '111' } });
    expect(mockSetValue).toBeCalledWith('111');
  });

  it('should reset the error when the value is empty', async () => {
    const { getByTestId } = render(<DiscountSection {...data} />);

    const input = getByTestId('input-discount') as HTMLInputElement;

    expect(input).toBeInTheDocument();

    fireEvent.change(input, { target: { value: '' } });

    expect(mockSetDiscountValidationError).toBeCalledWith('');
  });

  it('should show an error if the input value is made of characters', async () => {
    const { getByTestId } = render(<DiscountSection {...data} />);

    const input: HTMLInputElement = getByTestId('input-discount') as HTMLInputElement;

    expect(input).toBeInTheDocument();
    fireEvent.focus(input, { target: { value: 'qwerty' } });

    expect(input.value).toBe('qwerty');

    expect(mockSetValue).not.toBeCalledWith('qwerty');
  });

  it('should show an error if the input value is greater than the maxValue', async () => {
    const { getByTestId } = render(<DiscountSection {...data} />);

    const input: HTMLInputElement = getByTestId('input-discount') as HTMLInputElement;

    expect(input).toBeInTheDocument();

    fireEvent.focus(input, { target: { value: '1000011' } });
    expect(input.value).toBe('1000011');

    expect(mockSetValue).not.toBeCalledWith('1000011');
  });

  it('should call onDiscountUpdate when click is triggered outside of discount section', async () => {
    const { getByTestId } = render(<DiscountSection {...data} />);
    const input = getByTestId('input-discount') as HTMLInputElement;

    expect(input).toBeInTheDocument();

    fireEvent.change(input, { target: { value: '11' } });
    fireEvent.blur(input);

    expect(mockonDiscountUpdate).toBeCalled();
    expect(data.isDiscountServerError).toBeFalsy();
    expect(mockSetDiscountValidationError).toBeCalledWith('');
  });

  it('should have error when the error object is not empty', async () => {
    const { getByTestId } = render(
      <DiscountSection {...data} discountServerError={new Error('Error')} />
    );
    const input = getByTestId('input-discount') as HTMLInputElement;

    expect(input).toBeInTheDocument();

    fireEvent.blur(input);

    expect(mockonDiscountUpdate).toBeCalled();
    expect(data.discountServerError).toBeTruthy();
  });
});
