export class LoginCriteria {
  guid: String;
  worldlineSessionId?: String;
  worldlineSharedSecret?: String;
  scheme?: String;

  constructor(data: any) {
    this.guid = data.guid;
    this.worldlineSessionId = data.worldlineSessionId;
    this.worldlineSharedSecret = data.worldlineSharedSecret;
    this.scheme = data.scheme;
  }
}
