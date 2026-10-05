import https from "https";
import chalk from "chalk";

const odeUrl = `ode-${process.argv[2]}.ode.dev.premierinn.digital`;

const options = {
  hostname: odeUrl,
  port: 443,
  path: "/dispatcher/invalidate.cache",
  method: "POST",
  headers: {
    "CQ-Action": "Delete",
    "CQ-Handle": "/",
    "Content-Length": "0",
    "Content-Type": "application/octet-stream",
    "CQ-Path": "/",
  },
};

console.log(chalk.blueBright(`Clearing the cache for ${odeUrl}\n`));

const req = https.request(options, (res) => {
  res.on("data", (d) => {
    if (res.statusCode === 200) {
      console.log(chalk.greenBright("ODE CACHE HAS BEEN CLEARED!\n"));
    }
  });
});

req.on("error", (error) => {
  console.error(error);
});

req.end();
