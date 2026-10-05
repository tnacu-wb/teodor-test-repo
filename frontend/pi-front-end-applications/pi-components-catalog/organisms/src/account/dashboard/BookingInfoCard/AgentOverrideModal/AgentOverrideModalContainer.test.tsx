import '@testing-library/jest-dom';

import { fireEvent, render, waitFor } from '../../../../utils/test-utils';
import { mockOverrideReasonsResponse } from '../mockResponse';
import AgentOverrideModalContainer, { Props } from './AgentOverrideModal.container';

const mockProps: Props = {
  basketReference: 'AKU2084403',
  hotelId: 'LONEUS',
  reasons: mockOverrideReasonsResponse.data.cancellationReasons.cancellationReasons,
  isVisible: true,
  onClose: jest.fn(),
  getBookingInfo: jest.fn(),
  overridenUserInfo: {
    reservationOverrideReasons: {
      callerName: 'test testsdfsd',
      managerName: 'test manager',
      reasonName: 'DUP',
      reasonCode: 'DUP',
    },
    reservationOverridden: true,
  },
  error: '',
};

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => ({
    locale: 'en',
  }),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => ({
    mutation: {
      mutate: jest.fn(),
    },
    isSuccess: true,
  }),
}));

const Component = (props) => {
  return <AgentOverrideModalContainer {...props} />;
};
describe('AgentOverrideModal Container', () => {
  it('should render AgentOverrideModalContainer', () => {
    const { getByTestId } = render(
      <Component
        {...{
          ...mockProps,
          getBookingInfo: null,
          overridenUserInfo: { ...mockProps.overridenUserInfo, reservationOverridden: false },
        }}
      />
    );
    expect(getByTestId('AgentOverride-Container')).toBeInTheDocument();
  });

  it('should trigger onSubmit function', async () => {
    const { getByTestId } = render(<Component {...mockProps} />);

    await waitFor(() => {
      const submitBtn = getByTestId('AgentOverrideModal-Submit-Button');
      fireEvent.click(submitBtn);

      expect(getByTestId('AgentOverrideModal-Submit-Button')).toBeInTheDocument();
    });
  });
});
