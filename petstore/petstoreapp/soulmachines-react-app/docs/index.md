This template succeeds the previous [react-template](https://github.com/soulmachines/react-template). This is a complete re-write based off of [create-react-app](https://github.com/facebook/create-react-app) and is designed mainly to provide a simpler and more familiar developer experience.

This template serves as "opinionated documentation," in that it contains most of the functionality that UI's should have and how to use it. As members of Soul Machines Customer Success engineering, we believe this is a good example of how the user flow and interaction with the Digital Person should work. This template is provided with the expectation that most, if not all, of the styling will be altered.

## Setup

You need a token server to authenticate the UI session with the Soul Machines Persona server. Either Soul Machines will provide an endpoint, or you will have to spin up an instance of [express-token-server](https://github.com/soulmachines/express-token-server) with your credentials from DDNA Studio.

### Copy `.env.example` contents into `.env`
Create an empty text file called `.env` and copy the contents of `.env.example` into it. These environment variables are required for the UI to run. Set the value of `REACT_APP_TOKEN_URL` to the endpoint of your token server. Optionally, if you're using an orchestration server, set the value of

### `npm install`
Run to install the project's dependencies.

### `npm start`
Starts the development server. Open [http://localhost:3000](http://localhost:3000) to view it in the browser. The page will automatically reload when you make changes.

### `npm run build`
Builds the app for production to the `build` folder. The files will be bundled and minified for production.

## Linting & Code Style

This project strictly follows [AirBnB's JavaScript style guide](https://github.com/airbnb/javascript). We recommend you install [ESLint](https://eslint.org/) in your editor and adhere to its recommendations.

We have also provided an `.editorconfig` file that you can use in your editor to match the indentation, whitespace, and other styling rules we used in creating this template.

## Docs
- [Creating custom content cards](/creating-custom-content-cards)