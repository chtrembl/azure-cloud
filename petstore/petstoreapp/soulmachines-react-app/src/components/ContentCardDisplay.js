import React, { useEffect } from 'react';
import { connect } from 'react-redux';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { setActiveCards, animateCamera } from '../store/sm/index';
// uncomment if using manual camera moves
// import { calculateCameraPosition } from '../utils/camera';
import Transcript from './ContentCards/Transcript';
import ContentCardSwitch from './ContentCardSwitch';
import breakpoints from '../utils/breakpoints';

function ContentCardDisplay({
  activeCards,
  // uncomment if using manual camera moves
  // dispatchAnimateCamera,
  // videoWidth,
  // videoHeight,
  showTranscript,
  className,
  connected,
  inTranscript,
}) {
  if (!activeCards) return null;
  const CardDisplay = activeCards.map((c, index) => (
    <div className="mb-2" key={JSON.stringify(c)}>
      <ContentCardSwitch card={c} index={index} inTranscript={inTranscript} />
    </div>
  ));

  const animateCameraToFitCards = () => {
    if (connected) {
      // uncomment if using manual camera moves
      // if ((activeCards.length > 0 || showTranscript === true) && videoWidth >= breakpoints.md) {
      //   dispatchAnimateCamera(calculateCameraPosition(videoWidth, videoHeight, 0.7));
      // } else dispatchAnimateCamera(calculateCameraPosition(videoWidth, videoHeight, 0.5));
    }
  };

  useEffect(() => {
    animateCameraToFitCards();
  }, [showTranscript, activeCards]);

  useEffect(() => {
    window.addEventListener('resize', animateCameraToFitCards);
    return () => window.removeEventListener('resize', animateCameraToFitCards);
  });

  return (
    <div className={className}>
      {showTranscript ? (
        <div data-sm-content>
          <Transcript />
        </div>
      ) : (
        CardDisplay
      )}
    </div>
  );
}

ContentCardDisplay.propTypes = {
  // eslint-disable-next-line react/forbid-prop-types
  activeCards: PropTypes.arrayOf(PropTypes.object).isRequired,
  // uncomment if using manual camera moves
  // dispatchAnimateCamera: PropTypes.func.isRequired,
  // videoWidth: PropTypes.number.isRequired,
  // videoHeight: PropTypes.number.isRequired,
  showTranscript: PropTypes.bool.isRequired,
  className: PropTypes.string.isRequired,
  connected: PropTypes.bool.isRequired,
  inTranscript: PropTypes.bool,
};

ContentCardDisplay.defaultProps = {
  inTranscript: false,
};

const StyledContentCardDisplay = styled(ContentCardDisplay)`
  overflow-y: scroll;
  margin-bottom: 0.9rem;

  scrollbar-width: none;
  &::-webkit-scrollbar {
    display: none;
  }

  // make this smaller
  max-height: 40vh;
  @media(min-width: ${breakpoints.md}px) {
    max-height: 100%;
    background: none;
    outline: none;
    margin-bottom: auto;
  }
  width: 100%;
`;

const mapStateToProps = ({ sm }) => ({
  activeCards: sm.activeCards,
  videoWidth: sm.videoWidth,
  videoHeight: sm.videoHeight,
  showTranscript: sm.showTranscript,
  connected: sm.connected,
});

const mapDispatchToProps = (dispatch) => ({
  dispatchActiveCards: (activeCards) => dispatch(
    // the next time the persona speaks, if the cards are stale, it will clear them.
    // if this value isn't desired, don't set this value to true.
    setActiveCards({ activeCards, cardsAreStale: true }),
  ),
  dispatchAnimateCamera: (options, duration = 1) => dispatch(animateCamera({ options, duration })),
});

export default connect(mapStateToProps, mapDispatchToProps)(StyledContentCardDisplay);
