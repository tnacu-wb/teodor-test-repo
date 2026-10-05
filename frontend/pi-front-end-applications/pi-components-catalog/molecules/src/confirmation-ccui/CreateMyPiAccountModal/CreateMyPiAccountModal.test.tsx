import '@testing-library/jest-dom';

import { fireEvent, render } from '../../../src/utils/test-utils';
import CreateMyPiAccountContainer from './CreateMyPiAccountModal.container';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: (val: string) => val,
}));

type useMutationRequestType = {
  isLoading: boolean;
  isSuccess: boolean;
};

const mockUseMutationRequest: useMutationRequestType = {
  isLoading: false,
  isSuccess: false,
};

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useMutationRequest: () => mockUseMutationRequest,
}));

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

const clearRequestMockup = (): void => {
  mockUseMutationRequest.isLoading = false;
  mockUseMutationRequest.isSuccess = false;
};

const handleOnModalClose = jest.fn();

describe('<CreateMyPiAccountModalContainer>', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  beforeEach(() => {
    clearRequestMockup();
  });

  it('should render CreateMyPiAccountModalContainer', () => {
    const { getByTestId } = render(
      <CreateMyPiAccountContainer isModalVisible onModalClose={jest.fn()} />
    );
    expect(getByTestId('CreateMyPiAccountContainer')).toBeInTheDocument();
  });

  it('it should render the ResendConfirmationModal and click close modal', async () => {
    const { getByTestId } = render(
      <CreateMyPiAccountContainer isModalVisible onModalClose={handleOnModalClose} />
    );

    const closeModal = getByTestId('ModalCloseButton');
    fireEvent.click(closeModal);
    expect(handleOnModalClose).toBeCalled();
  });
});
