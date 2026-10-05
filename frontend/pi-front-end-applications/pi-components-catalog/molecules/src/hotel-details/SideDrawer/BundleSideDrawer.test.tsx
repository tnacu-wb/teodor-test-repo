import '@testing-library/jest-dom';

import { fireEvent, render, act, waitFor } from '../../utils/test-utils';
import { BundleSideDrawer } from './BundleSideDrawer.component';

const mockProps = {
  visible: true,
  onClose: jest.fn(),
  packages: [
    {
      name: 'Extra 1',
      description: 'Description for Extra 1',
      image: 'extra1.jpg',
      price: 100,
    },
    {
      name: 'Extra 2',
      description: 'Description for Extra 2',
      image: 'extra2.jpg',
      price: 100,
    },
  ],
  numberOfNights: 2,
  adultsNumber: 1,
  currency: 'GBP',
  language: 'en',
};

describe('BundleSideDrawer', () => {
  it('should not render BundleSideDrawer when not visible', () => {
    const { queryByTestId } = render(<BundleSideDrawer {...mockProps} visible={false} />);

    expect(queryByTestId('side-drawer')).not.toBeInTheDocument();
  });

  it('should call onClose when the close button is clicked', async () => {
    const { getByTestId } = render(<BundleSideDrawer {...mockProps} />);
    await act(async () => {
      fireEvent.click(getByTestId('Dismiss-Side-Drawer'));
    });

    await waitFor(() => {
      expect(mockProps.onClose).toHaveBeenCalled();
    });
  });

  it('should display multiple extras correctly', () => {
    const { getByText } = render(<BundleSideDrawer {...mockProps} />);

    expect(getByText('Extra 1')).toBeInTheDocument();
    expect(getByText('Extra 2')).toBeInTheDocument();
  });

  it('should display the single extra option', () => {
    const { getByText } = render(
      <BundleSideDrawer
        {...mockProps}
        packages={[
          {
            name: 'Extra 1',
            description: 'Description for Extra 1',
            image: 'extra2.jpg',
            price: undefined,
            links: ['Link1', 'Link2'],
          },
        ]}
      />
    );

    expect(getByText('Extra 1')).toBeInTheDocument();
  });

  it('should display the single extra option with one night', () => {
    const { getByText } = render(
      <BundleSideDrawer
        {...mockProps}
        numberOfNights={1}
        packages={[
          {
            name: 'Extra 1',
            description: 'Description for Extra 1',
            image: 'extra2.jpg',
            price: undefined,
            links: ['Link1', 'Link2'],
          },
        ]}
      />
    );

    expect(getByText('Extra 1')).toBeInTheDocument();
  });

  it('should display the single extra option with one night and price', () => {
    const { getByText } = render(
      <BundleSideDrawer
        {...mockProps}
        numberOfNights={1}
        packages={[
          {
            name: 'Extra 1',
            description: 'Description for Extra 1',
            image: 'extra2.jpg',
            price: 12,
            links: ['Link1', 'Link2'],
          },
        ]}
      />
    );

    expect(getByText('Extra 1')).toBeInTheDocument();
  });

  it('should display the correct price and count in adultsNumber and nightsNumber', () => {
    const adultsNumber = 2;
    const nightsNumber = 2;
    const packagePrice = 12;

    const { getByTestId } = render(
      <BundleSideDrawer
        {...mockProps}
        numberOfNights={nightsNumber}
        adultsNumber={adultsNumber}
        packages={[
          {
            name: 'Extra 1',
            description: 'Description for Extra 1',
            image: 'extra2.jpg',
            price: 12,
            links: ['Link1', 'Link2'],
          },
        ]}
      />
    );
    const expectedTotalPrice = packagePrice * adultsNumber * nightsNumber;

    expect(getByTestId('Side-Drawer-Single-Total-Price')).toHaveTextContent(
      expectedTotalPrice.toString()
    );
  });

  it('should render total price calculated with soft Bundles and count in adults number as 1 when not sent as prop', () => {
    const nightsNumber = 2;
    const packagePrice = 12;

    const { getByTestId } = render(
      <BundleSideDrawer
        {...{ ...mockProps, adultsNumber: undefined }}
        numberOfNights={nightsNumber}
        packages={[
          {
            name: 'Extra 1',
            description: 'Description for Extra 1',
            image: 'extra2.jpg',
            price: 12,
            links: ['Link1', 'Link2'],
          },
        ]}
      />
    );
    const expectedTotalPrice = packagePrice * nightsNumber;

    expect(getByTestId('Side-Drawer-Single-Total-Price')).toHaveTextContent(
      expectedTotalPrice.toString()
    );
  });
});
