module.exports = function(api) {
  const babelEnv = api.env();
  const babelVer = api.version;

  api.cache(true);

  return {
    presets: [
      [
        "@babel/preset-env",
        {
          targets: {
            browsers: [
              "last 2 versions",
              "not ie <= 8"
            ]
          },
          useBuiltIns: "usage",
          corejs: 3,
          modules: false,
          //loose: true,
          //debug: true
        }
      ]
    ],
    comments: false
  };
};
