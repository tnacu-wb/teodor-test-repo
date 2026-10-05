/**
  TermsAndConditions from API response
 {
     "data":{
         "termsAndConditions":
             {
                 "text": "<p>I have read, understand and accept the&nbsp;<a href=\"/content/pi/websites/desktop/gb/en/unsecured/terms/booking-terms-and-conditions.html\">Terms and Conditions</a>. Cancellations must be made before 1pm on your arrival day.&nbsp;</p>\n"
             }
         ]
     }
 }   
 */
export class TermsAndConditions {
  [key: string]: unknown;
  text?: string;

  /**
   * TermsAndConditions constructor
   * @param data object data
   * @param data.termsAndConditions termsAndConditions
   */
  constructor(data: { termsAndConditions?: { text?: string } } = {}) {
    this.text = data.termsAndConditions?.text;
  }

  static fromResponse(data: { termsAndConditions?: { text?: string } }): TermsAndConditions {
    return new TermsAndConditions(data);
  }
}
