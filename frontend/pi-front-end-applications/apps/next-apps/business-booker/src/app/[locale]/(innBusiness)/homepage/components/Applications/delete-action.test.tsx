import '@testing-library/jest-dom';
import { render, waitFor } from '@testing-library/react';
import { LOCALES, PayApplicationDetails } from '@whitbread-eos/api';

import DeleteAction from './delete-action';

const applicationMock: PayApplicationDetails = {
  applicationGuid: 'applicationGuid',
  applicationId: 'applicationId',
  scheme: 'GB',
};

const mockUseRouter = {
  refresh: jest.fn(),
};
const mockDeleteApplication = jest.fn((...args: unknown[]) => args);

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  useRouter: () => mockUseRouter,
}));

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    getLocaleByPathname: () => LOCALES.EN,
    deleteApplication: (...args: unknown[]) => mockDeleteApplication(...args),
    useTranslation: () => ({
      t: (str: string) => str,
    }),
  };
});

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getAuthCookie: () => 'token',
}));

describe('DeleteAction Component', () => {
  it('should render the delete link', async () => {
    const { getByTestId } = render(
      <DeleteAction application={applicationMock} baseDataTestId="DeleteAction" />
    );

    expect(getByTestId(`DeleteAction`)).toBeInTheDocument();
  });

  it('should open, delete application and close delete application modal', async () => {
    const { getByTestId, queryByTestId } = render(
      <DeleteAction application={applicationMock} baseDataTestId="DeleteAction" />
    );

    await waitFor(() => {
      getByTestId(`DeleteAction`).click();
    });

    await waitFor(() => {
      expect(getByTestId(`DeleteApplicationModal`)).toBeInTheDocument();
    });

    await waitFor(() => {
      getByTestId('DeleteApplicationModal-DeleteButton').click();
    });

    expect(mockDeleteApplication).toBeCalledWith(
      applicationMock.applicationId,
      applicationMock.applicationGuid,
      'GB',
      'token'
    );
    expect(mockUseRouter.refresh).toBeCalled();

    await waitFor(() => {
      expect(queryByTestId(`DeleteApplicationModal`)).not.toBeInTheDocument();
    });
  });
});
