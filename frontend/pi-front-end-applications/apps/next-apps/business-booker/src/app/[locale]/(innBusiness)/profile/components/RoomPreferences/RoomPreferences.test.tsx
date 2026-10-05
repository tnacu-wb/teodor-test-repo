import '@testing-library/jest-dom';
import { render, screen, fireEvent } from '@testing-library/react';

import { RoomPreferences } from './RoomPreferences';

const mockOnReviewChangesToggle = jest.fn();
const mockOnIsEditableToggle = jest.fn();
const mockOnIsUpdatingToggle = jest.fn();

const baseProps = {
  baseDataTestId: 'test',
  icons: { icon1: 'icon-url' },
  profileDetails: { id: '1', name: 'Test User' } as any,
  onReviewChangesToggle: mockOnReviewChangesToggle,
  onIsEditableToggle: mockOnIsEditableToggle,
  onIsUpdatingToggle: mockOnIsUpdatingToggle,
  onIsDirtyToggle: jest.fn(),
  isEditable: false,
  isUpdating: false,
  isDirty: false,
};

jest.mock('@whitbread-eos/utils/server', () => ({
  useTranslation: () => ({
    t: (key: string) => key,
  }),
  cn: (...classes: string[]) => classes.join(' '),
}));

jest.mock('./RoomPreferencesForm', () => ({
  RoomPreferencesForm: (props: any) => <div data-testid="mock-room-preferences-form" {...props} />,
}));

describe('RoomPreferences', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders container, title and description', () => {
    render(<RoomPreferences {...baseProps} />);
    expect(screen.getByTestId('test-Room-Preferences-Container')).toBeInTheDocument();
    expect(screen.getByTestId('test-Room-Preferences-Title')).toHaveTextContent(
      'roomrequirements.title'
    );
    expect(screen.getByTestId('test-Room-Preferences-Description')).toHaveTextContent(
      'roomrequirements.description'
    );
  });

  it('renders edit button when not editable', () => {
    render(<RoomPreferences {...baseProps} />);
    expect(screen.getByTestId('test-Room-Preferences-Edit-Button')).toBeInTheDocument();
    expect(screen.queryByTestId('mock-room-preferences-form')).not.toBeInTheDocument();
  });

  it('calls onIsEditableToggle with correct args when edit button is clicked', () => {
    render(<RoomPreferences {...baseProps} />);
    fireEvent.click(screen.getByTestId('test-Room-Preferences-Edit-Button'));
    expect(mockOnIsEditableToggle).toHaveBeenCalledWith('roomPreferences', true);
  });

  it('renders RoomPreferencesForm when editable', () => {
    render(<RoomPreferences {...baseProps} isEditable={true} />);
    expect(screen.getByTestId('mock-room-preferences-form')).toBeInTheDocument();
    expect(screen.queryByTestId('test-Room-Preferences-Edit-Button')).not.toBeInTheDocument();
  });

  it('passes correct props to RoomPreferencesForm', () => {
    render(<RoomPreferences {...baseProps} isEditable={true} isUpdating={true} />);
    const form = screen.getByTestId('mock-room-preferences-form');
    expect(form).toHaveAttribute('baseDataTestId', 'test');
  });

  it('renders with different baseDataTestId', () => {
    render(<RoomPreferences {...baseProps} baseDataTestId="another" />);
    expect(screen.getByTestId('another-Room-Preferences-Container')).toBeInTheDocument();
    expect(screen.getByTestId('another-Room-Preferences-Title')).toBeInTheDocument();
    expect(screen.getByTestId('another-Room-Preferences-Description')).toBeInTheDocument();
  });
});
