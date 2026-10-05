/**
 * response example:
{
  "data": {
    "footer": {
      "copyrightInfo": "&copy; 2022 Premier Inn",
      "socialMediaIcons": [
        {
          "linkSrc": "https://www.facebook.com/premierinn",
          "iconSrc": "/content/dam/icons/resources/social/facebook-dark-square-small.svg",
          "label": "Facebook icon"
        }
      ],
      "tabs": [
        {
          "intro": {
            "name": "",
            "description": "<p>Is it our Hypnos beds, our seriously tasty food, our great value hotels or our amazing teams that guests love so much? We reckon it’s a bit of everything. Take a look around to find out why we’re a much-loved, award-winning hotel chain, and rest easy knowing our range of rates give you both choice and flexibility.</p>\r\n"
          },
          "columns": [
            {
              "name": "Get in touch",
              "linkItems": [
                {
                  "linkSrc": "http://www.dev.premierinn.digital/gb/en/contact-us.html",
                  "openInNewTab": false,
                  "name": "Contact us"
                },
                {
                  "linkSrc": "http://www.dev.premierinn.digital/gb/en/faq.html",
                  "openInNewTab": false,
                  "name": "FAQs"
                },
                {
                  "linkSrc": "http://www.dev.premierinn.digital/gb/en/why/groups.html",
                  "openInNewTab": false,
                  "name": "Group bookings"
                },
                {
                  "linkSrc": "http://www.dev.premierinn.digital/gb/en/premier-inn-affiliate-programme.html",
                  "openInNewTab": false,
                  "name": "Affiliates"
                },
                {
                  "linkSrc": "http://www.dev.premierinn.digital/gb/en/business/international-development.html",
                  "openInNewTab": false,
                  "name": "International development "
                },
                {
                  "linkSrc": "https://www.whitbreadcareers.com/our-brands/premier-inn/",
                  "openInNewTab": true,
                  "name": "Careers"
                }
              ]
            }
          ],
          "name": "About us"
        }
      ]
    }
  }
}
 */
export class FooterInformation {
  [key: string]: unknown;
  copyrightInfo?: string;
  socialMediaIcons?: Array<{ linkSrc?: string; iconSrc?: string; label?: string }>;
  tabs?: Array<Record<string, unknown>>;

  /**
   * FooterInformation constructor
   * @param data object data
   * @param data.footer footer data
   */
  constructor(data: { footer?: Record<string, unknown> } = {}) {
    const footer = data.footer ?? {};
    this.copyrightInfo = footer.copyrightInfo as string | undefined;
    this.socialMediaIcons = footer.socialMediaIcons as FooterInformation['socialMediaIcons'];
    this.tabs = footer.tabs as Array<Record<string, unknown>> | undefined;
  }

  static fromResponse(data: { footer?: Record<string, unknown> }): FooterInformation {
    return new FooterInformation(data);
  }
}
