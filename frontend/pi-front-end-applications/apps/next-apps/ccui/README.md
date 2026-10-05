# NextJS Boilerplate and Premier Inn NextJS Apps

The NextJS Boilerplate is used as a base template to generate NextJS Apps inside the `/apps` folder when using
the `yarn generate:next-app` command in the mono-repo root folder...

## Features.

* [TypeScript](https://www.typescriptlang.org/) support.
* Linting support using [Eslint](https://eslint.org/) and [Prettier](https://prettier.io/)
* Testing and test coverage support using [Jest](https://jestjs.io/)
  and [React Testing Library](https://testing-library.com/docs/react-testing-library/intro/)
* Internationalization support using [i18next](https://www.i18next.com/).
* **Common UI** library integration and [Chakra UI](https://chakra-ui.com/) support by extending the Chakra theme
  provided by it.
* Forms logic and validation support using [React Hook Form](https://react-hook-form.com/)..
* Data management support using [React Query](https://react-query.tanstack.com/)..
* GraphQL support using [GraphQL Request](https://github.com/prisma-labs/graphql-request).
* GraphQL playground by using [GraphiQL](https://github.com/graphql/graphiql).

## Setup:

- Create a `.env.local` environment variables file (ignored in Git by default). Unlike other environment variables files, this file should contain secrets so DO NOT remove it from `.gitignore`! An additional `.env.local.sample` is present in this repository to be use as a reference..

- For the best development experience, you can use [VSCode](https://code.visualstudio.com/) with
  the [Eslint extension](https://marketplace.visualstudio.com/items?itemName=dbaeumer.vscode-eslint) and
  the [Prettier extension](https://marketplace.visualstudio.com/items?itemName=esbenp.prettier-vscode) and create
  a `.vscode/settings.json` file (git ignored out of the box) in the mono-repo root folder with the following:

```json
{
  "editor.codeActionsOnSave": {
    "source.fixAll.eslint": true
  },
  "editor.formatOnSave": true,
  "editor.defaultFormatter": "esbenp.prettier-vscode"
}
```
## Usage..............

* `yarn dev` - starts NextJS in dev mode
* `yarn build` - builds the NextJS files for production
* `yarn start` - starts NextJS in production mode
* `yarn lint` - lints the files in this project
* `lint:fix` - applies possible automatic linting and formatting fixes on the files in this project
* `test` - runs all unit tests in this project
* `test:ci` - runs all unit tests in this project in CI mode
* `test:watch` - runs all unit tests in this project in watch mode
* `test:coverage` - provides test coverage information in the terminal
* `test:coverage:open`: provides test coverage information in the browser


# Troubleshooting.

- When adding a new package to your application (with yarn or npm), you may receive the error that the
  @whitbread-eos/atoms module cannot be found. If that is the case:
    - manually remove the `@whitbread-eos/atoms` dependency from your app's `package.json`
    - install the desired package
    - link Common UI to your app again: `yarn workspace ${your-module-name} add @whitbread-eos/atoms` (run this
      command in the mono-repo root folder)

# Technical On-boarding Reading Recommendations

Find listed bellow some CORE recommended resources (both quick starts and more in depth) for a proper technical ramp-up.
For additional libraries that are used on the project (such as internationalization, React Query, etc.) you can take a
look at their official documentations or other prefferred learning mediums whenever required.

## Quick Start:.........

- [React](https://beta.reactjs.org/learn)
- [Chakra UI](https://egghead.io/courses/build-a-modern-user-interface-with-chakra-ui-fac68106)
- [Typesctipt (in React)](https://www.youtube.com/watch?v=PL1NUl7fQ2I&list=PLG-Mk4wQm9_LyKE5EwoZz2_GGXR-zJ5Ml)
- [React Testing Library](https://www.robinwieruch.de/react-testing-library)
- [NextJS](https://nextjs.org/learn)

## In Depth Resources

- [React](https://beta.reactjs.org)
- [Chakra UI](https://chakra-ui.com)
- [Typesctipt (in React)](https://react-typescript-cheatsheet.netlify.app/)
- [React Testing Library](https://testing-library.com/docs/react-testing-library/intro)
- [NextJS](https://nextjs.org/docs)
