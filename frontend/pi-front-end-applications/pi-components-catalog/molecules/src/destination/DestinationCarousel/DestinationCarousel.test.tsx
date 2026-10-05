import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import DestinationCarousel from './DestinationCarousel.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useScreenSize: () => ({ isLessThanXl: false }),
}));

const mockDlpItem = {
  picture:
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
  title: 'Hotels in London Wembley Stadium',
  link: '/hotels/gb/en/london/london-wembley-stadium',
  order: 1,
};

const mockProps = {
  title: 'Explore other destinations',
  items: Array.from({ length: 5 }).map(() => mockDlpItem),
  baseDataTestId: 'DestinationsList',
};

describe('DestinationCarousel Section', () => {
  it('should render the DestinationCarousel section with its cards', () => {
    const { getByText, getAllByRole } = render(<DestinationCarousel {...mockProps} />);
    expect(getByText('Explore other destinations')).toBeInTheDocument();
    expect(getAllByRole('img').length).toBe(5);
  });

  it('should not render the DestinationCarousel section if there are less than 2 cards', () => {
    const { queryByText } = render(
      <DestinationCarousel
        {...mockProps}
        items={Array.from({ length: 1 }).map(() => mockDlpItem)}
      />
    );
    expect(queryByText('Explore other destinations')).not.toBeInTheDocument();
  });

  it('should not display the fadeEffect if the cards are less than 5', () => {
    const { getAllByRole } = render(
      <DestinationCarousel
        {...mockProps}
        items={Array.from({ length: 4 }).map(() => mockDlpItem)}
      />
    );

    expect(getAllByRole('img').length).toBe(4);
  });

  it('should render specific styling if there are only 3 cards', () => {
    const { getAllByRole } = render(
      <DestinationCarousel
        {...mockProps}
        items={Array.from({ length: 3 }).map(() => mockDlpItem)}
      />
    );

    expect(getAllByRole('img').length).toBe(3);
  });

  it('should render specific styling if there are only 2 cards', () => {
    const { getAllByRole } = render(
      <DestinationCarousel
        {...mockProps}
        items={Array.from({ length: 2 }).map(() => mockDlpItem)}
      />
    );

    expect(getAllByRole('img').length).toBe(2);
  });
});
