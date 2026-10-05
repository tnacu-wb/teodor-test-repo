import { QueryClient } from '@tanstack/react-query';
import preloadAll from 'jest-next-dynamic';

import { fireEvent, render, userEvent, waitFor, act } from '../../utils/test-utils';
import DynamicSearchEmployee from './DynamicSearchEmployees';

const mockSetDisplayDynamic = jest.fn();
const mockSetGuestUser = jest.fn();
const props = {
  baseDataTestId: '',
  queryClient: new QueryClient(),
  listExclusion: [],
  setGuestUser: mockSetGuestUser,
  index: 0,
  setDisplayDynamic: mockSetDisplayDynamic,
  control: {
    _formValues: {
      bbGuestDetails: [
        {
          composedName: '',
          emailAddress: '',
          firstName: '',
          id: '',
          lastName: '',
          title: '',
        },
      ],
    },
  },
  errors: undefined,
};

const listSuggestions = [
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '2',
      firstName: 'Ella',
      emailAddress: 'booker.cgl@mailinator.com',
      composedName: 'Ms Ella  test (booker.cgl@mailinator.com)',
    },
    prettyFormatDisplay: '<p>Ms Ella  <strong>test</strong> (booker.cgl@mailinator.com)</p>',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '3',
      firstName: 'Ella',
      emailAddress: 'booker.cgl1@mailinator.com',
      composedName: 'Ms Ella  test (booker.cgl@mailinator.com)',
    },
    prettyFormatDisplay: '<p>Ms Ella  <strong>test</strong> (booker.cgl1@mailinator.com)</p>',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '6',
      firstName: 'Ella',
      emailAddress: 'booker.cgl6@mailinator.com',
      composedName: 'Ms Ella  test (booker.cgl6@mailinator.com)',
    },
    prettyFormatDisplay: '<p>Ms Ella  <strong>test</strong> (booker.cgl6@mailinator.com)</p>',
  },
];

let mockGetListOfEmployees = [];
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  generateSuggestionList: () => listSuggestions,
  getListOfEmployees: () => Promise.resolve(mockGetListOfEmployees),
}));

afterEach(() => {
  jest.resetAllMocks();
});

describe('DynamicSearchEmployee', () => {
  beforeAll(async () => {
    await preloadAll();
  });
  it('should render with default props', () => {
    const { getByPlaceholderText } = render(<DynamicSearchEmployee {...props} />);
    expect(getByPlaceholderText('booking.searchCriteria')).toBeTruthy();
  });

  it('should type in input', async () => {
    const { getByPlaceholderText } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.type(input, 'My Text');
    });
    expect(input).toHaveValue('My Text');
  });

  it('should render a list of suggestions', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
    });

    await waitFor(() => {
      expect(getByTestId('DropDownItem-0')).toBeTruthy();
      expect(getByTestId('DropDownItem-1')).toBeTruthy();
      expect(getByTestId('DropDownItem-2')).toBeTruthy();
    });
  });
  it('should render a list of suggestions and press key down to have active item the second', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
      fireEvent.keyDown(input, {
        key: 'Arrow Up',
        code: 'Arrow Up',
        charCode: 38,
      });
    });

    await waitFor(() => {
      expect(getByTestId('DropDownItem-1')).toHaveStyle(
        'background: var(--chakra-colors-lightGrey5)'
      );
    });
  });
  it('should render a list of suggestions and press key up to have active item the last one', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
      fireEvent.keyDown(input, {
        key: 'Arrow Down',
        code: 'Arrow Down',
        charCode: 40,
      });
    });

    await waitFor(() => {
      expect(getByTestId('DropDownItem-2')).toHaveStyle(
        'background: var(--chakra-colors-lightGrey5)'
      );
    });
  });

  it('should render a list of suggestions and press key down when we are on the last position', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');

      fireEvent.keyDown(input, {
        key: 'Arrow Up',
        code: 'Arrow Up',
        charCode: 38,
      });

      fireEvent.keyDown(input, {
        key: 'Arrow Down',
        code: 'Arrow Down',
        charCode: 40,
      });
    });

    await waitFor(() => {
      expect(getByTestId('DropDownItem-0')).toHaveStyle(
        'background: var(--chakra-colors-lightGrey5)'
      );
    });
  });

  it('should render a list of suggestions and press key up when we are on the second position', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');

      fireEvent.keyDown(input, {
        key: 'Arrow Down',
        code: 'Arrow Down',
        charCode: 40,
      });

      fireEvent.keyDown(input, {
        key: 'Arrow Up',
        code: 'Arrow Up',
        charCode: 38,
      });
    });

    await waitFor(() => {
      expect(getByTestId('DropDownItem-0')).toHaveStyle(
        'background: var(--chakra-colors-lightGrey5)'
      );
    });
  });

  it('should render a list of suggestions and press enter', async () => {
    const { getByPlaceholderText } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
    });

    const { title, firstName, lastName } = listSuggestions[0].employee;
    const expectValue = `${title} ${firstName} ${lastName}`;

    await waitFor(() => {
      fireEvent.keyDown(input, {
        key: 'Enter',
        code: 'Enter',
        charCode: 13,
      });
      expect(input).toHaveValue(expectValue);
      expect(mockSetDisplayDynamic).toBeCalled();
    });
  });

  it('should render a list of suggestions and press enter, after we click agan in input', async () => {
    const { getByPlaceholderText, getByText } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
    });

    await waitFor(async () => {
      fireEvent.keyDown(input, {
        key: 'Enter',
        code: 'Enter',
        charCode: 13,
      });

      await act(async () => {
        userEvent.click(input);
      });

      expect(input).toHaveValue('');
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeTruthy();

      expect(mockSetGuestUser).toBeCalled();
    });
  });

  it('should render a list of suggestions and hover over last element', async () => {
    const { getByPlaceholderText, getByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, 'My Text');
    });

    await waitFor(async () => {
      const dropDownElement = getByTestId('DropDownItem-2');

      await act(async () => {
        userEvent.hover(dropDownElement);
      });

      expect(dropDownElement).toHaveStyle('background: var(--chakra-colors-lightGrey5)');
      expect(getByTestId('DropDownItem-1')).toHaveStyle(
        'background: var(--chakra-colors-baseWhite)'
      );
    });
  });

  it('should call onBlur input', async () => {
    const { getByPlaceholderText, getByText } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeTruthy();
    });
  });

  it('should call onBlur after type 2 characters', async () => {
    const { getByPlaceholderText, getByText } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, '12');
      fireEvent.blur(input);
    });

    await waitFor(() => {
      expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeTruthy();
    });
  });

  it('should call display error message', async () => {
    props.errors = {
      bbGuestDetails: ['err'],
    };
    const { getByText } = render(<DynamicSearchEmployee {...props} />);

    expect(getByText('config.errorMessages.yourDetails.empSearch.invalid')).toBeTruthy();
  });

  it("should type 2 characters and we don't should have suggestions", async () => {
    const { getByPlaceholderText, queryByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, '12');
    });

    await waitFor(() => {
      expect(queryByTestId('DropDownItem-0')).toBeFalsy();
    });
  });

  it('should not render a list of suggestions', async () => {
    mockGetListOfEmployees = [];
    const { getByPlaceholderText, queryByTestId } = render(<DynamicSearchEmployee {...props} />);
    const input = getByPlaceholderText('booking.searchCriteria');

    await act(async () => {
      userEvent.click(input);
      userEvent.type(input, '124');
    });

    await waitFor(() => {
      expect(queryByTestId('DropDownItem-0')).toBeFalsy();
    });
  });
});
