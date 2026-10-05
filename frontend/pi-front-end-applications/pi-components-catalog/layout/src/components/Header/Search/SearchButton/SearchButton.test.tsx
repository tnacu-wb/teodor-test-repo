import { LOCALES, StandardRoomType } from '@whitbread-eos/api';
import { add } from 'date-fns';

import { act, fireEvent, render } from '../../../../utils/test-utils';
import SearchButton from './SearchButton.component';

const mockProps = {
  mobile: false,
  formLabels: {
    searchIcon: '/',
  },
  handleButtonClick: () => {
    return;
  },
  rooms: {
    adults: 1,
    children: 0,
    shouldIncludeCot: false,
    roomType: StandardRoomType.DB,
  },
  location,
  selectedDate: { from: new Date(), to: add(new Date(new Date()), { days: 1 }) },
  dateFromUrl: {
    from: add(new Date(new Date()), { days: 1 }),
    to: add(new Date(new Date()), { days: 2 }),
  },
};

jest.mock('@whitbread-eos/utils/server', () => {
  const serverUtils = jest.requireActual('@whitbread-eos/utils/server');

  return {
    cn: jest.fn(),
    getQueryParams: jest.fn(),
    getLocaleByPathname: () => {
      return LOCALES.EN;
    },
    getCountryLanguageByLocale: serverUtils.getCountryLanguageByLocale,
    formatIBAssetsUrl: () => {
      return '/';
    },
    RolesRequired: serverUtils.RolesRequired,
    useTranslation: () => {
      return {
        t: (str: string) => str,
        i18n: {
          changeLanguage: () => new Promise(() => true),
        },
      };
    },
  };
});

describe('IB SearchButton component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
    mockProps.formLabels = {
      searchIcon: '/',
    };
  });

  it('should render desktop search button component', () => {
    mockProps.mobile = false;
    const { getByTestId } = render(<SearchButton {...mockProps} />);
    expect(getByTestId('IB-Search-Button')).toBeInTheDocument();
  });

  it('should render desktop search button component with no formLabels', () => {
    mockProps.formLabels = undefined;
    const { getByTestId } = render(<SearchButton {...mockProps} />);
    expect(getByTestId('IB-Search-Button')).toBeInTheDocument();
  });

  it('should render mobile search button component', () => {
    mockProps.mobile = true;
    const { getByTestId } = render(<SearchButton {...mockProps} />);
    expect(getByTestId('IB-Search-Button')).toBeInTheDocument();
  });
  it('should render desktop search button component and click on it', async () => {
    mockProps.mobile = undefined;
    const { getByTestId } = render(<SearchButton {...mockProps} />);
    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
  it('should render mobile search button component and click on it', async () => {
    mockProps.mobile = true;
    const { getByTestId } = render(<SearchButton {...mockProps} />);
    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
  it('should render desktop search button component and click on it without selectedDate', async () => {
    mockProps.mobile = false;
    mockProps.selectedDate = undefined;

    const { getByTestId } = render(<SearchButton {...mockProps} />);
    const searchButton = getByTestId('IB-Search-Button');
    expect(searchButton).toBeInTheDocument();

    await act(async () => {
      fireEvent.click(searchButton);
    });
  });
});
