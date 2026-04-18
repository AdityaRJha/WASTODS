# Use Node.js 20 (LTS) as base image
# Stable and widely supported for React apps
FROM node:20

# Set working directory inside container
# All subsequent commands will run from /app
WORKDIR /app

# Copy only package.json and package-lock.json first
# This helps Docker cache dependencies (faster rebuilds)
COPY package*.json ./

# Install dependencies
# Runs only when package.json changes (thanks to caching above)
RUN npm install

# Copy the rest of the application code
# Includes src/, public/, etc.
COPY . .

# Expose port 3000 (React dev server runs here)
EXPOSE 3000

# Start React app in development mode
# This runs "npm start" → react-scripts start
# Hot reload enabled (good for dev, not production)
CMD ["npm", "start"]