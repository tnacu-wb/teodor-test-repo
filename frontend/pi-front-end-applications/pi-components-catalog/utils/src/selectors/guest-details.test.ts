import { boldMatchCharacters, generateSuggestionList, isTheSameGuest } from './guest-details';

const propsGenerateSuggestion = {
  data: [
    {
      id: '2',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'test',
      emailAddress: 'booker.cgl@mailinator.com',
      textConfirmation: false,
      accessLevel: 'BOOKER',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80686539',
      lockedForEditing: false,
    },
    {
      id: '3',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'test',
      emailAddress: 'booker.cgl1@mailinator.com',
      textConfirmation: false,
      accessLevel: 'BOOKER',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80686542',
      lockedForEditing: false,
    },
    {
      id: '4',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'test',
      emailAddress: 'selfbooker.cgl@mailinator.com',
      textConfirmation: false,
      accessLevel: 'SELF',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80686543',
      lockedForEditing: false,
    },
    {
      id: '5',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'test',
      emailAddress: 'guest.cgl@mailinator.com',
      textConfirmation: false,
      accessLevel: 'STAYER',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80686551',
      lockedForEditing: false,
    },
    {
      id: '7',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'test',
      emailAddress: 'pod456@mailinator.com',
      textConfirmation: false,
      accessLevel: 'SELF',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80804307',
      lockedForEditing: false,
    },
    {
      id: '8',
      title: 'Ms',
      firstName: 'Ella',
      lastName: 'Test',
      emailAddress: 'pod789@mailinator.com',
      textConfirmation: false,
      accessLevel: 'SELF',
      employeeStatus: 'INACTIVE',
      guestHistoryNumber: 'G80804308',
      lockedForEditing: false,
    },
    {
      id: '21',
      title: 'Mrs',
      firstName: 'Ella',
      lastName: 'Test',
      emailAddress: 'pod1234@mailinator.com',
      textConfirmation: false,
      accessLevel: 'BOOKER',
      employeeStatus: 'DEACTIVATED',
      guestHistoryNumber: 'G80834353',
      lockedForEditing: false,
    },
    {
      id: '6',
      title: 'Miss',
      firstName: 'ella',
      lastName: 'test  bla',
      emailAddress: 'pod123@mailinator.com',
      textConfirmation: false,
      accessLevel: 'SELF',
      employeeStatus: 'ACTIVE',
      guestHistoryNumber: 'G80804295',
      lockedForEditing: false,
    },
  ],
  searchCriteria: 'test',
  listSelections: [
    {
      title: 'Ms',
      lastName: 'test',
      id: '2',
      firstName: 'Ella',
      emailAddress: 'booker.cgl@mailinator.com',
      composedName: 'Ms Ella test (booker.cgl@mailinator.com)',
    },
    {
      emailAddress: '',
      firstName: '',
      id: '',
      lastName: '',
      title: '',
      composedName: '',
    },
    {
      emailAddress: '',
      firstName: '',
      id: '',
      lastName: '',
      title: '',
      composedName: '',
    },
  ],
};

const expectGenerateList = [
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '3',
      firstName: 'Ella',
      emailAddress: 'booker.cgl1@mailinator.com',
      composedName: 'Ms Ella test (booker.cgl1@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella <strong>test</strong> (booker.cgl1@mailinator.com)',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '4',
      firstName: 'Ella',
      emailAddress: 'selfbooker.cgl@mailinator.com',
      composedName: 'Ms Ella test (selfbooker.cgl@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella <strong>test</strong> (selfbooker.cgl@mailinator.com)',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '5',
      firstName: 'Ella',
      emailAddress: 'guest.cgl@mailinator.com',
      composedName: 'Ms Ella test (guest.cgl@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella <strong>test</strong> (guest.cgl@mailinator.com)',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'test',
      id: '7',
      firstName: 'Ella',
      emailAddress: 'pod456@mailinator.com',
      composedName: 'Ms Ella test (pod456@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella <strong>test</strong> (pod456@mailinator.com)',
  },
  {
    employee: {
      title: 'Ms',
      lastName: 'Test',
      id: '8',
      firstName: 'Ella',
      emailAddress: 'pod789@mailinator.com',
      composedName: 'Ms Ella Test (pod789@mailinator.com)',
    },
    prettyFormatDisplay: 'Ms Ella <strong>Test</strong> (pod789@mailinator.com)',
  },
  {
    employee: {
      title: 'Mrs',
      lastName: 'Test',
      id: '21',
      firstName: 'Ella',
      emailAddress: 'pod1234@mailinator.com',
      composedName: 'Mrs Ella Test (pod1234@mailinator.com)',
    },
    prettyFormatDisplay: 'Mrs Ella <strong>Test</strong> (pod1234@mailinator.com)',
  },
  {
    employee: {
      title: 'Miss',
      lastName: 'test  bla',
      id: '6',
      firstName: 'ella',
      emailAddress: 'pod123@mailinator.com',
      composedName: 'Miss ella test  bla (pod123@mailinator.com)',
    },
    prettyFormatDisplay: 'Miss ella <strong>test</strong>  bla (pod123@mailinator.com)',
  },
];

describe('generateSuggestionList', () => {
  it('should render a list of suggestions without already selected items', () => {
    expect(
      generateSuggestionList(
        propsGenerateSuggestion.data,
        propsGenerateSuggestion.searchCriteria,
        propsGenerateSuggestion.listSelections
      )
    ).toEqual(expectGenerateList);
  });

  it('should render a list of suggestions ', () => {
    const fullListSuggestions = [
      {
        employee: {
          title: 'Ms',
          lastName: 'test',
          id: '2',
          firstName: 'Ella',
          emailAddress: 'booker.cgl@mailinator.com',
          composedName: 'Ms Ella test (booker.cgl@mailinator.com)',
        },
        prettyFormatDisplay: 'Ms Ella <strong>test</strong> (booker.cgl@mailinator.com)',
      },
      ...expectGenerateList,
    ];

    expect(
      generateSuggestionList(
        propsGenerateSuggestion.data,
        propsGenerateSuggestion.searchCriteria,
        []
      )
    ).toEqual(fullListSuggestions);
  });
});

describe('boldMatchCharacters', () => {
  it('should bold the the characters from sentence', () => {
    expect(
      boldMatchCharacters({
        sentence: 'Ms Ella test (booker.cgl1@mailinator.com)',
        characters: 'test',
      })
    ).toEqual('Ms Ella <strong>test</strong> (booker.cgl1@mailinator.com)');
  });
  it('should return empty strong tag if parameters are undefined', () => {
    expect(boldMatchCharacters({})).toEqual('<strong></strong>');
  });
});

describe('isTheSameGuest selector', () => {
  const guest1 = {
    id: '1',
    title: 'Mr',
    firstName: 'guestFName',
    lastName: 'guestLName',
    emailAddress: 'guestName@mail.com',
    composedName: 'guestFName (guestFName@mail.com)',
  };
  const guest2 = { ...guest1 };
  it('should return false if the two users are different', () => {
    const differentGuest = { ...guest1, emailAddress: 'guestName2@mail.com' };
    expect(isTheSameGuest(guest1, differentGuest)).toEqual(false);
  });
  it('should return false if the two users are different', () => {
    expect(isTheSameGuest(guest1, guest2)).toEqual(true);
  });
});
