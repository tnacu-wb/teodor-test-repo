import '@testing-library/jest-dom';
import { act, fireEvent, render, waitFor } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import DeletePayAppCard from './delete-pay-app-card';

jest.mock('next/navigation', () => ({
  ...jest.requireActual('next/navigation'),
  usePathname: () => {
    return '/';
  },
  useRouter: () => ({
    push: jest.fn(),
  }),
}));

const mockUpdateResponse = {
  status: 'success',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    getCommonIcons: () => ({}),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    TranslationProvider: ({ children }: { children: React.ReactNode }) => <>{children}</>,
    deletePayAppCard: () => mockUpdateResponse,
  };
});

global.ResizeObserver = jest.fn().mockImplementation(() => ({
  observe: jest.fn(),
  unobserve: jest.fn(),
  disconnect: jest.fn(),
}));

jest.mock('@whitbread-eos/layout', () => {
  return {
    ...jest.requireActual('@whitbread-eos/layout'),
    useWizardContext: () => ({
      wizardState: {},
      setWizardState: (...args: any[]) => jest.fn(...args),
      goToNextStep: (...args: any[]) => jest.fn(...args),
    }),
  };
});

const mockProps = {
  applicationGuid: '123',
  applicationId: '123',
  cardGuid: 'card-123',
};

describe('Delete pay app card component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render DeletePayAppCard component', async () => {
    const { getByTestId } = render(<DeletePayAppCard {...mockProps} />);

    const openModalButton = getByTestId('Delete-PayAppCard-Button');
    expect(openModalButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(openModalButton);
    });

    await waitFor(() => {
      expect(openModalButton).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.click(getByTestId('Delete-PayAppCard-Cancel-Button'));
      fireEvent.click(openModalButton);
      fireEvent.click(getByTestId('Delete-PayAppCard-Submit-Button'));
    });
  });
});
