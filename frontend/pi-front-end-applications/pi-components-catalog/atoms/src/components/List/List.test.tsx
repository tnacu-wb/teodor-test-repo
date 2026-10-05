import { CheckIcon } from '@chakra-ui/icons';
import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import ListComponent from './List.component';

const list = [
  'Luggage facilities',
  'Air conditioning',
  '40 inch Smart TV',
  'Family rooms',
  'Premier Plus rooms',
  'Costa Coffee',
  'Lift access',
  'Accessible',
  'Free Wi-Fi',
];

describe('List', () => {
  it('renders the List Component', () => {
    const { getByText } = render(<ListComponent>{[list[0]]}</ListComponent>);
    expect(getByText(list[0])).toBeInTheDocument();
  });
  it('has the exact list items', () => {
    const { getByText } = render(<ListComponent>{list}</ListComponent>);
    list.forEach((item) => {
      expect(getByText(item)).toBeInTheDocument();
    });
  });
  it('has chakra-ui icon', () => {
    const { container } = render(
      <ListComponent icon={<CheckIcon w={4} h={4} />}>{list}</ListComponent>
    );
    expect(container.querySelector('.chakra-icon')).toBeInTheDocument();
  });
});
