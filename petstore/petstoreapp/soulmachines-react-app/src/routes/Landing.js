import React from 'react';
import styled from 'styled-components';
import PropTypes from 'prop-types';
import Color from 'color';
import { useDispatch, useSelector } from 'react-redux';
import { Link } from 'react-router-dom';
import { CameraVideoFill, MicFill } from 'react-bootstrap-icons';
import breakpoints from '../utils/breakpoints';
import Header from '../components/Header';
import { landingBackgroundImage, landingBackgroundColor } from '../config';
import { setRequestedMediaPerms } from '../store/sm';
import micFill from '../img/mic-fill.svg';
import videoFill from '../img/camera-video-fill.svg';

function Landing({ className }) {
  const { mic, camera } = useSelector(({ sm }) => sm.requestedMediaPerms);
  const dispatch = useDispatch();

  return (
    <div className={className}>
      <div className="landing-wrapper">
        <Header />
        <div className="container d-flex">
          <div className="landing-container flex-grow-1">
            <div className="col-12 col-lg-6">
              <div className="row" style={{ marginBottom: '9px' }}>
                <div>
                  <h1 className="fw-bol">Meet Digital Person A.</h1>
                </div>
              </div>
              <div className="row">
                <div>
                  <h4 className="fw-light" style={{ marginBottom: '31px' }}>
                    Briefly describe how your Digital Person can help users.
                  </h4>
                </div>
              </div>
              <div className="row" style={{ marginBottom: '36px' }}>
                <div>
                  <div className="form-check form-switch">
                    <label
                      className="form-check-label d-flex align-items-center"
                      htmlFor="micPermSwitch"
                    >
                      <input
                        className={`shadow form-check-input mic-switch switch ${
                          mic ? 'status-checked' : 'status-unchecked'
                        }`}
                        type="checkbox"
                        role="switch"
                        id="micPermSwitch"
                        onChange={() => dispatch(setRequestedMediaPerms({ mic: !mic }))}
                        checked={mic}
                      />
                      <div className="d-block ms-2">
                        Use your microphone so I can hear you.
                      </div>
                    </label>
                  </div>
                </div>
              </div>
              <div className="row" style={{ marginBottom: '52px' }}>
                <div>
                  <div className="form-check form-switch">
                    <label
                      className="form-check-label d-flex align-items-center"
                      htmlFor="cameraPermSwitch"
                    >
                      <input
                        className={`shadow form-check-input video-switch switch ${
                          camera ? 'status-checked' : 'status-unchecked'
                        }`}
                        type="checkbox"
                        role="switch"
                        id="micPermSwitch"
                        onChange={() => dispatch(setRequestedMediaPerms({ camera: !camera }))}
                        checked={camera}
                      />
                      <div className="d-block ms-2">
                        Use your camera so we can chat face-to-face.
                      </div>
                    </label>
                  </div>
                </div>
              </div>
              <div className="row" style={{ marginBottom: '60px' }}>
                <div>
                  <Link
                    to="/loading"
                    className="shadow btn primary-accent fs-3"
                    type="button"
                  >
                    Chat with Digital Person A
                  </Link>
                </div>
              </div>
              <div className="row">
                <div>
                  Link terms & conditions or company website here:
                  {' '}
                  <a href="https://example.com">www.example.com</a>
                  .
                </div>
              </div>
              <div className="col" />
            </div>
          </div>
          <div
            className="d-none d-lg-block"
            style={{
              width: '250px',
              position: 'fixed',
              right: '20px',
            }}
          >
            <div className="d-flex align-items-center justify-content-center fs-4">
              <CameraVideoFill size={26} />
              <span className="ps-1">+</span>
              <MicFill size={26} />
            </div>
            <div className="text-center fst-italic">
              Enable your camera and mic to optimize our interaction.
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

Landing.propTypes = {
  className: PropTypes.string.isRequired,
};

export default styled(Landing)`
  .landing-wrapper {
    min-height: 100vh;

    background: ${landingBackgroundImage ? `url(${landingBackgroundImage})` : ''} ${landingBackgroundColor ? `${landingBackgroundColor};` : ''};
    background-size: auto 60%;
    background-repeat: no-repeat;
    background-position: bottom center;

    @media (min-width: ${breakpoints.lg}px) {
      background-size: 60% auto;
      background-position: right bottom;
    }
  }
  .landing-container {
    padding-top: 1rem;
    display: flex;

    &>div {
      background-color: ${Color(landingBackgroundColor).alpha(0.5)};
      backdrop-filter: blur(10px);
      border: 1px solid rgba(0,0,0,0.1);
      padding: 1rem;
      border-radius: 5px;

      @media (min-width: ${breakpoints.lg}px) {
        border: none;
      }
    }
  }
  .form-switch .form-check-input {
    min-width: 7rem;
    height: 3rem;
    display: flex;
    align-items: center;
    justify-content: space-between;


    &.mic-switch::before, &.mic-switch.status-checked::after {
        background-image: url(${micFill});
    }
    &.video-switch::before, &.video-switch.status-checked::after {
        background-image: url(${videoFill});
    }
    &.mic-switch.status-checked::before, &.video-switch.status-checked::before {
      background-image: none;
    }

    &.status-unchecked {
      &::after {
        content: 'OFF';
        color: #000;
        margin-right: 18%;
      }
      &::before {
        background-size: 60%;
        background-repeat: no-repeat;
        background-color: rgb(220, 220, 220);
        background-position: 45% center;
        content: '';
        display: block;

        border-radius: 50%;

        height: 80%;
        margin-left: 5%;
        aspect-ratio: 1;
        float: right;
      }
    }

    &.status-checked {
      &::before {
        content: 'ON';
        color: #FFF;
        margin-left: 22%;
      }

      &::after {
        background-size: 60%;
        background-repeat: no-repeat;
        background-color: #FFF;
        background-position: 55% center;
        content: '';
        display: block;

        border-radius: 50%;

        height: 80%;
        margin-right: 5%;
        aspect-ratio: 1;
        float: right;
      }
    }
  }

`;
