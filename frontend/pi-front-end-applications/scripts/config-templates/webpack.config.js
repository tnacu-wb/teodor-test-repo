const path = require("path");
const { CleanWebpackPlugin } = require("clean-webpack-plugin");

module.exports = {
  entry: "./src/index.ts",
  mode: "development",
  plugins: [new CleanWebpackPlugin()],
  output: {
    filename: "index.js",
    path: path.resolve(__dirname, "dist"),
    libraryTarget: "commonjs2",
  },
  resolve: {
    extensions: [".tsx", ".ts", ".js"],
  },
  module: {
    rules: [
      {
        test: /\.(ts|js)x?$/,
        exclude: /node_modules/,
        use: [
          {
            loader: "babel-loader",
          },
          {
            loader: "ts-loader",
          },
        ],
      },
      {
        test: /\.svg$/i,
        type: "asset/inline",
      },
    ],
  },
  externals: [
    {
      react: "react",
      "@emotion/react": "@emotion/react",
      "@emotion/styled": "@emotion/styled",
      classnames: "classnames",
      "date-fns": "date-fns",
      "framer-motion": "framer-motion",
      "react-dom": "react-dom",
    },
    /^@chakra-ui/i,
  ],
};
