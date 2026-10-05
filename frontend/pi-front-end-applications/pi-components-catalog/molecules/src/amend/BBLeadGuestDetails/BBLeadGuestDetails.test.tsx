import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import BBLeadGuestDetails from './BBLeadGuestDetails.component';

const props = {
  labels: {},
  validationLabels: {},
  onSubmit: jest.fn(),
  numberOfRooms: 2,
  isEdit: false,
};

describe('<GuestDetailsBBContainer />', () => {
  it('should render the GuestDetailsBBContainer with given props', () => {
    const { getByTestId } = render(
      <QueryClientProvider client={new QueryClient()}>
        <BBLeadGuestDetails
          guestList={undefined}
          setGuestUser={undefined}
          getFormState={undefined}
          queryClient={new QueryClient()}
          isDynamicSearchVisible={false}
          {...props}
        />
      </QueryClientProvider>
    );
    expect(getByTestId('GuestDetailsBBContainer-Form')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Container')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Room-1')).toBeInTheDocument();
    expect(getByTestId('GuestDetailsBBContainer-Form-Room-2')).toBeInTheDocument();
  });
});
