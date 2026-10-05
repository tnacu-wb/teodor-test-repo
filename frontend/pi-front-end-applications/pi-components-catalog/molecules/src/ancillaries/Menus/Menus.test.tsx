import '@testing-library/jest-dom';
import type { Menu } from '@whitbread-eos/api';

import { render } from '../../utils/test-utils';
import Menus from './Menus.component';

const menus: Array<Menu> = [
  { name: 'Breakfast menu', menuSrc: 'https://www.google.com' },
  { name: 'Dinner menu', menuSrc: 'https://www.google.com' },
  { name: 'Kids menu', menuSrc: 'https://www.google.com' },
];

describe('Menus', () => {
  it('should render the <Menus /> with 3 menus', () => {
    const { getAllByTestId, getByTestId } = render(<Menus availableMenus={menus} />);
    expect(getByTestId('Menus-Wrapper')).toBeInTheDocument();
    expect(getByTestId('Menus-Heading-Title')).toBeInTheDocument();
    expect(getAllByTestId('Menus-Item').length).toBe(3);
  });
});
