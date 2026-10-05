import { GuestAddress } from './guest-address';

export class ContactDetail {
  title?: string;
  firstName: string;
  lastName: string;
  emailAddress?: string;
  mobile?: string;
  address: GuestAddress;

  constructor(data: any) {
    this.title = data.title;
    this.firstName = data.firstName;
    this.lastName = data.lastName;
    this.emailAddress = data.emailAddress;
    this.mobile = data.mobile;
    this.address = new GuestAddress(data.address);
  }
}
