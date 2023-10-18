import React, { useState, useEffect } from 'react';
import YouTube from 'react-youtube';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { useDispatch, useSelector } from 'react-redux';
import {
  setMicOn, sendTextMessage, keepAlive, setShowTranscript, setActiveCards, stopSpeaking,
} from '../../store/sm/index';

function Video({
  data,
  className,
  inTranscript,
}) {
  const { videoId } = data;
  if (videoId === undefined) return <div className="alert alert-danger">no value for videoId!</div>;

  const thumbnailURL = `http://img.youtube.com/vi/${videoId}/0.jpg`;

  const dispatch = useDispatch();

  const { showTranscript, micOn, activeCards } = useSelector(({ sm }) => (
    {
      showTranscript: sm.showTranscript,
      micOn: sm.micOn,
      activeCards: sm.activeCards,
    }));
  const [playPushed, setPlayPushed] = useState(false);
  // in order to most easily let the application pull up the video when the user wants to
  // watch something again, we just modify activeCards.
  // this could be a problem if we need to know if it's actually a part of the conversation,
  // so we prevent this by checking separately if the videoID matches and if it's being rewatched
  const isActiveCard = activeCards[0]?.data?.videoId === videoId;
  const isRewatch = activeCards[0]?.data?.isRewatch === true;

  // we need to store if the transcript is open and if the mic is on while the video plays
  const captureStateAndPrepForPlay = (lockWrites = false) => {
    const rawStored = sessionStorage.getItem('uiStateBeforeVideo');
    const stored = JSON.parse(rawStored);
    const writesLocked = 'writesLocked' in stored ? stored.writesLocked : false;
    // store UI state data only if it wasn't recently stored by another hook call/component remount
    if (writesLocked !== true
      || (isActiveCard && !isRewatch && !writesLocked)) {
      sessionStorage.setItem(
        'uiStateBeforeVideo',
        JSON.stringify({ showTranscript, micOn, writesLocked: lockWrites }),
      );
    } else setPlayPushed(true);
    dispatch(setShowTranscript(false));
    dispatch(setMicOn({ micOn: false }));
  };

  useEffect(() => {
    let prevWritesLocked = false;
    try {
      const stored = JSON.parse(
        sessionStorage.getItem('uiStateBeforeVideo'),
      );
      prevWritesLocked = 'writesLocked' in stored ? stored.writesLocked : false;
    } catch {
      sessionStorage.setItem(
        'uiStateBeforeVideo',
        JSON.stringify({ }),
      );
    }

    let keepAliveInterval;
    if (isActiveCard && !isRewatch && !prevWritesLocked) {
      captureStateAndPrepForPlay(true);
      keepAliveInterval = setInterval(() => dispatch(keepAlive()), 30000);
    }
    return () => (keepAliveInterval ? clearInterval(keepAliveInterval) : null);
  }, []);

  // then, when the video ends, return to the state it was at
  const resetState = () => {
    const {
      showTranscript: oldShowTranscript,
      micOn: oldMicOn,
    } = JSON.parse(sessionStorage.getItem('uiStateBeforeVideo'));
    sessionStorage.setItem(
      'uiStateBeforeVideo',
      JSON.stringify({ }),
    );
    dispatch(setShowTranscript(oldShowTranscript));
    dispatch(setMicOn({ micOn: oldMicOn }));
    dispatch(setActiveCards({ activeCards: [] }));
  };

  const handleEnd = () => {
    if (isRewatch === false) {
      setPlayPushed(false);
      dispatch(sendTextMessage({ text: 'I\'m done watching!' }));
    } else resetState();
  };

  const activeVideo = () => (playPushed === true || isRewatch === true ? (
    <div className="lightbox">
      <div>
        <YouTube
          videoId={videoId}
          opts={{
            playerVars: {
              autoplay: 1,
            },
          }}
          onReady={() => {
            captureStateAndPrepForPlay();
          }}
          onEnd={handleEnd}
        />
        <div className="d-flex align-items-center">
          <small className="me-1">
            The Digital Person is paused while you watch this video and will not
            respond to your voice.
          </small>
          <button
            className="btn btn-outline-dark float-end"
            type="button"
            onClick={handleEnd}
          >
            Exit
          </button>
        </div>
      </div>
    </div>
  ) : (
    <div
      className="video-thumbnail"
      style={{ backgroundImage: `url(${thumbnailURL})` }}
    >
      <button
        className="btn video-play-button"
        type="button"
        onClick={() => {
          dispatch(stopSpeaking());
          setPlayPushed(true);
        }}
      >
        ▶️
      </button>
    </div>
  ));

  return (
    <div className={className}>
      {inTranscript === true ? (
        <div
          className="video-thumbnail"
          style={{ backgroundImage: `url(${thumbnailURL})` }}
        >
          <button
            className="btn video-play-button"
            type="button"
            onClick={() => {
              captureStateAndPrepForPlay(true);
              dispatch(
                setActiveCards({
                  activeCards: [
                    { type: 'video', data: { videoId, isRewatch: true } },
                  ],
                }),
              );
            }}
          >
            ▶️
          </button>
        </div>
      ) : activeVideo()}
    </div>
  );
}

Video.propTypes = {
  data: PropTypes.shape({
    videoId: PropTypes.string.isRequired,
    autoplay: PropTypes.string,
  }).isRequired,
  className: PropTypes.string.isRequired,
  inTranscript: PropTypes.bool,
};

Video.defaultProps = {
  inTranscript: false,
};

export default styled(Video)`
  .video-thumbnail {
    width: 25rem;
    aspect-ratio: 16 / 9;
    background-size: cover;
    background-position: center;

    display: flex;
    justify-content: center;
    align-items: center;
  }
  .video-play-button {
    background-color: #f00;
    color: #FFF;
    height: 2.5rem;
    aspect-ratio: 1;
  }

  .lightbox {
    position: absolute;
    top: 0;
    left: 0;
    width: 100vw;
    height: 100vh;

    display: flex;
    align-items: center;
    justify-content: center;

    background: rgba(0, 0, 0, 0.1);
    backdrop-filter: blur(3px);
  }
`;
