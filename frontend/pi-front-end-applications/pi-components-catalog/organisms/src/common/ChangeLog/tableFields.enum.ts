// Fields that are shown in change log modal
// For the table to render correctly the table column keys should match the property names in the row object
// Update the enum to ensure integrity of table columns and rows

export enum TABLE_FIELDS {
  DATE = 'date',
  TIME = 'time',
  ACTION_TYPE = 'actionType',
  ACTION_DESCRIPTION = 'actionDescription',
  USER = 'user',
  CHANNEL = 'channel',
}
