import '@testing-library/jest-dom';
import { TripAdvisorReviews } from '@whitbread-eos/api';
import { useFeatureToggle, useStaticHotelInformation } from '@whitbread-eos/utils';

import { render, userEvent } from '../../utils/test-utils';
import { TripAdvisorReview } from './TripAdvisorReview';

const tripAdvisorReviews: TripAdvisorReviews = {
  awards: [
    {
      awardType: 'Travelers Choice',
      year: '2024',
      image: 'https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2024_L.png',
    },
  ],
  rating: 4.5,
  subRatings: [
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 4.5,
      localisedName: 'Location',
    },
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 4.5,
      localisedName: 'Sleep Quality',
    },
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 3.5,
      localisedName: 'Rooms',
    },
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 2.5,
      localisedName: 'Service',
    },
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 4.5,
      localisedName: 'Value',
    },
    {
      ratingImageUrl: 'https://static.tacdn.com/img2/ratings/traveler/ss4.5.svg',
      value: 1.5,
      localisedName: 'Cleanliness',
    },
  ],
  name: 'Premier Inn Northampton Town Centre Hotel',
  address: 'Swan Street, Northampton NN1 1FA England',
  reviews: [
    {
      publishedDate: '2024-01-22T00:38:07-0500',
      rating: 5,
      tripType: 'Family',
      title: 'Perfect location for the town centre.',
      text: 'Nice and clean hotel. Good customer service. Lovely food. Reasonably priced. Good location for town centre amenities. Wheelchair users take care as the pavement and ramp to the hotel by is a bit challenging.',
      user: {
        username: 'janinas759',
        location: undefined,
      },
    },
    {
      publishedDate: '2024-01-15T21:56:41-0500',
      rating: 2,
      tripType: 'Business',
      title: 'Avoid the breakfast',
      text: 'Negative: Avoid the breakfast\n\nI witnessed a grown adult taking the bread slices with his bare hands, looking at it and then placing it back in the bread basket to then repeatedly do this again.  Hands carry germs and regardless whether he left his hotel room or came in from his house, he still touched things along the way  before entering the restaurant and this is exactly how people get sick.  The tongs were visible, there was more than one tong that he could have used.  \n\nWas very shocked to see an adult do this and even worse there was only one waitress in the Thyme restaurant so there was nobody to monitor the bakery section to ensure hygiene was adhered by.  For this reason alone,  I had to give this hotel a low score. \n\nThe only positive was the room so should I ever stay again I will eat Breakfast elsewhere.',
      user: {
        username: 'Companion34089643261',
        location: 'United Kingdom',
      },
    },
    {
      publishedDate: '2024-01-06T03:10:36-0500',
      rating: 5,
      tripType: 'Couples',
      title: 'Nice clean rooms',
      text: 'We stayed here for the ASE conference at the university, so the accommodation was booked on my behalf. As with all our Premier Inn stays the room was clean and tidy. The bed was comfortable and the breakfast spot on \nThe multi storey car park opposite had plenty of spaces (and our VW camper van with pop-too fits!) and really cheap. We pulled in around 5pm and left at about half ten the next day and it was £2.20! \nWould definitely use this Premier Inn again if we came back to Northampton.',
      user: {
        username: 'catherinetwinmum',
        location: 'Castleford, United Kingdom',
      },
    },
    {
      publishedDate: '2023-12-31T00:45:10-0500',
      rating: 4,
      tripType: 'Family',
      title: 'Staff amazing, air con not so much',
      text: 'Considering it’s winter, the air con leaves a lot to be desired and we struggled to sleep each night we stayed unfortunately. The night staff provided a fan but that just moved the warm air around. The lovely manager moved us to another room which was marginally better but we still needed a fan on all night and still didn’t sleep as well as we usually do.',
      user: {
        username: 'Andrea G',
        location: 'Darlington, United Kingdom',
      },
    },
    {
      publishedDate: '2023-12-28T01:23:50-0500',
      rating: 3,
      tripType: 'Couples',
      title: 'Room like a sauna no AC',
      text: 'Shame I had to only give 3 out of 5. Great team, nice clean room and excellent breakfast. Unfortunately when you’re tired because you couldn’t get a good nights sleep it takes the shine off your stay. ',
      user: {
        username: 'Djh170169',
        location: 'Bedfordshire, United Kingdom',
      },
    },
  ],
  numberOfReviews: 1567,
  writeReview:
    'https://www.tripadvisor.co.uk/UserReview-g186349-d8869727-Premier_Inn_Northampton_Town_Centre_Hotel-Northampton_Northamptonshire_England.html?m=67640',
  webUrl:
    'https://www.tripadvisor.co.uk/Hotel_Review-g186349-d8869727-Reviews-Premier_Inn_Northampton_Town_Centre_Hotel-Northampton_Northamptonshire_England.html?m=67640',
  locationId: '8869727',
  hotelCode: 'NORDER',
};

const mockUseStaticHotelInformation = useStaticHotelInformation as jest.Mock;
const mockUseFeatureToggle = useFeatureToggle as jest.Mock;

const getStaticHotelInformationMock = (
  overrides?: Partial<ReturnType<typeof useStaticHotelInformation>>
) => ({
  tripAdvisorReviews,
  isLoading: false,
  isError: false,
  error: Error,
  brand: 'Premier Inn',
  title: 'Test Hotel Title',
  ...overrides,
});

// temporary solution until next/router will be deprecated
const mockUseRouter = jest.fn();
jest.mock('next/router', () => ({
  ...jest.requireActual('next/router'),
  useRouter: () => mockUseRouter(),
}));

jest.mock('@whitbread-eos/utils', () => ({
  ...jest.requireActual('@whitbread-eos/utils'),
  useFeatureToggle: jest.fn(),
  useStaticHotelInformation: jest.fn(),
}));

jest.mock('next-i18next', () => ({
  ...jest.requireActual('next-i18next'),
  useTranslation: jest.fn().mockReturnValue({
    t: (key: string) => {
      if (key === 'tripadvisor.award.title') {
        return '{award_name} - {award_year}';
      }
      return key;
    },
  }),
}));

describe('TripAdvisorReviews', () => {
  beforeEach(() => {
    mockUseFeatureToggle.mockReturnValue({
      release_pi_bb_ccui_white_trip_advisor_review: true,
    });
    mockUseStaticHotelInformation.mockReturnValue(getStaticHotelInformationMock());
  });

  it('should render a loading message, if isLoading prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({ isLoading: true })
    );

    const { getByText } = render(<TripAdvisorReview />);
    expect(getByText('searchresults.list.hotel.loading')).toBeInTheDocument();
  });

  it('should render error message, if isError prop is true', () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({
        isError: true,
        error: { message: 'Error' },
      })
    );

    const { getByText } = render(<TripAdvisorReview />);
    expect(getByText('Error')).toBeInTheDocument();
  });

  it('Should not render bottom user review section  if tripadvisor prop is empty object', () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({ tripAdvisorReviews: {} as TripAdvisorReviews })
    );

    const { queryByTestId } = render(<TripAdvisorReview isTopSection={false} />);
    expect(queryByTestId('tripadvisor-bottom-review-section-hdp')).not.toBeInTheDocument();
  });

  it('Should  not render the bottom review section in HDP if isTopSection is true', () => {
    const { queryByTestId } = render(<TripAdvisorReview isTopSection />);
    expect(queryByTestId('tripadvisor-user-review-section')).not.toBeInTheDocument();
  });

  it('Should  not render the top review section in HDP if isTopSection is false', () => {
    const { queryByTestId } = render(<TripAdvisorReview isTopSection={false} />);
    expect(queryByTestId('tripadvisor-top-rating-section-hdp')).not.toBeInTheDocument();
  });

  it('Should not render top user review section  if tripadvisor prop is empty object', () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({ tripAdvisorReviews: {} as TripAdvisorReviews })
    );

    const { queryByTestId } = render(<TripAdvisorReview isTopSection={true} />);
    expect(queryByTestId('tripadvisor-top-rating-section-hdp')).not.toBeInTheDocument();
  });

  it('Should open Awards modal when clicking the award icon', async () => {
    const { queryByTestId } = render(<TripAdvisorReview isTopSection={true} />);

    const awardButton = queryByTestId('tripadvisor-top-rating-award-button');
    expect(awardButton).toBeInTheDocument();

    await userEvent.click(awardButton as HTMLElement);

    const awardModalImage = queryByTestId('tripadvisor-modal-award-icon');
    expect(awardModalImage).toBeInTheDocument();
  });

  it('Should open Awards modal when clicking the award icon, and the award is for best choice', async () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({
        tripAdvisorReviews: {
          ...tripAdvisorReviews,
          awards: [
            {
              awardType: 'Best Choice',
              year: '2024',
              image: 'https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2024_L.png',
            },
          ],
        },
      })
    );

    const { queryByTestId } = render(<TripAdvisorReview isTopSection={true} />);

    const awardButton = queryByTestId('tripadvisor-top-rating-award-button');
    expect(awardButton).toBeInTheDocument();

    await userEvent.click(awardButton as HTMLElement);

    const awardModalImage = queryByTestId('tripadvisor-modal-award-icon');
    expect(awardModalImage).toBeInTheDocument();
  });

  it('Should open Awards modal when clicking the award icon, and the award is not defined', async () => {
    mockUseStaticHotelInformation.mockReturnValue(
      getStaticHotelInformationMock({
        tripAdvisorReviews: {
          ...tripAdvisorReviews,
          awards: [
            {
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              awardType: null as any,
              // eslint-disable-next-line @typescript-eslint/no-explicit-any
              year: null as any,
              image: 'https://static.tacdn.com/img2/travelers_choice/widgets/tchotel_2024_L.png',
            },
          ],
        },
      })
    );

    const { queryByTestId } = render(<TripAdvisorReview isTopSection={true} />);

    const awardButton = queryByTestId('tripadvisor-top-rating-award-button');
    expect(awardButton).toBeInTheDocument();

    await userEvent.click(awardButton as HTMLElement);

    const awardModalImage = queryByTestId('tripadvisor-modal-award-icon');
    expect(awardModalImage).toBeInTheDocument();
  });
});
