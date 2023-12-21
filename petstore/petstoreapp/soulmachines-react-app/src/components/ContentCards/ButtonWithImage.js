import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function ButtonWithImage({ data, className }) {
  const {
    title, imageUrl, description, imageAltText, buttonText,
  } = data;
  return (
    <div className={className}>
      <div className="card">
        <div className="d-flex justify-content-center">
          <img src={imageUrl} alt={imageAltText || description} />
        </div>
        <div className="card-body">
          <h5>{title}</h5>
          <p>{description}</p>
          <div className="d-flex justify-content-center">
            {/* open ButtonWithImage in new tab */}
            <button type="button">
              {buttonText}
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}

ButtonWithImage.propTypes = {
  data: PropTypes.objectOf({
    title: PropTypes.string.isRequired,
    imageUrl: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    imageAltText: PropTypes.string,
    buttonText: PropTypes.string,
  }).isRequired,
  className: PropTypes.string.isRequired,
};

export default styled(ButtonWithImage)`
  width: 20rem;

  img {
    width: 100%;
    height: auto;
  }
`;

// {
//   "type": "buttonWithImage",
//   "id": "buttonWithImage",
//   "data": {
//     "title": "Soul Machines",
//     "imageUrl": "https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png",
//     "description":"Soul Machines is the leader in astonishing AGI",
//     "imageAltText": "some text",
//     "buttonText": "push me"
//   }
// }
