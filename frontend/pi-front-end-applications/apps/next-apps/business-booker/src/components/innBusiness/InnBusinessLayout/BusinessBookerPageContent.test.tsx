import '@testing-library/jest-dom';
import { render } from '@testing-library/react';

import {
  BusinessBookerPageContent,
  BusinessBookerPageContentProps,
} from './BusinessBookerPageContent';

const mockProps: BusinessBookerPageContentProps = {
  isHotelDetailsPage: true,
};

describe('BusinessBookerPageContent', () => {
  it('should render the basket container', function () {
    const { getByTestId } = render(<BusinessBookerPageContent {...mockProps} />);

    expect(getByTestId('HotelDetailsBasket')).toBeInTheDocument();
  });
});
