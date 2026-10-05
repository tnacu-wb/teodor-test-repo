import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import HotelDiscountApplied from './HotelDiscountApplied.component';

describe('SRP - Hotel Discount applied', () => {
  it('Should display the Discount applied label (e.g. for Employee offer flow)', () => {
    const { getByText } = render(
      <HotelDiscountApplied label="Discount applied" testId="SRP-hotel-discount-applied" />
    );
    expect(getByText('Discount applied')).toBeInTheDocument();
  });
});
