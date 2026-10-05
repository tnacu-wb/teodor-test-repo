const execSync = require('child_process').execSync;

const packages = [{
  name: 'premier-inn',
  path: '../apps/next-apps/premier-inn/'
},
{
  name: 'ccui',
  path: '../apps/next-apps/ccui/'
},
{
  name: 'business-booker',
  path: '../apps/next-apps/business-booker/'
},
{
  name: 'api',
  path: '../pi-components-catalog/api/'
},
{
  name: 'atoms',
  path: '../pi-components-catalog/atoms/'
},
{
  name: 'molecules',
  path: '../pi-components-catalog/molecules/'
},
{
  name: 'organisms',
  path: '../pi-components-catalog/organisms/'
},
{
  name: 'utils',
  path: '../pi-components-catalog/utils/'
},
{
  name: 'pages - amend',
  path: '../pi-components-catalog/pages/amend/'
},
{
  name: 'pages - bookings',
  path: '../pi-components-catalog/pages/bookings/'
},
{
  name: 'pages - confirmation',
  path: '../pi-components-catalog/pages/confirmation/'
},
{
  name: 'pages - dashboard',
  path: '../pi-components-catalog/pages/dashboard/'
},
{
  name: 'pages - guest-details',
  path: '../pi-components-catalog/pages/guest-details/'
},
{
  name: 'pages - hotel-details',
  path: '../pi-components-catalog/pages/hotel-details/'
},
{
  name: 'pages - payment',
  path: '../pi-components-catalog/pages/payment/'
},
{
  name: 'pages - register',
  path: '../pi-components-catalog/pages/register/'
},
{
  name: 'pages - repeat-booking',
  path: '../pi-components-catalog/pages/repeat-booking/'
},
{
  name: 'pages - search-account',
  path: '../pi-components-catalog/pages/search-account/'
},
];
const averageOnly = process.argv[2] === '--average';

function showCoverage() {
  packages.forEach(package => {
    const output = execSync(`cd ${package.path} && ./node_modules/.bin/jest --coverage --silent | grep 'All files' && cd -`, { stdio: 'pipe' }).toString();
    const values = output.split('|').map(v => +(v.trim()));
    const average = (values[1] + values[2] + values[3] + values[4]) / 4;

    if (averageOnly) {
      console.log(`${package.name}: ${average.toFixed(2)}%`);
    } else {
      console.log(`
-------------------------
${package.name}:
-------------------------
Statements: ${values[1]}%
Branches:   ${values[2]}%
Functions:  ${values[3]}%
Lines:      ${values[4]}%
-------------------------
Average:    ${average.toFixed(2)}%
-------------------------
`);
    }
  });
}

showCoverage();
