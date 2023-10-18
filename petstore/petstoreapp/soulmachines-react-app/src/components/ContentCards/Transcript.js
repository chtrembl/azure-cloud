import React, { useEffect, useState } from 'react';
import { connect } from 'react-redux';
import styled from 'styled-components';
import PropTypes from 'prop-types';
import ContentCardSwitch from '../ContentCardSwitch';
import { primaryAccent } from '../../globalStyle';

function Transcript({ className, transcript }) {
  // scroll to bottom of transcript whenever it updates
  let scrollRef;
  const [isMounting, setIsMounting] = useState(true);
  useEffect(() => {
    setIsMounting(false);
    return () => setIsMounting(true);
  });
  // state value is arbitrary, we just need it to change to trigger the effect hook
  const [triggerScrollIntoView, setTriggerScroll] = useState(false);
  useEffect(() => {
    scrollRef.scrollIntoView({ behavior: isMounting ? 'auto' : 'smooth' });
    setTriggerScroll(false);
  }, [transcript, triggerScrollIntoView]);

  const transcriptDisplay = transcript.map(({
    source, text, card, timestamp,
  }, index) => {
    // we don't want to wrap cards in a bubble, return as is w/ a key added
    if (card) {
      return (
        <ContentCardSwitch
          card={card}
          index={index}
          key={timestamp}
          triggerScrollIntoView={() => setTriggerScroll(true)}
          inTranscript
        />
      );
    }
    if (!text || text?.length === 0) return null;
    return (
      <div key={timestamp}>
        <div className={`transcript-entry ${source === 'user' ? 'transcript-entry-user' : 'transcript-entry-persona'}`}>
          <div>
            <small>
              {source === 'user' ? 'You' : 'Digital Person A'}
            </small>
          </div>
          <div className="transcript-entry-content">
            {text}
          </div>
        </div>
      </div>
    );
  });

  return (
    <div className={className}>
      <div className="transcript-list-group">
        {transcriptDisplay.length > 0
          ? transcriptDisplay
          : (
            <li className="list-group-item">
              No items to show, say something!
            </li>
          )}
        {/* height added because safari doesn't display zero height elems,
        so the scroll behavior doesn't work */}
        <div ref={(el) => { scrollRef = el; }} style={{ clear: 'both', height: '1px' }} />
      </div>
    </div>
  );
}

Transcript.propTypes = {
  className: PropTypes.string.isRequired,
  transcript: PropTypes.arrayOf(PropTypes.shape({
    source: PropTypes.string,
    text: PropTypes.string,
    timestamp: PropTypes.string,
  })).isRequired,
};

const StyledTranscript = styled(Transcript)`
  width: 100%;

  .transcript-list-group {
    flex-shrink: 1;
    display: flex;
    flex-direction: column;
    overflow-y: scroll;
    scrollbar-width: none;

    &::-webkit-scrollbar {
      display: none;
    }
  }

  .transcript-entry {
    margin-bottom: 0.8rem;
    small {
      display: block;
      color: #B2B2B2;
      padding-bottom: 0.2rem;
    }
  }

  .transcript-entry-content {
    padding: 24px 20px;
  }

  .transcript-entry-persona {
    float: left;

    .transcript-entry-content {
      border-top-right-radius: 1.1rem;
      border-top-left-radius: 1.1rem;
      border-bottom-right-radius: 1.1rem;

      background: ${primaryAccent};
      color: #FFF;
    }
  }
  .transcript-entry-user {
    float: right;

    small {
      text-align: right;
    }
    .transcript-entry-content {
      border-top-right-radius: 1.1rem;
      border-top-left-radius: 1.1rem;
      border-bottom-left-radius: 1.1rem;

      background: #FFF;
      border: 1px solid rgba(0,0,0,0.3);
    }
  }
`;

const mapStateToProps = ({ sm }) => ({
  transcript: sm.transcript,
});

export default connect(mapStateToProps)(StyledTranscript);
