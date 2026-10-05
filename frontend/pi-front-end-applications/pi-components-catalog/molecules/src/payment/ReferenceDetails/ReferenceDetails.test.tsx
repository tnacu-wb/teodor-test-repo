import ReferenceDetails from '.';
import { fireEvent } from '@testing-library/dom';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';

const mockedProps = {
  referenceDetails: { reference: '', purchaseOrderNumber: '' },
  updateReference: jest.fn(),
  t: (key: string) => {
    switch (key) {
      default:
        return key;
    }
  },
};

describe('ReferenceDetails', () => {
  it('renders ReferenceDetails empty component', () => {
    const { getByText, getByPlaceholderText } = render(<ReferenceDetails {...mockedProps} />);
    expect(getByText('booking.bac.referenceDetails')).toBeInTheDocument();

    expect(
      getByPlaceholderText('booking.bac.customerReference booking.login.labelOptional')
    ).toBeInTheDocument();
    expect(
      getByPlaceholderText('booking.bac.purchaseOrder booking.login.labelOptional')
    ).toBeInTheDocument();
  });

  it('renders ReferenceDetails component with values', () => {
    mockedProps.referenceDetails = {
      reference: 'Reference 1',
      purchaseOrderNumber: 'Purchase Order Number 1',
    };
    const { getByDisplayValue } = render(<ReferenceDetails {...mockedProps} />);
    expect(getByDisplayValue(mockedProps.referenceDetails.reference)).toBeInTheDocument();
    expect(getByDisplayValue(mockedProps.referenceDetails.purchaseOrderNumber)).toBeInTheDocument();
  });

  it('renders ReferenceDetails and calls updateReference when changing value of input', () => {
    mockedProps.referenceDetails = {
      reference: 'Reference 1',
      purchaseOrderNumber: 'Purchase Order Number 1',
    };
    const { getByDisplayValue } = render(<ReferenceDetails {...mockedProps} />);

    const inputReference = getByDisplayValue(mockedProps.referenceDetails.reference);
    expect(inputReference).toBeInTheDocument();
    fireEvent.change(inputReference, { target: { value: 'Reference 2' } });
    expect(mockedProps.updateReference).toBeCalledWith({
      purchaseOrderNumber: 'Purchase Order Number 1',
      reference: 'Reference 2',
    });

    const inputPurchaseOrderNumber = getByDisplayValue(
      mockedProps.referenceDetails.purchaseOrderNumber
    );
    expect(inputPurchaseOrderNumber).toBeInTheDocument();
    fireEvent.change(inputPurchaseOrderNumber, { target: { value: 'PON 2' } });
    expect(mockedProps.updateReference).toBeCalledWith({
      purchaseOrderNumber: 'PON 2',
      reference: 'Reference 1',
    });
  });
});
