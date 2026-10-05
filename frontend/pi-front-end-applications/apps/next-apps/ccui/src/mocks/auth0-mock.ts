export class Auth0Client {
  constructor() {
    console.warn(
      'Auth0Client is mocked. Please use auth0.getSession.mockReturnValue to set the session value for your tests.'
    );
  }
  getSession = jest.fn();
  middleware = jest.fn();
}
