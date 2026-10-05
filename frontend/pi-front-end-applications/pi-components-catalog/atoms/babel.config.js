/* Important! This file handles transpiling the atoms for development and/or production.
  We use this filename (instead of the common babel.config.js name) to prevent Storybook from using it,
  and stick to it's own default internal babel config when building and running stories.
*/

const babelConfig = require('../config/babel.config');
module.exports = babelConfig;
