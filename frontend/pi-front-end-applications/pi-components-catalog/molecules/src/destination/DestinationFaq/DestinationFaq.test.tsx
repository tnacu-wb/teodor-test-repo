import '@testing-library/jest-dom';

import { render } from '../../utils/test-utils';
import DestinationFaq from './DestinationFaq.component';

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  formatAssetsUrl: () =>
    'https://secure2.premierinn.com/content/dam/pi/websites/hotelimages/gb/en/W/WEMPTI/London_Wembley_Stadium002.jpg',
}));

const mockedData = [
  {
    title: 'About Newcastle',
    faqItems: [
      {
        question: 'What is someone from Newcastle called?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'What is the population of Newcastle?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
    ],
  },
  {
    title: 'Visiting Newcastle',
    faqItems: [
      {
        question: 'What is someone from Newcastle called?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'What is the population of Newcastle?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
    ],
  },
  {
    title: 'Premier Inn',
    faqItems: [
      {
        question: 'What is someone from Newcastle called?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'What is the population of Newcastle?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
      {
        question: 'Lorem ipsum dolor sit amet, consectetur adipiscing elit?',
        answer:
          'People from Newcastle are affectionately referred to as Geordies. The term is thought to originate from the name ‘George’, which was a common name among pitmen and miners in the northeast of England in the late 1800s, or could perhaps be linked to the city’s support of English kings George I and II in opposition to the rest of the population of Northumberland in the early 1800s. It’s technically only supposed to refer to people born on the north of the Tyne within a mile of Newcastle, but nowadays tends to be a catch-all for anyone from the area. The area’s accent is made up of unique phrasings and words used by people from the Newcastle area and this is also referred to as Geordie.',
      },
    ],
  },
];

describe('FAQs Section', () => {
  it('should render the FAQs Section with tabs', () => {
    const { getByText, getAllByRole } = render(<DestinationFaq data={mockedData} />);
    expect(getByText('FAQs')).toBeInTheDocument();
    expect(getAllByRole('tab').length).toBe(3);
  });
});
