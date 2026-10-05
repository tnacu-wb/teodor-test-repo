import '@testing-library/jest-dom';
import { render, screen } from '@testing-library/react';
import { useCreatePromoCodeFormContext } from '@whitbread-eos/molecules/dist/unique-promotions/Create';
import { usePromoTranslation } from '@whitbread-eos/utils';
import React from 'react';

import CreatePromotionCodeFormDetails from './CreatePromotionCodeFormDetails';

const mockTranslation = {
  createNewBatchTitle: 'Create New Batch',
  formTitle: 'Fill the form',
};

jest.mock('@whitbread-eos/utils', () => ({
  usePromoTranslation: jest.fn(() => mockTranslation),
}));
jest.mock('@whitbread-eos/atoms', () => ({
  LoadingSpinner: () => <div data-testid="loading-spinner" />,
}));
const mockCreatePromotionCodeForm = jest.fn();

jest.mock('@whitbread-eos/molecules', () => ({
  CreatePromotionCodeForm: (props: any) => {
    mockCreatePromotionCodeForm(props);
    return <div data-testid="create-promo-form">Mock CreatePromotionCodeForm</div>;
  },
}));

jest.mock('@whitbread-eos/molecules/dist/unique-promotions', () => ({
  PromoNotes: () => <div data-testid="promo-notes">Mock Promo Notes</div>,
}));

jest.mock('@whitbread-eos/molecules/dist/unique-promotions/Create', () => ({
  useCreatePromoCodeFormContext: jest.fn(() => ({
    createPromoIsLoading: false,
    createPromoIsError: false,
  })),
}));

const mockControl = {} as any;
const mockErrors = {};
const mockGetValues = jest.fn();
const mockHandleSetError = jest.fn();
const mockClearErrors = jest.fn();
const setValue = jest.fn();

const mockFormField = {
  name: 'campaignName',
  label: 'Campaign Name',
  type: 'text',
};

describe('CreatePromotionCodeFormDetails', () => {
  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('renders without crashing', () => {
    const { container } = render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(container).toBeInTheDocument();
  });

  it('renders translated header text', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(screen.getByText('Create New Batch')).toBeInTheDocument();
  });

  it('renders translated form title text', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(screen.getByText('Fill the form')).toBeInTheDocument();
  });

  it('renders CreatePromotionCodeForm child component', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(screen.getByTestId('create-promo-form')).toBeInTheDocument();
  });

  it('passes all required props to CreatePromotionCodeForm', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(mockCreatePromotionCodeForm).toHaveBeenCalledTimes(1);
    expect(mockCreatePromotionCodeForm).toHaveBeenCalledWith(
      expect.objectContaining({
        control: mockControl,
        formField: mockFormField,
        errors: mockErrors,
        getValues: mockGetValues,
        handleSetError: mockHandleSetError,
      })
    );
  });

  it('renders PromoNotes component', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(screen.getByTestId('promo-notes')).toBeInTheDocument();
  });

  it('re-renders correctly when formField prop changes (branch coverage)', () => {
    const { rerender } = render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    const updatedFormField = {
      name: 'operaPromoCode',
      label: 'Opera Promo Code',
      type: 'text',
    };

    rerender(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={updatedFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(mockCreatePromotionCodeForm).toHaveBeenLastCalledWith(
      expect.objectContaining({
        formField: updatedFormField,
      })
    );
  });

  it('calls usePromoTranslation hook', () => {
    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(usePromoTranslation).toHaveBeenCalled();
  });

  it('renders main layout structure correctly', () => {
    const { container } = render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    const flex = container.querySelector('div');
    expect(flex).toBeInTheDocument();

    expect(container.querySelector('[class]')).toBeTruthy();
  });

  it('shows loading spinner when createPromoIsLoading is true', () => {
    (useCreatePromoCodeFormContext as jest.Mock).mockReturnValueOnce({
      createPromoIsLoading: true,
      createPromoIsError: false,
    });

    render(
      <CreatePromotionCodeFormDetails
        control={mockControl}
        formField={mockFormField}
        errors={mockErrors}
        getValues={mockGetValues}
        handleSetError={mockHandleSetError}
        clearErrors={mockClearErrors}
        setValue={setValue}
      />
    );

    expect(screen.getByTestId('loading-spinner')).toBeInTheDocument();
  });
});
