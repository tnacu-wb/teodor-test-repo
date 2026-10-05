import { FieldsType, FORM_FIELD_TYPES, FormDynamicFieldCompProps } from '@whitbread-eos/atoms';

import { render, userEvent, waitFor } from '../../utils/test-utils';
import ResetSearchCriteriaButton from './ResetSearchCriteriaButton.component';

const mockHandleResetField = jest.fn();
const mockHandleSetValue = jest.fn();
const mockGetValues = jest.fn();
const mockOnReset = jest.fn();
const mockSetClearHotelLocation = jest.fn();
const mockSetClearHotelName = jest.fn();

const formField: FieldsType = {
  type: FORM_FIELD_TYPES.DYNAMIC_FIELD,
  name: 'resetSearchCriteriaBtn',
  testid: 'ResetSearchCriteria',
  label: 'Clear Search',
  props: {
    action: mockOnReset,
    clearHotelFields: {
      setClearHotelLocation: mockSetClearHotelLocation,
      setClearHotelName: mockSetClearHotelName,
    },
  },
};

jest.mock('@tanstack/react-query', () => ({
  ...jest.requireActual('@tanstack/react-query'),
  useQueryClient: () => ({
    cancelQueries: jest.fn(),
    invalidateQueries: jest.fn(),
  }),
}));

const mockProps: FormDynamicFieldCompProps = {
  formField,
  field: { name: 'resetSearchCriteriaBtn', value: '', onChange: jest.fn(), onBlur: jest.fn() },
  handleSetValue: mockHandleSetValue,
  handleResetField: mockHandleResetField,
  getValues: mockGetValues,
};

Object.defineProperty(window, 'performance', {
  value: {
    getEntriesByType: jest.fn().mockReturnValue([{ type: 'reload' }]),
    measure: jest.fn(),
  },
});

describe('<ResetSearchCriteriaButton/>', () => {
  it('should render a <ResetSearchCriteriaButton/> ', () => {
    const { getByTestId } = render(<ResetSearchCriteriaButton {...mockProps} />);
    const button = getByTestId('ResetSearchCriteria-Button');
    expect(button).toBeInTheDocument();
  });

  it('should trigger reset when clicked ', async () => {
    const { getByTestId } = render(<ResetSearchCriteriaButton {...mockProps} />);
    const noOfResetFields = 12;
    const button = getByTestId('ResetSearchCriteria-Button');

    userEvent.click(button);
    await waitFor(async () => {
      expect(mockHandleResetField).toHaveBeenCalledTimes(noOfResetFields);
      expect(mockSetClearHotelLocation).toHaveBeenCalledWith(true);
      expect(mockSetClearHotelName).toHaveBeenCalledWith(true);
      expect(mockOnReset).toHaveBeenCalledTimes(1);
    });
  });

  it('should not trigger reset, if there is no handleSetValue ', async () => {
    const props = {
      ...mockProps,
      handleSetValue: undefined,
      handleResetField: undefined,
    };

    const { getByTestId } = render(<ResetSearchCriteriaButton {...props} />);
    const button = getByTestId('ResetSearchCriteria-Button');

    jest.clearAllMocks();
    userEvent.click(button);
    await expect(mockOnReset).not.toHaveBeenCalled();
  });
});
