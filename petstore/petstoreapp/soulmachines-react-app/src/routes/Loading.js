import React, { useEffect, useState } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import PropTypes from 'prop-types';
import { useHistory } from 'react-router-dom';
import styled from 'styled-components';
import { ArrowLeftCircleFill, ArrowRightCircleFill, MicFill } from 'react-bootstrap-icons';
import { createScene } from '../store/sm';
import Header from '../components/Header';
import { headerHeight, landingBackgroundColor, landingBackgroundImage } from '../config';

function Loading({
  className,
}) {
  const {
    connected,
    loading,
    error,
    requestedMediaPerms,
    connectionState,
  } = useSelector(({ sm }) => (sm));
  const dispatch = useDispatch();

  const {
    percentageLoaded, name, currentStep, totalSteps,
  } = connectionState;

  const stateNameMap = {
    SearchingForDigitalPerson: 'Searching For Digital Person',
    DownloadingAssets: 'Downloading Assets',
    ConnectingToDigitalPerson: 'Connecting To Digital Person',
  };
  // map name vals to plain english if we know the state name, otherwise just display the name as is
  const stateName = (name in stateNameMap) ? stateNameMap[name] : name;

  // // pull querystring to see if we are displaying an error
  // // (app can redirect to /loading on fatal err)
  // const useQuery = () => new URLSearchParams(useLocation().search);
  // const query = useQuery();

  // create persona scene on button press on on mount, depending on device size
  const createSceneIfNotStarted = () => {
    if (loading === false && connected === false && error === null) {
      dispatch(createScene());
    }
  };

  useEffect(() => {
    createSceneIfNotStarted();
  }, []);

  const iconSize = 66;
  const [page, setPage] = useState(0);
  const pages = [
    <div>
      <div className="row justify-content-center">
        <div className="tutorial-icon mb-2">
          <MicFill size={iconSize} />
        </div>
      </div>
      <div className="row">
        <div className="d-flex align-items-center justify-content-between">
          <button className="btn-unstyled" type="button" style={{ opacity: 0, width: '44px' }}>
            {' '}
          </button>
          <h4>
            Before you begin.
          </h4>
          <button className="btn-unstyled" type="button" onClick={() => setPage(page + 1)}>
            <ArrowRightCircleFill size={32} />
          </button>
        </div>
        <div className="mt-0 mb-2">
          {
            // show different modal if mic is off or if mic perms are denied
            requestedMediaPerms.mic === true && requestedMediaPerms.micDenied === false
              ? (
                <div>
                  <p>
                    The Digital Person works best in a quiet environment, when you&apos;re close to
                    your microphone, and your camera is on.
                  </p>
                  <p>
                    Speak clearly, and in short responses.
                  </p>
                </div>
              )
              : (
                <div>
                  <p>
                    The Digital Person works best with your microphone on. Enable your microphone
                    at any point during the experience by clicking the microphone icon & allowing
                    the permissions.
                  </p>
                  <p>
                    Remember to speak clearly, and in short responses.
                  </p>
                </div>
              )
          }
        </div>
      </div>
    </div>,
    <div>
      <div className="row justify-content-center">
        <div className="tutorial-icon mb-2">
          <div className="fs-4 fw-bold mt-2">
            &ldquo;hi, how are you?&rdquo;
          </div>
        </div>
      </div>
      <div className="row">
        <div className="d-flex align-items-center justify-content-between">
          <button className="btn-unstyled" type="button" onClick={() => setPage(page - 1)}>
            <ArrowLeftCircleFill size={32} />
          </button>
          <h4>
            What you do.
          </h4>
          <button className="btn-unstyled" type="button" onClick={() => setPage(page + 1)}>
            <ArrowRightCircleFill size={32} />
          </button>
        </div>
        <div className="mt-0 mb-2">
          Cory will listen to whatever you say.
          Other options, like typing or choosing your responses, are also available.
        </div>
      </div>
    </div>,
    <div>
      <div className="row justify-content-center">
        <div className="tutorial-icon tutorial-icon-dp mb-2" />
      </div>
      <div className="row">
        <div className="d-flex align-items-center justify-content-between">
          <button className="btn-unstyled" type="button" onClick={() => setPage(page - 1)}>
            <ArrowLeftCircleFill size={32} />
          </button>
          <h4>
            What you can talk about.
          </h4>
          <button className="btn-unstyled" type="button" style={{ opacity: 0, width: '44px' }}>
            {' '}
          </button>
        </div>
        <div className="mt-0 mb-2">
          You can explore the features of my UI, see examples of the different
          supporting content I can show, or hear about the latest updates to
          the UI template that I&apos;m using.
        </div>
      </div>
    </div>,
  ];

  const [skip, setSkip] = useState(false);
  const redirectToVideoOnConnect = () => {
    setSkip(true);
  };
  const history = useHistory();
  useEffect(() => {
    if (skip === true && connected === true) history.push('/video');
  }, [connected, skip]);

  return (
    <div className={className}>
      <Header />
      <div className="container">
        <div className="row justify-content-center align-items-center">
          <div className="col-11 col-md-6 text-center mobile">
            <div className="row">
              {pages[page]}
            </div>
            <div className="row justify-content-center">
              <div>
                {
                  page < pages.length - 1
                    ? (
                      <button
                        className="btn primary-accent m-2"
                        type="button"
                        onClick={() => setPage(page + 1)}
                        style={{ backgroundColor: '#3C3C3C', border: '2px solid #3C3C3C' }}
                      >
                        Next
                      </button>
                    )
                    : null
                }
              </div>
            </div>
            <div className="row">
              <div>
                <button
                  className={`${connected || page >= pages.length - 1 ? 'btn btn-dark connected-button' : 'btn-unstyled unconnected-button'} m-2`}
                  type="button"
                  disabled={skip}
                  onClick={redirectToVideoOnConnect}
                >
                  { connected || page >= pages.length - 1 ? 'Chat Now' : 'S K I P' }
                  { !connected && skip
                    ? (
                      <div className="ms-1 spinner-border spinner-border-sm text-secondary" role="status">
                        <span className="visually-hidden">Loading...</span>
                      </div>
                    )
                    : null }
                </button>
              </div>
            </div>
            <div className="row justify-content-center">
              <div>
                {/* eslint-disable-next-line react/no-array-index-key */}
                {pages.map((_, i) => (<div key={`${i}-${i === page}`} className="d-inline-block p-1">{i === page ? <div className="closed-dot" /> : <div className="open-dot" />}</div>))}
              </div>
            </div>
            {
              percentageLoaded < 100
                ? (
                  <div>
                    <div className="progress mt-1">
                      <div
                        className="progress-bar"
                        role="progressbar"
                        style={{ width: `${percentageLoaded}%` }}
                        aria-label={`${stateName} (${currentStep} out of ${totalSteps - 1})`}
                        aria-valuenow={percentageLoaded}
                        aria-valuemin="0"
                        aria-valuemax="100"
                      />
                    </div>
                    { stateName !== ''
                      ? (
                        <pre>
                          {`${stateName} (${currentStep} out of ${totalSteps - 1} steps)`}
                        </pre>
                      )
                      : null}
                  </div>
                )
                : null
            }
          </div>
        </div>
      </div>
    </div>
  );
}

Loading.propTypes = {
  className: PropTypes.string.isRequired,
};

export default styled(Loading)`
  background: ${landingBackgroundColor};
  background-size: contain;
  background-repeat: no-repeat;
  background-position: center bottom;

  width: 100vw;
  height: 100vh;
  color: #3C3C3C;

  &>.container>.row {
    height: calc(100vh - ${headerHeight});
  }
  .mobile {
    @media (max-width: 400px) {
      width: 300px;
    }
  .connected-button {
    background-color: #3C3C3C;
    border: 2px solid #3C3C3C;
  }

  .unconnected-button {
    font-size: 14px;
    font-family: "Helvetica Neue";
  }

  .tutorial-icon {
    width: 180px;
    height: 180px;
    aspect-ratio: 1;
    border-radius: 50%;

    display: flex;
    align-items: center;
    justify-content: center;

    background-color: #EAEAEA;
  }
  .tutorial-icon-dp {
    background-image: url(${landingBackgroundImage});
    background-size: cover;
    background-position: bottom center;
  }
  .open-dot {
    border: 2px solid #3C3C3C;
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }
  .closed-dot {
    border: 2px solid #3C3C3C;
    background: #3C3C3C;
    width: 8px;
    height: 8px;
    border-radius: 50%;
  }
`;
