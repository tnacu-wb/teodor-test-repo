# Generate & Replace placeholder variables with Environment variables

Since we are moving to a Next JS Standalone build type, we have the following inconvenience to cover:

1. While we are building the application, we need to provide the final env vars in order to generate static files (HTML and JS files) with their coresponding values. This conflicts with the current strategy of using the same Docker Image for multiple environemnts.
2. Until we establish another way to get the Env Vars, we will search and replace in the current build files for the placeholder values we set at the build time
