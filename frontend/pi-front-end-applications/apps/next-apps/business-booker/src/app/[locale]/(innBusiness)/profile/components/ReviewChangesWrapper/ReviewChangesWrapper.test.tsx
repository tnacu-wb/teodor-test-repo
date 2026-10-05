import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';

import { ReviewChangesWrapper, CHANGES_TRACKER } from './ReviewChangesWrapper';

const mockIcons = { icon1: 'icon1.svg' };
const mockProfileDetails = { id: '1', name: 'Test User' } as any;
const mockLocale = 'en-GB' as any;
const mockToken = 'token';
const mockEmployeeId = 'emp1';
const mockLanguage = 'en' as any;
const mockCompanyRegistrationQuestions = [{ id: 'q1', answer: 'a1' }] as any;
const mockEmployeeDetails = { emp1: { name: 'Test Employee' } };
const mockCompanyId = 'company1';

let yourProfileProps: any = {};
jest.mock('../YourProfile/YourProfile', () => ({
  YourProfile: (props: any) => {
    yourProfileProps = props;
    return <div data-testid="YourProfile" />;
  },
}));
jest.mock('../ChangePassword/ChangePassword', () => ({
  ChangePassword: (props: any) => <div data-testid="ChangePassword" {...props} />,
}));
jest.mock('../CompanyRegistrationQuestions/CompanyRegistrationQuestions', () => ({
  CompanyRegistrationQuestions: (props: any) => (
    <div data-testid="CompanyRegistrationQuestions" {...props} />
  ),
}));
jest.mock('../MealsAndExtras/MealsAndExtras', () => ({
  MealsAndExtras: (props: any) => (
    <div data-testid="MealsAndExtras" data-basedatatestid={props.baseDataTestId ?? ''} {...props} />
  ),
}));
jest.mock('../PaymentType', () => ({
  PaymentSection: (props: any) => <div data-testid="PaymentSection" {...props} />,
}));
jest.mock('../RoomPreferences/RoomPreferences', () => ({
  RoomPreferences: (props: any) => <div data-testid="RoomPreferences" {...props} />,
}));
jest.mock('~components/innBusiness/ReviewChanges', () => ({
  ReviewChanges: (props: any) => <div data-testid="ReviewChanges" {...props} />,
}));

describe('ReviewChangesWrapper', () => {
  const defaultProps = {
    baseDataTestId: 'test',
    icons: mockIcons,
    profileDetails: mockProfileDetails,
    locale: mockLocale,
    hasPaymentCard: true,
    token: mockToken,
    employeeId: mockEmployeeId,
    language: mockLanguage,
    companyRegistrationQuestions: mockCompanyRegistrationQuestions,
    employeeDetails: mockEmployeeDetails,
    companyId: mockCompanyId,
    isBusinessPayRole: false,
  };

  it('renders all main sections', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.getByTestId('YourProfile')).toBeInTheDocument();
    expect(screen.getByTestId('ChangePassword')).toBeInTheDocument();
    expect(screen.getByTestId('CompanyRegistrationQuestions')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentSection')).toBeInTheDocument();
    expect(screen.getByTestId('RoomPreferences')).toBeInTheDocument();
    expect(screen.getByTestId('MealsAndExtras')).toBeInTheDocument();
  });

  it('renders CompanyRegistrationQuestions container with correct test id', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.getByTestId('test-Company-Registration-Questions-Container')).toBeInTheDocument();
  });

  it('does not render CompanyRegistrationQuestions if companyRegistrationQuestions is falsy', () => {
    render(
      <ReviewChangesWrapper {...defaultProps} companyRegistrationQuestions={undefined as any} />
    );
    expect(screen.queryByTestId('CompanyRegistrationQuestions')).not.toBeInTheDocument();
  });

  it('does not render ReviewChanges by default', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('shows ReviewChanges when a section is editable and not updating', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('does not show ReviewChanges when updating is true', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    const { onIsEditableToggle, onIsUpdatingToggle } = yourProfileProps;
    onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
    onIsUpdatingToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('MealsAndExtras receives empty string for baseDataTestId if not provided', () => {
    render(<ReviewChangesWrapper {...{ ...defaultProps, baseDataTestId: undefined }} />);
    expect(screen.getByTestId('MealsAndExtras').getAttribute('baseDataTestId') || '').toBe('');
  });

  it('passes correct props to YourProfile', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(yourProfileProps.icons).toBe(mockIcons);
    expect(yourProfileProps.profileDetails).toBe(mockProfileDetails);
    expect(yourProfileProps.locale).toBe(mockLocale);
    expect(typeof yourProfileProps.onReviewChangesToggle).toBe('function');
    expect(typeof yourProfileProps.onIsEditableToggle).toBe('function');
    expect(typeof yourProfileProps.onIsUpdatingToggle).toBe('function');
  });

  it('renders MealsAndExtras with baseDataTestId as empty string if undefined', () => {
    render(<ReviewChangesWrapper {...{ ...defaultProps, baseDataTestId: undefined }} />);
    expect(screen.getByTestId('MealsAndExtras').getAttribute('data-basedatatestid')).toBe('');
  });

  it('should not show ReviewChanges if isEditable is true but isUpdating is true for another section', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    const { onIsEditableToggle, onIsUpdatingToggle } = yourProfileProps;
    onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    onIsUpdatingToggle(CHANGES_TRACKER.ROOM_PREFERENCES, true);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });
  it('should call handleReviewChangesToggle and update state accordingly', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    const { onReviewChangesToggle } = yourProfileProps;
    onReviewChangesToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    // Now, reviewChanges should be true for YOUR_PROFILE
    // But ReviewChanges is only shown if isEditable is true and isUpdating is false
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should not show ReviewChanges if isEditable is false for all', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('should render CompanyRegistrationQuestions container if companyRegistrationQuestions is non-empty array', () => {
    render(
      <ReviewChangesWrapper
        {...defaultProps}
        companyRegistrationQuestions={[
          {
            id: 'q1',
            answer: 'a1',
            mandatory: false,
            type: 'text',
            options: [],
            label: 'Question 1',
          },
        ]}
      />
    );
    expect(screen.getByTestId('test-Company-Registration-Questions-Container')).toBeInTheDocument();
  });

  it('should pass empty string as baseDataTestId to MealsAndExtras if baseDataTestId is undefined', () => {
    render(<ReviewChangesWrapper {...{ ...defaultProps, baseDataTestId: undefined }} />);
    expect(screen.getByTestId('MealsAndExtras').getAttribute('data-basedatatestid')).toBe('');
  });

  it('should pass baseDataTestId to MealsAndExtras if provided', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    expect(screen.getByTestId('MealsAndExtras').getAttribute('data-basedatatestid')).toBe('test');
  });

  it('should not render ReviewChanges if isEditable is true but isUpdating is true for any section', () => {
    render(<ReviewChangesWrapper {...defaultProps} />);
    const { onIsEditableToggle, onIsUpdatingToggle } = yourProfileProps;
    onIsEditableToggle(CHANGES_TRACKER.YOUR_PROFILE, true);
    onIsUpdatingToggle(CHANGES_TRACKER.PAYMENT_TYPE, true);
    expect(screen.queryByTestId('ReviewChanges')).not.toBeInTheDocument();
  });

  it('renders PaymentSection, RoomPreferences, MealsAndExtras, and CompanyRegistrationQuestions when isBusinessPayRole is false', () => {
    render(<ReviewChangesWrapper {...defaultProps} isBusinessPayRole={false} />);
    expect(screen.getByTestId('test-Company-Registration-Questions-Container')).toBeInTheDocument();
    expect(screen.getByTestId('PaymentSection')).toBeInTheDocument();
    expect(screen.getByTestId('RoomPreferences')).toBeInTheDocument();
    expect(screen.getByTestId('MealsAndExtras')).toBeInTheDocument();
  });

  it('does not render PaymentSection, RoomPreferences, MealsAndExtras, or CompanyRegistrationQuestions when isBusinessPayRole is true', () => {
    render(<ReviewChangesWrapper {...defaultProps} isBusinessPayRole={true} />);
    expect(
      screen.queryByTestId('test-Company-Registration-Questions-Container')
    ).not.toBeInTheDocument();
    expect(screen.queryByTestId('PaymentSection')).not.toBeInTheDocument();
    expect(screen.queryByTestId('RoomPreferences')).not.toBeInTheDocument();
    expect(screen.queryByTestId('MealsAndExtras')).not.toBeInTheDocument();
  });
});
