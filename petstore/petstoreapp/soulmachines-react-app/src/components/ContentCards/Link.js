import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';
import { ArrowUpRightSquare } from 'react-bootstrap-icons';

function Link({ data, className }) {
  const {
    title, url, imageUrl, description, imageAltText,
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
            {/* open link in new tab */}
            <a href={url} className="btn btn-primary" target="_blank" rel="noreferrer">
              Visit Link
              <ArrowUpRightSquare className="ms-1" />
            </a>
          </div>
        </div>
      </div>
    </div>
  );
}

Link.propTypes = {
  data: PropTypes.objectOf({
    url: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    imageUrl: PropTypes.string.isRequired,
    description: PropTypes.string.isRequired,
    imageAltText: PropTypes.string,
  }).isRequired,
  className: PropTypes.string.isRequired,
};

export default styled(Link)`
  width: 20rem;

  img {
    width: 100%;
    height: auto;
  }
`;
