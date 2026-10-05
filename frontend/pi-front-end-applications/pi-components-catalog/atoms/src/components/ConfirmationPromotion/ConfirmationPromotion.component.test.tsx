import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import ConfirmationPromotion, { Props } from './ConfirmationPromotion.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockPromotionData: Props = {
  routerPush: jest.fn(),
  data: {
    promotionPanel: [
      {
        image: '/content/dam/pi/websites/desktop/booking/bed-shop-promo.jpg',
        name: 'Promotion title',
        description: '<p>This is the promotion description.</p>\n',
        linkLabel: 'Promotion link',
        linkPath:
          'https://www.premierinn.com/gb/en/why/sleep/buy-our-bed.html?INTCMP=BEDSHOP_confirmationPanel',
      },
    ],
  },
};

describe('ConfirmationPromotion component', () => {
  it('should render promotion component correctly', () => {
    const { getByTestId, getByAltText } = render(<ConfirmationPromotion {...mockPromotionData} />);
    const promoImageWrapper = getByTestId('promo-image');
    const promoImage = getByAltText('Premier Inn Promo-Image');
    expect(promoImageWrapper).toBeInTheDocument();
    expect(promoImage).toHaveAttribute('src', 'https://secure2.premierinn.com/example/image.png');

    expect(getByTestId('promo-title')).toHaveTextContent('Promotion title');
    expect(getByTestId('promo-description')).toHaveTextContent(
      'This is the promotion description.'
    );

    const promoLink = getByTestId('promo-link');
    expect(promoLink).toBeInTheDocument();
    expect(promoLink).toHaveTextContent('Promotion link');
  });

  it('should not render anything if there is no promotion panel data', () => {
    const { container } = render(
      <ConfirmationPromotion data={{ promotionPanel: [] }} routerPush={jest.fn()} />
    );
    expect(container.firstChild).toBeNull();
  });

  it('calls routerPush with the correct URL when the promo link button is clicked', () => {
    const { getByTestId } = render(<ConfirmationPromotion {...mockPromotionData} />);

    fireEvent.click(getByTestId('promo-link'));

    expect(mockPromotionData.routerPush).toHaveBeenCalledWith(
      'https://www.premierinn.com/gb/en/why/sleep/buy-our-bed.html?INTCMP=BEDSHOP_confirmationPanel'
    );
  });
});
