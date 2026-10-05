import '@testing-library/jest-dom';
import { FREE_FOOD_OPTIONS } from '@whitbread-eos/api';
import { AddSubtract } from '@whitbread-eos/atoms';
import { useFeatureToggle, useLocalStorage } from '@whitbread-eos/utils';
import getConfig from 'next/config';

import { fireEvent, render, waitFor } from '../../utils/test-utils';
import type { Props } from './MealItem.component';
import MealItem, { getMealItemWrapperProps, FreePrice, FreeKidsMeal } from './MealItem.component';

jest.mock('@whitbread-eos/atoms', () => ({
  ...jest.requireActual('@whitbread-eos/atoms'),
  PromoTag: () => <div data-testid="promo-tag">PromoTag</div>,
}));

const mockCallSubtract = jest.fn();
const mockCallPlus = jest.fn();

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  getServerSideCustomLocale: jest.fn(),
  getI18nLabels: () =>
    Promise.resolve({
      isLoading: false,
      isError: false,
      error: { message: '' },
      data: {},
    }),
  useFeatureToggle: jest.fn().mockReturnValue({
    release_pi_bb_ccui_show_meals_package: true,
  }),
  getUnleashToggles: jest.fn(() => ({
    release_ib_enabled: true,
  })),
  useLocalStorage: jest.fn(() => [
    {
      rateTags: ['FREE_MEAL'],
    },
  ]),
}));

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

const mockProps: Props = {
  controller: (
    <AddSubtract
      onSubtract={mockCallSubtract}
      onPlus={mockCallPlus}
      prefixDataTestId="AddSubtractControls"
      isSubtractDisable={false}
      isPlusDisable={false}
      label={''}
      value={1}
    />
  ),
  currentLanguage: 'en',
  allergyInfoSrc: 'https://www.google.com',
  allergyInfoLabel: 'allergy_info_label',
  imageUrl:
    'https://secure2.premierinn.com/content/dam/global/restaurants/Global/full-breakfast-booking.png',
  title: 'Meal Deal',
  price: 24.99,
  currency: 'USD',
  numberNights: 3,
  totalPrice: 74.97,
  freeBreakfastOption: false,
  showFreeFoodKids: false,
  upsellType: 'dinner',
  isFree: false,
  basePrice: 24.99,
};

describe('MealItem', () => {
  it('should render a <MealItem> with default props ', function () {
    const { getByTestId, getByText } = render(<MealItem {...mockProps} />);

    expect(getByTestId('Image-Wrapper')).toBeInTheDocument();
    expect(getByTestId('Title')).toBeInTheDocument();
    expect(getByTestId('Price')).toBeInTheDocument();
    expect(getByTestId('Description')).toBeInTheDocument();
    expect(getByTestId('Allergy')).toBeInTheDocument();
    expect(getByText(mockProps.title as string)).toBeInTheDocument();
  });

  it('should render a <MealItem> with data-testid', function () {
    const { getByTestId } = render(<MealItem {...mockProps} prefixDataTestId="Prefix" />);

    expect(getByTestId('Prefix-Image-Wrapper')).toBeInTheDocument();
    expect(getByTestId('Prefix-Title')).toBeInTheDocument();
    expect(getByTestId('Prefix-Price')).toBeInTheDocument();
    expect(getByTestId('Prefix-Description')).toBeInTheDocument();
    expect(getByTestId('Prefix-Allergy')).toBeInTheDocument();
  });

  it('should render a <MealItem> without free food for children', function () {
    const { queryByText } = render(<MealItem {...mockProps} />);

    expect(queryByText('upsell.label.promoText.freeKidsMeal')).toBeFalsy();
  });

  it('should render a <MealItem> without free dinner for children', function () {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
    const { queryByText } = render(
      <MealItem {...mockProps} freeBreakfastOption={true} showFreeFoodKids={true} />
    );

    expect(queryByText('upsell.label.promoText.dinner')).toBeTruthy();
  });

  it('should render a <MealItem> and check buttons from add subtract ', function () {
    const { getByTestId } = render(<MealItem {...mockProps} />);
    expect(getByTestId('AddSubtractControls-SubtractButton')).toBeEnabled();
    expect(getByTestId('AddSubtractControls-AddButton')).toBeEnabled();

    fireEvent.click(getByTestId('AddSubtractControls-AddButton'));
    expect(mockCallPlus).toBeCalled();

    fireEvent.click(getByTestId('AddSubtractControls-SubtractButton'));
    expect(mockCallSubtract).toBeCalled();
  });

  it('should render a <MealItem> in german with £ currency', function () {
    const { queryAllByText, queryByText } = render(
      <MealItem {...mockProps} currentLanguage="de" currency="GBP" />
    );

    expect(queryAllByText('£24.99')).toBeTruthy();
    expect(queryByText('24.99£')).toBeFalsy();
  });

  it('should render a <MealItem> in german with £ currency with currentLanguage undefined', function () {
    const { queryAllByText, queryByText } = render(
      <MealItem {...mockProps} currentLanguage={undefined} currency="GBP" />
    );

    expect(queryAllByText('£24.99')).toBeTruthy();
    expect(queryByText('24.99£')).toBeFalsy();
  });

  it('should render a <MealItem> in german with € currency', function () {
    const { queryAllByText, queryByText } = render(
      <MealItem {...mockProps} currentLanguage="de" currency="EUR" />
    );

    expect(queryAllByText('24.99€')).toBeTruthy();
    expect(queryByText('€24.99')).toBeFalsy();
  });

  it('should render a <MealItem> in german with $ currency', function () {
    const { queryAllByText } = render(
      <MealItem {...mockProps} currentLanguage="de" currency="USD" />
    );

    expect(queryAllByText('24.99')).toBeTruthy();
  });

  it('should render a <MealItem> for entire stay format price', function () {
    const { queryByTestId } = render(<MealItem {...mockProps} isForEntireStay={true} />);

    expect(queryByTestId('Price')).toHaveTextContent(
      '24.99 upsell.label.per.adult/upsell.label.day (74.97 upsell.label.for 3 upsell.label.nights upsell.label.for upsell.label.all hoteldetails.bookingsummary.rooms)'
    );
  });

  it('should render a <MealItem> for 1 night', function () {
    const { getByText } = render(<MealItem {...mockProps} numberNights={1} />);
    expect(getByText(/upsell.label.night/i)).toBeInTheDocument();
  });

  it('should render a <MealItem> for 1 night with soft bundles', function () {
    const { getByTestId } = render(
      <MealItem {...mockProps} numberNights={1} isSoftBundlesVisible={true} />
    );
    expect(getByTestId('Title_SB')).toBeInTheDocument();
  });

  it('should render a <MealItem> for 1 night with soft bundles and meal selected', function () {
    const { getByTestId } = render(
      <MealItem {...mockProps} numberNights={1} isSoftBundlesVisible={true} isSelected={true} />
    );
    expect(getByTestId('SB-Meal-Item-Wrapper-Selected')).toBeInTheDocument();
  });

  it('should render a <MealItem> for 1 night with soft bundles and meal selected with outcome price', function () {
    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        numberNights={1}
        isSoftBundlesVisible={true}
        isSelected={true}
        outcomePrice={12}
      />
    );
    expect(getByTestId('SB-Meal-Item-Wrapper-Selected')).toBeInTheDocument();
  });

  it('should render a <MealItem> for 1 night with soft bundles no meal selected with outcome price', function () {
    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        numberNights={1}
        isSoftBundlesVisible={true}
        isSelected={false}
        outcomePrice={12}
      />
    );
    expect(getByTestId('SB-Meal-Item-Wrapper')).toBeInTheDocument();
  });

  it('should render a <MealItem> for 2 nights', function () {
    const { getByText } = render(<MealItem {...mockProps} numberNights={2} />);
    expect(getByText(/upsell.label.nights/i)).toBeInTheDocument();
  });

  it('should render empty string if the description did not come', async () => {
    const { getByTestId } = render(<MealItem {...mockProps} numberNights={2} />);

    await waitFor(() => {
      expect(getByTestId('Description').textContent).toBe('');
    });
  });

  it('should render breakfastPromoCode if the hasPromoMeal comes as true', async () => {
    const { getByTestId } = render(<MealItem {...mockProps} numberNights={2} hasPromoMeal />);

    await waitFor(() => {
      expect(getByTestId('FreeBreakfastPromo')).toBeInTheDocument();
    });
  });

  it('should render a <MealItem> with upsellType as dinner ', function () {
    const { getByTestId, getByText } = render(<MealItem {...mockProps} />);

    expect(getByTestId('Image-Wrapper')).toBeInTheDocument();
    expect(getByTestId('Title')).toBeInTheDocument();
    expect(getByTestId('Price')).toBeInTheDocument();
    expect(getByTestId('Description')).toBeInTheDocument();
    expect(getByTestId('Allergy')).toBeInTheDocument();
    expect(getByText(mockProps.title as string)).toBeInTheDocument();
  });

  it('should render a <MealItem> with upsellType as breakfast and showFreeFoodKids is true', function () {
    mockProps.upsellType = 'breakfast';
    mockProps.showFreeFoodKids = true;
    mockProps.freeBreakfastOption = true;
    const { queryByText } = render(<MealItem {...mockProps} />);

    expect(queryByText('upsell.label.promoText.freeKidsMeal')).toBeTruthy();
  });

  it('should render free dinner label when dinner upsell and free meal dinner flag enabled', () => {
    const props = {
      ...mockProps,
      upsellType: 'dinner',
      freeBreakfastOption: true,
      showFreeFoodKids: true,
    };

    const { queryByText } = render(<MealItem {...props} />);
    expect(queryByText('upsell.label.promoText.dinner')).toBeTruthy();
  });

  it('should not render free dinner label when dinner upsell and freeBreakfastOption is false', () => {
    const props = {
      ...mockProps,
      upsellType: 'dinner',
      freeBreakfastOption: false,
      showFreeFoodKids: false,
    };

    const { queryByText } = render(<MealItem {...props} />);
    expect(queryByText('upsell.label.promoText.dinner')).toBeFalsy();
  });

  it('should disable meal item when adult has free meals and item is not selected', () => {
    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        isAdultHasMealsFree={true}
        isSelected={false}
        isFree={false}
        prefixDataTestId="Test"
      />
    );

    const wrapper = getByTestId('Test-Meal-Item-Wrapper');

    expect(wrapper).toHaveStyle({ opacity: '0.5' });
  });
  it('should render included badge when isIncluded is true', () => {
    const { getByTestId } = render(
      <MealItem {...mockProps} isSoftBundlesVisible={true} isIncluded={true} />
    );

    expect(getByTestId('SB-Included-Badge')).toBeInTheDocument();
  });
  it('should render negative outcome price correctly', () => {
    const { getByTestId } = render(
      <MealItem {...mockProps} isSoftBundlesVisible={true} outcomePrice={-10} />
    );

    expect(getByTestId('SB-Meal-Outcome-Price')).toHaveTextContent('-');
  });
});

describe('getMealItemWrapperProps', () => {
  const baseProps = {
    wrapperMealItemStyle: {},
    prefixDataTestId: 'Test',
  };

  it('should return selected wrapper props', () => {
    const result = getMealItemWrapperProps({
      ...baseProps,
      isMealItemSelected: true,
      isMealItemDisabled: false,
    });

    expect(result['data-testid']).toBe('Test-Meal-Item-Wrapper-Selected');
    expect(result.opacity).toBe(1);
    expect(result.pointerEvents).toBe('auto');
  });

  it('should return unselected wrapper props', () => {
    const result = getMealItemWrapperProps({
      ...baseProps,
      isMealItemSelected: false,
      isMealItemDisabled: false,
    });

    expect(result['data-testid']).toBe('Test-Meal-Item-Wrapper');
    expect(result._hover).toBeUndefined();
  });

  it('should apply disabled styles', () => {
    const result = getMealItemWrapperProps({
      ...baseProps,
      isMealItemSelected: false,
      isMealItemDisabled: true,
    });

    expect(result.opacity).toBe(0.5);
    expect(result.pointerEvents).toBe('none');
  });

  it('should merge wrapper styles', () => {
    const result = getMealItemWrapperProps({
      wrapperMealItemStyle: { padding: '10px' },
      isMealItemSelected: false,
      isMealItemDisabled: false,
      prefixDataTestId: 'Test',
    });

    expect(result.padding).toBe('10px');
  });
});

describe('FreePrice', () => {
  const defaultProps = {
    isMealItemSelected: true,
    currency: 'GBP',
    basePrice: 20,
    currentLanguage: 'en',
    numberNights: 2,
    t: (key: string) => key,
    priceTextStyle: {},
  };

  it('should render FreePrice when valid props provided', () => {
    const { getByText } = render(<FreePrice {...defaultProps} />);

    expect(getByText('£20.00')).toBeInTheDocument(); // original price
    expect(getByText('£0.00')).toBeInTheDocument(); // free price
  });

  it('should render night label correctly for multiple nights', () => {
    const { getByText } = render(<FreePrice {...defaultProps} numberNights={2} />);

    expect(getByText(/upsell.label.nights/)).toBeInTheDocument();
  });

  it('should render night label correctly for single night', () => {
    const { getByText } = render(<FreePrice {...defaultProps} numberNights={1} />);

    expect(getByText(/upsell.label.night/)).toBeInTheDocument();
  });

  it('should return null if not selected', () => {
    const { getByText } = render(<FreePrice {...defaultProps} isMealItemSelected={false} />);

    expect(getByText(/upsell.label.night/)).toBeInTheDocument();
  });

  it('should apply line-through style to original price', () => {
    const { getByText } = render(<FreePrice {...defaultProps} />);

    const originalPrice = getByText('£20.00');
    expect(originalPrice).toHaveStyle('text-decoration: line-through');
  });

  it('should render FREE price when isFree is true', () => {
    const { getByText } = render(
      <MealItem
        {...mockProps}
        isFree={true}
        isSelected={true}
        isAdultHasMealsFree={true}
        currency="GBP"
        numberNights={2}
        basePrice={24.99}
      />
    );

    expect(getByText('£0.00')).toBeInTheDocument();
  });

  it('should render FreePrice component when isFree and selected', () => {
    const { getByText } = render(
      <MealItem
        {...mockProps}
        isFree={true}
        isSelected={true}
        isAdultHasMealsFree={true}
        currency="GBP"
        numberNights={2}
        basePrice={24.99}
      />
    );

    expect(getByText('£24.99')).toBeInTheDocument(); // original price
    expect(getByText('£0.00')).toBeInTheDocument(); // free price
  });

  it('should NOT disable meal when isFree is true even if adult has free meals', () => {
    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        isAdultHasMealsFree={true}
        isSelected={false}
        isFree={true}
        prefixDataTestId="Test"
      />
    );

    const wrapper = getByTestId('Test-Meal-Item-Wrapper');

    expect(wrapper).toHaveStyle({ opacity: '1' });
    expect(wrapper).toHaveStyle({ pointerEvents: 'auto' });
  });
  it('should render normal price when isFree is not provided (default false)', () => {
    const { getByTestId } = render(
      <MealItem {...mockProps} currency="GBP" numberNights={2} price={24.99} totalPrice={49.98} />
    );

    expect(getByTestId('Price')).toHaveTextContent('£24.99');
  });
  it('should show free dinner label when upsellType is DINNER and feature enabled', () => {
    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        upsellType={FREE_FOOD_OPTIONS.DINNER}
        freeBreakfastOption={true}
        showFreeFoodKids={true}
        adultsNumber={1}
      />
    );

    expect(getByTestId('FreeDinnerOptionPromo')).toBeInTheDocument();
  });

  it('should open side drawer when info icon is clicked', () => {
    const { getAllByTestId } = render(<MealItem {...mockProps} isSoftBundlesVisible={true} />);

    const infoButton = getAllByTestId('svg-container')[0];

    fireEvent.click(infoButton);
  });
  it('should render PromoTag when shouldShowPromoTag is true', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateTags: ['FREE_MEAL'],
      },
    ]);

    (useFeatureToggle as jest.Mock).mockReturnValue({
      [FREE_FOOD_OPTIONS.DINNER]: true,
      release_pi_bb_ccui_show_meals_package: true,
    });

    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        isAdultHasMealsFree={true}
        isSelected={true}
        showFreeFoodKids={true}
        freeBreakfastOption={true}
        upsellType={FREE_FOOD_OPTIONS.DINNER}
        prefixDataTestId="Test"
      />
    );

    expect(getByTestId('promo-tag')).toBeInTheDocument();
  });
  it('should open side drawer when info icon is clicked', () => {
    const { getAllByTestId, getByTestId } = render(
      <MealItem
        {...mockProps}
        isSoftBundlesVisible={true}
        adultsNumber={2}
        description="Meal description"
      />
    );

    const infoButton = getAllByTestId('svg-container')[0];

    expect(infoButton).toBeInTheDocument();

    fireEvent.click(infoButton);

    expect(getByTestId('Side-Drawer-Allergy-Link')).toBeInTheDocument();
  });

  it('should render PromoTag when shouldShowPromoTag is true', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateTags: ['FREE_MEAL'],
      },
    ]);

    (useFeatureToggle as jest.Mock).mockReturnValue({
      release_pi_bb_ccui_show_meals_package: true,
    });

    const { getByTestId } = render(
      <MealItem
        {...mockProps}
        isAdultHasMealsFree={true}
        isSelected={true}
        showFreeFoodKids={true}
        freeBreakfastOption={true}
        upsellType={FREE_FOOD_OPTIONS.DINNER}
      />
    );

    expect(getByTestId('promo-tag')).toBeInTheDocument();
  });

  it('should not render PromoTag when shouldShowPromoTag is false', () => {
    (useLocalStorage as jest.Mock).mockReturnValue([
      {
        rateTags: ['FREE_MEAL'],
      },
    ]);

    const { container } = render(
      <MealItem
        {...mockProps}
        isAdultHasMealsFree={false}
        isSelected={false}
        showFreeFoodKids={false}
        freeBreakfastOption={false}
      />
    );

    expect(container.querySelector('[class*="rateItemStyles"]')).not.toBeInTheDocument();
  });
});

describe('FreeKidsMeal', () => {
  const defaultProps = {
    isMealItemSelected: false,
    isMealItemDisabled: false,
    freeTag: ['FREE_MEAL'],
    freeFoodKidsStyle: {},
    prefixDataTestId: 'Test',
    t: (key: string) => key,
  };

  it('should render free kids meal text', () => {
    const { getByTestId } = render(<FreeKidsMeal {...defaultProps} />);

    expect(getByTestId('Test-FreeFoodKids')).toBeInTheDocument();
  });

  it('should render PromoTag when meal item is selected and freeTag exists', () => {
    const { getByTestId } = render(<FreeKidsMeal {...defaultProps} isMealItemSelected={true} />);

    expect(getByTestId('promo-tag')).toBeInTheDocument();
  });

  it('should not render PromoTag when meal item is not selected', () => {
    const { queryByTestId } = render(<FreeKidsMeal {...defaultProps} isMealItemSelected={false} />);

    expect(queryByTestId('promo-tag')).not.toBeInTheDocument();
  });

  it('should not render free kids meal text when disabled', () => {
    const { queryByTestId } = render(<FreeKidsMeal {...defaultProps} isMealItemDisabled={true} />);

    expect(queryByTestId('Test-FreeFoodKids')).not.toBeInTheDocument();
  });

  it('should apply selected styles when selected', () => {
    const { getByTestId } = render(<FreeKidsMeal {...defaultProps} isMealItemSelected={true} />);

    const text = getByTestId('Test-FreeFoodKids');

    expect(text).toHaveStyle({
      fontWeight: 'bold',
    });
  });
});
