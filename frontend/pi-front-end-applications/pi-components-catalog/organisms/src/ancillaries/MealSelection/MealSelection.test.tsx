import '@testing-library/jest-dom';
import type { MealItemExtension, SelectedMealsPerRoom } from '@whitbread-eos/api';
import getConfig from 'next/config';

import { fireEvent, render, waitFor, screen } from '../../utils/test-utils';
import MealSelection, {
  Props as MealProp,
  scrollToMenus,
  handleOnAddAdultMealsSelections,
  handleOnAddChildrenMealsSelections,
  handleRemoveAdultMealsSelections,
  handleRemoveChildrenMealsSelections,
  isAdultMealForEntireStaySelections,
  isAvailableAdultMealsForEntireStay,
} from './MealSelection.component';

const mockCookies = {
  bundles: 'class',
};

const mockFlags = {
  release_pi_display_soft_bundles: false,
  release_pi_bb_ccui_show_meals_package: false,
};

const mockSoftBundles = [
  {
    id: 'BFADBF',
    price: 86.99,
    description: 'Breakfast',
  },
  {
    id: 'HSATWN',
    price: 45.0,
    description: 'Wifi',
  },
];

const mockUseSessionStorage = (key: string) => {
  if (key === 'softBundles') {
    return [mockSoftBundles, jest.fn()];
  }
  return ['', jest.fn()];
};

jest.mock('@whitbread-eos/utils', () => {
  const utils = jest.requireActual('@whitbread-eos/utils');
  return {
    ...utils,
    getCookie: (cookieName: string) => {
      if (cookieName === utils.BUNDLE_CHOICE) {
        return mockCookies.bundles;
      }
    },
    useFeatureToggle: () => ({
      ...mockFlags,
    }),
    useSessionStorage: (key: string) => mockUseSessionStorage(key),
  };
});

const mockOnSaveReservation = jest.fn();
const mockOnAddMeal = jest.fn();
const mockOnRemoveMeal = jest.fn();

const defaultProps: any = {
  adults: 2,
  kids: 1,
  nights: 2,
  adultsMeals: [
    {
      id: 'adult1',
      name: 'Adult Meal 1',
      price: 10,
      freeBreakfastOption: true,
      freeBreakfastCode: 'child1',
      upsellType: 'breakfast',
    },
    {
      id: 'adult2',
      name: 'Adult Meal 2',
      price: 15,
      freeBreakfastOption: true,
      upsellType: 'breakfast',
    },
  ],
  childrenMeals: [
    { id: 'child1', name: 'Child Meal 1', price: 5 },
    { id: 'child2', name: 'Child Meal 2', price: 6 },
  ],
  selectedRoom: 0,
  selectedMeals: [{ adults: [], children: [], reservationId: 'res1' }],
  setSelectedMeals: jest.fn(),
  showFreeFoodKids: true,
  headingTitle: 'Select Meals',
  logoRestaurantUrl: '/logo.png',
  onSaveReservation: mockOnSaveReservation,
  onAddMeal: mockOnAddMeal,
  onRemoveMeal: mockOnRemoveMeal,
  showUpdateMealsButton: true,
};
const mockSetSelectedMeals = jest.fn();

const mealSelectionProps: MealProp = {
  headingTitle: 'Title',
  logoRestaurantUrl:
    'https://www.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/non-hotel-specific/PremierPlus/Premier-Plus-Room-1.jpg',
  adults: 2,
  kids: 2,
  nights: 3,
  childrenMeals: [],
  hasMenus: true,
  showFreeFoodKids: false,
  adultsMeals: [
    {
      id: '1',
      order: 1,
      name: 'Title',
      price: 12,
      currency: '£',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      totalPrice: 24,
      totalPriceForEntireStay: 10,
      freeBreakfastOption: true,
      freeBreakfastCode: 'VWXYZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
    {
      id: '2',
      order: 2,
      name: 'Title',
      price: 15,
      currency: '£',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      totalPrice: 15,
      totalPriceForEntireStay: 10,
      freeBreakfastOption: false,
      freeBreakfastCode: 'VWXYZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
    {
      id: '3',
      order: 3,
      name: 'Title2',
      price: 5,
      currency: '£',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      totalPrice: 5,
      totalPriceForEntireStay: 10,
      freeBreakfastOption: false,
      freeBreakfastCode: 'VWXYZZ',
      freeBreakfastMaxPerMeal: 2,
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedRoom: 0,
  selectedMeals: [
    {
      adults: [],
      children: [],
    },
  ],
  setSelectedMeals: mockSetSelectedMeals,
};

const noMealsAvailable: MealProp = {
  ...mealSelectionProps,
  adultsPerRoom: [3],
  adults: 3,
  selectedMeals: [
    {
      adults: ['1', '1', '1'],
      children: [],
    },
  ],
};

const mealForKidsAvailable: MealProp = {
  ...mealSelectionProps,
  showFreeFoodKids: true,
  selectedMeals: [
    {
      adults: ['1', '2'],
      children: [],
    },
  ],
  childrenMeals: [
    {
      id: '4',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
};

const noMoreMealsForKids: MealProp = {
  ...mealSelectionProps,
  showFreeFoodKids: true,
  childrenMeals: [
    {
      id: '3',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedMeals: [
    {
      adults: ['1', '2'],
      children: ['3', '3'],
    },
  ],
};

const mealForEntireStay: MealProp = {
  ...mealSelectionProps,
  isForEntireStay: true,
  showFreeFoodKids: true,
  childrenMeals: [
    {
      id: '3',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedMeals: [
    {
      adults: [],
      children: [],
    },
  ],
};
const availableMealsForKidsEntireStay: MealProp = {
  ...mealForEntireStay,
  kidsPerRoom: [1],
  kids: 1,
  selectedMeals: [
    {
      adults: ['1'],
      children: [],
    },
  ],
};

const adultMealIsForEntireStay: MealProp = {
  ...mealForEntireStay,
  adultsPerRoom: [1],
  adults: 1,
  selectedMeals: [
    {
      adults: ['1'],
      children: [],
    },
  ],
};

const childrenButtonIsDisableForEntireStay: MealProp = {
  ...mealForEntireStay,
  kidsPerRoom: [1, 1],
  adultsPerRoom: [1, 1],
  childrenMeals: [
    {
      id: '1',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedMeals: [
    {
      adults: ['1'],
      children: [],
    },
    {
      adults: [],
      children: [],
    },
  ],
};

const childrenMealIsForEntireStay: MealProp = {
  ...mealForEntireStay,
  kidsPerRoom: [1, 1],
  adultsPerRoom: [1, 1],
  childrenMeals: [
    {
      id: '1',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedMeals: [
    {
      adults: ['1'],
      children: ['1'],
    },
    {
      adults: ['1'],
      children: ['1'],
    },
  ],
};

const childrenButtonIsAvailableForEntireStay: MealProp = {
  ...mealForEntireStay,
  kidsPerRoom: [1, 0],
  adultsPerRoom: [1, 1],
  childrenMeals: [
    {
      id: '1',
      order: 1,
      name: 'Title',
      imageSrc: '/image_url',
      description: 'description',
      allergyInfoSrc: 'info_url',
      allergyInfoLabel: 'allergy_info_label',
      menu: {
        menuSrc: 'src1',
        name: 'name',
      },
    },
  ],
  selectedMeals: [
    {
      adults: ['1'],
      children: [],
    },
    {
      adults: [],
      children: [],
    },
  ],
};

jest.mock('next/config', () => ({
  __esModule: true,
  default: jest.fn(),
}));

jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => {
    return {
      router: {
        locale: 'en',
      },
    };
  },
}));

describe('scrollToMenus', () => {
  it('should scroll to the menus element', () => {
    const element = document.createElement('div');
    element.id = 'enus';
    document.body.appendChild(element);

    scrollToMenus();
    expect(window.scrollY).toBeGreaterThanOrEqual(0);
    document.body.removeChild(element);
  });

  it('should not scroll if the menus element is not found', () => {
    const originalScrollY = window.scrollY;

    scrollToMenus();
    expect(window.scrollY).toBe(originalScrollY);
  });
});

describe('MealSelection', () => {
  beforeEach(() => {
    jest.resetAllMocks();
    mockFlags.release_pi_bb_ccui_show_meals_package = true;
    mockFlags.release_pi_display_soft_bundles = false;
  });

  it('should render a MealSelection with soft bundles and meal included in bundle', () => {
    const props = {
      ...mealSelectionProps,
      selectedMeals: [
        {
          adults: ['1'],
          children: [],
          reservationId: 'RES123',
        },
      ],
      isSoftBundlesVisible: true,
      softBundleIncludedMeal: { id: '1' },
    };
    const { getAllByTestId, getByTestId } = render(<MealSelection {...props} />);

    const addSoftBundleButtons = getAllByTestId('add-meal-soft-bundle');
    expect(addSoftBundleButtons.length).toBeGreaterThan(0);
    fireEvent.click(addSoftBundleButtons[0]);
    expect(getByTestId('selected-meal-soft-bundle')).toBeInTheDocument();
  });

  it('should render a <MealSelection/> with 3 meal selections', () => {
    const { getAllByTestId } = render(<MealSelection {...mealSelectionProps} />);

    expect(getAllByTestId('Meals-Adults-MealItem-Wrapper').length).toBe(3);
  });

  it('should render a <MealSelection/> with logo restaurant', () => {
    const { getByTestId } = render(<MealSelection {...mealSelectionProps} />);

    expect(getByTestId('Meals-Heading-RestaurantLogo')).toBeInTheDocument();
  });

  it('should render a <MealSelection/> with meal for children', () => {
    const { getByTestId } = render(<MealSelection {...mealForKidsAvailable} />);

    expect(getByTestId('Meals-Children-Heading-Wrapper')).toBeInTheDocument();
  });

  it('should display the correct number of adults and nights received as props', () => {
    const expectedHeader = '(2 upsell.label.adults, 3 upsell.label.nights)';

    const { getByTestId } = render(<MealSelection {...mealSelectionProps} />);
    expect(getByTestId('Meals-Adults-Heading-Values').textContent).toBe(expectedHeader);
  });

  it('should display the correct label if the number of nights is 1', () => {
    (getConfig as jest.Mock).mockImplementation(() => ({
      publicRuntimeConfig: {
        NEXT_IMAGE_UNOPTIMIZED: 'true',
      },
    }));
    const expectedHeader = '(2 upsell.label.adults, 1 upsell.label.night)';

    const { getByTestId } = render(<MealSelection {...mealSelectionProps} nights={1} />);
    expect(getByTestId('Meals-Adults-Heading-Values').textContent).toBe(expectedHeader);
  });

  it('should display the correct label if the number of adults is 1', () => {
    const expectedHeader = '(1 upsell.label.adult, 3 upsell.label.nights)';

    const { getByTestId } = render(<MealSelection {...mealSelectionProps} adults={1} />);
    expect(getByTestId('Meals-Adults-Heading-Values').textContent).toBe(expectedHeader);
  });

  it('should display the correct label if the number of adults is undefined', () => {
    const expectedHeader = '(0 upsell.label.adult, 3 upsell.label.nights)';

    const { getByTestId } = render(<MealSelection {...mealSelectionProps} />);
    expect(getByTestId('Meals-Adults-Heading-Values').textContent).not.toBe(expectedHeader);
  });

  it('should display the correct label if the number of kids is 1', () => {
    const expectedHeader = '(1 upsell.label.child, 3 upsell.label.nights)';

    const { getByTestId } = render(<MealSelection {...mealForKidsAvailable} kids={1} />);
    expect(getByTestId('Meals-Children-Heading-Values').textContent).toBe(expectedHeader);
  });

  it('should display the correct label if the number of kids is undefined', () => {
    const expectedHeader = '(0 upsell.label.child, 3 upsell.label.nights)';

    const { getByTestId } = render(<MealSelection {...mealForKidsAvailable} />);
    expect(getByTestId('Meals-Children-Heading-Values').textContent).not.toBe(expectedHeader);
  });

  it('should call the handler for adding remaining meals correctly', async () => {
    const { getAllByTestId } = render(<MealSelection {...mealSelectionProps} />);

    fireEvent.click(getAllByTestId('Meals-Adults-MealItem-AddSubtractControls-AddButton')[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should call the handler for removing remaining meals correctly', async () => {
    const { getAllByTestId } = render(<MealSelection {...noMealsAvailable} />);

    fireEvent.click(getAllByTestId('Meals-Adults-MealItem-AddSubtractControls-SubtractButton')[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should allow clicking on adult meals plus button if no child meals are available', async () => {
    const { getAllByTestId } = render(<MealSelection {...noMealsAvailable} />);

    expect(getAllByTestId('Meals-Adults-MealItem-AddSubtractControls-AddButton')[0]).toBeEnabled();
    expect(
      getAllByTestId('Meals-Adults-MealItem-AddSubtractControls-SubtractButton')[0]
    ).toBeEnabled();
  });

  it('should not allow clicking on plus button for child meal if no child meals are available', async () => {
    const { getAllByTestId } = render(<MealSelection {...noMoreMealsForKids} />);

    expect(
      getAllByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton')[0]
    ).toBeDisabled();
    expect(
      getAllByTestId('Meals-Children-MealItem-AddSubtractControls-SubtractButton')[0]
    ).toBeEnabled();

    fireEvent.click(
      getAllByTestId('Meals-Children-MealItem-AddSubtractControls-SubtractButton')[0]
    );
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should render <MealSelection/>  for entire stay with default props', async () => {
    const { getAllByTestId } = render(<MealSelection {...mealForEntireStay} />);

    const adultMealsButtons = getAllByTestId('Meals-Adults-MealItem-Button');
    expect(adultMealsButtons.length).toBe(3);
    expect(adultMealsButtons[0]).toHaveTextContent('upsell.extras.remove');

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons.length).toBe(1);
    expect(childrenMealsButtons[0]).toBeDisabled();
    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.add');

    fireEvent.click(adultMealsButtons[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should render <MealSelection/>  and kids eats free is available', async () => {
    const { getAllByTestId } = render(<MealSelection {...availableMealsForKidsEntireStay} />);

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.add');
    expect(childrenMealsButtons[0]).toBeDisabled();
  });

  it('calls onRemoveMeal handler when adult meal subtract button is clicked', async () => {
    const props = {
      ...mealSelectionProps,
      selectedMeals: [
        {
          adults: ['1'],
          children: [],
          reservationId: 'RES123',
        },
      ],
    };

    const { getAllByTestId } = render(<MealSelection {...props} />);
    const subtractButton = getAllByTestId('Meals-Adults-MealItem-AddSubtractControls-AddButton')[0];

    fireEvent.click(subtractButton);
  });
  it('should render <MealSelection/>  and the first adult meal is for entire stay', async () => {
    const { getAllByTestId } = render(<MealSelection {...adultMealIsForEntireStay} />);

    const adultMealsButtons = getAllByTestId('Meals-Adults-MealItem-Button');
    expect(adultMealsButtons[0]).toHaveTextContent('upsell.extras.remove');

    fireEvent.click(adultMealsButtons[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should render <MealSelection/>  and the first children meal is for entire stay', async () => {
    const { getAllByTestId } = render(<MealSelection {...childrenMealIsForEntireStay} />);

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    screen.debug(childrenMealsButtons);
    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.remove');
  });

  it("should render <MealSelection/>  and the first children meal disable because in room 2 we don't have an adult meal with kidsEatsFree true", async () => {
    const { getAllByTestId } = render(<MealSelection {...childrenButtonIsDisableForEntireStay} />);

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons[0]).toBeDisabled();

    fireEvent.click(childrenMealsButtons[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalledTimes(0);
    });
  });

  it("should render <MealSelection/>  and the first children meal available because in room 2 we don't have kids", async () => {
    const { getAllByTestId } = render(
      <MealSelection {...childrenButtonIsAvailableForEntireStay} />
    );
    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    screen.debug(childrenMealsButtons);
    expect(childrenMealsButtons[0]).toBeDisabled();
  });

  it('renders notification when adultsMeals is empty', () => {
    const notification = <div data-testid="notification">No meals available</div>;
    render(
      <MealSelection
        adultsMeals={[]}
        childrenMeals={[]}
        adults={2}
        kids={1}
        nights={1}
        selectedRoom={0}
        selectedMeals={[{ adults: [], children: [], reservationId: '123' }]}
        setSelectedMeals={jest.fn()}
        logoRestaurantUrl=""
        showFreeFoodKids={false}
        headingTitle="Meals"
        notification={notification}
      />
    );

    expect(screen.getByTestId('notification')).toBeInTheDocument();
  });
  it('hides plus and subtract buttons when hasPromoMeal is true', () => {
    render(
      <MealSelection
        adultsMeals={[
          {
            id: 'adult1',
            name: 'Adult Meal',
            freeBreakfastOption: false,
            price: 10,
            currency: 'GBP',
            imageSrc: '',
            description: '',
          },
        ]}
        childrenMeals={[]}
        adults={1}
        kids={0}
        nights={1}
        selectedRoom={0}
        selectedMeals={[{ adults: [], children: [], reservationId: '123' }]}
        setSelectedMeals={jest.fn()}
        logoRestaurantUrl=""
        showFreeFoodKids={false}
        headingTitle="Meals"
        hasPromoMeal={true}
        isAdultHasMealsFree={true}
      />
    );

    const addButton = screen.queryByTestId('Meals-Adults-MealItem-AddSubtractControls-Plus');
    const subtractButton = screen.queryByTestId(
      'Meals-Adults-MealItem-AddSubtractControls-Subtract'
    );

    expect(addButton).not.toBeInTheDocument();
    expect(subtractButton).not.toBeInTheDocument();
  });

  it('renders correctly when both adultsMeals and childrenMeals are empty', () => {
    render(<MealSelection {...mealSelectionProps} adultsMeals={[]} childrenMeals={[]} />);

    expect(screen.queryByTestId('Meals-Adults-MealItem-Wrapper')).not.toBeInTheDocument();
    expect(screen.queryByTestId('Meals-Children-MealItem-Wrapper')).not.toBeInTheDocument();
  });
  it('disables add button when isForEntireStay is true and max meals are selected', () => {
    render(<MealSelection {...noMoreMealsForKids} />);

    const addButton = screen.getAllByTestId(
      'Meals-Children-MealItem-AddSubtractControls-AddButton'
    )[0];

    expect(addButton).toBeDisabled();
  });

  it('should enable child meal add button when matching adult meal is selected', () => {
    const props = {
      ...mealSelectionProps,
      adultsMeals: [
        {
          id: 'ADULT1',
          name: 'Adult Meal 1',
          freeBreakfastOption: true, // free breakfast
          freeBreakfastCode: 'CHILD1', // maps to child meal
          upsellType: 'A',
          price: 10,
          totalPrice: 10,
          imageSrc: '',
          allergyInfoSrc: '',
          allergyInfoLabel: '',
          currency: 'GBP',
          description: '',
        },
      ],
      selectedMeals: [
        {
          adults: ['ADULT1'], // adult meal selected
          children: [],
          reservationId: 'RES123',
        },
      ],
    };

    const { getByTestId } = render(<MealSelection {...props} />);
    const plusButton = getByTestId('Meals-Adults-MealItem-AddSubtractControls-AddButton');
    expect(plusButton).not.toBeDisabled();
  });
});

describe('Meals selection', () => {
  const mealSelectionProps: MealProp = {
    headingTitle: 'Select your meals',
    logoRestaurantUrl: '/content/dam/global/restaurants/logo.png',
    adults: 2,
    kids: 2,
    nights: 2,
    selectedRoom: 0,
    showFreeFoodKids: true,
    hasMenus: true,
    adultsMeals: [
      {
        id: 'BFADBF', // Premier Inn Breakfast
        name: 'Premier Inn Breakfast',
        price: 110.99,
        currency: 'GBP',
        description: 'Add our unlimited breakfast and look forward to freshly cooked bacon.',
        imageSrc:
          '/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Cooked WHB24136.jpg',
        allergyInfoSrc:
          '/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf',
        allergyInfoLabel: 'Allergy & nutrition info',
        freeBreakfastOption: true,
        freeBreakfastCode: 'BFCHDF', // Maps to kids meal
        freeBreakfastMaxPerMeal: 2,
        upsellType: 'breakfast',
        menu: {
          menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
          name: 'Breakfast menu',
        },
        order: 1,
        totalPrice: 221.98, // price * adults
        totalPriceForEntireStay: 443.96, // price * adults * nights
      },
      {
        id: 'BFADCT', // Continental Breakfast
        name: 'Continental Breakfast',
        price: 9,
        currency: 'GBP',
        description: 'A lighter start with tasty pastries, pancakes, fruit and cereals.',
        imageSrc:
          '/content/dam/global/restaurants/THY/2025/booking-flow/PIFB - Solus Breakfast Continental WHB2413.jpg',
        allergyInfoSrc:
          'https://barandblock.dit.premierinn.digital/en-gb/allergy-nutrition/bb-652535-web.html',
        allergyInfoLabel: 'Allergy & nutrition info',
        freeBreakfastOption: false,
        freeBreakfastCode: '',
        freeBreakfastMaxPerMeal: 0,
        upsellType: '',
        menu: {
          menuSrc: '/content/dam/global/restaurants/Global/premier-inn-breakfast.pdf',
          name: 'Breakfast menu',
        },
        order: 2,
        totalPrice: 18,
        totalPriceForEntireStay: 36,
      },
    ],
    childrenMeals: [
      {
        id: 'BFCHDF', // Free breakfast for kids
        name: 'Free breakfast for kids',
        imageSrc: '/content/dam/global/restaurants/Global/child-breakfast.jpg',
        description:
          'Up to two kids eat breakfast for free when an adult orders a Premier Inn Breakfast.',
        allergyInfoSrc:
          '/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-breakfast.pdf',
        allergyInfoLabel: 'Allergy & nutrition info',
        menu: undefined,
        order: 0,
      },
      {
        id: 'DBCHDF', // Free Children's Dinner
        name: "Free Children's Dinner",
        imageSrc: '/content/dam/global/restaurants/Global/kids-eat-1pound-offer.jpg',
        description:
          'With every paying adult selecting the dinner, a child will eat dinner for free.',
        allergyInfoSrc:
          '/content/dam/global/restaurants/allergy-nutrition-info/allergy-nutrition-thyme.pdf',
        allergyInfoLabel: 'Allergy & nutrition info',
        menu: undefined,
        order: 5,
      },
    ],
    selectedMeals: [
      {
        adults: [], // no adult meals initially selected
        children: [], // no kids meals initially selected
        reservationId: '3428958',
      },
    ],
    setSelectedMeals: jest.fn(),
  };
  it('enables child meal add button if matching adult meal is selected', () => {
    const props = {
      ...mealSelectionProps,
      selectedMeals: [
        {
          adults: ['BFADBF'], // Premier Inn Breakfast selected
          children: [],
          reservationId: '3428958',
        },
      ],
    };

    render(<MealSelection {...props} />);
  });

  it('disables child meal add button if adult meal does not have freeBreakfastCode', () => {
    const props = {
      ...mealSelectionProps,
      adultsMeals: [
        {
          id: 'ADULT1',
          name: 'Adult Meal 1',
          freeBreakfastOption: true,
          freeBreakfastCode: undefined, // No mapping
          price: 10,
          imageSrc: '',
          currency: 'GBP',
          description: '',
        },
      ],
      childrenMeals: [
        {
          id: 'CHILD1',
          name: 'Child Meal 1',
          imageSrc: '',
          description: '',
        },
      ],
      selectedMeals: [
        {
          adults: ['ADULT1'], // Adult meal selected
          children: [],
        },
      ],
    };

    const { getAllByTestId } = render(<MealSelection {...props} />);
    const childAddButtons = getAllByTestId('Meals-Adults-MealItem-Description');
    expect(childAddButtons[0]).not.toBeDisabled();
  });

  it('does not render children meals section when showFreeFoodKids is false', () => {
    const props = {
      ...mealSelectionProps,
      showFreeFoodKids: false,
    };

    const { queryByTestId } = render(<MealSelection {...props} />);
    expect(queryByTestId('Meals-Children-Heading-Wrapper')).not.toBeInTheDocument();
  });

  it('disables all add and subtract buttons when hasPromoMeal is true', () => {
    const props = {
      ...mealSelectionProps,
      hasPromoMeal: true,
      isAdultHasMealsFree: true,
    };

    render(<MealSelection {...props} />);

    const addButtons = screen.queryAllByTestId(/AddButton/i);
    const subtractButtons = screen.queryAllByTestId(/SubtractButton/i);

    addButtons.forEach((btn) => expect(btn).toBeDisabled());
    subtractButtons.forEach((btn) => expect(btn).toBeDisabled());
  });

  it('enables child meal button only for room with matching adult meal selected', () => {
    const props = {
      ...mealSelectionProps,
      adultsPerRoom: [1, 1],
      kidsPerRoom: [1, 1],
      adultsMeals: [
        {
          id: 'ADULT1',
          name: 'Adult Meal 1',
          freeBreakfastOption: true,
          freeBreakfastCode: 'CHILD1',
          price: 10,
          imageSrc: '',
          currency: 'GBP',
          description: '',
        },
      ],
      childrenMeals: [
        {
          id: 'CHILD1',
          name: 'Child Meal 1',
          imageSrc: '',
          description: '',
        },
      ],
      selectedMeals: [
        { adults: ['ADULT1'], children: [] }, // Room 1
        { adults: [], children: [] }, // Room 2
      ],
    };

    const { getAllByTestId } = render(<MealSelection {...props} />);

    const childAddButtons = getAllByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton');
    expect(childAddButtons[0]).toBeDisabled();
  });

  it('disables child meal add button if matching adult meal is removed', async () => {
    const props = {
      ...mealSelectionProps,
      adultsMeals: [
        {
          id: 'ADULT1',
          name: 'Adult Meal 1',
          freeBreakfastOption: true,
          freeBreakfastCode: 'CHILD1',
          price: 10,
          imageSrc: '',
          currency: 'GBP',
          description: '',
        },
      ],
      childrenMeals: [
        {
          id: 'CHILD1',
          name: 'Child Meal 1',
          imageSrc: '',
          description: '',
        },
      ],
      selectedMeals: [
        {
          adults: ['ADULT1'],
          children: [],
        },
      ],
    };

    const { getByTestId } = render(<MealSelection {...props} />);
    const adultSubtractButton = getByTestId(
      'Meals-Adults-MealItem-AddSubtractControls-SubtractButton'
    );
    fireEvent.click(adultSubtractButton);

    const childAddButton = getByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton');
    await waitFor(() => expect(childAddButton).toBeDisabled());
  });

  it('enables child meal add button if matching adult meal is added', async () => {
    const props = {
      ...mealSelectionProps,
      adultsMeals: [
        {
          id: 'ADULT1',
          name: 'Adult Meal 1',
          freeBreakfastOption: true,
          freeBreakfastCode: 'CHILD1',
          price: 10,
          imageSrc: '',
          currency: 'GBP',
          description: '',
        },
      ],
      childrenMeals: [
        {
          id: 'CHILD1',
          name: 'Child Meal 1',
          imageSrc: '',
          description: '',
        },
      ],
      selectedMeals: [
        {
          adults: [], // No adult meals selected initially
          children: [],
        },
      ],
    };

    const { getByTestId } = render(<MealSelection {...props} />);

    const adultAddButton = getByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton');
    fireEvent.click(adultAddButton);
  });
  it('renders heading and logo', () => {
    render(<MealSelection {...defaultProps} />);
    expect(screen.getByText('Select Meals')).toBeInTheDocument();
    expect(screen.getByRole('img')).toBeInTheDocument();
  });

  it('calls onSaveReservation when Update Meals button is clicked', () => {
    render(<MealSelection {...defaultProps} />);
    const updateButton = screen.getByTestId('Meals-Update-Meal-Selection');
    fireEvent.click(updateButton);
    expect(mockOnSaveReservation).toHaveBeenCalled();
  });

  it('calls onSaveReservation when Update Meals button is clicked', () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = false;
    render(<MealSelection {...defaultProps} />);
    const updateButton = screen.getByTestId('Meals-Update-Meal-Selection');
    fireEvent.click(updateButton);
    expect(mockOnSaveReservation).toHaveBeenCalled();
  });

  it('handles kids meal subtraction with isshowKidsMealsFreeFlag enabled', () => {
    const setSelectedMeals = jest.fn();

    render(
      <MealSelection
        adults={2}
        kids={1}
        nights={2}
        adultsMeals={[
          { id: 'ADULT_MEAL', name: 'Adult Meal', price: 10, freeBreakfastOption: true },
        ]}
        childrenMeals={[{ id: 'KIDS_MEAL', name: 'Kids Meal', price: 5 }]}
        selectedMeals={[{ adults: ['ADULT_MEAL'], children: ['KIDS_MEAL'], reservationId: '123' }]}
        setSelectedMeals={setSelectedMeals}
        selectedRoom={0}
        logoRestaurantUrl=""
        headingTitle="Test Meals"
        showFreeFoodKids={true}
      />
    );

    const subtractButton = screen.getByTestId(/Children-MealItem-AddSubtractControls-Subtract/i);
    fireEvent.click(subtractButton);

    expect(setSelectedMeals).toHaveBeenCalled();
  });
  it('adds and removes adult meals for entire stay', () => {
    const setSelectedMeals = jest.fn();

    render(
      <MealSelection
        adults={2}
        kids={1}
        nights={3}
        adultsMeals={[
          { id: 'ADULT_MEAL', name: 'Adult Meal', price: 10, freeBreakfastOption: true },
        ]}
        childrenMeals={[]}
        selectedMeals={[
          { adults: [], children: [], reservationId: '123' },
          { adults: [], children: [], reservationId: '456' },
        ]}
        setSelectedMeals={setSelectedMeals}
        selectedRoom={0}
        adultsPerRoom={[2, 2]}
        kidsPerRoom={[1, 1]}
        isForEntireStay={true}
        logoRestaurantUrl=""
        headingTitle="Test Meals"
        showFreeFoodKids={false}
      />
    );

    const addButton = screen.getByTestId(/Adults-MealItem-Button/i);
    fireEvent.click(addButton);

    expect(setSelectedMeals).toHaveBeenCalled();

    const removeButton = screen.getByTestId(/Adults-MealItem-Button/i);
    fireEvent.click(removeButton);

    expect(setSelectedMeals).toHaveBeenCalledTimes(2);
  });

  it('handles removeMealIds when mealId not present in children array', () => {
    const setSelectedMeals = jest.fn();

    render(
      <MealSelection
        adults={1}
        kids={1}
        nights={1}
        adultsMeals={[
          { id: 'ADULT_MEAL', name: 'Adult Meal', price: 10, freeBreakfastOption: true },
        ]}
        childrenMeals={[{ id: 'KIDS_MEAL', name: 'Kids Meal', price: 5 }]}
        selectedMeals={[{ adults: ['ADULT_MEAL'], children: [], reservationId: '123' }]} // 👈 no child meal
        setSelectedMeals={setSelectedMeals}
        selectedRoom={0}
        logoRestaurantUrl=""
        headingTitle="Test Meals"
        showFreeFoodKids={true}
      />
    );

    const subtractButton = screen.getByTestId(/Adults-MealItem-AddSubtractControls-Subtract/i);
    fireEvent.click(subtractButton);

    expect(setSelectedMeals).toHaveBeenCalled();
  });

  it('should render dinner item', () => {
    ((defaultProps.adultsMeals = [
      {
        id: 'adult1',
        name: 'Adult Meal 1',
        price: 10,
        freeBreakfastOption: true,
        freeBreakfastCode: 'child1',
        upsellType: 'breakfast',
      },
      {
        id: 'adult2',
        name: 'Adult Meal 2',
        price: 15,
        freeBreakfastOption: true,
        upsellType: 'dinner',
      },
    ]),
      render(<MealSelection {...defaultProps} />));
    expect(screen.getByText('Select Meals')).toBeInTheDocument();
    expect(screen.getByRole('img')).toBeInTheDocument();
  });
  test('renders adult dinner meal if upsellType is dinner', () => {
    const adultsMeals = [{ id: 'a1', name: 'Adult Dinner', upsellType: 'dinner' }];
    render(<MealSelection {...defaultProps} adultsMeals={adultsMeals} />);

    expect(screen.getByText('Adult Dinner')).toBeInTheDocument();
  });
  test('does not render dinner section if no dinner meals exist', () => {
    const adultsMeals = [{ id: 'a1', name: 'Only Breakfast', upsellType: 'breakfast' }];
    render(<MealSelection {...defaultProps} adultsMeals={adultsMeals} />);

    expect(screen.queryByText('Dinner')).not.toBeInTheDocument();
  });

  it('preserves child breakfast when adding adult Meal Deal (mapped to dinner)', () => {
    const mealDeal = { id: '101', upsellType: 'MEALDEAL' } as MealItemExtension;
    const adultsMeals = [{ id: '102', upsellType: 'DINNER' }] as MealItemExtension[];
    const selectedMeals = [
      {
        reservationId: 'res-1',
        adults: ['102'],
        children: ['c1'], // Currently selected child meal
      },
    ];
    const adultsPerRoom = [1];

    const childrenMeals: MealItemExtension[] = [
      {
        id: 'c1',
        name: 'Child Combo Meal',
        upsellType: 'BREAKFAST,DINNER', // Combo child meal
      },
      {
        id: 'c2',
        name: 'Child Dinner Only',
        upsellType: 'DINNER',
      },
      {
        id: 'c3',
        name: 'Child Breakfast Only',
        upsellType: 'BREAKFAST',
      },
    ];

    handleOnAddAdultMealsSelections(
      mealDeal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      mockSetSelectedMeals
    );

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'res-1',
        adults: ['102', '101'], // 1 existing + 1 new meal
        children: expect.arrayContaining(['c1']), // 'c1' retained because it still has BREAKFAST
      },
    ]);
  });

  it('adds child meals if not already present in selection', () => {
    const meal = { id: 'CH1' } as MealItemExtension;
    const selectedMeals = [{ adults: [], children: [], reservationId: 'res-1' }];
    const kidsPerRoom = [2];

    handleOnAddChildrenMealsSelections(meal, selectedMeals, kidsPerRoom, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CH1', 'CH1'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('does not add child meals if already selected', () => {
    const meal = { id: 'CH1' } as MealItemExtension;
    const selectedMeals = [{ adults: [], children: ['CH1'], reservationId: 'res-1' }];
    const kidsPerRoom = [2];

    handleOnAddChildrenMealsSelections(meal, selectedMeals, kidsPerRoom, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CH1'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('appends new child meal alongside different existing ones', () => {
    const meal = { id: 'CH2' } as MealItemExtension;
    const selectedMeals = [{ adults: [], children: ['CH1'], reservationId: 'res-1' }];
    const kidsPerRoom = [1];

    handleOnAddChildrenMealsSelections(meal, selectedMeals, kidsPerRoom, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CH1', 'CH2'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('handles multiple rooms independently', () => {
    const meal = { id: 'CHX' } as MealItemExtension;
    const selectedMeals = [
      { adults: ['A1'], children: ['CH1'], reservationId: 'res-1' },
      { adults: ['A2'], children: [], reservationId: 'res-2' },
    ];
    const kidsPerRoom = [1, 2];

    handleOnAddChildrenMealsSelections(meal, selectedMeals, kidsPerRoom, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['A1'],
        children: ['CH1', 'CHX'],
        reservationId: 'res-1',
      },
      {
        adults: ['A2'],
        children: ['CHX', 'CHX'],
        reservationId: 'res-2',
      },
    ]);
  });
  it('preserves adults and reservationId even when modifying children', () => {
    const meal = { id: 'CH3' } as MealItemExtension;
    const selectedMeals = [{ adults: ['A1', 'A2'], children: [], reservationId: 'res-123' }];
    const kidsPerRoom = [1];

    handleOnAddChildrenMealsSelections(meal, selectedMeals, kidsPerRoom, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['A1', 'A2'],
        children: ['CH3'],
        reservationId: 'res-123',
      },
    ]);
  });
  it('removes adult meals that match the upsellType of the meal', () => {
    const meal = { id: 'BF1', upsellType: 'breakfast' } as MealItemExtension;

    const selectedMeals = [
      {
        adults: ['BF1', 'DIN1'],
        children: [],
        reservationId: 'res-1',
      },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['DIN1'],
        children: [],
        reservationId: 'res-1',
      },
    ]);
  });
  it('removes child meals that match the freeBreakfastCode of the adult meal', () => {
    const meal = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastCode: ['CHBF1'],
    } as any;

    const selectedMeals = [
      {
        adults: ['BF1'],
        children: ['CHBF1', 'CHDIN1'],
        reservationId: 'res-1',
      },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CHDIN1'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('preserves adult and child meals not related to the given meal', () => {
    const meal = {
      id: 'MD1',
      upsellType: 'mealDeal',
      freeBreakfastCode: 'CHMD1',
    } as MealItemExtension;

    const selectedMeals = [
      {
        adults: ['BF1', 'DIN1'],
        children: ['CHDIN1', 'CHMD2'],
        reservationId: 'res-2',
      },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['BF1', 'DIN1'],
        children: ['CHDIN1', 'CHMD2'],
        reservationId: 'res-2',
      },
    ]);
  });
  it('removes matching adult and child meals across multiple rooms', () => {
    const meal = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastCode: 'CHBF1',
    } as MealItemExtension;

    const selectedMeals = [
      {
        adults: ['BF1', 'DIN1'],
        children: ['CHBF1'],
        reservationId: 'res-A',
      },
      {
        adults: ['BF1', 'MD1'],
        children: ['CHBF1', 'CHMD1'],
        reservationId: 'res-B',
      },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['DIN1'],
        children: [],
        reservationId: 'res-A',
      },
      {
        adults: ['MD1'],
        children: ['CHMD1'],
        reservationId: 'res-B',
      },
    ]);
  });
  it('does nothing if meal has no upsellType and no freeBreakfastCode', () => {
    const meal = { id: 'X1' } as MealItemExtension;

    const selectedMeals = [
      {
        adults: ['DIN1'],
        children: ['CH1'],
        reservationId: 'res-1',
      },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['DIN1'],
        children: ['CH1'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('removes matching child meal id from a single room', () => {
    const meal = { id: 'CHBF1' } as MealItemExtension;

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        adults: [],
        children: ['CHBF1', 'CHDIN1'],
        reservationId: 'res-1',
      },
    ];

    handleRemoveChildrenMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CHDIN1'],
        reservationId: 'res-1',
      },
    ]);
  });
  it('removes matching child meal from multiple rooms', () => {
    const meal = { id: 'CHBF1' } as MealItemExtension;

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        adults: [],
        children: ['CHBF1', 'CHDIN1'],
        reservationId: 'res-A',
      },
      {
        adults: ['DIN1'],
        children: ['CHBF1'],
        reservationId: 'res-B',
      },
    ];

    handleRemoveChildrenMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: [],
        children: ['CHDIN1'],
        reservationId: 'res-A',
      },
      {
        adults: ['DIN1'],
        children: [],
        reservationId: 'res-B',
      },
    ]);
  });
  it('preserves adult meals and reservationId while removing children', () => {
    const meal = { id: 'CHBF1' } as MealItemExtension;

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        adults: ['BF1'],
        children: ['CHBF1'],
        reservationId: 'res-1',
      },
    ];

    handleRemoveChildrenMealsSelections(meal, selectedMeals, mockSetSelectedMeals);

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        adults: ['BF1'],
        children: [],
        reservationId: 'res-1',
      },
    ]);
  });

  it('returns true when meal is selected in all rooms with correct upsell type', () => {
    const mealId = 'BF1';
    const adultsMeals: MealItemExtension[] = [{ id: 'BF1', upsellType: 'breakfast' }];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['BF1'], children: [], reservationId: 'res-2' },
    ];

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(true);
  });
  it('returns true when one room does not have the meal selected', () => {
    const mealId = 'BF1';
    const adultsMeals: MealItemExtension[] = [{ id: 'BF1', upsellType: 'breakfast' }];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['DIN1'], children: [], reservationId: 'res-2' },
    ];

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(true);
  });
  it('returns true when selected meal id matches but upsellType does not', () => {
    const mealId = 'BF1';
    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'BF1', upsellType: 'dinner' }, // different instance of same id
    ];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['BF1'], children: [], reservationId: 'res-2' },
    ];

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(true);
  });
  it('returns true when meal has multiple upsellTypes and all match', () => {
    const mealId = 'MD1';
    const adultsMeals: MealItemExtension[] = [{ id: 'MD1', upsellType: 'breakfast,dinner' }];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['MD1'], children: [], reservationId: 'res-1' },
      { adults: ['MD1'], children: [], reservationId: 'res-2' },
    ];

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(true);
  });
  it('returns false when meal is not found in adultsMeals list', () => {
    const mealId = 'UNKNOWN';
    const adultsMeals: MealItemExtension[] = [{ id: 'BF1', upsellType: 'breakfast' }];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
    ];

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(false);
  });
  it('returns true when meal ID matches but upsell type does not for even one room', () => {
    const mealId = 'MD1';
    const adultsMeals: MealItemExtension[] = [{ id: 'MD1', upsellType: 'breakfast,dinner' }];

    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['MD1'], children: [], reservationId: 'res-1' },
      {
        adults: ['MD1'], // Only "breakfast" present, missing "dinner"
        children: [],
        reservationId: 'res-2',
      },
    ];

    // Simulate that second room's MD1 does not include 'dinner'
    const original = Array.prototype.find;

    expect(isAdultMealForEntireStaySelections(mealId, adultsMeals, selectedMeals)).toBe(true);

    Array.prototype.find = original; // restore
  });

  it('returns true when mealItem has no upsellType', () => {
    const mealItem = { id: 'MD1', upsellType: undefined };
    const selectedMeals: SelectedMealsPerRoom[] = [];
    const adultsMeals: MealItemExtension[] = [];

    expect(isAvailableAdultMealsForEntireStay(mealItem, selectedMeals, adultsMeals)).toBe(true);
  });

  it('returns true when mealItem has empty upsellType array', () => {
    const mealItem = { id: 'MD1', upsellType: [] };
    const selectedMeals: SelectedMealsPerRoom[] = [];
    const adultsMeals: MealItemExtension[] = [];

    expect(isAvailableAdultMealsForEntireStay(mealItem as any, selectedMeals, adultsMeals)).toBe(
      true
    );
  });
  it('returns false when no adult meals selected in any room', () => {
    const mealItem = { id: 'BF1', upsellType: 'breakfast' };
    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: [], children: [], reservationId: 'res-1' },
      { adults: [], children: [], reservationId: 'res-2' },
    ];
    const adultsMeals: MealItemExtension[] = [];

    expect(isAvailableAdultMealsForEntireStay(mealItem, selectedMeals, adultsMeals)).toBe(false);
  });
  it('returns true if a mealType from the mealItem exists in all rooms', () => {
    const mealItem = { id: 'BF1', upsellType: 'breakfast' };
    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'BF2', upsellType: 'breakfast' },
    ];
    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['BF2'], children: [], reservationId: 'res-2' },
    ];

    expect(isAvailableAdultMealsForEntireStay(mealItem, selectedMeals, adultsMeals)).toBe(true);
  });
  it('returns false if a mealType is not present in all rooms', () => {
    const mealItem = { id: 'DIN1', upsellType: 'dinner' };
    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'DIN1', upsellType: 'dinner' },
    ];
    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' }, // no dinner here
      { adults: ['DIN1'], children: [], reservationId: 'res-2' },
    ];

    expect(isAvailableAdultMealsForEntireStay(mealItem, selectedMeals, adultsMeals)).toBe(false);
  });
  it('returns true if at least one upsellType (from array) exists in all rooms', () => {
    const mealItem = { id: 'MD1', upsellType: ['breakfast', 'dinner'] };
    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'DIN1', upsellType: 'dinner' },
    ];
    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['BF1'], children: [], reservationId: 'res-2' },
    ];

    expect(
      isAvailableAdultMealsForEntireStay(mealItem as any, selectedMeals, adultsMeals as any)
    ).toBe(true); // 'breakfast' is in both rooms
  });
  it('returns false if no upsellType from mealItem is in all rooms', () => {
    const mealItem = { id: 'MD1', upsellType: ['breakfast', 'dinner'] };
    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'DIN1', upsellType: 'dinner' },
    ];
    const selectedMeals: SelectedMealsPerRoom[] = [
      { adults: ['BF1'], children: [], reservationId: 'res-1' },
      { adults: ['DIN1'], children: [], reservationId: 'res-2' }, // not consistent across both
    ];

    expect(isAvailableAdultMealsForEntireStay(mealItem as any, selectedMeals, adultsMeals)).toBe(
      false
    );
  });

  it('should call the handler for adding remaining meals correctly for entire stay', async () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = false;
    const { getAllByTestId } = render(<MealSelection {...mealForKidsAvailable} />);

    fireEvent.click(getAllByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton')[0]);
    await waitFor(() => {
      fireEvent.click(
        getAllByTestId('Meals-Children-MealItem-AddSubtractControls-SubtractButton')[0]
      );
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should call the handler for adding remaining meals correctly for entire stay', async () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = true;
    const { getAllByTestId } = render(<MealSelection {...mealForKidsAvailable} />);

    fireEvent.click(getAllByTestId('Meals-Children-MealItem-AddSubtractControls-AddButton')[0]);
    await waitFor(() => {
      fireEvent.click(
        getAllByTestId('Meals-Children-MealItem-AddSubtractControls-SubtractButton')[0]
      );
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should render extras when feature flag is off', async () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = false;
    const { getAllByTestId } = render(<MealSelection {...adultMealIsForEntireStay} />);

    const adultMealsButtons = getAllByTestId('Meals-Adults-MealItem-Button');
    expect(adultMealsButtons.length).toBe(3);
    expect(adultMealsButtons[0]).toHaveTextContent('upsell.extras.remove');

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons.length).toBe(1);
    expect(childrenMealsButtons[0]).toBeEnabled();
    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.add');

    fireEvent.click(adultMealsButtons[0]);
    await waitFor(() => {
      fireEvent.click(childrenMealsButtons[0]);
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });

  it('should render extras when feature flag is on', async () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = true;
    const { getAllByTestId } = render(<MealSelection {...adultMealIsForEntireStay} />);

    const adultMealsButtons = getAllByTestId('Meals-Adults-MealItem-Button');
    expect(adultMealsButtons.length).toBe(3);
    expect(adultMealsButtons[0]).toHaveTextContent('upsell.extras.remove');

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons.length).toBe(1);
    expect(childrenMealsButtons[0]).toBeDisabled();
    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.add');

    fireEvent.click(adultMealsButtons[0]);
    await waitFor(() => {
      fireEvent.click(childrenMealsButtons[0]);
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });
  it('should render extras when feature flag is ON', async () => {
    mockFlags.release_pi_bb_ccui_show_meals_package = true;
    const { getAllByTestId } = render(
      <MealSelection
        {...adultMealIsForEntireStay}
        isForEntireStay={true}
        adults={2}
        kidsPerRoom={[1]}
        adultsMeals={[
          {
            id: 'BFGROL',
            name: 'Breakfast Roll Bundle',
            upsellType: 'breakfast',
            freeBreakfastCode: 'BFCHDF',
            freeBreakfastOption: true,
          },
          {
            id: 'DBR',
            name: 'Dinner and Drink Bundle for £19.99',
            upsellType: 'dinner',
            freeBreakfastCode: 'DBCHDF',
            freeBreakfastOption: true,
          },
        ]}
        childrenMeals={[
          {
            id: 'BFCHDF',
            name: 'Free breakfast for kids',
          },
          {
            id: 'DBCHDF',
            name: "Free Children's Dinner",
          },
        ]}
        selectedMeals={[
          { adults: ['DBR'], children: [], reservationId: '3496432' },
          { adults: ['DBR'], children: [], reservationId: '3497276' },
        ]}
      />
    );

    const adultMealsButtons = getAllByTestId('Meals-Adults-MealItem-Button');
    expect(adultMealsButtons.length).toBe(2);
    expect(adultMealsButtons[0]).toHaveTextContent('upsell.extras.add');

    const childrenMealsButtons = getAllByTestId('Meals-Children-MealItem-Button');
    expect(childrenMealsButtons.length).toBe(2);

    expect(childrenMealsButtons[0]).toHaveTextContent('upsell.extras.add');

    fireEvent.click(adultMealsButtons[0]);
    fireEvent.click(childrenMealsButtons[0]);
    await waitFor(() => {
      expect(mockSetSelectedMeals).toBeCalled();
    });
  });
  it('should replace only conflicting upsellType and keep valid child meals', () => {
    const breakfastMeal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastOption: false,
    };

    const dinnerMeal: MealItemExtension = {
      id: 'DR1',
      upsellType: 'dinner',
      freeBreakfastOption: false,
    };

    const newBreakfastMeal: MealItemExtension = {
      id: 'BF2',
      upsellType: 'breakfast',
      freeBreakfastOption: true,
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        adults: ['BF1', 'DR1'],
        children: ['KID1', 'KID2', 'KID3'],
        reservationId: 'R1',
      },
    ];

    const adultsMeals: MealItemExtension[] = [breakfastMeal, dinnerMeal, newBreakfastMeal];
    const adultsPerRoom = [2];

    const childrenMeals: MealItemExtension[] = [
      {
        id: 'KID1',
        upsellType: 'BREAKFAST',
      },
      {
        id: 'KID2',
        upsellType: 'DINNER',
      },
      {
        id: 'KID3',
        upsellType: 'BREAKFAST,DINNER', // Combo meal
      },
    ];

    const mockSetSelectedMeals = jest.fn();

    handleOnAddAdultMealsSelections(
      newBreakfastMeal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      mockSetSelectedMeals
    );

    expect(mockSetSelectedMeals).toHaveBeenCalledWith([
      {
        // BF1 replaced by BF2; DR1 retained
        adults: ['DR1', 'BF2', 'BF2'],
        // children: KID1 and KID3 (have BREAKFAST, still valid); KID2 (DINNER-only) removed
        children: expect.arrayContaining(['KID1', 'KID3']),
        reservationId: 'R1',
      },
    ]);

    expect(mockSetSelectedMeals.mock.calls[0][0][0].children).not.toContain([
      'KID1',
      'KID2',
      'KID3',
    ]);
  });
  it('adds child meals mapped from multiple adult upsellTypes', () => {
    const meal: MealItemExtension = {
      id: 'MD1',
      upsellType: 'breakfast,dinner',
      freeBreakfastOption: true,
    };

    const adultsMeals: any[] = [];
    const childrenMeals = [
      { id: 'CB1', upsellType: 'BFCHDF' }, // mapped from breakfast
      { id: 'CD1', upsellType: 'DBCHDF' }, // mapped from dinner
    ];

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: [],
        children: [],
      },
    ];

    const adultsPerRoom = [1];

    const setSelectedMeals = jest.fn();

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['MD1'],
        children: [],
      },
    ]);
  });
  it('retains non-conflicting child meals', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
    };

    const childrenMeals = [
      { id: 'KID-keep', upsellType: 'DINNER' }, // not conflicting
      { id: 'KID-remove', upsellType: 'BFCHDF' }, // conflicting
    ];

    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: [],
        children: ['KID-keep', 'KID-remove'],
      },
    ];

    const adultsPerRoom = [1];
    const setSelectedMeals = jest.fn();

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      [],
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['BF1'],
        children: ['KID-keep', 'KID-remove'], // re-added after clean-up
      },
    ]);
  });
  it('does not add child meals if no mapping is found for upsellType', () => {
    const meal: MealItemExtension = {
      id: 'LUNCH1',
      upsellType: 'lunch', // not in FREE_FOOD_OPTIONS
    };

    const childrenMeals = [{ id: 'X1', upsellType: 'XYZ' }];

    const selectedMeals = [
      {
        reservationId: 'R2',
        adults: [],
        children: [],
      },
    ];

    const adultsPerRoom = [1];
    const setSelectedMeals = jest.fn();

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      [],
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R2',
        adults: ['LUNCH1'],
        children: [],
      },
    ]);
  });
  it('adds correct number of child meals based on existing children count when match is found', () => {
    const meal: MealItemExtension = {
      id: 'BFADBF',
      upsellType: 'breakfast',
      freeBreakfastOption: true,
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: [],
        children: ['existing1', 'existing2'], // count = 2
      },
    ];

    const childrenMeals: MealItemExtension[] = [
      {
        id: 'BFCHDF',
        upsellType: 'BFCHDF', // matches FREE_FOOD_OPTIONS.breakfast
      },
    ];

    const adultsPerRoom = [1];
    const setSelectedMeals = jest.fn();

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      [],
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    // Should add 2 instances of BFCHDF (based on 2 existing children)
    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['BFADBF'],
        children: ['existing1', 'existing2'],
      },
    ]);
  });
});

describe('isAdultMealForEntireStaySelections', () => {
  const BREAKFAST_MEAL = {
    id: 'BF1',
    upsellType: 'breakfast',
  };
  const DINNER_MEAL = {
    id: 'DIN1',
    upsellType: 'dinner',
  };
  const COMBO_MEAL = {
    id: 'MDP1',
    upsellType: 'mealdeal|breakfast',
  };

  const adultsMeals = [BREAKFAST_MEAL, DINNER_MEAL, COMBO_MEAL];

  it('returns true when meal ID is selected and upsellType matches', () => {
    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: ['BF1'],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('BF1', adultsMeals, selectedMeals)).toBe(true);
  });

  it('returns false if meal is not selected in any room', () => {
    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: ['DIN1'],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('BF1', adultsMeals, selectedMeals)).toBe(false);
  });

  it('returns false if mealId is not found in adultsMeals list', () => {
    const selectedMeals = [
      {
        reservationId: 'R2',
        adults: ['BF1'],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('INVALID_ID', adultsMeals, selectedMeals)).toBe(
      false
    );
  });

  it('returns false if selectedMeals is empty', () => {
    expect(isAdultMealForEntireStaySelections('BF1', adultsMeals, [])).toBe(false);
  });

  it('returns false if adultsMeals is empty', () => {
    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: ['BF1'],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('BF1', [], selectedMeals)).toBe(false);
  });

  it('returns true for combo meal with matching ID and all upsellTypes present', () => {
    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: ['MDP1'],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('MDP1', adultsMeals, selectedMeals)).toBe(true);
  });

  it('returns false if combo meal is not selected by ID (even if types match)', () => {
    const selectedMeals = [
      {
        reservationId: 'R1',
        adults: ['BF1', 'DIN1'],
        children: [],
      },
    ];

    // MDP1 not selected directly by ID
    expect(isAdultMealForEntireStaySelections('MDP1', adultsMeals, selectedMeals)).toBe(false);
  });

  it('returns false if room has no adults array (undefined)', () => {
    const selectedMeals = [
      {
        reservationId: 'R3',
        adults: undefined,
        children: [],
      } as any,
    ];

    expect(isAdultMealForEntireStaySelections('BF1', adultsMeals, selectedMeals)).toBe(false);
  });

  it('returns false if room has empty adults array', () => {
    const selectedMeals = [
      {
        reservationId: 'R4',
        adults: [],
        children: [],
      },
    ];

    expect(isAdultMealForEntireStaySelections('BF1', adultsMeals, selectedMeals)).toBe(false);
  });
});

describe('handleRemoveAdultMealsSelections', () => {
  const setSelectedMeals = jest.fn();

  beforeEach(() => {
    setSelectedMeals.mockClear();
  });

  it('removes the adult meal ID from all rooms', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1', 'DIN1'], children: [] },
      { reservationId: 'R2', adults: ['BF1'], children: [] },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: ['DIN1'], children: [] },
      { reservationId: 'R2', adults: [], children: [] },
    ]);
  });

  it('removes child meals matching freeBreakfastCode from all rooms', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastCode: 'BFCHDF',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1'], children: ['BFCHDF', 'CH1'] },
      { reservationId: 'R2', adults: [], children: ['BFCHDF'] },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: [], children: ['CH1'] },
      { reservationId: 'R2', adults: [], children: [] },
    ]);
  });

  it('does nothing if meal ID is not in selected meals', () => {
    const meal: MealItemExtension = {
      id: 'BF2',
      upsellType: 'breakfast',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1'], children: [] },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: ['BF1'], children: [] },
    ]);
  });

  it('handles multiple freeBreakfastCodes (array)', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastCode: 'BFCHDF',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1'], children: ['BFCHX', 'CH2'] },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: [], children: ['BFCHX', 'CH2'] },
    ]);
  });

  it('does nothing if children list is empty or undefined', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
      freeBreakfastCode: 'BFCHDF',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1'], children: [] },
      { reservationId: 'R2', adults: ['BF1'], children: undefined as any },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: [], children: [] },
      { reservationId: 'R2', adults: [], children: [] },
    ]);
  });

  it('does nothing if freeBreakfastCode is missing', () => {
    const meal: MealItemExtension = {
      id: 'BF1',
      upsellType: 'breakfast',
    };

    const selectedMeals: SelectedMealsPerRoom[] = [
      { reservationId: 'R1', adults: ['BF1'], children: ['CH1'] },
    ];

    handleRemoveAdultMealsSelections(meal, selectedMeals, setSelectedMeals);

    expect(setSelectedMeals).toHaveBeenCalledWith([
      { reservationId: 'R1', adults: [], children: ['CH1'] },
    ]);
  });
});

describe('handleOnAddAdultMealsSelections', () => {
  const setSelectedMeals = jest.fn();

  beforeEach(() => {
    jest.clearAllMocks();
  });

  it('adds new adult meal and removes conflicting ones', () => {
    const meal: MealItemExtension = { id: 'BF1', upsellType: 'breakfast' };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: ['DIN1'],
        children: [],
      },
    ];

    const adultsMeals: MealItemExtension[] = [
      { id: 'DIN1', upsellType: 'dinner' },
      { id: 'BF1', upsellType: 'breakfast' },
    ];

    const childrenMeals: MealItemExtension[] = [{ id: 'BFCHDF', upsellType: 'breakfast' }];

    const adultsPerRoom = [2];

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['DIN1', 'BF1', 'BF1'],
        children: [],
      },
    ]);
  });

  it('removes conflicting children meals and adds mapped ones', () => {
    const meal: MealItemExtension = { id: 'DIN1', upsellType: 'dinner' };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: [],
        children: ['BFCHDF'],
      },
    ];

    const adultsMeals: MealItemExtension[] = [{ id: 'DIN1', upsellType: 'dinner' }];

    const childrenMeals: MealItemExtension[] = [
      { id: 'BFCHDF', upsellType: 'breakfast' },
      { id: 'DINCHDF', upsellType: 'dinner' },
    ];

    const adultsPerRoom = [1];

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['DIN1'],
        children: ['BFCHDF', 'DINCHDF'], // fix: expect mapped child meal to be added
      },
    ]);
  });

  it('handles upsellType as array and mixes types', () => {
    const meal: MealItemExtension = { id: 'MD1', upsellType: 'mealdeal,dinner' };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: ['BF1'],
        children: ['BFCHDF'],
      },
    ];

    const adultsMeals: MealItemExtension[] = [
      { id: 'BF1', upsellType: 'breakfast' },
      { id: 'MD1', upsellType: 'mealdeal,dinner' },
    ];

    const childrenMeals: MealItemExtension[] = [
      { id: 'BFCHDF', upsellType: 'breakfast' },
      { id: 'MDCHDF', upsellType: 'mealdeal,dinner' },
    ];

    const adultsPerRoom = [1];

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['BF1', 'MD1'], // both retained, no conflict
        children: ['BFCHDF', 'MDCHDF'], // both added
      },
    ]);
  });

  it('preserves non-conflicting children meals', () => {
    const meal: MealItemExtension = { id: 'BF1', upsellType: 'breakfast' };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: [],
        children: ['NON_CONFLICT'],
      },
    ];

    const adultsMeals: MealItemExtension[] = [{ id: 'BF1', upsellType: 'breakfast' }];
    const childrenMeals: MealItemExtension[] = [
      { id: 'BFCHDF', upsellType: 'breakfast' },
      { id: 'NON_CONFLICT', upsellType: 'snack' },
    ];
    const adultsPerRoom = [1];

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['BF1'],
        children: ['NON_CONFLICT', 'BFCHDF'],
      },
    ]);
  });

  it('handles empty children and adults arrays', () => {
    const meal: MealItemExtension = { id: 'BF1', upsellType: 'breakfast' };

    const selectedMeals: SelectedMealsPerRoom[] = [
      {
        reservationId: 'R1',
        adults: [],
        children: [],
      },
    ];

    const adultsMeals: MealItemExtension[] = [{ id: 'BF1', upsellType: 'breakfast' }];
    const childrenMeals: MealItemExtension[] = [{ id: 'BFCHDF', upsellType: 'breakfast' }];
    const adultsPerRoom = [2];

    handleOnAddAdultMealsSelections(
      meal,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    expect(setSelectedMeals).toHaveBeenCalledWith([
      {
        reservationId: 'R1',
        adults: ['BF1', 'BF1'],
        children: [],
      },
    ]);
  });
});

describe('handleOnAddAdultMealsSelections', () => {
  const setSelectedMeals = jest.fn();

  const adultsMeals = [
    { id: 'A1', upsellType: 'breakfast' },
    { id: 'A2', upsellType: 'dinner' },
  ] as any;

  const childrenMeals = [
    { id: 'C1', upsellType: 'breakfast_child' },
    { id: 'C2', upsellType: 'dinner_child' },
  ] as any;

  const selectedMeals = [{ reservationId: 'R1', adults: ['A2'], children: ['C2'] }];

  const adultsPerRoom = [2];

  it('should replace conflicting adult meal types', () => {
    handleOnAddAdultMealsSelections(
      { id: 'A1', upsellType: 'breakfast' } as any,
      selectedMeals,
      adultsMeals,
      adultsPerRoom,
      childrenMeals,
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].adults).toEqual(['A2', 'A1', 'A1']);
    // A2 stays (different type), A1 added twice
  });

  it('should add mapped children meals correctly', () => {
    handleOnAddAdultMealsSelections(
      { id: 'A1', upsellType: 'breakfast' } as any,
      [{ reservationId: 'R1', adults: [], children: ['C2'] }],
      adultsMeals,
      [1],
      childrenMeals,
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].children.length).toBeGreaterThanOrEqual(1);
  });
});

describe('handleOnAddChildrenMealsSelections', () => {
  it('should add children meals if not already present', () => {
    const setSelectedMeals = jest.fn();

    handleOnAddChildrenMealsSelections(
      { id: 'C1' } as any,
      [{ reservationId: 'R1', adults: [], children: [] }],
      [2],
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].children).toEqual(['C1', 'C1']);
  });

  it('should not duplicate if already selected', () => {
    const setSelectedMeals = jest.fn();

    handleOnAddChildrenMealsSelections(
      { id: 'C1' } as any,
      [{ reservationId: 'R1', adults: [], children: ['C1'] }],
      [2],
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].children).toEqual(['C1']);
  });
});

describe('handleRemoveAdultMealsSelections', () => {
  it('should remove only selected adult meal and mapped child meals', () => {
    const setSelectedMeals = jest.fn();

    handleRemoveAdultMealsSelections(
      { id: 'A1', freeBreakfastCode: 'C1' } as any,
      [
        {
          reservationId: 'R1',
          adults: ['A1', 'A2'],
          children: ['C1', 'C2'],
        },
      ],
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].adults).toEqual(['A2']);
    expect(result[0].children).toEqual(['C2']);
  });

  it('should render MealSelectionCount when isAdultHasMealsFree is true', async () => {
    render(
      <MealSelection
        isAdultHasMealsFree={true}
        logoRestaurantUrl=""
        adults={2}
        kids={0}
        nights={1}
        adultsMeals={[
          {
            id: 'BREAKFAST',
            name: 'Breakfast',
            upsellType: 'BREAKFAST',
            price: 10,
            currency: 'GBP',
          } as any,
        ]}
        childrenMeals={[]}
        selectedRoom={0}
        selectedMeals={[{ adults: ['BREAKFAST', 'BREAKFAST'], children: [], reservationId: '1' }]}
        setSelectedMeals={jest.fn()}
        showFreeFoodKids={false}
        isForEntireStay
        headingTitle="Meals"
        adultsPerRoom={[2]}
        kidsPerRoom={[0]}
      />
    );

    expect(screen.getByText('2')).toBeInTheDocument();
  });
});

describe('handleRemoveChildrenMealsSelections', () => {
  it('should remove only matching child meal id', () => {
    const setSelectedMeals = jest.fn();

    handleRemoveChildrenMealsSelections(
      { id: 'C1' } as any,
      [{ reservationId: 'R1', adults: [], children: ['C1', 'C2'] }],
      setSelectedMeals
    );

    const result = setSelectedMeals.mock.calls[0][0];

    expect(result[0].children).toEqual(['C2']);
  });
});
