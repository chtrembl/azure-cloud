import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function ButtonWithImage({ data, className }) {
  const {
    title, imageUrl, description, imageAltText, buttonText, productId,
  } = data;

  const handleButtonClick = (e) => {
    e.preventDefault();
    console.log(window.location + productId);
  };

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
            <button class="btn btn-outline-primary" type="button" onClick={handleButtonClick}>
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
    productId: PropTypes.string,
  }).isRequired,
  className: PropTypes.string.isRequired,
};

export default styled(ButtonWithImage)`
  border-radius: 20px;
  border: 1px solid #4581ba;
  overflow: hidden;

  background: none;
  color: #6c757d;
`;

// {
//   "type": "buttonWithImage",
//   "id": "buttonWithImage",
//   "data": {
//     "title": "Soul Machines",
//     "imageUrl": "https://www.soulmachines.com/wp-content/uploads/cropped-sm-favicon-180x180.png",
//     "description":"Soul Machines is the leader in astonishing AGI",
//     "imageAltText": "some text",
//     "buttonText": "push me",
//     "productId": 12345
//   }
// }
