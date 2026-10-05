module.exports = (api) => {
  let ignorePaths = [];
  const isTest = api.env('test');
  if (!isTest) {
    ignorePaths.push('**/*.test.tsx', '**/*.test.ts');
  }
  const config = {
    ignore: ignorePaths,
    comments: false,
    presets: [
      ['@babel/preset-env', { targets: { esmodules: true }, modules: isTest ? 'commonjs' : false }],
      '@babel/preset-typescript',
      [
        '@babel/preset-react',
        {
          runtime: 'automatic',
        },
      ],
    ],
    plugins: ['@babel/plugin-transform-runtime'],
  };
  return config;
};
