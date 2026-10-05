FROM 327347623470.dkr.ecr.eu-west-1.amazonaws.com/whitbreaddigital/node-opera:22.23.0-alpine3.24-base-f3d6f4e

WORKDIR /app

COPY --link --chown=9001:9001 . .

RUN npm install

EXPOSE ${PORT}

CMD ["node", "dist/index.js"]
