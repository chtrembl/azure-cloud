import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function ButtonWithImage({ data, className }) {
  const {
    title, imageUrl, description, imageAltText, buttonText, productId,
  } = data;

  const handleButtonClick = (e) => {
    e.preventDefault();
    const url = window.parent.location.toString();
    console.log(`${url} ${productId}`);
    const session = url.split('sid=')[1].split('&')[0];
    const csrf = url.split('csrf=')[1].split('&')[0];
    const arr = url.split('arr=')[1];
    const azureURL = `https://azurepetstore.com/api/updatecart?csrf=${csrf}&productId=${productId}`;
    console.log(azureURL);

    fetch(azureURL, {
      headers: {
        Cookie: `JSESSIONID=${session}; ARRAffinity=${arr};`,
        'Content-Type': 'text/html',
      },
      type: 'GET',
    }).then((response) => { console.log(response); });
  };

  return (
    <div className={className}>
      <div className="card">
        <div className="d-flex justify-content-center">
          <img className="imageReduce" src={imageUrl} alt={imageAltText || description} />
        </div>
        <div className="card-body">
          <h5>{title}</h5>
          <p className="descriptionReduce">{description}</p>
          <div className="d-flex justify-content-center">
            <button className="btn btn-outline-primary" type="button" onClick={handleButtonClick}>
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
