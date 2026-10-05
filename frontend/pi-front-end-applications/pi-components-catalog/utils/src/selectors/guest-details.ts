import { GuestDetails, ManualGuest, Suggestion } from '@whitbread-eos/api';

import { isStringValid } from '../validators';

export function generateSuggestionList(
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  data: any,
  searchCriteria: string,
  listSelections: GuestDetails[],
  excludeAlreadySelected = true,
  desiredKeys = ['id', 'title', 'firstName', 'lastName', 'emailAddress']
): Suggestion[] {
  const excludedIds = listSelections.map((selection: GuestDetails) => selection.id);

  const finalList: Suggestion[] = [];
  data.forEach((employee: GuestDetails) => {
    if (excludeAlreadySelected && !excludedIds.includes(employee.id)) {
      const text = `${employee.title} ${employee.firstName} ${employee.lastName} (${employee.emailAddress})`;

      const final: Suggestion = {
        employee: {
          title: '',
          lastName: '',
          id: '',
          firstName: '',
          emailAddress: '',
          composedName: text,
        },
        prettyFormatDisplay: boldMatchCharacters({ sentence: text, characters: searchCriteria }),
      };

      desiredKeys.forEach((key: string) => {
        if (
          Object.keys(employee).includes(key) &&
          isStringValid(employee[key as keyof GuestDetails])
        ) {
          final.employee[key as keyof GuestDetails] = employee[key as keyof GuestDetails];
        }
      });

      finalList.push(final);
    }
  });

  return finalList;
}

export const boldMatchCharacters = ({ sentence = '', characters = '' }) => {
  const regEx = new RegExp(characters, 'gi');

  return sentence.replace(regEx, '<strong>$&</strong>');
};

export function isTheSameGuest(actualGuest: GuestDetails, prevGuest: ManualGuest) {
  const { title, firstName, lastName, emailAddress } = prevGuest;
  const {
    title: newTitle,
    firstName: newFirstName,
    lastName: newLastName,
    emailAddress: newEmailAddress,
  } = actualGuest;

  return (
    title === newTitle &&
    firstName === newFirstName &&
    lastName === newLastName &&
    emailAddress === newEmailAddress
  );
}
