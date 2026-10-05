import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';

import { SelectedHotelsList } from './SelectedHotelsList';

jest.mock('next/image', () => ({
  __esModule: true,
  default: (props: any) => <img {...props} />,
}));

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => {
      const translations = {
        'company.coMngt.alerts.hotel.delete': 'Remove',
        'global.some.key': 'Some Value',
      };
      return translations[key as keyof typeof translations] || key;
    },
  }),
  cn: (...args: string[]) => args.filter(Boolean).join(' '),
}));

describe('SelectedHotelsList', () => {
  const mockSelectedHotels: any = [
    { id: 'hotel1', brand: 'premier-inn', suggestion: 'Premier Inn London' },
    { id: 'hotel2', brand: 'hub', suggestion: 'Hub Edinburgh' },
  ];

  const mockGetHotelIcon = (brand: string) => `/icons/${brand}.svg`;
  const mockOnRemoveHotel = jest.fn();

  it('renders selected hotels correctly', () => {
    render(
      <SelectedHotelsList
        selectedHotels={mockSelectedHotels}
        onRemoveHotel={mockOnRemoveHotel}
        getHotelIcon={mockGetHotelIcon}
      />
    );

    expect(screen.getByTestId('SelectedHotelsList-selected-list-container')).toBeInTheDocument();
    expect(screen.getByTestId('SelectedHotelsList-selected-list')).toBeInTheDocument();

    expect(screen.getByTestId('SelectedHotelsList-selected-item-hotel1')).toBeInTheDocument();
    expect(screen.getByTestId('SelectedHotelsList-selected-item-hotel2')).toBeInTheDocument();

    expect(screen.getByText('Premier Inn London')).toBeInTheDocument();
    expect(screen.getByText('Hub Edinburgh')).toBeInTheDocument();

    const icons = screen.getAllByRole('img');
    expect(icons).toHaveLength(2);
    expect(icons[0]).toHaveAttribute('src', '/icons/premier-inn.svg');
    expect(icons[1]).toHaveAttribute('src', '/icons/hub.svg');
  });

  it('calls onRemoveHotel when remove button is clicked', () => {
    render(
      <SelectedHotelsList
        selectedHotels={mockSelectedHotels}
        onRemoveHotel={mockOnRemoveHotel}
        getHotelIcon={mockGetHotelIcon}
      />
    );

    const removeButton = screen.getByTestId('SelectedHotelsList-remove-button-hotel1');
    fireEvent.click(removeButton);

    expect(mockOnRemoveHotel).toHaveBeenCalledTimes(1);
    expect(mockOnRemoveHotel).toHaveBeenCalledWith('hotel1');
  });

  it('applies scrollable class when there are more than 10 hotels', () => {
    const manyHotels = Array(15)
      .fill(null)
      .map((_, index) => ({
        id: `hotel${index}`,
        brand: 'premier-inn',
        suggestion: `Premier Inn Location ${index}`,
      }));

    render(
      <SelectedHotelsList
        selectedHotels={manyHotels as any}
        onRemoveHotel={mockOnRemoveHotel}
        getHotelIcon={mockGetHotelIcon}
      />
    );

    const listElement = screen.getByTestId('SelectedHotelsList-selected-list');
    expect(listElement).toHaveClass('w-full');
    expect(listElement).toHaveClass('space-y-4');
    expect(listElement).toHaveClass('overflow-y-auto');
    expect(listElement).toHaveClass('max-h-[30rem]');
  });
});
