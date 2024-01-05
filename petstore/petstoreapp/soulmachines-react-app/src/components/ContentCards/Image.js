import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function Image({ data, className, triggerScrollIntoView }) {
  const { url, alt } = data;
  return (
    <div className={className}>
      <div style={{ 'text-align': 'center' }}>
        <img
          src={url}
          alt={alt}
          style={{ width: '30%', height: 'auto' }}
          onLoad={triggerScrollIntoView}
        />
        <div style={{ 'margin-left': '50px;', 'margin-right': '50px;' }} className="text-center p-2">{alt}</div>
      </div>
    </div>
  );
}

Image.propTypes = {
  data: PropTypes.shape({
    url: PropTypes.string,
    alt: PropTypes.string,
    caption: PropTypes.string,
  }).isRequired,
  className: PropTypes.string.isRequired,
  triggerScrollIntoView: PropTypes.func.isRequired,
};

export default styled(Image)`
  border-radius: 20px;
  border: 0px solid #4581ba;
  overflow: hidden;

  background: none;
  color: #6c757d;
`;
