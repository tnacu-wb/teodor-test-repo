# Creating an app within the monorepo

To create an app we have a template which we use which has the most common packages in it, this can be located within ```/templates/apps```

A generator script has been created to deploy an app to save the many changes needed, to run this simply execute the following npm script in the moonorepo.

`npm run deploy:app`, this will prompt you to complete the package name and deployment destination and this will generate an application for you from the template. This will also bootstrap the new package once generated.



