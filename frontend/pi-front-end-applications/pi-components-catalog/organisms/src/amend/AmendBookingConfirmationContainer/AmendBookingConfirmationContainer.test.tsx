import AmendBookingConfirmationContainer from '.';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';
import { Area, CONFIRM_AMEND_STATUS } from '@whitbread-eos/api';
import { NextRouter } from 'next/router';

import { render } from '../../utils/test-utils';

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => {
    return {
      router: {
        locale: 'en',
      },
    };
  },
}));

jest.mock('next/image', () => ({
  __esModule: true,
  default: () => {
    return 'Next image stub';
  },
}));

jest.mock('../../account/dashboard/BookingInfoCardWrapper/BookingInfoCardWrapper', () => {
  const BookingInfoCardWrapper = () => <div data-testid="BookingInfoCardWrapper-id"></div>;
  BookingInfoCardWrapper.displayName = 'BookingInfoCardWrapper';
  return BookingInfoCardWrapper;
});
jest.mock('../../search/pi/SearchQueryWrapper.tsx', () => {
  const PISearchContainer = () => <div data-testid="PISearchContainer-id"></div>;
  PISearchContainer.displayName = 'PISearchContainer';
  return PISearchContainer;
});
jest.mock('../../search/bb/Search.container.tsx', () => {
  const BBSearchContainer = () => <div data-testid="BBSearchContainer-id"></div>;
  BBSearchContainer.displayName = 'BBSearchContainer';
  return BBSearchContainer;
});
jest.mock('../../search/ccui/Search.container.tsx', () => {
  const CCUISearchContainer = () => <div data-testid="CCUISearchContainer-id"></div>;
  CCUISearchContainer.displayName = 'CCUISearchContainer';
  return CCUISearchContainer;
});

describe('Amend Booking Confirmation Container', () => {
  let queryClient;
  let router;

  beforeEach(() => {
    queryClient = new QueryClient();
    router = { query: {} } as NextRouter;
  });

  const testVariant = (variant: any, additionalProps = {}) => {
    it(`should render the container with ${variant} variant`, () => {
      router.query.variant = variant;
      const commonProps = {
        queryClient: queryClient,
        router: router,
        basketReference: 'AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c',
        bookingReference: 'AWM8159458',
        variant: variant,
      };
      const props = { ...commonProps, ...additionalProps };

      const { getByTestId } = render(
        <QueryClientProvider client={queryClient}>
          <AmendBookingConfirmationContainer {...props} />
        </QueryClientProvider>
      );

      expect(getByTestId('booking-confirmation-wrapper')).toBeInTheDocument();
    });
  };

  const variants = [Area.PI, Area.BB, Area.CCUI, undefined];
  variants.forEach((variant) => {
    testVariant(variant, {
      amendBookingStatus:
        variant === Area.PI ? CONFIRM_AMEND_STATUS.error : CONFIRM_AMEND_STATUS.success,
    });
  });

  it('should render warning Notification', () => {
    router.query.variant = 'PI';
    const { getByText } = render(
      <QueryClientProvider client={queryClient}>
        <AmendBookingConfirmationContainer
          queryClient={queryClient}
          router={router}
          basketReference="AWM-782e1dee-e75e-4f85-9741-c7acf3d31d6c"
          bookingReference="AWM8159458"
          variant={Area.PI}
          amendBookingStatus={undefined}
        />
      </QueryClientProvider>
    );

    expect(getByText('errors.sorry')).toBeInTheDocument();
  });
});
