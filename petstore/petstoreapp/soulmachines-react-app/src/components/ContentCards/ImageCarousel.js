import React, { createRef, useEffect, useState } from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { ArrowLeftCircle, ArrowRightCircle } from 'react-bootstrap-icons';
import Image from './Image';

function ImageCarousel({
  data,
  className,
  inTranscript,
  triggerScrollIntoView,
}) {
  const { images } = data;
  const refs = [];
  const [viewIndex, setViewIndex] = useState(0);

  if (images.length <= 0) {
    return (
      <div className="alert alert-danger">
        image carousel payload contains no images!
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

  const carousel = images.map((imData) => {
    const imgRef = createRef();
    refs.push(imgRef);
    return (
      <div className="image-carousel-item" key={imData.url} ref={imgRef}>
        <Image data={imData} triggerScrollIntoView={triggerScrollIntoView} />
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
              style={{ width: `${((viewIndex + 1) / images.length) * 100}%` }}
              className="progress-bar progress-bar-dark"
            />
            <div
              style={{
                width: `${
                  ((images.length - (viewIndex + 1)) / images.length) * 100
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
              {` of ${images.length} images`}
            </div>
          )}
        </div>
        <button
          type="button"
          className="btn-unstyled"
          onClick={() => setViewIndex(viewIndex + 1)}
          disabled={images.length === viewIndex + 1}
        >
          <ArrowRightCircle size={28} />
        </button>
      </div>
    </div>
  );
}

ImageCarousel.propTypes = {
  data: PropTypes.shape({
    images: PropTypes.arrayOf(
      PropTypes.shape({
        data: {
          url: PropTypes.string,
          alt: PropTypes.string,
          caption: PropTypes.string,
        },
      }),
    ),
  }).isRequired,
  className: PropTypes.string.isRequired,
  inTranscript: PropTypes.bool.isRequired,
  triggerScrollIntoView: PropTypes.func.isRequired,
};

export default styled(ImageCarousel)`
  .image-carousel-wrapper {
    display: flex;
    min-height: 100%;
    overflow-x: hidden;
  }

  .image-carousel-item {
    min-width: 90%;
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
