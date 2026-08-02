/*
 * using for webpack3
 * 20200227
 */
const path = require('path');
const webpack = require('webpack');
//[when][vue-loader v15]const VueLoaderPlugin = require('vue-loader/lib/plugin');
const UglifyJsPlugin = require('uglifyjs-webpack-plugin');

//--[option] {
const { CleanWebpackPlugin } = require('clean-webpack-plugin'); // remove built files
const ManifestPlugin = require("webpack-manifest-plugin");      // build manifest file 
//--[option] }

const config = {
  context: path.resolve(__dirname, './src/app'),

  entry: {
    //--[ref]'_egPartCase':     './_egPartCase.js',
    'adptranService': './adptranService.js',
    'apieditService': './apieditService.js',
    'kosXlsxService': './kosXlsxService.js',
    'verifyExecute':  './verifyExecute.js',

    'apistatus/apistatus_group': './apistatus/apistatus_group.js',
    'apistatus/apistatus_list': './apistatus/apistatus_list.js',

    'devQuery': './devQuery.js',
  },
  output: {
    path: path.resolve(__dirname, './../../src/main/webapp/resources/adptran/bundle'),
    publicPath: '/resources/adptran/bundle/',
    filename: '[name]_bundle.js',
  },
  module: {
    rules: [
      {
        test: /\.js$/,
        use: {
          loader: 'babel-loader',
          /*--
          //[when][no babel config file(babel.config.js)]
          options: { ... }
          --*/
        },
        //--[try]include: path.join(__dirname 'src'),
        exclude: /node_modules/
        /*--
        //[when][vue-loader v15]
        exclude: file => (/node_modules/.test(file) && !/\.vue\.js/.test(file))
        --*/     
      },
      {
        test: /\.vue$/,
        loader: 'vue-loader',
        /*--
        //[when][vue-loader v14][no babel config file(babel.config.js)]
        options: {
          loaders: { js: [ loader: 'babel-loader', options: {  ... } ] }
        }
        --*/
      },
      {
        test: /\.css$/,
        use: ['vue-style-loader', 'css-loader']
      },
    ]
  },
  plugins: [
    //[when][vue-loader v15]new VueLoaderPlugin(),
    new webpack.optimize.ModuleConcatenationPlugin(), // reduce size

    //--[option] {
    new ManifestPlugin({ fileName: "manifest.json", basePath: path.resolve(__dirname, './../../src/main/webapp/WEB-INF/views/bcconsole') }),   // build manifest file
    //--[option] }
    //--[try]extractCommons,
  ],
  resolve: {
    alias: {
      'vue$': 'vue/dist/vue.esm.js',
      //-- for import from
      '@': path.resolve(__dirname, './src'),
      '@approot': path.resolve(__dirname, './src/app'),
    },
    extensions: ['*', '.js', '.vue']
  },
  devServer: {
    historyApiFallback: true,
    // --@@noInfo: true,
    overlay: true,
    hot: true,
    port: 19081,
    host: '0.0.0.0',
    disableHostCheck: true,
    // allowedHosts: [ 'drmccy.iptime.org','.menupan.com', ],
  },
  devtool: 'source-map',
}

if (process.env.NODE_ENV === 'production') {
  //-- config.devtool = 'source-map';
  config.plugins = (config.plugins || []).concat([
    new webpack.DefinePlugin({ 'process.env.NODE_ENV': JSON.stringify('production') }),
    new UglifyJsPlugin({ sourceMap: true, uglifyOptions: { compress: {}, mangle: true } }),

    //--[option] {
    new CleanWebpackPlugin({ cleanOnceBeforeBuildPatterns: [ '**/*_bundle.js', '**/*_bundle.js.map'] }),
    //--[option] }
  ]);
}

module.exports = config;
