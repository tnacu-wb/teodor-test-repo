const { exec } = require("child_process");
const name = process.argv[2];

if (!name) {
    return;
}

exec(`yarn workspace ${your-module-name} add @whitbread-eos/atoms`, (error, stdout, stderr) => {
    if (error) {
        console.log(`error: ${error.message}`);
        return;
    }
    if (stderr) {
        console.log(`stderr: ${stderr}`);
        return;
    }
    console.log(`stdout: ${stdout}`);
});