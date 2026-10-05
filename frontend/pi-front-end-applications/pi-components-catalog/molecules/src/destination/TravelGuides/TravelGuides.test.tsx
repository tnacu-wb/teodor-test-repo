import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import TravelGuides from './TravelGuides.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () => 'https://secure2.premierinn.com/example/image.png',
}));

const mockData = [
  {
    picture:
      '/content/dam/pi/websites/desktop/why/premier-everywhere/premier-everywhere/premier-inn-location-500x320.jpg',
    title: 'Promo 1',
    description:
      'Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua.',
    linkText: 'New Hotels',
    linkUrl: 'https://www.premierinn.com/gb/en/why/locations/new-hotels.html',
    linkTarget: '_self',
  },
  {
    title: 'Promo 2',
    description:
      'Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.',
    linkText: 'Airport hotels',
    linkTarget: '_self',
  },
];

const mockProps = {
  data: mockData,
  baseDataTestId: 'DestinationTravelGuides',
};

describe('TravelGuides Section', () => {
  it('should render the TravelGuides section with its cards', () => {
    const { getByText, getAllByRole } = render(<TravelGuides {...mockProps} />);
    expect(getByText('Promo 1')).toBeInTheDocument();
    expect(getByText('Promo 2')).toBeInTheDocument();
    expect(getAllByRole('img').length).toBe(2);
  });

  it('should redirect user to the linkUrl while pressing the button on one of the cards', async () => {
    const { getAllByRole } = render(<TravelGuides {...mockProps} />);
    Object.defineProperty(window, 'location', {
      value: new URL('https://example.com'),
      configurable: true,
      writable: true,
    });
    const cardBtn = getAllByRole('button')[0];

    await waitFor(() => {
      fireEvent.click(cardBtn);
      expect(window.location.href).toBe(
        'https://www.premierinn.com/gb/en/why/locations/new-hotels.html'
      );
    });
  });

  it('should render correctly when there is less than max number of cards', () => {
    const singleCardProps = {
      ...mockProps,
      data: [mockData[0]],
    };

    const { getByTestId, getAllByRole } = render(<TravelGuides {...singleCardProps} />);

    expect(getByTestId('DestinationTravelGuides-Wrapper')).toBeInTheDocument();
    expect(getAllByRole('img').length).toBe(1);
  });
});
