import '@testing-library/jest-dom';
import { useForm } from 'react-hook-form';

import { act, render, userEvent } from '../../../../utils/test-utils';
import FormMenuDropdown from './FormMenuDropdown.component';

const onChangeMock = jest.fn();

// Mock the useQueryRequestRestaurants hook and its response
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useQueryRequestRestaurants: jest.fn(),
}));

// Custom type definition for the mock
const mockUseQueryRequest = jest.requireMock('@whitbread-eos/utils');

const GetMenuDropdownComponentProps = () => {
  const { control, clearErrors } = useForm();
  return {
    control: control,
    field: { onChange: onChangeMock },
    formField: {
      name: 'menuId',
      testid: 'formMenuDropdown',
    },
    time: '7:00',
    clearErrors: clearErrors,
    errors: {},
    setValue: jest.fn(),
    getValues: jest.fn(),
  };
};

const MenuComponent = () => {
  const props = GetMenuDropdownComponentProps();
  return <FormMenuDropdown {...props} />;
};

describe('Session Form Component', () => {
  beforeEach(() => {
    mockUseQueryRequest.useQueryRequestRestaurants.mockReturnValue({
      data: {
        menu: {
          menus: [
            {
              id: '51021d5e-c364-4a71-ae46-f129062534fd',
              name: 'Evening Set Menu',
              available: true,
            },
            { id: 'ce159ca7-68b7-4e7b-bb8c-ec98328569e2', name: 'Festive Menu', available: false },
            { id: '9d899e06-8fee-47b6-9dfb-54f497464279', name: 'Main Menu', available: true },
            { id: 'ec4e9112-0291-475a-a258-da5c9fb17caa', name: 'Testing York', available: false },
          ],
        },
      },
      isLoading: false,
      isError: false,
      error: null,
    });
  });
  it('should render loader', () => {
    mockUseQueryRequest.useQueryRequestRestaurants.mockReturnValue({
      data: {
        menu: {
          menus: [],
        },
      },
      isLoading: true,
      isError: false,
      error: null,
    });
    const { getByTestId } = render(<MenuComponent />);
    expect(getByTestId('loading-spinner')).toBeInTheDocument();
  });
  it('should render the Menu component', () => {
    const { getByTestId } = render(<MenuComponent />);
    expect(getByTestId('formMenuDropdown')).toBeInTheDocument();
  });
  it('should not render the Menu component when data is null', () => {
    mockUseQueryRequest.useQueryRequestRestaurants.mockReturnValue({
      data: { menu: { menus: [] } },
      isLoading: false,
      isError: false,
      error: null,
    });

    const { container } = render(<MenuComponent />);
    expect(container).toBeNull;
  });
  it('should render the error component when data is isError true', () => {
    mockUseQueryRequest.useQueryRequestRestaurants.mockReturnValue({
      data: null,
      isLoading: false,
      isError: true,
      error: {
        response: {
          errors: [
            {
              message: 'menu not available',
            },
          ],
        },
      },
    });

    const { getByText } = render(<MenuComponent />);
    expect(getByText('Menu are not available')).toBeInTheDocument;
  });
  it('should set menuId when a menu is selected', async () => {
    const { getByText, getByTestId, container } = render(<MenuComponent />);

    const dropdownToggleForMenus = getByTestId('formMenuDropdown');
    const selector = container.querySelector('[aria-haspopup="menu"]');

    await userEvent.click(dropdownToggleForMenus);
    const dropdownMenus = getByText('Evening Set Menu');
    act(() => {
      dropdownMenus.click();
    });
    expect(selector).toHaveTextContent('Evening Set Menu');
  });
});
