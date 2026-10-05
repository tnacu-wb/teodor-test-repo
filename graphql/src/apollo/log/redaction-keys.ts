/**
 * Add the keys to mask in the logs
 * Include the levels of object keys to mask by using wildcard (*)
 */
export const redactionKeys = [
  'password',
  'userId',
  'email',
  'emailAddress',
  '*.*.emailAddress',
  '*.name',
  '*.telephoneNumber',
  '*.*.address.addressLine',
  '*.*.postalCode',
  '*.corpId',
  '*.companyId',
  '*.*.arNumber',
  'surname',
  'contactValue',
  'cardId',
  'companyId',
  'line1',
  'line2',
  'line3',
  'line4',
  'line5',
  'postCode',
  'companyName',
  'cardNumber',
  'cardHolderName',
  'cardToken',
  'billingAddress',
  'cnpBusinessAccountUsername',
  'cnpBusinessAccountPassword',
  '*.displayName',
  '*.forename'
];
