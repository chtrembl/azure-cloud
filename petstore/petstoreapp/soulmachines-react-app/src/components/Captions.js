import React, { useState, useEffect } from 'react';
import { connect } from 'react-redux';
import styled from 'styled-components';
import PropTypes from 'prop-types';
import { syllable } from 'syllable';

function Captions({
  speechState, lastPersonaUtterance, className, connected,
}) {
  const [showCaptions, setShowCaptions] = useState(false);
  // if we have a very long response, we need to cycle the displayed content
  const [captionText, setCaptionText] = useState('');
  // keep track of when we first showed this caption. captions should be on screen min 1.5s
  const [captionStartTimestamp, setCaptionStartTimestamp] = useState();
  const [captionTimeout, setCaptionsTimeout] = useState();
  const minCaptionDuration = 1500;

  useEffect(() => {
    if (connected === false) setShowCaptions(false);
    else if (speechState === 'speaking') {
      // when a new utterance starts, show captions
      setShowCaptions(true);
      const sentences = lastPersonaUtterance.split('. ');
      // estimate how long each caption should be displayed based on # of syllables and punctuation
      const sentencesWithDurationEstimate = sentences.map((s) => {
        const millisPerSyllable = 210;
        const millisPerPunct = 330;

        const syllableCount = syllable(s);

        const regex = /[^\w ]/gm;
        const punctCount = [...s.matchAll(regex)].length;

        const durationEstimate = (syllableCount * millisPerSyllable)
          + (punctCount * millisPerPunct)
          // add one punct delay for the period that gets stripped when splitting the sentences
          + millisPerPunct;
        return { text: s, durationEstimate };
      });

      // recursively cycle through sentences on very long captions
      const displayCaption = (i) => {
        const { text, durationEstimate } = sentencesWithDurationEstimate[i];
        setCaptionText(text);
        if (sentencesWithDurationEstimate[i + 1]) {
          setTimeout(() => displayCaption(i + 1), durationEstimate);
        }
      };
      displayCaption(0);

      // record when we put captions on the screen
      setCaptionStartTimestamp(Date.now());
      // clear any previous timeout from previous captions.
      // this won't make the captions disappear, since we're overwriting the content
      clearTimeout(captionTimeout);
    } else {
      // when the utterance ends:
      const captionsDisplayedFor = Date.now() - captionStartTimestamp;
      // check to see if the captions have been displayed for the min. amount of time
      if (captionsDisplayedFor > minCaptionDuration) setShowCaptions(false);
      // if not, set a timeout to hide them when it has elapsed
      else {
        const newCaptionTimeout = setTimeout(() => {
          setShowCaptions(false);
        }, minCaptionDuration - captionsDisplayedFor);
        setCaptionsTimeout(newCaptionTimeout);
      }
      // sometimes we get blank input, hide that when it happens
      if (captionText === '') setShowCaptions(false);
    }
  }, [speechState, connected]);

  if (showCaptions === false) return null;

  return (
    <div className={className}>
      <div className="captions text-center">
        { showCaptions ? captionText : null }
      </div>
    </div>
  );
}

Captions.propTypes = {
  speechState: PropTypes.string.isRequired,
  lastPersonaUtterance: PropTypes.string.isRequired,
  className: PropTypes.string.isRequired,
  connected: PropTypes.bool.isRequired,
};

const StyledCaptions = styled(Captions)`
  display: inline-block;
  .captions {
    margin-bottom: .3rem;

    padding-top: 0.3rem;
    padding-bottom: 0.3rem;
    padding-left: 0.6rem;
    padding-right: 0.6rem;

    background-color: rgba(0, 0, 0, 0.7);
    color: #FFF;

    border-radius: 2px;

    display: flex;
    align-items: center;

    min-height: 35px;
    transition: height 0.3s;
  }
`;

const mapStateToProps = (state) => ({
  speechState: state.sm.speechState,
  lastPersonaUtterance: state.sm.lastPersonaUtterance,
  connected: state.sm.connected,
});

export default connect(mapStateToProps)(StyledCaptions);
