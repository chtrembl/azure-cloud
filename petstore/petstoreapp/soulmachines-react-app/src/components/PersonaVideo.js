/* eslint-disable jsx-a11y/media-has-caption */
import React, { createRef, useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import * as actions from '../store/sm';
import proxyVideo from '../proxyVideo';
import { headerHeight, transparentHeader } from '../config';

function PersonaVideo({
  className,
}) {
  const dispatch = useDispatch();
  const setVideoDimensions = (videoWidth, videoHeight) => dispatch(
    actions.setVideoDimensions({ videoWidth, videoHeight }),
  );
  const { isOutputMuted, loading, connected } = useSelector(({ sm }) => ({ ...sm }));
  // video elem ref used to link proxy video element to displayed video
  const videoRef = createRef();
  // we need the container dimensions to render the right size video in the persona server
  const containerRef = createRef();
  // only set the video ref once, otherwise we get a flickering whenever the window is resized
  const [videoDisplayed, setVideoDisplayed] = useState(false);
  // we need to keep track of the inner window height so the video displays correctly
  const [height, setHeight] = useState('100vh');

  const handleResize = () => {
    if (containerRef.current) {
      // the video should resize with the element size.
      // this needs to be done through the redux store because the Persona server
      // needs to be aware of the video target dimensions to render a propperly sized video
      const videoWidth = containerRef.current.clientWidth;
      const videoHeight = containerRef.current.clientHeight;
      setVideoDimensions(videoWidth, videoHeight);
      // constrain to inner window height so it fits on mobile
      setHeight(`${videoHeight}`);
    }
  };

  // persona video feed is routed through a proxy <video> tag,
  // we need to get the src data from that element to use here
  useEffect(() => {
    handleResize();
    window.addEventListener('resize', handleResize);
    if (connected) {
      if (!videoDisplayed) {
        videoRef.current.srcObject = proxyVideo.srcObject;
        setVideoDisplayed(true);
      }
    }
    // when component dismounts, remove resize listener
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <div ref={containerRef} className={className} style={{ height }}>
      {
        connected
          ? (
            <video
              ref={videoRef}
              autoPlay
              playsInline
              className="persona-video"
              id="personavideo"
              data-sm-video
              muted={isOutputMuted}
            />
          )
          : null
      }
      {
        loading
          ? (
            <div className="spinner-border text-primary" role="status">
              <span className="visually-hidden">Loading...</span>
            </div>
          )
          : null
      }
      {
        connected === false && loading === false ? 'disconnected' : ''
      }
    </div>
  );
}

PersonaVideo.propTypes = {
  className: PropTypes.string.isRequired,
};

export default styled(PersonaVideo)`
  /* if you need the persona video to be different than the window dimensions, change these values */
  width: 100vw;

  position: fixed;
  z-index: 0;

  display: flex;
  align-items: center;
  justify-content: center;
  margin-top: ${transparentHeader ? '' : headerHeight};
  .persona-video {
    /* the video element will conform to the container dimensions, so keep this as it is */
    width: 100%;
    height: 100%;
  }
`;
