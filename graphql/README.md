# opera-apollo-subgraphs
This repo contains the Opera Apollo Subgraphs

To run the Apollo Server on the local machine, please follow the instructions below:

First, you need to install the dependencies by running the following command:

`npm install`

You will find a new folder *node_modules* in the root directory after this command is completed successfully. It contains all the dependencies required to run the Apollo Server.

To compile the code, run the following command:

`npm run build`

After this command is completed successfully, you can find a newly created folder *dist* in the root directory. This folder includes the compiled code of the entire project

To start the application on your local machine, run the following command:

`npm run start-local`

Now you can access the Apollo Server at http://localhost:4000/graphql. Be aware that if you make any changes in the code you have to restart the server to make it reflect the new code version.

If you want the Apollo Server to automatically restart when you make changes in the code, you can run the following command:

`npm run start-local-hot`

To run the tests, run the following command:

`npm run test`