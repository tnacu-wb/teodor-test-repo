import { ContactDetail } from './contact-detail';

export class CreateAccountRequest {
  country?: string;
  language?: string;
  captcha?: string;
  password: string;
  contactDetail?: ContactDetail;
  basketReference?: string;

  constructor(data: any) {
    this.country = data.country;
    this.language = data.language;
    this.captcha = data.captcha;
    this.password = data.password;
    this.contactDetail = new ContactDetail(data.contactDetail);
    this.basketReference = data.basketReference;
  }
}
