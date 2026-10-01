
FROM node:24-alpine
WORKDIR /app
COPY package.json ./
COPY server.js ./
EXPOSE 8080
ENV PORT=8080
CMD ["npm", "start"]
