import '@testing-library/jest-dom';
import { render } from '@testing-library/react';
import { LOCALES } from '@whitbread-eos/api';

import { CentrallyStored, CentrallyStoredSkeleton } from './centrally-stored';

const mockProps = {
  locale: LOCALES.EN,
  token: {} as string,
  companyId: 'abc',
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getPathForLocale: () => {
      return '/';
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    getTranslations: () => {
      return {
        t: (str: string) => str,
      };
    },
    getSearchParams: () => Promise.resolve(new URLSearchParams()),
    getCardManagementLabels: () => null,
    getCommonIcons: () => ({}),
    formatIBAssetsUrl: () => {
      return '/';
    },
    getPaymentCards: jest.fn().mockResolvedValue([
      {
        cardId: '1',
        cardLabel: 'Test Card',
        cardHolderName: 'John Doe',
        cardNumber: '1234567890123456',
        expiryDate: '12/25',
        cardStatus: 'ACTIVE',
        cardType: 'VISA',
      },
      {
        cardId: '2',
        cardLabel: 'Test Card 2',
        cardHolderName: 'Jane Smith',
        cardNumber: '2345678901234567',
        expiryDate: '06/24',
        cardStatus: 'EXPIRED',
        cardType: 'MASTERCARD',
      },
    ]),
  };
});

jest.mock('~components/innBusiness/CardStatus', () => {
  return { CardStatus: () => null };
});
jest.mock('~components/innBusiness/DataTable', () => {
  return {
    DataTable: ({ columns, getPage, getTotal }: any) => {
      if (getPage) {
        getPage(1, 10);
      }
      if (getTotal) {
        getTotal();
      }

      columns.forEach((column: any) => {
        if (column.render) {
          column.render('1234567890123456', {
            cardId: '1',
            cardType: 'VISA',
            expiryDate: '12/25',
            cardStatus: 'ACTIVE',
          });
        }
      });
      return <div data-testid="DataTable-mock">DataTable</div>;
    },
  };
});

describe('CentrallyStored Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render CentrallyStored component', async () => {
    const { getByTestId } = render(await CentrallyStored(mockProps));

    expect(getByTestId('CentrallyStoredTab-container')).toBeInTheDocument();
    expect(getByTestId('CentrallyStoredTab-add-card-button')).toBeInTheDocument();
    expect(getByTestId('DataTable-mock')).toBeInTheDocument();
  });

  it('should render table with data and call internal functions', async () => {
    const { getByTestId } = render(await CentrallyStored(mockProps));

    expect(getByTestId('CentrallyStoredTab-container')).toBeInTheDocument();

    const { getPaymentCards } = jest.requireMock('@whitbread-eos/utils/server');
    expect(getPaymentCards).toHaveBeenCalled();
  });
});

describe('CentrallyStoredSkeleton Component', () => {
  it('should render CentrallyStoredSkeleton component', () => {
    const { getByTestId } = render(<CentrallyStoredSkeleton />);

    expect(getByTestId('CentrallyStoredTab-Skeleton')).toBeInTheDocument();
  });
});
