import React, { createRef, useEffect, useState } from 'react';
import { connect, useDispatch, useSelector } from 'react-redux';
import PropTypes from 'prop-types';
import ContentCardDisplay from '../components/ContentCardDisplay';
import {
  addConversationResult, clearTranscript, setActiveCards, setShowTranscript,
} from '../store/sm';

const checkIsValidJSON = (s) => {
  try {
    JSON.parse(s);
  } catch {
    return false;
  }
  return true;
};

function ContentCardTest({ dispatchSetActiveCards }) {
  const localStorageResults = localStorage.getItem('input') || '';
  const [input, setInput] = useState(localStorageResults);
  const [height, setHeight] = useState(64);
  const textAreaRef = createRef();

  const dispatch = useDispatch();
  const { showTranscript } = useSelector(({ sm }) => {
    const obj = { showTranscript: sm.showTranscript };
    return obj;
  });

  const sizeTextArea = () => {
    // autosize text area to fit
    const scrollHeight = textAreaRef?.current?.scrollHeight;
    if (scrollHeight) setHeight(scrollHeight);
  };
  const executeDisplay = () => {
    if (checkIsValidJSON(input)) {
      dispatchSetActiveCards([{ ...JSON.parse(input) }]);
      dispatch(clearTranscript());
      dispatch(addConversationResult({
        source: 'persona',
        card: { ...JSON.parse(input) },
      }));
    }
    sizeTextArea();
  };

  useEffect(() => {
    executeDisplay();
  }, []);

  useEffect(() => {
    sizeTextArea();
  }, [textAreaRef]);

  const handleTextAreaInput = (e) => {
    // store value for refresh
    const { value } = e.target;
    localStorage.setItem('input', value);

    // if it's valid JSON, prettify
    const isValid = checkIsValidJSON(value);
    if (isValid) {
      const parsedValue = JSON.parse(value);
      dispatchSetActiveCards([{ ...parsedValue }]);
      setInput(value);
    } else {
      dispatchSetActiveCards([]);
      setInput(value);
    }
  };

  const prettify = () => {
    const parsedValue = JSON.parse(input);
    setInput(JSON.stringify(parsedValue, undefined, 4));
  };

  return (
    <div className="mt-3 container">
      <div className="row">
        <h6 className="text-center">content card tester</h6>
        <div className="col-md-8 offset-md-2 form-floating">
          <textarea
            value={input}
            onChange={handleTextAreaInput}
            className="form-control p-1"
            style={{ fontSize: '12pt', height, maxHeight: '20rem' }}
            id="cardsInput"
            ref={textAreaRef}
            onBlur={prettify}
          />
          <small>JSON input will prettify on blur</small>
          <div>
            <button
              type="button"
              className={`btn btn-${showTranscript ? 'outline-' : ''}danger me-2`}
              onClick={() => dispatch(setShowTranscript(!showTranscript))}
            >
              showTranscript:
              {' '}
              {`${showTranscript}`}
            </button>
            <button
              type="button"
              className="btn btn-outline-success me-2"
              onClick={executeDisplay}
            >
              Reset activeCards
            </button>
          </div>
        </div>
        <div className="mt-2 col-md-8 offset-md-2">
          {!checkIsValidJSON(input) ? (
            <code className="text-danger">input is not valid JSON!</code>
          ) : (
            ''
          )}
          <ContentCardDisplay inTranscript={showTranscript} />
        </div>
      </div>
    </div>
  );
}

ContentCardTest.propTypes = {
  dispatchSetActiveCards: PropTypes.func.isRequired,
};

const mapDispatchToProps = (dispatch) => ({
  dispatchSetActiveCards: (activeCards) => dispatch(setActiveCards({ activeCards })),
});
export default connect(null, mapDispatchToProps)(ContentCardTest);
