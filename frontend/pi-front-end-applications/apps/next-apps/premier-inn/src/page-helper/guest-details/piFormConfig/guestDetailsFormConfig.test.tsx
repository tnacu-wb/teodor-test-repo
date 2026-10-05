import '@testing-library/jest-dom';
import { screen, waitFor } from '@testing-library/react';

import { render, userEvent } from '~utils/test-utils';

import { guestDetailsFormConfig } from './guestDetailsFormConfig';

const mockT = (key: string) => key;
const mockGetFormState = jest.fn();
const mockOnSubmit = jest.fn();
const mockGoBack = jest.fn();
const mockUpdateReasonForStay = jest.fn();
const mockSetIsLocationRequired = jest.fn();
const mockSetIsBookingForSomeoneElse = jest.fn();
const mockSetShowCheckInInfo = jest.fn();
const mockGetTypographyProps = jest.fn((_, semanticTypography) => semanticTypography);

const defaultProps = {
  getTypographyProps: mockGetTypographyProps,
  getFormState: mockGetFormState,
  defaultValues: {},
  onSubmit: mockOnSubmit,
  baseDataTestId: 'GuestDetailsTest',
  t: mockT,
  currentLang: 'en',
  basketReferenceId: 'TEST123',
  resetForm: 0,
  brand: 'PI',
  bkndData: {
    rooms: [{ adults: 1, children: 0 }],
    hiData: {
      hotelInformation: {
        brand: 'PI',
      },
    },
  },
  goBack: mockGoBack,
  cityTaxMessages: {
    mainBanner: '',
    secondaryBanner: '',
    summaryText: '',
  },
  updateReasonForStay: mockUpdateReasonForStay,
  isLocationRequired: false,
  setIsLocationRequired: mockSetIsLocationRequired,
  hotelBrand: 'PI',
  isRegisterSelected: false,
  isSingleRoomRedesignEnabled: false,
  isMultiRoomRedesignEnabled: false,
  isBillingAddressEnabled: false,
  isBookingForSomeoneElse: false,
  setIsBookingForSomeoneElse: mockSetIsBookingForSomeoneElse,
  isGermanHotel: false,
  isAdditionalInformationEnabled: false,
  showCheckInInfo: {},
  setShowCheckInInfo: mockSetShowCheckInInfo,
  isCompanyNameAdvanceEnabled: false,
  shouldAskForAccompanyingGuest: false,
  isDifferentBillingAddress: false,
  suppressMarketingCheckbox: false,
  isDEOptInEnabled: false,
  submitButtonDisabled: false,
  isConsolidateMobileLandlineEnabled: false,
  isCountryAllowTypingEnabled: false,
  isRemovePIIDataFromLocalStorageEnabled: false,
  isAuth0Enabled: false,
  privacyPolicyLinkPath: '/gb/en/terms/privacy-policy.html',
};

describe('guestDetailsFormConfig - ManualAddressToggle', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  describe('Keyboard Accessibility', () => {
    it('should render manual address link as a button element', async () => {
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      expect(manualAddressToggle).toBeDefined();
      expect(manualAddressToggle!.Component).toBeDefined();

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      await waitFor(() => {
        const button = container.querySelector('button');
        expect(button).toBeInTheDocument();
        expect(button).toHaveAttribute('type', 'button');
      });
    });

    it('should render link with proper text content', async () => {
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      await waitFor(() => {
        const button = container.querySelector('button');
        expect(button).toHaveTextContent('booking.enterManuallAddress');
      });
    });

    it('should be keyboard focusable and activatable with Enter key', async () => {
      const user = userEvent.setup();
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      const button = screen.getByRole('button', {
        name: 'booking.enterManuallAddress',
      });

      await user.tab();
      expect(button).toHaveFocus();

      await user.keyboard('{Enter}');

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should be activatable with Space key', async () => {
      const user = userEvent.setup();
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      const button = screen.getByRole('button', {
        name: 'booking.enterManuallAddress',
      });

      button.focus();
      await user.keyboard(' ');

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should call handleSetValue when clicked', async () => {
      const user = userEvent.setup();
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      const button = screen.getByRole('button', {
        name: 'booking.enterManuallAddress',
      });

      await user.click(button);

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledTimes(1);
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'manualAddress');
      });
    });

    it('should not render when field value equals fieldName', () => {
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: 'manualAddress', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };
      const mockHandleSetValue = jest.fn();

      const { container } = render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      expect(container.firstChild).toBeNull();
    });

    it('should handle click gracefully when handleSetValue is undefined', async () => {
      const user = userEvent.setup();
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'manualAddress' } };

      render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={undefined}
          errors={undefined}
        />
      );

      const button = screen.getByRole('button', {
        name: 'booking.enterManuallAddress',
      });

      await user.click(button);

      await waitFor(() => {
        expect(button).toBeInTheDocument();
      });
    });

    it('should use fieldName from formField prop', async () => {
      const user = userEvent.setup();
      const config = guestDetailsFormConfig(defaultProps);
      const manualAddressToggle = config.elements.fields.find(
        (field: any) => field.name === 'manualAddressToggle'
      );

      const MockComponent = manualAddressToggle!.Component as any;
      const mockField = { value: '', name: 'manualAddressToggle' };
      const mockFormField = { props: { fieldName: 'customFieldName' } };
      const mockHandleSetValue = jest.fn();

      render(
        <MockComponent
          field={mockField}
          formField={mockFormField}
          handleSetValue={mockHandleSetValue}
          errors={undefined}
        />
      );

      const button = screen.getByRole('button', {
        name: 'booking.enterManuallAddress',
      });

      await user.click(button);

      await waitFor(() => {
        expect(mockHandleSetValue).toHaveBeenCalledWith('manualAddressToggle', 'customFieldName');
      });
    });
  });

  describe('UpdateProfileConsent', () => {
    it('should hide update profile consent fields when auth0 flag is disabled', () => {
      const config = guestDetailsFormConfig({
        ...defaultProps,
        isAuth0Enabled: false,
      });

      const updateConsentCheckbox = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsent'
      );
      const updateConsentNotice = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsentNotice'
      );

      expect(updateConsentCheckbox?.hidden).toBe(true);
      expect(updateConsentNotice?.hidden).toBe(true);
    });

    it('should keep update profile consent fields hidden when the user is not signed in', () => {
      const config = guestDetailsFormConfig({
        ...defaultProps,
        isAuth0Enabled: true,
        isUserSignedIn: false,
      });

      const checkbox = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsent'
      );
      const notice = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsentNotice'
      );

      expect(checkbox?.hidden).toBe(true);
      expect(notice?.hidden).toBe(true);
    });

    it('should show update profile consent fields and place them before the leadGuest block', () => {
      const config = guestDetailsFormConfig({
        ...defaultProps,
        isAuth0Enabled: true,
        isUserSignedIn: true,
      });

      const updateConsentCheckbox = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsent'
      );
      const updateConsentNotice = config.elements.fields.find(
        (field: any) => field.name === 'updateProfileConsentNotice'
      );
      const updateConsentIndex = config.elements.fields.findIndex(
        (field: any) => field.name === 'updateProfileConsent'
      );
      const leadGuestIndex = config.elements.fields.findIndex(
        (field: any) => field.name === 'leadGuest'
      );

      expect(updateConsentCheckbox).toBeDefined();
      expect(updateConsentCheckbox?.hidden).toBe(false);
      expect(updateConsentCheckbox?.label).toBe('booking.saveDetailsCheckboxLabel');
      expect(updateConsentNotice).toBeDefined();
      expect(updateConsentNotice?.hidden).toBe(false);
      expect(updateConsentNotice?.content).toBeDefined();
      expect(updateConsentIndex).toBeGreaterThan(-1);
      expect(leadGuestIndex).toBeGreaterThan(-1);
      expect(updateConsentIndex).toBeLessThan(leadGuestIndex);
    });
  });
});
