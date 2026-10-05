import '@testing-library/jest-dom';

import { fireEvent, render } from '../../utils/test-utils';
import BookersReferenceDetails from './BookersReferenceDetails';

const mockSetValue = jest.fn();
const mockSetHasError = jest.fn();

const data = {
  bookerReferencesDetails: {
    purchaseOrderNumber: '',
    companyReference: '',
  },
  setBookerReferencesDetails: mockSetValue,
  setHasError: mockSetHasError,
};

describe('<BookersReferenceDetails />', () => {
  it('should render the component with default props', () => {
    const { getByTestId, getAllByRole } = render(<BookersReferenceDetails {...data} />);
    const purchaseOrderNumber: HTMLInputElement = getByTestId(
      'input-purchaseOrderNumber'
    ) as HTMLInputElement;

    const companyReference: HTMLInputElement = getByTestId(
      'input-companyReference'
    ) as HTMLInputElement;

    expect(getByTestId('bookersReferenceDetailsSection')).toBeInTheDocument();
    expect(purchaseOrderNumber).toBeInTheDocument();
    expect(companyReference).toBeInTheDocument();
    expect(purchaseOrderNumber.value).toEqual('');
    expect(purchaseOrderNumber.placeholder).toBe('ccui.purchaseOrderNumber.placeholder');
    expect(companyReference.value).toEqual('');
    expect(companyReference.placeholder).toBe('ccui.companyReference.placeholder');
    expect(getAllByRole('textbox')).toHaveLength(2);
  });

  it('should update value on purchase order number input change', () => {
    const { getByTestId } = render(<BookersReferenceDetails {...data} />);
    const purchaseOrderNumber: HTMLInputElement = getByTestId(
      'input-purchaseOrderNumber'
    ) as HTMLInputElement;

    fireEvent.focus(purchaseOrderNumber, { target: { value: '123456' } });

    expect(purchaseOrderNumber.value).toEqual('123456');

    fireEvent.change(purchaseOrderNumber, { target: { value: '123456' } });

    expect(mockSetValue).toBeCalled();
    expect(mockSetHasError).toBeCalledWith(false);
  });
  it('should update value on last name input change', () => {
    const { getByTestId } = render(<BookersReferenceDetails {...data} />);
    const companyReference: HTMLInputElement = getByTestId(
      'input-companyReference'
    ) as HTMLInputElement;

    fireEvent.focus(companyReference, { target: { value: 'AWS13333' } });

    expect(companyReference.value).toEqual('AWS13333');

    fireEvent.change(companyReference, { target: { value: 'AWS13333' } });

    expect(mockSetValue).toBeCalled();
    expect(mockSetHasError).toBeCalledWith(false);
  });

  it('should set an error state when invalid purchaseOrderNumber is typed', () => {
    const { getByTestId } = render(<BookersReferenceDetails {...data} />);
    const purchaseOrderNumber: HTMLInputElement = getByTestId(
      'input-purchaseOrderNumber'
    ) as HTMLInputElement;
    const companyReference: HTMLInputElement = getByTestId(
      'input-companyReference'
    ) as HTMLInputElement;

    fireEvent.change(purchaseOrderNumber, {
      target: {
        value: '123123123123123123123123123123123123123123123123123123123123123123123123123',
      },
    });
    fireEvent.change(companyReference, {
      target: {
        value: '123123123123123123123123123123123123123123123123123123123123123123123123123',
      },
    });
    expect(mockSetValue).toHaveBeenCalled();
    expect(mockSetHasError).toBeCalledWith(true);
  });
});
