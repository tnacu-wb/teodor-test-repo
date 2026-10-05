export class SourceDetails {
  channel: string;
  journey?: string;
  locale?: string;

  constructor(data: any) {
    this.channel = data.channel;
    this.journey = data.journey;
    this.locale = data.locale;
  }
}
