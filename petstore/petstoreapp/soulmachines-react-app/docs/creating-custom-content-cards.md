# Creating custom content cards

Creating a custom content card is relatively simple and involves two main steps: creating your card component in `src/components/ContentCards/` and adding your card to the map in `src/components/ContentCardSwitch.js`. 

## Creating a new content card component

Content cards can be as simple or complex as you need. 

### Predefined arguments

Content cards are passed an object as the first arguments that contain at least four key-value pairs: `id`, `data`, `triggerRemoval`, and `inTranscript`.

`id` is the name of the content card payload as defined in the corpus. When you call `@showCards(cardID)` in your dialog, the persona server will pass the value of `cardID` to the UI at the moment the speech marker is called, allowing you to show a content card at just the right time. 

Your conversation engineer should note that content cards name must be prefixed with `public-`, however this will be truncated by the persona server, and the value for `id` will be everything following the prefix. 

The `data` object contains whatever data payload you've defined in your corpus. For example, here's what a potential payload for an `options` content card could look like:
```json
{
	"public-menuoptions": {
		"component": "options",
		"data": {
			"options": [
				{
					"label": "First Option",
					"value": "first-option"
				},
				{
					"label": "Second Option",
					"value": "second-option"
				}
			]
		}
	}
}
```

`triggerRemoval` is a function that the component can call if you need it to hide itself at an arbitrary time. For example, in the stock video component, the user is expected to click on the component several times, but we only want a click on "I'm done" to hide the card.

`inTranscript` is a boolean value that indicates whether the content card is being displayed inside of the transcript. The transcript component will show content cards inline with the conversation history. It's important that content cards that take up a large portion of the screen (e.g. the video component) don't disrupt the experience when the transcript is open. In the case of the video component, when on display in the transcript, it renders itself as a small thumbnail that can be clicked on to open the full-screen video experience. 

### Sending input to the NLP

Text data can be sent to the NLP by dispatching events to the Redux store, which will interact with `smwebsdk`. `sendTextMessage` or `sendEvent` can be imported and the `connect` method from `react-redux` can be used to dispatch events to Redux. Here is a complete example of a minimal component that sends text and events when a button is pressed:

```jsx
import React from 'react';
import { connect } from 'react-redux';
import { sendTextMessage, sendEvent } from '../../store/sm/index';

const CardThatSendsThings = ({ dispatchText, dispatchEvent, data }) => {
	const { textPayload, eventName, eventPayload } = data;
	const handleText = () => dispatchText(textPayload);
	const handleEvent = () => dispatchEvent({ eventName: eventName, payload: eventPayload });

	return(
	<div>
		Text: <button type="button" onClick={handleText} />
		Event: <button type="button" onClick={handleEvent} />
	</div>
	);
}

const mapDispatchToProps = (dispatch) => ({
	dispatchText: (text) => sendTextMessage({ text }),
	dispatchEvent: ({ eventName, payload }) => sendEvent({ eventName, payload }),
});

export default connect(null, mapDispatchToProps)(CardThatSendsThings);
```

## Connecting your component to `ContentCardSwitch`

The `ContentCardSwitch` component maps the component key of the card payload to the proper React component. To add your component, add a key to the `componentMap` object with an object defining the component handler. `ContentCardSwitch` can also automatically hide a card once it's been clicked on—in this case, we'll set the value of `removeOnClick` to `true`. Using the previous example, we'll render the example card when the `component` key of the content card payload is set to `demoCard`:

```jsx
import React from 'react';
import CardThatSendsThings from './ContentCards/CardThatSendsThings';

const ContentCardSwitch = ({ ... }) => {
  const componentMap = {
		...,
		demoCard: {
			element: CardThatSendsThings,
			removeOnClick: true,
		},
	};
	...
```

And to use our card in Dialogflow ES, we could create an utterance: `Here's the demo card we just created! @showCards(firstInstanceOfDemoCard)` and this accompanying payload:

```json
{
	"soulmachines": {
		"public-firstInstanceOfDemoCard": {
			"component": "demoCard",
			"data": {
				"textPayload": "Hello world!",
				"eventName": "foo",
				"eventPayload": { "foo": "bar" }
			}
		}
	}
}
```
When defining content card payloads in Dialogflow ES, the data must be a value of the `soulmachines` key.

At the moment the `@showCards` marker is called, your content card should appear!