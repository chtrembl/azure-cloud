/* eslint-disable react/prop-types */
import React, { createRef, useEffect, useState } from 'react';
// import PropTypes from 'prop-types';
import styled from 'styled-components';
import { ArrowLeftCircle, ArrowRightCircle } from 'react-bootstrap-icons';
import ButtonWithImage from './ButtonWithImage';

function ButtonCarousel({
  data,
  className,
  inTranscript,
  triggerScrollIntoView,
}) {
  const { buttonCards } = data;
  const refs = [];
  const [viewIndex, setViewIndex] = useState(0);

  if (buttonCards.length <= 0) {
    return (
      <div className="alert alert-danger">
        Button carousel payload contains no buttonCards!
        <pre>{JSON.stringify(data, null, 2)}</pre>
      </div>
    );
  }

  useEffect(() => {
    if (refs[viewIndex]?.current) {
      refs[viewIndex].current.scrollIntoView({ behavior: 'smooth' });
    } else {
      console.error("can't find ref! check your payload.");
    }
  }, [viewIndex]);

  const carousel = buttonCards.map((imData) => {
    if (!imData) return null;
    const imgRef = createRef();
    refs.push(imgRef);
    return (
      <div className="image-carousel-item" key={imData.url || 'key'} ref={imgRef}>
        <ButtonWithImage data={imData} triggerScrollIntoView={triggerScrollIntoView} />
      </div>
    );
  });

  return (
    <div className={className}>
      <div className="image-carousel-wrapper">{carousel}</div>
      <div className="d-flex justify-content-between m-1">
        <button
          type="button"
          className="btn-unstyled"
          onClick={() => setViewIndex(viewIndex - 1)}
          disabled={viewIndex <= 0}
        >
          <ArrowLeftCircle size={28} />
        </button>
        <div className="flex-grow-1 ps-1 pe-1 text-center d-flex justify-content-start align-items-center">
          <div className="d-flex flex-grow-1 mb-1">
            <div
              style={{ width: `${((viewIndex + 1) / buttonCards.length) * 100}%` }}
              className="progress-bar progress-bar-dark"
            />
            <div
              style={{
                width: `${
                  ((buttonCards.length - (viewIndex + 1)) / buttonCards.length) * 100
                }%`,
              }}
              className="progress-bar progress-bar-light"
            />
          </div>
          {/* make position absolute so it takes up zero height,
          makes vertical centering progress bar easier */}
          {inTranscript ? null : (
            <div style={{ position: 'absolute', marginTop: '1.4rem' }}>
              <b>{`${viewIndex + 1}`}</b>
              {` of ${buttonCards.length}`}
            </div>
          )}
        </div>
        <button
          type="button"
          className="btn-unstyled"
          onClick={() => setViewIndex(viewIndex + 1)}
          disabled={buttonCards.length === viewIndex + 1}
        >
          <ArrowRightCircle size={28} />
        </button>
      </div>
    </div>
  );
}

// ButtonCarousel.propTypes = {
//   data: PropTypes.shape({
//     buttonCards: PropTypes.arrayOf(
//       PropTypes.shape({
//         data: {
//           title: PropTypes.string.isRequired,
//           imageUrl: PropTypes.string.isRequired,
//           description: PropTypes.string.isRequired,
//           imageAltText: PropTypes.string,
//           buttonText: PropTypes.string,
//           productId: PropTypes.string,
//         },
//       }),
//     ),
//   }).isRequired,
//   className: PropTypes.string.isRequired,
//   inTranscript: PropTypes.bool.isRequired,
//   triggerScrollIntoView: PropTypes.func.isRequired,
// };

export default styled(ButtonCarousel)`

  div[data-sm-content="buttonCarousel"] {
    width: 80%;
  }

  .imageReduce {
    width:30%;
    margin-top:10px;
  }

  .descriptionReduce {
      width:100%;
      overflow: hidden;
      display: -webkit-box;
      -webkit-line-clamp: 2;
      -webkit-box-orient: vertical;
      background: transparent;
  }

  .card {
    border:2px solid rgb(69, 129, 186) !important;
    border-radius:20px !important;
    --bs-card-bg: transparent !important;
  }

  .image-carousel-wrapper {
    display: flex;
    min-height: 100%;
    overflow-x: hidden;
  }

  .image-carousel-item {
    min-width: 50%;
    position: relative;
    &>div {
      margin-right: 1rem;
      height: 100%;
      width: auto;
    }
  }

  .progress-bar {
    border-top: 2px solid;
    border-radius: 1px;
  }
  .progress-bar-dark {
    border-color: rgba(0,0,0,0.9);
  }
  .progress-bar-light {
    border-color: rgba(0,0,0,0.18);
  }
`;

// {
//   "type": "buttonCarousel",
//   "id": "buttonCarousel",
//   "data": {
//     "buttonCards": [
//       {
//         "title": "Soul Machines",
//         "imageUrl": "https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png",
//         "description":"1 Soul Machines is the leader in astonishing AGI",
//         "imageAltText": "some text",
//         "buttonText": "push me"
//       },
//       {
//         "title": "Soul Machines",
//         "imageUrl": "https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png",
//         "description":"2 Soul Machines is the leader in astonishing AGI",
//         "imageAltText": "some text",
//         "buttonText": "push me"
//       },
//       {
//         "title": "Soul Machines",
//         "imageUrl": "https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png",
//         "description":"3 Soul Machines is the leader in astonishing AGI",
//         "imageAltText": "some text",
//         "buttonText": "push me"
//       },
//     ]
//   }
// }
