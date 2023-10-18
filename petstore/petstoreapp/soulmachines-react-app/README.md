# Soul Machines React Reference UI

This template succeeds the previous [react-template](https://github.com/soulmachines/react-template). This is re-write is based on [create-react-app](https://github.com/facebook/create-react-app) and is designed mainly to provide a simple and familiar developer experience.

This template contains functional examples of how the user flow and interaction with the Digital Person should work, and likley require styling chanages to suit branding requirements.

## Setup

In order to run this application, you'll either need an API key or a token server. Most projects will use an API key--token servers are only necessary when interfacing with a non-native NLP through a orchestration server.

### Copy `.env.example` contents into `.env`
Create an empty text file called `.env` and copy the contents of `.env.example` into it. These environment variables are required for the UI to run.

If using an API key, set `REACT_APP_PERSONA_AUTH_MODE` to `0` and populate `REACT_APP_TOKEN_URL` with your key.

If using an orchestration server, set `REACT_APP_PERSONA_AUTH_MODE` to `1` and populate `REACT_APP_TOKEN_URL` with your token server endpoint and set `REACT_APP_TOKEN_URL` to `true`.

### `npm install`
Run to install the project's dependencies.

### `npm start`
Starts the development server. Open [http://localhost:3000](http://localhost:3000) to view it in the browser. The page will automatically reload when you make changes.

### `npm run build`
Builds the app for production to the `build` folder. The files will be bundled and minified for production.

## License

Soul Machines React Reference UI is available under the Apache License, Version 2.0. See the [LICENSE.txt](./LICENSE.txt) file for more info.

## Linting & Code Style

This project strictly follows [AirBnB's JavaScript style guide](https://github.com/airbnb/javascript). We recommend you install [ESLint](https://eslint.org/) in your editor and adhere to its recommendations.

We have also provided an `.editorconfig` file that you can use in your editor to match the indentation, whitespace, and other styling rules we used in creating this template.

## Support 
Our team would love to hear from you. For any additional support, feedback, bugs, feature requests, please [submit a ticket](https://support.soulmachines.com) or reach out to us at [support@soulmachines.com](support@soulmachines.com).
