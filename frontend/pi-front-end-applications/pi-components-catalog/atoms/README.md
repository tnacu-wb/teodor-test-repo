# Common UI library

This library is building on top of Chakra UI and is transpiled using microbundle. For more info on microbundle checkout their [repo](https://github.com/developit/microbundle), it contains usefull information on how it handles dependencies.

&nbsp;

# Install

Run the below command in the root folder of the mono-repo to link the common-ui package to your application.

  ```bash
    yarn workspace ${your-module-name} add @whitbread-eos/atoms
  ```
> Example: yarn lerna add @whitbread-eos/common-ui --scope=registration

&nbsp;


# Usage

This module can be ran in watch mode:
```
    yarn dev
```
Or you can just build it one time:

```
    yarn build
```
> The commands have to be ran in the module directory.

**If you are adding a component or modifying an existing one, make sure to check the index.js file from the dist folder for syntax error or to check if the module is correctly consumed in another application. Microbundle sometimes produces some syntax errors while transpiling external assets (e.g. icons).**
> If this happens, checkout for fixes in the troubleshooting tab

&nbsp;

# Troubleshooting


- When adding a new package to your application (with yarn or npm), you may receive the error that the @whitbread-eos/common-ui module cannot be found. If that is the case:
  - manually remove the `@whitbread-eos/common-ui` dependency from your app's `package.json`
  - install the desired package
  - link Common UI to your app again: `yarn lerna add @whitbread-eos/common-ui --scope={your-module-name}` (run this command in the mono-repo root folder)
- When transpiling external assets, like icons, microbundle sometimes produces some syntax errors in the index.js file. A temporary hack to this is to make a change in one of the components (comment out a line then comment it back in) and then re-build the module until the transpiled index.js file is properly compiled.