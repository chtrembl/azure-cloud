import React from 'react';
import ReactMarkdown from 'react-markdown';
import PropTypes from 'prop-types';

function Markdown({ data }) {
  const { text } = data;
  return (
    <div className="card">
      <div className="card-body">
        <ReactMarkdown>{text}</ReactMarkdown>
      </div>
    </div>
  );
}

Markdown.propTypes = {
  data: PropTypes.shape({
    text: PropTypes.string.isRequired,
  }).isRequired,
};

export default Markdown;
