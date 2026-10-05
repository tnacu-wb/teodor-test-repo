import '@testing-library/jest-dom';
import { fireEvent, render, waitFor } from '@testing-library/react';
import { act } from 'react-dom/test-utils';

import { userEvent } from '~utils/test-utils';

import { RoomRequirementsForm } from './RoomRequirementsForm';

const mockProps = {
  onSubmit: jest.fn(),
  formRef: { current: document.createElement('form') },
  icons: { icon: 'test' },
  roomRequirements: {
    type: 'FAM',
    lettingType: null,
    adults: 2,
    children: 1,
    cotRequired: true,
    hotelBrand: null,
  } as any,
};

jest.mock('@whitbread-eos/utils/server', () => {
  return {
    cn: jest.fn(),
    useTranslation: () => {
      return {
        t: (str: string) => str,
      };
    },
    formatIBAssetsUrl: () => {
      return '/';
    },
  };
});

describe('RoomRequirementsForm Component', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should render RoomRequirementsForm component', async () => {
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);

    const adultsInput = getByTestId('Adults-IB-Form-Select');
    const childrenInput = getByTestId('Children-IB-Form-Select');
    const cotInput = getByTestId('CotRequired-IB-Form-Select');
    const typeInput = getByTestId('Type-IB-Form-Select');

    await act(async () => {
      adultsInput.focus();
      await userEvent.tab();
    });

    await act(async () => {
      childrenInput.focus();
      await userEvent.tab();
    });

    await act(async () => {
      cotInput.focus();
      await userEvent.tab();
    });

    await act(async () => {
      typeInput.focus();
      await userEvent.tab();
    });

    await waitFor(() => {
      expect(adultsInput).toBeInTheDocument();
      expect(childrenInput).toBeInTheDocument();
      expect(cotInput).toBeInTheDocument();
      expect(typeInput).toBeInTheDocument();
    });
  });

  it('should render RoomRequirementsForm component with no Room Requirements', async () => {
    mockProps.roomRequirements = {};
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);

    await waitFor(() => {
      expect(getByTestId('Adults-IB-Form-Select')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(getByTestId('Children-IB-Form-Select')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(getByTestId('CotRequired-IB-Form-Select')).toBeInTheDocument();
    });

    await waitFor(() => {
      expect(getByTestId('Type-IB-Form-Select')).toBeInTheDocument();
    });
  });
});

describe('handleTypeOnChange functionality', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should change option from 2 adults TWIN into 1 adult DOUBLE', async () => {
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-2-Option'));
    });
    await waitFor(async () => {
      userEvent.click(roomTypeButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Type-TWIN-Option'));
    });
    await waitFor(async () => {
      userEvent.click(adultsButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-1-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.double');
    });
  });

  it('should change option from 1 adult SINGLE into 2 adults DOUBLE', async () => {
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-1-Option'));
    });
    await waitFor(async () => {
      userEvent.click(roomTypeButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Type-SB-Option'));
    });
    await waitFor(async () => {
      userEvent.click(adultsButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-2-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.double');
    });
  });

  it('should change option with 1 Children 2 Adults FAMILY to 0 children DOUBLE and back', async () => {
    mockProps.roomRequirements = {
      type: 'FAM',
      lettingType: null,
      adults: 2,
      children: 1,
      cotRequired: true,
      hotelBrand: null,
    } as any;
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const childrenButton = getByTestId('Children-IB-Form-Select-Button');
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(childrenButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Children-0-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.double');
    });

    await waitFor(async () => {
      userEvent.click(childrenButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Children-1-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.family');
    });
    await waitFor(async () => {
      userEvent.click(childrenButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Children-2-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.family');
    });
  });

  it('should change option with 1 Children 1 Adult FAMILY to 0 children DOUBLE and back', async () => {
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');
    const childrenButton = getByTestId('Children-IB-Form-Select-Button');
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-1-Option'));
    });
    await waitFor(async () => {
      userEvent.click(childrenButton);
    });
    await waitFor(async () => {
      userEvent.click(getByTestId('Children-0-Option'));
    });
    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.double');
    });
  });

  it('should check options for 2 Adults, 0 Children and No Room Type', async () => {
    mockProps.roomRequirements = {
      lettingType: null,
      adults: 2,
      children: 0,
      cotRequired: true,
      hotelBrand: null,
    } as any;

    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');
    const cotRequiredButton = getByTestId('CotRequired-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(cotRequiredButton);
    });

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-2-Option'));
    });

    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.double');
    });
  });

  it('should check options for Twin room', async () => {
    mockProps.roomRequirements = {
      type: 'DB',
      lettingType: null,
      adults: 2,
      children: 0,
      cotRequired: true,
      hotelBrand: null,
    } as any;

    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const roomTypeButton = getByTestId('Type-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(roomTypeButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('Type-TWIN-Option'));
    });

    await waitFor(async () => {
      expect(roomTypeButton).toHaveTextContent('roomrequirements.type.twin');
    });
  });
});

describe('RoomRequirementsForm submit functionality', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });
  it('should submit the form with correct data', async () => {
    const { onSubmit } = mockProps;
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const form = getByTestId('Room-Requirements-Form');

    await waitFor(() => {
      expect(getByTestId('Adults-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('Children-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('CotRequired-IB-Form-Select')).toBeInTheDocument();
      expect(getByTestId('Type-IB-Form-Select')).toBeInTheDocument();
    });

    await act(async () => {
      fireEvent.submit(form);
    });

    await waitFor(() => {
      expect(onSubmit).toHaveBeenCalled();
    });
  });
});
describe('RoomRequirementsForm onDirtyChange functionality', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should call onDirtyChange when form becomes dirty', async () => {
    const onDirtyChange = jest.fn();
    const props = { ...mockProps, onDirtyChange };
    const { getByTestId } = render(<RoomRequirementsForm {...props} />);

    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-1-Option'));
    });

    await waitFor(() => {
      expect(onDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should call onDirtyChange when children value changes', async () => {
    const onDirtyChange = jest.fn();
    mockProps.roomRequirements = {
      type: 'DB',
      lettingType: null,
      adults: 2,
      children: 0,
      cotRequired: false,
      hotelBrand: null,
    } as any;
    const props = { ...mockProps, onDirtyChange };
    const { getByTestId } = render(<RoomRequirementsForm {...props} />);

    const childrenButton = getByTestId('Children-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(childrenButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('Children-1-Option'));
    });

    await waitFor(() => {
      expect(onDirtyChange).toHaveBeenCalledWith(true);
    });
  });

  it('should not call onDirtyChange when prop is not provided', async () => {
    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);

    const adultsButton = getByTestId('Adults-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(adultsButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('Adults-1-Option'));
    });

    await waitFor(() => {
      expect(getByTestId('Adults-IB-Form-Select')).toBeInTheDocument();
    });
  });
});

describe('RoomRequirementsForm cot required functionality', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('should toggle cot required from true to false', async () => {
    mockProps.roomRequirements = {
      type: 'FAM',
      lettingType: null,
      adults: 2,
      children: 1,
      cotRequired: true,
      hotelBrand: null,
    } as any;

    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const cotButton = getByTestId('CotRequired-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(cotButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('CotRequired-false-Option'));
    });

    await waitFor(() => {
      expect(cotButton).toHaveTextContent('roomrequirements.cotrequired.false');
    });
  });

  it('should toggle cot required from false to true', async () => {
    mockProps.roomRequirements = {
      type: 'FAM',
      lettingType: null,
      adults: 2,
      children: 1,
      cotRequired: false,
      hotelBrand: null,
    } as any;

    const { getByTestId } = render(<RoomRequirementsForm {...mockProps} />);
    const cotButton = getByTestId('CotRequired-IB-Form-Select-Button');

    await waitFor(async () => {
      userEvent.click(cotButton);
    });

    await waitFor(async () => {
      userEvent.click(getByTestId('CotRequired-true-Option'));
    });

    await waitFor(() => {
      expect(cotButton).toHaveTextContent('roomrequirements.cotrequired.true');
    });
  });
});
