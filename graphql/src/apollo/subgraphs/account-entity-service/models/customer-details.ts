export class CustomerDetails {
  title?: string;
  firstName?: string;
  lastName?: string;
  nationality?: string;
  userId?: string;
  countryOfResidence?: string;
  customerId?: string;
  language?: string;

  constructor(data: any) {
    this.title = data.title;
    this.firstName = data.firstName;
    this.lastName = data.lastName;
    this.nationality = data.nationality;
    this.userId = data.userId;
    this.countryOfResidence = data.countryOfResidence;
    this.customerId = data.customerId;
    this.language = data.language;
  }
}
