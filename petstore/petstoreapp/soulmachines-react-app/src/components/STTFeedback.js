import React, { useEffect, useState } from 'react';
import { useSelector } from 'react-redux';
import styled from 'styled-components';
import PropTypes from 'prop-types';

function STTFeedback({ className }) {
  const {
    intermediateUserUtterance,
    userSpeaking,
    lastUserUtterance,
    transcript,
  } = useSelector(({ sm }) => ({ ...sm }));
  const [hideInputDisplay, setHideInputDisplay] = useState(false);

  const placeholder = intermediateUserUtterance === '' ? '' : intermediateUserUtterance;

  let timeout;
  useEffect(() => {
    if (userSpeaking === true || lastUserUtterance.length > 0) setHideInputDisplay(false);
    const createTimeout = () => setTimeout(() => {
      if (userSpeaking === false) setHideInputDisplay(true);
      else createTimeout();
    }, 3000);
    timeout = createTimeout();
    return () => clearTimeout(timeout);
  }, [userSpeaking, lastUserUtterance]);

  const transcriptOnlyText = transcript.filter((t) => 'text' in t);
  const feedbackDisplay = (
    <span
      className={`badge bg-light input-display
        ${userSpeaking ? 'utterance-processing' : ''}
        ${(transcriptOnlyText.length < 1 && intermediateUserUtterance === '') || hideInputDisplay ? 'hide-input' : 'show-input'}
        `}
    >
      <div className="text-wrap text-start input-display">
        {userSpeaking ? 'Listening: ' : 'DP heard: '}
        {placeholder || lastUserUtterance}
        {
          userSpeaking
            ? (
              <div>
                <div className="spinner-border spinner-border-sm ms-1" role="status">
                  <span className="visually-hidden">Loading...</span>
                </div>
              </div>
            )
            : null
        }
      </div>
    </span>
  );

  return (
    <div className={className}>
      {hideInputDisplay ? null : feedbackDisplay}
    </div>
  );
}

STTFeedback.propTypes = {
  className: PropTypes.string.isRequired,
};

export default styled(STTFeedback)`
  display: inline-block;

  .badge {
    font-size: 14pt;
    font-weight: normal;
    color: #000;
  }
  .utterance-processing {
    opacity: 0.5;
    font-style: italic;
  }
  .input-display {
    transition: bottom 0.3s, opacity 0.3s;
    height: auto;
    display: flex;
  }
  .hide-input {
    position: relative;
    bottom: -2.5rem;
    opacity: 0;
  }
`;
