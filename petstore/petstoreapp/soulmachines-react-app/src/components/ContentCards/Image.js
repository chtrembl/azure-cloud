import React from 'react';
import PropTypes from 'prop-types';
import styled from 'styled-components';

function Image({ data, className, triggerScrollIntoView }) {
  const { url, alt, caption } = data;
  return (
    <div className={className}>
      <div>
        <img
          src={url}
          alt={alt}
          style={{ width: '100%', height: 'auto' }}
          onLoad={triggerScrollIntoView}
        />
        {caption ? <div className="text-center p-2">{caption}</div> : null}
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
  border-radius: 10px;
  border: 1px solid rgba(0,0,0,0.2);
  overflow: hidden;

  background: #393939;
  color: #FFF;
`;
