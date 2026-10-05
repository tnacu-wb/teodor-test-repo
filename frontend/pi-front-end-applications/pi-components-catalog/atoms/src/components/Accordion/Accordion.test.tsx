import '@testing-library/jest-dom';
import React from 'react';

import { fireEvent, render } from '../../utils/test-utils';
import Accordion, { AccordionItemProp } from './Accordion.component';

describe('Accordion', () => {
  const accordionItems = [
    {
      title: 'Title 1',
      content: 'Content 1',
    },
    {
      title: 'Title 2',
      content: 'Content 2',
    },
  ];
  it('render the Accordion Component', () => {
    const { getByText, getByTestId } = render(<Accordion accordionItems={accordionItems} />);
    accordionItems.forEach((item: AccordionItemProp) => {
      const button = getByTestId(`Button-${item?.title?.toString().split(' ').join('_')}`);
      fireEvent.click(button);
      getByText(item.content as string);
      fireEvent.click(button);
      !getByText(item.content as string);
    });
  });
});
