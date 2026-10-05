export enum LoginStep {
  LOGIN_FORM = 'LOGIN_FORM',
}

export type LoginState = {
  email: string;
  password: string;
  redirect: string;
};
