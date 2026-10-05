import { Text } from '@chakra-ui/react';
import '@testing-library/jest-dom';
import React from 'react';

import { semanticTextStyles } from '../../theme/adapters/semanticTypography';
import { fireEvent, render, userEvent, waitFor } from '../../utils/test-utils';
import Tabs from './Tabs.component';

let mockIsSemanticTypographyEnabled = false;
jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useSemanticTypography: () => (legacyTypography: object, semanticTypography: object) =>
    mockIsSemanticTypographyEnabled ? semanticTypography : legacyTypography,
}));

const options = [
  {
    index: 0,
    label: 'Breakfast',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 1,
    label: 'Dinner',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken tikka curry or a
        margherita pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a
        luxury sticky toffee pudding or get stuck into a delicious triple chocolate brownie for
        dessert.
      </Text>
    ),
  },
  {
    index: 2,
    label: 'Meal Deal',
    content: (
      <Text>
        Fancy saving up to 20% with our tempting Meal Deal offer? Enjoy a delicious two course
        dinner plus a selected drink*, then wake up and tuck into our famous unlimited
        all-you-can-eat Premier Inn Breakfast the next day. Plus, up to two under-16s also eat
        breakfast for free when an adult orders a Me al Deal. We’ll confirm at booking if our Meal
        Deal is available at your selected hotel.
      </Text>
    ),
  },
];

const roomOptions = [
  {
    index: 0,
    label: 'Room 1',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 1,
    label: 'Room 2',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 2,
    label: 'Room 3',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 3,
    label: 'Room 4',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 4,
    label: 'Room 5',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 5,
    label: 'Room 6',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 6,
    label: 'Room 7',
    description: 'Double room',
    content: <Text>content</Text>,
  },
  {
    index: 7,
    label: 'Room 8',
    description: 'Double room',
    content: <Text>content</Text>,
  },
];

const optionsWithDescription = [
  {
    index: 0,
    label: 'Breakfast',
    description: 'Some Breakfast Description',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 1,
    label: 'Dinner',
    description: 'Some Dinner Description',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken tikka curry or a
        margherita pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a
        luxury sticky toffee pudding or get stuck into a delicious triple chocolate brownie for
        dessert.
      </Text>
    ),
  },
  {
    index: 2,
    label: 'Meal Deal',
    description: 'Short',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 3,
    label: 'Breakfast',
    description: 'Some Breakfast Description',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 4,
    label: 'Dinner',
    description: 'Some Dinner Description',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken tikka curry or a
        margherita pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a
        luxury sticky toffee pudding or get stuck into a delicious triple chocolate brownie for
        dessert.
      </Text>
    ),
  },
  {
    index: 5,
    label: 'Meal Deal',
    description: 'Short',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 6,
    label: 'Breakfast',
    description: 'Some Breakfast Description',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
  {
    index: 7,
    label: 'Dinner',
    description: 'Some Dinner Description',
    content: (
      <Text>
        Tuck into all your Premier Inn restaurant favourites like chicken tikka curry or a
        margherita pizza, and there’s even a new mac and cheese for the little ones! Then enjoy a
        luxury sticky toffee pudding or get stuck into a delicious triple chocolate brownie for
        dessert.
      </Text>
    ),
  },
  {
    index: 8,
    label: 'Meal Deal',
    description: 'Short',
    content: (
      <Text>
        Tuck into our unlimited breakfast from just £9.50*. And don’t forget, kids eat free when an
        adult orders a full Premier Inn breakfast – what’s not to love! Our great value,
        all-you-can-eat cooked breakfast includes favourites like freshly cooked bacon, fluffy hash
        browns, succulent sausages, eggs as you like them, and more. Our continental breakfast is a
        lighter option, with fruit, cereal and freshly baked pastries – and as always, everything in
        the continental breakfast is included in the Premier Inn Breakfast. We’ll confirm which
        breakfast options will be available at your selected hotel during booking. *Prices may vary.
        At some restaurants our Premier Inn Breakfast is £10.50
      </Text>
    ),
  },
];

const optionsWithoutContent = [
  { index: 0, label: 'Tab1' },
  { index: 1, label: 'Tab2' },
  { index: 2, label: 'Tab3' },
];
const singleContentMock = 'SingleContentMock';

const CCUIOptions = [
  {
    index: 0,
    label: 'Family',
    content: <Text>Family room content</Text>,
    roomTypeInventoryCount: 32,
    roomTypeInventoryRoomTypesWithCount: 'Triple (22) Quad (10)',
  },
];

describe('Tabs', () => {
  afterEach(() => {
    mockIsSemanticTypographyEnabled = false;
  });

  it('render the GroupButton Component', () => {
    const { getByRole } = render(<Tabs index={0} options={options} />);
    expect(getByRole('tablist')).toBeInTheDocument();
  });

  it('has the exact label', () => {
    const { getAllByRole } = render(<Tabs index={0} options={options} />);
    const tab = getAllByRole('tab');

    expect(tab[0]).toHaveTextContent('Breakfast');
    expect(tab[1]).toHaveTextContent('Dinner');
    expect(tab[2]).toHaveTextContent(/meal.../i);
  });

  it('has the exact label with short Mobile Labels ', () => {
    const { getAllByRole } = render(<Tabs index={0} options={options} shortMobileLabels={true} />);
    const tab = getAllByRole('tab');

    expect(tab[0]).toHaveTextContent('Breakfast');
    expect(tab[1]).toHaveTextContent('Dinner');
    expect(tab[2]).toHaveTextContent(/meal.../i);
  });

  it('displays the correct content by selected label', () => {
    const { getByRole } = render(<Tabs index={1} options={options} />);

    const tabpanel = getByRole('tabpanel', { name: /dinner/i });

    expect(tabpanel).toHaveTextContent('Tuck into');
  });

  it('should switch the content if the index has been changed', () => {
    const { getByRole, getByText } = render(<Tabs index={0} options={options} />);
    const tab = getByRole('tab', { name: /meal.../i });
    fireEvent.click(tab);
    const tabpanel = getByText(/fancy/i);

    expect(tabpanel).toBeInTheDocument();
  });

  it('should render description correctly', () => {
    const { getAllByRole } = render(<Tabs index={0} options={optionsWithDescription} />);
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Some Breakfast Description');
    expect(tab[1]).toHaveTextContent('Some Dinner Description');
  });

  it('should not apply semantic styles to the description when the semantic typography toggle is disabled', () => {
    mockIsSemanticTypographyEnabled = false;
    const { getAllByTestId } = render(<Tabs index={0} options={optionsWithDescription} />);
    const [description] = getAllByTestId('Some Breakfast Description-TabButtonDescription');

    expect(description).not.toHaveAttribute('style');
  });

  it('should apply semantic styles to the description when the semantic typography toggle is enabled', () => {
    mockIsSemanticTypographyEnabled = true;
    const { getAllByTestId } = render(<Tabs index={0} options={optionsWithDescription} />);
    const [description] = getAllByTestId('Some Breakfast Description-TabButtonDescription');

    expect(description).toHaveStyle(semanticTextStyles['body-s-regular']);
  });

  it('should render alternative room label (with just room number and description) for tabs in mobile view', () => {
    const { getAllByRole } = render(
      <Tabs
        index={0}
        options={roomOptions}
        isMobileView={true}
        isScrollable={false}
        hasRoomLabels={true}
        showRoomInventory={false}
        shortMobileLabels={true}
      />
    );
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('1Doub...');
    expect(tab[1]).toHaveTextContent('2Doub...');
    expect(tab[7]).toHaveTextContent('8Doub...');
  });

  it('should render alternative room label (with full room label - not truncated) for tabs with room labels and room count 8 and above', () => {
    const { getAllByRole } = render(
      <Tabs
        index={0}
        options={roomOptions}
        isMobileView={false}
        isScrollable={false}
        hasRoomLabels={true}
        showRoomInventory={false}
        shortMobileLabels={true}
      />
    );
    const tab = getAllByRole('tab');
    const tabLabelHeading = getAllByRole('heading', { level: 3 });
    expect(tab[0]).toHaveTextContent('Room 1Doub...');
    expect(tab[7]).toHaveTextContent('Room 8Doub...');
    expect(tabLabelHeading[0]).toHaveStyle('fontSize: 17px');
  });

  it('should render standard room label (with full room label - not truncated) for all tabs with no shortMobileLabels', () => {
    const { getAllByRole } = render(
      <Tabs
        index={0}
        options={roomOptions}
        isMobileView={false}
        isScrollable={false}
        hasRoomLabels={true}
        showRoomInventory={false}
        shortMobileLabels={false}
      />
    );
    const tab = getAllByRole('tab');
    const tabLabelHeading = getAllByRole('heading', { level: 3 });
    expect(tab[0]).toHaveTextContent('Room 1Double room');
    expect(tab[7]).toHaveTextContent('Room 8Double room');
    expect(tabLabelHeading[0]).toHaveStyle('fontSize: 17px');
  });

  it('should render description correctly with short mobile labels', () => {
    const { getAllByRole } = render(
      <Tabs index={0} options={optionsWithDescription} shortMobileLabels={true} />
    );
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Some');
    expect(tab[1]).toHaveTextContent('Some');
    expect(tab[2]).toHaveTextContent('Shor...');
  });

  it('display the correct content when is single content', () => {
    const { getByTestId } = render(
      <Tabs index={0} options={optionsWithoutContent} singleContent={singleContentMock} />
    );
    expect(getByTestId('TabsComponent')).toHaveTextContent(singleContentMock);
  });

  it('should display the correct inventory count for CCUI tabs', () => {
    const { getAllByRole } = render(
      <Tabs index={0} options={CCUIOptions} shortMobileLabels={false} showRoomInventory={true} />
    );
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Family (32)');
  });

  it('should display a tooltip for CCUI tab if inventory information exists', async () => {
    const { getByTestId, queryByTestId } = render(
      <Tabs index={0} options={CCUIOptions} shortMobileLabels={false} showRoomInventory={true} />
    );
    expect(getByTestId('Family-TabButtonWithToolTipLabel')).toBeInTheDocument();
    expect(queryByTestId(/Family-TabButtonLabel/)).toBeNull();
  });

  it('should not display a tooltip for CCUI tab if roomInventory info is zero and empty string', () => {
    CCUIOptions[0].roomTypeInventoryCount = 0;
    CCUIOptions[0].roomTypeInventoryRoomTypesWithCount = '';
    const { getByTestId, queryByTestId } = render(
      <Tabs index={0} options={CCUIOptions} shortMobileLabels={false} showRoomInventory={true} />
    );
    expect(getByTestId('Family-TabButtonLabel')).toHaveTextContent('Family (0)');
    expect(queryByTestId(/Family-TabButtonWithToolTipLabel/)).toBeNull();
  });

  it('should not display the inventory count for non-CCUI tabs', () => {
    const { getAllByRole, getByTestId } = render(
      <Tabs index={0} options={CCUIOptions} shortMobileLabels={false} showRoomInventory={false} />
    );
    const tab = getAllByRole('tab');
    expect(tab[0]).toHaveTextContent('Family');
    expect(tab[0]).not.toHaveTextContent('Family (22)');
    expect(getByTestId('Family-TabButtonLabel')).toBeInTheDocument();
  });

  // AB Test for scrollable tabs arrows - mobile view only
  it('should display scrollable tabs arrows for a/b test - if isScrollable prop is true', () => {
    const { getByTestId } = render(
      <Tabs index={0} options={options} shortMobileLabels={true} isScrollable={true} />
    );
    expect(getByTestId('TabScrollLeft-Button')).toBeInTheDocument();
    expect(getByTestId('TabScrollRight-Button')).toBeInTheDocument();

    expect(getByTestId('TabScrollLeft-Button')).toBeDisabled();
    expect(getByTestId('TabScrollRight-Button')).not.toBeDisabled();
  });

  it('should not display scrollable tabs arrows for a/b test - if isScrollable prop is false', () => {
    const { queryByTestId } = render(
      <Tabs
        index={0}
        options={options}
        shortMobileLabels={true}
        isScrollable={false}
        isMobileView={true}
      />
    );
    expect(queryByTestId('TabScrollLeft-Button')).not.toBeInTheDocument();
    expect(queryByTestId('TabScrollRight-Button')).not.toBeInTheDocument();
  });

  it('disables the left arrow when at the first tab', () => {
    const { getByTestId } = render(
      <Tabs options={options} isScrollable={true} tabScrollSize={1} />
    );
    const leftArrow = getByTestId('TabScrollLeft-Button');
    expect(leftArrow).toBeDisabled();
  });

  it('enables the right arrow when not at the last tab', () => {
    const { getByTestId } = render(
      <Tabs options={options} isScrollable={true} tabScrollSize={1} />
    );
    const rightArrow = getByTestId('TabScrollRight-Button');
    expect(rightArrow).not.toBeDisabled();
  });

  it('disables the right arrow when at the last tab', () => {
    const { getByTestId } = render(
      <Tabs options={options} isScrollable={true} tabScrollSize={1} startingTab={2} />
    );
    const rightArrow = getByTestId('TabScrollRight-Button');
    expect(rightArrow).toBeDisabled();
  });

  it('changes the tab when the left arrow is clicked', async () => {
    const { getByTestId } = render(
      <Tabs options={options} isScrollable={true} tabScrollSize={1} startingTab={1} />
    );
    const tabButton1 = getByTestId('Breakfast-TabButton');
    const leftArrow = getByTestId('TabScrollLeft-Button');

    userEvent.click(leftArrow);
    await waitFor(() => {
      expect(tabButton1).toHaveAttribute('aria-selected', 'true');
    });
  });

  it('changes the tab when the right arrow is clicked', async () => {
    const { getByTestId } = render(
      <Tabs options={options} isScrollable={true} tabScrollSize={1} startingTab={0} />
    );
    const tabButton2 = getByTestId('Dinner-TabButton');
    const rightArrow = getByTestId('TabScrollRight-Button');
    userEvent.click(rightArrow);
    expect(tabButton2).toBeVisible;
  });
});
