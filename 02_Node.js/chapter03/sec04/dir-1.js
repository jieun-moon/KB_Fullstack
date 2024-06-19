fs = require('fs');

if (fs.existsSync('./test')) {
  console.log('folder already exists');
} else {
  fs.mkdir('./test', (err) => {
    if (err) {
      return console.error(err);
    }
    console.log('folder created');
  });
}
