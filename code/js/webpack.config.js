const path = require("path");

module.exports = {
  mode: "development",
  resolve: {
    extensions: [".tsx", ".ts", ".js"],
  },
  module: {
    rules: [
      {
        test: /\.tsx?$/,
        use: "ts-loader",
        exclude: /node_modules/,
      },
      {
        test: /\.css$/i,
        include: path.resolve(__dirname, 'src'),
        use: ['style-loader', 'css-loader', 'postcss-loader'],
      },
    ],
  },
  devServer: {
    allowedHosts: "gomoku.serveo.net",
    static: path.resolve(__dirname, "dist"),
    historyApiFallback: true,
    proxy: {
      '/api': 'http://localhost:2001',
    },
  },
};
