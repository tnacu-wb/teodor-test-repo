export enum requestStatus {
  success = 'success',
  fail = 'fail',
}

export enum requestErrors {
  generic = 'generic',
  lastTravelManager = '2518', //code used for last Travel Manager Error
  currentPasswordNotMatch = '037', //code used for currentPassword not match
  payAppAccessRestricted = 262, //code used for pay application access restricted
  phoneNumberInvalid = '616', //code used for WL phone number validation failure
}
